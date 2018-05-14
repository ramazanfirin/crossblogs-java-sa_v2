package com.crossover.techtrial.service;

import java.util.List;

import com.crossover.techtrial.model.Article;
import com.crossover.techtrial.model.Comment;

public interface CommentService {

  /*
   * Returns all items
   */
  List<Comment> findAll();

  /*
   * Save the default article.
   */
  Comment save(Comment comment);

  /*
   * Returns all the Comments related to article along with Pagination information.
   */
  List<Comment> findByArticleId(Long articleId);
  
  /**
   * 
   * @param id
   * @return
   */
  Comment findById(Long id);
  
  /**
   * 
   * @param id
   */
  void delete(Long id);
}
