package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Article;

public interface IArticleService {
	    public void supprimer(Article entity);
		
		public void enregistrer(Article entity);
		
		public void mettre_a_jour(Article entity);
		
		public List<Article> lister();

		public int maxSeqno();
		
		public void initializeLazyJoins(Article entity);

}
