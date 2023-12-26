package com.wanmi.sbc.setting.api.request.homedelivery;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import java.time.LocalDateTime;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>配送到家分页查询请求参数</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDeliveryPageRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-主键List
	 */
	@ApiModelProperty(value = "批量查询-主键List")
	private List<Long> homeDeliveryIdList;

	/**
	 * 主键
	 */
	@ApiModelProperty(value = "主键")
	private Long homeDeliveryId;

	/**
	 * 送货范围(KM)
	 */
	@ApiModelProperty(value = "送货范围(KM)")
	private Long distance;

	/**
	 * content
	 */
	@ApiModelProperty(value = "content")
	private String content;

	/**
	 * 搜索条件:生成时间开始
	 */
	@ApiModelProperty(value = "搜索条件:生成时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:生成时间截止
	 */
	@ApiModelProperty(value = "搜索条件:生成时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	/**
	 * 删除标志位
	 */
	@ApiModelProperty(value = "删除标志位")
	private DeleteFlag delFlag;

}