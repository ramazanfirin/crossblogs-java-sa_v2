package com.crossover.techtrial.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.crossover.techtrial.model.Article;
import com.crossover.techtrial.model.Comment;
import com.crossover.techtrial.service.ArticleService;
import com.crossover.techtrial.service.CommentService;

@RestController
public class CommentController {
  @Autowired
  CommentService commentService;

  @Autowired
  ArticleService articleService;

  @PostMapping(path = "articles/{article-id}/comments/")
  public ResponseEntity<Comment> createComment(@PathVariable(value = "article-id") Long articleId,
		  @Valid  @RequestBody Comment comment) {
    
	Article article = articleService.findById(articleId);
	if (article != null){
	     comment.setArticle(articleService.findById(articleId));
    	 return new ResponseEntity<>(commentService.save(comment), HttpStatus.CREATED);
	}else
		 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PutMapping(path = "comments")
  public ResponseEntity<Comment> updateComment(@Valid @RequestBody Comment comment) {
    return new ResponseEntity<>(commentService.save(comment), HttpStatus.OK);
  }
  
  @DeleteMapping(path = "comments/{comment-id}")
  public ResponseEntity<Void> deleteCommentById(@PathVariable("comment-id") Long id) {
    commentService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @GetMapping(path = "comments/{comment-id}")
  public ResponseEntity<Comment> getCommentById(@PathVariable("comment-id") Long id) {
	  Comment comment = commentService.findById(id);
	    if (comment != null)
	      return new ResponseEntity<>(comment, HttpStatus.OK);
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }
  
  @GetMapping(path = "comments")
  public ResponseEntity<List<Comment>> findAllComments(Pageable pageable) {
      return new ResponseEntity<>(commentService.findAll(pageable).getContent(), HttpStatus.OK);
  }

  @GetMapping(path = "articles/{article-id}/comments")
  public ResponseEntity<List<Comment>> getComments(@PathVariable("article-id") Long articleId,Pageable pageable) {
	  return new ResponseEntity<>(commentService.findByArticleId(articleId,pageable).getContent(), HttpStatus.OK);
  }
}
