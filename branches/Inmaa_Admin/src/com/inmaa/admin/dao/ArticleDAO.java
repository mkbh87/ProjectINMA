package com.inmaa.admin.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.service.IArticleService;
import com.inmaa.admin.persistence.Article;

public class ArticleDAO {
	private static ClassPathXmlApplicationContext context;
    private  static IArticleService articleserviceint;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("/application-context.xml");
	
		articleserviceint = (IArticleService) context.getBean("ArticleService");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}
	
//@Test
	public void testFindAll() {
	
	List<Article> maliste= articleserviceint.lister();
	assertNotNull(maliste);
	assertTrue(maliste.size() > 0);
	}

//@Test
//public void testInsertEnseign() {
//
//	Article Article = new Article();
//	Article.setArticletitle("Touristes soutiennent la tunisie");
//	articleserviceint.enregistrer(Article);
//}
}
