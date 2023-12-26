package com.wanmi.sbc.marketing.marketing.strategy;

import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.common.service.MarketingService;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftDetail;
import com.wanmi.sbc.marketing.gift.model.root.MarketingFullGiftLevel;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftDetailRepository;
import com.wanmi.sbc.marketing.gift.repository.MarketingFullGiftLevelRepository;
import com.wanmi.sbc.marketing.marketing.builder.FullMarketData;
import com.wanmi.sbc.marketing.marketing.builder.MarketingFullGiftLevelWrapper;
import com.wanmi.sbc.marketing.marketing.builder.NewMarketingBuilder;
import com.wanmi.sbc.marketing.marketing.strategy.check.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ReachGiftMarketingStrategy extends MarketingStrategy {

    /**
     * 通过 service-marketing-app >> resource >> spring-plugin.xml 文件注入
     */
    @Resource(name = "reachGiftMarketingCheckList")
    List<MarketingCheckChainNode> checkList;

    @Autowired
    private MarketingRepository marketingRepository;
    @Autowired
    private MarketingScopeRepository marketingScopeRepository;
    @Autowired
    private MarketingFullGiftLevelRepository marketingFullGiftLevelRepository;
    @Autowired
    private MarketingFullGiftDetailRepository marketingFullGiftDetailRepository;
    @Autowired
    private MarketingService marketingService;

    @Override
    public String getMarketingTypeEnum() {
        return MarketingType.GIFT.name();
    }

    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();
        if (CollectionUtils.isEmpty(checkList)) {
            result.setShowMessage("检查链为空");
            result.setSuccess(false);
            return result;
        }
        try {
            for (MarketingCheckChainNode checkNode : checkList) {
                MarketingCheckResult curResult = checkNode.checkIt(request);
                result = Objects.isNull(curResult) ? result : curResult;
                if (!result.isSuccess()) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setResultCode("-1");
            result.setSuccess(false);
            result.setShowMessage("执行检查链时发生异常");
            return result;
        }
        return result;
    }

    @Override
    public Marketing saveOrUpdate(SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest) {

        NewMarketingBuilder newMarketingBuilder = new NewMarketingBuilder(saveOrUpdateMarketingRequest);

        Long marketingId = saveOrUpdateMarketingRequest.getMarketingId();
        if(Objects.nonNull(marketingId)){
            Optional<Marketing> marketingOptional = Optional.ofNullable(marketingRepository.findByMarketingId(marketingId));
            if(marketingOptional.isPresent()){
                Marketing marketingRecord = marketingOptional.get();
                newMarketingBuilder =  new NewMarketingBuilder(saveOrUpdateMarketingRequest, marketingRecord);

                // 清理关联数据
                detachRelationship(marketingRecord);
            }
        }

        FullMarketData fullMarketData = newMarketingBuilder.buildMarketing()
                .buildMarketingScopes()
                .buildMarketingFullGiftLevelWrappers()
                .build();

        // 保存活动信息
        Marketing marketingDO = fullMarketData.getMarketing();
        Marketing marketing = marketingRepository.save(marketingDO);

        // 保存活动与商品之间的关系
        List<MarketingScope> marketingScopes = fullMarketData.getMarketingScopes();
        for (MarketingScope marketingScope : marketingScopes) {
            marketingScope.setMarketingId(marketing.getMarketingId());
        }
        marketingScopeRepository.saveAll(marketingScopes);
        marketing.setMarketingScopeList(marketingScopes);


        // 保存活动的阶梯信息(最多5次循环)
        List<MarketingFullGiftLevel> marketingFullGiftLevels = new ArrayList<>();
        List<MarketingFullGiftLevelWrapper> marketingFullGiftLevelWrappers = fullMarketData.getMarketingFullGiftLevelWrappers();
        for (MarketingFullGiftLevelWrapper marketingFullGiftLevelWrapper : marketingFullGiftLevelWrappers){
            MarketingFullGiftLevel marketingFullGiftLevelDO = marketingFullGiftLevelWrapper.getMarketingFullGiftLevel();
            marketingFullGiftLevelDO.setMarketingId(marketing.getMarketingId());
            MarketingFullGiftLevel marketingFullGiftLevel = marketingFullGiftLevelRepository.save(marketingFullGiftLevelDO);

            List<MarketingFullGiftDetail> marketingFullGiftDetailSegment = marketingFullGiftLevelWrapper.getMarketingFullGiftDetails();
            for (MarketingFullGiftDetail marketingFullGiftDetail : marketingFullGiftDetailSegment) {
                marketingFullGiftDetail.setMarketingId(marketing.getMarketingId());
                marketingFullGiftDetail.setGiftLevelId(marketingFullGiftLevel.getGiftLevelId());
            }
            List<MarketingFullGiftDetail> marketingFullGiftDetails = marketingFullGiftDetailRepository.saveAll(marketingFullGiftDetailSegment);

            //赠品限赠数量存储到redis
            marketingFullGiftDetails.forEach(var ->{
                if(Objects.nonNull(var.getBoundsNum()) && var.getBoundsNum() > 0){
                    marketingService.updateMarketingGiftNum(
                            marketing.getMarketingId().toString(),
                            marketingFullGiftLevel.getGiftLevelId().toString(),
                            var.getProductId(),var.getBoundsNum()
                    );
                }
            });

            marketingFullGiftLevel.setMarketingFullGiftDetails(marketingFullGiftDetails);
            marketingFullGiftLevel.setFullGiftDetailList(marketingFullGiftDetails);
            marketingFullGiftLevels.add(marketingFullGiftLevel);
        }
        marketing.setMarketingFullGiftLevels(marketingFullGiftLevels);

        return marketing;
    }

    public void setCheckList(List<MarketingCheckChainNode> checkList) {
        this.checkList = checkList;
    }

    /**
     * 移除关联数据
     * @param marketingRecord
     */
    private void detachRelationship(Marketing marketingRecord){
        marketingScopeRepository.deleteByMarketingId(marketingRecord.getMarketingId());
        marketingFullGiftLevelRepository.deleteByMarketingId(marketingRecord.getMarketingId());
        marketingFullGiftDetailRepository.deleteByMarketingId(marketingRecord.getMarketingId());
    }
}