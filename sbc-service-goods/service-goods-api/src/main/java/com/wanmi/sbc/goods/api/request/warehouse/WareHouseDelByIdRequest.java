package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.goods.api.request.GoodsBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * <p>单个删除仓库表请求参数</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseDelByIdRequest extends GoodsBaseRequest {
private static final long serialVersionUID = 1L;

    /**
     * wareId
     */
    @ApiModelProperty(value = "wareId")
    @NotNull
    private Long wareId;

    /**
     * 编辑人
     */
    @ApiModelProperty(value = "编辑人", hidden = true)
    private String updatePerson;

}
