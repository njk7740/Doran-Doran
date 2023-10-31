package com.voda.blog.user;

import com.voda.blog.comment.Comment;
import com.voda.blog.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, unique = true)
    private String username;
    private String password;
    @Column(length = 10, unique = true)
    private String nickname;
    private String email;
    @Column(length = 5)
    private String rName;
    private Integer age;
    private String pNum;
    @Column(columnDefinition = "TEXT")
    private String intro;
    private String address;
    private String job;
    private LocalDateTime createDate;
    @OneToMany(mappedBy = "author")
    private List<Post> postList;
    @ManyToMany
    private Set<Post> likePost;
    @OneToMany(mappedBy = "author")
    private List<Comment> commentList;
}
