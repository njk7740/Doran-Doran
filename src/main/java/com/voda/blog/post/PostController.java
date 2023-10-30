package com.voda.blog.post;

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

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping("/list")
    public String list(Model model, Principal principal, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "order", defaultValue = "liker") String order) {
        model.addAttribute("paging", postService.getList(page, kw, order));
        model.addAttribute("kw", kw);
        if (principal != null) model.addAttribute("user", userService.getByUsername(principal.getName()));
        model.addAttribute("order", order);
        return "post_list";
    }

    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(PostForm postForm, Model model, Principal principal) {
        if (principal != null) model.addAttribute("user", userService.getByUsername(principal.getName()));
        return "post_createForm";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid PostForm postForm, BindingResult bindingResult, Model model, Principal principal) {
        if (principal != null) model.addAttribute("user", userService.getByUsername(principal.getName()));
        if (bindingResult.hasErrors()) return "post_createForm";
        SiteUser user = userService.getByUsername(principal.getName());
        postService.create(postForm.getSubject(), postForm.getContent(), user);
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
        postForm.setSubject(post.getSubject());
        postForm.setContent(post.getContent());
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
        if (!post.getAuthor().getUsername().equals(principal.getName())) throw new RuntimeException("권한이 없습니다");
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
