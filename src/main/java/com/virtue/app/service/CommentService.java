package com.virtue.app.service;

import com.virtue.app.domain.Comment;
import com.virtue.app.domain.Item;
import com.virtue.app.repository.CommentRepository;
import com.virtue.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public Map<String, Object> createComment(String itemIdRaw, String content, String username) {
        Long itemId;
        try {
            itemId = Long.parseLong(itemIdRaw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 itemId입니다.");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setItem(item);
        comment.setAuthor(username);
        commentRepository.save(comment);

        Map<String, Object> response = new HashMap<>();
        response.put("id", comment.getId());
        response.put("content", comment.getContent());
        response.put("author", comment.getAuthor());
        response.put("createdAt", comment.getCreatedAt());

        return response;
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getAuthor().equals(username)) {
            throw new SecurityException("권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}