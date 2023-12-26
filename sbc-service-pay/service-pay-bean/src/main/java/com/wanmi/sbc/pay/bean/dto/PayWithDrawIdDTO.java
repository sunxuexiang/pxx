package com.wanmi.sbc.pay.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lm
 * @date 2022/10/21 15:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayWithDrawIdDTO implements Serializable {

    private static final long serialVersionUID = -6618059067856724471L;

    @ApiModelProperty("鲸币提现ID")
    private Integer withdrawId;
}
