package com.inmaa.admin.persistence;
// Generated 6 déc. 2015 18:31:26 by Hibernate Tools 4.3.1.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Media generated by hbm2java
 */
@Entity
@Table(name = "Media", catalog = "inmaa")
public class Media implements java.io.Serializable {

	private Integer mediaId;
	private String mediaName;
	private Date mediaDate;
	private String mediaLocation;
	private String mediaCollection;
	private String mediaLink;
	private Boolean mediaType;
	private Integer seqNo;

	public Media() {
	}

	public Media(String mediaName, Date mediaDate, String mediaLocation, String mediaCollection, String mediaLink,
			Boolean mediaType, Integer seqNo) {
		this.mediaName = mediaName;
		this.mediaDate = mediaDate;
		this.mediaLocation = mediaLocation;
		this.mediaCollection = mediaCollection;
		this.mediaLink = mediaLink;
		this.mediaType = mediaType;
		this.seqNo = seqNo;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "Media_Id", unique = true, nullable = false)
	public Integer getMediaId() {
		return this.mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	@Column(name = "Media_Name", length = 45)
	public String getMediaName() {
		return this.mediaName;
	}

	public void setMediaName(String mediaName) {
		this.mediaName = mediaName;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "Media_Date", length = 10)
	public Date getMediaDate() {
		return this.mediaDate;
	}

	public void setMediaDate(Date mediaDate) {
		this.mediaDate = mediaDate;
	}

	@Column(name = "Media_Location", length = 45)
	public String getMediaLocation() {
		return this.mediaLocation;
	}

	public void setMediaLocation(String mediaLocation) {
		this.mediaLocation = mediaLocation;
	}

	@Column(name = "Media_Collection", length = 65535)
	public String getMediaCollection() {
		return this.mediaCollection;
	}

	public void setMediaCollection(String mediaCollection) {
		this.mediaCollection = mediaCollection;
	}

	@Column(name = "Media_Link")
	public String getMediaLink() {
		return this.mediaLink;
	}

	public void setMediaLink(String mediaLink) {
		this.mediaLink = mediaLink;
	}

	@Column(name = "Media_Type")
	public Boolean getMediaType() {
		return this.mediaType;
	}

	public void setMediaType(Boolean mediaType) {
		this.mediaType = mediaType;
	}

	@Column(name = "SeqNo")
	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

}
