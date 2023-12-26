package com.wanmi.sbc.customer.api.response.customer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jeffrey
 * @create 2021-08-19 14:24
 */

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerIdAllResponse {

    @ApiModelProperty(value = "所有用户ID")
    private List<String> customerIds;
}
