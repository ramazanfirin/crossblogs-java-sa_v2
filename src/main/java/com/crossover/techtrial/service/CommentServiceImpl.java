package com.crossover.techtrial.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crossover.techtrial.model.Comment;
import com.crossover.techtrial.repository.CommentRepository;

@Service
public class CommentServiceImpl implements CommentService {

  @Autowired
  CommentRepository commentRepository;

  /*
   * Returns all comments.
   */
  public List<Comment> findAll() {
    return commentRepository.findAll();
  }

  /*
   * Save the default article.
   */
  public Comment save(Comment comment) {
    return commentRepository.save(comment);
  }

  /*
   * Returns all the Comments related to article along with Pagination information.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.CommentService#findByArticleId(java.lang.Long)
   */
  public List<Comment> findByArticleId(Long articleId) {
	return commentRepository.findByArticleIdOrderByDate(articleId);
}

/**
 * 
 */
public Comment findById(Long id) {
	return commentRepository.findById(id).orElse(null);
}

@Override
public void delete(Long id) {
	commentRepository.deleteById(id);
	
}

}
