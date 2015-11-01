package com.inmaa.admin.persistence;

// Generated 1 nov. 2015 20:22:35 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Event generated by hbm2java
 */
@Entity
@Table(name = "Event", catalog = "inmaa", uniqueConstraints = @UniqueConstraint(columnNames = "Event_Name"))
public class Event implements java.io.Serializable {

	private Integer eventId;
	private Integer seqNo;
	private String eventName;
	private String eventDesc;
	private Date eventStartDate;
	private Date eventEndDate;
	private String eventLogo;
	private String eventLocation;
	private String eventPresenter;
	private Set<Project> projects = new HashSet<Project>(0);

	public Event() {
	}

	public Event(String eventName, String eventDesc, Date eventStartDate,
			Date eventEndDate, String eventLogo, String eventLocation) {
		this.eventName = eventName;
		this.eventDesc = eventDesc;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.eventLogo = eventLogo;
		this.eventLocation = eventLocation;
	}

	public Event(Integer seqNo, String eventName, String eventDesc,
			Date eventStartDate, Date eventEndDate, String eventLogo,
			String eventLocation, String eventPresenter, Set<Project> projects) {
		this.seqNo = seqNo;
		this.eventName = eventName;
		this.eventDesc = eventDesc;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.eventLogo = eventLogo;
		this.eventLocation = eventLocation;
		this.eventPresenter = eventPresenter;
		this.projects = projects;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Event_ID", unique = true, nullable = false)
	public Integer getEventId() {
		return this.eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	@Column(name = "SeqNo")
	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	@Column(name = "Event_Name", unique = true, nullable = false)
	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	@Column(name = "Event_Desc", nullable = false)
	public String getEventDesc() {
		return this.eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Event_StartDate", nullable = false, length = 19)
	public Date getEventStartDate() {
		return this.eventStartDate;
	}

	public void setEventStartDate(Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Event_EndDate", nullable = false, length = 19)
	public Date getEventEndDate() {
		return this.eventEndDate;
	}

	public void setEventEndDate(Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	@Column(name = "Event_Logo", nullable = false)
	public String getEventLogo() {
		return this.eventLogo;
	}

	public void setEventLogo(String eventLogo) {
		this.eventLogo = eventLogo;
	}

	@Column(name = "Event_Location", nullable = false, length = 45)
	public String getEventLocation() {
		return this.eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	@Column(name = "Event_Presenter", length = 45)
	public String getEventPresenter() {
		return this.eventPresenter;
	}

	public void setEventPresenter(String eventPresenter) {
		this.eventPresenter = eventPresenter;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Event_Project", catalog = "inmaa", joinColumns = { @JoinColumn(name = "Event_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "Project_ID", nullable = false, updatable = false) })
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

}
