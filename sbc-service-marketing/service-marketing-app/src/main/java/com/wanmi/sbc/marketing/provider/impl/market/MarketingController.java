package com.wanmi.sbc.marketing.provider.impl.market;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.market.MarketingProvider;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.response.market.MarketingAddResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingDeleteResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPauseResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingStartResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.request.MarketingSaveRequest;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.marketing.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-21 14:27
 */
@Validated
@RestController
public class MarketingController implements MarketingProvider {

    @Autowired
    private MarketingService marketingService;

    @Autowired
    private MarketService marketService;

    @Override
    public BaseResponse syncCacheFromPersistence() {
        marketService.syncCacheFromPersistence();
        return  BaseResponse.SUCCESSFUL();
    }

    /**
     * @param addRequest 新增参数 {@link MarketingAddRequest}
     * @return
     */
    @Override
    public BaseResponse<List<String>> add(@RequestBody @Valid MarketingAddRequest addRequest) {

        MarketingSaveRequest convert = KsBeanUtil.convert(addRequest, MarketingSaveRequest.class);
        List<String> strings = marketingService.validParam(convert);
        Marketing marketing = marketingService.addMarketing(convert);
        MarketingAddResponse response = MarketingAddResponse.builder().marketingVO(KsBeanUtil.convert(marketing, MarketingVO.class)).build();

        return BaseResponse.success(strings);
    }

    /**
     * @param modifyRequest 新增参数 {@link MarketingModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid MarketingModifyRequest modifyRequest) {

        marketingService.modifyMarketing(KsBeanUtil.convert(modifyRequest, MarketingSaveRequest.class));

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param deleteByIdRequest 营销ID {@link MarketingDeleteByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingDeleteResponse> deleteById(@RequestBody @Valid MarketingDeleteByIdRequest deleteByIdRequest) {

        int result = marketService.deleteMarketingById(
                deleteByIdRequest.getMarketingId(),
                deleteByIdRequest.getOperatorId()
        );
        MarketingDeleteResponse response = MarketingDeleteResponse.builder()
                .resultNum(result)
                .build();

        return BaseResponse.success(response);
    }

    /**
     * @param pauseByIdRequest 营销ID {@link MarketingPauseByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingPauseResponse> pauseById(@RequestBody @Valid MarketingPauseByIdRequest pauseByIdRequest) {

        MarketingPauseResponse response = MarketingPauseResponse.builder()
                .resultNum( marketingService.pauseMarketingById(pauseByIdRequest.getMarketingId())).build();
        return BaseResponse.success(response);
    }

    /**
     * @param startByIdRequest 营销ID {@link MarketingStartByIdRequest}
     * @return
     */
    @Override
    public BaseResponse<MarketingStartResponse> startById(@RequestBody @Valid MarketingStartByIdRequest startByIdRequest) {

        MarketingStartResponse response = MarketingStartResponse.builder()
                .resultNum(marketingService.startMarketingById(startByIdRequest.getMarketingId())).build();

        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse terminationMarketingById(@Valid MarketingStartByIdRequest startByIdRequest) {
        marketService.terminationMarketingById(startByIdRequest.getMarketingId(),startByIdRequest.getOperatorId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Map<String, Object>> incrActiveNum(MarketingDecrNumRequest marketingDecrNumRequest) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",0);
        if (marketingService.hasNext(marketingDecrNumRequest.getMarketingId(), marketingDecrNumRequest.getLevelId(), marketingDecrNumRequest.getGoodsInfoId())){


            marketingService.decrActiveNum(marketingDecrNumRequest.getMarketingId(),
                    marketingDecrNumRequest.getLevelId(), marketingDecrNumRequest.getGoodsInfoId(),marketingDecrNumRequest.getNum());
        }
        return BaseResponse.success(map);
    }
}
