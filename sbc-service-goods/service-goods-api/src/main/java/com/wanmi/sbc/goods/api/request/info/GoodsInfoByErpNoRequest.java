package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @description: TODO
 * @author: XinJiang
 * @time: 2022/4/19 15:07
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsInfoByErpNoRequest implements Serializable {

    private static final long serialVersionUID = 5433721294455536352L;

    /**
     * erp编码
     */
    @ApiModelProperty(value = "erp编码")
    @NotBlank
    private String erpNo;
}
