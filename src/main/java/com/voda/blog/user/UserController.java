package com.voda.blog.user;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String info(Model model, Principal principal) {
        model.addAttribute("user", userService.getByUsername(principal.getName()));
        return "user_info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/myPost")
    public String myPost(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page) {
        SiteUser user = userService.getByUsername(principal.getName());
        Page<Post> postList = postService.getByAuthor(page, user);
        model.addAttribute("user", user);
        model.addAttribute("postList", postList);
        return "user_myPost";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/likePost")
    public String myPost(Model model, Principal principal) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
        return "user_likePost";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info/modify")
    public String modify(Model model, Principal principal, UserModifyForm userModifyForm) {
        SiteUser user = userService.getByUsername(principal.getName());
        model.addAttribute("user", user);
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
}
