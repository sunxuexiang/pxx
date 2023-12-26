package com.wanmi.sbc.returnorder.api.request.trade;


import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class NewPileGoodsNumByCustomerRequest implements Serializable {
    /**
     * 下单用户
     */
    @ApiModelProperty(value = "下单用户")
    private CustomerVO customer;

    @ApiModelProperty(value = "仓库id")
    private Long wareId;
}
