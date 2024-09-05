package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.util.SortOrder;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Указываем, что класс PostService - является бином и его
// нужно добавить в контекст приложения
@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public Collection<Post> findAllByFilter(int from, int size, String sort) {
        if (!sort.equals("asc") || !sort.equals("desc")) {
            throw new ParameterNotValidException(sort, "Сортировка должна быть либо asc, либо desc");
        }
        if (size <= 0) {
            throw new ParameterNotValidException(String.valueOf(size), "Некорректный размер выборки. Размер должен быть больше нуля");
        }
        if (from < 0) {
            throw new ParameterNotValidException(String.valueOf(from), "Нужно выбрать неотрицательное значение, с которого производится поиск постов.");
        }
        SortOrder sortOrder = SortOrder.from(sort);
        return posts.values().stream()
                .sorted((post1,post2) -> {
                    int comp = post1.getPostDate().compareTo(post2.getPostDate());
                    if (sortOrder == SortOrder.DESCENDING) {
                        comp = -1 * comp;
                    }
                    return comp;
                })
                .skip(from)
                .limit(size)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        userService.findUserById(post.getAuthorId());

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post findPostById(Long id) {
        if (posts.containsKey(id)) {
            return posts.get(id);
        } else {
            throw new NotFoundException("Пост с ID " + id + " не найден.");
        }
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
