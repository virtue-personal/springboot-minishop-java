package com.virtue.springbootweb;

import jakarta.persistence.*;
import lombok.ToString;

@ToString
@Entity
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column
    public String title;

    @Column
    public Integer price;

}
