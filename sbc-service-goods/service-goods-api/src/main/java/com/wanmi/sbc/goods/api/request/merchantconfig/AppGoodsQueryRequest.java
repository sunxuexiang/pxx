package com.wanmi.sbc.goods.api.request.merchantconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * <p>app端查询推荐商品</p>
 * @author sgy
 * @date 2023-06-27 10:53:36
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppGoodsQueryRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "商户ID")
	private Long 	companyInfoId;

	@ApiModelProperty(value = "店铺Id")
	private Long 	storeId;
	@ApiModelProperty(value = "客户id")
	private String customerId;
}