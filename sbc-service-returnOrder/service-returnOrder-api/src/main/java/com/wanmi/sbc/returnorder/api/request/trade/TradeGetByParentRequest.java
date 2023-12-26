package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-14 09:31
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradeGetByParentRequest  implements Serializable {
    private static final long serialVersionUID = -4153498256754887224L;
    /**
     * 交易id
     */
    @ApiModelProperty(value = "订单父id")
    private String parentId;
}
