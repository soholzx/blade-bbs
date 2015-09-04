package bbs;

import java.util.Map;

import jetbrick.template.JetGlobalContext;

/**
 * 常量类
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Constant {
	
	/**
	 * 用户登录session标识
	 */
	public static final String LOGIN_SESSION = "login_user";
	
	/**
	 * 当前主题
	 */
	public static String THEME_NAME = "default";
	
	/**
	 * 是否是debug模式
	 */
	public static final boolean IS_DEBUG = true;
	
	/**
	 * 是否已经安装
	 */
	public static boolean IS_INSTALL = false;
	
	/**
	 * 上传文件目录
	 */
	public static final String UPLOAD_FOLDER = "static/upload";
	
	/**
	 * 系统配置
	 */
	public static final String SYSINFO_FIELD = "sysinfo";
	
	/**
	 * 记录访问cookie
	 */
	public static final String VIEWCOUNT_FIELD = "topic_view_count";

	/**
	 * CDN域名
	 */
	protected static String CDN_SITE = "";
	
	/**
	 * 系统配置
	 */
	public static Map<String, Object> SYSINFO = null;
	
	/**
	 * Jet全局context对象
	 */
	public static JetGlobalContext CONTEXT;
	
	/**
	 * 网站域名
	 */
	public static String WEB_SITE = "";
	
	/**
	 * 网站名称
	 */
	public static String SITE_NAME = "Blade BBS";
	
	/**
	 * 激活邮件标题
	 */
	public static String MAIL_ACTIVE_TITLE = "Blade BBS - 账号激活";
	
	/**
	 * 激活邮件内容
	 */
	public static String MAIL_ACTIVE_CONTENT = "<p>感谢您注册%s！ </p>"
			+ "<p>请点击如下链接进行激活：</p>"
			+ "<p> <a href='%s' target='_blank'>%s</a> </p>"; 
	
 }
