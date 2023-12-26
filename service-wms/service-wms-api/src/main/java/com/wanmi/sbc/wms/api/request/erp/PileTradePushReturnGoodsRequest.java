package com.wanmi.sbc.wms.api.request.erp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 推金蝶退货表头
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class PileTradePushReturnGoodsRequest implements Serializable {

    private static final long serialVersionUID = 5350092033634144696L;

    /**
     * 囤货退单号
     */
    private String fBillNo;

    /**
     * 日期
     */
    private String fDate;

    /**
     * 组织
     */
    private Map fSaleOrgId;

    /**
     * 客户
     */
    private Map fCustId;

    /**
     * 销售部门(不传)
     */
    private String fSaleDeptId;

    /**
     * 销售员
     */
    private Map fSalerId;

    /**
     * 退货原因
     */
    private String fReturnReason;

    /**
     * 囤货单号
     */
    private String fSaleNum;

    /**
     * 备注
     */
    private String fNote;

    /**
     * 银行账号
     */
    private Map fBankAccount;

    /**
     * 收款方式
     */
    private String fCollectType;

    /**
     * 结算方式
     */
    private Map fSetType;

    /**
     * 表体
     */
    private List<PileTradePushReturnGoodsTableBodyRequest> pushReturnGoodsTableBodyRequestList;

    /**
     * 登录token
     */
    private String loginToken;

    /**
     * 是否使用线程池
     */
    private Boolean threadPool;

}
