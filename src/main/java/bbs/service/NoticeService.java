package bbs.service;

import java.util.Map;

import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;


public interface NoticeService {
	
	public boolean save(Integer cid, Integer tid, Integer fuid, Integer tuid);
	
	public Page<Map<String, Object>> getNotices(WhereParam whereParam, Integer page, Integer pagesize);
}