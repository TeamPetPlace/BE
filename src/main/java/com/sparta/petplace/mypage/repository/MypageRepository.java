package com.sparta.petplace.mypage.repository;

import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.mypage.entity.Mypage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MypageRepository extends JpaRepository<Mypage,Long> {
    List<Mypage> findByMemberId(Long id);

    void deleteByPostId(Long postId);

}
