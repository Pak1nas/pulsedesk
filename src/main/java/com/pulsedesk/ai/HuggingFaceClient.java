package com.pulsedesk.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulsedesk.dto.AiTicketResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class HuggingFaceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.model}")
    private String model;

    public HuggingFaceClient() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public AiTicketResult analyzeComment(String commentText) {
        try {
            String prompt = buildPrompt(commentText);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    ),
                    "max_tokens", 300,
                    "temperature", 0.1
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            return parseResponse(response.getBody());

        } catch (RestClientException e) {
            throw new RuntimeException("Hugging Face API request failed:" + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Could not parse Hugging Face response", e);
        }
    }

private String buildPrompt(String commentText) {
    return "Analyze this user comment for a support platform.\n\n"
            + "Decide if it should become a support ticket.\n\n"
            + "Return ONLY valid JSON.\n"
            + "Do not include markdown.\n"
            + "Do not include explanation.\n\n"
            + "JSON format:\n"
            + "{\n"
            + "  \"shouldCreateTicket\": true,\n"
            + "  \"title\": \"short ticket title\",\n"
            + "  \"category\": \"bug\",\n"
            + "  \"priority\": \"medium\",\n"
            + "  \"summary\": \"short summary\"\n"
            + "}\n\n"
            + "Rules:\n"
            + "- shouldCreateTicket must be true only for real issues, bugs, billing problems, account problems, or feature requests.\n"
            + "- shouldCreateTicket must be false for compliments, general praise, or neutral comments.\n"
            + "- category must be one of: bug, feature, billing, account, other.\n"
            + "- priority must be one of: low, medium, high.\n"
            + "- If shouldCreateTicket is false, use null for title, category, priority, and summary.\n\n"
            + "Comment:\n"
            + "\"" + commentText + "\"";
}

    private AiTicketResult parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        String content = root
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

        String jsonOnly = extractJson(content);

        return objectMapper.readValue(jsonOnly, AiTicketResult.class);
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new IllegalArgumentException("No JSON object found in AI response: " + text);
        }

        return text.substring(start, end + 1);
    }
}