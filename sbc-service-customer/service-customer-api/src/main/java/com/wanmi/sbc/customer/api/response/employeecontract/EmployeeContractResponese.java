package com.wanmi.sbc.customer.api.response.employeecontract;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeContractResponese implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

    @ApiModelProperty("合同表ID")
    private String userContractId;
    @ApiModelProperty("合同URL")
    private String contractUrl;
    @ApiModelProperty("员工ID")
    private String employeeId;
    @ApiModelProperty("客户ID")
    private String customerId;
    @ApiModelProperty("员工姓名")
    private String employeeName;
    @ApiModelProperty("商户名称")
    private String supplierName;

    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    @ApiModelProperty("签署状态")
    private int status;
    @ApiModelProperty("交易号")
    private String transactionNo;
    @ApiModelProperty("签署合同ID")
    private String contractId;
    @ApiModelProperty("上传的合同照片路径")
    private String imgUrl;
    @ApiModelProperty("招商经理名称")
    private String investmentManager;
    @ApiModelProperty("招商经理ID")
    private String investemntManagerId;
    @ApiModelProperty("签署类型：0：线下，1：线上")
    private Integer signType;
    @ApiModelProperty("企业性质")
    private Integer isPerson;
    @ApiModelProperty(value = "移动端用户账号ID")
    private String appCustomerId;
    @ApiModelProperty(value = "批发市场")
    private String tabRelationName;

}
