package com.wanmi.sbc.marketing.marketing.strategy.check;


import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.repository.MarketingRepository;
import com.wanmi.sbc.marketing.util.error.MarketingErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * 检查相同活动之间是否有冲突(已经发布的活动，或者是正在执行中的活动)
 */
@Slf4j
@Component
public class MarketingTimeConflictCheckNode implements MarketingCheckChainNode {

    @Autowired
    private MarketingRepository marketingRepository;


    @Override
    public MarketingCheckResult checkIt(SaveOrUpdateMarketingRequest request) {
        MarketingCheckResult result = new MarketingCheckResult();
        result.setSuccess(true);

        return result;
        // 营销活动忽略草稿
//        if(Objects.nonNull(request.getIsDraft()) && BoolFlag.YES.equals(request.getIsDraft())){
//            return result;
//        }
//
//        // 新增：同一子类型的营销活动时间不能有重叠。
//        // 编辑：排除自己，同一时间的营销活动时间不能有冲突。
//        Optional<Marketing> marketingOptional = marketingRepository.findOne((Specification<Marketing>) (root, cQuery, cBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            Long marketingId = request.getMarketingId();
//            if (Objects.nonNull((marketingId))) {
//                // 编辑时排除当前Marketing
//                predicates.add(cBuilder.notEqual(root.get("marketingId"), marketingId));
//            }
//            predicates.add(cBuilder.equal(root.get("subType"), request.getSubType()));
//            if(Objects.nonNull(request.getStoreId())){
//                predicates.add(cBuilder.notEqual(root.get("storeId"), request.getStoreId()));
//            }
//            predicates.add(cBuilder.notEqual(root.get("delFlag"), DeleteFlag.NO));
//            predicates.add(cBuilder.notEqual(root.get("isPause"), BoolFlag.NO));
//            predicates.add(cBuilder.notEqual(root.get("isDraft"), BoolFlag.NO));
//            predicates.add(cBuilder.notEqual(root.get("terminationFlag"), BoolFlag.NO));
//
//            LocalDateTime beginTime = request.getBeginTime();
//            LocalDateTime endTime = request.getEndTime();
//
//            // 当前传入的时间不能与之前创建的营销活动时间有重叠
//            predicates.add(cBuilder.not(cBuilder.or(
//                cBuilder.greaterThan(root.get("beginTime"), endTime),// >
//                cBuilder.lessThan(root.get("endTime"), beginTime) // <
//            )));
//
//            Predicate[] array = new Predicate[predicates.size()];
//            return cBuilder.and(predicates.toArray(array));
//        });
//
//        if(!marketingOptional.isPresent()){
//            return result;
//        }
//        result.setShowMessage("营销活动时间冲突");
//        result.setResultCode(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT);
//        result.setSuccess(false);
//
//        return result;
    }

}
