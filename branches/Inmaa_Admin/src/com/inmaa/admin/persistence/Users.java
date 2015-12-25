package com.inmaa.admin.persistence;
// Generated 25 déc. 2015 16:35:57 by Hibernate Tools 4.3.1.Final

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
 * Users generated by hbm2java
 */
@Entity
@Table(name = "Users", catalog = "inmaa")
public class Users implements java.io.Serializable {

	private String id;
	private Boolean enabled;
	private String password;
	private String username;
	private Set<Authorities> authoritieses = new HashSet<Authorities>(0);

	public Users() {
	}

	public Users(String id) {
		this.id = id;
	}

	public Users(String id, Boolean enabled, String password, String username, Set<Authorities> authoritieses) {
		this.id = id;
		this.enabled = enabled;
		this.password = password;
		this.username = username;
		this.authoritieses = authoritieses;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(name = "enabled")
	public Boolean getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "password")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "username")
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Users_Authorities", catalog = "inmaa", joinColumns = {
			@JoinColumn(name = "user_id", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "authorities_id", nullable = false, updatable = false) })
	public Set<Authorities> getAuthoritieses() {
		return this.authoritieses;
	}

	public void setAuthoritieses(Set<Authorities> authoritieses) {
		this.authoritieses = authoritieses;
	}

}
