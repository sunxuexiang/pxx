package com.wanmi.sbc.order.api.response.returnorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 根据订单id查询可退商品数响应结构
 * Created by jinwei on 6/5/2017.
 */

@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class CanReturnItemResponse implements Serializable {

    private static final long serialVersionUID = -7559699036673758917L;

    @ApiModelProperty(value = "退单id")
    private Boolean canReturnFlag;

}
