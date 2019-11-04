package com.nikj.blog.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BbsWordId implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private int bbs;
    private int word;
    
 // equals, hashcode 구현
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      BbsWordId that = (BbsWordId) o;
      return Objects.equals(bbs, that.bbs) &&
          Objects.equals(word, that.word);
    }
    
    @Override
    public int hashCode() {

      return Objects.hash(bbs, word);
    }
}
