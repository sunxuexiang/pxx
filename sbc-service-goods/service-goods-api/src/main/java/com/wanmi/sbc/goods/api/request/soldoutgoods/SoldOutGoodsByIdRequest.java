package com.wanmi.sbc.goods.api.request.soldoutgoods;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询类目品牌排序表请求参数</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldOutGoodsByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 将要被下架的商品
	 */
	@ApiModelProperty(value = "将要被下架的商品")
	@NotNull
	private String goodsId;

}