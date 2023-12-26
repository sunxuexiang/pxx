package com.wanmi.sbc.advertising.service.rule;

import java.util.HashMap;
import java.util.Map;

import com.wanmi.sbc.advertising.bean.enums.ActivityType;

public class AdvertiseRuleHolder {

	private static Map<ActivityType, AdvertiseRule> advertiseRules = new HashMap<ActivityType, AdvertiseRule>();

	public static AdvertiseRule getAdvertiseRule(ActivityType type) {
		if (type == null) {
			return null;
		}
		return advertiseRules.get(type);
	}

	public static void register(ActivityType type, AdvertiseRule rule) {
		advertiseRules.put(type, rule);
	}


}
