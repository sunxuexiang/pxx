package com.wanmi.sbc.goods.api.request.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 批量修改商品排序序号
 *
 * @author yang
 * @dateTime 2021/01/04
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBatchModifySeqNumRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 修改商品排序List
     */
    @ApiModelProperty(value = "修改商品排序List")
    private List<GoodsModifySeqNumRequest> batchRequest;

}
