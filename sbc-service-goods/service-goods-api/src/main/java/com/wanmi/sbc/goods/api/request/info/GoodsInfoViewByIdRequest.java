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
 * 根据商品SKU编号查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoViewByIdRequest implements Serializable {

    private static final long serialVersionUID = -2265501195719873212L;

    /**
     * SKU编号
     */
    @ApiModelProperty(value = "SKU编号")
    @NotBlank
    private String goodsInfoId;

    /**
     * SKU编号
     */
    @ApiModelProperty(value = "厂库id")
//    @NotBlank
    private Long wareId;

}
