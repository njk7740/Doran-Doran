package com.voda.blog.alarm;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {
    private final AlarmService alarmService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/confirm/{id}")
    public String confirm(@PathVariable(value = "id") Integer id) {
        Alarm alarm = alarmService.get(id);
        alarmService.confirm(alarm);
        return "redirect:/";
    }
}
