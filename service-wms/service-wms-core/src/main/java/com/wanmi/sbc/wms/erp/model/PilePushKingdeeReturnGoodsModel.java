package com.wanmi.sbc.wms.erp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilePushKingdeeReturnGoodsModel implements Serializable {
    private static final long serialVersionUID = -6373283017641196797L;

    /**
     * 囤货单号
     */
    @JSONField(name = "FBillNo")
    private String fBillNo;

    /**
     * 日期
     */
    @JSONField(name = "FDate")
    private String fDate;

    /**
     * 组织
     */
    @JSONField(name = "FSaleOrgId")
    private Map fSaleOrgId;

    /**
     * 客户
     */
    @JSONField(name = "FRetcustId")
    private Map fRetcustId;

    /**
     * 销售部门
     */
    @JSONField(name = "FSaleDeptId")
    private String fSaleDeptId;

    /**
     * 销售员
     */
    @JSONField(name = "FSalerId")
    private Map fSalerId;

    /**
     * 退货原因
     */
    @JSONField(name = "FReturnReason")
    private String fReturnReason;

    /**
     * 囤货单号
     */
    @JSONField(name = "FSaleNum")
    private String fSaleNum;

    /**
     * 备注
     */
    @JSONField(name = "FNote")
    private String fNote;

    /**
     * 银行账号
     */
    @JSONField(name = "FBankAccount")
    private Map fBankAccount;

    /**
     * 收款方式
     */
    @JSONField(name = "FCollectType")
    private String fCollectType;

    /**
     * 结算方式
     */
    @JSONField(name = "FSetType")
    private Map fSetType;

    /**
     * 表体
     */
    @JSONField(name = "FEntity")
    private List<PilePushKingdeeReturnGoodsTableBodyModel> pushReturnGoodsTableBodyRequestList;
}
