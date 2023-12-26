package com.wanmi.sbc.order.returnorder.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 * @date 2021/07/29 19:25:59
 */
@Setter
@Getter
public class KingdeeAuditOrder implements Serializable {
    private static final long serialVersionUID = 8211888895908372097L;

    /**
     * 下单时间
     */
    @JSONField(name = "FDate")
    private String FDate;

    /**
     * 组织
     */
    @JSONField(name = "FSaleOrgId")
    private Map FSaleOrgId;

    /**
     * 客户
     */
    @JSONField(name = "FRetcustId")
    private Map FRetcustId;

    /**
     * 销售部门
     */
    @JSONField(name = "FSaleDeptId")
    private Map FSaleDeptId;

    /**
     * 退货单 销售订单
     */
    @JSONField(name = "FBillNo")
    private String orderNumber;

    /**
     * 订单状态
     */
    @JSONField(name = "")
    private String orderState;

    /**
     * 销售员
     */
    @JSONField(name = "FSalerId")
    private Map FSalerId;

    /**
     * 销售订单
     */
    @JSONField(name = "FSaleNum")
    private String fSaleNum;

    /**
     * 备注
     */
    @JSONField(name = "FNote")
    private String fNote;

    /**
     * 仓库
     */
    @JSONField(name = "FStockId")
    private Map FStockId;

    /**
     * 表体
     */
    @JSONField(name = "FEntity")
    private List<AuditOrderEntry> FEntity;
}
