package com.wanmi.sbc.returnorder.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 线上支付结算信息
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
public class KingdeePayOrderSettlement implements Serializable {
    private static final long serialVersionUID = -2971466818210456381L;

    /**
     * 结算方式
     */
    @JSONField(name = "FSETTLETYPEID",ordinal =1)
    private Map FSETTLETYPEID;

    /**
     * 金额
     */
    @JSONField(name = "FRECTOTALAMOUNTFOR",ordinal = 2)
    private String FRECTOTALAMOUNTFOR;

    /**
     * 银行账号  非必
     */
    @JSONField(name = "FACCOUNTID",ordinal = 3)
    private Map FACCOUNTID;

    /**
     * 备注
     */
    @JSONField(name = "FNOTE",ordinal = 4)
    private String FNOTE;

    /**
     * 销售订单号
     */
    @JSONField(name = "F_ora_YDDH",ordinal =5)
    private String f_ora_YDDH;

    /**
     * 预收款
     */
    @JSONField(name = "FPURPOSEID",ordinal = 6)
    private Map FPURPOSEID;
}
