package com.wanmi.sbc.returnorder.shopcart.ChainHandle;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.CustomerAreaLimitDetailVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoMarketingVO;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoPureVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.returnorder.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.returnorder.api.request.purchase.StockAndPureChainNodeRequeest;
import com.wanmi.sbc.returnorder.api.response.purchase.StockAndPureChainNodeRsponse;
import com.wanmi.sbc.returnorder.bean.vo.TradeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 区域限购检查器
 */
@Component
@Slf4j
public class AreaPurchaseCheckNode implements StockAndPureChainNode{
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;
    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;


    /**
     *区域限购检查器
     * @param request
     * @return
     */
    @Override
    public StockAndPureChainNodeRsponse checkStockPure(StockAndPureChainNodeRequeest request) {
        StockAndPureChainNodeRsponse res =new StockAndPureChainNodeRsponse();
        List<DevanningGoodsInfoPureVO> relist =new LinkedList<>();
        if ( CollectionUtils.isNotEmpty(request.getCheckPure())){

            log.info("========数据"+request);
            request.getCheckPure().forEach(v->{
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
            Map<String, DevanningGoodsInfoMarketingVO> collect3 = request.getCheckPure().stream().collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, Function.identity(),(a,b)->a));
            Map<String, BigDecimal> goodinfonum = request.getCheckPure().stream()
                    .collect(Collectors.toMap(DevanningGoodsInfoMarketingVO::getGoodsInfoId, DevanningGoodsInfoMarketingVO::getBigBuyCount, (a, b) -> a.add(b)));
            List<String> collect2 = request.getCheckPure().stream().map(DevanningGoodsInfoMarketingVO::getGoodsInfoId).collect(Collectors.toList());
            GoodsInfoListByIdsResponse context1 = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(collect2).build()).getContext();
            if (Objects.nonNull(context1)&& CollectionUtils.isNotEmpty(context1.getGoodsInfos())){
                for (GoodsInfoVO q:context1.getGoodsInfos()){
                    q.setBuyCount(collect3.get(q.getGoodsInfoId()).getBuyCount());
                    q.setDevanningId(collect3.get(q.getGoodsInfoId()).getDevanningId());
                    q.setGoodsInfoImg(collect3.get(q.getGoodsInfoId()).getGoodsInfoImg());
                    q.setDivisorFlag(collect3.get(q.getGoodsInfoId()).getDivisorFlag());
                    q.setMarketPrice(collect3.get(q.getGoodsInfoId()).getMarketPrice());
                    log.info("查出的数据=============="+q);
                    if (Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())  && StringUtils.isNotBlank(q.getAllowedPurchaseArea())) {
                        DevanningGoodsInfoPureVO convert = KsBeanUtil.convert(q, DevanningGoodsInfoPureVO.class);
                        List<Long> allowedPurchaseAreaList = Arrays.stream(q.getAllowedPurchaseArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                            convert.setType(-1);
                            convert.setMaxPurchase(BigDecimal.ZERO);
                            relist.add(convert);
                            continue;
                        }
                        if (StringUtils.isNotBlank(q.getSingleOrderAssignArea())){
                            List<Long> singleOrderAssignAreaList = Arrays.stream(q.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)&& (singleOrderAssignAreaList.contains(request.getProvinceId()) || singleOrderAssignAreaList.contains(request.getCityId()))){
                                //在限购区域内获取商品限购数量 以及客户以及购买的数量
                                Long singleOrderPurchaseNum = q.getSingleOrderPurchaseNum();
                                List<CustomerAreaLimitDetailVO> detailVOS = customerAreaLimitDetailProvider.listByCids(CustomerAreaLimitDetailRequest.builder().customerId(request.getCustomerId())
                                        .goodsInfoId(q.getGoodsInfoId())
                                        .regionIds(singleOrderAssignAreaList)
                                        .tid(request.getTid())
                                        .build()).getContext().getDetailVOS();
                                AtomicReference<BigDecimal> quyunum = new AtomicReference(BigDecimal.ZERO);
                                if (CollectionUtils.isNotEmpty(detailVOS)){
                                    //过滤出已经退货或者已经取消的订单
                                    List<String> collect = detailVOS.stream().map(CustomerAreaLimitDetailVO::getTradeId).collect(Collectors.toList());
                                    //传入集合中生效的订单
                                    List<TradeVO> context = tradeQueryProvider.getOrderByIds(collect).getContext();
                                    List<String> collect1 = context.stream().map(TradeVO::getId).collect(Collectors.toList());
                                    detailVOS.forEach(v->{
                                        if (collect1.contains(v.getTradeId())){
                                            quyunum.set(quyunum.get().add(v.getNum()));
                                        }
                                    });

                                }
                                BigDecimal bigDecimal = goodinfonum.get(q.getGoodsInfoId());
                                log.info("比较区域限购==========="+bigDecimal);
                                log.info("比较区域限购==========="+quyunum);
                                log.info("比较区域限购==========="+singleOrderPurchaseNum);
                                if (BigDecimal.valueOf(singleOrderPurchaseNum).compareTo(quyunum.get().add(bigDecimal))<0){
                                    convert.setType(1);
                                    convert.setMaxPurchase(BigDecimal.valueOf(singleOrderPurchaseNum));
                                    convert.setAlreadyNum(quyunum.get());
                                }
                            }
                        }
                        relist.add(convert);

                    }
                }
            }
        }
        res.setCheckPure(relist);
        return res;
    }
}
