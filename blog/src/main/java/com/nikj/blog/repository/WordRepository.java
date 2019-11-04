package com.nikj.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nikj.blog.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer>{
    public Optional<Word> findByWordAndPos(String word, String pos);
}
