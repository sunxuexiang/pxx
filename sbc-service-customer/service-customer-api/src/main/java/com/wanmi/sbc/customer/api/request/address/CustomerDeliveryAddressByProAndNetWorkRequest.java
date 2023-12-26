package com.wanmi.sbc.customer.api.request.address;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerDeliveryAddressByProAndNetWorkRequest  implements Serializable {

    private static final long serialVersionUID = 5105651558850105057L;

    /**
     * 省id
     */
    @ApiModelProperty(value = "省id")
    private String provinceId;

    /**
     * 分页起始数量
     */
    @ApiModelProperty(value = "分页起始数量")
    private int pagenum;


    /**
     * 分页页数
     */
    @ApiModelProperty(value = "分页页数")
    private int page;
}
