package com.inmaa.admin.persistence;

// Generated 7 nov. 2015 10:51:41 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Authorities generated by hbm2java
 */
@Entity
@Table(name = "Authorities", catalog = "inmaa")
public class Authorities implements java.io.Serializable {

	private String id;
	private String authorityname;
	private Set<Users> userses = new HashSet<Users>(0);

	public Authorities() {
	}

	public Authorities(String id) {
		this.id = id;
	}

	public Authorities(String id, String authorityname, Set<Users> userses) {
		this.id = id;
		this.authorityname = authorityname;
		this.userses = userses;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "authorityname")
	public String getAuthorityname() {
		return this.authorityname;
	}

	public void setAuthorityname(String authorityname) {
		this.authorityname = authorityname;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Users_Authorities", catalog = "inmaa", joinColumns = { @JoinColumn(name = "authorities_id", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "user_id", nullable = false, updatable = false) })
	public Set<Users> getUserses() {
		return this.userses;
	}

	public void setUserses(Set<Users> userses) {
		this.userses = userses;
	}

}