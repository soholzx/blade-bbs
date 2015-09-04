package bbs.service;

import java.util.List;
import java.util.Map;


public interface CommentService {
	
	public Integer save(Integer uid, Integer tid, String content);

	public List<Map<String, Object>> getComments(Integer tid);
	
}