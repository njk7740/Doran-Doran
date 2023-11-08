package com.voda.blog.alarm;

import com.voda.blog.post.Post;
import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public void create(SiteUser user, Post post, String type, SiteUser target) {
        Alarm alarm = new Alarm(user, type);
        alarm.setPost(post);
        alarm.setTarget(target);
        alarmRepository.save(alarm);
    }
}
