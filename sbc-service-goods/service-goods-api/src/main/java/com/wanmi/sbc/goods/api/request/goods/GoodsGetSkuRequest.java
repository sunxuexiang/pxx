package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsGetSkuRequest implements Serializable {



    /**
     * 属性名称
     */
    @ApiModelProperty(value = "属性名称")
    private List<GoodsAttributeVo> attributeList;

}
