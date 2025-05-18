package com.virtue.app.dto;

import com.virtue.app.domain.Sales;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String title;
    private Integer price;
    private Integer count;
    private String imgUrl;
    private Sales.OrderStatus status;
    private LocalDateTime createdAt;
}
