package bbs.route.admin;

import java.util.Map;

import bbs.model.User;
import bbs.route.RouteBase;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.StringKit;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;

public class UserRoute implements RouteBase {
	
	@Inject
	private UserService userService;
	
	@Inject
	private TopicService topicService;
	
	@Override
	public void run() {
		
		// 用户设置
		Blade.get("/admin/user", (request, response) -> {
			User user = adminUser();
			if(null == user){
				response.go("/");
				return null;
			}
			ModelAndView modelAndView = this.getAdminModelAndView("user");
			
			WhereParam whereParam = WhereParam.me();
			
			Page<Map<String, Object>> userpage = userService.getUsers(whereParam, 1, this.pageSize);
			modelAndView.add("userpage", userpage);
			return modelAndView;
		});
		
		Blade.get("/admin/user/:page", (request, response) -> {
			User user = adminUser();
			if(null == user){
				response.go("/");
				return null;
			}
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			
			ModelAndView modelAndView = this.getAdminModelAndView("user");
			
			WhereParam whereParam = WhereParam.me();
			Page<Map<String, Object>> userpage = userService.getUsers(whereParam, page, pageSize);
			modelAndView.add("userpage", userpage);
			return modelAndView;
		});
		
		
		Blade.get("/admin/user/edit/:uid", (request, response) -> {
			User adminuser = adminUser();
			if(null == adminuser){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getAdminModelAndView("user_edit");
			Integer uid = request.pathParamToInt("uid");
			User user = userService.getByUID(uid);
			modelAndView.add("user", user);
			return modelAndView;
		});
		
		Blade.post("/admin/user/edit", (request, response) -> {
			User adminuser = adminUser();
			if(null == adminuser){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getAdminModelAndView("user_edit");
			Integer uid = request.queryToInt("uid");
			User user = userService.getByUID(uid);
			modelAndView.add("user", user);
			
			String username = request.query("username");
			String qq = request.query("qq");
			String location = request.query("location");
			String homepage = request.query("homepage");
			String signature = request.query("signature");
			String introduction = request.query("introduction");
			
			// 修改了用户名
			if(StringKit.isNotBlank(username) && !user.getUsername().equals(username)){
				User temp = userService.getByUsername(username);
				if(null != temp){
					modelAndView.add(ERROR, "该用户名已经存在，请更换");
					return modelAndView;
				} else {
					userService.updateUserName(uid, username, user.getEmail());
				}
			}
			user = userService.updateInfo(uid, null, qq, location, homepage, signature, introduction);
			modelAndView.add("user", user);
			return modelAndView;
		});
	}
	
	
}
