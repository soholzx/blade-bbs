package bbs.route.admin;

import java.util.Map;

import bbs.model.User;
import bbs.route.RouteBase;
import bbs.service.OptionService;
import blade.Blade;
import blade.annotation.Inject;
import blade.render.ModelAndView;

public class OptionsRoute implements RouteBase {

	@Inject
	private OptionService optionService;
	
	@Override
	public void run() {
		
		Blade.get("/admin/settings/site", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = getAdminModelAndView("setting_site");
			Map<String, Object> options = optionService.getOptions();
			modelAndView.add("options", options);
			return modelAndView;
		});
		
		Blade.post("/admin/settings/site", (request, response) -> {
			User user = adminUser();
			if(null == user || user.getGroup_id() == 3){
				response.go("/signin");
				return null;
			}
			ModelAndView modelAndView = getAdminModelAndView("settings");
			
			String site_name = request.query("site_name");
			String site_subtitle = request.query("site_subtitle");
			String site_welcome_msg = request.query("site_welcome_msg");
			String site_keywords = request.query("site_keywords");
			String site_description = request.query("site_description");
			String site_introduction = request.query("site_introduction");
			String site_analysis = request.query("site_analysis");
			
			String old_site_name = optionService.getOption("site_name");
			if(null != site_name && !old_site_name.equals(site_name)){
				optionService.update("site_name", site_name);
			}
			
			String old_site_subtitle = optionService.getOption("site_subtitle");
			if(null != site_subtitle && !old_site_subtitle.equals(site_subtitle)){
				optionService.update("site_subtitle", site_subtitle);
			}
			
			String old_site_welcome_msg = optionService.getOption("site_welcome_msg");
			if(null != site_welcome_msg && !old_site_welcome_msg.equals(site_welcome_msg)){
				optionService.update("site_welcome_msg", site_welcome_msg);
			}

			String old_site_keywords = optionService.getOption("site_keywords");
			if(null != site_keywords && !old_site_keywords.equals(site_keywords)){
				optionService.update("site_welcome_msg", site_keywords);
			}
			
			String old_site_description = optionService.getOption("site_description");
			if(null != site_description && !old_site_description.equals(site_description)){
				optionService.update("site_welcome_msg", site_description);
			}
			
			String old_site_introduction = optionService.getOption("site_introduction");
			if(null != site_introduction && !old_site_introduction.equals(site_introduction)){
				optionService.update("site_welcome_msg", site_introduction);
			}
			
			String old_site_analysis = optionService.getOption("site_analysis");
			if(null != site_analysis && !old_site_analysis.equals(site_analysis)){
				optionService.update("site_welcome_msg", site_analysis);
			}
			
			Map<String, Object> options = optionService.getOptions();
			modelAndView.add("options", options);
			modelAndView.add(STATUS, SUCCESS);
			
			return modelAndView;
		});
		
		
	}
	
}
