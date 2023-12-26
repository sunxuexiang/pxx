package com.wanmi.sbc.customer.api.request.employeecontract;

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

import javax.persistence.Convert;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeContractSaveRequest implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

    private String userContractId;
    private String contractUrl;
    private String employeeId;
    private String customerId;
    private String transactionNo;
    private int status;
    private String contractId;
    private String supplierName;
    private String employeeName;
    private Integer isPerson;
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    @ApiModelProperty(value = "线下签署合同上传来的照片(多个用逗号分隔)")
    private String imgUrl;

    @ApiModelProperty(value = "0: 线上。1:线下")
    private Integer signType;

    @ApiModelProperty(value = "招商经理")
    private String investmentManager;

    @ApiModelProperty(value = "招商经理ID")
    private String investemntManagerId;
    @ApiModelProperty(value = "移动端用户账号ID")
    private String appCustomerId;
    @ApiModelProperty(value = "appId")
    private String appId;

    @Override
    public String toString() {
        return "EmployeeContractSaveRequest{" +
                "userContractId=" + userContractId +
                ", contractUrl='" + contractUrl + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", status=" + status +
                ", contractId='" + contractId + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", createTime=" + createTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", signType=" + signType +
                ", investmentManager='" + investmentManager + '\'' +
                ", investemntManagerId='" + investemntManagerId + '\'' +
                '}';
    }
}
