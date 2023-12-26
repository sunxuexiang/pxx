package com.wanmi.sbc.advertising.api.request.slot;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.wanmi.sbc.advertising.bean.enums.SlotState;
import com.wanmi.sbc.advertising.bean.enums.SlotType;
import com.wanmi.sbc.common.base.BaseQueryRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author zc
 *
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotQueryListPageRequest extends BaseQueryRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 广告位类型
	 */
	@ApiModelProperty("广告位类型")
	@NotNull
	private SlotType slotType;


	/**
	 * 批发市场id
	 */
    @ApiModelProperty("批发市场id")
	private Integer marketId;
    
	/**
	 * 商城id
	 */
    @ApiModelProperty("商城id")
	private Integer mallTabId;
    
	/**
	 * 商品类目id
	 */
    @ApiModelProperty("商品类目id")
	private Integer goodsCateId;

}
