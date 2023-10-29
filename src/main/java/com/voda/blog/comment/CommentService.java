package com.voda.blog.comment;

import com.voda.blog.post.Post;
import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
