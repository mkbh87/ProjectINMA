package com.inmaa.admin.service;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Publication;

@Service("PublicationService")
@Transactional
public class IPublicationServiceImpl implements IPublicationService{

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
	public void supprimer(Publication entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(Publication entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Publication entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Publication> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Publication").list();
	}

	@Override
	public List<Member> listerMember() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Member").list();
	}

	@Override
	public int maxSeqno() {
		try {
			//	        String sqlQuery = "SELECT COALESCE(max(seqno),0) FROM  Publication  ";
			// 	        Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery);


			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM  Publication");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
