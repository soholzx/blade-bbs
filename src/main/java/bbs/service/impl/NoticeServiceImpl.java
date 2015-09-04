package bbs.service.impl;

import java.util.Date;
import java.util.Map;

import bbs.model.Notice;
import bbs.service.NoticeService;
import blade.annotation.Component;
import blade.plugin.sql2o.Model;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;

@Component
public class NoticeServiceImpl implements NoticeService {

	private Model<Notice> model = new Model<Notice>(Notice.class);
	
	/**
	 * 
	 * 保存一条通知
	 * @param cid		评论ID
	 * @param tid		主题ID
	 * @param fuid		发送通知人UID
	 * @param tuid		接收通知人UID
	 * @return
	 */
	@Override
	public boolean save(Integer cid, Integer tid, Integer fuid, Integer tuid) {
		
		return model.insert().param("cid", cid)
		.param("tid", tid)
		.param("fuid", fuid)
		.param("tuid", tuid)
		.param("addtime", new Date())
		.param("ntype", 0).executeAndCommit() > 0;
		
	}

	@Override
	public Page<Map<String, Object>> getNotices(WhereParam whereParam,
			Integer page, Integer pagesize) {
		
		Page<Map<String, Object>> noticePage = 
				model.select("SELECT a.*, b.username as from_username, c.tid as topicid, c.title as topictitle, d.content as comment "
						+ "from bbs_notice a "
						+ "left join bbs_user b on b.uid = a.fuid "
						+ "left join bbs_topic c on c.tid = a.tid "
						+ "left join bbs_comment d on d.cid = a.cid")
						.where(whereParam)
						.orderBy("a.addtime desc")
						.fetchPageMap(page, pagesize);
		
		return noticePage;
	}

}
