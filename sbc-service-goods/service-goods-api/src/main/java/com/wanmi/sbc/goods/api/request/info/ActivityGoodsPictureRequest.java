package com.wanmi.sbc.goods.api.request.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityGoodsPictureRequest implements Serializable {

    @ApiModelProperty(value = "goodsInfoId/skuId")
    @NotNull
    private String goodsInfoId;

    @ApiModelProperty(value = "goodsId/spuId")
    @NotNull
    private String goodsId;

    @ApiModelProperty(value = "图片上传路径")
    @NotNull
    private String imgPath;
}
