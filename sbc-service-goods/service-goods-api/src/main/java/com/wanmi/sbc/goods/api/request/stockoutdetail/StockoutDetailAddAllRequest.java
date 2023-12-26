package com.wanmi.sbc.goods.api.request.stockoutdetail;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>缺货管理新增参数</p>
 * @author tzx
 * @date 2020-05-27 10:48:14
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailAddAllRequest extends BaseRequest {

	private static final long serialVersionUID = -7397045474186162735L;

	@ApiModelProperty(value = "缺货明细")
	@NotNull
	private List<StockoutDetailRequest> stockOutList;
}