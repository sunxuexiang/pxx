package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * <p>单个查询仓库表请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseBySkuIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * wareId
	 */
	@ApiModelProperty(value = "wareId")
	@NotNull
	private String wareId;

	/**
	 * skuid
	 */
	@ApiModelProperty(value = "skuid", hidden = true)
	private String skuId;

}