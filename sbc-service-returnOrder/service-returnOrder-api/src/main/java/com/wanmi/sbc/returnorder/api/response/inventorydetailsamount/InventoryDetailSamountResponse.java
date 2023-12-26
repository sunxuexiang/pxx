package com.wanmi.sbc.returnorder.api.response.inventorydetailsamount;

import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountVO;
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
