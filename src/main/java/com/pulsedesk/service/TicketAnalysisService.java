package com.pulsedesk.service;

import com.pulsedesk.ai.HuggingFaceClient;
import com.pulsedesk.dto.AiTicketResult;
import org.springframework.stereotype.Service;

@Service
public class TicketAnalysisService {

    private final HuggingFaceClient huggingFaceClient;

    public TicketAnalysisService(HuggingFaceClient huggingFaceClient) {
        this.huggingFaceClient = huggingFaceClient;
    }

    public AiTicketResult analyzeComment(String text) {
        try {
            AiTicketResult aiResult = huggingFaceClient.analyzeComment(text);

            System.out.println("Hugging Face API was used successfully.");

            if (aiResult == null) {
                return fallbackAnalyze(text);
            }

            return cleanResult(aiResult, text);

        } catch (Exception e) {
            System.out.println("Hugging Face failed. Using fallback logic. Error: " + e.getMessage());
            return fallbackAnalyze(text);
        }
    }

    private AiTicketResult cleanResult(AiTicketResult result, String originalText) {
        if (!result.isShouldCreateTicket()) {
            result.setTitle(null);
            result.setCategory(null);
            result.setPriority(null);
            result.setSummary(null);
            return result;
        }

        if (!isValidCategory(result.getCategory())) {
            result.setCategory("other");
        }

        if (!isValidPriority(result.getPriority())) {
            result.setPriority("medium");
        }

        if (result.getTitle() == null || result.getTitle().isBlank()) {
            result.setTitle(generateTitle(originalText));
        }

        if (result.getSummary() == null || result.getSummary().isBlank()) {
            result.setSummary(originalText.length() > 200 ? originalText.substring(0, 200) : originalText);
        }

        return result;
    }

    private boolean isValidCategory(String category) {
        return category != null && (
                category.equals("bug")|| category.equals("feature")|| category.equals("billing")|| category.equals("account") || category.equals("other")
        );
    }

    private boolean isValidPriority(String priority) {
        return priority != null && (
                priority.equals("low")|| priority.equals("medium")|| priority.equals("high")
        );
    }

    private AiTicketResult fallbackAnalyze(String text) {
        AiTicketResult result = new AiTicketResult();

        if (text == null || text.isBlank()) {
            result.setShouldCreateTicket(false);
            return result;
        }

        String lower = text.toLowerCase();

        boolean issue = lower.contains("error")|| lower.contains("bug")|| lower.contains("crash")|| lower.contains("broken")|| lower.contains("refund")|| lower.contains("charged")|| lower.contains("invoice")|| lower.contains("login")|| lower.contains("password")|| lower.contains("can't") || lower.contains("cannot") || lower.contains("not working")|| lower.contains("problem")|| lower.contains("issue");

        result.setShouldCreateTicket(issue);

        if (!issue) {
            result.setTitle(null);
            result.setCategory(null);
            result.setPriority(null);
            result.setSummary(null);
            return result;
        }

        if (lower.contains("refund") || lower.contains("charged") || lower.contains("invoice")) {
            result.setCategory("billing");
        } else if (lower.contains("login") || lower.contains("password") || lower.contains("account")) {
            result.setCategory("account");
        } else if (lower.contains("feature") || lower.contains("add") || lower.contains("request")) {
            result.setCategory("feature");
        } else if (lower.contains("error") || lower.contains("bug") || lower.contains("crash") || lower.contains("broken")) {
            result.setCategory("bug");
        } else {
            result.setCategory("other");
        }

        if (lower.contains("urgent") || lower.contains("can't login") || lower.contains("charged twice")) {
            result.setPriority("high");
        } else if (
                lower.contains("not working")|| lower.contains("problem")|| lower.contains("issue") || lower.contains("crash")|| lower.contains("crashes")|| lower.contains("error")
        ) {
            result.setPriority("medium");
        } else {
            result.setPriority("low");
        }

        result.setTitle(generateTitle(text));
        result.setSummary(text.length() > 200 ? text.substring(0, 200) : text);

        return result;
    }

    private String generateTitle(String text) {
        String clean = text.trim();

        if (clean.length() <= 60) {
            return clean;
        }

        return clean.substring(0, 60) + "...";
    }
}