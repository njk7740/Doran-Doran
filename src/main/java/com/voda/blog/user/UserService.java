package com.voda.blog.user;

import com.voda.blog.SortBy;
import com.voda.blog.alarm.Alarm;
import com.voda.blog.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser getByUsername(String username) {
        Optional<SiteUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) return user.get();
        throw new RuntimeException("유저 찾기 실패");
    }

    public void create(UserCreateForm userCreateForm) {
        SiteUser user = new SiteUser();
        user.setUsername(userCreateForm.getUsername());
        user.setPassword(passwordEncoder.encode(userCreateForm.getPassword1()));
        user.setEmail(userCreateForm.getEmail());
        user.setNickname(userCreateForm.getNickname());
        user.setCreateDate(LocalDateTime.now());
        user.setAge(userCreateForm.getAge());
        user.setRName(userCreateForm.getRName());
        user.setPNum(userCreateForm.getPNum());
        user.setIntro(userCreateForm.getIntro());
        user.setAddress(userCreateForm.getAddress());
        user.setJob(userCreateForm.getJob());
        user.setPicture("/default-profile.png");
        userRepository.save(user);
    }

    public void modify(SiteUser user, UserModifyForm userModifyForm) {
        user.setRName(userModifyForm.getRName());
        user.setJob(userModifyForm.getJob());
        user.setPNum(userModifyForm.getPNum());
        user.setAge(userModifyForm.getAge());
        user.setIntro(userModifyForm.getIntro());
        user.setAddress(userModifyForm.getAddress());
        userRepository.save(user);
    }

    public void like(SiteUser user, Post post) {
        user.getLikePost().add(post);
        userRepository.save(user);
    }

    public void unlike(SiteUser user, Post post) {
        user.getLikePost().remove(post);
        userRepository.save(user);
    }

    public void modifyPicture(SiteUser user, String path) {
        user.setPicture(path);
        userRepository.save(user);
    }

    public void favorite(SiteUser user, String username) {
        user.getFavorite().add(username);
        userRepository.save(user);
    }

    public void unfavor(SiteUser user, String username) {
        user.getFavorite().remove(username);
        userRepository.save(user);
    }

    public List<Alarm> getAlamList(SiteUser user) {
        List<Alarm> alarmList = new ArrayList<>();
        Set<String> favorList = user.getFavorite();
        for (String favor : favorList) {
            SiteUser _user = getByUsername(favor);
            for (Alarm a : _user.getAlarm())
                if (a.getType().equals("create"))
                    alarmList.add(a);
        }
        for (Alarm a : user.getAlarm())
            if (a.getType().equals("comment"))
                alarmList.add(a);
        alarmList.sort(new SortBy());
        return alarmList;
    }

    public void setNick(SiteUser user, String nickname) {
        user.setNickname(nickname);
        userRepository.save(user);
    }
}
