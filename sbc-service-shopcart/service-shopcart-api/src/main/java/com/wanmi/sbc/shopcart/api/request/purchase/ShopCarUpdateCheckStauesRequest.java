package com.wanmi.sbc.shopcart.api.request.purchase;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 * author: yang
 * Date: 2021-01-13
 */
@Data
@ApiModel
public class ShopCarUpdateCheckStauesRequest implements Serializable {


    private static final long serialVersionUID = -7920821110781887934L;


    @ApiModelProperty(value = "devanningIdList")
    private List<Long> devanningIdList;
    @ApiModelProperty(value = "仓库Id")
    private Long wareId;
    @ApiModelProperty(value = "用户id")
    private String customerId;
    @ApiModelProperty(value = "类型  3全选中 4全未选中  0部分选中 1部分未选中")
    private String type;
    /**
     * @description  storeId:1:全选,2全不选，其他是正常的storeId
     * @author  shiy
     * @date    2023/5/18 16:11
    */
    @ApiModelProperty(value = "店铺id")
    private Long storeId;
}
