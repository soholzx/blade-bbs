package bbs.model;

import java.io.Serializable;
import java.util.Date;

import blade.plugin.sql2o.Table;

@Table(value="bbs_user", PK = "uid")
public class User implements Serializable{

	private static final long serialVersionUID = -7718091985540738974L;
	
	private Integer uid;
	private String username;
	private String password;
	private String email;
	private String avatar;
	private String homepage;
	private String money;
	private String qq;
	private String location;
	private String signature;
	private String introduction;
	private Integer topic;
	private Integer reply;
	private Integer notice;
	private Integer node_follow;
	private Integer user_follow;
	private Integer topic_follow;
	private Date regtime;
	private Date lastlogin;
	private Date lastpost;
	private Integer group_id;
	private Integer is_active;
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public Integer getTopic() {
		return topic;
	}

	public void setTopic(Integer topic) {
		this.topic = topic;
	}

	public Integer getReply() {
		return reply;
	}

	public void setReply(Integer reply) {
		this.reply = reply;
	}

	public Integer getNotice() {
		return notice;
	}

	public void setNotice(Integer notice) {
		this.notice = notice;
	}

	public Integer getNode_follow() {
		return node_follow;
	}

	public void setNode_follow(Integer node_follow) {
		this.node_follow = node_follow;
	}

	public Integer getUser_follow() {
		return user_follow;
	}

	public void setUser_follow(Integer user_follow) {
		this.user_follow = user_follow;
	}

	public Integer getTopic_follow() {
		return topic_follow;
	}

	public void setTopic_follow(Integer topic_follow) {
		this.topic_follow = topic_follow;
	}

	public Date getRegtime() {
		return regtime;
	}

	public void setRegtime(Date regtime) {
		this.regtime = regtime;
	}

	public Date getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}

	public Date getLastpost() {
		return lastpost;
	}

	public void setLastpost(Date lastpost) {
		this.lastpost = lastpost;
	}

	public Integer getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}

	public Integer getIs_active() {
		return is_active;
	}

	public void setIs_active(Integer is_active) {
		this.is_active = is_active;
	}
	
}
