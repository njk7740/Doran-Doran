package com.voda.blog.post;

import com.voda.blog.alarm.AlarmService;
import com.voda.blog.comment.CommentForm;
import com.voda.blog.comment.CommentService;
import com.voda.blog.user.SiteUser;
import com.voda.blog.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final AlarmService alarmService;

    @GetMapping("/list")
    public String list(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "order", defaultValue = "liker") String order) {
        model.addAttribute("paging", postService.getList(page, kw, order));
        model.addAttribute("kw", kw);
        if (principal != null) {
            SiteUser user = userService.getByUsername(principal.getName());
            if (user.getNickname() == null || user.getNickname().isEmpty()) return "user_setNick";
            model.addAttribute("user", user);
            model.addAttribute("alarmList", userService.getAlamList(user));
        }
        model.addAttribute("order", order);
        model.addAttribute("newTime", LocalDateTime.now().minusHours(1));

        return "post_list";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model, Principal principal) {
        model.addAttribute("user", userService.getByUsername(principal.getName()));
        return "post_createForm";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model, Principal principal, String subject, String content) {
        model.addAttribute("user", userService.getByUsername(principal.getName()));
        SiteUser user = userService.getByUsername(principal.getName());
        Post post = postService.create(subject, content, user);
        for (SiteUser _user : user.getFavoriteMe())
            alarmService.create(_user, post, "create", user);
        return "redirect:/";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model, Principal principal, CommentForm commentForm,
                         @RequestParam(value = "page", defaultValue = "0") int page) {
        Post post = postService.getById(id);
        if (principal != null) {
            SiteUser user = userService.getByUsername(principal.getName());
            model.addAttribute("user", user);
            model.addAttribute("like", post.getLiker().contains(user));
        } else model.addAttribute("like", false);
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentService.getList(post, page));
        return "post_detail";
    }

    @GetMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    public String modify(@PathVariable("id") Integer id, Model model, Principal principal, PostForm postForm) {
        if (principal != null) model.addAttribute("user", userService.getByUsername(principal.getName()));
        Post post = postService.getById(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) throw new RuntimeException("권한이 없습니다");
        model.addAttribute("subject", post.getSubject());
        model.addAttribute("content", post.getContent());
        return "post_createForm";
    }

    @PostMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    public String modify(@PathVariable("id") Integer id, Model model, Principal principal, @Valid PostForm postForm,
                         BindingResult bindingResult) {
        if (principal != null) model.addAttribute("user", userService.getByUsername(principal.getName()));
        if (bindingResult.hasErrors()) return "post_createForm";
        Post post = postService.getById(id);
        postService.modify(post, postForm.getSubject(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable("id") Integer id, Principal principal) {
        Post post = postService.getById(id);
        SiteUser user = userService.getByUsername(principal.getName());
        if (!post.getAuthor().getUsername().equals(principal.getName())) throw new RuntimeException("권한이 없습니다");
        userService.unlike(user, post);
        postService.delete(post);
        return "redirect:/";
    }

    @GetMapping("/like/{id}")
    @PreAuthorize("isAuthenticated()")
    public String like(@PathVariable("id") Integer id, Principal principal) {
        Post post = postService.getById(id);
        SiteUser user = userService.getByUsername(principal.getName());
        postService.like(post, user);
        userService.like(user, post);
        return String.format("redirect:/post/detail/%s", id);
    }

    @GetMapping("/unlike/{id}")
    @PreAuthorize("isAuthenticated()")
    public String unlike(@PathVariable("id") Integer id, Principal principal) {
        Post post = postService.getById(id);
        SiteUser user = userService.getByUsername(principal.getName());
        postService.unlike(post, user);
        userService.unlike(user, post);
        return String.format("redirect:/post/detail/%s", id);
    }
}
