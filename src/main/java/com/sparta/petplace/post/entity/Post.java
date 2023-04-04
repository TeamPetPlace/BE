package com.sparta.petplace.post.entity;

import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import com.sparta.petplace.post.RequestDto.PostRequestDto;
import com.sparta.petplace.review.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
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
    @Column
    private String contents;
    @OneToMany(mappedBy = "post")
    private List<PostImage> image = new ArrayList<>();
    @Column
    private String resizeImage;
    @Column(nullable = false)
    private String lat;
    @Column(nullable = false)
    private String lng;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String telNum;
    @Column(nullable = false)
    private String ceo;
    @Column
    private String startTime;
    @Column
    private String endTime;
    @Column(nullable = false)
    private String closedDay;
    // 병원 : 기본 진료비, 미용 : 기본 미용비, 카페 : 입장료
    @Column
    private String cost;
    // 병원 : 야간진료, 미용 : 예약여부
    @Column
    private String aboolean1;
    // 미용 : 주차여부, 카페 : 주차여부
    @Column
    private String aboolean2;
    // 부대시설 ,진료항목
    @Column(length = 10000)
    private String feature1;
    @Column
    private Integer star;
    @OneToMany(mappedBy = "post")
    private List<Review> reviews =new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID",nullable = false)
    private Member member;


    @Builder
    public Post(PostRequestDto requestDto, Member member , Integer star, String resizeImage ) {
        this.email = member.getEmail();
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.contents = requestDto.getContents();
        this.lat = requestDto.getLat();
        this.lng = requestDto.getLng();
        this.address = requestDto.getAddress();
        this.telNum = requestDto.getTelNum();
        this.ceo = requestDto.getCeo();
        this.startTime = requestDto.getStartTime();
        this.endTime = requestDto.getEndTime();
        this.closedDay = requestDto.getClosedDay();
        this.cost = requestDto.getCost();
        this.aboolean1 = requestDto.getAboolean1();
        this.aboolean2 = requestDto.getAboolean2();
        this.feature1 = requestDto.getFeature1();
        this.resizeImage = resizeImage;
        this.star = star;
        this.member = member;
    }




    public void update(PostRequestDto requestDto , List<PostImage> image, Integer star) {
        this.title = requestDto.getTitle();
        this.category = requestDto.getCategory();
        this.contents = requestDto.getContents();
        this.lat = requestDto.getLat();
        this.lng = requestDto.getLng();
        this.address = requestDto.getAddress();
        this.telNum = requestDto.getTelNum();
        this.ceo = requestDto.getCeo();
        this.startTime = requestDto.getStartTime();
        this.endTime = requestDto.getEndTime();
        this.closedDay = requestDto.getClosedDay();
        this.cost = requestDto.getCost();
        this.aboolean1 = requestDto.getAboolean1();
        this.aboolean2 = requestDto.getAboolean2();
        this.feature1 = requestDto.getFeature1();
        this.image = image;
        this.star = star;
    }

    public static Post of (PostRequestDto requestDto, Member member){
        return builder().
                requestDto(requestDto).
                member(member).
                build();
    }

    public static Post of (PostRequestDto requestDto, Member member, Integer star){
        return builder().
                requestDto(requestDto).
                star(star).
                member(member).
                build();
    }
}
