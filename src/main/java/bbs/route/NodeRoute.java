package bbs.route;

import java.util.List;
import java.util.Map;

import bbs.model.Node;
import bbs.model.User;
import bbs.service.NodeService;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.StringKit;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;


public class NodeRoute implements RouteBase {

	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private UserService userService;
	
	@Override
	public void run() {
		
		Blade.get("/node", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("node");
			List<Node> nodes = nodeService.getNodes(null, null);		
			modelAndView.add("nodes", nodes);
			return modelAndView;
		});
		
		Blade.get("/go/:nkey", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("node_topic");
			String nkey = request.pathParam("nkey");
			if(StringKit.isBlank(nkey)){
				response.go("/");
				return null;
			}
			
			Node node = nodeService.getNodeByNKey(nkey);
			if(null == node){
				response.go("/");
				return null;
			}
			
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			WhereParam where = WhereParam.me().eq("a.nid", node.getNid()).eq("a.status", 1);
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(where, page, 15, null);
			modelAndView.add("topicPage", topicPage);
			modelAndView.add("node", node);
			// 计算收藏状态
			return modelAndView;
		});
		
		// 收藏节点
		Blade.get("/node/follow/:nkey", (request, response)->{
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			String nkey = request.pathParam("nkey");
			if(StringKit.isBlank(nkey)){
				return null;
			}
			Node node = nodeService.getNodeByNKey(nkey);
			if(null != node){
				userService.follow(user.getUid(), node.getNid(), "node");
			}
			response.go("/go/" + nkey);
			return null;
		});
		
		// 取消收藏节点
		Blade.get("/node/follow/:nkey", (request, response)->{
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			String nkey = request.pathParam("nkey");
			if(StringKit.isBlank(nkey)){
				return null;
			}
			Node node = nodeService.getNodeByNKey(nkey);
			if(null != node){
				userService.unfollow(user.getUid(), node.getNid(), "node");
			}
			response.go("/go/" + nkey);
			return null;
		});
		
		
	}
	
}