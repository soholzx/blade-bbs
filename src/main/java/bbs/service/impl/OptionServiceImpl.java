package bbs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bbs.Constant;
import bbs.model.Option;
import bbs.service.OptionService;
import blade.annotation.Component;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;

@Component
public class OptionServiceImpl implements OptionService {
	
	private Model<Option> model = new Model<Option>(Option.class);

	@Override
	public Map<String, Object> getOptions() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Option> options = model.select().fetchList();
		if(null != options && options.size() > 0){
			for(Option option : options){
				String value = option.getOpt_value();
				map.put(option.getOpt_key(), value);
			}
		}
		return map;
	}

	@Override
	public boolean update(String key, Object value) {
		if(StringKit.isNotBlank(key) && null != value){
			model.update().param("opt_value", value.toString()).eq("opt_key", key).executeAndCommit();
			Constant.SYSINFO = this.getOptions();
			Constant.CONTEXT.set(Constant.SYSINFO_FIELD, Constant.SYSINFO);
		}
		return false;
	}

	@Override
	public String getOption(String key) {
		if(StringKit.isNotBlank(key)){
			String value = model.fetchByPk(key).getOpt_value();
			return null == value ? "" : value;
		}
		return null;
	}
}
