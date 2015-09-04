package bbs.route;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bbs.Constant;
import bbs.Funcs;
import bbs.model.Node;
import bbs.model.Topic;
import bbs.model.User;
import bbs.service.CommentService;
import bbs.service.NodeService;
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

// 主页操作
public class TopicRoute implements RouteBase{
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private OptionService optionService;
	
	@Inject
	private CommentService commentService;
	
	@Override
	public void run() {
		Blade.get("/topic/add", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("topic_add");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			return modelAndView;
		});
		
		Blade.post("/topic/add", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getFrontModelAndView("topic_add");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			String title = request.query("title");
			String content = request.query("content");
			Integer nid = request.queryToInt("nid");
			
			modelAndView.add("title", title);
			modelAndView.add("nid", nid);
			modelAndView.add("content", content);
			if(StringKit.isBlank(title)){
				modelAndView.add(ERROR, "标题不能为空");
				return modelAndView;
			}
			if(null == nid){
				modelAndView.add(ERROR, "请选择一个节点");
				return modelAndView;
			}
			if(StringKit.isBlank(content) || content.length() < 5){
				modelAndView.add(ERROR, "内容长度必须大于5个字符");
				return modelAndView;
			}
			
			if(content.length() > 10000){
				modelAndView.add(ERROR, "内容长度超过限制");
				return modelAndView;
			}
			
			// 保存帖子
			Integer tid = topicService.save(user.getUid(), nid, title, content);
			
			// 更新用户发表帖子数量
			userService.updateCount(user.getUid(), "topic", true);
			
			// 更新网站统计信息 主题
			Integer topic_number = Integer.valueOf(optionService.getOption("site_topic_number"));
			optionService.update("site_topic_number", topic_number + 1);
			
			response.go("/topic/" + tid);
			return null;
		});
		
		Blade.get("/topic/edit/:tid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			Integer tid = request.pathParamToInt("tid");
			Map<String, Object> topicMap = topicService.getMap(tid);
			ModelAndView modelAndView = this.getFrontModelAndView("topic_edit");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			modelAndView.add("topic", topicMap);
			return null;
		});
		
		Blade.get("/topic/edit/:tid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			Integer tid = request.pathParamToInt("tid");
			Map<String, Object> topicMap = topicService.getMap(tid);
			ModelAndView modelAndView = this.getFrontModelAndView("topic_edit");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			modelAndView.add("topic", topicMap);
			return modelAndView;
		});
		
		Blade.post("/topic/edit/:tid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			Integer tid = request.pathParamToInt("tid");
			Map<String, Object> topicMap = topicService.getMap(tid);
			ModelAndView modelAndView = this.getFrontModelAndView("topic_edit");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			modelAndView.add("topic", topicMap);
			
			String title = request.query("title");
			String content = request.query("content");
			Integer nid = request.queryToInt("nid");
			
			modelAndView.add("title", title);
			modelAndView.add("nid", nid);
			modelAndView.add("content", content);
			if(StringKit.isBlank(title)){
				modelAndView.add(ERROR, "标题不能为空");
				return modelAndView;
			}
			if(null == nid){
				modelAndView.add(ERROR, "请选择一个节点");
				return modelAndView;
			}
			if(StringKit.isBlank(content) || content.length() < 5){
				modelAndView.add(ERROR, "内容长度必须大于5个字符");
				return modelAndView;
			}
			
			if(content.length() > 10000){
				modelAndView.add(ERROR, "内容长度超过限制");
				return modelAndView;
			}
			
			// 修改帖子
			topicService.update(tid, nid, title, content);
			
			response.go("/topic/" + tid);
			
			return null;
		});
		
		Blade.get("/topic/:tid", (request, response) -> {
			Integer tid = request.pathParamToInt("tid");
			if(null == tid){
				response.go("/");
				return null;
			}
			
			Topic topic = topicService.get(tid);
			if(null == topic){
				response.go("/");
				return null;
			}
			
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			
			
			ModelAndView modelAndView = this.getFrontModelAndView("topic_detail");
			
			Map<String, Object> topicMap = topicService.getMap(tid);
			modelAndView.add("topic", topicMap);
			modelAndView.add("hot_topics", hot_topics);
			
			// 评论列表
			List<Map<String, Object>> comments = commentService.getComments(tid);
			modelAndView.add("comments", comments);
			
			// 刷新浏览数
			String count = request.cookie(Constant.VIEWCOUNT_FIELD);
			if(StringKit.isBlank(count)){
				response.cookie(Constant.VIEWCOUNT_FIELD, "1", 3600);
				topicService.updateView(tid);
			}
			
			Integer uid = null;
			User user = request.session().attribute(Constant.LOGIN_SESSION);
			if(null != user){
				uid = user.getUid();
			}
			// 收藏状态
			boolean isfollow = userService.isFollow(uid, tid, "topic");
			modelAndView.add("isfollow", isfollow);
			return modelAndView;
		});
		
		Blade.get("/topic/follow/:tid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				String path = Funcs.base_url("/signin");
				response.html("<script>alert('请登录后进行收藏！');location.href='"+ path +"';</script>");
				return null;
			}
			Integer tid = request.pathParamToInt("tid");
			if(null != tid){
				userService.follow(user.getUid(), tid, "topic");
			}
			// 更新用户收藏帖子数
			userService.updateCount(user.getUid(), "topic_follow", true);
			request.session().attribute(Constant.LOGIN_SESSION, userService.getByUID(user.getUid()));
			response.go("/topic/" + tid);
			return null;
		});
		
		Blade.get("/topic/unfollow/:tid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			Integer tid = request.pathParamToInt("tid");
			if(null != tid){
				userService.unfollow(user.getUid(), tid, "topic");
			}
			// 更新用户收藏帖子数
			userService.updateCount(user.getUid(), "topic_follow", false);
			request.session().attribute(Constant.LOGIN_SESSION, userService.getByUID(user.getUid()));
			response.go("/topic/" + tid);
			return null;
		});
		
		Blade.get("/follow/topics", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = getFrontModelAndView("home");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			List<Node> nodes = nodeService.getNodes(1, null);
			List<Node> tnodes = nodeService.getNomalNodes(null, 1);
			
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			
			List<Integer> follows = topicService.followTopic(user.getUid());
			if(null != follows){

				List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
				
				WhereParam pagewhere = WhereParam.me().eq("a.status", 1);
				pagewhere.in("a.tid", follows.toArray(new Integer[follows.size()]));
				
				Page<Map<String, Object>> topicPage = topicService.getTopicRecent(pagewhere, page, 20, null);
				
				modelAndView.add("nodes", nodes);
				modelAndView.add("tnodes", tnodes);
				modelAndView.add("hot_topics", hot_topics);
				modelAndView.add("topicPage", topicPage);
			}
			return modelAndView;
		});
	}
}
