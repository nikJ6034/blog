package com.nikj.blog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter @Setter
public class Bbs {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column
	private String title;
	@Column(columnDefinition = "TEXT")
	private String content;
	@Column
	private String bbsType;
	@Column
	private String contentId;
}
