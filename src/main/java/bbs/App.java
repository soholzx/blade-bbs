package bbs;

import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import jetbrick.template.JetEngine;
import jetbrick.template.JetGlobalContext;
import jetbrick.template.resolver.GlobalResolver;
import bbs.kit.BBSKit;
import bbs.route.admin.AdminRoute;
import bbs.route.admin.NodeRoute;
import bbs.route.admin.TopicRoute;
import bbs.service.OptionService;
import bbs.service.UserService;
import blade.Blade;
import blade.Bootstrap;
import blade.annotation.Inject;
import blade.kit.MailKit;
import blade.kit.PropertyKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.ds.DataSourceManager;
import blade.render.JetbrickRender;
import blade.route.Router;
import blade.servlet.Request;
import blade.servlet.Response;

public class App extends Bootstrap {

	static Logger logger = Logger.getLogger(App.class);
	
	@Inject
	UserService userService;
	
	@Inject
	OptionService optionService;
	
	JetEngine jetEngine;
	
	@Override
	public void init() {
		
		Blade.load(bbs.route.HomeRoute.class);
		Blade.load(bbs.route.InstallRoute.class);
		Blade.load(bbs.route.UserRoute.class);
		Blade.load(bbs.route.NodeRoute.class);
		Blade.load(bbs.route.TopicRoute.class);
		Blade.load(bbs.route.CommentRoute.class);
		
		// 加载后台路由
		Blade.load(AdminRoute.class);
		Blade.load(NodeRoute.class);
		Blade.load(TopicRoute.class);
		
		Blade.before("/*", new Router() {
			@Override
			public Object handler(Request request, Response response) {
				if(!BBSKit.isInstall() && request.uri().indexOf("/install") == -1){
					return request.invoke("/install");
				}
				
				request.attribute("cdn", Constant.CDN_SITE);
				request.attribute("version", "1.0");
				return request.invoke();
			}
		});
		
		Blade.config("blade.properties");
		
		// 设置模板引擎
		JetbrickRender jetbrickRender = new JetbrickRender();
		jetEngine = jetbrickRender.getJetEngine();
		Blade.viewEngin(jetbrickRender);
		
		// 配置邮箱管理员
		MailKit.config(MailKit.SMTP_QQ, Blade.config().get("mail.user"), Blade.config().get("mail.pass"));
		
	}
	
	@Override
	public void contextInitialized(ServletContext servletContext) {
		
		Constant.IS_INSTALL = BBSKit.isInstall();
		
		// 配置数据库
		if(Constant.IS_INSTALL){
			try {
				Properties props = PropertyKit.getProperty("ds.properties");
				DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
				DataSourceManager.me().setDataSource(dataSource);
				Sql2oPlugin.INSTANCE.run();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
				
		JetGlobalContext globalContext = jetEngine.getGlobalContext();
		Constant.CONTEXT = globalContext;
		
		GlobalResolver resolver = jetEngine.getGlobalResolver();
		resolver.registerFunctions(Funcs.class);
		resolver.registerMethods(Methods.class);
		
		// 参数初始化
		Constant.WEB_SITE = Blade.config().get("web_site");
		
		Constant.CDN_SITE = Blade.config().get("cnd_site");
		
		if(Constant.IS_INSTALL){
			Map<String, Object> sysinfo = optionService.getOptions();
			Constant.SYSINFO = sysinfo;
			Constant.CONTEXT.set(Map.class, Constant.SYSINFO_FIELD, Constant.SYSINFO);
			
			Constant.SITE_NAME = Constant.SYSINFO.get("site_name").toString();
			
			String theme = Constant.SYSINFO.get("site_theme").toString();
			if(StringKit.isNotBlank(theme)){
				Constant.THEME_NAME = theme;
			}
			
			Constant.MAIL_ACTIVE_TITLE = Constant.SITE_NAME + "帐号激活";
		}
		
	}
	
}