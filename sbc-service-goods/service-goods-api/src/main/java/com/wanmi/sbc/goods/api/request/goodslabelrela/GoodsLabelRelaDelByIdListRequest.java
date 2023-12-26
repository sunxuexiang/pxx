package com.wanmi.sbc.goods.api.request.goodslabelrela;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除邀新统计请求参数</p>
 * @author lvheng
 * @date 2021-04-23 14:20:19
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLabelRelaDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-商品标签关联主键IDList
	 */
	@ApiModelProperty(value = "批量删除-商品标签关联主键IDList")
	@NotEmpty
	private List<Long> idList;
}
