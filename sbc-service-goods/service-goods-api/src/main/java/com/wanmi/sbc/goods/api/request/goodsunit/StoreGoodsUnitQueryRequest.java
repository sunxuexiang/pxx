package com.wanmi.sbc.goods.api.request.goodsunit;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
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
public class StoreGoodsUnitQueryRequest extends BaseQueryRequest implements Serializable {

    private static final long serialVersionUID = 215195317836066025L;

    /**
     * 商品属性
     */
    @ApiModelProperty(value = "商品编号，采用UUID")
    private String unit;
    /**
     * 删除标识,0: 未删除 1: 已删除
     */
    @ApiModelProperty(value = "删除标识,0: 未删除 1: 已删除")
    private Integer delFlag;
    /**
     * 是否启用 0：停用，1：启用
     */
    @ApiModelProperty(value = "是否启用 0：停用，1：启用")
    private Integer status;
    @ApiModelProperty(value = "商户Id")
    private Long companyInfoId;

    private List<Long> companyInfoIds;

}
