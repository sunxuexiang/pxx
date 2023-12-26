package com.wanmi.sbc.advertising.enumconverter;

import javax.persistence.AttributeConverter;

import com.wanmi.sbc.advertising.bean.enums.SlotType;

public class SlotTypeConverter implements AttributeConverter<SlotType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(SlotType attribute) {
		return attribute.toValue();
	}

	@Override
	public SlotType convertToEntityAttribute(Integer dbData) {
		return SlotType.fromValue(dbData);
	}

}





