package com.wanmi.sbc.shopcart.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p></p>
 * author: sunkun
 * Date: 2018-12-07
 */
@Data
@ApiModel
public class PurchaseCountGoodsRequest implements Serializable {

    private static final long serialVersionUID = -1501153196040911906L;

    @ApiModelProperty(value = "客户id")
    @NotBlank
    private String customerId;

    /**
     * 邀请人id-会员id
     */
    @ApiModelProperty(value = "邀请人id")
    String inviteeId;

    /**
     * 公司信息ID
     */
    @ApiModelProperty(value = "公司信息ID")
    private Long companyInfoId;

    /**
     * saas开关
     */
    @ApiModelProperty(value = "saas开关")
    private Boolean saasStatus;

    /**
     * 仓库Id
     */
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;

    /**
     * 散批仓库Id
     */
    @ApiModelProperty(value = "散批仓库Id")
    private Long bulkWareId;
}
