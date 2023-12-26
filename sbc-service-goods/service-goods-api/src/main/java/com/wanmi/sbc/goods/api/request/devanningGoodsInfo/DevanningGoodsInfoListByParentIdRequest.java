package com.wanmi.sbc.goods.api.request.devanningGoodsInfo;

import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.GoodsInfoSelectStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SKU列表条件查询请求
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DevanningGoodsInfoListByParentIdRequest implements Serializable {


    /**
     * 商品SKU父级ID
     */
    @ApiModelProperty(value = "商品SKU父级ID")
    private String parentGoodsInfoId;
}
