package com.wanmi.sbc.marketing.provider.impl.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeProvider;
import com.wanmi.sbc.marketing.api.request.market.TerminationMarketingScopeRequest;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.service.MarketingScopeService;
import com.wanmi.sbc.marketing.marketing.MarketService;
import com.wanmi.sbc.setting.api.provider.OperationLogProvider;
import com.wanmi.sbc.setting.api.request.OperationLogAddRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-21 14:27
 */
@Validated
@RestController
public class MarketingScopeController implements MarketingScopeProvider {

    @Autowired
    private MarketingScopeService marketingScopeService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private OperationLogProvider operationLogProvider;

    @Override
    public BaseResponse terminationMarketingIdAndScopeId(@RequestBody @Valid TerminationMarketingScopeRequest request) {
        marketService.terminationMarketingIdAndScopeId(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse terminationByMarketingIdAndScopeId(@Valid TerminationMarketingScopeRequest request) {
        marketService.terminationByMarketingIdAndScopeId(request);

        OperationLogAddRequest operationLogAddRequest = new OperationLogAddRequest();
        operationLogAddRequest.setEmployeeId(StringUtils.EMPTY);
        operationLogAddRequest.setOpAccount(StringUtils.EMPTY);
        operationLogAddRequest.setOpName(StringUtils.EMPTY);
        operationLogAddRequest.setStoreId(0L);
        operationLogAddRequest.setCompanyInfoId(0L);
        operationLogAddRequest.setOpRoleName(StringUtils.EMPTY);
        operationLogAddRequest.setOpIp("127.0.0.1");
        operationLogAddRequest.setOpModule("营销");
        operationLogAddRequest.setOpCode("单个终止促销活动");
        operationLogAddRequest.setOpContext("营销活动ID："+request.getMarketingId()+"，商品ID："+request.getScopeId());
        operationLogAddRequest.setOpTime(LocalDateTime.now());
        operationLogProvider.add(operationLogAddRequest);

        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saveMarketingScope(MarketingScopeVO marketingScopeVO) {
        marketingScopeService.saveMarketingScope(KsBeanUtil.convert(marketingScopeVO, MarketingScope.class));
        return BaseResponse.SUCCESSFUL();
    }
}
