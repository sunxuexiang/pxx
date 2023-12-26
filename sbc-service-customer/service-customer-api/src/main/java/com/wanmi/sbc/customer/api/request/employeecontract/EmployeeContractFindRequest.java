package com.wanmi.sbc.customer.api.request.employeecontract;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeContractFindRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

    @ApiModelProperty("商家账号")
    private String supplierName;
    @ApiModelProperty("招商经理")
    private String investmentManager;
    @ApiModelProperty("招商经理ID")
    private String investmentManagerId;
    @ApiModelProperty(value = "签署方式0: 线上，1：线下")
    private Integer signType;
    @ApiModelProperty(value = "签署状态")
    private Integer status;
    @ApiModelProperty("商家账号")
    private String accountName;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    @ApiModelProperty("合同编号")
    private String contractNo;
    @ApiModelProperty("批发市场")
    private List<String> tabRelationValue;
    @ApiModelProperty("多个电话号码查询")
    private List<String> accountNames;


}
