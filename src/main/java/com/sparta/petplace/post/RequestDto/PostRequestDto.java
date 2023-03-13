package com.sparta.petplace.post.RequestDto;

import com.sparta.petplace.post.entity.Posts;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostRequestDto {
    private Long id;
    private String email;
    private String title;
    private Posts category;
    private String ceo;
    private String contents;
    private List<MultipartFile> images = new ArrayList<>();
    private String mapdate;
    private String address;
    private Integer telNum;
    private Integer startTime;
    private Integer endTime;
    private Integer closedDay;
}
