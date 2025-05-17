package com.virtue.app.repository;

import com.virtue.app.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findPageBy(Pageable page);
    Slice<Item> findSliceBy(Pageable page);

    List<Item> findAllByTitleContains(String title);

    @Query(value = "SELECT * FROM item WHERE match(title) against('?1')", nativeQuery = true)
    Item rawQuery(String text);
}
