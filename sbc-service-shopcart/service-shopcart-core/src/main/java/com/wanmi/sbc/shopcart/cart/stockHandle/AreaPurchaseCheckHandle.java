package com.wanmi.sbc.shopcart.cart.stockHandle;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.api.provider.customerarelimitdetail.CustomerAreaLimitDetailProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.customerarealimitdetail.CustomerAreaLimitDetailRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.vo.CustomerAreaLimitDetailVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.shopcart.cart.request.ShopCartRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Log4j2
public class AreaPurchaseCheckHandle extends CheckHandle {
    @Autowired
    private CustomerAreaLimitDetailProvider customerAreaLimitDetailProvider;
    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    public AreaPurchaseCheckHandle(CheckHandle next) {
        super(next);
    }
    @Override
    public  boolean process(ShopCartRequest request) {
        log.info("区域限购校验器=======入参");
        GoodsInfoByIdResponse context1 = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder().goodsInfoId(request.getGoodsInfoId()).build()).getContext();
        if (CollectionUtils.isNotEmpty(context1.getGoodsInfos() )) {
            GoodsInfoVO goodsInfoVOs = context1.getGoodsInfos().stream().findAny().get();
            if (Objects.nonNull(request.getProvinceId()) && Objects.nonNull(request.getCityId())  && StringUtils.isNotBlank(goodsInfoVOs.getAllowedPurchaseArea())) {
                List<Long> allowedPurchaseAreaList = Arrays.stream(goodsInfoVOs.getAllowedPurchaseArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                if (!allowedPurchaseAreaList.contains(request.getCityId()) && !allowedPurchaseAreaList.contains(request.getProvinceId())) {
                    throw new SbcRuntimeException("k-030301","限购无法购买");
                }
                if (StringUtils.isNotBlank(goodsInfoVOs.getSingleOrderAssignArea())){
                    List<Long> singleOrderAssignAreaList = Arrays.stream(goodsInfoVOs.getSingleOrderAssignArea().split(",")).map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(singleOrderAssignAreaList)&& (singleOrderAssignAreaList.contains(request.getProvinceId()) || singleOrderAssignAreaList.contains(request.getCityId()))){
                        //在限购区域内获取商品限购数量 以及客户以及购买的数量
                        Long singleOrderPurchaseNum = goodsInfoVOs.getSingleOrderPurchaseNum();
                        List<CustomerAreaLimitDetailVO> detailVOS = customerAreaLimitDetailProvider.listByCids(CustomerAreaLimitDetailRequest.builder().customerId(request.getCustomerId())
                                .goodsInfoId(request.getGoodsInfoId()).regionIds(allowedPurchaseAreaList).build()).getContext().getDetailVOS();
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
                            if (BigDecimal.valueOf(singleOrderPurchaseNum).compareTo(quyunum.get())<0){
                                throw new SbcRuntimeException("k-030301","限购无法购买");
                            }
                        }
                    }
                }
            }
        }
        CheckHandle next = this.getNext();
        if (Objects.isNull(next)){
            return true;
        }else {
            return  next.process(request);
        }
    }

}
