package com.wanmi.sbc.returnorder.api.request.historytownshiporder;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;


@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryTownShipOrderStockRequest extends BaseRequest {


	private static final long serialVersionUID = 1595311683126197446L;

	@ApiModelProperty(value = "skuid集合")
	private List<String> skuids;

	@ApiModelProperty(value = "wareId")
	private Long wareId;
}