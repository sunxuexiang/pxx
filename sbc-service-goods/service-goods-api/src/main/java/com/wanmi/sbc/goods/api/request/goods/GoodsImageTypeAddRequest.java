package com.wanmi.sbc.goods.api.request.goods;

import com.wanmi.sbc.goods.bean.vo.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;

import java.io.Serializable;
import java.util.List;

/**
 * com.wanmi.sbc.goods.api.request.goods.GoodsModifyRequest
 * 修改商品请求对象
 *
 * @author lipeng
 * @dateTime 2018/11/5 上午10:23
 */
@ApiModel
@Data

@AllArgsConstructor
public class GoodsImageTypeAddRequest implements Serializable {

    private static final long serialVersionUID = 8511009723657831203L;

    public GoodsImageTypeAddRequest(){}

    /**
     * 批量集合
     */
    @ApiModelProperty(value = "goodsId")
    private List<BaseGoodsImageTypeAdd> baseGoodsImageTypeAddList;

    @ApiModel
    @Data

    @AllArgsConstructor
    public static  class BaseGoodsImageTypeAdd implements Serializable{
        private static final long serialVersionUID = -1L;
        public BaseGoodsImageTypeAdd(){}

        /**
         * 类型图片集合
         */
        @ApiModelProperty(value = "goodsId")
        private String goodsId;


        /**
         * 类型图片集合
         */
        @ApiModelProperty(value = "类型图片集合")
        private List<GoodsImageStypeVO> goodsImageVOS;

        /**
         * 选中的商品id
         */
        @ApiModelProperty(value = "选中的商品id")
        private Long checkImageId;
    }


}
