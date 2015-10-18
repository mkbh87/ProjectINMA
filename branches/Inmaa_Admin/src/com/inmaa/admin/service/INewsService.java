package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.News;

public interface INewsService {
	    public void supprimer(News entity);
		
		public void enregistrer(News entity);
		
		public void mettre_a_jour(News entity);
		
		public List<News> lister();

		public int maxSeqno();
}
