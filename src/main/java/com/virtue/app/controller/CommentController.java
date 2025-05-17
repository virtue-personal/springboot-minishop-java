package com.virtue.app.controller;

import com.virtue.app.domain.Comment;
import com.virtue.app.domain.Item;
import com.virtue.app.repository.CommentRepository;
import com.virtue.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createComment(@RequestBody Map<String, String> request, Authentication auth) {
        Long itemId = Long.parseLong(request.get("itemId"));
        String content = request.get("content");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setItem(item);
        comment.setAuthor(auth.getName());

        commentRepository.save(comment);

        Map<String, Object> response = new HashMap<>();
        response.put("id", comment.getId());
        response.put("content", comment.getContent());
        response.put("author", comment.getAuthor());
        response.put("createdAt", comment.getCreatedAt());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication auth) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().equals(auth.getName())) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        commentRepository.delete(comment);
        return ResponseEntity.ok().build();
    }
}
