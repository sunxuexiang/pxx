package com.wanmi.sbc.returnorder.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>测试代码生成VO</p>
 * @author lh
 * @date 2020-07-14 13:48:26
 */
@ApiModel
@Data
public class PickUpRecordVO implements Serializable {


	private static final long serialVersionUID = 341689752111138229L;
	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private String pickUpId;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id")
	private Long storeId;

	/**
	 * 订单id
	 */
	@ApiModelProperty(value = "订单id")
	private String tradeId;


	/**
	 * 自提码
	 */
	@ApiModelProperty(value = "自提码")
	private String pickUpCode;

	/**
	 * 是否已自提:0:未自提 1：已自提
	 */
	@ApiModelProperty(value = "是否已自提:0:未自提 1：已自提")
	private DefaultFlag pickUpFlag;

	/**
	 * 自提时间
	 */
	@ApiModelProperty(value = "自提时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime pickUpTime;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号")
	private String contactPhone;


	/**
	 * 创建时间
	 */
	@ApiModelProperty(value = "创建时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}