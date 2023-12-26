package com.wanmi.sbc.goods.api.request.catebrandsortrel;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import lombok.*;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>单个删除类目品牌排序表请求参数</p>
 * @author lvheng
 * @date 2021-04-08 11:24:32
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CateBrandSortRelDelByIdRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * 品类ID
     */
    @ApiModelProperty(value = "品类ID")
    @NotNull
    private Long cateId;
}
