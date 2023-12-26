package com.wanmi.sbc.goods.api.request.goodslabelrela;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


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
public class GoodsLabelRelaByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品标签关联主键ID
	 */
	@ApiModelProperty(value = "商品标签关联主键ID")
	@NotNull
	private Long id;

}