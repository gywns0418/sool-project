package com.example.sool.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import com.example.sool.dto.CommentDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.CommentService;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService){
        this.commentService = commentService;
    }

    @GetMapping("/notes/{noteId}/comments")
    public List<CommentDto> getCommentList(@PathVariable int noteId){
        return commentService.findByNoteId(noteId);
    }

    @PostMapping("/comments")
    public void insertComment(@RequestBody CommentDto dto, Authentication authentication) {
        
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        dto.setUserId(user.getUserId());

        commentService.insertComment(dto);
    }
}
