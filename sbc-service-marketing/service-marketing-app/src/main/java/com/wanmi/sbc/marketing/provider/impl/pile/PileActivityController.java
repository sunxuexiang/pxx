package com.wanmi.sbc.marketing.provider.impl.pile;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.pile.*;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import com.wanmi.sbc.marketing.pile.model.root.PileActivityGoods;
import com.wanmi.sbc.marketing.pile.service.PileActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@Validated
@RestController
@Slf4j
public class PileActivityController implements PileActivityProvider {
    @Autowired
    private PileActivityService pileActivityService;

    @Override
    public BaseResponse add(@Valid PileActivityAddRequest request) {
        pileActivityService.add(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse modify(@Valid PileActivityModifyRequest request) {
        pileActivityService.modify(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse close(@Valid PileActivityCloseByIdRequest request) {
        pileActivityService.close(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteById(PileActivityDeleteByIdRequest request) {
        pileActivityService.delete(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<String>> getStartPileActivityGoodsInfoIds() {
        return BaseResponse.success(pileActivityService.getStartPileActivityGoodsInfoIds());
    }

    @Override
    public BaseResponse<List<PileActivityVO>> getStartPileActivity() {
        return BaseResponse.success(pileActivityService.getStartPileActivity());
    }


    @Override
    public BaseResponse<List<PileActivityGoodsVO>> getStartPileActivityPileActivityGoods(@Valid PileActivityPileActivityGoodsRequest request) {
        List<PileActivityGoods> startPileActivityVirtualStock = null;
        if (StringUtils.isNotBlank(request.getPileActivityId())){
            startPileActivityVirtualStock = pileActivityService.getStartPileActivityVirtualStock(request.getPileActivityId(),request.getGoodsInfoIds());
        } else {
            startPileActivityVirtualStock = pileActivityService.getStartPileActivityGoodsListVirtualStock(request.getGoodsInfoIds());
        }
        List<PileActivityGoodsVO> pileActivityGoodsVOS = KsBeanUtil.convertList(startPileActivityVirtualStock, PileActivityGoodsVO.class);
        return BaseResponse.success(pileActivityGoodsVOS);
    }

    @Override
    public BaseResponse updateVirtualStock(@Valid List<PileActivityStockRequest> request) {
        pileActivityService.updateVirtualStock(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<PileActivityVO> getByActivityId(@Valid PileActivityCloseByIdRequest request) {
        return BaseResponse.success(pileActivityService.getByActivityId(request));
    }

}
