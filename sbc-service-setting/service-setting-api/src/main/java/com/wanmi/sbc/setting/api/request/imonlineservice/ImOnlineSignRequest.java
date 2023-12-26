package com.wanmi.sbc.setting.api.request.imonlineservice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-06 14:04
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImOnlineSignRequest implements Serializable {
    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号")
    private String customerServiceAccount;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "客服账号Id")
    private String customerId;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "商户id")
    private long companyInd;

    /**
     * 客服账号
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;
}
