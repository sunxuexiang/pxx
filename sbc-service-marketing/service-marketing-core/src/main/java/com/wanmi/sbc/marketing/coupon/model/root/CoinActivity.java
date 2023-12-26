package com.wanmi.sbc.marketing.coupon.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 指定商品赠金币
 * @author Administrator
 */
@Entity
@Table(name = "coin_activity")
@Data
public class CoinActivity {

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
     * 促销类型 0：满减 1:满折 2:满赠
     */
    @Column(name = "activity_type")
    @Enumerated
    private CoinActivityType activityType = CoinActivityType.SPECIFIC;

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
     *
     * 0 任买返
     */
    @Column(name = "full_type")
    @Enumerated
    private CoinActivityFullType coinActivityFullType;

    /**
     * 是否暂停 ，1 终止
     */
    @Column(name = "pause_flag")
    @Enumerated
    private BoolFlag pauseFlag = BoolFlag.NO;

    /**
     * 商户id
     */
    @Column(name = "store_id")
    private Long storeId;

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
     * 是否叠加（0：否，1：是）
     */
    @Enumerated
    @Column(name = "is_overlap")
    private DefaultFlag isOverlap = DefaultFlag.YES;

    /**
     * 仓库id
     */
    @Column(name = "ware_id")
    private Long wareId;

    /**
     * 鲸币数量
     */
    @Column(name = "coin_num")
    private BigDecimal coinNum;

    @Column(name = "termination_flag")
    @Enumerated
    private BoolFlag terminationFlag=BoolFlag.NO;

    /**
     * 真正的终止时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "real_end_time")
    private LocalDateTime realEndTime;

}
