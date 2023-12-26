package com.wanmi.sbc.order.trade.model.entity.value;

import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家名称
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Supplier implements Serializable{

    private static final long serialVersionUID = 3541731840944835830L;

    /**
     * 商家编码
     */
    private String supplierCode;

    /**
     * 商家名称
     */
    private String supplierName;

    /**
     * 商家id
     */
    private Long supplierId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 使用的运费模板类别(0:店铺运费,1:单品运费)
     */
    private DefaultFlag freightTemplateType;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 代理人Id，用于代客下单
     */
    private String employeeId;

    /**
     * 代理人名称，用于代客下单，相当于OptUserName
     */
    private String employeeName;

    /**
     * 是否平台自营
     */
    private Boolean isSelf;

    /**
     * 商家在erp里的标志（目前只有个XYY）
     */
    private String erpId;
    
    /**
     * 商家类型
     */
    private CompanyType companyType;

    /**
     * 建行商家编号
     */
    private String ccbMerchantNumber;

    /**
     * 收取佣金比例 0.01 - 0.99
     */
    private BigDecimal shareRatio;

    /**
     * 清算日期
     */
    private Integer clearingDay;

    /**
     * @desc  市场id
     * @author shiy  2023/9/21 10:41
    */
    private Long marketId;
    /**
     * @desc  市场名称
     * @author shiy  2023/9/21 10:42
    */
    private String marketName;
    /**
     * @desc 商城Id 
     * @author shiy  2023/9/21 10:42
    */
    private Long tabId;
    /**
     * @desc  商城名称
     * @author shiy  2023/9/21 10:42
    */
    private String tabName;

    /**
     * 商家公司编码 对应 `sbc-customer`.company_info.company_code_new
     */
    private String supplierCodeNew;
}
