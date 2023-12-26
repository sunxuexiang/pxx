package com.wanmi.sbc.order.api.request.trade;

import com.wanmi.sbc.order.bean.vo.StockupActionVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 批量新增提货明细记录请求类
 * @author: XinJiang
 * @time: 2021/12/17 16:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class BatchAddStockupActionRequest {

    /**
     * 提货明细list
     */
    private List<StockupActionVO> stockupActionVOS;
}
