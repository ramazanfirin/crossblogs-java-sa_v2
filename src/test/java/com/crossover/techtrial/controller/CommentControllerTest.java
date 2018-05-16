package com.crossover.techtrial.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.exceptions.GlobalExceptionHandler;
import com.crossover.techtrial.exceptions.TestUtil;
import com.crossover.techtrial.model.Article;
import com.crossover.techtrial.model.Comment;
import com.crossover.techtrial.repository.ArticleRepository;
import com.crossover.techtrial.repository.CommentRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class CommentControllerTest {


  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  private CommentRepository commentRepository;
  
  @Autowired
  private ArticleRepository articleRepository;

  private MockMvc restUseRecordMockMvc;
  
  @Autowired
  private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
  
  @Autowired
  private GlobalExceptionHandler exceptionTranslator;
  
  @Autowired
  private CommentController commentController;
  
  private Article article;

  private Comment comment;
  
  @Before
  public void setup() throws Exception {
	  MockitoAnnotations.initMocks(this);
      this.restUseRecordMockMvc = MockMvcBuilders.standaloneSetup(commentController)
    		  .setCustomArgumentResolvers(pageableArgumentResolver)
          .setControllerAdvice(exceptionTranslator)
          .build();
  
  }
  
  public static Article createArticleEntity() {
	  Article article = new Article();
      article.setContent("CONTENT");
	  article.setDate(LocalDateTime.now());
	  article.setEmail("aaa@aaa");
	  article.setPublished(true);
	  article.setTitle("DEFAULT_TITLE");
	  
	  return article;
  }

  public static Comment createCommentEntity() {
	  Comment comment = new Comment();
      comment.setDate(LocalDateTime.now());
      comment.setEmail("aaa@aaa");
	  comment.setMessage("DEFAULT_CONTENT"); 
	  
	  
	  Article article = new Article();
      article.setContent("CONTENT");
	  article.setDate(LocalDateTime.now());
	  article.setEmail("aaa@aaa");
	  article.setPublished(true);
	  article.setTitle("DEFAULT_TITLE");
	  
	  //comment.setArticle(article);
	  return comment;
  }
  
  @Before
  public void initTest() {
	  article = createArticleEntity();
	  comment = createCommentEntity();
  }
  
  @Test
  public void createComment() throws Exception {
	  articleRepository.saveAndFlush(article);
	  System.out.println(article.getId());
	  int databaseSizeBeforeCreate = commentRepository.findAll().size();
      restUseRecordMockMvc.perform(post("/articles/"+article.getId()+"/comments/")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(comment)))
          .andExpect(status().isCreated());

      List<Comment> commentList = commentRepository.findAll();
      assertThat(commentList).hasSize(databaseSizeBeforeCreate + 1);
      Comment testComment = commentList.get(commentList.size() - 1);
      assertThat(testComment.getEmail()).isEqualTo("aaa@aaa");
  }
  
  @Test
  public void updateComment() throws Exception {
	  commentRepository.saveAndFlush(comment);
      
	  int databaseSizeBeforeUpdate = commentRepository.findAll().size();

      Optional<Comment> updatedComment = commentRepository.findById(comment.getId());
      updatedComment.orElseGet(null).setEmail("bbb@bbb");

      restUseRecordMockMvc.perform(put("/comments")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(updatedComment.get())))
          .andExpect(status().isOk());

      List<Comment> commentList = commentRepository.findAll();
      assertThat(commentList).hasSize(databaseSizeBeforeUpdate);
      Comment testComment = commentList.get(commentList.size() - 1);
      assertThat(testComment.getEmail()).isEqualTo("bbb@bbb");
  }
 
  @Test
  public void deleteComment() throws Exception {
	  commentRepository.saveAndFlush(comment);

      int databaseSizeBeforeDelete = commentRepository.findAll().size();

      restUseRecordMockMvc.perform(delete("/comments/{comments-id}", comment.getId())
          .accept(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(status().isOk());

      List<Comment> commentList = commentRepository.findAll();
      assertThat(commentList).hasSize(databaseSizeBeforeDelete - 1);
  }
  
  @Test
  public void getComment() throws Exception {
	  Article articleNew=articleRepository.saveAndFlush(article);
	  comment.setArticle(articleNew);
	  commentRepository.saveAndFlush(comment);
	  
	  restUseRecordMockMvc.perform(get("/comments/{comment-id}", comment.getId()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
          .andExpect(jsonPath("$.article.email").value("aaa@aaa"))
          .andExpect(jsonPath("$.email").value("aaa@aaa"));
  }
  
  @Test
  public void getAllComments() throws Exception {
	  commentRepository.saveAndFlush(comment);
	  restUseRecordMockMvc.perform(get("/comments"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
          .andExpect(jsonPath("$.[*].message").value(hasItem("DEFAULT_CONTENT")));
  }
  
  @Test
  public void getCommentsByArticleId() throws Exception {
	  articleRepository.saveAndFlush(article);
	  comment.setArticle(article);
	  commentRepository.saveAndFlush(comment);
	  restUseRecordMockMvc.perform(get("/articles/{article-id}/comments", article.getId()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
          .andExpect(jsonPath("$.[*].message").value(hasItem("DEFAULT_CONTENT")));
  }
}
