package bbs.route.admin;

import java.util.HashMap;
import java.util.Map;

import bbs.model.User;
import bbs.route.RouteBase;
import bbs.service.CommentService;
import bbs.service.NodeService;
import bbs.service.OptionService;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.StringKit;
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
		Blade.get("/admin/topic/edit/:tid", (request, response) -> {
			User user = adminUser();
			if(null == user){
				response.go("/");
				return null;
			}
			
			Integer tid = request.queryToInt("tid");
			Map<String, Object> topic = topicService.getMap(tid);
			ModelAndView modelAndView = this.getAdminModelAndView("topic_edit");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			modelAndView.add("topic", topic);
			return modelAndView;
		});
		
		Blade.post("/admin/topic/edit", (request, response) -> {
			User user = adminUser();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getFrontModelAndView("topic_edit");
			modelAndView.add("nodes", nodeService.getNodes(null, null));
			String title = request.query("title");
			String content = request.query("content");
			Integer tid = request.queryToInt("tid");
			Integer nid = request.queryToInt("nid");
			
			Map<String, Object> topic = new HashMap<String, Object>();
			topic.put("tid", tid);
			topic.put("title", title);
			topic.put("nid", nid);
			topic.put("content", content);
			
			modelAndView.add("topic", topic);
			
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
			topicService.update(tid, nid, title, content);
			
			response.go("/topic/" + tid);
			return null;
		});
		
		Blade.post("/admin/topic/delete/:tid", (request, response) -> {
			User user = adminUser();
			if(null == user){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getAdminModelAndView("topic");
			if(user.getGroup_id() != 1){
				modelAndView.add(ERROR, "您没有权限删除");
				return modelAndView;
			}
			
			Integer tid = request.queryToInt("tid");
			topicService.delete(tid);
			
			// 更新网站统计信息 主题
			Integer topic_number = Integer.valueOf(optionService.getOption("site_topic_number"));
			optionService.update("site_topic_number", topic_number - 1);
						
			return modelAndView;
		});
		
	}
}
