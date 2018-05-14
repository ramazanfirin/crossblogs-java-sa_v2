package com.crossover.techtrial.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.crossover.techtrial.model.Comment;

public interface CommentService {

    /**
     * Returns all the Comments related to article along with Pagination information.
     * 
     * @return list of all comments with pagination
     */
	Page<Comment> findAll(Pageable pageable);

	/**
	 * save comment
	 * 
	 * @param comment value will be saves
	 * @return saved comment vaue
	 */
	Comment save(Comment comment);

	/**
	 * Returns all the Comments related to article along with Pagination information.
	 * 
	 * @param articleId id of article
	 * @return list of article
	 */
	Page<Comment> findByArticleId(Long articleId,Pageable pageable);
  
	/**
	 *FindById will find the specific Comment
	 * 
	 * @param id value will be search
	 * @return found in the search result
	 */
	Comment findById(Long id);
  
	  /**
	   * Delete a particular comment with id
	   * 
	   * @param id value of id whichs will be deleted
	   */
	  void delete(Long id);
}
