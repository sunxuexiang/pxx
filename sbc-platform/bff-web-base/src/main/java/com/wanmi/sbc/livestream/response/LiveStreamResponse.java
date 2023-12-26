package com.wanmi.sbc.livestream.response;

import com.wanmi.sbc.common.annotation.CanEmpty;
import com.wanmi.sbc.live.bean.vo.LiveStreamVO;
import com.wanmi.sbc.marketing.bean.enums.FullBuyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class LiveStreamResponse implements Serializable {
    private static final long serialVersionUID = -2596395758191032675L;

    /**
     * 是否有商品
     */
    @ApiModelProperty(value = "是否有商品 0 无 1有")
    private Integer isHaveGoods;

    /**
     * 是否有优惠卷
     */
    @ApiModelProperty(value = "是否有优惠卷 0 无 1有")
    private Integer isHaveCoupon;
    /**
     * 直播详情
     */
    @ApiModelProperty(value = "直播详情")
    public LiveStreamVO liveStreamVO;

    /**
     * 优惠券活动id
     */
    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 优惠券活动名称
     */
    @ApiModelProperty(value = "优惠券活动名称")
    private String activityName;

    /**
     * 优惠券主键Id
     */
    @ApiModelProperty(value = "优惠券主键Id")
    private String couponId;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;


    /**
     * 优惠券面值
     */
    @ApiModelProperty(value = "优惠券面值")
    private BigDecimal denomination;

    /**
     * 优惠券说明
     */
    @ApiModelProperty(value = "优惠券说明")
    private String couponDesc;

    @ApiModelProperty(value = "购满类型 0：无门槛，1：满N元可使用")
    private Integer fullBuyType;

    /**
     * 购满多少钱
     */
    @ApiModelProperty(value = "购满多少钱")
    private BigDecimal fullBuyPrice;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品spu id")
    private String goodsInfoId;

    /**
     * 商品图片
     */
    @ApiModelProperty(value = "商品图片")
    private String goodsInfoImg;

    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    /**
     * 市场价
     */
    @ApiModelProperty(value = "市场价")
    @CanEmpty
    private BigDecimal marketPrice;

    @ApiModelProperty(value = " 0：批发，1：零售")
    private Integer goodsType;
    /**
     * 点赞次数
     */
    @ApiModelProperty(value = "点赞次数")
    private Integer likeNum;

    @ApiModelProperty(value = "在线人数")
    private Integer onlineNum;

    /**
     * 是否有优惠卷
     */
    @ApiModelProperty(value = "是否有福袋 0 无 1有")
    private Integer isHaveBag;

    /**
     * 福袋id
     */
    @ApiModelProperty(value = "福袋id")
    private Integer bagId;

    @ApiModelProperty(value = "福袋名称")
    private String bagName;
    /**
     * 福袋指定内容
     */
    @ApiModelProperty(value = "福袋指定内容")
    private String specifyContent;

    @ApiModelProperty(value = "开奖状态 0 未开奖 1开奖中 2已开奖")
    private Integer lotteryStatus;

    @ApiModelProperty(value = "抽奖倒计时")
    private Long countdown;

    /**中奖名额**/
    @ApiModelProperty(value = "中奖名额")
    private Long winningNumber;
    /**
     * 是否参与抽奖
     */
    @ApiModelProperty(value = "是否参与抽奖 0 否 1是")
    private Integer isJoin;

    @ApiModelProperty(value = "主播状态 0 离开 1回来")
    private Integer hostStatus;
}
