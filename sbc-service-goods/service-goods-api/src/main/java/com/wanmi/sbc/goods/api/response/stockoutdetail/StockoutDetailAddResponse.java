package com.wanmi.sbc.goods.api.response.stockoutdetail;

import com.wanmi.sbc.goods.bean.vo.StockoutDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>缺货管理新增结果</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailAddResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已新增的缺货管理信息
     */
    @ApiModelProperty(value = "已新增的缺货管理信息")
    private StockoutDetailVO stockoutDetailVO;
}
