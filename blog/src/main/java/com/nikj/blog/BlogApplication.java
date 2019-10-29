package com.nikj.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nikj.blog.crawling.Blog;

@SpringBootApplication
public class BlogApplication {

    @Autowired
	static
    Blog blog;
    
	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

}
