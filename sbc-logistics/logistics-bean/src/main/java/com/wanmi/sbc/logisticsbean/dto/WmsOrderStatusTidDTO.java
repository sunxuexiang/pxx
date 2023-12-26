package com.wanmi.sbc.logisticsbean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author lm
 * @date 2022/11/08 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("wms订单状态查询参数")
public class WmsOrderStatusTidDTO implements Serializable {

    /**
     * 订单ID
     */
    @ApiModelProperty("订单ID")
    @NotBlank(message = "订单号不能为空")
    private String tid;

}
