package com.wanmi.sbc.live.activity.model.root;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class LiveStreamActivity {
    private static final long serialVersionUID = 1791727470875246381L;

    private Integer id;

    /**
     * 直播间ID
     */
    private Integer liveRoomId;

    /**
     * 直播id
     */
    private Integer liveId;

    /**
     * 优惠卷活动id
     */
    private String activityId;

    /**
     * 优惠券Id
     */
    private String couponId;

    /**
     * 赠送标识,0:未赠送1:赠送中 2 已结束
     */
    private Integer presentFlag;

    /**
     * 删除标识,0:未删除1:已删除
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
