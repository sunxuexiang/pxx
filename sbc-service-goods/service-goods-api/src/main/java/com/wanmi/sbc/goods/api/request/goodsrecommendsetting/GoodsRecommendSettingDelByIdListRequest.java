package com.wanmi.sbc.goods.api.request.goodsrecommendsetting;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * <p>批量删除商品推荐配置请求参数</p>
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsRecommendSettingDelByIdListRequest extends GoodsBaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-商品推荐配置主键List
	 */
	@ApiModelProperty(value = "批量删除-商品推荐配置主键List")
	@NotEmpty
	private List<String> settingIdList;
}