package com.wanmi.sbc.order.api.response.inventorydetailsamounttrade;

import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountTradeVO;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDetailSamountTradeResponse implements Serializable {

    @ApiModelProperty(value = "InventoryDetailSamountTradeVO")
    private List<InventoryDetailSamountTradeVO> inventoryDetailSamountTradeVOS;
}
