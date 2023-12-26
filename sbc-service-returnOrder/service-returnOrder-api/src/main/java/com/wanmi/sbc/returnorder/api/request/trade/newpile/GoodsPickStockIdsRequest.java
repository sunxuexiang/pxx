package com.wanmi.sbc.returnorder.api.request.trade.newpile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lm
 * @date 2022/10/12 16:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoodsPickStockIdsRequest {

    List<String> newPileTradeIds;
}
