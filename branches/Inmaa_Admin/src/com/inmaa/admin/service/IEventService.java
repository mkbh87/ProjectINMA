package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Event;
import com.inmaa.admin.persistence.Project;



public interface IEventService {
	 public void supprimer(Event entity);
		
		public void enregistrer(Event entity);
		
		public void mettre_a_jour(Event entity);
		
		public List<Event> lister();

		public int maxSeqno();

		List<Project> listerProject();
}
