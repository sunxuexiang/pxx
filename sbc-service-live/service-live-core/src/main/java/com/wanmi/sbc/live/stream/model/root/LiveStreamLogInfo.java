package com.wanmi.sbc.live.stream.model.root;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 直播记录商品，活动存储
 */
@Data
@ToString(callSuper = true)
public class LiveStreamLogInfo implements Serializable {

    private static final long serialVersionUID = 1791727470875246381L;

    private Integer liveId;
    /**
     * 商品直播记录
     */
    private String goodsInfoIds;

    /**
     * 优惠劵活动记录
     */
    private String activityIds;

    private Date createTime;


}