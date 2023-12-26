package com.wanmi.sbc.marketing.api.request.pile;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class PileActivityGoodsAddRequest extends BaseRequest {
    @ApiModelProperty(value = "操作人", hidden = true)
    private String createPerson;

    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @ApiModelProperty(value = "囤货活动id")
    @NotBlank
    private String activityId;

    @ApiModelProperty(value = "选择的商品集合")
    List<String> selectedGoods;

    @ApiModelProperty(value = "分仓Id")
    private Long wareId;

    @ApiModelProperty(value = "选择标记： 0 选中商品集合 ，1 分仓全选， 2 分仓条件筛选")
    private Integer selectFlag = 0;

    @ApiModelProperty(value = "筛选条件")
    private String filterCondition;

    @ApiModelProperty(value = "公共虚拟库存",hidden = true)
    private Long publicVirtualStock;
}
