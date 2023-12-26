package com.wanmi.sbc.goods.api.request.stockoutmanage;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>批量删除缺货管理请求参数</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageDelByIdListRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

	/**
	 * 批量删除-缺货管理List
	 */
	@ApiModelProperty(value = "批量删除-缺货管理List")
	@NotEmpty
	private List<String> stockoutIdList;
}
