package com.wanmi.sbc.goods.price.model.root;


import com.wanmi.sbc.goods.bean.enums.PriceType;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 商品订货区间价格实体
 * Created by dyt on 2017/4/17.
 */
@Data
@Entity
@Table(name = "goods_interval_price")
public class GoodsIntervalPrice {

    /**
     * 订货区间ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interval_price_id")
    private Long intervalPriceId;

    /**
     * 商品ID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 订货区间
     */
    @Column(name = "count")
    private Long count;

    /**
     * 订货价
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 商品ID
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 类型
     */
    @Column(name = "type")
    @Enumerated
    private PriceType type;
}
