package com.inmaa.admin.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.inmaa.admin.service.IEventService;
import com.inmaa.admin.persistence.Event;



public class EventDAO {
	private static ClassPathXmlApplicationContext context;
	private  static IEventService eventserviceint;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		context = new ClassPathXmlApplicationContext("/application-context.xml");

		eventserviceint= (IEventService) context.getBean("EventService");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		context.close();
	}

	//@Test
	public void testFindAll() {

		List<Event> maliste= eventserviceint.lister();
		assertNotNull(maliste);
		assertTrue(maliste.size() > 0);
	}

//	@Test
//	public void testInsertEnseign() {
//
//		Event ev = new Event();
//		ev.setEventname("3idElOm");
//		eventserviceint.enregistrer(ev);
//	}


}
