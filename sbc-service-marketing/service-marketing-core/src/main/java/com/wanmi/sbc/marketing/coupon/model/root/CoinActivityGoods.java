package com.wanmi.sbc.marketing.coupon.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 指定商品赠金币
 * @author Administrator
 */
@Entity
@Table(name = "coin_activity_goods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoinActivityGoods {

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private String activityId;

    /**
     * 商品信息skuid
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 是否终止
     */
    @Column(name = "termination_flag")
    @Enumerated
    private BoolFlag terminationFlag=BoolFlag.NO;

    /**
     * 终止时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "termination_time")
    private LocalDateTime terminationTime;


    /**
     * 是否显示
     * 0 显示 1 不显示
     */
    @Column(name = "display_type")
    private Integer displayType=0;


}
