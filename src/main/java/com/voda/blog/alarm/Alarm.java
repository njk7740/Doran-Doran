package com.voda.blog.alarm;

import com.voda.blog.post.Post;
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
    @ManyToOne
    private Post post;
    @ManyToOne
    private SiteUser target;

    public Alarm(SiteUser user, String type) {
        this.user = user;
        this.createDate = LocalDateTime.now();
        this.type = type;
    }

    public Alarm() {}
}
