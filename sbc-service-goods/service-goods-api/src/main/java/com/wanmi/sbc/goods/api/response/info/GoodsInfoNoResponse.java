package com.wanmi.sbc.goods.api.response.info;

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
public class GoodsInfoNoResponse implements Serializable {

    private static final long serialVersionUID = -2388177001599052340L;

    /**
     * goodsInfoNoList
     */
    @ApiModelProperty(value = "goodsInfoNoList")
    private List<String> goodsInfoNoList;
}
