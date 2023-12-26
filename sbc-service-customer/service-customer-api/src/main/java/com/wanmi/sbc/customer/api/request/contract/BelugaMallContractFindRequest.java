package com.wanmi.sbc.customer.api.request.contract;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BelugaMallContractFindRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

    @ApiModelProperty("商家账号")
    private String supplierName;
    @ApiModelProperty(value = "签署状态")
    private Integer status;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime beginTime;
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    @ApiModelProperty("合同编号")
    private String contractNo;
    @ApiModelProperty("交易号")
    private String transactionId;


}
