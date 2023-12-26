package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * <p>批量删除仓库表请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-wareIdList
	 */
	@ApiModelProperty(value = "批量删除-wareIdList")
	private List<Long> wareIdList;
}
