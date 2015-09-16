package com.inmaa.admin.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.inmaa.admin.service.IProjectService;
import com.inmaa.admin.persistence.Project;

public class ProjectDAO {
	private static ClassPathXmlApplicationContext context;
    private  static IProjectService projectServiceint;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("/application-context.xml");
	
		projectServiceint = (IProjectService) context.getBean("ProjectService");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}
	
@Test
	public void testFindAll() {
	
	List<Project> maliste= projectServiceint.lister();
	assertNotNull(maliste);
	assertTrue(maliste.size() > 0);
	//System.out.println((projectServiceint.lister()).get(0).getProjectdesc());
	}

////@Test
//public void testInsertEnseign() {
//
//	Project project = new Project();
//	project.setProjectname("Artisanat");
//	projectServiceint.enregistrer(project);
//}
}
