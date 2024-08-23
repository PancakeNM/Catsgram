package ru.yandex.practicum.catsgram.model;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
public class Post {
    Long id;
    long authorId;
    String description;
    Instant postDate;
}
