package com.wanmi.sbc.returnorder.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 退款
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class KingdeeRefundOrder implements Serializable {
    private static final long serialVersionUID = -4772302638534754980L;

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
     * 退款单
     */
    @JSONField(name = "FBillNo")
    private String orderNumber;

    /**
     * 订单状态
     */
    @JSONField(name = "")
    private String orderState;

    /**
     * 销售员 非必
     */
    @JSONField(name = "FSalerId")
    private Map FSalerId;

    /**
     * 销售订单
     */
    @JSONField(name = "FSaleNum")
    private String FSaleNum;

    @JSONField(name = "FSaleType")
    private Map FSaleType;

    /**
     * 表体
     */
    @JSONField(name = "FREFUNDBILLENTRY")
    private List<KingdeeRefundOrderSettlement> FRECEIVEBILLENTRY;
}
