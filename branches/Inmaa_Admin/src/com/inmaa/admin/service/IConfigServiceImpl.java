package com.inmaa.admin.service;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.Config;


@Service("ConfigService")
@Transactional
public class IConfigServiceImpl implements IConfigService {

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void supprimer(Config entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(Config entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Config entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Config> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Config").list();
	}



	@Override
	public int maxSeqno() {
		try {

			int seqno = 0;
			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM  Config");
			if (sqlQuery.uniqueResult() != null)
				seqno= (Integer) sqlQuery.uniqueResult();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}


}
