package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Publication;

public interface IPublicationService {
	public void supprimer(Publication entity);

	public void enregistrer(Publication entity);

	public void mettre_a_jour(Publication entity);

	public List<Publication> lister();

	public List<Member> listerMember();

	public Member getMember(Integer memberId);

	public int maxSeqno();

}
