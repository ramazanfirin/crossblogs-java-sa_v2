package com.crossover.techtrial.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import com.crossover.techtrial.repository.ArticleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public class ArticleControllerTest {


  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  private ArticleRepository articleRepository;

  private MockMvc restUseRecordMockMvc;
  
  @Autowired
  private GlobalExceptionHandler exceptionTranslator;
  
  @Autowired
  private ArticleController articleController;
  
  @Autowired
  private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
  
  private Article article;
  
  @Before
  public void setup() throws Exception {
	  MockitoAnnotations.initMocks(this);
      this.restUseRecordMockMvc = MockMvcBuilders.standaloneSetup(articleController)
    	  .setCustomArgumentResolvers(pageableArgumentResolver)
          .setControllerAdvice(exceptionTranslator)
          .build();
  
  }
  
  public static Article createEntity() {
	  Article article = new Article();
      article.setContent("CONTENT");
	  article.setDate(LocalDateTime.now());
	  article.setEmail("adasd@asdas");
	  article.setPublished(true);
	  article.setTitle("DEFAULT_TITLE");
	  
	  return article;
  }

  @Before
  public void initTest() {
	  article = createEntity();
  }
  
  @Test
  public void createArticle() throws Exception {
      int databaseSizeBeforeCreate = articleRepository.findAll().size();

      restUseRecordMockMvc.perform(post("/articles")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(article)))
          .andExpect(status().isCreated());
          

      List<Article> articleList = articleRepository.findAll();
      assertThat(articleList).hasSize(databaseSizeBeforeCreate + 1);
      Article testArticle = articleList.get(articleList.size() - 1);
      assertThat(testArticle.getTitle()).isEqualTo("DEFAULT_TITLE");
  }
  
  @Test
  public void updateArticle() throws Exception {
	  articleRepository.saveAndFlush(article);
      int databaseSizeBeforeUpdate = articleRepository.findAll().size();

      Optional<Article> updatedArticle = articleRepository.findById(article.getId());
      updatedArticle.orElseGet(null).setTitle("UPDATED_TITLE");

      restUseRecordMockMvc.perform(put("/articles")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(updatedArticle.get())))
          .andExpect(status().isOk());

      List<Article> cityList = articleRepository.findAll();
      assertThat(cityList).hasSize(databaseSizeBeforeUpdate);
      Article testCity = cityList.get(cityList.size() - 1);
      assertThat(testCity.getTitle()).isEqualTo("UPDATED_TITLE");
  }
  
  @Test
  public void deleteArticle() throws Exception {
      articleRepository.saveAndFlush(article);
      int databaseSizeBeforeDelete = articleRepository.findAll().size();

      restUseRecordMockMvc.perform(delete("/articles/{article-id}", article.getId())
          .accept(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(status().isOk());

      List<Article> cityList = articleRepository.findAll();
      assertThat(cityList).hasSize(databaseSizeBeforeDelete - 1);
  }
  
  @Test
  @Transactional
  public void checkEmailRequired() throws Exception {
      int databaseSizeBeforeTest = articleRepository.findAll().size();
      article.setEmail(null);

      restUseRecordMockMvc.perform(post("/articles")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(article)))
          .andExpect(status().isBadRequest());

      List<Article> articleList = articleRepository.findAll();
      assertThat(articleList).hasSize(databaseSizeBeforeTest);
  }
  
  @Test
  public void checkTitleLength() throws Exception {
      int databaseSizeBeforeTest = articleRepository.findAll().size();
      StringBuffer temp=new StringBuffer();
      for (int i = 0; i < 150; i++) {
    	  temp.append(i);
	}
      
      article.setTitle(temp.toString());

      restUseRecordMockMvc.perform(post("/articles")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(article)))
          .andExpect(status().isBadRequest());

      List<Article> articleList = articleRepository.findAll();
      assertThat(articleList).hasSize(databaseSizeBeforeTest);
  }
  
  @Test
  public void checkContentLength() throws Exception {
      int databaseSizeBeforeTest = articleRepository.findAll().size();
      StringBuffer temp=new StringBuffer();
      for (int i = 0; i < 35000; i++) {
    	  temp.append(i);
	}
      
      article.setContent(temp.toString());

      restUseRecordMockMvc.perform(post("/articles")
          .contentType(TestUtil.APPLICATION_JSON_UTF8)
          .content(TestUtil.convertObjectToJsonBytes(article)))
          .andExpect(status().isBadRequest());

      List<Article> articleList = articleRepository.findAll();
      assertThat(articleList).hasSize(databaseSizeBeforeTest);
  }
  
  @Test
  public void getArticle() throws Exception {

	  articleRepository.saveAndFlush(article);
	  
	  restUseRecordMockMvc.perform(get("/articles/{article-id}", article.getId()))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.id").value(article.getId().intValue()))
          .andExpect(jsonPath("$.title").value("DEFAULT_TITLE"));
  }
  
  @Test
  public void getNonExistingArticle() throws Exception {
	  restUseRecordMockMvc.perform(get("/articles/{article-id}", Long.MAX_VALUE))
          .andExpect(status().isNotFound());
  }
  
  @Test
  public void getAllArticles() throws Exception {
      articleRepository.saveAndFlush(article);
      restUseRecordMockMvc.perform(get("/articles?sort=id,desc"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
          .andExpect(jsonPath("$.[*].title").value(hasItem("DEFAULT_TITLE")));
  }
  
  @Test
  public void getAllArticlesPagination() throws Exception {
      articleRepository.saveAndFlush(article);
   
      Article article2 = new Article();
      article2.setContent("CONTENT");
	  article2.setDate(LocalDateTime.now());
	  article2.setEmail("adasd@asdas");
	  article2.setPublished(true);
	  article2.setTitle("CONTENT");
      
	  articleRepository.saveAndFlush(article2);
	  
      restUseRecordMockMvc.perform(get("/articles?page=0&size=1"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  public void search() throws Exception {
      articleRepository.saveAndFlush(article);
   
      Article article2 = new Article();
      article2.setContent("CONTENT");
	  article2.setDate(LocalDateTime.now());
	  article2.setEmail("adasd@asdas");
	  article2.setPublished(true);
	  article2.setTitle("CONTENT");
      
	  articleRepository.saveAndFlush(article2);
	  
	  int size = articleRepository.findAll().size();
	  System.out.println(size);
	  
      restUseRecordMockMvc.perform(get("/articles/search?text=heat"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$", hasSize(1)));
  }

  
  
  

}
