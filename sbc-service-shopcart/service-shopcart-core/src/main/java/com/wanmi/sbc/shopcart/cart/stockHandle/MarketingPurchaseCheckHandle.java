package com.wanmi.sbc.shopcart.cart.stockHandle;


import com.wanmi.sbc.goods.api.provider.marketing.GoodsMarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.marketingpurchaselimit.MarketingPurchaseLimitProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.shopcart.cart.request.ShopCartRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class MarketingPurchaseCheckHandle extends CheckHandle{
    @Autowired
    private GoodsMarketingQueryProvider goodsMarketingQueryProvider;
    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;
    @Autowired
    private MarketingPurchaseLimitProvider marketingPurchaseLimitProvider;
    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    public MarketingPurchaseCheckHandle(CheckHandle next){
        super(next);
    }

    @Override
    public boolean process(ShopCartRequest request1) {
        return true;
//        log.info("营销限购=======入参"+ JSON.toJSONString(request1));
//        if (Objects.isNull(request1.getGoodsInfoId())){
//            throw new SbcRuntimeException("传参缺少关键参数为GoodsInfoId");
//        }
//        if (Objects.nonNull(request1.getMarketingId())){
//                //查询营销总限购和单商品限购
//                MarketingScopeByMarketingIdRequest request = new MarketingScopeByMarketingIdRequest();
//                request.setMarketingId(request1.getMarketingId());
//                request.setSkuId(request1.getGoodsInfoId());
//                List<MarketingScopeVO> marketingScopeVOList = marketingScopeQueryProvider.listByMarketingIdAndSkuIdAndCache(request).getContext().getMarketingScopeVOList();
//                if (CollectionUtils.isNotEmpty(marketingScopeVOList)){
//                    MarketingScopeVO marketingScopeVO = marketingScopeVOList.stream().findFirst().get();
//                    Long purchaseNum = marketingScopeVO.getPurchaseNum();
//                    Long perUserPurchaseNum = marketingScopeVO.getPerUserPurchaseNum();
//                    Long marketingId = marketingScopeVO.getMarketingId();
//                    AtomicReference<BigDecimal> marketingunum = new AtomicReference<>(BigDecimal.ZERO);
//                    //通过用户id查询当前商品的营销购买数量
//                    Map<String,Object> req = new LinkedHashMap<>();
//                    req.put("customerId",request1.getCustomerId());
//                    req.put("marketingId",request1.getMarketingId());
//                    req.put("goodsInfoId",request1.getGoodsInfoId());
//                    List<MarketingPurchaseLimitVO> context1 = marketingPurchaseLimitProvider.queryListByParm(req).getContext();
//                    List<MarketingPurchaseLimitVO> context = marketingPurchaseLimitProvider.queryListByParmNoUser(req).getContext();
//                    if (CollectionUtils.isNotEmpty(context)){
//                        List<String> collect = context.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
//                        //获取生效订单
//                        List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
//                        List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
//                        context.forEach(q->{
//                            if (collect1.contains(q.getTradeId())){
//                                marketingunum.set(marketingunum.get().add(q.getNum()));
//                            }
//                        });
//                        if (BigDecimal.valueOf(purchaseNum).compareTo(marketingunum.get())<0){
//                            throw new RuntimeException("营销限购已超额满足");
//                        }
//                    }
//                    if (CollectionUtils.isNotEmpty(context1)){
//                        List<String> collect = context1.stream().map(MarketingPurchaseLimitVO::getTradeId).collect(Collectors.toList());
//                        //获取生效订单
//                        List<TradeVO> context2 = tradeQueryProvider.getOrderByIds(collect).getContext();
//                        List<String> collect1 = context2.stream().map(TradeVO::getId).collect(Collectors.toList());
//                        context1.forEach(q->{
//                            if (collect1.contains(q.getTradeId())){
//                                marketingunum.set(marketingunum.get().add(q.getNum()));
//                            }
//                        });
//                        if (BigDecimal.valueOf(perUserPurchaseNum).compareTo(marketingunum.get())<0){
//                            throw new RuntimeException("个人限购已超额满足");
//                        }
//                    }
//                }
//        }
//        CheckHandle next = this.getNext();
//        if (Objects.isNull(next)){
//            return true;
//        }else {
//            return  next.process(request1);
//        }
    }
}
