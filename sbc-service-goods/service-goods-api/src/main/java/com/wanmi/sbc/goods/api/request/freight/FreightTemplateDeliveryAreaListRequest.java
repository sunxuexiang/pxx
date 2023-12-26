package com.wanmi.sbc.goods.api.request.freight;


import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * <p>配送到家范围列表查询请求参数</p>
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightTemplateDeliveryAreaListRequest extends BaseQueryRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 配送地id(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地id", notes = "逗号分隔")
	private String[] destinationArea;

	/**
	 * 配送地名称(逗号分隔)
	 */
	@ApiModelProperty(value = "配送地名称", notes = "逗号分隔")
	private String[] destinationAreaName;

	/**
	 * 店铺标识
	 */
	@ApiModelProperty(value = "店铺标识")
	private Long storeId;

	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "公司信息ID")
	private Long companyInfoId;

	/**
	 * 公司信息ID
	 */
	@ApiModelProperty(value = "仓库ID")
	private Long wareId;

	/**
	 * 配送方式List
	 */
	@ApiModelProperty(value = "配送方式List")
	private List<Integer> destinationTypeList;
}