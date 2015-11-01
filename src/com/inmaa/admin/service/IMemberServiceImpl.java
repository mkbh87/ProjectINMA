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

@Service("MemberService")
@Transactional
public class IMemberServiceImpl implements IMemberService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@Override
	public void supprimer(Member entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);	
	}

	@Override
	public void enregistrer(Member entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Member entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Member> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Member").list();
	}

	@Override
	public int maxSeqno() {
		try {

			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT max(seqno) FROM  Member");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
