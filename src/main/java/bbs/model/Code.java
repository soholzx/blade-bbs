package bbs.model;

import java.io.Serializable;

import blade.plugin.sql2o.Table;

@Table(value = "bbs_code")
public class Code implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private Integer id;
	private Integer uid;
	private String code;
	private Integer status;

	public Code() {
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

}
