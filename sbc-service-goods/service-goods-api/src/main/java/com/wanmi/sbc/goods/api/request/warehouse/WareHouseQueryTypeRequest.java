package com.wanmi.sbc.goods.api.request.warehouse;

import com.wanmi.sbc.common.enums.WareHouseType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @program: sbc_h_tian
 * @description:
 * @author: Mr.Tian
 * @create: 2020-05-22 17:57
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseQueryTypeRequest implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * 线上仓:0 ,门店仓:1
     */
    @ApiModelProperty(value = "线上仓:0 ,门店仓:1")
    private WareHouseType wareHouseType;
}
