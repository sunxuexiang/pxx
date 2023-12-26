package com.wanmi.sbc.advertising.api.request.slot;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.wanmi.sbc.advertising.bean.dto.AdSlotPriceDTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zc
 *
 */
@ApiModel
@Data
@Builder
public class SlotBatchSetPriceRequest {

	@ApiModelProperty("广告位价格集合")
	@NotEmpty
	List<AdSlotPriceDTO> prices;
	
}
