package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jeffrey
 * @create 2021-08-07 9:16
 */
@Data
public class GoodsSalesRankingRequest implements Serializable {
    private static final long serialVersionUID = -2908106907870427257L;

    @ApiModelProperty(value = "0 || null：获取TOP榜")
    private Long cateId;

    private String customerId;

}
