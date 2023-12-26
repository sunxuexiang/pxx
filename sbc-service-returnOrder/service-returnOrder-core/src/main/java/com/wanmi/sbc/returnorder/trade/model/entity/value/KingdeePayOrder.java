package com.wanmi.sbc.returnorder.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * push金蝶实例
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
public class KingdeePayOrder implements Serializable {
    private static final long serialVersionUID = -2318285677271072624L;
    /**
     * 时间
     */
    @JSONField(name = "FDate")
    private String FDate;

    /**
     * 客户
     */
    @JSONField(name = "FCustId")
    private Map FCustId;

    /**
     * 收款组织 付款组织
     */
    @JSONField(name = "FPAYORGID")
    private Map FPAYORGID;


    /**
     * 销售部门 非必
     */
    @JSONField(name = "FSaleDeptId")
    private Map FSaleDeptId;

    /**
     * 销售订单
     */
    @JSONField(name = "FBillNo")
    private String orderNumber;

    /**
     * 销售员 非必
     */
    @JSONField(name = "FSalerId")
    private Map FSalerId;

    /**
     * 销售单号 非必
     */
    @JSONField(name = "FSaleNum")
    private String FSaleNum;

    /**
     * 收款方式
     */
    @JSONField(name = "FColType")
    private String fColType;

    /**
     * 表体
     */
    @JSONField(name = "FRECEIVEBILLENTRY")
    private List<KingdeePayOrderSettlement> FRECEIVEBILLENTRY;
}
