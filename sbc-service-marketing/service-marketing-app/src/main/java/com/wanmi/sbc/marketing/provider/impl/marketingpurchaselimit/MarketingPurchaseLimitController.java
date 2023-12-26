package com.wanmi.sbc.marketing.provider.impl.marketingpurchaselimit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.request.marketingpurchaselimit.MarketingPurchaseLimitRequest;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.marketingpurchaselimit.Service.MarketingPurchaseLimitService;
import com.wanmi.sbc.marketing.marketingpurchaselimit.model.root.MarketingPurchaseLimit;
import com.wanmi.sbc.marketing.marketingpurchaselimit.repository.MarketingPurchaseLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;


@Validated
@RestController
public class MarketingPurchaseLimitController implements MarketingPurchaseLimitProvider {

    @Autowired
    private MarketingPurchaseLimitService purchaseLimitService;

    @Autowired
    private MarketingPurchaseLimitRepository repository;

    @Override
    public BaseResponse<List<MarketingPurchaseLimitVO>> queryListByMarketingIdAndGoodsInfoIds(@Valid MarketingPurchaseLimitRequest request) {
        List<MarketingPurchaseLimit> marketingPurchaseLimits = purchaseLimitService.getbyMarketingIdAndGoodsInfosId(request.getMarketingId(), request.getGoodsInfoIds());
        return BaseResponse.success(KsBeanUtil.convert(marketingPurchaseLimits,MarketingPurchaseLimitVO.class));
    }

    @Override
    public BaseResponse<List<MarketingPurchaseLimitVO>> queryListByParm(Map<String, Object> request) {
        List<MarketingPurchaseLimit> marketingPurchaseLimits = purchaseLimitService.getbyCoutomerIdAndMarketingIdAndGoodsInfoId(request.get("customerId").toString(), Long.parseLong(request.get("marketingId").toString()), request.get("goodsInfoId").toString());
        return BaseResponse.success(KsBeanUtil.convert(marketingPurchaseLimits,MarketingPurchaseLimitVO.class));
    }

    @Override
    public BaseResponse<List<MarketingPurchaseLimitVO>> queryListByParmNoUser(Map<String, Object> request) {
        List<MarketingPurchaseLimit> marketingPurchaseLimits = purchaseLimitService.getMarketingIdAndGoodsInfoId( Long.parseLong(request.get("marketingId").toString()), request.get("goodsInfoId").toString());
        return BaseResponse.success(KsBeanUtil.convert(marketingPurchaseLimits,MarketingPurchaseLimitVO.class));
    }

    @Override
    public BaseResponse add(List<MarketingPurchaseLimitVO> request) {
        repository.saveAll(KsBeanUtil.convert(request,MarketingPurchaseLimit.class));
        return BaseResponse.SUCCESSFUL();
    }


}
