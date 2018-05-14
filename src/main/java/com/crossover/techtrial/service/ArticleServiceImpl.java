package com.crossover.techtrial.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.model.Article;
import com.crossover.techtrial.repository.ArticleRepository;

@Service
public class ArticleServiceImpl implements ArticleService {

  @Autowired
  ArticleRepository articleRepository;

  /*
   * save article.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.ArticleService#save(com.crossover.techtrial.model.Article)
   */
  public Article save(Article article) {
    return articleRepository.save(article);
  }

  /*
   * FindById will find the specific Comment
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.ArticleService#findById(java.lang.Long)
   */
  public Article findById(Long id) {
    return articleRepository.findById(id).orElse(null);
  }

  /*
   * Delete a particular comment with id.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.ArticleService#delete(java.lang.Long)
   */
  public void delete(Long id) {
    articleRepository.deleteById(id);
  }

  
  /*
	 * Search Articles Table matching the title and return result with pagination.
	 * (non-Javadoc) 
	 * @see com.crossover.techtrial.service.ArticleService#search(java.lang.String) 
	 */
  public List<Article> search(String search) {
    return articleRepository
        .findTop10ByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search, search);
  }
  
  /*
   * Returns all the Article with Pagination information.
   * (non-Javadoc)
   * @see com.crossover.techtrial.service.ArticleService#findAll()
   */
  public Page<Article> findAll(Pageable pageable) {
		return articleRepository.findAll(pageable);
	}

}