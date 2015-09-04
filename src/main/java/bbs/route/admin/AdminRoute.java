package bbs.route.admin;

import java.util.Map;

import bbs.model.User;
import bbs.route.RouteBase;
import bbs.service.NodeService;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;

/**
 * 后台
 * @author biezhi
 *
 */
public class AdminRoute implements RouteBase {
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicService topicService;
	
	@Override
	public void run() {
		
		Blade.get("/admin", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = this.getAdminModelAndView("topic");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(null, page, 20, "addtime desc");
			modelAndView.add("topicPage", topicPage);
			return modelAndView;
		});
		
		Blade.get("/admin/topic", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = this.getAdminModelAndView("topic");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(null, page, 20, "addtime desc");
			modelAndView.add("topicPage", topicPage);
			return modelAndView;
		});
		
		Blade.get("/admin/topic/search", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = this.getAdminModelAndView("topic_verify");
			String title = request.query("title");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			WhereParam where = WhereParam.me().eq("a.title", "%"+ title +"%");
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(where, page, 20, "addtime desc");
			modelAndView.add("topicPage", topicPage);
			return modelAndView;
		});
		
		Blade.get("/admin/topic/verify", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = this.getAdminModelAndView("topic_verify");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			WhereParam where = WhereParam.me().eq("a.status", 0);
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(where, page, 20, "addtime desc");
			modelAndView.add("topicPage", topicPage);
			return modelAndView;
		});
		
	}
	
	
}
