package com.wanmi.sbc.order.request;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: sbc_h_tian
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-14 09:56
 **/
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class TradeParentTidRequest  extends BaseRequest {

    private static final long serialVersionUID = -3106790833666168436L;
    @ApiModelProperty(value = "订单父id")
    private String parentId;
    @ApiModelProperty(value = "订单id")
    private String tId;
}
