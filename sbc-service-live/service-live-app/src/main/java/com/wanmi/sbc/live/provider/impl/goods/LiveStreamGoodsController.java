package com.wanmi.sbc.live.provider.impl.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.live.api.provider.goods.LiveStreamGoodsProvider;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsAddRequest;
import com.wanmi.sbc.live.api.request.goods.LiveStreamGoodsModifyRequest;
import com.wanmi.sbc.live.goods.service.LiveStreamGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>直播商品保存服务接口实现</p>
 * @author zwb
 * @date 2020-06-10 11:05:45
 */
@RestController
@Validated
public class LiveStreamGoodsController implements LiveStreamGoodsProvider {
    @Autowired
    private LiveStreamGoodsService liveStreamGoodsService;
    @Override
    public BaseResponse supplier(@RequestBody LiveStreamGoodsAddRequest supplierAddReq) {
        liveStreamGoodsService.addGoods(supplierAddReq);
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse modify(@RequestBody LiveStreamGoodsModifyRequest supplierModifyReq) {
        if(Objects.nonNull(supplierModifyReq.getExplainFlag())){
            liveStreamGoodsService.updateStreamGoods(supplierModifyReq.getGoodsInfoId());
            return BaseResponse.SUCCESSFUL();
        }
        liveStreamGoodsService.modifyStreamGoods(supplierModifyReq.getGoodsInfoId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saleGoods(LiveStreamGoodsModifyRequest supplierModifyReq) {
        liveStreamGoodsService.saleGoods(supplierModifyReq);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saleGoodsBatch(LiveStreamGoodsModifyRequest supplierModifyReq) {
        liveStreamGoodsService.saleGoodsBatch(supplierModifyReq);
        return BaseResponse.SUCCESSFUL();
    }
}
