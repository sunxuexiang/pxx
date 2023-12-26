package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.WalletUserRecord;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WalletUserRecordQueryBuilder {

    public static Specification<WalletUserRecord> queryByStoreId(String storeId,String customerId) {
        return (root, cquery, cbuild)->{
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(storeId)){
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            if(Objects.nonNull(customerId)) {
                predicates.add(cbuild.equal(root.get("customerId"), customerId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
