package com.inmaa.admin.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.service.INewsService;
import com.inmaa.admin.persistence.News;

public class NewsDAO {
	private static ClassPathXmlApplicationContext context;
    private  static INewsService newsserviceint;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("/application-context.xml");
	
		newsserviceint = (INewsService) context.getBean("NewsService");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}
	
//@Test
	public void testFindAll() {
	
	List<News> maliste= newsserviceint.lister();
	assertNotNull(maliste);
	assertTrue(maliste.size() > 0);
	}

//@Test
//public void testInsertEnseign() {
//
//	News News = new News();
//	News.setNewstitle("Touristes soutiennent la tunisie");
//	newsserviceint.enregistrer(News);
//}
}
