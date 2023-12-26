package com.wanmi.sbc.goods.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description: spu维度校验是否已经插入数据在缺货明细
 * @author: Mr.Tian
 * @create: 2020-06-04 09:27
 **/
@ApiModel
@Data
public class StockoutDetailVerifyVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * sku id
     */
    @ApiModelProperty(value = "sku id")
    private  String goodsInfoId;

    /**
     * 是否已经记录当前用户插入缺货
     * true 为插入过任何一条记录 false 已插入记录
     */
    @ApiModelProperty(value = "是否已经记录当前用户插入缺货,true 为插入过任何一条记录 false 已插入记录")
    private Boolean flag;


}
