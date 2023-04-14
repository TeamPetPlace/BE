package com.sparta.petplace.post.RequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {
//    private Long id;
//    private String email;
    private String title;
    private String category;
    private String ceo;
    private String contents;
    private List<MultipartFile> image = new ArrayList<>();
    private String lat;
    private String lng;
    private String address;
    private String cost;
    private String telNum;
    private String startTime;
    private String endTime;
    private String closedDay;
    private String feature1;

    private String aboolean1;

    private String aboolean2;

    public PostRequestDto(String title, String category, String ceo, String contents, String lat, String lng, String address, String telNum, String closedDay) {
        this.title = title;
        this.category = category;
        this.ceo = ceo;
        this.contents = contents;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.telNum = telNum;
        this.closedDay = closedDay;
    }
}
