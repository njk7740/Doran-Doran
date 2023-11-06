package com.voda.blog.user;

import com.voda.blog.comment.Comment;
import com.voda.blog.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(length = 10)
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
    private String picture;
    private Set<String> favorite;

    public SiteUser(String key, String name, String email, String picture, LocalDateTime time) {
        this.nickname = name;
        this.username = key;
        this.email = email;
        this.picture = (picture == null) ? "/default-profile.png" : picture;
        this.createDate = time;
        favorite = new HashSet<>();
    }

    public SiteUser() {
        favorite = new HashSet<>();
    }
    public SiteUser update(String name, String picture) {
        this.nickname = name;
        this.picture = picture;

        return this;
    }
}
