package com.crossover.techtrial.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasItem;

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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.exceptions.GlobalExceptionHandler;
import com.crossover.techtrial.exceptions.TestUtil;
import com.crossover.techtrial.model.Article;
import com.crossover.techtrial.repository.ArticleRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
  
  private Article article;
  
  @Before
  public void setup() throws Exception {
	  MockitoAnnotations.initMocks(this);
      this.restUseRecordMockMvc = MockMvcBuilders.standaloneSetup(articleController)
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
      restUseRecordMockMvc.perform(get("/articles/"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
          .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
          .andExpect(jsonPath("$.[*].title").value(hasItem("DEFAULT_TITLE")));
  }
  
  
//  @Test
//  @Rollback(true)
//  public void testArticleShouldBeCreated() throws Exception {
//    HttpEntity<Object> article = getHttpEntity(
//        "{\"email\": \"user1@gmail.com\", \"title\": \"hello\" }");
//    ResponseEntity<Article> resultAsset = template.postForEntity("/articles", article,
//        Article.class);resultAsset.getBody().getId()
//    System.out.println("");
//    //Assert.assertNotNull(resultAsset.getBody().getId());
//  }
  
//  private HttpEntity<Object> getHttpEntity(Object body) {
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//	    return new HttpEntity<Object>(body, headers);
//	  }
// 
}
