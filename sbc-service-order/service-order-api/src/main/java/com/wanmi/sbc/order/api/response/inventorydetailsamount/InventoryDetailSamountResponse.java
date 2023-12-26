package com.wanmi.sbc.order.api.response.inventorydetailsamount;

import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.order.bean.vo.TrueStockVO;
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
public class InventoryDetailSamountResponse implements Serializable {

    @ApiModelProperty(value = "InventoryDetailSamountVO")
    private List<InventoryDetailSamountVO> inventoryDetailSamountVOS;
}
