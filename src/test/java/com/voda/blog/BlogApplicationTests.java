package com.voda.blog;

import com.voda.blog.comment.CommentService;
import com.voda.blog.post.Post;
import com.voda.blog.post.PostService;
import com.voda.blog.user.SiteUser;
import com.voda.blog.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogApplicationTests {
	@Autowired
	private PostService postService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommentService commentService;

	@Test
	void contextLoads() {
		SiteUser user = userService.getByUsername("njk7740");
		Post post = postService.getById(14);
		for(int i = 0; i < 100; i++) {
			commentService.create(post, user, "테스트용 댓글");
		}
	}

}
