package com.wanmi.sbc.wms.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import lombok.Data;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>请求记录VO</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@Data
public class RecordVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 记录主键
	 */
	@ApiModelProperty(value = "记录主键")
	private Long recordId;

	/**
	 * 请求类型
	 */
	@ApiModelProperty(value = "请求类型")
	private Integer method;//1:post 2:put

	/**
	 * 请求的地址
	 */
	@ApiModelProperty(value = "请求的地址")
	private String requestUrl;

	/**
	 * 请求的实体
	 */
	@ApiModelProperty(value = "请求的实体")
	private String requestBody;

	/**
	 * 返回的信息
	 */
	@ApiModelProperty(value = "返回的信息")
	private String resposeInfo;

	/**
	 * 返回的状态
	 */
	@ApiModelProperty(value = "返回的状态")
	private String status;

	/**
	 * 请求时间
	 */
	@ApiModelProperty(value = "请求时间")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTime;

}