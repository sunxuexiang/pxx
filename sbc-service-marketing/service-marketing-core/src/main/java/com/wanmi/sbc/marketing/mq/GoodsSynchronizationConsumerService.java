package com.wanmi.sbc.marketing.mq;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.JmsDestinationConstants;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPurchaseNumDTO;
import com.wanmi.sbc.goods.api.request.info.UpdateGoodsInfoPurchaseNumRequest;
import com.wanmi.sbc.marketing.api.provider.constants.MarketingJmsDestinationConstants;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.response.MarketingResponse;
import com.wanmi.sbc.marketing.common.service.MarketingScopeService;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.util.marketing.MarketingPurchaseNumUtil;
import com.wanmi.sbc.marketing.util.marketing.PurchaseNumDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@EnableBinding(GoodsSink.class)
public class GoodsSynchronizationConsumerService {

    @Autowired
    GoodsInfoProvider goodsInfoProvider;

    @Autowired
    MarketingScopeService marketingScopeService;

    @Autowired
    MarketingService marketingService;

    /**
     * 超过一定时间未支付订单，自动取消订单
     * @param marketingId
     */
    @StreamListener(MarketingJmsDestinationConstants.Q_SYNCHRONIZATION_GOODS_INFO_PURCHASE_NUM_CONSUMER)
    @LcnTransaction
    public void synchronizationGoodsInfoConsumer(Long marketingId) {
        log.info("营销id：{},活动结束同步到goodsInfo，开始运行处理",marketingId);
        Marketing marketingById = marketingService.getMarketingById(marketingId);
        LocalDateTime endTime = marketingById.getEndTime();
        //只有当当前时间大于活动结束时间的时候才去执行逻辑
        if(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() > endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()){
            List<MarketingScope> MarketingScopes = marketingScopeService.findByMarketingId(marketingId);
            if(CollectionUtils.isNotEmpty(MarketingScopes)){
                log.info("=============== 查询到 MarketingScopes 营销活动 开始同步库存 ========================");
                List<PurchaseNumDTO> purchaseNumDTOS = new ArrayList<>();
                MarketingScopes.forEach(marketingScope -> {
                    if(Objects.nonNull(marketingScope.getPurchaseNum()) && marketingScope.getMarketingId().equals(marketingId)){
                        PurchaseNumDTO dto = new PurchaseNumDTO();
                        dto.setMarketingId(marketingScope.getMarketingId());
                        dto.setGoodsInfoId(marketingScope.getScopeId());
                        Long purchaseNum = marketingScope.getPurchaseNum();
                        if(Objects.nonNull(purchaseNum)){
                            Long max = Long.max(marketingScope.getPurchaseNum(), 0L);
                            dto.setPurchaseNum(max);
                        } else {
                            dto.setPurchaseNum(purchaseNum);
                        }
                        purchaseNumDTOS.add(dto);
                    }
                });
                log.info("====================== 需要同步的商品信息:{}",purchaseNumDTOS);
                if(CollectionUtils.isNotEmpty(purchaseNumDTOS)){
                    goodsInfoProvider.updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest.builder().goodsInfoPurchaseNumDTOS(KsBeanUtil.convertList(purchaseNumDTOS, GoodsInfoPurchaseNumDTO.class)).b(false).build());
                }
            }
        }
    }
}
