package com.wanmi.sbc.live.api.response.activity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveBagLogInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**live_bag_id**/
    private Long bagId;

    /**福袋名称**/
    @ApiModelProperty(value = "福袋名称")
    private String bagName;

    /**中奖名额**/
    @ApiModelProperty(value = "中奖名额")
    private Long winningNumber;

    /**开奖时间：分钟**/
    @ApiModelProperty(value = "开奖时间：分钟")
    private Double lotteryTime;
    /**
     * 福袋指定内容
     */
    @ApiModelProperty(value = "福袋指定内容")
    private String specifyContent;

    @ApiModelProperty(value = "参与人数")
    private Integer joinNum;

    @ApiModelProperty(value = "抽奖倒计时")
    private Long countdown;
    /**
     * 是否参与抽奖
     */
    @ApiModelProperty(value = "是否参与抽奖 0 否 1是")
    private Integer isJoin;
    /**
     * 是否中奖 0 否 1是
     */
    @ApiModelProperty(value = "是否中奖 0 否 1是")
    private Integer isWin;

    @ApiModelProperty(value = "开奖状态 0 未开奖 1开奖中 2已开奖")
    private Integer lotteryStatus;


    /**
     * 优惠券主键Id
     */
    @ApiModelProperty(value = "优惠券主键Id")
    private String couponId;

    /**
     * 是否有优惠卷
     */
    @ApiModelProperty(value = "是否有优惠卷 0 无 1有")
    private Integer isHaveCoupon;

    /**
     * 优惠券名称
     */
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "中奖用户id")
    private String customerIds;

    @ApiModelProperty(value = "中奖用户列表")
    private List<Map<String,String>> customerDetail;
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
}
