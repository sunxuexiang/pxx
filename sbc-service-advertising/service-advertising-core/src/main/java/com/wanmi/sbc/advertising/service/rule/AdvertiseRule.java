package com.wanmi.sbc.advertising.service.rule;

import javax.annotation.PostConstruct;

import com.wanmi.sbc.advertising.bean.enums.ActivityType;

public abstract class AdvertiseRule {

	@PostConstruct
	public void register() {
		AdvertiseRuleHolder.register(getActivityType(), this);
	}

	public abstract ActivityType getActivityType();

	


	
	

}
