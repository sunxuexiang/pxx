package com.wanmi.sbc.live.api.request.stream;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * 聊天请求参数
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IMAppRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 系统消息类型 0 文字消息 1 商品消息 2 优惠卷活动 3直播结束时间 4取消发送优惠劵  5点赞人数 6 在线人数 7福袋 8福袋抽奖结果
     */
    @ApiModelProperty(value = "系统消息类型 0 文字消息 1 商品消息 2 优惠卷活动 3直播结束时间 4取消发送优惠劵  5点赞人数 6 在线人数 7发送福袋 8福袋抽奖结果")
    private Integer type;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播间id")
    private Integer liveRoomId;

    /**
     * 商品spu id
     */
    @ApiModelProperty(value = "商品spu id")
    private String goodsInfoId;

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

    /**
     * 福袋Id
     */
    @ApiModelProperty(value = "福袋id")
    private Integer bagId;
    /**
     * 发送系统消息json格式
     */
    @ApiModelProperty(value = "发送系统消息json格式")
    private String systemMessageJson;

    @ApiModelProperty(value = "点赞个数")
    private Long likeNum;

    /**
     * 直播间id
     */
    @ApiModelProperty(value = "直播记录id")
    private Integer liveId;
}
