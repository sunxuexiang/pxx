package com.wanmi.sbc.returnorder.api.response.trade;

import com.wanmi.sbc.returnorder.bean.dto.TradeDeliveryWayResDTO;
import com.wanmi.sbc.returnorder.bean.dto.TradeDeliveryWayResMergeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeDeliveryWayResponse implements Serializable {
    private static final long serialVersionUID = 719026499910276135L;

    @ApiModelProperty(value = "店铺配送方式")
    List<TradeDeliveryWayResDTO> resDTOList;

    @ApiModelProperty(value = "Android要的店铺配送方式")
    List<TradeDeliveryWayResMergeDTO> resMergeDTOList;

    @ApiModelProperty(value = "IOS要的店铺配送方式")
    List<TradeDeliveryWayResMergeDTO> resIOSMergeDTOList;
}
