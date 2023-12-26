package com.wanmi.sbc.goods.provider.impl.goodswarestock;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockAddResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockModifyResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoMinusStockDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>sku分仓库存表保存服务接口实现</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:22:56
 */
@RestController
@Validated
public class GoodsWareStockController implements GoodsWareStockProvider {
    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Override
    public BaseResponse<GoodsWareStockAddResponse> add(@RequestBody @Valid GoodsWareStockAddRequest goodsWareStockAddRequest) {
        GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockAddRequest, GoodsWareStock.class);
        goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId()+"_"+goodsWareStock.getWareId());
        return BaseResponse.success(new GoodsWareStockAddResponse(
                goodsWareStockService.wrapperVo(goodsWareStockService.add(goodsWareStock))));
    }

    @Override
    public BaseResponse<GoodsWareStockAddResponse> addList(@RequestBody @Valid GoodsWareStockAddListRequest goodsWareStockAddListRequest) {
        goodsWareStockService.addList(goodsWareStockAddListRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<GoodsWareStockModifyResponse> modify(@RequestBody @Valid GoodsWareStockModifyRequest goodsWareStockModifyRequest) {
        GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockModifyRequest, GoodsWareStock.class);
        return BaseResponse.success(new GoodsWareStockModifyResponse(
                goodsWareStockService.wrapperVo(goodsWareStockService.modify(goodsWareStock))));
    }

    @Override
    public BaseResponse deleteById(@RequestBody @Valid GoodsWareStockDelByIdRequest goodsWareStockDelByIdRequest) {
        GoodsWareStock goodsWareStock = KsBeanUtil.convert(goodsWareStockDelByIdRequest, GoodsWareStock.class);
        goodsWareStock.setDelFlag(DeleteFlag.YES);
        goodsWareStockService.deleteById(goodsWareStock);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDelByIdListRequest goodsWareStockDelByIdListRequest) {
        List<GoodsWareStock> goodsWareStockList = goodsWareStockDelByIdListRequest.getIdList().stream()
                .map(Id -> {
                    GoodsWareStock goodsWareStock = KsBeanUtil.convert(Id, GoodsWareStock.class);
                    goodsWareStock.setDelFlag(DeleteFlag.YES);
                    return goodsWareStock;
                }).collect(Collectors.toList());
        goodsWareStockService.deleteByIdList(goodsWareStockList);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateList(@RequestBody @Valid GoodsWareStockUpdateListRequest goodsWareStockAddListRequest) {
        goodsWareStockService.updateList(goodsWareStockAddListRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse subStock(GoodsWareStockUpdateRequest goodsWareStockRequest) {
        GoodsInfoMinusStockDTO dto = new GoodsInfoMinusStockDTO();
        dto.setGoodsInfoId(goodsWareStockRequest.getGoodsInfoId());
        dto.setWareId(goodsWareStockRequest.getWareId());
        dto.setStock(goodsWareStockRequest.getStock());
        goodsWareStockService.subStock(dto);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse addStock(GoodsWareStockUpdateRequest goodsWareStockRequest) {
        GoodsInfoPlusStockDTO dto = new GoodsInfoPlusStockDTO();
        dto.setGoodsInfoId(goodsWareStockRequest.getGoodsInfoId());
        dto.setWareId(goodsWareStockRequest.getWareId());
        dto.setStock(goodsWareStockRequest.getStock());
        goodsWareStockService.addStock(dto);
        return BaseResponse.SUCCESSFUL();
    }
}

