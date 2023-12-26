package com.wanmi.sbc.returnorder.shopcart.ChainHandle;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.returnorder.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.returnorder.api.response.purchase.StockAndPureChainNodeRsponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InventoryVirtualCheckNode implements StockAndPureChainNode{
    @Autowired
    private PileActivityProvider pileActivityProvider;


    //囤货虚拟库存是没有拆箱的
    @Override
    public StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request) {
        log.info("囤货虚拟库存=========="+request);
        StockAndPureChainNodeRsponse res =new StockAndPureChainNodeRsponse();
        List<DevanningGoodsInfoPureVO> relist =new LinkedList<>();
        if (CollectionUtils.isNotEmpty(request.getCheckPure())){
            //数量
            Map<String, BigDecimal> collect = request.getCheckPure().stream()
                    .collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, DevanningGoodsInfoMarketingVO::getBigBuyCount, (a, b) -> a));
            List<DevanningGoodsInfoMarketingVO> list = new ArrayList();
            KsBeanUtil.copyList(request.getCheckPure(),list);
            //验证库存（marketing 虚拟库存）
            List<String> collect1 = list.stream().map(DevanningGoodsInfoMarketingVO::getGoodsInfoId).collect(Collectors.toList());

            BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();
            if(CollectionUtils.isEmpty(startPileActivity.getContext())){
                throw new SbcRuntimeException("K-050137", "无正在进行中的囤货活动");
            }
            List<PileActivityGoodsVO> context = pileActivityProvider.getStartPileActivityPileActivityGoods(
                    PileActivityPileActivityGoodsRequest.builder().goodsInfoIds(collect1).build()).getContext();

            Map<String, Long> collect2 = new HashMap<>();
            if (CollectionUtils.isNotEmpty(context)) {
                collect2= context.stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, PileActivityGoodsVO::getVirtualStock));
            }
            Map<String, DevanningGoodsInfoMarketingVO> collect3 = request.getCheckPure().stream().collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, Function.identity(),(a, b)->a));
            log.info("数据检查=========="+collect);
            log.info("数据检查=========="+collect2);
            for (DevanningGoodsInfoMarketingVO i :list){
                if (collect.get(i.getGoodsInfoId()).compareTo(BigDecimal.valueOf(collect2.get(i.getGoodsInfoId())))>0 ){
                    i.setBuyCount(collect3.get(i.getGoodsInfoId()).getBuyCount());
                    i.setDevanningId(collect3.get(i.getGoodsInfoId()).getDevanningId());
                    i.setGoodsInfoImg(collect3.get(i.getGoodsInfoId()).getGoodsInfoImg());
                    i.setDivisorFlag(collect3.get(i.getGoodsInfoId()).getDivisorFlag());
                    i.setMarketPrice(collect3.get(i.getGoodsInfoId()).getMarketPrice());
                    DevanningGoodsInfoPureVO convert = KsBeanUtil.convert(i, DevanningGoodsInfoPureVO.class);
                    convert.setType(4);
                    convert.setMaxPurchase(BigDecimal.valueOf(collect2.get(i.getGoodsInfoId())));
                    convert.setAlreadyNum(BigDecimal.ZERO);
                    relist.add(convert);
                }
            }
        }
        res.setCheckPure(relist);
        return res;
    }
}
