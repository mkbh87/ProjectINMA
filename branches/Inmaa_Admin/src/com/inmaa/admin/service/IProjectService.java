package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Project;

public interface IProjectService {
	public void supprimer(Project entity);

	public void enregistrer(Project entity);

	public void mettre_a_jour(Project entity);

	public List<Project> lister();

	public List<Member> listerMember();

	public Member getMember(Integer memberId);

	public int maxSeqno();
	
	public void initializeLazyJoins(Project entity);

}
