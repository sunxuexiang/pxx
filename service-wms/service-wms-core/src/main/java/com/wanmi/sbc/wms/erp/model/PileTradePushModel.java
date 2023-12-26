package com.wanmi.sbc.wms.erp.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 推金蝶囤货销售订单
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PileTradePushModel implements Serializable {
    private static final long serialVersionUID = -150782689099248783L;

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
    @JSONField(name = "FCustId")
    private Map fCustId;

    /**
     * 销售部门（不传）
     */
    @JSONField(name = "FSaleDeptId")
    private String fSaleDeptId;

    /**
     * 销售员
     */
    @JSONField(name = "FSalerId")
    private Map fSalerId;

    /**
     * 订单状态
     */
    @JSONField(name = "FOrderStatus")
    private String fOrderStatus;

    /**
     * 联系电话
     */
    @JSONField(name = "FLinkPhone")
    private String fLinkPhone;

    /**
     * 联系人
     */
    @JSONField(name = "FContact")
    private String fContact;

    /**
     * 仓库
     */
    @JSONField(name = "FStock")
    private String fStock;

    /**
     * 备注
     */
    @JSONField(name = "FLogNote")
    private String fLogNote;

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
    @JSONField(name = "FSaleOrderEntry")
    private List<PileTradePushtableBodyModel> pushTableBodyRequestList;
}
