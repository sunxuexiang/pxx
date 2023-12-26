package com.wanmi.sbc.customer.service.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * push客户信息给金蝶
 *
 * @author yitang
 * @version 1.0
 */
@Getter
@Setter
public class CustomerKingdeeModel implements Serializable {
    private static final long serialVersionUID = 205270845056630416L;

    /**
     * 客户编码
     */
    @JSONField(name = "FNumber")
    private String fNumber;

    /**
     * 客户名称
     */
    @JSONField(name = "FName")
    private String fName;

    /**
     * 组织
     */
    @JSONField(name = "FCreateOrgId")
    private Map fCreateOrgId;

    /**
     * 省份
     */
    @JSONField(name = "FProvince")
    private Map fProvince;

    /**
     * 城市
     */
    @JSONField(name = "FCity")
    private Map fCity;

    /**
     * 区
     */
    @JSONField(name = "FArea")
    private Map fArea;

    /**
     * 联系人
     */
    @JSONField(name = "FContact")
    private String fContact;

    /**
     * 联系电话
     */
    @JSONField(name = "FTel")
    private String fTel;

    /**
     * 客户分组
     */
    @JSONField(name = "FGroup")
    private Map fGroup;

    /**
     * 通讯地址
     */
    @JSONField(name = "FAddress")
    private String fAddress;

    /**
     * 价目表
     */
    @JSONField(name = "FPriceListId")
    private Map fPriceListId;
}
