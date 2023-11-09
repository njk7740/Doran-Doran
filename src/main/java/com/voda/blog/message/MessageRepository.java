package com.voda.blog.message;

import com.voda.blog.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByAuthorOrderByCreateDateDesc(SiteUser author);
    List<Message> findByReceiverOrderByCreateDateDesc(SiteUser receiver);
}
