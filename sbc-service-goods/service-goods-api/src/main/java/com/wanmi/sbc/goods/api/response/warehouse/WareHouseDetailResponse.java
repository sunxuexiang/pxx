package com.wanmi.sbc.goods.api.response.warehouse;

import com.wanmi.sbc.goods.bean.vo.WareHouseDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>仓库表新增结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseDetailResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的仓库表信息
     */
    @ApiModelProperty(value = "已新增的仓库表信息")
    private WareHouseDetailVO wareHouseDetailVO;
}
