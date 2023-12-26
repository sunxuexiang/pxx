package com.wanmi.sbc.order.trade.model.entity.value;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 * @date 2021/07/29 16:23:11
 */
@Setter
@Getter
public class KingdeeSalesOrder implements Serializable {

    private static final long serialVersionUID = 5733006080881008443L;

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
    @JSONField(name = "FCustId")
    private Map FCustId;

    /**
     * 销售订单
     */
    @JSONField(name = "FBillNo")
    private String orderNumber;

    /**
     * 订单状态
     */
    @JSONField(name = "FOrderStatus")
    private Map orderState;

    /**
     * 销售员
     */
    @JSONField(name = "FSalerId")
    private Map FSalerId;

    /**
     * 销售部门 非必
     */
    @JSONField(name = "FSaleDeptId")
    private Map FSaleDeptId;


    /**
     * 运费
     */
    @JSONField(name = "FFreight")
    private BigDecimal FFreight;

    /**
     * 省
     */
    @JSONField(name = "FProvince")
    private Map provinceId;

    /**
     * 市
     */
    @JSONField(name = "FCity")
    private Map cityId;

    /**
     * 区
     */
    @JSONField(name = "FArea")
    private Map areaId;

    /**
     * 联系人
     */
    @JSONField(name = "FContact")
    private String fContact;

    /**
     * 联系电话
     */
    @JSONField(name = "FLinkPhone")
    private String fLinkPhone;

    /**
     * 详细地址(包含省市区）
     */
    @JSONField(name = "FAddress")
    private String detailAddress;

    /**
     * 运输方式
     */
    @JSONField(name = "FTranType")
    private Map fTranType;

    /**
     * 物流公司名称
     */
    @JSONField(name = "FLogName")
    private String fLogName;

    /**
     * 物流电话
     */
    @JSONField(name = "FLogPhone")
    private String fLogPhone;

    /**
     * 物流地址
     */
    @JSONField(name = "FLogAddress")
    private String fLogAddress;

    /**
     * 送货站点
     */
    @JSONField(name = "FLogSite")
    private String fLogSite;

    /**
     * 备注
     */
    @JSONField(name = "FLogNote")
    private String fLogNote;

    /**
     * 仓库
     */
    @JSONField(name = "FStock")
    private Map fStock;

    /**
     * 销售类型 0批发 1零售
     */
    @JSONField(name = "FSaleType")
    private Map fSaleType;

    /**
     * 商家信息
     */
    @JSONField(name = "F_ORA_BASE5")
    private Map fOraBase5;

    /**
     * 表体
     */
    @JSONField(name = "FSaleOrderEntry")
    private List<FSaleOrderEntry> FSaleOrderEntry;
}
