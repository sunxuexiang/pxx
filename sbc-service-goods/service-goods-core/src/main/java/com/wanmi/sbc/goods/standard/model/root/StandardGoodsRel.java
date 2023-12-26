package com.wanmi.sbc.goods.standard.model.root;

import lombok.Data;

import javax.persistence.*;

/**
 * ${DESCRIPTION}
 *
 * @auther ruilinxin
 * @create 2018/03/20 10:04
 */
@Data
@Entity
@Table(name = "standard_goods_rel")
public class StandardGoodsRel {

    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rel_id")
    private Long relId;

    /**
     * SPU标识
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     *属性值id
     */
    @Column(name = "standard_id")
    private String standardId;

    /**
     *店铺id
     */
    @Column(name = "store_id")
    private Long storeId;

}
