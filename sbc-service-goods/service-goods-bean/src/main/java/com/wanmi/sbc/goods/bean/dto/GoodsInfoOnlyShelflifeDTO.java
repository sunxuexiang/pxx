package com.wanmi.sbc.goods.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author jeffrey
 * @create 2021-08-04 17:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goods_info")
public class GoodsInfoOnlyShelflifeDTO implements Serializable {

    /**
     * 商品SKU编号
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "goods_info_id")
    private String goodsInfoId;


    /**
     * 保质期
     */
    @Column(name = "shelflife")
    private Long shelflife;

    /**
     * 指定销售城市
     */
    @Column(name = "allowed_purchase_area")
    private String allowedPurchaseArea;

}
