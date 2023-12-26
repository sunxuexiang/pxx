package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>单个查询分类推荐分类请求参数</p>
 * @author sgy
 * @date 2023-06-08 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigByIdRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐分类主键编号
	 */
	@ApiModelProperty(value = "推荐分类主键编号")
	@NotNull
	private String merchantTypeId;
}