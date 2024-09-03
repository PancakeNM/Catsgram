package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAllByFilter(@RequestParam(defaultValue = "desc", value = "sort", required = false) String sort,
                                            @RequestParam(defaultValue = "10", value = "size", required = false) int size,
                                            @RequestParam(defaultValue = "1", value = "page", required = false) int page) {
        if (page < 1 || size <= 0) {
            throw new IllegalArgumentException();
        }
        if (!(sort.equals("asc") || sort.equals("desc"))){
            throw new IllegalArgumentException();
        }
        return postService.findByFilter(page, size , sort);
    }

    @GetMapping("/all")
    public Collection<Post> findAll() {
        return postService.findAll();
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/{postId}")
    public Post findById(@PathVariable("postId") Long postId) {
        return postService.findPostById(postId);
    }
}
