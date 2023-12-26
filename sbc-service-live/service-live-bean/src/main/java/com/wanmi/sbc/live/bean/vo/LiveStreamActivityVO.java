package com.wanmi.sbc.live.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>直播商品VO</p>
 */
@ApiModel
@Data
public class LiveStreamActivityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;
    /**
     * 直播id
     */
    @ApiModelProperty(value = "直播id")
    private Integer liveId;
    /**
     * 活动ID
     */
    @ApiModelProperty(value = "活动ID")
    private String activityId;

    /**
     * 优惠卷id
     */
    @ApiModelProperty(value = "优惠卷id")
    private String couponId;
    /**
     * 讲解标识,0:未讲解1:讲解中
     */
    @ApiModelProperty(value = "赠送标识,0:未赠送1:赠送中 2 已结束")
    private Integer presentFlag;
}
