package com.wanmi.sbc.order.api.response.payorder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class DeleteByPayOrderIdResponse  implements Serializable {

    @ApiModelProperty(value = "删除结果")
    String value;
}