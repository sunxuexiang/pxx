package com.wanmi.sbc.marketing.provider.impl.suittobuy;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.suittobuy.SuitToBuyProvider;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyAddRequest;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyModifyRequest;
import com.wanmi.sbc.marketing.api.response.suittobuy.SuitToBuyAddResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.suittobuy.request.MarketingSuitToBuySaveRequest;
import com.wanmi.sbc.marketing.suittobuy.service.SuitToBuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 套装购买
 * @author: XinJiang
 * @time: 2022/2/4 15:39
 */
@Validated
@RestController
public class SuitToBuyController implements SuitToBuyProvider {

    @Autowired
    private SuitToBuyService suitToBuyService;

    @Override
    public BaseResponse<List<String>> add(SuitToBuyAddRequest addRequest) {
        return BaseResponse.success(suitToBuyService.addMarketingSuitToBuy(KsBeanUtil.convert(addRequest, MarketingSuitToBuySaveRequest.class)));
        //return BaseResponse.success(SuitToBuyAddResponse.builder().marketingVO(KsBeanUtil.convert(marketing, MarketingVO.class)).build());

       /* Marketing marketing = suitToBuyService.addMarketingSuitToBuy(KsBeanUtil.convert(addRequest, MarketingSuitToBuySaveRequest.class));
        return BaseResponse.success(SuitToBuyAddResponse.builder().marketingVO(KsBeanUtil.convert(marketing, MarketingVO.class)).build());
*/
    }

    @Override
    public BaseResponse modify(SuitToBuyModifyRequest modifyRequest) {
        suitToBuyService.modifyMarketingSuitToBuy(KsBeanUtil.convert(modifyRequest,MarketingSuitToBuySaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

}
