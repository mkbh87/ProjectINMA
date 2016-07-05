package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Contact;

public interface IContactService {
	 public void supprimer(Contact entity);
		
		public void enregistrer(Contact entity);
		
		public void mettre_a_jour(Contact entity);
		
		public List<Contact> lister();
		
		public int maxSeqno();

		public List<String> getListOfTypes();

 }
