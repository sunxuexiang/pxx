package com.wanmi.sbc.goods.api.response.warehousecity;

import com.wanmi.sbc.goods.bean.vo.WareHouseCityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p> 仓库地区表新增结果</p>
 * @author zhangwenchang
 * @date 2020-04-06 17:28:33
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WareHouseCityAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的 仓库地区表信息
     */
    @ApiModelProperty(value = "已新增的 仓库地区表信息")
    private WareHouseCityVO wareHouseCityVO;
}
