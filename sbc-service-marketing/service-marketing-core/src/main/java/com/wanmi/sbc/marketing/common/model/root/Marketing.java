package com.wanmi.sbc.marketing.common.model.root;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingJoinLevel;
import com.wanmi.sbc.marketing.bean.enums.MarketingScopeType;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullDiscountLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullReductionLevelVO;
import com.wanmi.sbc.marketing.common.BaseBean;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.reduction.model.entity.MarketingFullReductionLevel;
import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 营销规则实体类
 */
@Data
@Entity
@Table(name = "marketing")
public class Marketing extends BaseBean {

    /**
     * 促销Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "marketing_id")
    private Long marketingId;

    /**
     * 促销名称
     */
    @Column(name = "marketing_name")
    private String marketingName;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     */
    @Column(name = "marketing_type")
    @Enumerated
    private MarketingType marketingType;

    /**
     * 促销子类型 0：满金额减 1：满数量减 2:满金额折 3:满数量折 4:满金额赠 5:满数量赠
     */
    @Column(name = "sub_type")
    @Enumerated
    private MarketingSubType subType;

    /**
     * 开始时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "begin_time")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 参加促销类型 0：全部货品 1：货品 2：品牌 3：分类
     */
    @Column(name = "scope_type")
    private MarketingScopeType scopeType;

    /**
     * 参加会员 0:全部等级  other:其他等级
     */
    @Column(name = "join_level")
    private String joinLevel;

    /**
     * 是否是商家，0：商家，1：boss
     */
    @Column(name = "is_boss")
    @Enumerated
    private BoolFlag isBoss;

    /**
     * 商家Id  0：boss,  other:其他商家
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 删除标记  0：正常，1：删除
     */
    @Column(name = "del_flag")
    @Enumerated
    private DeleteFlag delFlag;

    /**
     * 是否暂停 0：正常，1：暂停
     */
    @Column(name = "is_pause")
    @Enumerated
    private BoolFlag isPause;

    /**
     * 是否叠加, 0：否， 1：是
     */
    @Column(name = "is_overlap")
    @Enumerated
    private BoolFlag isOverlap;

    /**
     * 是否追加名称[同种活动可跨单品]（0：否，1：是）
     */
    @Column(name = "is_add_marketing_name")
    @Enumerated
    private BoolFlag isAddMarketingName;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @Column(name = "create_person")
    private String createPerson;

    /**
     * 修改时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @Column(name = "update_person")
    private String updatePerson;

    /**
     * 删除时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "delete_time")
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @Column(name = "delete_person")
    private String deletePerson;

    /**
     * 当前活动是否有必选商品
     */
    @Column(name = "whether_choice")
    private BoolFlag whetherChoice;

    /**
     * 套装价格
     */
    @Column(name = "suit_price")
    private BigDecimal suitPrice;

    /**
     * 购买数量
     */
    @Column(name = "suit_buy_num")
    private Integer suitBuyNum;

    /**
     * 每套每人限制购买数量
     */
    @Column(name = "suit_limit_num")
    private Integer suitLimitNum;

    /**
     * 优惠标签
     */
    @Column(name = "suit_coupon_label")
    private String suitCouponLabel;

    /**
     * 优惠文案
     */
    @Column(name = "suit_coupon_desc")
    private String suitCouponDesc;

    /**
     * 营销头图
     */
    @Column(name = "suit_marketing_banner")
    private String suitMarketingBanner;

    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 顶部头图
     */
    @Column(name = "suit_top_image")
    private String suitTopImage;

    /**
     * 关联商品
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketing_id", referencedColumnName = "marketing_id",insertable = false, updatable = false)
    @JsonIgnore
    private List<MarketingScope> marketingScopeList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketing_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<MarketingSuitDetail> marketingSuitDetialList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketing_id",referencedColumnName = "marketing_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<MarketingFullDiscountLevel> marketingFullDiscountLevels;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketing_id", referencedColumnName = "marketing_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<MarketingFullGiftLevel> marketingFullGiftLevels;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketing_id", referencedColumnName = "marketing_id", insertable = false, updatable = false)
    @JsonIgnore
    private List<MarketingFullReductionLevel> marketingFullReductionLevels;


    /**
     * 真正的终止时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "real_end_time")
    private LocalDateTime realEndTime;

    /**
     * 是否终止 0：未终止，1：已终止
     */
    @Column(name = "termination_flag")
    @Enumerated
    private BoolFlag terminationFlag;

    /**
     * joinLevel的衍射属性，获取枚举
     */
    @Transient
    private MarketingJoinLevel marketingJoinLevel;

    /**
     * joinLevel的衍射属性，marketingJoinLevel == LEVEL_LIST 时，可以获取对应的等级集合
     */
    @Transient
    private List<Long> joinLevelList;


    /**
     * 是否为草稿 0：是，1：否
     */
    @Column(name = "is_draft",nullable = false)
    @Enumerated
    private BoolFlag isDraft;


    public MarketingJoinLevel getMarketingJoinLevel() {
        if(joinLevel.equals("0")){
            return MarketingJoinLevel.ALL_LEVEL;
        }else if(joinLevel.equals("-1")){
            return MarketingJoinLevel.ALL_CUSTOMER;
        }else{
            return MarketingJoinLevel.LEVEL_LIST;
        }
    }

    public List<Long> getJoinLevelList() {
        return Arrays.stream(joinLevel.split(",")).map(Long::parseLong).collect(Collectors.toList());
    }
}
