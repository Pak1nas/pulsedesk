package com.pulsedesk.dto;

public class AiTicketResult {

    private boolean shouldCreateTicket;
    private String title;
    private String category;
    private String priority;
    private String summary;

    public boolean isShouldCreateTicket() {
        return shouldCreateTicket;
    }

    public void setShouldCreateTicket(boolean shouldCreateTicket) {
        this.shouldCreateTicket = shouldCreateTicket;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}