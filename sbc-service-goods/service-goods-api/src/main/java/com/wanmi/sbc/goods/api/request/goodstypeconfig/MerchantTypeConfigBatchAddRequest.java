package com.wanmi.sbc.goods.api.request.goodstypeconfig;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>分类推荐分类新增参数</p>
 * @author sgy
 * @date 2023-06-07 10:53:36
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantTypeConfigBatchAddRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 推荐的分类编号
	 */
	@ApiModelProperty(value = "推荐的分类编号")
	private List<String> merchantRecommendTypeId;


	@ApiModelProperty(value = "店铺ID")
	private Long storeId;

	@ApiModelProperty(value = "商家Id")
	private Long companyInfoId;


}