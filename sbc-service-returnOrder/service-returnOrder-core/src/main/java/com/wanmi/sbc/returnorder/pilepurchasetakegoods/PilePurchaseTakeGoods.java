package com.wanmi.sbc.returnorder.pilepurchasetakegoods;

import lombok.Data;

import javax.persistence.*;

/**
 * 提货明细
 */
@Data
@Entity
@Table(name = "pile_purchase_take_goods")
public class PilePurchaseTakeGoods {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 屯货明细id
     */
    @Column(name = "action_id")
    private Long actionId;

    /**
     * 提货订单编号
     */
    @Column(name = "take_goods_order_code")
    private String takeGoodsOrderCode;

    /**
     * 囤货订单编号
     */
    @Column(name = "pile_order_code")
    private String pileOrderCode;

    /**
     * 操作数量
     */
    @Column(name = "goods_num")
    private Long goodsNum;

    /**
     * 1 部分提货 2 提货完成 3 已退
     */
    @Column(name = "status")
    private Integer status;

}
