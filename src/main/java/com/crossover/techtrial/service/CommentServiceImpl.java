package com.crossover.techtrial.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.model.Comment;
import com.crossover.techtrial.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  CommentRepository commentRepository;

  /*
   * Returns all the Comments related to article along with Pagination information.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.CommentService#findAll()
   */
  public Page<Comment> findAll(Pageable pageable) {
    return commentRepository.findAll(pageable);
  }

  /*
   * save comment.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.CommentService#save(com.crossover.techtrial.model.Comment)
   */
  public Comment save(Comment comment) {
    return commentRepository.save(comment);
  }

  /*
   * Returns all the Comments related to article along with Pagination information.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.CommentService#findByArticleId(java.lang.Long)
   */
  public Page<Comment> findByArticleId(Long articleId,Pageable pageable) {
	return commentRepository.findByArticleIdOrderByDate(articleId,pageable);
  }

  /*
   * FindById will find the specific Comment
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.CommentService#findById(java.lang.Long)
   */
	public Comment findById(Long id) {
		return commentRepository.findById(id).orElse(null);
	}

	/*
	   * Delete a particular comment with id.
	   * (non-Javadoc)
	   * @see com.crossover.techtrial.service.CommentService#delete(java.lang.Long)
	   */
	public void delete(Long id) {
		commentRepository.deleteById(id);
		
	}

}
