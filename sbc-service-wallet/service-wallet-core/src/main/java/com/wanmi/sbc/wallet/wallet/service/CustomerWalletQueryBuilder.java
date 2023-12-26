package com.wanmi.sbc.wallet.wallet.service;

import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerWalletQueryBuilder {

    public static Specification<CustomerWallet> queryByCustomerId(String customerId){
        return (root, cquery, cbuild)->{
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(customerId)){
                predicates.add(cbuild.equal(root.get("customerId"), customerId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Specification<CustomerWallet> queryByStoreId(String storeId) {
        return (root, cquery, cbuild)->{
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(storeId)){
                predicates.add(cbuild.equal(root.get("storeId"), storeId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public static Specification<CustomerWallet> queryByWalletId (Long walletId) {
        return (root, cquery, cbuild)->{
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(walletId)){
                predicates.add(cbuild.equal(root.get("walletId"), walletId));
            }
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
