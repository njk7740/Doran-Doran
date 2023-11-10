package com.voda.blog.message;

import com.voda.blog.alarm.AlarmService;
import com.voda.blog.user.SiteUser;
import com.voda.blog.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;
    private final AlarmService alarmService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/send")
    public String send(String message, String receiver, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        SiteUser target = userService.getByUsername(receiver);
        Message m = messageService.create(user, target, message);
        alarmService.create(user, m, "sendMessage", target);
        alarmService.create(target, m, "receiveMessage", user);
        return "redirect:/";
    }
}
