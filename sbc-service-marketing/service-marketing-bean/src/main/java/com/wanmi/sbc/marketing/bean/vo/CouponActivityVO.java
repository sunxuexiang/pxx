package com.wanmi.sbc.marketing.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>优惠券活动表</p>
 * author: sunkun
 * Date: 2018-11-23
 */
@ApiModel
@Data
public class CouponActivityVO implements Serializable {

    private static final long serialVersionUID = 512238496026684444L;

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
     * 优惠券活动类型，0全场赠券，1指定赠券，2进店赠券，3注册赠券， 4权益赠券
     */
    @ApiModelProperty(value = "优惠券活动类型")
    private CouponActivityType couponActivityType;

    /**
     * 优惠券赠券满赠条件[购买指定商品赠券]
     * 0 全部满足赠，1 满足任意一个赠
     */
    @ApiModelProperty(value = "优惠券赠券满赠条件[购买指定商品赠券，0 全部满足赠，1 满足任意一个赠")
    private CouponActivityFullType couponActivityFullType;

    /**
     * 是否限制领取优惠券次数，0 每人限领次数不限，1 每人限领N次
     */
    @ApiModelProperty(value = "是否限制领取优惠券次数")
    @Enumerated
    private DefaultFlag receiveType;

    /**
     * 是否暂停 ，1 暂停
     */
    @ApiModelProperty(value = "是否暂停")
    @Enumerated
    private DefaultFlag pauseFlag;

    /**
     * 优惠券被使用后可再次领取的次数，每次仅限领取1张
     */
    @ApiModelProperty(value = "优惠券被使用后可再次领取的次数，每次仅限领取1张")
    private Integer receiveCount;

    /**
     * 生效终端，逗号分隔 0全部,1.PC,2.移动端,3.APP
     */
    @ApiModelProperty(value = "生效终端,以逗号分隔", dataType = "com.wanmi.sbc.marketing.bean.enums.TerminalType")
    private String terminals;

    /**
     * 商户id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;
    
    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String supplierName;
    
    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 是否平台 0店铺 1平台
     */
    @ApiModelProperty(value = "是否平台")
    @Enumerated
    private DefaultFlag platformFlag;

    /**
     * 是否删除标志 0：否，1：是
     */
    @ApiModelProperty(value = "是否已删除")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 关联的客户等级   -1:全部客户 0:全部等级 other:其他等级 ,
     */
    @ApiModelProperty(value = "关联的客户等级", dataType = "com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel")
    private String joinLevel;

    /**
     * 是否平台等级 （1平台（自营店铺属于平台等级） 0店铺）
     */
    @ApiModelProperty(value = "是否平台等级 （1平台（自营店铺属于平台等级） 0店铺）")
    @Enumerated
    private DefaultFlag joinLevelType;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createPerson;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String delPerson;


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
    @ApiModelProperty(value = "注册赠券弹窗图片地址")
    private String imageUrl;

    /**
     * 是否叠加（0：否，1：是）
     */
    @ApiModelProperty(value = "是否叠加（0：否，1：是）")
    private DefaultFlag isOverlap;

    /**
     * 久未下单时间限制（单位 ： 天）
     */
    @ApiModelProperty(value = "久未下单时间限制（单位 ： 天）")
    private Integer longNotOrder;


    @ApiModelProperty(value = "仓库id")
    private Long wareId;

    /***
     * 是否立即发券
     */
    @ApiModelProperty(value = "是否立即发券（仅精准发券使用，0:->否；1：->是）")
    private Integer sendNow;

    /**
     * 赠卷类型 0 普通赠卷 1 直播赠卷
     */
    @ApiModelProperty(value = "赠卷类型")
    private  Integer sendType;

    /**
     *
     */
    @ApiModelProperty(value = "商品id")
    private String goodsInfoId;
}
