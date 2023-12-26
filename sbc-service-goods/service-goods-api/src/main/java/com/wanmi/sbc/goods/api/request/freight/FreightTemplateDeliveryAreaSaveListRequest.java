package com.wanmi.sbc.goods.api.request.freight;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreightTemplateDeliveryAreaSaveListRequest {

    @ApiModelProperty(value = "免费店配集合")
    private List<FreightTemplateDeliveryAreaSaveRequest> freightTemplateDeliveryAreaList;
}
