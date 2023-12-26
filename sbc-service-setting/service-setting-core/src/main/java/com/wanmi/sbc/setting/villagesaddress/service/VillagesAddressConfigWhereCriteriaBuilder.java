package com.wanmi.sbc.setting.villagesaddress.service;

import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.villagesaddress.model.root.VillagesAddressConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 动态构建乡镇件地址配置查询条件
 * @author: XinJiang
 * @time: 2022/4/29 11:28
 */
public class VillagesAddressConfigWhereCriteriaBuilder {
    public static Specification<VillagesAddressConfig> build(VillagesAddressConfigQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 省
            if (queryRequest.getProvinceId() != null) {
                predicates.add(cbuild.equal(root.get("provinceId"), queryRequest.getProvinceId()));
            }

            if (CollectionUtils.isNotEmpty(queryRequest.getProvinceIdList())) {
                CriteriaBuilder.In in= cbuild.in(root.get("provinceId"));
                queryRequest.getProvinceIdList().forEach(t->{in.value(t);});
                predicates.add(in);
            }

            // 市
            if (queryRequest.getCityId() != null) {
                predicates.add(cbuild.equal(root.get("cityId"), queryRequest.getCityId()));
            }

            if (CollectionUtils.isNotEmpty(queryRequest.getCityIdList())) {
                CriteriaBuilder.In in= cbuild.in(root.get("cityIdList"));
                queryRequest.getCityIdList().forEach(t->{in.value(t);});
                predicates.add(in);
            }

            // 区
            if (queryRequest.getAreaId() != null) {
                predicates.add(cbuild.equal(root.get("areaId"), queryRequest.getAreaId()));
            }

            // 街道
            if (queryRequest.getVillageId() != null) {
                predicates.add(cbuild.equal(root.get("villageId"), queryRequest.getVillageId()));
            }

            //按街道id批量查询
            if (CollectionUtils.isNotEmpty(queryRequest.getVillagesIds())) {
                predicates.add(root.get("villageId").in(queryRequest.getVillagesIds()));
            }

            // 模糊查询 - 省名
            if (StringUtils.isNotEmpty(queryRequest.getProvinceName())) {
                predicates.add(cbuild.like(root.get("provinceName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getProvinceName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 市名
            if (StringUtils.isNotEmpty(queryRequest.getCityName())) {
                predicates.add(cbuild.like(root.get("cityName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getCityName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 区名
            if (StringUtils.isNotEmpty(queryRequest.getAreaName())) {
                predicates.add(cbuild.like(root.get("areaName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getAreaName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 模糊查询 - 街道名
            if (StringUtils.isNotEmpty(queryRequest.getVillageName())) {
                predicates.add(cbuild.like(root.get("villageName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(queryRequest.getVillageName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            // 删除标志
            if (queryRequest.getDelFlag() != null) {
                predicates.add(cbuild.equal(root.get("delFlag"), queryRequest.getDelFlag()));
            }
            //店铺ID
            if(Objects.isNull(queryRequest.getStoreId())){
                predicates.add(cbuild.equal(root.get("storeId"), Constants.BOSS_DEFAULT_STORE_ID));
            }else{
                if (Objects.nonNull(queryRequest.getCompanyType()) && queryRequest.getCompanyType() ==0) {//自营的商家用之前平台的乡镇件
                    predicates.add(cbuild.in(root.get("storeId")).value(Constants.BOSS_DEFAULT_STORE_ID).value(queryRequest.getStoreId()));
                }else {
                    predicates.add(cbuild.equal(root.get("storeId"), queryRequest.getStoreId()));
                }
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
