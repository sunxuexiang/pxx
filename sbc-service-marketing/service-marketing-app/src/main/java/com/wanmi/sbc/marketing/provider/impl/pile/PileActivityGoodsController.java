package com.wanmi.sbc.marketing.provider.impl.pile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityGoodsProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsAddRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsDeleteRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsModifyRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsPageRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsPageVO;
import com.wanmi.sbc.marketing.pile.service.PileActivityGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-19
 */
@Validated
@RestController
public class PileActivityGoodsController implements PileActivityGoodsProvider {
    @Autowired
    private PileActivityGoodsService pileActivityGoodsService;

    @Override
    public BaseResponse add(@RequestBody @Valid PileActivityGoodsAddRequest request) {
        pileActivityGoodsService.add(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modify(@RequestBody @Valid PileActivityGoodsModifyRequest request) {
        pileActivityGoodsService.modify(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delete(@RequestBody @Valid PileActivityGoodsDeleteRequest request) {
        pileActivityGoodsService.delete(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<MicroServicePage<PileActivityGoodsPageVO>> page(@RequestBody @Valid PileActivityGoodsPageRequest request) {
        return BaseResponse.success(pileActivityGoodsService.page(request));
    }

}
