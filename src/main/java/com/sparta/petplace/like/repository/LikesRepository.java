package com.sparta.petplace.like.repository;

import com.sparta.petplace.like.entity.Likes;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostAndMember(Post post, Member member);

    Likes findByPostIdAndMemberId(Long post_id , Long id);

    void deleteByPostId(Long postId);

    List<Likes> findByMemberId(Long id);
}

