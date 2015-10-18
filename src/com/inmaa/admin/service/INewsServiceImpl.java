package com.inmaa.admin.service;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.News;

@Service("NewsService")
@Transactional
public class INewsServiceImpl implements INewsService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void supprimer(News entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(News entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
		
	}

	@Override
	public void mettre_a_jour(News entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<News> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from News").list();
	}

	@Override
	public int maxSeqno() {
 	    try {
 	        
 	       Session session=sessionFactory.getCurrentSession();
 	       SQLQuery sqlQuery=session.createSQLQuery("SELECT max(seqno) FROM  News");
 	       int seqno= (Integer) sqlQuery.uniqueResult();
 	       return seqno;

	    } catch (HibernateException e) {
	        e.printStackTrace();
	    }
	    return 0;
	}
}
