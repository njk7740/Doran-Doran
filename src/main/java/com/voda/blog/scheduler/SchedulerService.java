package com.voda.blog.scheduler;

import com.voda.blog.alarm.AlarmService;
import com.voda.blog.post.Post;
import com.voda.blog.post.PostService;
import com.voda.blog.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private ThreadPoolTaskScheduler scheduler;
    private final PostService postService;
    private final AlarmService alarmService;
    private String cron = "";
    private int year;

    public void start(String subject, String content, SiteUser user) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(getRunnable(subject, content, user), getTrigger());
    }

    private Runnable getRunnable(String subject, String content, SiteUser user) {
        return () -> {
            if (LocalDateTime.now().getYear() == year) {
                Post post = postService.create(subject, content, user);
                alarmService.create(user, post.getId(), "reserveCreate", user);
                scheduler.destroy();
            }
        };
    }

    public Trigger getTrigger() {
        return new CronTrigger(this.cron);
    }

    public void setCron(LocalDateTime time) {
        this.cron = time.format(DateTimeFormatter.ofPattern("s m H d M ?"));
        this.year = time.getYear();
    }
}
