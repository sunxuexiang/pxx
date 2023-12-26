package com.wanmi.sbc.marketing.common.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.common.model.root.Marketing;
import com.wanmi.sbc.marketing.common.model.root.MarketingScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * marketing + marketingScope 关联
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingSubTypeRequest extends BaseQueryRequest {

    /**
     * 营销ID
     */
    Long marketingId;

    /**
     * 商品Id集合，查询出对应的营销信息
     */
    List<String> goodsInfoIdList;

    /**
     * 查询某个状态下的营销活动
     */
    MarketingStatus marketingStatus;

    /**
     * 排除某个状态下的营销活动
     */
    MarketingStatus excludeStatus;

    /**
     * 删除状态
     */
    DeleteFlag deleteFlag;

    /**
     * 是否关联活动级别
     */
    private Boolean cascadeLevel;

    /**
     * 门店信息
     */
    private Long storeId;

    /**
     * 具体营销类型
     */
    private MarketingSubType marketingSubType;

    /**
     * 是否激活
     */
    private BoolFlag isPause;

    /**
     * 是否为草稿
     */
    private Boolean isDraft;


    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<Marketing> getWhereCriteria() {
        return (root, cQuery, cBuild) -> {
            cQuery.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            Join<Marketing, MarketingScope> marketingJoin = root.join("marketingScopeList", JoinType.LEFT);

            if(Objects.nonNull(marketingId)){
                predicates.add(cBuild.notEqual(root.get("marketingId"), marketingId));
            }

            if(deleteFlag != null){
                predicates.add(cBuild.equal(root.get("delFlag"), deleteFlag));
            }

            //没写全，需要的可以添加
            if (marketingStatus != null) {
                predicates.add(cBuild.equal(root.get("isPause"), BoolFlag.NO));
                switch (marketingStatus) {
                    case STARTED:
                        predicates.add(cBuild.lessThanOrEqualTo(root.get("beginTime"), LocalDateTime.now()));
                        predicates.add(cBuild.greaterThanOrEqualTo(root.get("endTime"), LocalDateTime.now()));
                        break;
                    default:
                        break;
                }
            }

            if (excludeStatus != null) {
                predicates.add(cBuild.equal(root.get("isPause"), BoolFlag.NO));
                switch (excludeStatus) {
                    case ENDED:
                        predicates.add(cBuild.greaterThan(root.get("endTime"), LocalDateTime.now()));
                        break;
                    default:
                        break;
                }
            }

            if (CollectionUtils.isNotEmpty(goodsInfoIdList)) {
                CriteriaBuilder.In in = cBuild.in(marketingJoin.get("scopeId"));
                for (String goodsId : goodsInfoIdList) {
                    in.value(goodsId);
                }
                predicates.add(in);
            }

            if(!Objects.isNull(storeId) && storeId > 0){
                predicates.add(cBuild.equal(root.get("storeId"), storeId));
            }

            if(!Objects.isNull(marketingSubType)){
                predicates.add(cBuild.equal(root.get("subType"), marketingSubType));
            }
            if(isPause != null) {
                predicates.add(cBuild.equal(root.get("isPause"), BoolFlag.NO));
            }
            if(Objects.nonNull(isDraft)){
                predicates.add(cBuild.equal(root.get("isDraft"), isDraft));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);

            return p.length == 0 ? null : p.length == 1 ? p[0] : cBuild.and(p);
        };
    }

}
