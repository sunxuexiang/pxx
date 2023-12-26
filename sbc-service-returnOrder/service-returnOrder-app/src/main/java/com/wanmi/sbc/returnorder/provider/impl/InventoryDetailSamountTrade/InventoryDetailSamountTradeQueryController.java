package com.wanmi.sbc.returnorder.provider.impl.InventoryDetailSamountTrade;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.InventoryDetailSamountTrade.InventoryDetailSamountTradeProvider;
import com.wanmi.sbc.returnorder.api.request.InventoryDetailSamountTrade.InventoryDetailSamountTradeRequest;
import com.wanmi.sbc.returnorder.api.response.inventorydetailsamounttrade.InventoryDetailSamountTradeResponse;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountTradeVO;
import com.wanmi.sbc.returnorder.inventorydetailsamounttrade.model.root.InventoryDetailSamountTrade;
import com.wanmi.sbc.returnorder.inventorydetailsamounttrade.service.InventoryDetailSamountTradeService;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@Validated
public class InventoryDetailSamountTradeQueryController implements InventoryDetailSamountTradeProvider {

    @Autowired
    private InventoryDetailSamountTradeService inventoryDetailSamountTradeService;


    /**
     * 插入分摊数据方法
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse saveGoodsShareMoney(InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        inventoryDetailSamountTradeService.saveGoodsShareMoney
                (KsBeanUtil.convert(inventoryDetailSamountTradeRequest.getTradeItemVOS(), TradeItem.class), inventoryDetailSamountTradeRequest.getOid());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse updateGoodsShareMoney(InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        inventoryDetailSamountTradeService.updateGoodsShareMoney(KsBeanUtil.convert(inventoryDetailSamountTradeRequest.getTradeItemVOS(), TradeItem.class), inventoryDetailSamountTradeRequest.getOid());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改退货退款状态
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse updateInventoryDetailSamountReturnFlag(InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        inventoryDetailSamountTradeService.updateInventoryDetailSamountReturnFlag(inventoryDetailSamountTradeRequest.getInventoryDetailSamountTradeVOS());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改退货退款状态
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse updateInventoryDetailSamountFlag(InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        inventoryDetailSamountTradeService.updateInventoryDetailSamountTradeFlag(inventoryDetailSamountTradeRequest.getOid()
                , inventoryDetailSamountTradeRequest.getGoodsInfoId(), inventoryDetailSamountTradeRequest.getNum()
                , inventoryDetailSamountTradeRequest.getReturnId(), inventoryDetailSamountTradeRequest.getReturnFlag());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询方法, 尽量请使用此方法
     * -- 此方法已处理老数据
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> getInventoryAdaptive(@Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        BaseResponse<InventoryDetailSamountTradeResponse> inventoryDetailSamountTradeResponseBaseResponse = new BaseResponse<>();
        if (StringUtils.isNotEmpty(inventoryDetailSamountTradeRequest.getReturnId())) {
            inventoryDetailSamountTradeResponseBaseResponse = getInventoryByReturnId(inventoryDetailSamountTradeRequest);
        } else if (StringUtils.isNotEmpty(inventoryDetailSamountTradeRequest.getOid())) {
            inventoryDetailSamountTradeResponseBaseResponse = getInventoryByOId(inventoryDetailSamountTradeRequest);
        } else if (CollectionUtils.isNotEmpty(inventoryDetailSamountTradeRequest.getGoodsInfoIds()) || Objects.nonNull(inventoryDetailSamountTradeRequest.getGoodsInfoId())) {
            inventoryDetailSamountTradeResponseBaseResponse = getNoTiinventoryDetailSamount(inventoryDetailSamountTradeRequest);
        } else {
            throw new SbcRuntimeException("没有获取到查询订单商品价格列表参数, 请重试!");
        }
        //初始化老数据
        if (inventoryDetailSamountTradeResponseBaseResponse == null || inventoryDetailSamountTradeResponseBaseResponse.getContext() == null
                || CollectionUtils.isEmpty(inventoryDetailSamountTradeResponseBaseResponse.getContext().getInventoryDetailSamountTradeVOS())) {

            List<InventoryDetailSamountTrade> inventoryDetailSamountTrades = inventoryDetailSamountTradeService
                    .saveInitializeGoodsShareMoney(inventoryDetailSamountTradeRequest.getOid(), inventoryDetailSamountTradeRequest.getReturnId());
            inventoryDetailSamountTradeResponseBaseResponse = BaseResponse.success(InventoryDetailSamountTradeResponse
                    .builder().inventoryDetailSamountTradeVOS(KsBeanUtil.convert(inventoryDetailSamountTrades, InventoryDetailSamountTradeVO.class)).build());
        }

        return inventoryDetailSamountTradeResponseBaseResponse;
    }

    /**
     * 通过订单id获取所有商品价格
     * -- 此方法已处理老数据
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> getInventoryAllByOId(@Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        List<InventoryDetailSamountTrade> inventoryByReturnId = inventoryDetailSamountTradeService.findByTradeId(inventoryDetailSamountTradeRequest.getOid());
        return BaseResponse.success(InventoryDetailSamountTradeResponse.builder().inventoryDetailSamountTradeVOS(KsBeanUtil.convert(inventoryByReturnId, InventoryDetailSamountTradeVO.class)).build());
    }


    /**
     * 通过退单id,设置商品成未退款状态
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> updateInventoryByReturnIdBack(@Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        inventoryDetailSamountTradeService.updateInventoryByReturnId(inventoryDetailSamountTradeRequest.getReturnId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商品批量(通过退单id)
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> getInventoryByReturnId(@Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        List<InventoryDetailSamountTrade> inventoryByReturnId = inventoryDetailSamountTradeService.getInventoryByReturnId(inventoryDetailSamountTradeRequest.getReturnId());
        return BaseResponse.success(InventoryDetailSamountTradeResponse.builder().inventoryDetailSamountTradeVOS(KsBeanUtil.convert(inventoryByReturnId, InventoryDetailSamountTradeVO.class)).build());
    }

    /**
     * 通过订单id获取未退款的商品
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> getInventoryByOId(@Valid InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        List<InventoryDetailSamountTrade> inventoryByReturnId = inventoryDetailSamountTradeService.getInventoryByOId(inventoryDetailSamountTradeRequest.getOid());
        return BaseResponse.success(InventoryDetailSamountTradeResponse.builder().inventoryDetailSamountTradeVOS(KsBeanUtil.convert(inventoryByReturnId, InventoryDetailSamountTradeVO.class)).build());
    }

    /**
     * 获取未退款的商品, 通过 . . .
     *
     * @param inventoryDetailSamountTradeRequest
     * @return
     */
    @Override
    public BaseResponse<InventoryDetailSamountTradeResponse> getNoTiinventoryDetailSamount(InventoryDetailSamountTradeRequest inventoryDetailSamountTradeRequest) {
        List<InventoryDetailSamountTrade> noTiinventoryDetailSamount = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(inventoryDetailSamountTradeRequest.getGoodsInfoIds())) {
            noTiinventoryDetailSamount = inventoryDetailSamountTradeService.getNoTiinventoryDetailSamounts
                    (inventoryDetailSamountTradeRequest.getOid(), inventoryDetailSamountTradeRequest.getGoodsInfoIds());
        } else if (Objects.nonNull(inventoryDetailSamountTradeRequest.getGoodsInfoId())) {
            noTiinventoryDetailSamount = inventoryDetailSamountTradeService.getNoTiinventoryDetailSamount
                    (inventoryDetailSamountTradeRequest.getOid(), inventoryDetailSamountTradeRequest.getGoodsInfoId());
        }
        return BaseResponse.success(InventoryDetailSamountTradeResponse.builder().inventoryDetailSamountTradeVOS(KsBeanUtil.convert(noTiinventoryDetailSamount, InventoryDetailSamountTradeVO.class)).build());
    }
}

