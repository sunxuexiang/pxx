package com.wanmi.sbc.marketing.provider.impl.gift;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftAddRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftModifyRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.gift.request.MarketingFullGiftSaveRequest;
import com.wanmi.sbc.marketing.gift.service.MarketingFullGiftService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-22 9:37
 */
@Validated
@RestController
public class FullGiftController implements FullGiftProvider {

    @Autowired
    private MarketingFullGiftService marketingFullGiftService;

    @Autowired
    private MarketingService marketingService;
    
    @Autowired
    private com.wanmi.sbc.marketing.marketing.MarketService newMarketService;


    /**
     * @param addRequest 新增参数 {@link FullGiftAddRequest}
     * @return
     */
    @Override
    public BaseResponse<List<String>> add(@RequestBody @Valid FullGiftAddRequest addRequest) {
        MarketingFullGiftSaveRequest convert = KsBeanUtil.convert(addRequest, MarketingFullGiftSaveRequest.class);
        List<String> strings = marketingService.validParam(convert);
        if(CollectionUtils.isNotEmpty(strings)){
            return BaseResponse.success(strings);
        }
        Marketing marketing = marketingFullGiftService.addMarketingFullGift(convert);
        return BaseResponse.success(strings);
        //return BaseResponse.success(FullGiftAddResponse.builder().marketingVO(KsBeanUtil.convert(marketing, MarketingVO.class)).build());
    }

    @Override
    public BaseResponse<MarketingResponse> saveOrUpdateMarketing(@RequestBody @Valid SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest) {
        Marketing marketing = newMarketService.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        MarketingVO marketingVO = KsBeanUtil.convert(marketing, MarketingVO.class);
        return BaseResponse.success(MarketingResponse.builder().marketingVO(marketingVO).build());
    }


    /**
     * @param modifyRequest 修改参数 {@link FullGiftModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid FullGiftModifyRequest modifyRequest) {
        marketingFullGiftService.modifyMarketingFullGift(KsBeanUtil.convert(modifyRequest, MarketingFullGiftSaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }
}
