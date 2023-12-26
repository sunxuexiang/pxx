package com.wanmi.sbc.goods.provider.impl.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsProvider;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsQueryResponse;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.service.RetailGoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class RetailGoodsController implements RetailGoodsProvider {

    @Autowired
    private RetailGoodsService retailGoodsService;

    @Override
    public BaseResponse modifyRetailGoodsSeqNum(@Valid GoodsModifySeqNumRequest request) {
        retailGoodsService.modifyGoodsSeqNum(request.getGoodsId(), request.getGoodsSeqNum());
        return BaseResponse.SUCCESSFUL();
    }
    @Override
    public BaseResponse modifyAddedRetailStatus(@Valid GoodsModifyAddedStatusRequest request) {
        Integer addedFlag = request.getAddedFlag();
        List<String> goodsIdList = request.getGoodsIds();
        retailGoodsService.updateAddedStatus(addedFlag, goodsIdList);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyBatchGoodsSeqNum(GoodsBatchModifySeqNumRequest request) {
        request.getBatchRequest().forEach(batch -> retailGoodsService.modifyGoodsSeqNum(batch.getGoodsId(), batch.getGoodsSeqNum()));
        return BaseResponse.SUCCESSFUL();
    }

}
