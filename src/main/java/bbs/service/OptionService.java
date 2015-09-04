package bbs.service;

import java.util.Map;

public interface OptionService {
	
	public String getOption(String key);
	
	public Map<String, Object> getOptions();
	
	public boolean update(String key, Object value);
}
