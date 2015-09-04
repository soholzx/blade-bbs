package bbs.route;

import bbs.Constant;
import bbs.kit.BBSKit;
import bbs.model.User;
import blade.BladeWebContext;
import blade.render.ModelAndView;

public interface RouteBase extends blade.route.RouteBase{

	Integer pageSize = 10;
	
	String STATUS = "status";
	
	// 成功
	String SUCCESS = "success";
	
	// 服务器异常
	String ERROR = "error";
	
	// 已经存在
	String EXIST = "exist";
	
	// 失败
	String FAILURE = "failure";
	
	public default ModelAndView getFrontModelAndView(String view){
		view = "/theme/" + Constant.THEME_NAME + "/" + view + ".html";
		return new ModelAndView(view);
	}
	
	public default String getFront(String view){
		view = "/theme/" + Constant.THEME_NAME + "/" + view + ".html";
		return view;
	}
	
	public default ModelAndView getAdminModelAndView(String view){
		view = "/admin/" + view + ".html";
		return new ModelAndView(view);
	}
	
	public default ModelAndView getInstallModelAndView(String view){
		view = "/install/" + view + ".html";
		ModelAndView modelAndView = new ModelAndView(view);
		// 已经安装
		if(BBSKit.isInstall()){
			modelAndView.add(ERROR, "程序已经安装成功，请勿重复安装！");
		}
		return modelAndView;
	}
	
	/*
	 * 验证是否登录
	 */
	public default User verifySignin(){
		return BladeWebContext.session().attribute(Constant.LOGIN_SESSION);
	}
	
	/*
	 * 验证是否登录并且是管理员
	 */
	public default User adminUser(){
		return BladeWebContext.session().attribute(Constant.LOGIN_SESSION);
	}
	
}
