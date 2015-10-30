package com.inmaa.admin.persistence;

// Generated 29 oct. 2015 21:42:46 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Member generated by hbm2java
 */
@Entity
@Table(name = "Member", catalog = "inmaa", uniqueConstraints = @UniqueConstraint(columnNames = "Member_Name"))
public class Member implements java.io.Serializable {

	private Integer memberId;
	private Integer seqNo;
	private String memberName;
	private String memberName1;
	private String memberStatus;
	private int memberAge;
	private boolean memberIsOrg;
	private String memberTravail;
	private String memberImage;
	private String memberFacebook;
	private String memberTwitter;
	private Set<Project> projects = new HashSet<Project>(0);

	public Member() {
	}

	public Member(String memberName, String memberName1, int memberAge,
			boolean memberIsOrg, String memberImage) {
		this.memberName = memberName;
		this.memberName1 = memberName1;
		this.memberAge = memberAge;
		this.memberIsOrg = memberIsOrg;
		this.memberImage = memberImage;
	}

	public Member(Integer seqNo, String memberName, String memberName1,
			String memberStatus, int memberAge, boolean memberIsOrg,
			String memberTravail, String memberImage, String memberFacebook,
			String memberTwitter, Set<Project> projects) {
		this.seqNo = seqNo;
		this.memberName = memberName;
		this.memberName1 = memberName1;
		this.memberStatus = memberStatus;
		this.memberAge = memberAge;
		this.memberIsOrg = memberIsOrg;
		this.memberTravail = memberTravail;
		this.memberImage = memberImage;
		this.memberFacebook = memberFacebook;
		this.memberTwitter = memberTwitter;
		this.projects = projects;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Member_ID", unique = true, nullable = false)
	public Integer getMemberId() {
		return this.memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	@Column(name = "SeqNo")
	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	@Column(name = "Member_Name", unique = true, nullable = false)
	public String getMemberName() {
		return this.memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	@Column(name = "Member_Name1", nullable = false)
	public String getMemberName1() {
		return this.memberName1;
	}

	public void setMemberName1(String memberName1) {
		this.memberName1 = memberName1;
	}

	@Column(name = "Member_Status")
	public String getMemberStatus() {
		return this.memberStatus;
	}

	public void setMemberStatus(String memberStatus) {
		this.memberStatus = memberStatus;
	}

	@Column(name = "Member_Age", nullable = false)
	public int getMemberAge() {
		return this.memberAge;
	}

	public void setMemberAge(int memberAge) {
		this.memberAge = memberAge;
	}

	@Column(name = "Member_IsOrg", nullable = false)
	public boolean isMemberIsOrg() {
		return this.memberIsOrg;
	}

	public void setMemberIsOrg(boolean memberIsOrg) {
		this.memberIsOrg = memberIsOrg;
	}

	@Column(name = "Member_Travail")
	public String getMemberTravail() {
		return this.memberTravail;
	}

	public void setMemberTravail(String memberTravail) {
		this.memberTravail = memberTravail;
	}

	@Column(name = "Member_Image", nullable = false, length = 45)
	public String getMemberImage() {
		return this.memberImage;
	}

	public void setMemberImage(String memberImage) {
		this.memberImage = memberImage;
	}

	@Column(name = "Member_facebook", length = 45)
	public String getMemberFacebook() {
		return this.memberFacebook;
	}

	public void setMemberFacebook(String memberFacebook) {
		this.memberFacebook = memberFacebook;
	}

	@Column(name = "Member_twitter", length = 45)
	public String getMemberTwitter() {
		return this.memberTwitter;
	}

	public void setMemberTwitter(String memberTwitter) {
		this.memberTwitter = memberTwitter;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "members")
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}