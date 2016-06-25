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
import com.inmaa.admin.persistence.Partner;

@Service("PartnerService")
@Transactional
public class IPartnerServiceImpl implements IPartnerService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void supprimer(Partner entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(Partner entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Partner entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Partner> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Partner  order by seqNo").list();
	}

	@Override
	public int maxSeqno() {
		try {
			//	        String sqlQuery = "SELECT COALESCE(max(seqno),0) FROM  Partner  ";
			// 	        Query query = sessionFactory.getCurrentSession().createQuery(sqlQuery);


			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM  Partner");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
