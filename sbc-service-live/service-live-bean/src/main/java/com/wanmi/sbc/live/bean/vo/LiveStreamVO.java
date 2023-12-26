package com.wanmi.sbc.live.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
/**
 * <p>直播VO</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveStreamVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer liveId;

    /**
     * 直播间id
     */
    private Integer liveRoomId;

    /**
     * 流 ID
     */
    private String streamName;

    /**
     * 直播间名称
     */
    private String roomName;

    /**
     * 封面图片
     */
    private String coverUrl;

    /**
     * 直播设备
     */
    private String sourceChannel;

    /**
     * 主播昵称
     */
    private String anchorName;

    /**
     * 直播间介绍弹幕
     */
    private String notification;

    /**
     * 弹幕是否启用 0: 未启用 1: 启用
     */
    private Integer enableFlag;

    /**
     * 账号id
     */
    private String customerId;

    /**
     * 账号
     */
    private String customerAccount;

    /**
     * 主播头像
     */
    private String faceUrl;

    /**
     * IM群组id
     */
    private String groupId;

    /**
     * 推流地址
     */
    private String pushUrl;

    /**
     * 直播拉流地址
     */
    private String liveUrl;

    /**
     * 直播状态0: 未开始 1: 直播中 2: 已结束
     */
    private Integer liveStatus;

    /**
     * 直播开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    /**
     * 直播结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /**
     * 直播观看人数
     */
    private Integer viewerNumber;

    /**
     * 加购人数
     */
    private Integer addPurchseNumber;

    /**
     * 立购人数
     */
    private Integer oncePurchseNumber;

    /**
     * 优惠卷领取人数
     */
    private Integer couponGetNumber;

    /**
     * 视频回放路径
     */
    private String  mediaUrl;

    /**
     * 店铺id
     */
    private String storeId;

    private String goodsInfoId;

    private String activityId;

    /**
     * 优惠卷id
     */
    private String couponId;

    private String dateTime;

    /**
     * 在线人数
     */
    private Integer onlineNum;

    private Integer likeNum;

    /**
     * 福袋id
     */
    private Integer bagId;

    /**
     * 是否为官方直播间
     */
    private Integer sysFlag;
}
