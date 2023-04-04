package com.sparta.petplace.member.repository;

import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.member.entity.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
    List<MemberHistory> findTop3ByMemberOrderByCreatedAtDesc(Member member);

    void deleteByPostId(Long postId);

}