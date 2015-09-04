package bbs.model;

import java.io.Serializable;
import java.util.Date;

import blade.plugin.sql2o.Table;

/**
 * 记录签到
 *
 */
@Table(value = "bbs_clock")
public class Clock implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private Integer id;
	private Integer uid;
	private Integer count;
	private Date addtime;

	public Clock() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

}
