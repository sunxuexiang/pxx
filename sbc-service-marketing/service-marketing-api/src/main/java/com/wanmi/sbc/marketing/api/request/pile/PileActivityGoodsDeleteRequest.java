package com.wanmi.sbc.marketing.api.request.pile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel
public class PileActivityGoodsDeleteRequest implements Serializable {
    private static final long serialVersionUID = -8374298681841353552L;

    @ApiModelProperty(value = "删除人", hidden = true)
    private String deletePerson;

    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "囤货活动Id")
    @NotBlank
    private String activityId;

    @ApiModelProperty(value = "分仓Id")
    private Long wareId;

    @ApiModelProperty(value = "删除类型：0 单个商品删除，1 按仓库清空")
    @NotNull
    private Integer delType;
}
