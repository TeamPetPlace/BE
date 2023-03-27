package com.sparta.petplace.common.sse.entity;

import com.sparta.petplace.common.Timestamped;
import com.sparta.petplace.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;


    @Embedded
    private RelatedURL url;

    @Column
    private Boolean isRead;

    // 알림내용 - 100자 이내
    @Embedded
    private NotificationContent content;


    // 회원정보
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "MEMBER_ID")
    private Member receiver;

    @Builder
    public Notification(Member receiver, String content, String url, Boolean isRead) {
        this.receiver = receiver;
        this.content = new NotificationContent(content);
        this.url = new RelatedURL(url);
        this.isRead = isRead;
    }

    public void read() {
        isRead = true;
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }


}
