package com.wanmi.sbc.marketing.api.request.pile;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityGoodsPageRequest extends BaseQueryRequest {
    private static final long serialVersionUID = -9152581358962527700L;

    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @ApiModelProperty(value = "囤货活动id")
    @NotBlank(message = "囤货活动id不能为空")
    private String activityId;

    @ApiModelProperty(value = "仓库Id")
    @NotNull(message = "仓库Id不能为空")
    private Long wareId;

    @ApiModelProperty(value = "模糊条件-erp编码")
    private String likeErpNo;

    @ApiModelProperty(value = "模糊条件-商品名称")
    private String likeGoodsName;

    @ApiModelProperty(value = "分类编号")
    private Long cateId;

    @ApiModelProperty(value = "品牌编号")
    private Long brandId;
}
