package bbs.model;

import java.io.Serializable;
import java.util.Date;

import blade.plugin.sql2o.Table;

@Table(value = "bbs_comment", PK = "cid")
public class Comment implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private Integer cid;
	private Integer tid;
	private Integer uid;
	private Date replytime;
	private String content;

	public Comment() {
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Date getReplytime() {
		return replytime;
	}

	public void setReplytime(Date replytime) {
		this.replytime = replytime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
