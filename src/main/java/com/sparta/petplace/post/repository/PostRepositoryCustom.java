package com.sparta.petplace.post.repository;

import com.sparta.petplace.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.sparta.petplace.post.entity.Sort;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface PostRepositoryCustom {
    List<Post> search(String category, String keyword, Pageable pageable);
    List<Post> find(String category, Pageable pageable, Double lat, Double lng,  Sort sort);
    long countByCategory(String category);
    long countByCategoryAndKeyword(String category, String keyword);
}
