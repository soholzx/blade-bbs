package bbs.route;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbs.Constant;
import bbs.model.User;
import bbs.service.CommentService;
import bbs.service.NoticeService;
import bbs.service.OptionService;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;

// 回复帖子
public class CommentRoute implements RouteBase {
	
	@Inject
	private CommentService commentService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private OptionService optionService;
	
	@Inject
	private NoticeService noticeService;
	
	@Inject
	private UserService userService;
	
	@Override
	public void run() {
		
		Blade.post("/comment/add", (request, response)->{
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getFrontModelAndView("topic_detail");
			Integer tid = request.queryToInt("tid");
			Integer tuid = request.queryToInt("tuid");
			String content = request.query("content");
			
			if(null == tid || null == tuid){
				modelAndView.add(ERROR, "无效的回复！");
				return modelAndView;
			}
			
			if(StringKit.isBlank(content) || content.length() < 2){
				modelAndView.add(ERROR, "回复内容长度必须大于2个字符");
				return modelAndView;
			}
			
			if(content.length() > 1000){
				modelAndView.add(ERROR, "回复长度超过限制");
				return modelAndView;
			}
			
			// 保存回复
			Integer cid = commentService.save(user.getUid(), tid, content);
			
			// 更新用户回复数量
			userService.updateCount(user.getUid(), "reply", true);
			
			//@功能的发送回复提醒
			String pattern = "@([a-z0-9]{3,12})\\s";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(content);
			while(m.find()){
				String username = m.group(1);
				User senduser = userService.getByUsername(username);
				if(null != senduser){
					//写入notice表 更新通知
					noticeService.save(cid, tid, user.getUid(), senduser.getUid());
					//更新senduser的未读提醒数
					userService.updateCount(senduser.getUid(), "notice", true);
				}
			}
			
			//更新帖子信息
			Integer comment = topicService.updateReply(tid, user.getUid());
			
			//向主题发布者发送提醒
			noticeService.save(cid, tid, user.getUid(), tuid);
			userService.updateCount(tuid, "notice", true);
			
			// 更新网站回复数
			Integer comment_number = Integer.valueOf(optionService.getOption("site_comment_number")) + 1;
			optionService.update("site_comment_number", comment_number);
			
			// 跳转到原帖子
			response.go("/topic/" + tid + "#Reply" + comment);
			return null;
		});
		
		Blade.get("/notice", (request, response)->{
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getFrontModelAndView("notice");
			
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			
			
			WhereParam whereParam = WhereParam.me();
			whereParam.eq("a.tuid", user.getUid());
			Page<Map<String, Object>> notices = noticeService.getNotices(whereParam, page, 15);
			
			modelAndView.add("notices", notices);
			modelAndView.add("hot_topics", hot_topics);
			
			// 通知数清零
			userService.noticeTo0(user.getUid());
			
			request.session().attribute(Constant.LOGIN_SESSION, userService.getByUID(user.getUid()));
			
			return modelAndView;
		});
	}
}
