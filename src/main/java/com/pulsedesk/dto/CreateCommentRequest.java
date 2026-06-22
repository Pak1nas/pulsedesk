package com.pulsedesk.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCommentRequest {

    @NotBlank
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}