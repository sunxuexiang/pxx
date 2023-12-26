package com.wanmi.sbc.goods.api.response.goodsunit;

import com.wanmi.sbc.goods.bean.vo.GoodsUnitVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p> 新增商品单位响应对象</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsUnitListResponse implements Serializable {

    private static final long serialVersionUID = 8570091662488856494L;

    /**
     * 商品单位分页数据
     */
    @ApiModelProperty(value = "商品单位分页数据")
    private List<GoodsUnitVo> goodsUnitVos;
}

