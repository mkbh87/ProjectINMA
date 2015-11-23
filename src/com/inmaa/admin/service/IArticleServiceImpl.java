package com.inmaa.admin.service;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.Article;

@Service("ArticleService")
@Transactional
public class IArticleServiceImpl implements IArticleService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public void supprimer(Article entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(Article entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
		
	}

	@Override
	public void mettre_a_jour(Article entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Article> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Article").list();
	}
	@Override
	public int maxSeqno() {
		try {

			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM Article");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}
	public void initializeLazyJoins(Article entity)
	{
		SessionFactory sessionfactory = getSessionFactory();
		
		Session session = sessionfactory.openSession();
		
		session.refresh(entity);
		Hibernate.initialize(entity.getProjects());
		Hibernate.initialize(entity.getEvents());

		session.close();

	}
}
