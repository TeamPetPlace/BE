package com.sparta.petplace.review.repository;

import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.entity.Post;
import com.sparta.petplace.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>{
    void deleteByPostId(Long postId);

    Optional<Review> findByIdAndMember(Long id, Member member);



    List<Review> findAllByMemberId(Long id);

    Review findTopByMemberAndPostOrderByCreatedAtDesc(Member member, Post post);

}
