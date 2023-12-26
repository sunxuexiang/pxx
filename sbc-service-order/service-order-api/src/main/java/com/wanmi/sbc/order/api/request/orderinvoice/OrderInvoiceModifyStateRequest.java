package com.wanmi.sbc.order.api.request.orderinvoice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-12-03 14:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class OrderInvoiceModifyStateRequest implements Serializable {

    /**
     * 开票id列表
     */
    @ApiModelProperty(value = "开票id列表")
    private List<String> orderInvoiceIds;

}

