package com.crossover.techtrial.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.crossover.techtrial.model.Article;

/*
 * This interface provides all methods to access the functionality. See ArticleServiceImpl for implementation.
 * 
 * @author crossover
 */
public interface ArticleService {

	/**
	 * Save article
	 * 
	 * @param article  which will be saves
	 * @return article which is saved
	 */
	Article save(Article article);

	/**
	 * FindById will find the specific article
	 * 
	 * @param id value which will be find
	 * @return finded article, if not found return null
	 */
	Article findById(Long id);

    /**
     * Delete a particular article with id
     * 
     * @param id value which will be deleted
     */
	void delete(Long id);

	/**
	 * Search Articles Table matching the title and return result with pagination.
	 * 
	 * @param title 
	 * @return list of article
	 */
	 List<Article> search(String title);

	/**
	 * Retun all articles
	 * 
	 * @return list of all article
	 */
	 Page<Article> findAll(Pageable pageable);
  
}
