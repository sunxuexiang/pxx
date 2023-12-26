package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.request.wallet.CustomerBankCardRequest;
import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 银行卡分页查询条件动态构建
 * @author: jiangxin
 * @create: 2021-08-23 11:50
 */
public class CustomerBankCardQueryBuilder {

    public static Specification<CustomerBindBankCard> build(CustomerBankCardRequest request){

        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(Objects.nonNull(request.getBankCode())){
                predicates.add(cbuild.like(root.get("bankCode"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getBankCode()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(Objects.nonNull(request.getWalletId())){
                predicates.add(cbuild.equal(root.get("walletId"),request.getWalletId()));
            }
            if(Objects.nonNull(request.getBankBranch())){
                predicates.add(cbuild.like(root.get("bankBranch"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getBankBranch()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(Objects.nonNull(request.getBankName())){
                predicates.add(cbuild.like(root.get("bankName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getBankName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(Objects.nonNull(request.getCardHolder())){
                predicates.add(cbuild.like(root.get("cardHolder"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getCardHolder()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }
            if(Objects.nonNull(request.getBindPhone())){
                predicates.add(cbuild.equal(root.get("bindPhone"),request.getBindPhone()));
            }
            if(Objects.nonNull(request.getCardType())){
                predicates.add(cbuild.equal(root.get("cardType"),request.getCardType()));
            }
            if(Objects.nonNull(request.getDelFlag())){
                predicates.add(cbuild.equal(root.get("delFlag"),request.getDelFlag().toValue()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
