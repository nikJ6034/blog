package com.nikj.blog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(BbsWordId.class)
@Getter @Setter
public class BbsWord {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "bbs_id")
    private Bbs bbs;
    
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "word_id")
    private Word word;
    
    @Column(nullable = true)
    private int count;
}
