package com.inmaa.admin.service;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inmaa.admin.persistence.Abonnement;
import com.inmaa.admin.persistence.SubEvent;

@Service("AboService")
@Transactional
public class IAboServiceImpl implements IAboService{

	@Autowired
	SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}



	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	@Override
	public void supprimer(Abonnement entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().delete(entity);	
	}

	@Override
	public void enregistrer(Abonnement entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().saveOrUpdate(entity);

	}

	@Override
	public void mettre_a_jour(Abonnement entity) {
		// TODO Auto-generated method stub
		sessionFactory.getCurrentSession().update(entity);
	}

	@Override
	public List<Abonnement> lister() {
		// TODO Auto-generated method stub
		return sessionFactory.getCurrentSession().createQuery("from Abonnement").list();
	}



	@Override
	public List<Abonnement> listerbyMember(int id) {
		List<Integer> ids =  Arrays.asList(id);

		return sessionFactory.getCurrentSession().createQuery("from Abonnement where Abonnement_MemberID = "+id).list();
	}
}
