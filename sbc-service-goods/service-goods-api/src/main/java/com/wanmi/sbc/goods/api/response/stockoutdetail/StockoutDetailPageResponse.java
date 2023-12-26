package com.wanmi.sbc.goods.api.response.stockoutdetail;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.bean.vo.StockoutDetailVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>缺货管理分页结果</p>
 * @author tzx
 * @date 2020-05-27 11:37:12
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 缺货管理分页结果
     */
    @ApiModelProperty(value = "缺货管理分页结果")
    private MicroServicePage<StockoutDetailVO> stockoutDetailVOPage;
}
