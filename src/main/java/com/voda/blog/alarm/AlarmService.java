package com.voda.blog.alarm;

import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public void create(SiteUser user, Integer postId, String type, SiteUser target) {
        Alarm alarm = new Alarm(user, type);
        alarm.setPostId(postId);
        alarm.setTarget(target);
        alarmRepository.save(alarm);
    }
}
