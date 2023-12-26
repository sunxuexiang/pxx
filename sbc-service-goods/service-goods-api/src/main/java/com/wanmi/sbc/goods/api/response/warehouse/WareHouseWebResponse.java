package com.wanmi.sbc.goods.api.response.warehouse;

import com.wanmi.sbc.goods.bean.vo.WareHouseVOSimple;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>根据id查询任意（包含已删除）仓库表信息response</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseWebResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 仓库表信息
     */
    @ApiModelProperty(value = "仓库表信息")
    private WareHouseVOSimple wareHouseVO;
}
