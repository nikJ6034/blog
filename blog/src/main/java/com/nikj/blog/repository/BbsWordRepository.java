package com.nikj.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nikj.blog.entity.BbsWord;
import com.nikj.blog.entity.BbsWordId;

public interface BbsWordRepository extends JpaRepository<BbsWord, BbsWordId>{

}
