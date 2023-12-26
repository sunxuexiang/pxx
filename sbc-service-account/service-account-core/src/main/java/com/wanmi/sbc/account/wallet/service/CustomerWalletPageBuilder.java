package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.request.wallet.CustomerWalletRequest;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.common.util.XssUtils;
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
public class CustomerWalletPageBuilder {

    public static Specification<CustomerWallet> build(CustomerWalletRequest request){

        return (root,cquery,cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            /**
             * 客户账户-模糊查询
             */
            if(Objects.nonNull(request.getCustomerAccount())){
                predicates.add(cbuild.like(root.get("customerAccount"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getCustomerAccount()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            /**
             * 客户名称-模糊查询
             */
            if(Objects.nonNull(request.getCustomerName())){
                predicates.add(cbuild.like(root.get("customerName"), StringUtil.SQL_LIKE_CHAR
                        .concat(XssUtils.replaceLikeWildcard(request.getCustomerName()))
                        .concat(StringUtil.SQL_LIKE_CHAR)));
            }

            /**
             * 钱包id
             */
            if(Objects.nonNull(request.getWalletId())){
                predicates.add(cbuild.equal(root.get("walletId"),request.getWalletId()));
            }

            /**
             * 账户状态
             */
            if(Objects.nonNull(request.getCustomerStatus())){
                predicates.add(cbuild.equal(root.get("walletId"),request.getCustomerStatus().toValue()));
            }

            /**
             * 删除标志 默认0-未删除
             */
            if(Objects.nonNull(request.getDelFlag())){
                predicates.add(cbuild.equal(root.get("walletId"),request.getDelFlag().toValue()));
            }else{
                predicates.add(cbuild.equal(root.get("delFlag"), DefaultFlag.NO));
            }

            // 大于或等于 搜索条件:账户余额
            if (request.getMaxBalance() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("balance"),
                        request.getMaxBalance()));
            }
            // 小于或等于 搜索条件:账户余额
            if (request.getMaxBalance() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("balance"),
                        request.getMaxBalance()));
            }

            // 大于或等于 搜索条件:赠送金额
            if (request.getMaxGiveBalance() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("giveBalance"),
                        request.getMaxGiveBalance()));
            }
            // 小于或等于 搜索条件:赠送金额
            if (request.getMinGiveBalance() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("giveBalance"),
                        request.getMinGiveBalance()));
            }

            // 大于或等于 搜索条件:充值金额
            if (request.getMaxRechargeBalance() != null) {
                predicates.add(cbuild.greaterThanOrEqualTo(root.get("giveBalance"),
                        request.getMaxRechargeBalance()));
            }
            // 小于或等于 搜索条件:充值金额
            if (request.getMinRechargeBalance() != null) {
                predicates.add(cbuild.lessThanOrEqualTo(root.get("rechargeBalance"),
                        request.getMinRechargeBalance()));
            }

            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }
}
