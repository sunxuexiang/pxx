package com.wanmi.sbc.goods.provider.impl.flashsalegoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.flashsalegoods.*;
import com.wanmi.sbc.goods.api.response.flashsalegoods.*;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.goods.flashsalegoods.model.root.FlashSaleGoods;
import com.wanmi.sbc.goods.flashsalegoods.service.FlashSaleGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>抢购商品表查询服务接口实现</p>
 *
 * @author bob
 * @date 2019-06-11 14:54:31
 */
@Slf4j
@RestController
@Validated
public class FlashSaleGoodsQueryController implements FlashSaleGoodsQueryProvider {
    @Autowired
    private FlashSaleGoodsService flashSaleGoodsService;

    @Override
    public BaseResponse<FlashSaleGoodsPageResponse> page(@RequestBody @Valid FlashSaleGoodsPageRequest flashSaleGoodsPageReq) {
        FlashSaleGoodsQueryRequest queryReq = new FlashSaleGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(flashSaleGoodsPageReq, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        Page<FlashSaleGoods> flashSaleGoodsPage = flashSaleGoodsService.page(queryReq);
        Page<FlashSaleGoodsVO> newPage = flashSaleGoodsPage.map(entity -> flashSaleGoodsService.wrapperVo(entity));
        MicroServicePage<FlashSaleGoodsVO> microPage = new MicroServicePage<>(newPage, flashSaleGoodsPageReq.getPageable());
        FlashSaleGoodsPageResponse finalRes = new FlashSaleGoodsPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<FlashSaleGoodsListResponse> list(@RequestBody @Valid FlashSaleGoodsListRequest flashSaleGoodsListReq) {
        FlashSaleGoodsQueryRequest queryReq = new FlashSaleGoodsQueryRequest();
        KsBeanUtil.copyPropertiesThird(flashSaleGoodsListReq, queryReq);
        List<FlashSaleGoods> flashSaleGoodsList = flashSaleGoodsService.getFlashSaleGoodsAndGoodsInfoList(queryReq);
        // log.info("flashSaleGoodsList {}", flashSaleGoodsList);
        List<FlashSaleGoodsVO> newList = flashSaleGoodsList.stream().map(entity -> flashSaleGoodsService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new FlashSaleGoodsListResponse(newList));
    }

    @Override
    public BaseResponse<FlashSaleGoodsByIdResponse> getById(@RequestBody @Valid FlashSaleGoodsByIdRequest flashSaleGoodsByIdRequest) {
        FlashSaleGoods flashSaleGoods = flashSaleGoodsService.getById(flashSaleGoodsByIdRequest.getId());
        return BaseResponse.success(new FlashSaleGoodsByIdResponse(flashSaleGoodsService.wrapperVo(flashSaleGoods)));
    }

    /**
     * 商品是否正在抢购API
     *
     * @param isInProgressReq {@link IsInProgressReq}
     * @author bob
     */
    @Override
    public BaseResponse<IsInProgressResp> isInProgress(@RequestBody @Valid IsInProgressReq isInProgressReq) {
        List<FlashSaleGoods> flashSaleGoodsList = flashSaleGoodsService.getByGoodsIdAndFullTime(isInProgressReq);
        List<FlashSaleGoodsVO> newList = flashSaleGoodsList.stream().map(entity -> flashSaleGoodsService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(IsInProgressResp.builder().flashSaleGoodsVOS(newList).serverTime(LocalDateTime.now()).build());
    }

    @Override
    public BaseResponse<FlashSaleGoodsStoreCountResponse> storeCount() {
        FlashSaleGoodsQueryRequest queryReq = new FlashSaleGoodsQueryRequest();
        queryReq.setDelFlag(DeleteFlag.NO);
        List<FlashSaleGoods> flashSaleGoodsList = flashSaleGoodsService.list(queryReq);
        Long storeCount = flashSaleGoodsList.stream().map(FlashSaleGoods::getStoreId).distinct().count();
        return BaseResponse.success(new FlashSaleGoodsStoreCountResponse(storeCount));
    }

    @Override
    public BaseResponse<IsFlashSaleResponse> isFlashSale(@RequestBody @Valid IsFlashSaleRequest isFlashSaleRequest) {
        IsFlashSaleResponse isFlashSaleResponse = new IsFlashSaleResponse();
        isFlashSaleResponse.setActivityTime(isFlashSaleRequest.getActivityTime());
        IsFlashSaleResponse flashSaleResponse = flashSaleGoodsService.getByActivityTime(isFlashSaleRequest);
        if (flashSaleResponse.getGoodsId() != null) {
            isFlashSaleResponse.setIsFlashSale(true);
        } else {
            isFlashSaleResponse.setIsFlashSale(false);
        }
        return BaseResponse.success(isFlashSaleResponse);
    }

    @Override
    public BaseResponse<List<FlashSaleGoodsVO>> isSecKill(@RequestBody @Valid IsSecKillRequest isFlashSaleRequest) {
        List<FlashSaleGoods> byGoodsInfoIdAndFullTime =
                flashSaleGoodsService.getByGoodsInfoIdAndFullTime(isFlashSaleRequest);
        List<FlashSaleGoodsVO> flashSaleGoodsVOS = KsBeanUtil.convertList(byGoodsInfoIdAndFullTime,
                FlashSaleGoodsVO.class);

        return BaseResponse.success(flashSaleGoodsVOS);
    }
}

