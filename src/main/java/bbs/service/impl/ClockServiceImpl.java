package bbs.service.impl;

import java.util.Date;

import bbs.model.Clock;
import bbs.service.ClockService;
import bbs.service.NoticeService;
import blade.annotation.Component;
import blade.annotation.Inject;
import blade.kit.DateKit;
import blade.plugin.sql2o.Model;

@Component
public class ClockServiceImpl implements ClockService{

	private Model<Clock> model =  new Model<>(Clock.class);
	
	@Inject
	private NoticeService noticeService;
	
	@Override
	public boolean signin(Integer uid) {
		if(null == uid){
			return false;
		}
		// 判断今天是否签到
		
		Date start = DateKit.getYesterday(DateKit.getTomorrow());
		Date end = DateKit.getTomorrow();
		Integer clock_count = 1;
		if(issignin(clock_count)){
			Date ns = DateKit.getYesterday(start);
			clock_count = model.select("select count from bbs_clock").eq("uid", uid).greaterThan("addtime", ns).lessThan("addtime", start).fetchColum();
		}
		
		// 判断连续签到天数
		return false;
	}

	@Override
	public boolean issignin(Integer uid) {
		Date start = DateKit.getYesterday(DateKit.getTomorrow());
		Date end = DateKit.getTomorrow();
		return model.count().eq("uid", uid).greaterThan("addtime", start).lessThan("addtime", end).fetchCount() > 0;
	}

}
