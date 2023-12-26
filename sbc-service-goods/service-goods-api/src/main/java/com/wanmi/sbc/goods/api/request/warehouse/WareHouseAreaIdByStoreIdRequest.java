package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.enums.WareHouseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 仓库覆盖地区
 * @author zhangwenchang
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WareHouseAreaIdByStoreIdRequest implements Serializable {

    private static final long serialVersionUID = -8107078231538944644L;

    /**
     * 仓库id
     */
    @ApiModelProperty(value = "仓库id")
    @NotNull
    private Long wareId;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    @NotNull
    private Long storeId;
    /**
     * 线上仓:0 ,门店仓:1
     */
    @ApiModelProperty(value = "线上仓:0 ,门店仓:1")
    private WareHouseType wareHouseType;
}
