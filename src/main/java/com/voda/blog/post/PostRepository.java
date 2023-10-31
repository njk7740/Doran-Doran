package com.voda.blog.post;

import com.voda.blog.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAll(Pageable pageable);
    Page<Post> findBySubjectLike(String kw, Pageable pageable);
    Page<Post> findByAuthor(Pageable pageable, SiteUser user);
}
