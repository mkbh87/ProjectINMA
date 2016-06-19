package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Abonnement;

public interface IAboService {
	 public void supprimer(Abonnement entity);
		
		public void enregistrer(Abonnement entity);
		
		public void mettre_a_jour(Abonnement entity);
		
		public List<Abonnement> lister();

		public List<Abonnement> listerbyMember(int id);

 }
