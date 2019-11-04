package com.nikj.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nikj.blog.service.Blog;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class BlogController {
    
    @Autowired
    private Blog blog;
    
    @GetMapping("/blog/logid")
    public Mono<Object> logid(){
        return Mono.just(blog.test3());
    }
    
    @GetMapping("/blog/logid2")
    public Flux<Object> logid2(){
        return Flux.just(blog.test3(),blog.test2());
    }
    
    @GetMapping("/blog/komo")
    public Mono<String> komo(){
        return Mono.fromCallable(blog::komo)
                .subscribeOn(Schedulers.elastic()).log();
        //return Mono.just(blog.komo());
    }
}
