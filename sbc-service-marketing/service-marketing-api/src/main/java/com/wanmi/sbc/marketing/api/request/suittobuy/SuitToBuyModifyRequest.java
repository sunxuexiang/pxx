package com.wanmi.sbc.marketing.api.request.suittobuy;

import com.wanmi.sbc.marketing.api.request.market.MarketingModifyRequest;
import com.wanmi.sbc.marketing.bean.dto.MarketingSuitDetialDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @description: 修改套装营销信息请求类
 * @author: XinJiang
 * @time: 2022/2/4 16:45
 */
@ApiModel
@Data
public class SuitToBuyModifyRequest extends MarketingModifyRequest {

    private static final long serialVersionUID = -6809645641881871678L;

    /**
     * 套装商品信息实体类
     */
    private List<MarketingSuitDetialDTO> marketingSuitDetialDTOList;

    /**
     * 套装购买选择的营销活动id集合
     */
    private List<Long> marketingIds;
}
