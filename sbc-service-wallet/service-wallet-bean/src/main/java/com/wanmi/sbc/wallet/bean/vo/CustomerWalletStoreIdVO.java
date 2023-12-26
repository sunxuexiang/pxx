package com.wanmi.sbc.wallet.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包VO
 */
@Data
public class CustomerWalletStoreIdVO implements Serializable {
    /**
     * 钱包id
     */
    @ApiModelProperty(value = "钱包id")
    private Long walletId;

    @ApiModelProperty(value = "商家Id")
    private String StoreId;

    /**
     * 商家编号
     * */
    @ApiModelProperty(value = "商家编号")
    private String supplierCode;

    @ApiModelProperty(value = "商家账号")
    private String supplierAccount;

    @ApiModelProperty(value = "商家名称")
    private String supplierName;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "付款方式")
    private String payType;

    @ApiModelProperty(value = "支付单号")
    private String payOrderNo;

    @ApiModelProperty(value = "收款流水号")
    private String  collectionNumber;

    /**
     * 充值金额
     */
    @ApiModelProperty(value = "充值金额")
    private BigDecimal rechargeBalance;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createId;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String updateId;

    /**
     * 删除标识
     */
    @ApiModelProperty(value = "删除标识")
    private DefaultFlag delFlag;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 冻结金额状态 0 可用 1 不可用
     */
    @ApiModelProperty(value = "冻结金额状态 0 可用 1 不可用")
    private  Integer giveBalanceState;

    /**
     * 申请时间(提现申请)
     */
    @ApiModelProperty(value = "申请时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime applyTime;

}
