package com.wanmi.sbc.marketing.coupon.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author: songhanlin
 * @Date: Created In 11:15 AM 2018/9/12
 * @Description: 优惠券活动表
 */
@Entity
@Table(name = "coupon_activity")
@Data
public class CouponActivity {

    /**
     * 优惠券活动id
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 优惠券活动名称
     */
    @Column(name = "activity_name")
    private String activityName;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     * 优惠券类型，0全场赠券，1指定赠券，2进店赠券，3注册赠券，4权益赠券，5分销邀新赠券，6积分兑换券，7企业会员注册赠券，8签到
     * 9购买指定商品赠券
     */
    @Column(name = "activity_type")
    @Enumerated
    private CouponActivityType couponActivityType;

    /**
     * 领取类型，0 每人限领次数不限，1 每人限领N次
     */
    @Column(name = "receive_type")
    @Enumerated
    private DefaultFlag receiveType;

    /**
     * 优惠券赠券满赠条件[购买指定商品赠券]
     * 0 全部满足赠，1 满足任意一个赠
     */
    @Column(name = "full_type")
    @Enumerated
    private CouponActivityFullType couponActivityFullType;

    /**
     * 赠卷类型
     * 0 普通赠卷 1 直播赠卷
     */
    @Column(name = "send_type")
    private  Integer sendType;
    /**
     * 是否暂停 ，1 暂停
     */
    @Column(name = "pause_flag")
    @Enumerated
    private DefaultFlag pauseFlag;

    /**
     * 优惠券被使用后可再次领取的次数，每次仅限领取1张
     */
    @Column(name = "receive_count")
    private Integer receiveCount;

    /**
     * 生效终端，逗号分隔 0全部,1.PC,2.移动端,3.APP
     */
    @Column(name = "terminals")
    private String terminals;

    /**
     * 商户id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 是否平台 0店铺 1平台
     */
    @Column(name = "platform_flag")
    @Enumerated
    private DefaultFlag platformFlag;

    /**
     * 是否删除标志 0：否，1：是
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 关联的客户等级  -2指定客户 -1:全部客户 0:全部等级 other:其他等级 ,
     */
    @Column(name = "join_level")
    private String joinLevel;


    /**
     * 是否平台等级 （1平台（自营店铺属于平台等级） 0店铺）
     */
    @Column(name = "join_level_type")
    private DefaultFlag joinLevelType;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 修改时间
     */
    @Column(name = "update_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 删除时间
     */
    @Column(name = "del_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 删除人
     */
    @Column(name = "del_person")
    private String delPerson;

    /**
     * 剩余优惠券组数
     */
    @Column(name = "left_group_num")
    private Integer leftGroupNum;

    /**
     * 参与成功通知标题
     */
    @Column(name = "activity_title")
    private String activityTitle;

    /**
     * 参与成功通知描述
     */
    @Column(name = "activity_desc")
    private String activityDesc;

    /**
     * 注册赠券弹窗图片地址
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * 是否叠加（0：否，1：是）
     */
    @Enumerated
    @Column(name = "is_overlap")
    private DefaultFlag isOverlap = DefaultFlag.NO;


    /**
     * 久未下单时间限制（单位 ： 天）
     */
    @Column(name = "long_not_order")
    private Integer longNotOrder;

    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /***
     * 是否立即发券
     */
    @Column(name = "send_now")
    private Integer sendNow;


}
