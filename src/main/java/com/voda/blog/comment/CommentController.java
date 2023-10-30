package com.voda.blog.comment;

import com.voda.blog.post.Post;
import com.voda.blog.post.PostService;
import com.voda.blog.user.SiteUser;
import com.voda.blog.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String create(Model model, Principal principal, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                         BindingResult bindingResult) {
        Post post = postService.getById(id);
        SiteUser user = userService.getByUsername(principal.getName());
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("like", post.getLiker().contains(user));
            model.addAttribute("post", post);
            model.addAttribute("commentList", commentService.getList(post, 0));
            return "post_detail";
        }
        commentService.create(post, user, commentForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = commentService.getById(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) throw new RuntimeException("권한이 없습니다");
        commentService.delete(comment);
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(Principal principal, @PathVariable("id") Integer id, @Valid CommentForm commentForm,
                         BindingResult bindingResult) {
        Comment comment = commentService.getById(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) throw new RuntimeException("권한이 없습니다");
        if (bindingResult.hasErrors()) return String.format("redirect:/post/detail/%s", comment.getPost().getId());
        commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }
}
