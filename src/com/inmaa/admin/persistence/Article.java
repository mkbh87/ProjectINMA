package com.inmaa.admin.persistence;
// Generated 1 janv. 2016 18:44:24 by Hibernate Tools 4.3.1.Final

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
 * Article generated by hbm2java
 */
@Entity
@Table(name = "Article", catalog = "inmaa", uniqueConstraints = @UniqueConstraint(columnNames = "Article_Title") )
public class Article implements java.io.Serializable {

	private Integer articleId;
	private String articleDesc;
	private String articleDesc1;
	private String articleTitle;
	private String articleSource;
	private Integer seqNo;
	private String articlePicture;
	private String articleAuthor;
	private Date articleDate;
	private Set<Project> projects = new HashSet<Project>(0);
	private Set<Event> events = new HashSet<Event>(0);

	public Article() {
	}

	public Article(String articleDesc, String articleTitle, String articlePicture, String articleAuthor,
			Date articleDate) {
		this.articleDesc = articleDesc;
		this.articleTitle = articleTitle;
		this.articlePicture = articlePicture;
		this.articleAuthor = articleAuthor;
		this.articleDate = articleDate;
	}

	public Article(String articleDesc, String articleDesc1, String articleTitle, String articleSource, Integer seqNo,
			String articlePicture, String articleAuthor, Date articleDate, Set<Project> projects, Set<Event> events) {
		this.articleDesc = articleDesc;
		this.articleDesc1 = articleDesc1;
		this.articleTitle = articleTitle;
		this.articleSource = articleSource;
		this.seqNo = seqNo;
		this.articlePicture = articlePicture;
		this.articleAuthor = articleAuthor;
		this.articleDate = articleDate;
		this.projects = projects;
		this.events = events;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "Article_ID", unique = true, nullable = false)
	public Integer getArticleId() {
		return this.articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	@Column(name = "Article_Desc", nullable = false, length = 65535)
	public String getArticleDesc() {
		return this.articleDesc;
	}

	public void setArticleDesc(String articleDesc) {
		this.articleDesc = articleDesc;
	}

	@Column(name = "Article_Desc1")
	public String getArticleDesc1() {
		return this.articleDesc1;
	}

	public void setArticleDesc1(String articleDesc1) {
		this.articleDesc1 = articleDesc1;
	}

	@Column(name = "Article_Title", unique = true, nullable = false)
	public String getArticleTitle() {
		return this.articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

	@Column(name = "Article_Source")
	public String getArticleSource() {
		return this.articleSource;
	}

	public void setArticleSource(String articleSource) {
		this.articleSource = articleSource;
	}

	@Column(name = "SeqNo")
	public Integer getSeqNo() {
		return this.seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	@Column(name = "Article_Picture", nullable = false, length = 90)
	public String getArticlePicture() {
		return this.articlePicture;
	}

	public void setArticlePicture(String articlePicture) {
		this.articlePicture = articlePicture;
	}

	@Column(name = "Article_Author", nullable = false, length = 90)
	public String getArticleAuthor() {
		return this.articleAuthor;
	}

	public void setArticleAuthor(String articleAuthor) {
		this.articleAuthor = articleAuthor;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "Article_Date", nullable = false, length = 10)
	public Date getArticleDate() {
		return this.articleDate;
	}

	public void setArticleDate(Date articleDate) {
		this.articleDate = articleDate;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Article_Project", catalog = "inmaa", joinColumns = {
			@JoinColumn(name = "Article_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Projects_ID", nullable = false, updatable = false) })
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Article_Event", catalog = "inmaa", joinColumns = {
			@JoinColumn(name = "Article_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "Event_ID", nullable = false, updatable = false) })
	public Set<Event> getEvents() {
		return this.events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

}
