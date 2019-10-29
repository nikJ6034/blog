package com.nikj.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nikj.blog.crawling.Blog;

@SpringBootApplication
public class BlogApplication {

    
    
	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
		
		Blog blog = new Blog();
		
		blog.test();
	}

}
