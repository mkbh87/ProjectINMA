package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Users;

public interface IUsersService {
	    public void supprimer(Users entity);
		
		public void enregistrer(Users entity);
		
		public void mettre_a_jour(Users entity);
		
		public List<Users> lister();

		public int maxSeqno();
		
		public void initializeLazyJoins(Users entity);

		public Users findUserForAuthentication(String username);

}
