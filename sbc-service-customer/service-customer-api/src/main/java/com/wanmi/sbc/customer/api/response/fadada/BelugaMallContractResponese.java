package com.wanmi.sbc.customer.api.response.fadada;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@ApiModel
@Data
@NoArgsConstructor
public class BelugaMallContractResponese {

    @ApiModelProperty("合同ID")
    private String belugaMallId;
    @ApiModelProperty("承运商用户ID")
    private String belugaUser;
    @ApiModelProperty("公司名称")
    private String companyName;
    @ApiModelProperty("签署时期")
    private String signingDate;
    @Column(name = "status")
    private int status;
    @ApiModelProperty("电话")
    private String phoneNumber;
    @ApiModelProperty("合同ID")
    private String contractId;
    @ApiModelProperty("合同下载浏览地址")
    private String contractUrl;
}
