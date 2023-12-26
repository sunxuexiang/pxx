package com.wanmi.sbc.advertising.service.rule;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wanmi.sbc.advertising.bean.enums.ActivityType;
import com.wanmi.sbc.advertising.repository.AdActivityRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CPTRule extends AdvertiseRule{
	
	@Autowired
	private AdActivityRepository adActivityRepository;

	@Override
	public ActivityType getActivityType() {
		return ActivityType.CPT;
	}


}
