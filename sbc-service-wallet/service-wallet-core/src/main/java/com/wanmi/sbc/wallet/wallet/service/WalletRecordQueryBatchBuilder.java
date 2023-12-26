package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.wallet.wallet.model.root.WalletRecord;
import com.wanmi.sbc.wallet.api.request.wallet.WalletRecordQueryRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 动态构建钱包充值查询条件
 * @author: jiangxin
 * @create: 2021-11-03 9:34
 */
public class WalletRecordQueryBatchBuilder {

    public static Specification<WalletRecord> build(WalletRecordQueryRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 客户帐号
            if (CollectionUtils.isNotEmpty(request.getAccounts())) {
                predicates.add(root.get("customerAccount").in( request.getAccounts()));
                predicates.add(cbuild.equal(root.get("tradeType"),0));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
