package com.voda.blog.alarm;

import com.voda.blog.message.Message;
import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    public void create(SiteUser user, String type, SiteUser target) {
        Alarm alarm = new Alarm(user, type, target);
        alarmRepository.save(alarm);
    }
    public void create(SiteUser user, Integer postId, String type, SiteUser target) {
        Alarm alarm = new Alarm(user, type, target);
        alarm.setPostId(postId);
        alarmRepository.save(alarm);
    }

    public void create(SiteUser user, Message message, String type, SiteUser target) {
        Alarm alarm = new Alarm(user, type, target);
        alarm.setMessage(message);
        alarmRepository.save(alarm);
    }

    public void delete(SiteUser user, SiteUser target, String type) {
        for (Alarm alarm : user.getAlarm()) {
            if (alarm.getType().equals(type) && alarm.getTarget().getUsername().equals(target.getUsername()))
                alarmRepository.delete(alarm);
        }
    }

    public Alarm get(Integer id) {
        Optional<Alarm> alarm = alarmRepository.findById(id);
        if (alarm.isPresent()) return alarm.get();
        throw new RuntimeException("알림을 찾을 수 없습니다.");
    }

    public void confirm(Alarm alarm) {
        alarm.setConfirm(true);
        alarmRepository.save(alarm);
    }
}
