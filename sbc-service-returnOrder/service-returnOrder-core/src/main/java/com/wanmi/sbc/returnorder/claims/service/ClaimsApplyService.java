package com.wanmi.sbc.returnorder.claims.service;

import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsApplyPageRequest;
import com.wanmi.sbc.returnorder.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.returnorder.api.response.manualrefund.RefundForClaimsApplyPageResponse;
import com.wanmi.sbc.returnorder.bean.vo.RefundForClaimsApplyVO;
import com.wanmi.sbc.returnorder.claims.model.root.ClaimsApply;
import com.wanmi.sbc.returnorder.claims.repository.ClaimsApplyRepository;
import com.wanmi.sbc.returnorder.util.XssUtils;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClaimsApplyService {

    @Autowired
    private ClaimsApplyRepository claimsApplyRepository;

    @Autowired
    private GeneratorService generatorService;


    public ClaimsApply saveApply(RefundForClaimsRequest request) {
        ClaimsApply claimsApply = new ClaimsApply();
        claimsApply.setApplyNo(generatorService.generate(GeneratorService._PREFIX_PROVIDER_CLAIMS));
        claimsApply.setCustomerAccount(request.getCustomerAccount());
        claimsApply.setRechargeBalance(request.getRechargeBalance());
        claimsApply.setOrderNo(request.getOrderNo());
        claimsApply.setReturnOrderNo(request.getReturnOrderNo());
        claimsApply.setRemark(request.getRemark());
        //暂不走审核，默认已审核
        claimsApply.setStatus(1);
        claimsApply.setApplyUserId(request.getOperator().getAccount());
        claimsApply.setApproveUserId(request.getOperator().getAccount());
        claimsApply.setApplyUserName(request.getOperator().getName());
        claimsApply.setApproveUserName(request.getOperator().getName());
        claimsApply.setApplyTime(LocalDateTime.now());
        claimsApply.setApproveTime(LocalDateTime.now());
        claimsApply.setApplyType(request.getClaimsApplyType());

        claimsApplyRepository.save(claimsApply);
        return claimsApply;
    }

    public RefundForClaimsApplyPageResponse page(RefundForClaimsApplyPageRequest request) {
        RefundForClaimsApplyPageResponse pageResponse = new RefundForClaimsApplyPageResponse();
        pageResponse.setList(Collections.emptyList());
        pageResponse.setTotal(0L);
        pageResponse.setCurrentPage(request.getPageNum());
        pageResponse.setPageSize(request.getPageSize());

        Page<ClaimsApply> page = claimsApplyRepository.findAll(findByRequest(request),
                request.getPageable());
        pageResponse.setList(this.convertToListVo(page.getContent()));
        pageResponse.setTotal(page.getTotalElements());
        return pageResponse;
    }

    public List<RefundForClaimsApplyVO> queryList(RefundForClaimsApplyPageRequest request) {
        List<ClaimsApply> list= claimsApplyRepository.findAll(findByRequest(request));
        return convertToListVo(list);
    }

    private List<RefundForClaimsApplyVO> convertToListVo(List<ClaimsApply> list) {
        if(CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }

        return list.stream().map(ClaimsApplyService::convertListVo).collect(Collectors.toList());
    }

    private RefundForClaimsApplyVO convertToDetailVo(ClaimsApply item) {
        if (Objects.isNull(item)) {
            return new RefundForClaimsApplyVO();
        }

        RefundForClaimsApplyVO vo = convertListVo(item);
        vo.setRemark(item.getRemark());
        vo.setOrderNo(item.getOrderNo());
        vo.setReturnOrderNo(item.getReturnOrderNo());
        return vo;
    }

    private static RefundForClaimsApplyVO convertListVo(ClaimsApply item) {
        RefundForClaimsApplyVO vo = new RefundForClaimsApplyVO();
        vo.setId(String.valueOf(item.getId()));
        vo.setApplyNo(item.getApplyNo());
        vo.setCustomerAccount(item.getCustomerAccount());
        vo.setContactPhone(item.getCustomerAccount());
        vo.setRechargeTime(item.getApproveTime());
        vo.setOperatorId(item.getApproveUserId());
        vo.setOperatorName(item.getApplyUserName());
        vo.setRechargeTypeText(item.getApplyType().getDesc());
        vo.setRechargeBalance(item.getRechargeBalance());
        vo.setChaimApllyType(item.getApplyType().getId());
        return vo;
    }

    /**
     * 构建动态查
     *
     * @param request
     * @return
     */
    private Specification<ClaimsApply> findByRequest(final RefundForClaimsApplyPageRequest request) {
        return (Root<ClaimsApply> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> buildWhere(request, root,
                query, cb);
    }

    /**
     * 构建列表查询的where条件
     *
     * @param request request
     * @param root            root
     * @param query           query
     * @param cb              bc
     * @return predicates
     */
    private Predicate buildWhere(RefundForClaimsApplyPageRequest request, Root<ClaimsApply> root, CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(request.getCustomerAccount()) && !StringUtils.isEmpty(request.getCustomerAccount().trim())) {
            predicates.add(cb.like(root.get("customerAccount"), buildLike(request.getCustomerAccount())));
        }

        if (Objects.nonNull(request.getOperatorName()) && !StringUtils.isEmpty(request.getOperatorName().trim())) {
            predicates.add(cb.like(root.get("approveUserName"), buildLike(request.getOperatorName())));
        }

        if (Objects.nonNull(request.getRechargeBalanceFrom())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("rechargeBalance"), request.getRechargeBalanceFrom()));
        }

        if (Objects.nonNull(request.getRechargeBalanceTo())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("rechargeBalance"), request.getRechargeBalanceTo()));
        }

        if (Objects.nonNull(request.getRechargeTimeFrom())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("approveTime"), DateUtil.parseDay(request.getRechargeTimeFrom())));
        }

        if (Objects.nonNull(request.getRechargeTimeTo())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("approveTime"), DateUtil.getDayEnd(DateUtil.parseDay(request.getRechargeTimeTo()))));
        }
        if (Objects.nonNull(request.getChaimApllyType())) {
            predicates.add(cb.equal(root.get("applyType"), request.getChaimApllyType()));
        }

        query.orderBy(cb.desc(root.get("approveTime")));
        return cb.and(predicates.toArray(new Predicate[]{}));
    }

    private static String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }


    public RefundForClaimsApplyVO getDetail(String applyNo) {
        if (StringUtils.isBlank(applyNo)) {
            return new RefundForClaimsApplyVO();
        }
        Optional<ClaimsApply> byIdOpt = claimsApplyRepository.findByApplyNo(applyNo);
        return byIdOpt.map(this::convertToDetailVo).orElseGet(RefundForClaimsApplyVO::new);
    }
}
