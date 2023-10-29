package com.voda.blog.comment;

import com.voda.blog.post.Post;
import com.voda.blog.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String content;
    @ManyToOne
    private Post post;
    @ManyToOne
    private SiteUser author;
    private LocalDateTime createDate;
}
