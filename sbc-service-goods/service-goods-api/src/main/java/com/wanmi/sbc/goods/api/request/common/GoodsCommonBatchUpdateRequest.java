package com.wanmi.sbc.goods.api.request.common;

import com.wanmi.sbc.goods.bean.dto.BatchGoodsUpdateDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 批量导入商品信息请求对象
 * @author daiyitian
 * @dateTime 2018/11/2 上午9:54
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCommonBatchUpdateRequest implements Serializable {

    private static final long serialVersionUID = 2373497523561481281L;

    /**
     * 商品批量信息
     */
    @ApiModelProperty(value = "商品批量信息")
    private List<BatchGoodsUpdateDTO> goodsUpdateList;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;

}
