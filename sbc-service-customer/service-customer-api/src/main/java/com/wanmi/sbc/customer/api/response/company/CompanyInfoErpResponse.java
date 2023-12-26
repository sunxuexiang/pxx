package com.wanmi.sbc.customer.api.response.company;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: songhanlin
 * @Date: Created In 上午11:38 2017/11/14
 * @Description: 工商信息Response
 */
@ApiModel
@Data
public class CompanyInfoErpResponse {


    @ApiModelProperty(value = "是否已经存在erpId")
    private Boolean hasSame;

}
