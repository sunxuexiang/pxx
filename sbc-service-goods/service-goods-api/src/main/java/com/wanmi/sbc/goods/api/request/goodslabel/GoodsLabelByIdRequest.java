package com.wanmi.sbc.goods.api.request.goodslabel;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询导航配置请求参数</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 标签id
	 */
	@ApiModelProperty(value = "标签id")
	@NotNull
	private Long id;

	/**
	 * 店铺id
	 */
	@ApiModelProperty(value = "店铺id", hidden = true)
	private Long storeId;

}