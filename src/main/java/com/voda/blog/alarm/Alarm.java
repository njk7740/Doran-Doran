package com.voda.blog.alarm;

import com.voda.blog.message.Message;
import com.voda.blog.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String comment;
    private LocalDateTime createDate;
    @ManyToOne
    private SiteUser user;
    private String type;
    private Integer postId;
    @ManyToOne
    private SiteUser target;
    @ManyToOne
    private Message message;
    private boolean confirm;

    public Alarm(SiteUser user, String type, SiteUser target) {
        this.user = user;
        this.target = target;
        this.createDate = LocalDateTime.now();
        this.type = type;
        this.confirm = false;
    }

    public Alarm() {
        this.createDate = LocalDateTime.now();
        this.confirm = false;
    }
}
