package com.wanmi.sbc.customer.store.service;

import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.request.store.WalletStoreQueryRequest;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.util.XssUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>店铺分页查询条件动态条件构建</p>
 * Created by of628-wenzhi on 2018-09-11-下午5:05.
 */
public class StoreWhereWalletCriteriaBuilder {
    public static Specification<Store> build(WalletStoreQueryRequest queryRequest) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            //批量查询店铺ID
            if (StringUtils.isNotEmpty(queryRequest.getSupplierAccount())) {
                predicates.add(cbuild.like(root.get("contactMobile"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils
                        .replaceLikeWildcard(queryRequest.getSupplierAccount().trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if (StringUtils.isNotEmpty(queryRequest.getSupplierName())) {
                predicates.add(cbuild.like(root.get("supplierName"), StringUtil.SQL_LIKE_CHAR.concat(XssUtils
                        .replaceLikeWildcard(queryRequest.getSupplierName().trim())).concat(StringUtil.SQL_LIKE_CHAR)));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
