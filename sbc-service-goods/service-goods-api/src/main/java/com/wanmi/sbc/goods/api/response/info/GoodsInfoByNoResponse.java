package com.wanmi.sbc.goods.api.response.info;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 商品SKU视图响应
 * Created by daiyitian on 2017/3/24.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInfoByNoResponse implements Serializable {


    private static final long serialVersionUID = -5435573068669866093L;
    /**
     * 商品SKU信息
     */
    @ApiModelProperty(value = "商品SKU信息")
    private List<GoodsInfoVO> goodsInfo;
}
