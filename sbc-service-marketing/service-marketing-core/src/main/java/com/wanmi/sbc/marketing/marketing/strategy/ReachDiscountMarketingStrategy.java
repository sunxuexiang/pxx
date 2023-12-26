package com.wanmi.sbc.marketing.marketing.strategy;

import com.google.common.collect.Lists;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.common.repository.MarketingScopeRepository;
import com.wanmi.sbc.marketing.discount.model.entity.MarketingFullDiscountLevel;
import com.wanmi.sbc.marketing.discount.repository.MarketingFullDiscountLevelRepository;
import com.wanmi.sbc.marketing.marketing.builder.FullMarketData;
import com.wanmi.sbc.marketing.marketing.builder.NewMarketingBuilder;
import com.wanmi.sbc.marketing.marketing.strategy.check.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ReachDiscountMarketingStrategy extends MarketingStrategy {

    /**
     * 通过 service-marketing-app >> resource >> spring-plugin.xml 文件注入
     */
    @Resource(name = "reachDiscountMarketingCheckList")
    private List<MarketingCheckChainNode> checkList;

    @Autowired
    private MarketingRepository marketingRepository;
    @Autowired
    private MarketingScopeRepository marketingScopeRepository;
    @Autowired
    private MarketingFullDiscountLevelRepository marketingFullDiscountLevelRepository;

    @Override
    public String getMarketingTypeEnum() {
        return MarketingType.DISCOUNT.name();
    }

    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {

        MarketingCheckResult result = new MarketingCheckResult();
        if (CollectionUtils.isEmpty(checkList)) {
            result.setShowMessage("检查链为空");
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
                marketingScopeRepository.deleteByMarketingId(marketingRecord.getMarketingId());
                marketingFullDiscountLevelRepository.deleteByMarketingId(marketingRecord.getMarketingId());
            }
        }

        FullMarketData fullMarketData = newMarketingBuilder.buildMarketing()
                .buildMarketingScopes()
                .buildMarketingFullDiscountLevels()
                .build();

        // 保存活动信息
        Marketing marketingDO = fullMarketData.getMarketing();
        Marketing marketing = marketingRepository.save(marketingDO);

        // 保存活动与商品之间的关系
        List<MarketingScope> marketingScopes = fullMarketData.getMarketingScopes();
        for (MarketingScope marketingScope : marketingScopes) {
            marketingScope.setMarketingId(marketing.getMarketingId());
        }
        // 该集合总数可能很大，分批插入数据
        List<List<MarketingScope>> partitionMarketingScopes = Lists.partition(marketingScopes, 500);
        for (List<MarketingScope> marketingScopeList : partitionMarketingScopes) {
            marketingScopeRepository.saveAll(marketingScopeList);
        }
        marketing.setMarketingScopeList(marketingScopes);

        // 保存活动的阶梯信息
        List<MarketingFullDiscountLevel> marketingFullDiscountLevels = fullMarketData.getMarketingFullDiscountLevels();
        for (MarketingFullDiscountLevel marketingFullDiscountLevel : marketingFullDiscountLevels) {
            marketingFullDiscountLevel.setMarketingId(marketing.getMarketingId());
        }
        marketingFullDiscountLevelRepository.saveAll(marketingFullDiscountLevels);
        marketing.setMarketingFullDiscountLevels(marketingFullDiscountLevels);

        return marketing;
    }

    public void setCheckList(List<MarketingCheckChainNode> checkList) {
        this.checkList = checkList;
    }
}