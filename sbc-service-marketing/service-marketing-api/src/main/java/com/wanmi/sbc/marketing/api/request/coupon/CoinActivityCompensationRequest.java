package com.wanmi.sbc.marketing.api.request.coupon;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/7/5 8:41
 */
@ApiModel
@Data
public class CoinActivityCompensationRequest implements Serializable {

    private static final long serialVersionUID = -6535819681308291711L;

    @ApiModelProperty(value = "1:根据订单号,2：根据推送号")
    private Integer type;

    @ApiModelProperty(value = "单号列表")
    private List<String> orderNoList;
}
