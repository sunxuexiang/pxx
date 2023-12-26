package com.wanmi.sbc.goods.api.request.livegoods;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;


/**
 * <p>单个查询直播商品请求参数</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveGoodsBySkuIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 微信id
	 */
	@ApiModelProperty(value = "skuId")
	@NotNull
	private String goodsInfoId;


}