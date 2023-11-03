package com.voda.blog.post;

import com.voda.blog.user.SiteUser;
import com.voda.blog.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public void create(String subject, String content, SiteUser user) {
        Post post = new Post();
        post.setSubject(subject);
        post.setContent(content);
        post.setCreateDate(LocalDateTime.now());
        post.setAuthor(user);
        post.setLikerSize(0);
        postRepository.save(post);
    }

    public List<Post> getList() {
        return postRepository.findAll();
    }

    public Page<Post> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 12, Sort.by(sorts));
        return postRepository.findAll(pageable);
    }

    public Page<Post> getList(int page, String kw, String str) {
        List<Sort.Order> sorts = new ArrayList<>();
        if(str.equals("recent")) sorts.add(Sort.Order.desc("createDate"));
        else sorts.add(Sort.Order.desc("likerSize"));
        Pageable pageable = PageRequest.of(page, 12, Sort.by(sorts));
        return postRepository.findBySubjectLike("%" + kw + "%" , pageable);
    }

    public Page<Post> getByAuthor(int page, SiteUser user) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 5, Sort.by(sorts));
        return postRepository.findByAuthor(pageable, user);
    }

    public Post getById(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent()) return post.get();
        throw new RuntimeException("게시물이 없습니다.");
    }

    public void modify(Post post, String subject, String content) {
        post.setSubject(subject);
        post.setContent(content);
        postRepository.save(post);
    }

    public void delete(Post post) {
        for(SiteUser user : post.getLiker()) userService.unlike(user, post);
        post.getLiker().clear();
        postRepository.delete(post);
    }

    public void like(Post post, SiteUser user) {
        post.getLiker().add(user);
        post.setLikerSize(post.getLiker().size());
        postRepository.save(post);
    }

    public void unlike(Post post, SiteUser user) {
        post.getLiker().remove(user);
        post.setLikerSize(post.getLiker().size());
        postRepository.save(post);
    }
}
