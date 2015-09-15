package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Member;

public interface IMemberService {
	 public void supprimer(Member entity);
		
		public void enregistrer(Member entity);
		
		public void mettre_a_jour(Member entity);
		
		public List<Member> lister();
}
