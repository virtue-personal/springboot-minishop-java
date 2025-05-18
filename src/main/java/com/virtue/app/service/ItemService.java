package com.virtue.app.service;

import com.virtue.app.domain.Item;
import com.virtue.app.repository.CommentRepository;
import com.virtue.app.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    /**
     * 아이템 저장
     */
    @Transactional
    public void saveItem(String title, Integer price, String imgUrl) {
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setImgUrl(imgUrl);
        itemRepository.save(item);
    }

    /**
     * 아이템 수정
     */
    @Transactional
    public void editItem(Long id, String title, Integer price, String imgUrl) {
        itemRepository.findById(id).ifPresent(item -> {
            item.setTitle(title);
            item.setPrice(price);
            if (imgUrl != null && !imgUrl.isBlank()) {
                item.setImgUrl(imgUrl);
            }
            itemRepository.save(item);
        });
    }

    /**
     * 아이템 삭제
     */
    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    /**
     * 페이지네이션된 아이템 리스트 조회
     */
    @Transactional(readOnly = true)
    public Page<Item> getItemPage(int pageNum, String sort, String searchText) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(pageNum - 1, 6, Sort.by(direction, "id"));

        if (searchText != null && !searchText.isBlank()) {
            return itemRepository.searchByTitleWithPaging(searchText.trim(), pageRequest);
        } else {
            return itemRepository.findPageBy(pageRequest);
        }
    }

    /**
     * 아이템 상세 정보 + 댓글 조회
     */
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getItemDetail(Long id) {
        return itemRepository.findById(id).map(item -> {
            Map<String, Object> result = new HashMap<>();
            result.put("item", item);
            result.put("comments", commentRepository.findByItemIdOrderByCreatedAtDesc(id));
            return result;
        });
    }

    /**
     * 수정 페이지용 아이템 조회
     */
    @Transactional(readOnly = true)
    public Optional<Item> getEditPageData(Long id) {
        return itemRepository.findById(id);
    }

    /**
     * 제목 기반 아이템 검색
     */
    @Transactional(readOnly = true)
    public Page<Item> searchItems(String searchText, int page, String sort) {
        Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page - 1, 6, Sort.by(direction, "id"));
        return itemRepository.searchByTitleWithPaging(searchText.trim(), pageRequest);
    }
}