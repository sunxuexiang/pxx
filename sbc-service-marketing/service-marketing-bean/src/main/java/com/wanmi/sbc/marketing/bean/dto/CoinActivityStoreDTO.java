package com.wanmi.sbc.marketing.bean.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CoinActivityStoreDTO {

	/**
	 * 主键id
	 */
    @ApiModelProperty(value = "主键id")
	private Long id;

	/**
	 * 活动id
	 */
    @ApiModelProperty(value = "活动id")
	private String activityId;

	/**
	 * 店铺id
	 */
    @ApiModelProperty(value = "店铺id")
	private Long storeId;
	
	/**
	 * 店铺name
	 */
    @ApiModelProperty(value = "店铺name")
	private String storeName;
    
	/**
	 * 商家账号
	 */
    @ApiModelProperty(value = "商家账号")
	private String accountName;
	
	/**
	 * 批发市场名称
	 */
    @ApiModelProperty(value = "批发市场名称")
	private String marketName;


	/**
	 * 商家类型
	 */
    @ApiModelProperty(value = "商家类型")
	private CompanyType companyType;

	/**
	 * 是否终止
	 */
    @ApiModelProperty(value = "是否终止")
	private BoolFlag terminationFlag;

	/**
	 * 终止时间
	 */
    @ApiModelProperty(value = "终止时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime terminationTime;
}
