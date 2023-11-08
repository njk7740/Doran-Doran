package com.voda.blog.user;

import com.voda.blog.alarm.AlarmService;
import com.voda.blog.post.Post;
import com.voda.blog.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final AlarmService alarmService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "user_signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "user_signup";
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect",
                    "비밀번호와 확인이 일치하지 않습니다");
            return "user_signup";
        }
        try {
            userService.create(userCreateForm);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("userIncorrect", "아이디 또는 닉네임이 중복입니다");
            return "user_signup";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "user_login";
    }

    @GetMapping("/info")
    public String Info(Principal principal) {
        return String.format("redirect:/user/info/%s", principal.getName());
    }

    @GetMapping("/{username}/writePost")
    public String writePost(Model model, @PathVariable(value = "username") String username, @RequestParam(value = "page",
            defaultValue = "0") int page, Principal principal) {
        SiteUser target = userService.getByUsername(username);
        Page<Post> postList = postService.getByAuthor(page, target);
        model.addAttribute("postList", postList);
        attributeInSide(model, principal, username);
        return "user_myPost";
    }

    @GetMapping("/{username}/likePost")
    public String likePost(Model model, @PathVariable(value = "username") String username, Principal principal) {
        attributeInSide(model, principal, username);
        return "user_likePost";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/modify")
    public String modify(Model model, Principal principal, UserModifyForm userModifyForm) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("target", user);
        model.addAttribute("own", true);
        userModifyForm.setAge(user.getAge());
        userModifyForm.setJob(user.getJob());
        userModifyForm.setAddress(user.getAddress());
        userModifyForm.setIntro(user.getIntro());
        userModifyForm.setPNum(user.getPNum());
        userModifyForm.setRName(user.getRName());
        return "user_modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/modify")
    public String modify(Model model, Principal principal, @Valid UserModifyForm userModifyForm, BindingResult bindingResult) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        userService.modify(user, userModifyForm);
        return "redirect:/user/info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/modifyPicture")
    public String modifyPicture(Model model, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user_modifyPicture";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/info/modifyPicture")
    public String modifyPicture(Model model, Principal principal, @RequestParam(value = "file") MultipartFile file) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        String filePath;
        if (file.isEmpty()) userService.modifyPicture(user, "/default-profile.png");
        else {
            String originalFilename = file.getOriginalFilename();
            int idx = originalFilename.lastIndexOf(".");
            String ext = originalFilename.substring(idx);
            String dirPath = "src/main/resources/static/" + user.getUsername();
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
            filePath = dir.getAbsolutePath() + "/profile" + ext;
            try {
                file.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            userService.modifyPicture(user, String.format("/%s/profile%s", user.getUsername(), ext));
        }

        return "redirect:/user/info";
    }

    @GetMapping("/info/{username}")
    public String info(Model model, @PathVariable(value = "username") String username, Principal principal) {
        attributeInSide(model, principal, username);
        return "user_info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/favorite/{username}")
    public String favorite(@PathVariable(value = "username") String username, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        SiteUser target = userService.getByUsername(username);
        userService.favorite(user, target);
        return String.format("redirect:/user/info/%s", username);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unfavor/{username}")
    public String unfavor(@PathVariable(value = "username") String username, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        SiteUser target = userService.getByUsername(username);
        userService.unfavor(user, target);
        return String.format("redirect:/user/info/%s", username);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/renick")
    public String renick(String nickname, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        userService.setNick(user, nickname);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/requestFriend/{username}")
    public String friend(@PathVariable(value = "username") String username, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        SiteUser target = userService.getByUsername(username);
        alarmService.create(target, null, "receiveFriendRequest", user);
        alarmService.create(user, null, "requestFriend", target);
        return String.format("redirect:/user/info/%s", username);
    }

    public void attributeInSide(Model model, Principal principal, String username) {
        SiteUser target = userService.getByUsername(username);
        model.addAttribute("target", target);
        if (principal != null) {
            SiteUser user = userService.getByUsername(principal.getName());
            model.addAttribute("own", username.equals(principal.getName()));
            model.addAttribute("user", user);
            model.addAttribute("favorite", user.getFavorite().contains(target));
            model.addAttribute("hasRequest", userService.hasRequestFriend(user, target));
        } else model.addAttribute("own", false);
    }
}
