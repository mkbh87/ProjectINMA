package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Media;

public interface IMediaService {
	    public void supprimer(Media entity);
		
		public void enregistrer(Media entity);
		
		public void mettre_a_jour(Media entity);
		
		public List<Media> listerPic();

		public int maxSeqno();
		
		public void initializeLazyJoins(Media entity);

		public List<Media> listerVideos();

}
