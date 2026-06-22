package com.pulsedesk.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String category;

    private String priority;

    @Column(length = 1000)
    private String summary;

    private LocalDateTime createdAt;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }

    public String getSummary() {
        return summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Comment getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}