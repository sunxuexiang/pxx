package com.wanmi.sbc.marketing.api.request.pile;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
public class PileActivityGoodsModifyRequest extends BaseRequest {
    @ApiModelProperty(value = "修改人", hidden = true)
    private String updatePerson;

    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @ApiModelProperty(value = "Id")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "囤货活动Id")
    @NotBlank
    private String activityId;

    @ApiModelProperty(value = "虚拟库存")
    @NotNull
    private Long virtualStock;


}
