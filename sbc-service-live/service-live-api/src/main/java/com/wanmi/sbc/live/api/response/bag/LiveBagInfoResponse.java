package com.wanmi.sbc.live.api.response.bag;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBagInfoResponse  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**live_bag_id**/
    private Long liveBagId;

    /**直播间id**/
    private Long liveRoomId;

    /**福袋名称**/
    private String bagName;

    /**中奖名额**/
    private Long winningNumber;

    /**开奖时间：分钟**/
    private Double lotteryTime;

    /**用户参与方式：0 指定内容**/
    private Long joinType;

    /**用户参与方式的内容**/
    private String joinContent;

    /**中奖用户兑换方式：0 自动发放**/
    private Long ticketWay;

    /**活动id**/
    private String activityId;

    /**优惠劵id**/
    private String couponId;

    /**删除标识,0:未删除1:已删除**/
    private Integer delFlag;
}
