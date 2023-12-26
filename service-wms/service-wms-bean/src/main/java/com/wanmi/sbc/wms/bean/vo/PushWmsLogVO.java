package com.wanmi.sbc.wms.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;


@ApiModel
@Data
@Accessors(chain = true)
public class PushWmsLogVO implements Serializable {


	/**
	 * 记录主键
	 */
	@ApiModelProperty(value = "记录主键")
	private Long pWmsId;

	/**
	 * 作业单号
	 */
	@ApiModelProperty(value = "作业单号")
	private String docNo;

	/**
	 * 作业单类型
	 */
	@ApiModelProperty(value = "货主ID")
	private String customerId;

	/**
	 * 取消原因
	 */
	@ApiModelProperty(value = "取消原因")
	private String erpCancelReason;

	/**
	 * 请求json
	 */
	@ApiModelProperty(value = "请求json")
	private String pPrarmJson;



	/**
	 * 请求时间
	 */
	@ApiModelProperty(value = "请求时间")
	@CreatedDate
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime create_time;


	/**
	 * 修改时间
	 */
	@ApiModelProperty(value = "修改时间")
	@LastModifiedDate
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime updateTime;


	/**
	 * 转态0成功1失败
	 */
	@ApiModelProperty(value = "转态0成功1失败")
	private Integer statues;

	/**
	 * 返回信息
	 */
	@ApiModelProperty(value = "返回信息")
	private String resposeInfo;

	/**
	 * 错误原因
	 */
	@ApiModelProperty(value = "错误原因")
	private String erroInfo;




	/**
	 * 仓库ID
	 */
	@ApiModelProperty(value = "仓库ID")
	private String warehouseId;


	/**
	 * 作业单类型
	 */
	@ApiModelProperty(value = "作业单类型")
	private String orderType;
}