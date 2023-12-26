package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletSupplierRequest;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 账户余额分页查询动态条件构建
 * @author: jiangxin
 * @create: 2021-08-24 14:15
 */
public class CustomerWalletSupplierPageBuilder {

    public static Specification<CustomerWallet> build(CustomerWalletSupplierRequest request){

        return (root,cquery,cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            /**
             * 客户账户-模糊查询
             */
            if (Objects.nonNull(request.getStartTime()) && Objects.nonNull(request.getEndTime())) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("updateTime"), request.getEndTime()));// <=
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("updateTime"), request.getStartTime())); // >=
            }

            if (CollectionUtils.isNotEmpty(request.getStoredIds())) {
                predicates.add(root.get("storeId").in(request.getStoredIds()));
            }

            if (CollectionUtils.isNotEmpty(request.getCustomerIds())) {
                predicates.add(root.get("customerId").in(request.getCustomerIds()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
