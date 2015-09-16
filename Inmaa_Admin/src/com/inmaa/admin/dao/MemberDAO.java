package com.inmaa.admin.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.service.IMemberService;
import com.inmaa.admin.persistence.Member;

public class MemberDAO {
	private static ClassPathXmlApplicationContext context;
    private  static IMemberService memberserviceint;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("/application-context.xml");
	
		memberserviceint= (IMemberService) context.getBean("MemberService");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}
	
//@Test
	public void testFindAll() {
	
	List<Member> maliste= memberserviceint.lister();
	assertNotNull(maliste);
	assertTrue(maliste.size() > 0);
	}

//@Test
//public void testInsertEnseign() {
//
//	Member member = new Member();
//	member.setMembername("Arbia");
//	memberserviceint.enregistrer(member);
//}
}
