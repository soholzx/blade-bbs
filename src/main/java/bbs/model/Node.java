package bbs.model;

import java.io.Serializable;
import java.util.List;

import blade.plugin.sql2o.Table;

@Table(value = "bbs_node", PK = "nid")
public class Node implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private Integer nid;
	private Integer pid;
	private Integer featured;
	private Integer topshow;
	private String nname;
	private String nkey;
	private String content;
	private String keywords;
	
	private List<Node> children;

	public Node() {
		// TODO Auto-generated constructor stub
	}

	public Integer getNid() {
		return nid;
	}

	public void setNid(Integer nid) {
		this.nid = nid;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getFeatured() {
		return featured;
	}

	public void setFeatured(Integer featured) {
		this.featured = featured;
	}

	public Integer getTopshow() {
		return topshow;
	}

	public void setTopshow(Integer topshow) {
		this.topshow = topshow;
	}

	public String getNname() {
		return nname;
	}

	public void setNname(String nname) {
		this.nname = nname;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public String getNkey() {
		return nkey;
	}

	public void setNkey(String nkey) {
		this.nkey = nkey;
	}

}
