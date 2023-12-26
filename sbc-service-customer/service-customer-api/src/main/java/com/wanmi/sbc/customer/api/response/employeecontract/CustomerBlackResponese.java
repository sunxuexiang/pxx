package com.wanmi.sbc.customer.api.response.employeecontract;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerBlackResponese implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;
    private String storeName;
    private Integer id;

}
