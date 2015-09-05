package bbs.route;

import java.util.Date;
import java.util.List;
import java.util.Map;

import bbs.Constant;
import bbs.kit.BBSKit;
import bbs.model.Code;
import bbs.model.Node;
import bbs.model.User;
import bbs.service.CodeService;
import bbs.service.NodeService;
import bbs.service.OptionService;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.DateKit;
import blade.kit.EncrypKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.Page;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;

// 主页操作
public class HomeRoute implements RouteBase{
	
	@Inject
	private UserService userService;
	
	@Inject
	private NodeService nodeService;
	
	@Inject
	private TopicService topicService;
	
	@Inject
	private CodeService codeService;
	
	@Inject
	private OptionService optionService;
	
	@Override
	public void run() {
		
		Blade.get("/", (request, response) -> {
			ModelAndView modelAndView = getFrontModelAndView("home");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			List<Node> nodes = nodeService.getNodes(1, null);
			List<Node> tnodes = nodeService.getNomalNodes(null, 1);
			
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			
			String tab = request.query("tab");
			if(StringKit.isNotBlank(tab)){
				if(tab.equals("all")){
					modelAndView.add("top_show_node", "all");
				} else {
					Node node = nodeService.getNodeByNKey(tab);
					if(null != node){
						where.eq("d.nkey", tab);
						modelAndView.add("top_show_node", tab);
					}
				}
			}
			
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(where, page, 20, null);
			
			modelAndView.add("nodes", nodes);
			modelAndView.add("tnodes", tnodes);
			modelAndView.add("hot_topics", hot_topics);
			modelAndView.add("topicPage", topicPage);
			
			return modelAndView;
		});
		
		Blade.get("/signin", (req, res) -> {
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			ModelAndView modelAndView = this.getFrontModelAndView("signin");
			modelAndView.add("hot_topics", hot_topics);
			return modelAndView;
		});
		Blade.get("/signup", (req, res) -> {
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			ModelAndView modelAndView = this.getFrontModelAndView("signup");
			modelAndView.add("hot_topics", hot_topics);
			return modelAndView;
		});
		
		// 注册
		Blade.post("/signup", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("signup");
			String username = request.query("username");
			String password = request.query("password");
			String email = request.query("email");
			
			modelAndView.add("username", username);
			modelAndView.add("email", email);
			
			if(StringKit.isBlank(username)){
				modelAndView.add(ERROR, "用户名不能为空");
				return modelAndView;
			}
			
			if(username.length() < 3 || username.length() > 12){
				modelAndView.add(ERROR, "用户名长度错误");
				return modelAndView;
			}
			
			if(!BBSKit.isUsername(username)){
				modelAndView.add(ERROR, "用户名必须是小写字母+数字组成");
				return modelAndView;
			}
			
			if(StringKit.isBlank(password)){
				modelAndView.add(ERROR, "密码不能为空");
				return modelAndView;
			}
			
			if(password.length() < 6){
				modelAndView.add(ERROR, "密码长度必须大于6位");
				return modelAndView;
			}
			
			if(StringKit.isBlank(email)){
				modelAndView.add(ERROR, "邮箱不能为空");
				return modelAndView;
			}
			
			if(!BBSKit.isEmail(email)){
				modelAndView.add(ERROR, "邮箱格式错误");
				return modelAndView;
			}
			
			User user = userService.getByUsername(username);
			if(null != user){
				if(user.getIs_active() == 0){
					modelAndView.add(ERROR, "该用户已经成功注册，请查看邮件进行激活");
				} else {
					modelAndView.add(ERROR, "该用户名已经被注册，请重新输入");
				}
				return modelAndView;
			}
			
			User reguser = userService.signup(username, password, email);
			if(null != reguser){
				// 发送激活邮件
				codeService.sendActive(reguser.getUid(), email, EncrypKit.md5(username + StringKit.random(5)));
				modelAndView.remove("username");
				modelAndView.remove("email");
				
				// 刷新注册人数
				Integer user_number = Integer.valueOf(optionService.getOption("site_user_number"));
				optionService.update("site_user_number", user_number + 1);
				
				modelAndView.add(STATUS, SUCCESS);
			} else {
				modelAndView.add(STATUS, FAILURE);
			}
			return modelAndView;
		});
		
		Blade.get("/recent", (request, response) -> {
			ModelAndView modelAndView = getFrontModelAndView("home");
			Integer page = (null == request.queryToInt("page")) ? 1 : request.queryToInt("page");
			List<Node> nodes = nodeService.getNodes(1, null);
			List<Node> tnodes = nodeService.getNomalNodes(null, 1);
			
			Date time = DateKit.getDateByUnixTime(DateKit.getCurrentUnixTime() - 86400*90);
			
			WhereParam where = WhereParam.me().eq("a.status", 1).greaterThan("a.addtime", time);
			
			List<Map<String, Object>> hot_topics = topicService.getTopicRecent(where, 1, 10, "view desc").getResults();
			
			WhereParam pagewhere = WhereParam.me().eq("a.status", 1);
			
			Page<Map<String, Object>> topicPage = topicService.getTopicRecent(pagewhere, page, 20, null);
			
			modelAndView.add("nodes", nodes);
			modelAndView.add("tnodes", tnodes);
			modelAndView.add("hot_topics", hot_topics);
			modelAndView.add("topicPage", topicPage);
			
			return modelAndView;
		});
		
		Blade.get("/signin", (req, res) -> this.getFront("signin"));
		Blade.get("/signup", (req, res) -> this.getFront("signup"));
		
		// 注册
		Blade.post("/signup", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("signup");
			String username = request.query("username");
			String password = request.query("password");
			String email = request.query("email");
			
			modelAndView.add("username", username);
			modelAndView.add("email", email);
			
			if(StringKit.isBlank(username)){
				modelAndView.add(ERROR, "用户名不能为空");
				return modelAndView;
			}
			
			if(username.length() < 3 || username.length() > 12){
				modelAndView.add(ERROR, "用户名长度错误");
				return modelAndView;
			}
			
			if(!BBSKit.isUsername(username)){
				modelAndView.add(ERROR, "用户名必须是小写字母+数字组成");
				return modelAndView;
			}
			
			if(StringKit.isBlank(password)){
				modelAndView.add(ERROR, "密码不能为空");
				return modelAndView;
			}
			
			if(password.length() < 6){
				modelAndView.add(ERROR, "密码长度必须大于6位");
				return modelAndView;
			}
			
			if(StringKit.isBlank(email)){
				modelAndView.add(ERROR, "邮箱不能为空");
				return modelAndView;
			}
			
			if(!BBSKit.isEmail(email)){
				modelAndView.add(ERROR, "邮箱格式错误");
				return modelAndView;
			}
			
			User user = userService.getByUsername(username);
			if(null != user){
				if(user.getIs_active() == 0){
					modelAndView.add(ERROR, "该用户已经成功注册，请查看邮件进行激活");
				} else {
					modelAndView.add(ERROR, "该用户名已经被注册，请重新输入");
				}
				return modelAndView;
			}
			
			User reguser = userService.signup(username, password, email);
			if(null != reguser){
				// 发送激活邮件
				codeService.sendActive(reguser.getUid(), email, EncrypKit.md5(username + StringKit.random(5)));
				modelAndView.remove("username");
				modelAndView.remove("email");
				
				// 刷新注册人数
				Integer user_number = Integer.valueOf(optionService.getOption("site_user_number"));
				optionService.update("site_user_number", user_number + 1);
				
				modelAndView.add(STATUS, SUCCESS);
			} else {
				modelAndView.add(STATUS, FAILURE);
			}
			return modelAndView;
		});
		
		// 登录
		Blade.post("/signin", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("signin");
			String username = request.query("username");
			String password = request.query("password");
			
			modelAndView.add("username", username);
			
			if(StringKit.isBlank(username)){
				modelAndView.add(ERROR, "用户名不能为空");
				return modelAndView;
			}
			
			if(StringKit.isBlank(password)){
				modelAndView.add(ERROR, "密码不能为空");
				return modelAndView;
			}
			
			User user = userService.getByUsername(username);
			if(null == user){
				modelAndView.add(ERROR, "不存在该用户");
				return modelAndView;
			}
			
			String pwd = EncrypKit.md5(username + password);
			if(!user.getPassword().equals(pwd)){
				modelAndView.add(ERROR, "用户名或密码错误");
				return modelAndView;
			}
			request.session().attribute(Constant.LOGIN_SESSION, user);
			response.go("/");
			return null;
		});
		
		// 注销
		Blade.get("/signout", (request, response) -> {
			User user = request.session().attribute(Constant.LOGIN_SESSION);
			if(null != user){
				request.session().removeAttribute(Constant.LOGIN_SESSION);
			}
			response.go("/");
			return null;
		});
		
		// 激活邮件
		Blade.get("/active", (request, response) -> {
			ModelAndView modelAndView = this.getFrontModelAndView("active");
			// 激活成功 更新注册会员数
			String code = request.query("code");
			if(StringKit.isBlank(code)){
				response.go("/");
				return null;
			}
			
			Code codeModel = codeService.getCode(code);
			if(null == codeModel){
				modelAndView.add(ERROR, "不存在该激活码");
				return modelAndView;
			}
			
			if(codeModel.getStatus() == 1){
				modelAndView.add(ERROR, "该用户已经激活成功，请自行登录");
				return modelAndView;
			}
			
			boolean flag = codeService.active(code);
			if(flag){
				modelAndView.add(STATUS, SUCCESS);
			} else {
				modelAndView.add(ERROR, "激活失败，请联系管理员！");
			}
			return modelAndView;
		});
	}
	
}
