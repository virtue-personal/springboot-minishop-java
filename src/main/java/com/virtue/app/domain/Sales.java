package com.virtue.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Sales {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer price;

    private Integer count;

    private String imgUrl;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Member member;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING,
        COMPLETED
    }
}
