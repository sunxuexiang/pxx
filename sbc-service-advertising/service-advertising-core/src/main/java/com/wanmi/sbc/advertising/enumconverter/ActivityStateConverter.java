package com.wanmi.sbc.advertising.enumconverter;

import javax.persistence.AttributeConverter;

import com.wanmi.sbc.advertising.bean.enums.ActivityState;

public class ActivityStateConverter implements AttributeConverter<ActivityState, Integer> {

	@Override
	public Integer convertToDatabaseColumn(ActivityState attribute) {
		return attribute.toValue();
	}

	@Override
	public ActivityState convertToEntityAttribute(Integer dbData) {
		return ActivityState.fromValue(dbData);
	}

}





