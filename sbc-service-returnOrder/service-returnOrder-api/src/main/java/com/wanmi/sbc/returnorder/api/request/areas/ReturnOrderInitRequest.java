package com.wanmi.sbc.returnorder.api.request.areas;

import com.wanmi.sbc.returnorder.bean.dto.ReturnOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ReturnOrderInitRequest {

    @ApiModelProperty(value = "退单信息")
    ReturnOrderDTO returnOrderVO;
}
