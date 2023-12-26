package com.wanmi.sbc.customer.api.request.network;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>直播间新增参数</p>
 * @author zwb
 * @date 2020-06-06 18:28:57
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetWorkByconditionRequest extends BaseQueryRequest implements Serializable  {
	private static final long serialVersionUID = 1L;


	/**
	 * netWorkId
	 */
	@ApiModelProperty(value = "netWorkId")
	private Long networkId;


	/**
	 * netWorkIds
	 */
	@ApiModelProperty(value = "networkIds")
	private List<Long> networkIds;

	/**
	 * 网点联系人
	 */
	@ApiModelProperty(value = "contacts")
	private String contacts;


	/**
	 * 网点名字
	 */
	@ApiModelProperty(value = "网点名字")
	private String networkName;


	/**
	 * 网点手机号码
	 */
	@ApiModelProperty(value = "网点手机号码")
	private String phone;


	/**
	 * 网点座机号码
	 */
	@ApiModelProperty(value = "网点座机号码")
	private String landline;


	/**
	 * 网点地址
	 */
	@ApiModelProperty(value = "networkAddress")
	private String networkAddress;


	/**
	 * 省
	 */
	@ApiModelProperty(value = "网点省地址")
	private String province;

	/**
	 * 网点市
	 */
	@ApiModelProperty(value = "city")
	private String city;

	/**
	 * 区
	 */
	@ApiModelProperty(value = "区")
	private String area;


	/**
	 * 镇
	 */
	@ApiModelProperty(value = "镇")
	private String town;


	@ApiModelProperty(value = "省名称")
	private String provinceName;


	@ApiModelProperty(value = "市名称")
	private String cityName;

	@ApiModelProperty(value = "区名称")
	private String areaName;

	@ApiModelProperty(value = "镇名称")
	private String townName;

	@ApiModelProperty(value = "镇名称")
	private String specificAdress;


	/**
	 * 纬度值
	 */
	@ApiModelProperty(value = "lat")
	private BigDecimal lat;


	/**
	 * 经度值
	 */
	@ApiModelProperty(value = "lng")
	private BigDecimal lng;

	/**
	 * 可配送距离 米
	 */
	@ApiModelProperty(value = "可配送距离米")
	private int distance;

	/**
	 * 是否删除
	 */
	@ApiModelProperty(value = "是否删除")
	private Integer delFlag;

}