package com.wanmi.sbc.goods.api.request.customerarealimitdetail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

 
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAreaLimitDetailRequest implements Serializable {


    private static final long serialVersionUID = 898960325852548815L;





    @ApiModelProperty(value = "用户id")
    private String customerId;

    @ApiModelProperty(value = "商品id")
    private String goodsInfoId;

    @ApiModelProperty(value = "区域集合")
    private List<Long> regionIds;

    @ApiModelProperty(value = "订单id")
    private String tid;

}
