package com.virtue.springbootweb.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 새 아이템 저장
     */
    public void saveItem(String title, Integer price, String imgUrl) {
        Item item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setImgUrl(imgUrl);

        itemRepository.save(item);
    }

    /**
     * 기존 아이템 수정
     */
    public void editItem(Long id, String title, Integer price, String imgUrl) {
        Optional<Item> optional = itemRepository.findById(id);
        if (optional.isPresent()) {
            Item item = optional.get();
            item.setTitle(title);
            item.setPrice(price);
            if (imgUrl != null && !imgUrl.isBlank()) {
                item.setImgUrl(imgUrl);
            }
            itemRepository.save(item);
        }
    }
}