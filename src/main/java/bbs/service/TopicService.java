package bbs.service;

import java.util.List;
import java.util.Map;

import bbs.model.Topic;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;

public interface TopicService {
	
	public Topic get(Integer tid);
	
	public List<Integer> followTopic(Integer uid);
	
	public Map<String, Object> getMap(Integer tid);
	
	public Page<Map<String, Object>> getTopicRecent(WhereParam whereParam, Integer page, Integer pageSize, String order);
	
	public Integer save(Integer uid, Integer nid, String title, String content);

	public void updateView(Integer tid);

	public Integer updateReply(Integer tid, Integer replyuid);

	public void update(Integer tid, Integer nid, String title, String content);

	public void delete(Integer tid);
	
}