package com.wanmi.sbc.marketing.provider.impl.reduction;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionModifyRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionSaveLevelListRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.reduction.model.request.MarketingFullReductionSaveRequest;
import com.wanmi.sbc.marketing.reduction.service.MarketingFullReductionService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MarketingFullReductionController implements MarketingFullReductionProvider {

    @Autowired
    private MarketingFullReductionService marketingFullReductionService;

    @Autowired
    private com.wanmi.sbc.marketing.marketing.MarketService newMarketService;

    /**
     * @param request 新增满减请求结构 {@link MarketingFullReductionAddRequest}
     * @return
     */
    @Override
    @Deprecated
    public BaseResponse<List<String>> add(@RequestBody @Valid MarketingFullReductionAddRequest request) {
        List<String> strings = marketingFullReductionService.addMarketingFullReduction(KsBeanUtil.convert(request, MarketingFullReductionSaveRequest.class));
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
     * @param request 修改满减请求结构 {@link MarketingFullReductionModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modify(@RequestBody @Valid MarketingFullReductionModifyRequest request) {
        log.info("MarketingFullReductionModifyRequest1------>"+ JSONObject.toJSONString(request));
        MarketingFullReductionSaveRequest convert = KsBeanUtil.convert(request, MarketingFullReductionSaveRequest.class);
        log.info("MarketingFullReductionModifyRequest2------>"+ JSONObject.toJSONString(convert));
        marketingFullReductionService.modifyMarketingFullReduction(convert);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * @param request 保存多级优惠信息请求结构 {@link MarketingFullReductionSaveLevelListRequest}
     * @return
     */
    @Override
    public BaseResponse saveLevelList(@RequestBody @Valid MarketingFullReductionSaveLevelListRequest request) {
        return BaseResponse.SUCCESSFUL();
    }
}
