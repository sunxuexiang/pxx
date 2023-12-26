package com.wanmi.sbc.goods.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品批量导入参数
 * 增加虚拟goodsId，表示与其他商品相关类的数据关联
 * Created by dyt on 2017/4/11.
 */
@ApiModel
@Data
public class BatchGoodsUpdateDTO implements Serializable {


    private static final long serialVersionUID = 3494758944332449753L;
    /**
     * 模拟goodsId
     */
    @ApiModelProperty(value = "erpId")
    private String erpId;

    @ApiModelProperty(value = "上下架：0下架，1上架")
    private Integer addFlag;

    /**
     * 品牌ID
     */
    @ApiModelProperty(value = "品牌ID")
    private Long brandId;

    /**
     * 销售类型 0:批发, 1:零售
     */
    @ApiModelProperty(value = "销售类型", dataType = "com.wanmi.sbc.goods.bean.enums.SaleType")
    private Integer saleType;

    /**
     * 类别ID
     */
    @ApiModelProperty(value = "类别ID")
    private Long cateId;
}
