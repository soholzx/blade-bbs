package bbs.service.impl;

import bbs.Constant;
import bbs.kit.BBSKit;
import bbs.model.Code;
import bbs.service.CodeService;
import blade.annotation.Component;
import blade.kit.MailKit;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;

@Component
public class CodeServiceImpl implements CodeService {

	Model<Code> model = new Model<Code>(Code.class);
	
	@Override
	public Code getCode(String code) {
		return model.select().eq("code", code).fetchOne();
	}

	@Override
	public boolean sendActive(Integer uid, String email, String code) {
		if(null != uid && StringKit.isNotBlank(code) && BBSKit.isEmail(email)){
			
			String href = Constant.WEB_SITE + "/active?code=" + code;
			
			MailKit.asynSend(email, Constant.MAIL_ACTIVE_TITLE, String.format(Constant.MAIL_ACTIVE_CONTENT, Constant.SITE_NAME, href, href));
			
			return model.insert()
					.param("uid", uid)
					.param("status", 0)
					.param("code", code).executeAndCommit() > 0;
		}
		return false;
	}

	@Override
	public boolean active(String code) {
		Code codeModel = this.getCode(code);
		if(null != codeModel && codeModel.getStatus() == 0){
			Integer uid = codeModel.getUid();
			model.update("update bbs_user set is_active = 1").eq("uid", uid).executeAndCommit();
			return model.update().param("status", 1).eq("id", codeModel.getId()).executeAndCommit() > 0;
		}
		return false;
	}

}
