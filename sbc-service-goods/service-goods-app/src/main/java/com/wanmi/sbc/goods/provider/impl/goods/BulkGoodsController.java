package com.wanmi.sbc.goods.provider.impl.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsBatchModifySeqNumRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyAddedStatusRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyCollectNumRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifySeqNumRequest;
import com.wanmi.sbc.goods.info.service.BulkGoodsService;
import com.wanmi.sbc.goods.info.service.RetailGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
public class BulkGoodsController implements BulkGoodsProvider {

    @Autowired
    private BulkGoodsService bulkGoodsService;

    @Override
    public BaseResponse modifyRetailGoodsSeqNum(@Valid GoodsModifySeqNumRequest request) {
        bulkGoodsService.modifyGoodsSeqNum(request.getGoodsId(), request.getGoodsSeqNum());
        return BaseResponse.SUCCESSFUL();
    }
    @Override
    public BaseResponse modifyAddedRetailStatus(@Valid GoodsModifyAddedStatusRequest request) {
        Integer addedFlag = request.getAddedFlag();
        List<String> goodsIdList = request.getGoodsIds();
        bulkGoodsService.updateAddedStatus(addedFlag, goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyBatchGoodsSeqNum(GoodsBatchModifySeqNumRequest request) {
        request.getBatchRequest().forEach(batch -> bulkGoodsService.modifyGoodsSeqNum(batch.getGoodsId(), batch.getGoodsSeqNum()));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateGoodsCollectNum(@RequestBody @Valid GoodsModifyCollectNumRequest goodsModifyCollectNumRequest) {
        bulkGoodsService.updateGoodsCollectNum(goodsModifyCollectNumRequest);
        return BaseResponse.SUCCESSFUL();
    }

}
