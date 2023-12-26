package com.wanmi.sbc.goods.api.request.goodslabelrela;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * <p>单个查询邀新统计请求参数</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaInGoodsIdsRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品标签关联主键ID
	 */
	@ApiModelProperty(value = "商品ID")
	@NotNull
	private List<String> goodsIds;

	@ApiModelProperty(value = "标签ID")
	private Long labelId;
}