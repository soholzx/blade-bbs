package bbs.route;

import static blade.Blade.get;
import static blade.Blade.post;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import bbs.Constant;
import bbs.kit.BBSKit;
import bbs.model.User;
import bbs.service.OptionService;
import bbs.service.UserService;
import blade.Blade;
import blade.annotation.Inject;
import blade.kit.EncrypKit;
import blade.kit.FileKit;
import blade.kit.PropertyKit;
import blade.kit.StringKit;
import blade.kit.SystemKit;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.ds.DataSourceManager;
import blade.render.ModelAndView;

public class InstallRoute implements RouteBase {
	
	@Inject
	UserService userService;
	
	@Inject
	OptionService optionService;
	
	@Override
	public void run() {
		
		/**
		 * 安装页面
		 */
		get("/install", (req, res) -> this.getInstallModelAndView("index"));
		
		/**
		 * 输入数据库步骤
		 */
		get("/install/process", (req, res) -> {
			String referer = req.header("Referer");
			if(null == referer || referer.indexOf("/install/check") == -1){
				res.go("/install");
				return null;
			}
			return this.getInstallModelAndView("process");
		});
		
		/**
		 * 检查环境
		 */
		get("/install/check", (req, res) -> {
			
			ModelAndView modelAndView = this.getInstallModelAndView("check");
			
			boolean doNext = true, write = false;;
			
			// 环境变量
			List<Map<String, Object>> environment = new ArrayList<>();
			Map<String, Object> os = new HashMap<>();
			os.put("name", "操作系统");
			os.put("recommend", "Linux");
			os.put("lowest", "不限制");
			os.put("current", SystemKit.getOsInfo().getName());
			os.put("isok", true);
			environment.add(os);
			
			Map<String, Object> jdk = new HashMap<>();
			jdk.put("name", "Java版本");
			jdk.put("recommend", "1.8");
			jdk.put("lowest", "1.6");
			jdk.put("current", SystemKit.getJavaInfo().getVersionFloat());
			jdk.put("isok", SystemKit.getJavaInfo().getVersionFloat() >= 1.6);
			if(!(boolean) jdk.get("isok")){
				doNext = false;
			}
			environment.add(jdk);
			
			Map<String, Object> space = new HashMap<>();
			space.put("name", "磁盘空间");
			space.put("recommend", ">200M");
			space.put("lowest", "200M");
			File temp = new File(InstallRoute.class.getResource("/").getPath());
			long free = temp.getFreeSpace();
			space.put("current", BBSKit.FormetFileSize(free));
			space.put("isok", free >= 1024 * 1024 * 200);
			if(!(boolean) space.get("isok")){
				doNext = false;
			}
			environment.add(space);
			
			// 系统目录
			String uploadPath = Blade.webRoot() + "" + Constant.UPLOAD_FOLDER;
			
			try {
				File file = FileKit.createTempFile("temp", ".txt", uploadPath);
				if(file.exists()){
					write = true;
					file.delete();
				}
			} catch (Exception e) {
				write = false;
				doNext = false;
			}
			
			modelAndView.add("write", write);
			modelAndView.add("doNext", doNext);
			
			modelAndView.add("environment", environment);
			return modelAndView;
		});
		
		get("/install/testdb", (req, res) -> {
			
			String dbhost = req.query("dbhost");
			String dbuser = req.query("dbuser");
			String dbpsw = req.query("dbpsw");
			String dbname = req.query("dbname");
			
			String text = "";
			
			if(StringKit.isNotBlank(dbhost) && StringKit.isNotBlank(dbuser) &&
					StringKit.isNotBlank(dbpsw) && StringKit.isNotBlank(dbname)){
				try {
					if(dbhost.indexOf(":") == -1){
						dbhost += ":3306";
					}
					
					Class.forName("com.mysql.jdbc.Driver");
					
					String url = "jdbc:mysql://" + dbhost + "/" + dbname;
					Sql2o sql2o = new Sql2o(url, dbuser, dbpsw);
					Connection connection = sql2o.open();
					connection.close();
					text = "数据库连接成功";
				} catch (Exception e) {
					e.printStackTrace();
					text = "数据库连接失败";
				}
			} else {
				text = "数据库信息不完整";
			}
			
			res.text(text);
			
			return null;
		});
		
		/**
		 * 输入数据库步骤
		 */
		post("/install/process", (req, res) -> {
			
			ModelAndView modelAndView = this.getInstallModelAndView("process");
			String dbhost = req.query("dbhost");
			String dbuser = req.query("dbuser");
			String dbpsw = req.query("dbpsw");
			String dbname = req.query("dbname");
			String username = req.query("username");
			String password = req.query("password");
			
			try {
				if(dbhost.indexOf(":") == -1){
					dbhost += ":3306";
				}
				String pwd = EncrypKit.md5(username + password);
				String url = "jdbc:mysql://" + dbhost + "/" + dbname;
				
				Sql2o sql2o = new Sql2o(url, dbuser, dbpsw);
				Connection connection = sql2o.open();
				List<String> tables = new ArrayList<>();
				tables.add("DROP TABLE IF EXISTS `bbs_code`;");
				tables.add("CREATE TABLE `bbs_code` ( `id` int(10) NOT NULL AUTO_INCREMENT, `uid` int(10) NOT NULL COMMENT '用户uid', `code` varchar(64) NOT NULL COMMENT '激活码', `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态，1：已经使用，0：未使用', PRIMARY KEY (`id`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_comment`;");
				tables.add("CREATE TABLE `bbs_comment` ( `cid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID', `tid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '主题ID', `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论人UID', `replytime` datetime NOT NULL COMMENT '评论时间', `content` text NOT NULL COMMENT '评论内容', PRIMARY KEY (`cid`), KEY `uid` (`uid`), KEY `tid` (`tid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_node`;");
				tables.add("CREATE TABLE `bbs_node` ( `nid` int(10) unsigned NOT NULL AUTO_INCREMENT, `pid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '父级ID', `featured` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否在首页显示', `topshow` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否置顶', `nname` varchar(250) NOT NULL DEFAULT '' COMMENT '节点名称', `nkey` varchar(100) NOT NULL COMMENT '节点key', `content` text NOT NULL COMMENT '节点介绍', `keywords` varchar(250) NOT NULL DEFAULT '' COMMENT '节点关键字', PRIMARY KEY (`nid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_node_follow`;");
				tables.add("CREATE TABLE `bbs_node_follow` ( `id` int(10) unsigned NOT NULL AUTO_INCREMENT, `nid` int(10) unsigned NOT NULL DEFAULT '0', `uid` int(10) unsigned NOT NULL DEFAULT '0', `addtime` int(10) unsigned NOT NULL DEFAULT '0', PRIMARY KEY (`id`), KEY `uid` (`uid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_notice`;");
				tables.add("CREATE TABLE `bbs_notice` ( `nid` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '通知ID', `cid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论ID', `tid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '主题ID', `fuid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论人UID', `tuid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '主题发布人UID', `ntype` tinyint(4) NOT NULL DEFAULT '0' COMMENT '通知类型', `addtime` datetime NOT NULL COMMENT '添加时间', PRIMARY KEY (`nid`), KEY `tuid` (`tuid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_option`;");
				tables.add("CREATE TABLE `bbs_option` ( `opt_key` varchar(255) NOT NULL, `opt_value` text, PRIMARY KEY (`opt_key`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_topic`;");
				tables.add("CREATE TABLE `bbs_topic` ( `tid` int(10) unsigned NOT NULL AUTO_INCREMENT, `nid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '节点ID', `uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '发布人UID', `title` text NOT NULL COMMENT '主题标题', `content` longtext NOT NULL COMMENT '主题内容', `addtime` datetime NOT NULL COMMENT '添加时间', `replyuid` int(10) DEFAULT NULL COMMENT '回复人uid', `replytime` datetime NOT NULL COMMENT '回复时间', `view` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '查看次数', `comment` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '评论个数', `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态', PRIMARY KEY (`tid`), KEY `replytime` (`replytime`), KEY `nid` (`nid`), KEY `uid` (`uid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_topic_follow`;");
				tables.add("CREATE TABLE `bbs_topic_follow` ( `id` int(10) unsigned NOT NULL AUTO_INCREMENT, `tid` int(10) unsigned NOT NULL DEFAULT '0', `uid` int(10) unsigned NOT NULL DEFAULT '0', `addtime` datetime NOT NULL, PRIMARY KEY (`id`), KEY `uid` (`uid`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_user`;");
				tables.add("CREATE TABLE `bbs_user` ( `uid` int(10) unsigned NOT NULL AUTO_INCREMENT, `username` varchar(60) NOT NULL DEFAULT '' COMMENT '用户名', `password` varchar(64) NOT NULL DEFAULT '' COMMENT '密码', `email` varchar(100) NOT NULL DEFAULT '' COMMENT '登录邮箱', `avatar` varchar(100) DEFAULT NULL COMMENT '头像', `homepage` varchar(100) NOT NULL DEFAULT '' COMMENT '主页', `money` int(10) NOT NULL DEFAULT '100' COMMENT '金钱', `qq` varchar(100) NOT NULL DEFAULT '' COMMENT 'qq', `location` varchar(250) NOT NULL DEFAULT '' COMMENT '地址', `signature` text, `introduction` text COMMENT '介绍', `topic` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '主题数', `reply` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '回复数', `notice` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '通知数', `node_follow` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '关注节点', `user_follow` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '关注用户', `topic_follow` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '关注主题', `regtime` datetime NOT NULL COMMENT '注册时间', `lastlogin` datetime DEFAULT NULL COMMENT '最后登录时间', `lastpost` datetime DEFAULT NULL COMMENT '最后操作时间', `group_id` int(11) NOT NULL DEFAULT '0' COMMENT '所在组id', `is_active` int(11) NOT NULL DEFAULT '1' COMMENT '是否已经激活', PRIMARY KEY (`uid`), KEY `username` (`username`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_clock`;");
				tables.add("CREATE TABLE `bbs_clock` ( `id` int(10) NOT NULL AUTO_INCREMENT, `uid` int(10) NOT NULL, `count` tinyint(4) NOT NULL DEFAULT '0', `addtime` datetime NOT NULL, PRIMARY KEY (`id`) ) DEFAULT CHARSET=utf8;");
				
				tables.add("DROP TABLE IF EXISTS `bbs_user_follow`;");
				tables.add("CREATE TABLE `bbs_user_follow` ( `id` int(10) unsigned NOT NULL AUTO_INCREMENT, `uid` int(10) unsigned NOT NULL DEFAULT '0', `fuid` int(10) unsigned NOT NULL DEFAULT '0', `addtime` datetime NOT NULL, PRIMARY KEY (`id`), KEY `uid` (`uid`) ) DEFAULT CHARSET=utf8;");
				tables.add("INSERT INTO `bbs_option`(`opt_key`,`opt_value`) values ('site_theme', ''), ('site_analysis',''),('site_comment_number','0'),('site_description','简约开源的轻社区'),('site_introduction','<p>欢迎访问 Blade BBS ！</p>\r\n<p>Blade BBS是一个简约开源的轻社区。</p>'),('site_keywords','BladeBBS,Java中国,Blade框架'),('site_name','Blade BBS'),('site_subtitle','简约开源的Java社区'),('site_topic_number','0'),('site_topic_status','1'),('site_user_number','1'),('site_welcome_msg','欢迎访问 Blade BBS');");
				tables.add("INSERT INTO `bbs_user`(`uid`,`username`,`password`,`email`,`avatar`,`homepage`,`money`,`qq`,`location`,`signature`,`introduction`,`topic`,`reply`,`notice`,`node_follow`,`user_follow`,`topic_follow`,`regtime`,`lastlogin`,`lastpost`,`group_id`,`is_active`) VALUES (1, '"+username+"', '"+pwd+"', '', 'static/upload/avatar/default/4.png', '', 100, '', '', NULL, NULL, 0, 0, 0, 0, 0, 0, '2015-09-01 15:22:57', NULL, NULL, 1, 1);");
				for(String tableName : tables){
					connection.createQuery(tableName).executeUpdate();
				}
				
				connection.close();
				
				// 添加加锁文件
				String classPath = InstallRoute.class.getResource("/").getPath();
				FileKit.createFile(classPath + File.separator + "install.lock");
				
				Map<String, Object> dbMap = new HashMap<>();
				dbMap.put("blade.db.url", url);
				dbMap.put("blade.db.username", dbuser);
				dbMap.put("blade.db.password", dbpsw);
				
				// 写入数据库配置
				BBSKit.writeDB(dbMap);
				
				try {
					Properties props = PropertyKit.getProperty("ds.properties");
					DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
					DataSourceManager.me().setDataSource(dataSource);
					Sql2oPlugin.INSTANCE.run();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				User user = userService.getByUsername(username);
				req.session().attribute(Constant.LOGIN_SESSION, user);
				
				Map<String, Object> sysinfo = optionService.getOptions();
				Constant.SYSINFO = sysinfo;
				Constant.CONTEXT.set(Constant.SYSINFO_FIELD, Constant.SYSINFO);
				
				Constant.SITE_NAME = Constant.SYSINFO.get("site_name").toString();
				Constant.MAIL_ACTIVE_TITLE = Constant.SITE_NAME + "帐号激活";
				
				Constant.IS_INSTALL = true;
				
				// 跳转到后台
				res.go("/");
				return null;
			} catch (Exception e) {
				modelAndView.add(ERROR, "数据库连接失败！");
				return modelAndView;
			}
		});
	}
	
}
