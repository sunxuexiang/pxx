package com.wanmi.sbc.goods.api.request.goodsattributekey;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 新增商品属性请求对象
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsAttributeKeyQueryRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = -8933154540285476077L;

    @ApiModelProperty(value = "'ID")
    private String attributeId;
    /**
     * 属性id
     */
    @ApiModelProperty(value = "'属性id")
    private String goodsAttributeId;
    /**
     * 属性 名称
     */
    @ApiModelProperty(value = "'名称")
    private String goodsAttributeValue;
    /**
     * 商品明细id
     */
    @ApiModelProperty(value = "'明细id")
    private String goodsInfoId;
    /**
     * 商品id
     */
    @ApiModelProperty(value = "'商品id")
    private String goodsId;

    /**
     * 商品id
     */
    @ApiModelProperty(value = "'商品id")
    private List<String> goodsIds;


}
