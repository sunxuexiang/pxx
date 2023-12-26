package com.wanmi.sbc.setting.homedelivery.service;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.homedelivery.HomeDeliveryQueryRequest;
import com.wanmi.sbc.setting.homedelivery.model.root.HomeDelivery;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>配送到家动态查询条件构建器</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
public class HomeDeliveryWhereCriteriaBuilder {
    public static Specification<HomeDelivery> build(HomeDeliveryQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 批量查询-主键List
            if (CollectionUtils.isNotEmpty(queryRequest.getHomeDeliveryIdList())) {
                predicates.add(root.get("homeDeliveryId").in(queryRequest.getHomeDeliveryIdList()));
            }

            // 主键
            if (queryRequest.getHomeDeliveryId() != null) {
                predicates.add(cbuild.equal(root.get("homeDeliveryId"), queryRequest.getHomeDeliveryId()));
            }

            // 送货范围(KM)
            if (queryRequest.getDistance() != null) {
                predicates.add(cbuild.equal(root.get("distance"), queryRequest.getDistance()));
            }

            // 模糊查询 - content
            if (StringUtils.isNotEmpty(queryRequest.getContent())) {
                predicates.add(cbuild.like(root.get("content"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getContent()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 大于或等于 搜索条件:生成时间开始
            if (queryRequest.getCreateTimeBegin() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeBegin()));
            }
            // 小于或等于 搜索条件:生成时间截止
            if (queryRequest.getCreateTimeEnd() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("createTime"),
                        queryRequest.getCreateTimeEnd()));
            }

            // 删除标志位
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            // 配送方式
            if (queryRequest.getDeliveryType() != null) {
                predicates.add(cbuild.equal(root.get("deliveryType"), queryRequest.getDeliveryType()));
            }

            //店铺ID
            if(Objects.isNull(queryRequest.getStoreId())){
                predicates.add(cbuild.equal(root.get("storeId"), Constants.BOSS_DEFAULT_STORE_ID));
            }else{
                /*if (queryRequest.getCompanyType() ==0) {//自营的商家用之前平台的物流公司
                    predicates.add(cbuild.in(root.get("storeId")).value(Constants.BOSS_DEFAULT_STORE_ID).value(queryRequest.getStoreId()));
                }else {*/
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
                //}
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
