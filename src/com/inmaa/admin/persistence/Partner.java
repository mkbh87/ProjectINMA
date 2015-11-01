package com.inmaa.admin.persistence;

// Generated 1 nov. 2015 20:22:35 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Partner generated by hbm2java
 */
@Entity
@Table(name = "Partner", catalog = "inmaa", uniqueConstraints = @UniqueConstraint(columnNames = "Partner_Name"))
public class Partner implements java.io.Serializable {

	private Integer partnerId;
	private Integer seqNo;
	private String partnerName;
	private String partnerPlace;
	private String partnerStartDate;
	private String partnerDesc;
	private String partnerLogo;

	public Partner() {
	}

	public Partner(String partnerName, String partnerPlace, String partnerLogo) {
		this.partnerName = partnerName;
		this.partnerPlace = partnerPlace;
		this.partnerLogo = partnerLogo;
	}

	public Partner(Integer seqNo, String partnerName, String partnerPlace,
			String partnerStartDate, String partnerDesc, String partnerLogo) {
		this.seqNo = seqNo;
		this.partnerName = partnerName;
		this.partnerPlace = partnerPlace;
		this.partnerStartDate = partnerStartDate;
		this.partnerDesc = partnerDesc;
		this.partnerLogo = partnerLogo;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Partner_ID", unique = true, nullable = false)
	public Integer getPartnerId() {
		return this.partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}

	@Column(name = "SeqNo")
	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	@Column(name = "Partner_Name", unique = true, nullable = false, length = 45)
	public String getPartnerName() {
		return this.partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	@Column(name = "Partner_Place", nullable = false, length = 45)
	public String getPartnerPlace() {
		return this.partnerPlace;
	}

	public void setPartnerPlace(String partnerPlace) {
		this.partnerPlace = partnerPlace;
	}

	@Column(name = "Partner_StartDate", length = 45)
	public String getPartnerStartDate() {
		return this.partnerStartDate;
	}

	public void setPartnerStartDate(String partnerStartDate) {
		this.partnerStartDate = partnerStartDate;
	}

	@Column(name = "Partner_Desc", length = 16777215)
	public String getPartnerDesc() {
		return this.partnerDesc;
	}

	public void setPartnerDesc(String partnerDesc) {
		this.partnerDesc = partnerDesc;
	}

	@Column(name = "Partner_Logo", nullable = false, length = 45)
	public String getPartnerLogo() {
		return this.partnerLogo;
	}

	public void setPartnerLogo(String partnerLogo) {
		this.partnerLogo = partnerLogo;
	}

}
