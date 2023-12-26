package com.wanmi.sbc.returnorder.api.request.trade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: yang
 * @Description:
 * @Date: 2021-06-18 15:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class TradePushRequest implements Serializable {


    private static final long serialVersionUID = 4158944060107876746L;

    /**
     * 交易id
     */
    @ApiModelProperty(value = "交易id")
    private String tid;

}
