package com.wanmi.sbc.advertising.api.request.statistic;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zc
 *
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticAddRequest {

	/**
	 * 广告活动id
	 */
    @ApiModelProperty("广告活动id")
	@NotNull
	private String activityId;

	/**
	 * 发生时间
	 */
    @ApiModelProperty("发生时间")
	@NotNull
	private Date generateTime;

	/**
	 * 地理位置
	 */
    @ApiModelProperty("地理位置")
	@NotBlank
	private String geoPosition;

	/**
	 * 设备型号
	 */
    @ApiModelProperty("设备型号")
	@NotBlank
	private String phoneModel;

	/**
	 * 用户账号，登陆后有
	 */
    @ApiModelProperty("用户账号，登陆后有")
	private String userAccount;
	
    @ApiModelProperty("广告统计类型 0.展示 1.点击")
	private Integer statisticInfoType;
	

}
