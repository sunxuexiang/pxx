package com.wanmi.sbc.order.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * 退款
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class KingdeeRefundOrderSettlement implements Serializable {

    private static final long serialVersionUID = 6871960228752611991L;

    /**
     * 结算方式
     */
    @JSONField(name = "FSETTLETYPEID",ordinal = 1)
    private Map FSETTLETYPEID;

    /**
     * 金额
     */
    @JSONField(name = "FREFUNDAMOUNTFOR",ordinal = 2)
    private String FRECTOTALAMOUNTFOR;

    /**
     * 备注
     */
    @JSONField(name = "FNOTE",ordinal = 3)
    private String FNOTE;

    /**
     * 银行账号  非必
     */
    @JSONField(name = "FACCOUNTID",ordinal = 4)
    private Map FACCOUNTID;
}
