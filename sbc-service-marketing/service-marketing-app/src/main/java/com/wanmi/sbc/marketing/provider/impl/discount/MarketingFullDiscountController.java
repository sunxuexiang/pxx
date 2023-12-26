package com.wanmi.sbc.marketing.provider.impl.discount;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountModifyRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountSaveLevelListRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.discount.model.request.MarketingFullDiscountSaveRequest;
import com.wanmi.sbc.marketing.discount.service.MarketingFullDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-22 10:41
 */
@Validated
@RestController
public class MarketingFullDiscountController implements MarketingFullDiscountProvider {

    @Autowired
    private MarketingFullDiscountService marketingFullDiscountService;

    @Autowired
    private com.wanmi.sbc.marketing.marketing.MarketService newMarketService;

    /**
     * @param request 新增满折请求结构 {@link MarketingFullDiscountAddRequest}
     * @return
     */
    @Override
    @Deprecated
    public BaseResponse<List<String>> add(@RequestBody @Valid MarketingFullDiscountAddRequest request) {
        List<String> strings = marketingFullDiscountService.addMarketingFullDiscount(KsBeanUtil.convert(request, MarketingFullDiscountSaveRequest.class));
        return BaseResponse.success(strings);
    }

    /**
     * 新增满折请求
     * @param saveOrUpdateMarketingRequest
     * @return
     */
    @Override
    public BaseResponse<MarketingResponse> saveOrUpdateMarketing(SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest) {
        Marketing marketing = newMarketService.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        MarketingVO marketingVO = KsBeanUtil.convert(marketing, MarketingVO.class);
        return BaseResponse.success(MarketingResponse.builder().marketingVO(marketingVO).build());
    }

    /**
     * @param request 修改满折请求结构 {@link MarketingFullDiscountModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid MarketingFullDiscountModifyRequest request) {
        marketingFullDiscountService.modifyMarketingFullDiscount(KsBeanUtil.convert(request, MarketingFullDiscountSaveRequest.class));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 保存多级优惠信息请求结构 {@link MarketingFullDiscountSaveLevelListRequest}
     * @return
     */
    @Override
    public BaseResponse saveLevelList(@RequestBody @Valid MarketingFullDiscountSaveLevelListRequest request) {
        return BaseResponse.SUCCESSFUL();
    }
}
