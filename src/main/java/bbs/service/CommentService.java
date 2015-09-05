package bbs.service;

import java.util.List;
import java.util.Map;

import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;


public interface CommentService {
	
	public Integer save(Integer uid, Integer tid, String content);

	public List<Map<String, Object>> getComments(Integer tid);
	
	public Page<Map<String, Object>> getComments(WhereParam whereParam, Integer page, Integer pageSize);
	
}