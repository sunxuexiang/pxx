package com.wanmi.sbc.goods.api.response.warehouse;

import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
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
public class WareHouseBySkuIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 库位信息
     */
    @ApiModelProperty(value = "库位信息")
    private String stockName;

    /**
     * 排序库位
     */
    @ApiModelProperty(value = "排序库位")
    private String sortStockName;
}
