package com.wanmi.sbc.setting.advertising.model.root;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingQueryRequest;
import com.wanmi.sbc.setting.api.request.advertising.AdvertisingRetailQueryRequest;
import com.wanmi.sbc.setting.api.request.advertising.StartPageAdvertisingQueryRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 首页广告位公共查询条件条件封装类
 * @author: XinJiang
 * @time: 2022/2/18 11:57
 */
public class AdvertisingWhereCriteriaBuilder {

    public static Specification<Advertising> build(AdvertisingQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询
            if (StringUtils.isNotBlank(queryRequest.getAdvertisingName())) {
                predicates.add(cbuild.like(root.get("advertisingName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAdvertisingName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (Objects.nonNull(queryRequest.getAdvertisingType())) {
                predicates.add(cbuild.equal(root.get("advertisingType"), queryRequest.getAdvertisingType()));
            }

            if (Objects.nonNull(queryRequest.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if (Objects.nonNull(queryRequest.getWareId())) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }
            //新增店铺id 区分运营端与商家端首页广告查询
            if (Objects.nonNull(queryRequest.getStoreId())) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            }
            //新增首页广告位Ids查询
            if (CollectionUtils.isNotEmpty(queryRequest.getAdvertisingIds())) {
                predicates.add(cbuild.in(root.get("advertisingId")).value(queryRequest.getAdvertisingIds()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Specification<AdvertisingRetail> retailBuild(AdvertisingRetailQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询
            if (StringUtils.isNotBlank(queryRequest.getAdvertisingName())) {
                predicates.add(cbuild.like(root.get("advertisingName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAdvertisingName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (Objects.nonNull(queryRequest.getAdvertisingType())) {
                predicates.add(cbuild.equal(root.get("advertisingType"), queryRequest.getAdvertisingType()));
            }

            if (Objects.nonNull(queryRequest.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if (Objects.nonNull(queryRequest.getStatus())) {
                predicates.add(cbuild.equal(root.get("status"), queryRequest.getStatus()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    /**
     * 启动页广告查询条件封装
     * @param request
     * @return
     */
    public static Specification<StartPageAdvertising> buildStartPage(StartPageAdvertisingQueryRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            //模糊查询
            if (StringUtils.isNotBlank(request.getAdvertisingName())) {
                predicates.add(cbuild.like(root.get("advertisingName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getAdvertisingName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (Objects.nonNull(request.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), request.getDelFlag()));
            }

            if (Objects.nonNull(request.getStatus())) {
                predicates.add(cbuild.equal(root.get("status"), request.getStatus()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Specification<Advertising> buildStore(AdvertisingQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //模糊查询
            if (StringUtils.isNotBlank(queryRequest.getAdvertisingName())) {
                predicates.add(cbuild.like(root.get("advertisingName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAdvertisingName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            if (Objects.nonNull(queryRequest.getAdvertisingType())) {
                predicates.add(cbuild.equal(root.get("advertisingType"), queryRequest.getAdvertisingType()));
            }

            if (Objects.nonNull(queryRequest.getDelFlag())) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }

            if (Objects.nonNull(queryRequest.getWareId())) {
                predicates.add(cbuild.equal(root.get("wareId"), queryRequest.getWareId()));
            }
            //区分新需求平台首页白鲸头条广告位落地页中的广告分栏取各个商家的分栏广告位（根据后台配置）
            if (Objects.nonNull(queryRequest.getStoreId())) {
                predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
            } else {
                predicates.add(cbuild.notEqual(root.get("storeId"), -1));
            }
            //新增首页广告位Ids查询
            if (CollectionUtils.isNotEmpty(queryRequest.getAdvertisingIds())) {
                predicates.add(cbuild.in(root.get("advertisingId")).value(queryRequest.getAdvertisingIds()));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
