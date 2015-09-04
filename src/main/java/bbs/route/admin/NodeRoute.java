package bbs.route.admin;

import java.util.List;

import bbs.model.Node;
import bbs.model.User;
import bbs.route.RouteBase;
import bbs.service.NodeService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.StringKit;
import blade.render.ModelAndView;


public class NodeRoute implements RouteBase {

	@Inject
	private NodeService nodeService;
	
	@Override
	public void run() {
		
		Blade.get("/admin/node", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = getAdminModelAndView("node");
			List<Node> nodes = nodeService.getNodes(null, null);		
			modelAndView.add("nodes", nodes);
			return modelAndView;
		});
		
		Blade.get("/admin/node/add", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = getAdminModelAndView("node_add");
			List<Node> nodes = nodeService.getNomalNodes(null, null);		
			modelAndView.add("nodes", nodes);
			return modelAndView;
		});
		
		Blade.post("/admin/node/add", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = getAdminModelAndView("node_add");
			List<Node> nodes = nodeService.getNomalNodes(null, null);		
			modelAndView.add("nodes", nodes);
			
			String nname = request.query("nname");
			String nkey = request.query("nkey");
			Integer pid = request.queryToInt("pid");
			Integer featured = request.queryToInt("featured");
			Integer topshow = request.queryToInt("topshow");
			String keywords = request.query("keywords");
			String content = request.query("content");
			if(StringKit.isBlank(nname)){
				modelAndView.add(ERROR, "节点名称不能为空");
				return modelAndView;
			}
			if(null == pid){
				modelAndView.add(ERROR, "请选择父节点");
				return modelAndView;
			}
			
			boolean flag = nodeService.save(nname, nkey, pid, featured, topshow, keywords, content);
			if(flag){
				response.go("/admin/node");
				return null;
			} else {
				modelAndView.add(ERROR, "保存失败");
			}
			
			return modelAndView;
		});
		
		Blade.get("/admin/node/edit/:nid", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			Integer nid = request.pathParamToInt("nid");
			
			ModelAndView modelAndView = getAdminModelAndView("node_edit");
			
			Node node = nodeService.getNodeByNid(nid);
		
			List<Node> nodes = nodeService.getNomalNodes(null, null);		
			modelAndView.add("nodes", nodes);
			modelAndView.add("node", node);
			return modelAndView;
		});
		
		Blade.post("/admin/node/edit", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			
			ModelAndView modelAndView = getAdminModelAndView("node_edit");
			
			Integer nid = request.queryToInt("nid");
			
			Node node = nodeService.getNodeByNid(nid);
		
			modelAndView.add("node", node);
			
			List<Node> nodes = nodeService.getNomalNodes(null, null);		
			modelAndView.add("nodes", nodes);
			
			String nname = request.query("nname");
			String nkey = request.query("nkey");
			Integer pid = request.queryToInt("pid");
			Integer featured = request.queryToInt("featured");
			Integer topshow = request.queryToInt("topshow");
			String keywords = request.query("keywords");
			String content = request.query("content");
			if(StringKit.isBlank(nname)){
				modelAndView.add(ERROR, "节点名称不能为空");
				return modelAndView;
			}
			if(null == pid){
				modelAndView.add(ERROR, "请选择父节点");
				return modelAndView;
			}
			Node nknode = nodeService.getNodeByNKey(nkey);
			if(!nknode.getNid().equals(nid)){
				modelAndView.add(ERROR, "节点key已经存在");
				return modelAndView;
			}
			
			boolean flag = nodeService.update(nid, nname, nkey, pid, featured, topshow, keywords, content);
			if(flag){
				response.go("/admin/node");
				return null;
			} else {
				modelAndView.add(ERROR, "保存失败");
			}
			return modelAndView;
		});
		
	}
	
}
