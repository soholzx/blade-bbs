package bbs.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bbs.model.Comment;
import bbs.service.CommentService;
import blade.annotation.Component;
import blade.plugin.sql2o.Model;

@Component
public class CommentServiceImpl implements CommentService {

	private Model<Comment> model = new Model<Comment>(Comment.class);
	
	@Override
	public Integer save(Integer uid, Integer tid, String content) {
		return model.insert().param("uid", uid)
		.param("tid", tid)
		.param("content", content)
		.param("replytime", new Date()).executeAndCommit();
	}

	@Override
	public List<Map<String, Object>> getComments(Integer tid) {
		
		return model.select("select a.*, b.username, b.avatar from bbs_comment a "
				+ "left join bbs_user b on b.uid=a.uid").eq("tid", tid).orderBy("a.replytime desc").fetchListMap();
	}

}
