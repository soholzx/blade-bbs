package bbs.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import bbs.Constant;
import bbs.model.User;
import bbs.service.UserService;
import blade.annotation.Component;
import blade.kit.BeanKit;
import blade.kit.EncrypKit;
import blade.kit.MailKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;

@Component
public class UserServiceImpl implements UserService {
	
	private Model<User> model = new Model<User>(User.class);
	
	public Long getUserCount(String email){
		return model.count().eq("email", email).fetchCount();
	}
	
	@Override
	public Map<String, Object> getProfile(Integer uid) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(null != uid){
			User user = model.select().eq("uid", uid).fetchOne();
			if(null != user){
				map = BeanKit.beanToMap(user);
			}
		}
		return map;
	}

	@Override
	public User signup(String username, String password, String email) {
		String pwd = EncrypKit.md5(username + password);
		String avatar = new Random().nextInt(5) + 1 + ".png";
		
		Integer uid = model.insert()
		.param("username", username)
		.param("password", pwd)
		.param("email", email)
		.param("regtime", new Date())
		.param("group_id", 3)
		.param("is_active", 0)
		.param("avatar", "static/upload/avatar/default/" + avatar)
		.executeAndCommit();
		
		if(null != uid){
			return model.fetchByPk(uid);
		}
		return null;
	}
	

	@Override
	public User signin(String username, String password) {
		String pwd = EncrypKit.md5(username + password);
		return model.select().eq("username", username).eq("password", pwd).fetchOne();
	}

	@Override
	public User getByUsername(String username) {
		if(StringKit.isNotBlank(username)){
			return model.select().eq("username", username).fetchOne();
		}
		return null;
	}

	@Override
	public boolean updateCount(Integer uid, String type, boolean add) {
		if(null != uid && StringKit.isNotBlank(type)){
			if(type.equals("topic")){
				if(add){
					return model.update("update bbs_user set topic = (topic+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set topic = (topic-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
			if(type.equals("reply")){
				if(add){
					return model.update("update bbs_user set reply = (reply+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set reply = (reply-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
			if(type.equals("notice")){
				if(add){
					return model.update("update bbs_user set notice = (notice+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set notice = (notice-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
			if(type.equals("node_follow")){
				if(add){
					return model.update("update bbs_user set node_follow = (node_follow+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set node_follow = (node_follow-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
			if(type.equals("user_follow")){
				if(add){
					return model.update("update bbs_user set user_follow = (user_follow+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set user_follow = (user_follow-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
			if(type.equals("topic_follow")){
				if(add){
					return model.update("update bbs_user set topic_follow = (topic_follow+1)").eq("uid", uid).executeAndCommit() > 0;
				} else {
					return model.update("update bbs_user set topic_follow = (topic_follow-1)").eq("uid", uid).executeAndCommit() > 0;
				}
			}
		}
		return false;
	}

	@Override
	public void noticeTo0(Integer uid) {
		if(null != uid){
			model.update().set("notice", 0).eq("uid", uid).executeAndCommit();
		}
	}

	@Override
	public boolean isFollow(Integer uid, Integer relate_id, String type) {
		if(null != uid && null != relate_id && StringKit.isNotBlank(type)){
			// 是否收藏主题
			if(type.equals("topic")){
				return model.count("select count(1) from bbs_topic_follow").eq("uid", uid).eq("tid", relate_id).fetchCount() > 0;
			}
			
			// 是否收藏节点
			if(type.equals("node")){
				return model.count("select count(1) from bbs_node_follow").eq("uid", uid).eq("nid", relate_id).fetchCount() > 0;
			}
			
			// 是否关注用户
			if(type.equals("user")){
				return model.count("select count(1) from bbs_user_follow").eq("uid", uid).eq("fuid", relate_id).fetchCount() > 0;
			}
		}
		return false;
	}

	@Override
	public boolean follow(Integer uid, Integer relate_id, String type) {
		if(null != uid && null != relate_id && StringKit.isNotBlank(type)){
			Date addTime = new Date();
			// 收藏主题
			if(type.equals("topic")){
				Long count = model.count("select count(1) from bbs_topic_follow").eq("uid", uid).eq("tid", relate_id).fetchCount();
				if(count == 0){
					return model.insert("insert into bbs_topic_follow").param("uid", uid).param("tid", relate_id).param("addtime", addTime).executeAndCommit() > 0;
				}
			}
			
			// 收藏节点
			if(type.equals("node")){
				Long count = model.count("select count(1) from bbs_node_follow").eq("uid", uid).eq("nid", relate_id).fetchCount();
				if(count == 0){
					return model.insert("insert into bbs_node_follow").param("uid", uid).param("nid", relate_id).param("addtime", addTime).executeAndCommit() > 0;
				}
			}
			
			// 关注用户
			if(type.equals("user")){
				Long count = model.count("select count(1) from bbs_user_follow").eq("uid", uid).eq("fuid", relate_id).fetchCount();
				if(count == 0){
					return model.insert("insert into bbs_node_follow").param("uid", uid).param("fuid", relate_id).param("addtime", addTime).executeAndCommit() > 0;
				}
			}
		}
		return false;
	}

	@Override
	public boolean unfollow(Integer uid, Integer relate_id, String type) {
		if(null != uid && null != relate_id && StringKit.isNotBlank(type)){
			// 取消收藏主题
			if(type.equals("topic")){
				return model.delete("delete from bbs_topic_follow").eq("uid", uid).eq("tid", relate_id).executeAndCommit() > 0;
			}
			
			// 取消收藏节点
			if(type.equals("node")){
				return model.delete("delete from bbs_node_follow").eq("uid", uid).eq("nid", relate_id).executeAndCommit() > 0;
			}
			
			// 取消关注用户
			if(type.equals("user")){
				return model.delete("delete from bbs_user_follow").eq("uid", uid).eq("fuid", relate_id).executeAndCommit() > 0;
			}
		}
		return false;
	}

	@Override
	public User updateInfo(Integer uid, String email, String qq,
			String location, String homepage, String signature,
			String introduction) {
		
		if(null == uid){
			return null;
		}
		
		model.update().set("email", email)
		.set("qq", qq)
		.set("location", location)
		.set("homepage", homepage)
		.set("signature", signature)
		.set("introduction", introduction).eq("uid", uid).executeAndCommit();
		
		return model.fetchByPk(uid);
	}

	@Override
	public User getByEmail(String email) {
		return model.select().eq("email", email).fetchOne();
	}

	@Override
	public User updateAvatar(Integer uid, String avatar) {
		if(null == uid){
			return null;
		}
		
		model.update().set("avatar", avatar).eq("uid", uid).executeAndCommit();
		
		return model.fetchByPk(uid);
	}

	@Override
	public User getByUID(Integer uid) {
		return model.fetchByPk(uid);
	}

	@Override
	public Page<Map<String, Object>> getUsers(WhereParam whereParam, Integer page, Integer pageSize) {
		return model.where(whereParam).fetchPageMap(page, pageSize);
	}

	@Override
	public void updateUserName(Integer uid, String username, String email) {
		// 修改用户名，发送新密码到邮箱
		String pwd = StringKit.random(10);
		String newPwd = EncrypKit.md5(username + pwd);
		model.update().param("username", username).param("password", newPwd).eq("uid", uid).executeAndCommit();
		MailKit.asynSend(email, Constant.MAIL_ACTIVE_TITLE, String.format(Constant.MAIL_NEWUSER_CONTENT, username, pwd));
	}
	
}
