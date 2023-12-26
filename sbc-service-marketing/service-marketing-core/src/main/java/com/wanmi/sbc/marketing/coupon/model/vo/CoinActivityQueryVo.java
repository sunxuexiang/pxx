package com.wanmi.sbc.marketing.coupon.model.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityFullType;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/28 9:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoinActivityQueryVo {

    /**
     * 优惠券活动id
     */
    private String activityId;

    /**
     * 优惠券活动名称
     */
    private String activityName;

    /**
     * 促销类型 0：满减 1:满折 2:满赠
     */

    private CoinActivityType activityType ;

    /**
     * 开始时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    /**
     *
     * 0 任买返
     */
    private CoinActivityFullType coinActivityFullType;


    /**
     * 商户id
     */
    private Long storeId;

    /**
     * 是否删除标志 0：否，1：是
     */
    private DeleteFlag delFlag;

    /**
     * 关联的客户等级  -2指定客户 -1:全部客户 0:全部等级 other:其他等级 ,
     */
    private String joinLevel;

    /**
     * 是否叠加（0：否，1：是）
     */
    private DefaultFlag isOverlap;



    private BigDecimal coinNum;

    private BoolFlag terminationFlag;

    private String goodsInfoId;

    /**
     * 是否显示
     * 0 显示 1 不显示
     */
    @ApiModelProperty(value = "是否显示")
    private Integer displayType;
}
