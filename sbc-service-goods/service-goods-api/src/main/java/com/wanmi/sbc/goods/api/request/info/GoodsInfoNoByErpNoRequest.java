package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据erpGoodsInfoNo 查询skuNo
 * Created by baijz
 * @2020-06-09  16:37:17
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoNoByErpNoRequest implements Serializable {

    private static final long serialVersionUID = -2265501195719873212L;
    
    /**
     * erp的SKU编码
     */
    @ApiModelProperty(value = "erp的SKU编码")
    private List<String> erpGoodsInfoNo;

}
