package bbs.service;

import bbs.model.Code;

public interface CodeService {
	
	public Code getCode(String code);
	
	public boolean sendActive(Integer uid, String email, String code);
	
	public boolean active(String code);
	
}
