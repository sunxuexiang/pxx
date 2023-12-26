package com.wanmi.sbc.live.api.response.stream;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

/**
 * 返回福袋中奖用户信息
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BagAppResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "中奖用户")
    private List<String> customerIds;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "活动id")
    private String activityId;

    /**
     * 优惠券Id
     */
    @ApiModelProperty(value = "优惠卷id")
    private String couponId;


    @ApiModelProperty(value = "中奖个数")
    private Integer winningNumber;
}
