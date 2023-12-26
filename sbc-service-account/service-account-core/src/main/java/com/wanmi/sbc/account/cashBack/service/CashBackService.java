package com.wanmi.sbc.account.cashBack.service;

import com.wanmi.sbc.account.api.request.cashBack.CashBackPageRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsPageRequest;
import com.wanmi.sbc.account.cashBack.model.root.CashBack;
import com.wanmi.sbc.account.cashBack.repository.CashBackRepository;
import com.wanmi.sbc.account.funds.model.root.CustomerFunds;
import com.wanmi.sbc.common.util.XssUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CashBackService {
    @Autowired
    private CashBackRepository cashBackRepository;

    public Page<CashBack> page(CashBackPageRequest request) {
        return cashBackRepository.findAll(buildSpecification(request), request.getPageRequest());
    }

    public CashBack add(CashBack cashBack){
        cashBack.setCreateTime(LocalDateTime.now());
        cashBack.setUpdateTime(LocalDateTime.now());
      return cashBackRepository.save(cashBack);
    }

    public void modify(Integer id,Integer returnStatus){
        cashBackRepository.updateCashBackByIds(id,returnStatus);
    }

    /**
     * 会员资金查询条件封装
     *
     * @param cashBackPageRequest
     * @return
     */
    private Specification<CashBack> buildSpecification(final CashBackPageRequest cashBackPageRequest) {
        return (Root<CashBack> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            //会员账号
            if (StringUtils.isNotBlank(cashBackPageRequest.getCustomerAccount())) {
                predicates.add(cb.like(root.get("customerAccount"), buildLike(cashBackPageRequest.getCustomerAccount())));
            }

            //会员名称
            if (StringUtils.isNotBlank(cashBackPageRequest.getCustomerName())) {
                predicates.add(cb.like(root.get("customerName"), buildLike(cashBackPageRequest.getCustomerName())));
            }

            return cb.and(predicates.toArray(new Predicate[]{}));
        };
    }

    /**
     * 封装SQL语句like模糊匹配字段
     *
     * @param field
     * @return
     */
    private String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }
}
