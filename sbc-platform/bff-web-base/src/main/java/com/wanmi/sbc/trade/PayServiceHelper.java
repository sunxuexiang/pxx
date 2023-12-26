package com.wanmi.sbc.trade;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.order.api.provider.trade.PileTradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByParentRequest;
import com.wanmi.sbc.order.api.request.trade.TradeListByParentIdRequest;
import com.wanmi.sbc.order.bean.enums.*;
import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>客户端支付公共方法</p>
 * Created by of628-wenzhi on 2019-07-24-19:56.
 */
@Service
public class PayServiceHelper {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private PileTradeQueryProvider pileTradeQueryProvider;

    @Autowired
    private NewPileTradeProvider newPileTradeProvider;

    /**
     * 获取可用于支付交易的订单号（子订单号或父订单号）
     *
     * @param id
     * @param parentId
     * @return
     */
    public String getPayBusinessId(String id, String parentId) {
        return StringUtils.isNotBlank(id) ? id : parentId;
    }

    /**
     * 根据订单号或父订单号获取订单信息，用于支付前获取订单信息
     *
     * @param businessId 订单号（单笔支付）或 父订单号（多笔订单合并支付）
     * @return 订单信息集合
     */
    public List<TradeVO> findTrades(String businessId) {
        List<TradeVO> tradeVOList = new ArrayList<>();
        if (businessId.contains(GeneratorService._PREFIX_PARENT_TRADE_ID)) {
            tradeVOList.addAll(tradeQueryProvider.getListByParentId(TradeListByParentIdRequest.builder().parentTid(businessId)
                    .build()).getContext().getTradeVOList());
        } else if (businessId.startsWith(GeneratorService._PREFIX_TRADE_ID)
                || businessId.startsWith(GeneratorService._PREFIX_RETAIL_TRADE_ID)
                || businessId.startsWith(GeneratorService._PREFIX_BULK_TRADE_ID)
        ){
            tradeVOList.add(tradeQueryProvider.getById(TradeGetByIdRequest.builder()
                    .tid(businessId).build()).getContext().getTradeVO());
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return tradeVOList;
    }

    /**
     * 根据订单号或父订单号获取订单信息，用于支付前获取订单信息
     *
     * @param businessId 订单号（单笔支付）或 父订单号（多笔订单合并支付）
     * @return 订单信息集合
     */
    public List<NewPileTradeVO> findNewPileTrades(String businessId) {
        List<NewPileTradeVO> tradeVOList = new ArrayList<>();
        if (businessId.startsWith(GeneratorService._NEW_PILE_PARENT_PREFIX_TRADE_ID)) {
            //查询多商家囤货订单
            tradeVOList.addAll(newPileTradeProvider.getListByParentId(TradeGetByParentRequest.builder()
                    .parentId(businessId).build()).getContext());
        } else if (businessId.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {
            tradeVOList.add(newPileTradeProvider.getById(TradeGetByIdRequest.builder()
                    .tid(businessId).build()).getContext().getTradeVO());
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return tradeVOList;
    }

    /**
     * 根据订单号或父订单号获取订单信息，用于支付前获取订单信息
     * 囤货专用
     * @param businessId 订单号（单笔支付）或 父订单号（多笔订单合并支付）
     * @return 订单信息集合
     */
    public List<TradeVO> findPileTrades(String businessId) {
        List<TradeVO> tradeVOList = new ArrayList<>();
        if (businessId.startsWith(GeneratorService._PREFIX_TRADE_ID) || businessId.startsWith(GeneratorService._PREFIX_RETAIL_TRADE_ID)) {
            tradeVOList.add(pileTradeQueryProvider.getById(TradeGetByIdRequest.builder()
                    .tid(businessId).build()).getContext().getTradeVO());
        } else if (businessId.startsWith(GeneratorService._PREFIX_PARENT_TRADE_ID)) {
            tradeVOList.addAll(pileTradeQueryProvider.getListByParentId(TradeListByParentIdRequest.builder().parentTid(businessId)
                    .build()).getContext().getTradeVOList());
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return tradeVOList;
    }

    /**
     * 公共方法，支付前校验订单状态，已作废，未审核并且已支付的订单做异常处理
     *
     * @param tradeList 订单列表
     */
    public void checkPayBefore(List<TradeVO> tradeList) {
        tradeList.forEach(i -> {
            if ((i.getTradeState().getFlowState() == FlowState.INIT) || (i.getTradeState().getFlowState() ==
                    FlowState.VOID)) {
                throw new SbcRuntimeException("K-050206");
            }
            if (i.getTradeState().getPayState() == PayState.PAID) {
                throw new SbcRuntimeException("K-100203");
            }
        });
    }

    /**
     * 公共方法，支付前校验订单状态，已作废，未审核并且已支付的订单做异常处理
     *
     * @param tradeList 订单列表
     */
    public void checkPayBeforeNewPile(List<NewPileTradeVO> tradeList) {
        tradeList.forEach(i -> {
            if (i.getTradeState().getFlowState() == NewPileFlowState.VOID) {
                throw new SbcRuntimeException("K-050206");
            }
            if (i.getTradeState().getPayState() == PayState.PAID) {
                throw new SbcRuntimeException("K-100203");
            }
        });
    }

    public List<TradeVO> checkPileTrades(String id) {
        List<TradeVO> trades = findPileTrades(id);
        checkPayBefore(trades);
        return trades;
    }

    public List<TradeVO> checkTrades(String id) {
        List<TradeVO> trades = findTrades(id);
        checkPayBefore(trades);
        return trades;
    }

    public List<NewPileTradeVO> checkNewPileTrades(String id) {
        List<NewPileTradeVO> trades = findNewPileTrades(id);
        checkPayBeforeNewPile(trades);
        return trades;
    }


    public BigDecimal calcTotalPriceByPenny(List<TradeVO> trades) {
        //订单总金额
        BigDecimal reduce = BigDecimal.ZERO;
        for(int i=0;i<trades.size();i++){
            TradeVO trade = trades.get(i);
            if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
                reduce = reduce.add(trade.getTradePrice().getDeliveryPrice()).multiply(new BigDecimal(100))
                        .setScale(0, BigDecimal.ROUND_DOWN);
            }else{
                reduce = reduce.add(
                        trade.getTradePrice().getDeliveryPrice()
                                .add(trade.getTradeItems().stream()
                                        .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                                        .map(TradeItemVO::getSplitPrice)
                                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                                .add(trade.getTradePrice().getPackingPrice())
                                .multiply(new BigDecimal(100))
                                .setScale(0, BigDecimal.ROUND_DOWN)
                );
            }
        }
        return reduce;
       /* return trades.stream().map(i -> i.getTradePrice().getTotalPrice().multiply(new BigDecimal(100))
                .setScale(0, BigDecimal.ROUND_DOWN)).reduce(BigDecimal.ZERO, BigDecimal::add);*/
    }

    public BigDecimal calcTotalPriceByPennyPile(List<NewPileTradeVO> trades) {
        //订单总金额
        return trades.stream().map(
                i -> i.getTradePrice().getDeliveryPrice().add(
                        i.getTradeItems().stream()
                            .filter(tradeItem -> tradeItem.getSplitPrice() != null && tradeItem.getSplitPrice().compareTo(BigDecimal.ZERO) > 0)
                            .map(TradeItemVO::getSplitPrice)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
        ).add(i.getTradePrice().getPackingPrice())
                        .multiply(new BigDecimal(100))
                        .setScale(0, BigDecimal.ROUND_DOWN)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcTotalPriceByYuan(List<TradeVO> trades) {
        //订单总金额
        return trades.stream().map(i -> i.getTradePrice().getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String buildBody(List<TradeVO> trades) {
        TradeVO trade = trades.get(0);
        String body = StringUtils.substring(trade.getTradeItems().get(0).getSkuName(),0,20) + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trades.size() > 1 || trade.getTradeItems().size() > 1) {
            body = body + " 等多件商品";
        }
        return body;
    }

    public String buildBodyNewPile(List<NewPileTradeVO> trades) {
        NewPileTradeVO trade = trades.get(0);
        String body = StringUtils.substring(trade.getTradeItems().get(0).getSkuName(),0,20) + " " + (trade.getTradeItems().get(0).getSpecDetails
                () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
        if (trades.size() > 1 || trade.getTradeItems().size() > 1) {
            body = body + " 等多件商品";
        }
        return body;
    }
}
