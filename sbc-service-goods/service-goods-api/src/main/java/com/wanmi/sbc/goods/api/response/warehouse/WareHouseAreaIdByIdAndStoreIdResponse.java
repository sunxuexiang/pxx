package com.wanmi.sbc.goods.api.response.warehouse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 仓库覆盖地区
 * @author zhangwenchang
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseAreaIdByIdAndStoreIdResponse implements Serializable {

    private static final long serialVersionUID = 8246352610810060194L;

    /**
     * 区域ids
     */
    @ApiModelProperty(value = "区域ids")
    private List<Long> areaIds;


}
