package com.wanmi.sbc.live.stream.model.root;

import com.wanmi.sbc.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 直播
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class LiveStream extends BaseEntity {

    private static final long serialVersionUID = 1791727470875246381L;

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
     * 主播昵称
     */
    private String anchorName;

    /**
     * 直播间介绍弹幕
     */
    private String notification;

    /**
     * 直播设备
     */
    private String sourceChannel;

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
    private Date startTime;

    /**
     * 直播结束时间
     */
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

    /**
     * 商品spuid
     */
    private String goodsInfoId;

    /**
     * 活动id
     */
    private String activityId;

    /**
     * 优惠卷id
     */
    private String couponId;

    /**
     * 福袋id
     */
    private Integer bagId;

    /**
     * 是否为官方直播间
     */
    private Integer sysFlag;

    /**
     * 推断流错误码
     */
    private Integer errcode;

    /**
     * 推断流错误描述
     */
    private String errmsg;

    /**
     * 0 直播断流 1 直播推流
     */
    private Integer eventType;

    /**
     * 事件消息产生的 UNIX 时间戳
     */
    private Integer eventTime;

    /** 直播间类型：0：非自营直播间；1、自营直播间；2：平台直播间 */
    private Integer roomType;
}
