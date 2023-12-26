package com.wanmi.sbc.marketing.api.request.coupon;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityLevelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import javax.validation.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponActivityAddRequest extends BaseRequest {

    private static final long serialVersionUID = -1144976258555148289L;

    @ApiModelProperty(value = "优惠券活动id")
    private String activityId;

    /**
     * 优惠券活动名称
     */
    @ApiModelProperty(value = "优惠券活动名称")
    @NotBlank
    private String activityName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 优惠券活动类型，0全场赠券，1指定赠券，2进店赠券，3注册赠券，4权益赠券，5分销邀新赠券，6积分兑换券，7企业会员注册赠券 8签到获得优惠券
     */
    @ApiModelProperty(value = "优惠券活动类型")
    @NotNull
    private CouponActivityType couponActivityType;

    /**
     * 是否暂停 ，1 暂停
     */
    private DefaultFlag pauseFlag;

    /**
     * 是否限制领取优惠券次数，0 每人限领次数不限，1 每人限领N次
     */
    @ApiModelProperty(value = "是否限制领取优惠券次数")
    @NotNull
    private DefaultFlag receiveType;

    /**
     * 赠卷类型 0 普通赠卷 1 直播赠卷
     */
    @ApiModelProperty(value = "赠卷类型")
    private  Integer sendType=0;
    /**
     * 优惠券被使用后可再次领取的次数，每次仅限领取1张
     */
    @ApiModelProperty(value = "优惠券被使用后可再次领取的次数，每次仅限领取1张")
    private Integer receiveCount;

    /**
     * 生效终端，逗号分隔 0全部,1.PC,2.移动端,3.APP
     * {@link com.wanmi.sbc.marketing.bean.enums.TerminalType}
     */
    @ApiModelProperty(value = "生效终端,以逗号分隔", dataType = "com.wanmi.sbc.marketing.bean.enums.TerminalType")
    private String terminals;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;


    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /**
     * 是否平台 0店铺 1平台
     */
    @ApiModelProperty(value = "是否是平台")
    private DefaultFlag platformFlag;

    /**
     * 关联的客户等级   -2：指定客户 -1:全部客户 0:全部等级 other:其他等级
     * {@link com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel}
     */
    @ApiModelProperty(value = "关联的客户等级", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    @NotBlank
    private String joinLevel;

    /**
     * 是否平台等级 （1平台（自营店铺属于平台等级） 0店铺）
     */
    @ApiModelProperty(value = "是否平台等级")
    private DefaultFlag joinLevelType;


    @ApiModelProperty(value = "操作人")
    private String createPerson;

    @ApiModelProperty(value = "优惠券活动配置信息")
    @Size(max = 10)
    private List<CouponActivityConfigSaveRequest> couponActivityConfigs;

    @ApiModelProperty(value = "签到天数配置信息")
    private List<CouponActivitySignDaysSaveRequest> signDaysSaveRequests;

    /**
     * 订单满额送优惠券配置信息
     */
    @ApiModelProperty(value = "订单满额送优惠券配置信息，单个活动最多配置5层联级")
    @Size(max = 5)
    private List<CouponActivityFullOrderRequest> couponActivityFullOrders;

    /**
     * 购买指定商品赠券商品信息id列表
     */
    @ApiModelProperty(value = "购买指定商品赠券商品信息id列表 单个活动最多配置100个商品")
    @Size(max = 100)
    private List<String> goodsInfoIds;

    /**
     * 购买指定商品赠券促销等级集合
     */
    @ApiModelProperty(value = "购买指定商品赠券促销等级集合")
    @Size(max = 5)
    private List<CouponActivityLevelVO> couponActivityLevelVOS;

    /**
     * 购买指定商品赠券条件，0购所有赠，1购任一赠
     */
    @ApiModelProperty(value = "购买指定商品赠券条件，0购所有赠，1购任一赠")
    private CouponActivityFullType couponActivityFullType;

    /**
     * 促销目标客户范围Ids
     */
    @ApiModelProperty(value = "促销目标客户范围Id列表")
    private List<String> customerScopeIds;

    /**
     * 剩余优惠券组数
     */
    @ApiModelProperty(value = "剩余优惠券组数")
    private Integer leftGroupNum;

    /**
     * 参与成功通知标题
     */
    @ApiModelProperty(value = "参与成功通知标题")
    private String activityTitle;

    /**
     * 参与成功通知描述
     */
    @ApiModelProperty(value = "参与成功通知描述")
    private String activityDesc;

    /**
     * 注册赠券弹窗图片地址
     */
    @ApiModelProperty(value = "注册赠券，弹窗图片地址")
    private String imageUrl;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "是否叠加（0：否，1：是）")
    private DefaultFlag isOverlap = DefaultFlag.NO;


    /**
     * 久未下单时间限制（单位 ： 天）
     */
    @ApiModelProperty(value = "久未下单时间限制（单位 ： 天）")
    private Integer longNotOrder = 0;

    /**
     * 是否立即发券
     */
    @ApiModelProperty(value = "是否立即发券（仅精准发券使用，0:->否；1：->是）")
    private Integer sendNow = 0;

    @Override
    public void checkParam() {
        if (this.couponActivityType.equals(CouponActivityType.BUY_ASSIGN_GOODS_COUPON)) {
            if (Objects.isNull(couponActivityFullType)) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            } else {
                if (this.couponActivityFullType.equals(CouponActivityFullType.FULL_COUNT)) {
                    if (CollectionUtils.isEmpty(this.couponActivityLevelVOS)) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    } else {
                        this.couponActivityLevelVOS.forEach(level -> {
                            if (Objects.isNull(level.getFullCount()) || level.getFullCount() == 0) {
                                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                            }
                        });
                    }
                } else if (this.couponActivityFullType.equals(CouponActivityFullType.FULL_AMOUNT)) {
                    if (CollectionUtils.isEmpty(this.couponActivityLevelVOS)) {
                        throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                    } else {
                        this.couponActivityLevelVOS.forEach(level -> {
                            if (Objects.isNull(level.getFullAmount()) || BigDecimal.ZERO.equals(level.getFullAmount())) {
                                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                            }
                        });
                    }
                }
            }
        }

        if (this.receiveType == DefaultFlag.YES && (this.receiveCount == null || this.receiveCount == 0)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

         //指定赠券
        if (Objects.equals(CouponActivityType.SPECIFY_COUPON, couponActivityType)
                ) {
            //促销目标客户范围Ids
            if (Objects.equals(String.valueOf(MarketingJoinLevel.SPECIFY_CUSTOMER.toValue()), joinLevel)) {
                if (CollectionUtils.isEmpty(customerScopeIds) || customerScopeIds.size() > 1000) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            }
            if (!Objects.equals(String.valueOf(MarketingJoinLevel.SPECIFY_CUSTOMER.toValue()), joinLevel)) {
                if (CollectionUtils.isNotEmpty(customerScopeIds)) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
            }
        }


    }
}
