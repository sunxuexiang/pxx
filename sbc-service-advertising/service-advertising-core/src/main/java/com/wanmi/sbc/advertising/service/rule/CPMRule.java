package com.wanmi.sbc.advertising.service.rule;

import org.springframework.stereotype.Component;

import com.wanmi.sbc.advertising.api.request.activity.ActSaveRequest;
import com.wanmi.sbc.advertising.bean.enums.ActivityType;
import com.wanmi.sbc.advertising.model.AdActivity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CPMRule extends AdvertiseRule {
	
	@Override
	public ActivityType getActivityType() {
		return ActivityType.CPM;
	}

}
