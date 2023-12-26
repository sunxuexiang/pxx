package com.wanmi.sbc.goods.goodsattributekey.root;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
/**
 * 商品属性实体关联类
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Entity
@Table(name = "goods_attribute_key")
public class GoodsAttributeKey implements Serializable {
    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_attribute_key_id")
    private String attributeId;
    /**
     * 属性id
     */
    @Column(name = "goods_attribute_id")
    private String goodsAttributeId;
  /**
     * 属性 名称
     */
    @Column(name = "goods_attribute_value")
    private String goodsAttributeValue;
    /**
     * 商品明细id
     */
    @Column(name = "goods_info_id")
    private String goodsInfoId;

    /**
     * 商品Id
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 会员ID
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "goods_attribute_id", insertable = false, updatable = false)
    @JsonBackReference
    private GoodsAttribute attribute;
}
