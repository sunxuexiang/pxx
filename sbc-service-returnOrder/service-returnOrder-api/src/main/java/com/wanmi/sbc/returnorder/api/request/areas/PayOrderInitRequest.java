package com.wanmi.sbc.returnorder.api.request.areas;

import com.wanmi.sbc.returnorder.bean.dto.PayOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class PayOrderInitRequest implements Serializable {

    @ApiModelProperty(value = "付款单信息列表")
    List<PayOrderDTO> payOrderVOS;
}
