package com.sparta.petplace.post.repository;

import com.sparta.petplace.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom , PagingAndSortingRepository<Post, Long> {

    List<Post> findByCategory(String category);
    List<Post> findAllByEmail(String email);
    Optional<Post> findByTitle(String title);


}
