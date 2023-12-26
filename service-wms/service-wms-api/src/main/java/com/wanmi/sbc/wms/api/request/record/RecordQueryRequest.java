package com.wanmi.sbc.wms.api.request.record;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>请求记录通用查询请求参数</p>
 * @author baijz
 * @date 2020-05-06 19:23:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-记录主键List
	 */
	@ApiModelProperty(value = "批量查询-记录主键List")
	private List<Long> recordIdList;

	/**
	 * 记录主键
	 */
	@ApiModelProperty(value = "记录主键")
	private Long recordId;

	/**
	 * 请求类型
	 */
	@ApiModelProperty(value = "请求类型")
	private Integer method;

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
	 * 搜索条件:请求时间开始
	 */
	@ApiModelProperty(value = "搜索条件:请求时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:请求时间截止
	 */
	@ApiModelProperty(value = "搜索条件:请求时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

}