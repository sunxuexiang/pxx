package com.wanmi.sbc.returnorder.api.response.trade;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 统计订单spu 的数据
 * @author sgy
 * @create 2021-08-07 16:43
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRecommendCount implements Serializable {

    private static final long serialVersionUID = -3345961109775737749L;
    @ApiModelProperty(value = "spuId")
    private String spuId;
    @ApiModelProperty(value = "skuId")
    private String skuId;
    @ApiModelProperty(value = "公司id")
    private String companyId;
    @ApiModelProperty(value = "数量")
    private int count;
    @ApiModelProperty(value = "对应id")
    private List<String> _id;
    @ApiModelProperty(value = "用户id")
    private String userId;


}
