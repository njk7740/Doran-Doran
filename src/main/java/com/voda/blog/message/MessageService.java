package com.voda.blog.message;

import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public void create(SiteUser author, SiteUser receiver, String content) {
        Message message = new Message();
        message.setAuthor(author);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setCreateDate(LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getSendMessage(SiteUser user) {
        return messageRepository.findByAuthorOrderByCreateDateDesc(user);
    }

    public List<Message> getReceiveMessage(SiteUser user) {
        return messageRepository.findByReceiverOrderByCreateDateDesc(user);
    }
}
