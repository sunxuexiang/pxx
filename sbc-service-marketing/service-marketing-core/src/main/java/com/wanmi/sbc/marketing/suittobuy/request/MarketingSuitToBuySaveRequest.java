package com.wanmi.sbc.marketing.suittobuy.request;

import com.wanmi.sbc.marketing.common.request.MarketingSaveRequest;
import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 套装购买
 * @author: XinJiang
 * @time: 2022/2/4 16:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketingSuitToBuySaveRequest extends MarketingSaveRequest {

    /**
     * 套装购买营销活动商品详细信息
     */
    private List<MarketingSuitDetail> marketingSuitDetailList;

    /**
     * 套装购买选择的营销活动id集合
     */
    private List<Long> marketingIds;
}
