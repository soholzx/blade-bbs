package bbs.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bbs.Constant;
import bbs.model.Topic;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;

@Component
public class TopicServiceImpl implements TopicService {
	
	private Model<Topic> model = new Model<Topic>(Topic.class);

	@Inject
	private UserService userService;
	
	@Override
	public Page<Map<String, Object>> getTopicRecent(WhereParam whereParam, Integer page, Integer pageSize, String order) {
		if(StringKit.isBlank(order)){
			order = "a.replytime desc";
		}
		
		boolean iscache = false;
		if(null != whereParam && null == whereParam.greaterThan("a.addtime") && model.isOpenCache()){
			iscache = true;
		}
		
		Page<Map<String, Object>> topicPage = 
				model.select("select a.*, b.username, b.avatar, c.username as rname, d.nkey, d.nname "
						+ "from bbs_topic a left join bbs_user b on b.uid = a.uid "
						+ "left join bbs_user c on c.uid = a.replyuid "
						+ "left join bbs_node d on d.nid = a.nid")
						.where(whereParam)
						.orderBy(order)
						.cache(iscache)
						.fetchPageMap(page, pageSize);
		
		return topicPage;
	}
	
	@Override
	public Integer save(Integer uid, Integer nid, String title, String content) {
		Date date = new Date();
		return model.insert()
		.param("uid", uid)
		.param("nid", nid)
		.param("title", title)
		.param("content", content)
		.param("addtime", date)
		.param("replytime", date)
		.param("status", Constant.SYSINFO.get("site_topic_status"))
		.executeAndCommit();
	}

	@Override
	public Topic get(Integer tid) {
		return model.fetchByPk(tid);
	}

	@Override
	public Map<String, Object> getMap(Integer tid) {
		return model.select("select a.*, b.username, b.avatar, c.username as rname, d.nkey, d.nname "
				+ "from bbs_topic a left join bbs_user b on b.uid = a.uid "
				+ "left join bbs_user c on c.uid = a.replyuid "
				+ "left join bbs_node d on d.nid = a.nid").eq("a.tid", tid).fetchMap();
	}

	@Override
	public void updateView(Integer tid) {
		if(null != tid){
			model.update("update bbs_topic set view = (view+1)").eq("tid", tid).executeAndCommit();
		}
	}

	@Override
	public Integer updateReply(Integer tid, Integer replyuid) {
		if(null != tid && null != replyuid){
			model.update("update bbs_topic set comment = (comment+1)")
			.set("replyuid", replyuid)
			.set("replytime", new Date())
			.eq("tid", tid).executeAndCommit();
			return model.fetchByPk(tid).getComment();
		}
		return 0;
	}

	@Override
	public List<Integer> followTopic(Integer uid) {
		return model.select("select tid from bbs_topic_follow").eq("uid", uid).executeAndFetch(Integer.class);
	}

	@Override
	public void update(Integer tid, Integer nid, String title, String content) {
		model.update().param("nid", nid).param("title", title).param("content", content)
		.eq("tid", tid).executeAndCommit();
	}

	@Override
	public void delete(Integer tid) {
		Topic topic = model.fetchByPk(tid);
		if(null != topic){
			Integer uid = topic.getUid();
			userService.updateCount(uid, "topic", false);
			model.delete().eq("tid", tid);
			model.delete("delete from bbs_topic_follow").eq("tid", tid);
		}
	}
	
}
