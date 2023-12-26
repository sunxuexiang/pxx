package com.wanmi.sbc.order.api.request.InventoryDetailSamount;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubInventoryDetailSamountRequest  extends BaseRequest implements Serializable {


    @ApiModelProperty(value = "订单商品集合")
    private List<InventoryDetailSamountVO> inventoryDetailSamountVOList;

    @ApiModelProperty(value = "takeId")
    private String takeId;
}
