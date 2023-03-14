package com.sparta.petplace.post.entity;

import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String contents;
    @OneToMany(mappedBy = "post")
    private List<PostImage> image = new ArrayList<>();
    @Column(nullable = false)
    private String mapdata;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String telNum;
    @Column(nullable = false)
    private String ceo;
    @Column(nullable = false)
    private String startTime;
    @Column(nullable = false)
    private String endTime;
    @Column(nullable = false)
    private String closedDay;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID",nullable = false)
    private Member member;

    @Builder
    public Post(PostRequestDto requestDto, Member member) {
        this.email = member.getEmail();
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.contents = requestDto.getContents();
        this.mapdata = requestDto.getMapdata();
        this.address = requestDto.getAddress();
        this.telNum = requestDto.getTelNum();
        this.ceo = requestDto.getCeo();
        this.startTime = requestDto.getStartTime();
        this.endTime = requestDto.getEndTime();
        this.closedDay = requestDto.getClosedDay();
        this.member = member;
    }

    public static Post of (PostRequestDto requestDto, Member member){
        return builder().
                requestDto(requestDto).
                member(member).
                build();
    }

    public void update(PostRequestDto requestDto , List<PostImage> image) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.contents = requestDto.getContents();
        this.mapdata = requestDto.getMapdata();
        this.address = requestDto.getAddress();
        this.telNum = requestDto.getTelNum();
        this.ceo = requestDto.getCeo();
        this.startTime = requestDto.getStartTime();
        this.endTime = requestDto.getEndTime();
        this.closedDay = requestDto.getClosedDay();
        this.image = image;
    }
}
