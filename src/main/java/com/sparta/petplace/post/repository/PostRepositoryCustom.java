package com.sparta.petplace.post.repository;

import com.sparta.petplace.post.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> search(String category, String keyword);
}
