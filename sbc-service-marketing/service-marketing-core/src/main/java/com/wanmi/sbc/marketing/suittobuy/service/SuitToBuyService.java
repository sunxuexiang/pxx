package com.wanmi.sbc.marketing.suittobuy.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.suittobuy.model.root.MarketingSuitDetail;
import com.wanmi.sbc.marketing.suittobuy.repository.MarketingSuitDetialRepository;
import com.wanmi.sbc.marketing.suittobuy.request.MarketingSuitToBuySaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 套装购买
 * @author: XinJiang
 * @time: 2022/2/4 16:28
 */
@Service
@Slf4j
public class SuitToBuyService {

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private MarketingSuitDetialRepository marketingSuitDetialRepository;

    @Autowired
    private MarketingScopeRepository marketingScopeRepository;

    @Transactional(rollbackFor = Exception.class)
    public List<String> addMarketingSuitToBuy(MarketingSuitToBuySaveRequest request) throws SbcRuntimeException {
        List<String> strings = marketingService.validParam(request);
        Marketing marketing = marketingService.addMarketing(request);
        this.saveSuitDetail(request.getMarketingIds(), marketing.getMarketingId());
        return strings;
    }

    @Transactional(rollbackFor = Exception.class)
    public void modifyMarketingSuitToBuy(MarketingSuitToBuySaveRequest request) throws SbcRuntimeException {
        marketingService.modifyMarketing(request);
        marketingSuitDetialRepository.deleteByMarketingId(request.getMarketingId());
        this.saveSuitDetail(request.getMarketingIds(), request.getMarketingId());
    }

    private void saveSuitDetail(List<Long> marketingIds,Long marketingId) {
        if (CollectionUtils.isNotEmpty(marketingIds)) {
            List<MarketingScope> marketingScopeList = marketingScopeRepository.findAllByMarketingIdIn(marketingIds);
            List<MarketingSuitDetail> marketingSuitDetailList = new ArrayList<>();
            marketingScopeList.forEach(marketingScope -> {
                MarketingSuitDetail marketingSuitDetail = new MarketingSuitDetail();
                marketingSuitDetail.setMarketingId(marketingId);
                marketingSuitDetail.setGoodsInfoId(marketingScope.getScopeId());
                marketingSuitDetail.setGoodsMarketingId(marketingScope.getMarketingId());
                marketingSuitDetailList.add(marketingSuitDetail);
            });
            marketingSuitDetialRepository.saveAll(marketingSuitDetailList);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
    }
}
