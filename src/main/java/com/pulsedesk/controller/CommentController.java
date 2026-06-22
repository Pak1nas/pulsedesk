package com.pulsedesk.controller;

import com.pulsedesk.dto.CreateCommentRequest;
import com.pulsedesk.model.Comment;
import com.pulsedesk.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public Comment createComment(@Valid @RequestBody CreateCommentRequest request) {
        return commentService.createComment(request.getText());
    }

    @GetMapping
    public List<Comment> getComments() {
        return commentService.getAllComments();
    }
}