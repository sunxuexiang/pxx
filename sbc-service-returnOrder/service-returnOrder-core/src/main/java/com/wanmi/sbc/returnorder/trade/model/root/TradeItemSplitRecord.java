package com.wanmi.sbc.returnorder.trade.model.root;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @description  订单主物料拆分数量表
 * @author  shiy
 * @date    2023/4/8 8:58
 * @params
 * @return
*/
@Data
@Entity
@Table(name = "trade_item_split_record")
public class TradeItemSplitRecord {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 订单号
     */
    @Column(name = "trade_no")
    private String tradeNo;

    /**
     * 子物料sku ID
     */
    @Column(name = "child_sku_id")
    private String childSkuId;

    /**
     * 子物料仓库ID
     */
    @Column(name = "child_ware_id")
    private Long childWareId;

    /**
     * 子物料订单数量
     */
    @Column(name = "child_order_num")
    private BigDecimal childOrderNum;

    /**
     * 子物料货品库存
     */
    @Column(name="child_stock")
    private BigDecimal childStock;

    /**
     * 主物料sku ID
     */
    @Column(name = "parent_sku_id")
    private String parentSkuId;

    /**
     * 主物料仓库ID
     */
    @Column(name = "parent_ware_id")
    private Long parentWareId;

    /**
     * 主物料货品库存
     */
    @Column(name = "parent_stock")
    private BigDecimal parentStock;

    /**
     * 相对主物料的换算率
     */
    @Column(name = "main_add_step")
    private BigDecimal mainAddStep;

    /**
     * 转换父物料库存
     */
    @Column(name = "exchange_parent_num")
    private BigDecimal exchangeParentNum;

    /**
     * 回退标记
     */
    @Column(name = "back_flag")
    private Integer backFlag;
}
