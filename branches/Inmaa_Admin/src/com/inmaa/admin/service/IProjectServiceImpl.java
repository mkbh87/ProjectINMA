package com.inmaa.admin.service;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Project;

@Service("ProjectService")
@Transactional
public class IProjectServiceImpl implements IProjectService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public Member getMember(Integer memberId){
		// TODO Auto-generated method stub
		return (Member) sessionFactory.getCurrentSession().get(Member.class, memberId);
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void supprimer(Project entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(Project entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Project entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Project> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Project order by seqNo asc").list();
	}

	@Override
	public List<Member> listerMember() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Member").list();
	}

	@Override
	public int maxSeqno() {
		try {
			//	        String sqlQuery = "SELECT COALESCE(max(seqno),0) FROM Project  ";
			// 	        Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery);


			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM Project");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void initializeLazyJoins(Project entity)
	{
		SessionFactory sessionfactory = getSessionFactory();

		Session session = sessionfactory.openSession();

		session.refresh(entity);
		Hibernate.initialize(entity.getArticles());
		Hibernate.initialize(entity.getEvents());
		Hibernate.initialize(entity.getPartners());
		Hibernate.initialize(entity.getMembers());
		session.close();

	}

}
