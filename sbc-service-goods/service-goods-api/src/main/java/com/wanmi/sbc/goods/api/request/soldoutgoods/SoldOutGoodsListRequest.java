package com.wanmi.sbc.goods.api.request.soldoutgoods;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>类目品牌排序表列表查询请求参数</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoldOutGoodsListRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 批量查询-将要被下架的商品List
	 */
	@ApiModelProperty(value = "批量查询-将要被下架的商品List")
	private List<String> goodsIdList;

	/**
	 * 将要被下架的商品
	 */
	@ApiModelProperty(value = "将要被下架的商品")
	private String goodsId;

}