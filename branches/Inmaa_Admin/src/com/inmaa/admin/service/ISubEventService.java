package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.SubEvent;



public interface ISubEventService {
	 public void supprimer(SubEvent entity);
		
		public void enregistrer(SubEvent entity);
		
		public void mettre_a_jour(SubEvent entity);
		
		public List<SubEvent> lister();

		public int maxSeqno();
		
		public void initializeLazyJoins(SubEvent entity);

}
