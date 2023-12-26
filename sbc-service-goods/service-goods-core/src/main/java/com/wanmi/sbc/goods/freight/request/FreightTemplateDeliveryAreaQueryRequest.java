package com.wanmi.sbc.goods.freight.request;

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
 * <p>配送到家范围通用查询请求参数</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightTemplateDeliveryAreaQueryRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-主键标识List
	 */
	@ApiModelProperty(value = "批量查询-主键标识List")
	private List<Long> idList;

	/**
	 * 主键标识
	 */
	@ApiModelProperty(value = "主键标识")
	private Long id;

	/**
	 * 配送地id(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地id(逗号分隔)")
	private String destinationArea;

	/**
	 * 配送地名称(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地名称(逗号分隔)")
	private String destinationAreaName;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识List")
	private List<Long> storeIdList;

	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	private Long companyInfoId;

	/**
	 * 搜索条件:创建时间开始
	 */
	@ApiModelProperty(value = "搜索条件:创建时间开始")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeBegin;
	/**
	 * 搜索条件:创建时间截止
	 */
	@ApiModelProperty(value = "搜索条件:创建时间截止")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	private LocalDateTime createTimeEnd;

	/**
	 * 是否删除(0:否,1:是)
	 */
	@ApiModelProperty(value = "是否删除(0:否,1:是)")
	private DeleteFlag delFlag;

	/**
	 * 是否启用(0:否,1:是)
	 */
	@ApiModelProperty(value = "是否启用(0:否,1:是)")
	private Integer openFlag;

	/**
	 * 仓库ID
	 */
	@ApiModelProperty(value = "仓库ID")
	private Long wareId;

	/**
	 * 免费店配类型
	 */
	@ApiModelProperty(value = "免费店配类型")
	private Integer destinationType;

	/**
	 * 免费店配类型
	 */
	@ApiModelProperty(value = "免费店配类型集合")
	private List<Integer> destinationTypeList;

	/**
	 * 免运费起始数量
	 */
	@ApiModelProperty(value = "免运费起始数量")
	private Long freightFreeNumber;
}