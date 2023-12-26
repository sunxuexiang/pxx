package com.wanmi.sbc.returnorder.shopcart.ChainHandle;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.marketing.GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.marketing.GoodsMarketingListByCustomerIdResponse;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.marketing.api.request.market.MarketingScopeByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.vo.MarketingPurchaseLimitVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingScopeVO;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.returnorder.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 营销限购校验器
 */
@Component
@Slf4j
public class MarketingPureChainNode implements StockAndPureChainNode{

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;
    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;
    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private GoodsMarketingQueryProvider goodsMarketingQueryProvider;
    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Override
    public StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request1) {
        log.info("营销检查============"+request1);
        StockAndPureChainNodeRsponse res =new StockAndPureChainNodeRsponse();
        List<DevanningGoodsInfoPureVO> relist =new LinkedList<>();
        if (CollectionUtils.isNotEmpty(request1.getCheckPure())){
            //得到goodsinfo:num的数据 因为有拆箱的存在
            request1.getCheckPure().forEach(v->{
                if (Objects.isNull(v.getDivisorFlag())){
                    DevanningGoodsInfoByIdResponse devanningGoodsInfoById = devanningGoodsInfoQueryProvider.getInfoById(
                            DevanningGoodsInfoByIdRequest
                                    .builder()
                                    .devanningId(v.getDevanningId())
                                    .build()).getContext();
                    v.setDivisorFlag(devanningGoodsInfoById.getDevanningGoodsInfoVO().getDivisorFlag());
                }
                v.setBigBuyCount(v.getDivisorFlag().multiply(BigDecimal.valueOf(v.getBuyCount())));
            });
            Map<String, DevanningGoodsInfoMarketingVO> collect3 = request1.getCheckPure().stream().collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, Function.identity(),(a, b)->a));

            //需求变动拆箱不参与营销
            Map<String, BigDecimal> goodinfonum = request1.getCheckPure().stream().filter(v->{
                     if ((Objects.isNull(v.getDivisorFlag())?BigDecimal.ONE:v.getDivisorFlag()).compareTo(BigDecimal.ONE)==0){
                         return true;
                     }
                     return false;
                    })
                    .collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, DevanningGoodsInfoMarketingVO::getBigBuyCount, BigDecimal::add));

            List<DevanningGoodsInfoMarketingVO> list = new ArrayList();
            KsBeanUtil.copyList(request1.getCheckPure(),list);

            for (DevanningGoodsInfoMarketingVO v:list){
                v.setBuyCount(collect3.get(v.getGoodsInfoId()).getBuyCount());
                v.setDevanningId(collect3.get(v.getGoodsInfoId()).getDevanningId());
                v.setGoodsInfoImg(collect3.get(v.getGoodsInfoId()).getGoodsInfoImg());
                v.setDivisorFlag(collect3.get(v.getGoodsInfoId()).getDivisorFlag());
                v.setMarketPrice(collect3.get(v.getGoodsInfoId()).getMarketPrice());

                DevanningGoodsInfoPureVO convert = KsBeanUtil.convert(v, DevanningGoodsInfoPureVO.class);

                if (Objects.isNull(request1.getNeedCheack())?false:request1.getNeedCheack()){  
                    GoodsMarketingListByCustomerIdResponse context = goodsMarketingQueryProvider.listByCustomerIdAndGoodsInfoId
                            (GoodsMarketingListByCustomerIdAndGoodsInfoIdRequest.builder().customerId(request1.getCustomerId())
                            .goodsInfoIds(v.getGoodsInfoId()).build()).getContext();
                    if (CollectionUtils.isNotEmpty(context.getGoodsMarketings())){
                        GoodsMarketingVO goodsMarketingVO = context.getGoodsMarketings().stream().findAny().get();
                        v.setMarketingId(goodsMarketingVO.getMarketingId());
                    }
                }
                if (Objects.nonNull(v.getMarketingId())){
                    //查询营销总限购和单商品限购
                    MarketingScopeByMarketingIdRequest request = new MarketingScopeByMarketingIdRequest();
                    request.setMarketingId(v.getMarketingId());
                    request.setSkuId(v.getGoodsInfoId());

                    BaseResponse<MarketingScopeByMarketingIdResponse> marketingScopeByMarketingIdResponseBaseResponse = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(request);
                    if(Objects.isNull(marketingScopeByMarketingIdResponseBaseResponse)){ continue; }
                    MarketingScopeByMarketingIdResponse marketingScopeByMarketingIdResponse = marketingScopeByMarketingIdResponseBaseResponse.getContext();
                    if(Objects.isNull(marketingScopeByMarketingIdResponse)){ continue; }
                    List<MarketingScopeVO> marketingScopeVOList = marketingScopeByMarketingIdResponse.getMarketingScopeVOList();

                    BigDecimal bigDecimal = goodinfonum.get(v.getGoodsInfoId());
                    if (CollectionUtils.isNotEmpty(marketingScopeVOList)){
                        MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();
                        Long purchaseNum = marketingScopeVO.getPurchaseNum();
                        Long perUserPurchaseNum = marketingScopeVO.getPerUserPurchaseNum();
                        AtomicReference<BigDecimal> marketingunum = new AtomicReference<>(BigDecimal.ZERO);
                        //通过用户id查询当前商品的营销购买数量
                        Map<String,Object> req = new LinkedHashMap<>();
                        req.put("customerId",request1.getCustomerId());
                        req.put("marketingId",v.getMarketingId());
                        req.put("goodsInfoId",v.getGoodsInfoId());
                        List<MarketingPurchaseLimitVO> context1 = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
                        List<MarketingPurchaseLimitVO> context = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
                        if (CollectionUtils.isNotEmpty(context)){
                            List<String> collect = context.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                            //获取生效订单
                            List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
                            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                            context.forEach(q->{
                                if (collect1.contains(q.getTradeId())){
                                    marketingunum.set(marketingunum.get().add(q.getNum()));
                                }
                            });

                        }
                        log.info("比较数据======"+purchaseNum);
                        log.info("比较数据======"+marketingunum.get());
                        log.info("比较数据======"+bigDecimal);
                        if ( Objects.nonNull(purchaseNum) && BigDecimal.valueOf(purchaseNum).compareTo(marketingunum.get().add(bigDecimal))<0 ){
                            convert.setType(2);
                            convert.setMaxPurchase(BigDecimal.valueOf(purchaseNum));
                            convert.setAlreadyNum(marketingunum.get());
                            relist.add(convert);
                            continue;
                        }
                        marketingunum.set(BigDecimal.ZERO);
                        if (CollectionUtils.isNotEmpty(context1)){
                            List<String> collect = context1.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
                            //获取生效订单
                            List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
                            List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
                            context1.forEach(q->{
                                if (collect1.contains(q.getTradeId())){
                                    marketingunum.set(marketingunum.get().add(q.getNum()));
                                }
                            });

                        }
                        log.info("比较数据======"+perUserPurchaseNum);
                        log.info("比较数据======"+marketingunum.get());
                        log.info("比较数据======"+bigDecimal);
                        if (Objects.nonNull(perUserPurchaseNum) && BigDecimal.valueOf(perUserPurchaseNum).compareTo(marketingunum.get().add(bigDecimal))<0){
                            log.info("成功--------------------");
                            convert.setType(3);
                            convert.setMaxPurchase(BigDecimal.valueOf(perUserPurchaseNum));
                            convert.setAlreadyNum(marketingunum.get());
                        }
                    }
                }


                relist.add(convert);
            }
        }
        res.setCheckPure(relist);
        return res;
    }
}
