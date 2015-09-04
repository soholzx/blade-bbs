package bbs.model;

import java.io.Serializable;
import java.util.Date;

import blade.plugin.sql2o.Table;

@Table(value="bbs_topic", PK = "tid")
public class Topic implements Serializable{

	private static final long serialVersionUID = -7718091985540738974L;
	
	private Integer tid;
	private Integer nid;
	private Integer uid;
	private String title;
	private String content;
	private Date addtime;
	private Integer replyuid;
	private Date replytime;
	private Integer view;
	private Integer comment;
	private Integer status;
	
	public Topic() {
		// TODO Auto-generated constructor stub
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Integer getReplyuid() {
		return replyuid;
	}

	public void setReplyuid(Integer replyuid) {
		this.replyuid = replyuid;
	}

	public Date getReplytime() {
		return replytime;
	}

	public void setReplytime(Date replytime) {
		this.replytime = replytime;
	}

	public Integer getView() {
		return view;
	}

	public void setView(Integer view) {
		this.view = view;
	}

	public Integer getComment() {
		return comment;
	}

	public void setComment(Integer comment) {
		this.comment = comment;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
