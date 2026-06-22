package com.pulsedesk.service;

import com.pulsedesk.dto.AiTicketResult;
import com.pulsedesk.model.Comment;
import com.pulsedesk.model.Ticket;
import com.pulsedesk.repository.CommentRepository;
import com.pulsedesk.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final TicketAnalysisService ticketAnalysisService;

    public CommentService(
            CommentRepository commentRepository,
            TicketRepository ticketRepository,
            TicketAnalysisService ticketAnalysisService
    ) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.ticketAnalysisService = ticketAnalysisService;
    }

    @Transactional
    public Comment createComment(String text) {
        // 1. Create and save the comment
        Comment comment = new Comment();
        comment.setText(text);
        comment.setTicketCreated(false);

        Comment savedComment = commentRepository.save(comment);

        // 2. Analyze the comment using Hugging Face / fallback logic
        AiTicketResult aiResult = ticketAnalysisService.analyzeComment(text);

        // 3. If AI says this should become a ticket, create ticket
        if (aiResult.isShouldCreateTicket()) {
            Ticket ticket = new Ticket();
            ticket.setTitle(aiResult.getTitle());
            ticket.setCategory(aiResult.getCategory());
            ticket.setPriority(aiResult.getPriority());
            ticket.setSummary(aiResult.getSummary());
            ticket.setComment(savedComment);

            Ticket savedTicket = ticketRepository.save(ticket);

            savedComment.setTicketCreated(true);
            savedComment.setTicket(savedTicket);

            savedComment = commentRepository.save(savedComment);
        }

        // 4. Return saved comment
        return savedComment;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }
}