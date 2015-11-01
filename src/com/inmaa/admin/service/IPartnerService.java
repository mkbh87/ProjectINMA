package com.inmaa.admin.service;

import java.util.List;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.persistence.Partner;

public interface IPartnerService {
	public void supprimer(Partner entity);

	public void enregistrer(Partner entity);

	public void mettre_a_jour(Partner entity);

	public List<Partner> lister();

	public List<Member> listerMember();

	public Member getMember(Integer memberId);

	public int maxSeqno();

}
