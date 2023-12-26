package com.wanmi.sbc.walletorder.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺信息
 * Created by CHENLI on 2017/11/2.
 */
@ApiModel
@Data
public class WalletStoreVO implements Serializable {

    private static final long serialVersionUID = -5716176566940635853L;

    /**
     * 店铺主键
     */
    @ApiModelProperty(value = "店铺主键")
    private Long storeId;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "商家编号")
    private String storeCode;

    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;

    @ApiModelProperty(value = "商家账号")
    private String storeAccount;

    @ApiModelProperty(value = "充值时间")
    private String rechargeTime;

    @ApiModelProperty(value = "交易流水")
    private String recordNo;

}
