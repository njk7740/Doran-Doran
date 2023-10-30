package com.voda.blog.comment;

import com.voda.blog.post.Post;
import com.voda.blog.user.SiteUser;
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
public class CommentService {
    private final CommentRepository commentRepository;

    public void create(Post post, SiteUser user, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(user);
        comment.setCreateDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public Page<Comment> getList(Post post, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.asc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findByPost(pageable, post);
    }

    public Comment getById(Integer id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()) return comment.get();
        throw new RuntimeException("해당 댓글을 찾을 수 없습니다");
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        commentRepository.save(comment);
    }
}
