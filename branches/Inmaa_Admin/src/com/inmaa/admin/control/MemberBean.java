package com.inmaa.admin.control;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inmaa.admin.persistence.Member;
import com.inmaa.admin.service.IMemberService;


@Component("memberBean")
@SessionScoped
public class MemberBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	IMemberService memberService;

	private Member currentMember ;

	List<Member> target = new ArrayList<Member>();
	List<Member> source = new ArrayList<Member>();
	private DualListModel<Member> memberModel;

	private List<Member> memberList;

	private transient DataModel<Member> members;
	private int id;



	@PostConstruct
	public void init() {
		memberList = memberService.lister();
		members = new ListDataModel<Member>();
		members.setWrappedData( memberService.lister());
		
		source = getmemberList();
		List<Member> target = new ArrayList<Member>();
		memberModel = new DualListModel<Member>(source, target);


	}
	public Member getcurrentMember() {
		return currentMember;
	}

	public void setcurrentMember(Member p) {
		this.currentMember = p;
	}

	public MemberBean(){
		this.currentMember = new Member();	

	}

	public IMemberService getMemberService() {
		return memberService;
	}

	public void setMemberService(IMemberService memberService) {
		this.memberService = memberService;
	}

	public DataModel<Member> getMembers() {
		return members;
	}

	public void setMembers(DataModel<Member> members) {
		this.members = members;
	}

	public String ajouter(){
		memberService.enregistrer(currentMember);
		currentMember = new Member();
		return null;
	}

	public String vider(){
		currentMember = new Member();
		return null;
	}

	public List<Member> getmemberList() {

		return memberList;
	}

	public void setMemberList(List<Member> members) {
		this.memberList = members;
	}

	public String viewMemberDetail(){
		currentMember = getMembers().getRowData();
		return "detailMember";
	}
	public void setmemberModel(DualListModel<Member> memberModel) {
		this.memberModel = memberModel;
	}
	public DualListModel<Member> getmemberModel() {
		return memberModel;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

}
