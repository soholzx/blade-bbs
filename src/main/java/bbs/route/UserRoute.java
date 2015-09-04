package bbs.route;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import bbs.Constant;
import bbs.Funcs;
import bbs.kit.BBSKit;
import bbs.kit.ImageKit;
import bbs.model.User;
import bbs.service.TopicService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.EncrypKit;
import blade.kit.FileKit;
import blade.kit.IOKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.WhereParam;
import blade.render.ModelAndView;
import blade.servlet.FileItem;
import blade.servlet.ServletFileUpload;

public class UserRoute implements RouteBase {
	
	@Inject
	private UserService userService;
	
	@Inject
	private TopicService topicService;
	
	@Override
	public void run() {
		
		// 用户设置
		Blade.get("/settings", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			ModelAndView modelAndView = this.getFrontModelAndView("settings");
			return modelAndView;
		});
		
		// 会员详情
		Blade.get("/member/:username", (request, response) -> {
			String username = request.pathParam("username");
			if(StringKit.isBlank(username)){
				response.go("/");
				return null;
			}
			User user = userService.getByUsername(username);
			if(null == user || user.getIs_active() == 0){
				response.go("/");
				return null;
			}
			Map<String, Object> profile = userService.getProfile(user.getUid());
			ModelAndView modelAndView = this.getFrontModelAndView("member_detail");
			modelAndView.add("profile", profile);
			
			WhereParam where = WhereParam.me().eq("a.uid", user.getUid()).eq("a.status", 1);
			
			// 该用户最近主题
			List<Map<String, Object>> recent_topics = topicService.getTopicRecent(where, 1, 15, null).getResults();
			modelAndView.add("recent_topics", recent_topics);
			
			// 收藏状态
			Integer uid = null;
			User loginuser = request.session().attribute(Constant.LOGIN_SESSION);
			if(null != loginuser){
				uid = loginuser.getUid();
			}
			
			boolean isfollow = userService.isFollow(uid, user.getUid(), "user");
			modelAndView.add("isfollow", isfollow);
					
			return modelAndView;
		});
		
		// 保存用户信息
		Blade.post("/user/edit", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			
			String type = request.query("type");
			if(StringKit.isBlank(type)){
				response.go("/");
				return null;
			}
			
			ModelAndView modelAndView = this.getFrontModelAndView("settings");
			
			// 基本信息
			if(type.equals("base")){
				String email = request.query("email");
				String qq = request.query("qq");
				String location = request.query("location");
				String homepage = request.query("homepage");
				String signature = request.query("signature");
				String introduction = request.query("introduction");
				
				if(StringKit.isBlank(email)){
					modelAndView.add("base_error", "邮箱不能为空！");
					return modelAndView;
				}
				
				if(!BBSKit.isEmail(email)){
					modelAndView.add("base_error", "邮箱格式错误！");
					return modelAndView;
				}
				
				boolean isModifyEmail = false;
					
				if(!email.equals(user.getEmail())){
					User emailuser = userService.getByEmail(email);
					if(null != emailuser){
						modelAndView.add("base_error", "该邮箱已经被占用！");
						return modelAndView;
					} else {
						// 修改了邮箱
						isModifyEmail = true;
					}
				}
				
				user = userService.updateInfo(user.getUid(), email, qq, location, homepage, signature, introduction);
				
				// 刷新登录用户session
				request.session().attribute(Constant.LOGIN_SESSION, user);
				if(isModifyEmail){
					modelAndView.add("base_success", "邮箱修改成功，请验证邮箱并修改密码！");
					return modelAndView;
				}
				
				response.go("/settings");
				
				return null;
			}
			
			// 更新头像
			if(type.equals("avatar")){
				ServletFileUpload fileUpload = ServletFileUpload.parseRequest(request.servletRequest());
				
				boolean isMultipart = fileUpload.isMultipartContent(request.servletRequest());
				
				if(isMultipart){
					FileItem fileItem = fileUpload.fileItem("avatar");
					
					String suffix = FileKit.getExtension(fileItem.getFileName());
					if(StringKit.isNotBlank(suffix)){
						suffix = "." + suffix;
					}
					
					// 不是图片类型
					if(!BBSKit.isImage(suffix)){
						response.go("/settings");
						return null;
					}
					String fileName = BBSKit.userAvatar(user.getUid());
					
					// 原图
					String saveName = Constant.UPLOAD_FOLDER + "/avatar/" + fileName + suffix;
					
					// 缩略图
					String normalName = Constant.UPLOAD_FOLDER + "/avatar/" + fileName + "_normal" + suffix;
					
					String savepath = Blade.webRoot() + File.separator + saveName;
					
					String normalpath = Blade.webRoot() + File.separator + normalName;
					
					File file = new File(savepath);
					
					IOKit.write(fileItem.getFileContent(), file);
					
					try {
						
						// 如果原图宽度<200则原图=缩略图
						if(!ImageKit.resize(file, new File(normalpath), 200, 0.9f)){
							FileKit.copy(file.getPath(), normalpath);
						}
						
						// 更新用户头像
						user = userService.updateAvatar(user.getUid(), normalName);
						// 刷新登录用户session
						request.session().attribute(Constant.LOGIN_SESSION, user);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					response.go("/settings");
					
					return null;
				}
			}
			
			
			// 修改密码
			if(type.equals("pwd")){
				String curpwd = request.query("curpwd");
				String newpwd = request.query("newpwd");
				String renewpwd = request.query("renewpwd");
				if(StringKit.isBlank(curpwd)){
					modelAndView.add("pwd_error", "请输入当前密码！");
					return modelAndView;
				}
				if(StringKit.isBlank(newpwd)){
					modelAndView.add("pwd_error", "请输入新密码！");
					return modelAndView;
				}
				if(StringKit.isBlank(renewpwd)){
					modelAndView.add("pwd_error", "请确认新密码！");
					return modelAndView;
				}
				if(!renewpwd.equals(newpwd)){
					modelAndView.add("pwd_error", "新密码输入不一致！");
					return modelAndView;
				}
				
				if(!EncrypKit.md5(user.getUsername() + curpwd).equalsIgnoreCase(user.getPassword())){
					modelAndView.add("pwd_error", "当前密码错误！");
					return modelAndView;
				}
				
				modelAndView.add("pwd_success", "密码修改成功，请重新登录！");
			}
			return modelAndView;
		});
		
		Blade.get("/user/follow/:uid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				String path = Funcs.base_url("/signin");
				response.html("<script>alert('请登录后进行收藏！');location.href='"+ path +"';</script>");
				return null;
			}
			Integer uid = request.pathParamToInt("uid");
			if(null != uid){
				userService.follow(user.getUid(), uid, "user");
			}
			// 更新用户关注人数
			userService.updateCount(user.getUid(), "user", true);
			request.session().attribute(Constant.LOGIN_SESSION, userService.getByUID(user.getUid()));
			response.go("/member/" + userService.getByUID(uid).getUsername());
			return null;
		});
		
		Blade.get("/user/unfollow/:uid", (request, response) -> {
			User user = verifySignin();
			if(null == user){
				response.go("/");
				return null;
			}
			Integer uid = request.pathParamToInt("uid");
			if(null != uid){
				userService.unfollow(user.getUid(), uid, "user");
			}
			// 更新用户关注人数
			userService.updateCount(user.getUid(), "user", false);
			request.session().attribute(Constant.LOGIN_SESSION, userService.getByUID(user.getUid()));
			response.go("/member/" + userService.getByUID(uid).getUsername());
			return null;
		});
		
		/**
		 * 签到
		 */
		Blade.post("/clock-in", (req, res) -> {
			User user = verifySignin();
			if(null == user){
				res.go("/");
				return null;
			}
			return null;
		});
		
	}
	
}
