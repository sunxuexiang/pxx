package com.wanmi.sbc.returnorder.api.request.areas;

import com.wanmi.sbc.returnorder.bean.dto.ReturnOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class ReturnOrderRequest implements Serializable {

    @ApiModelProperty(value = "退单信息")
    ReturnOrderDTO returnOrderDTO;
}
