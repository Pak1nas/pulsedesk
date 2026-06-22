package com.pulsedesk.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3000, nullable = false)
    private String text;

    private LocalDateTime createdAt;

    private boolean ticketCreated;

    @JsonManagedReference
    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL)
    private Ticket ticket;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isTicketCreated() {
        return ticketCreated;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setTicketCreated(boolean ticketCreated) {
        this.ticketCreated = ticketCreated;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}