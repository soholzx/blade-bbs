package bbs.model;

import java.io.Serializable;
import java.util.Date;

import blade.plugin.sql2o.Table;

@Table(value = "bbs_notice", PK = "nid")
public class Notice implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private Integer nid;
	private Integer cid;
	private Integer tid;
	private Integer fuid;
	private Integer tuid;
	private Integer ntype;
	private Date addtime;

	public Notice() {
		// TODO Auto-generated constructor stub
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
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

	public Integer getFuid() {
		return fuid;
	}

	public void setFuid(Integer fuid) {
		this.fuid = fuid;
	}

	public Integer getTuid() {
		return tuid;
	}

	public void setTuid(Integer tuid) {
		this.tuid = tuid;
	}

	public Integer getNtype() {
		return ntype;
	}

	public void setNtype(Integer ntype) {
		this.ntype = ntype;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
}
