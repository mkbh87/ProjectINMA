package com.inmaa.admin.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.SubEvent;


@Service("SubEventService")
@Transactional
public class ISubEventServiceImpl implements ISubEventService {

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void supprimer(SubEvent entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);
	}

	@Override
	public void enregistrer(SubEvent entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(SubEvent entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<SubEvent> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from SubEvent").list();
	}



	@Override
	public int maxSeqno() {
		try {

			Session session=sessionFactory.getCurrentSession();
			SQLQuery sqlQuery=session.createSQLQuery("SELECT COALESCE(max(seqno),0) FROM SubEvent");
			int seqno=  ((BigInteger) sqlQuery.uniqueResult()).intValue();
			return seqno;

		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void initializeLazyJoins(SubEvent entity)
	{
		SessionFactory sessionfactory = getSessionFactory();
		
		Session session = sessionfactory.openSession();
		
		session.refresh(entity);
		Hibernate.initialize(entity.getEvents());
 
		session.close();

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SubEvent> listerbyEvent(int p_id) {
		List<Integer> ids =  Arrays.asList(  p_id);

		return sessionFactory
			    .getCurrentSession()
			    .createCriteria(SubEvent.class)
			    .createAlias("events", "ep")
			    .add( Restrictions.in( "ep.eventId", ids ) )
			    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
			    .addOrder( Order.asc("subEventStartDate") )
			    .list();
	}


}
