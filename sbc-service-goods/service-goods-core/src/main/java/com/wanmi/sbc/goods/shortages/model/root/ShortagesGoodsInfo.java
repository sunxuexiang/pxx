package com.wanmi.sbc.goods.shortages.model.root;

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
 * 商品SKU实体类
 * Created by dyt on 2017/4/11.
 */
@Data
@Entity
@Table(name = "shortages_goods_info")
public class ShortagesGoodsInfo implements Serializable {
    private static final long serialVersionUID = 1416326592228094088L;

    @Id
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 商品SKU名称
     */
    @Column(name = "goods_info_name")
    private String goodsInfoName;

    /**
     * 商品SKU编码
     */
    @Column(name = "goods_info_no")
    private String goodsInfoNo;

    /**
     * erpSKU编码
     */
    @Column(name = "erp_goods_info_no")
    private String erpGoodsInfoNo;

    /**
     * 商品分类ID
     */
    @Column(name = "cate_id")
    private Long cateId;

    /**
     * 品牌ID
     */
    @Column(name = "brand_id")
    private Long brandId;

    /**
     * 创建时间
     */
    @Column(name = "check_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime checkTime;

}
