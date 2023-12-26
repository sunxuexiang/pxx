package com.wanmi.sbc.returnorder.api.request.purchase;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckedBulkCartRequest {

    /**
     * 商品编号
     */
    private List<String> goodsInfos;

    /**
     * 用户ID
     */

    private String customerId;

    /**
     * 仓库ID
     */
    private Long wareId;

    @ApiModelProperty(value = "类型  3全选中 4全未选中  0部分选中 1部分未选中")
    private String type;

}
