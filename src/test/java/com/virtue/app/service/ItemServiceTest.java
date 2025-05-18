package com.virtue.app.service;

import com.virtue.app.domain.Item;
import com.virtue.app.repository.CommentRepository;
import com.virtue.app.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item();
        sampleItem.setId(1L);
        sampleItem.setTitle("테스트 상품");
        sampleItem.setPrice(10000);
        sampleItem.setImgUrl("test.jpg");
    }

    @Test
    void 아이템_저장_성공() {
        itemService.saveItem("상품1", 1000, "img1.png");

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(captor.capture());

        Item saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("상품1");
        assertThat(saved.getPrice()).isEqualTo(1000);
        assertThat(saved.getImgUrl()).isEqualTo("img1.png");
    }

    @Test
    void 아이템_수정_성공() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        itemService.editItem(1L, "수정된 상품", 9999, "new.jpg");

        assertThat(sampleItem.getTitle()).isEqualTo("수정된 상품");
        assertThat(sampleItem.getPrice()).isEqualTo(9999);
        assertThat(sampleItem.getImgUrl()).isEqualTo("new.jpg");
        verify(itemRepository).save(sampleItem);
    }

    @Test
    void 아이템_삭제_성공() {
        itemService.deleteItem(1L);
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void 아이템_상세조회_성공() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));
        when(commentRepository.findByItemIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        Optional<Map<String, Object>> result = itemService.getItemDetail(1L);

        assertThat(result).isPresent();
        assertThat(result.get().get("item")).isEqualTo(sampleItem);
        assertThat(result.get().get("comments")).isInstanceOf(List.class);
    }

    @Test
    void 아이템_페이징_조회_성공() {
        Page<Item> mockPage = new PageImpl<>(List.of(sampleItem));
        when(itemRepository.findPageBy(any())).thenReturn(mockPage);

        Page<Item> result = itemService.getItemPage(1, "desc", null);

        assertThat(result.getContent()).hasSize(1);
        verify(itemRepository).findPageBy(any(PageRequest.class));
    }

    @Test
    void 아이템_검색_조회_성공() {
        Page<Item> mockPage = new PageImpl<>(List.of(sampleItem));
        when(itemRepository.searchByTitleWithPaging(eq("테스트"), any())).thenReturn(mockPage);

        Page<Item> result = itemService.searchItems("테스트", 1, "asc");

        assertThat(result.getContent()).hasSize(1);
        verify(itemRepository).searchByTitleWithPaging(eq("테스트"), any(PageRequest.class));
    }

    @Test
    void 아이템_수정페이지_조회() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(sampleItem));

        Optional<Item> result = itemService.getEditPageData(1L);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(sampleItem);
    }
}