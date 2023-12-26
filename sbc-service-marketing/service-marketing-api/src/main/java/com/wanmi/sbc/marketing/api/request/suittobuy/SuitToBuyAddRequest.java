package com.wanmi.sbc.marketing.api.request.suittobuy;

import com.wanmi.sbc.marketing.api.request.market.MarketingAddRequest;
import com.wanmi.sbc.marketing.bean.dto.MarketingSuitDetialDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @description: 新增套装购买营销活动请求类
 * @author: XinJiang
 * @time: 2022/2/4 16:01
 */
@ApiModel
@Data
public class SuitToBuyAddRequest extends MarketingAddRequest {

    private static final long serialVersionUID = 3276684596239185799L;

    private List<MarketingSuitDetialDTO> marketingSuitDetialDTOList;

    /**
     * 套装购买选择的营销活动id集合
     */
    private List<Long> marketingIds;
}
