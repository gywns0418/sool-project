package com.example.sool.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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

    //댓글 정보
    @GetMapping("/notes/{noteId}/comments")
    public List<CommentDto> getCommentList(@PathVariable int noteId){
        return commentService.findByNoteId(noteId);
    }

    //댓글 작성
    @PostMapping("/notes/{noteId}/comments")
    public ResponseEntity<?> createComment(@PathVariable int noteId,@RequestBody CommentDto dto,
            Authentication authentication){
        //로그인 확인
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(user.getUserId());

        dto.setNoteId(noteId);

        commentService.createComment(dto);

        return ResponseEntity.ok().build();
    }

    //대댓글 작성
    @PostMapping("/notes/{noteId}/comments/reply")
    public ResponseEntity<?> createReply(@PathVariable int noteId, @RequestBody CommentDto dto,
            Authentication authentication){
        //로그인 확인
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(user.getUserId());
        
        dto.setNoteId(noteId);

        commentService.createReply(dto);

        return ResponseEntity.ok().build();
    }

    // 댓글 수정
    @PostMapping("/comments/{commentId}/update")
    public ResponseEntity<?> updateComment(@PathVariable Integer commentId, @RequestBody CommentDto dto,
                                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(userDetails.getUserId());

        dto.setCommentId(commentId);

        commentService.updateComment(dto);

        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @PostMapping("/comments/{commentId}/delete")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {

        commentService.deleteComment(commentId);

        return ResponseEntity.ok().build();
    }
}
