package com.virtue.springbootweb;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String title;

    private String date;
}
