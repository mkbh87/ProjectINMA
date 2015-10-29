package com.inmaa.admin.persistence;

// Generated 29 oct. 2015 21:42:46 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Config generated by hbm2java
 */
@Entity
@Table(name = "config", catalog = "inmaa")
public class Config implements java.io.Serializable {

	private Integer configId;
	private String configImgpath;

	public Config() {
	}

	public Config(String configImgpath) {
		this.configImgpath = configImgpath;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "Config_Id", unique = true, nullable = false)
	public Integer getConfigId() {
		return this.configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	@Column(name = "config_Imgpath", nullable = false)
	public String getConfigImgpath() {
		return this.configImgpath;
	}

	public void setConfigImgpath(String configImgpath) {
		this.configImgpath = configImgpath;
	}

}
