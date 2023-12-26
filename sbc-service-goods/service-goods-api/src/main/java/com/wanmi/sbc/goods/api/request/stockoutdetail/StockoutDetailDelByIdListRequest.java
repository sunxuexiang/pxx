package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除缺货管理请求参数</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-缺货明细List
	 */
	@ApiModelProperty(value = "批量删除-缺货明细List")
	@NotEmpty
	private List<String> stockoutDetailIdList;
}
