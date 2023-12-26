package com.wanmi.sbc.goods.api.request.stockoutmanage;

import com.wanmi.sbc.common.base.BaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * <p>单个查询缺货管理请求参数</p>
 * @author tzx
 * @date 2020-05-27 09:37:01
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutManageByIdRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;
	/**
	 * 缺货管理
	 */
	@ApiModelProperty(value = "缺货管理")
	@NotNull
	private String stockoutId;

}