package com.wanmi.sbc.goods.api.response.stockoutdetail;

import com.wanmi.sbc.goods.bean.vo.StockoutDetailVerifyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc_h_tian
 * @description: 缺货明细校验
 * @author: Mr.Tian
 * @create: 2020-06-04 09:34
 **/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockoutDetailVerifyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * sku缺货明细状态
     */
    @ApiModelProperty(value = "sku缺货明细状态")
    private List<StockoutDetailVerifyVO> stockoutVerifyList;
}
