package com.wanmi.sbc.goods.brand.model.root;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品新品推荐实体类
 */
@Data
@Entity
@Table(name = "goods_brand_recommend")
public class GoodsBrandRecommend implements Serializable {

    /**
     * 品牌推荐id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_brand_recommend_id")
    private Long goodsBrandRecommendId;

    /**
     * 品牌id
     */
    @Column(name = "brand_id")
    private Long brandId;

    /**
     * 上下架的时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "added_time")
    private LocalDateTime addedTime;


    /**
     * 上下架状态,0:下架1:上架
     */
    @Column(name = "added_flag")
    private Integer addedFlag;


    /**
     * 删除标识,0:未删除1:已删除
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 创建时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 分类名称显示状态：0不显示，1显示
     */
    @Column(name = "name_status")
    private Integer nameStatus;
}
