package com.wanmi.sbc.customer.company.request;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.util.XssUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sunkun on 2017/11/6.
 */
@Data
public class CompanyRequest extends BaseQueryRequest {

    /**
     * 商家id列表
     */
    private List<Long> companyInfoIds;

    private List<Long> companyInfoIdsNotIn;

    /**
     * 模糊商家名称
     */
    private String supplierName;

    /**
     * 精确商家名称
     */
    private String equalSupplierName;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 商家账号
     */
    private String accountName;

    /**
     * 商家编号
     */
    private String companyCode;

    /**
     * 签约结束日期
     */
    private String contractEndDate;

    /**
     * 账户状态  -1:全部 0：启用   1：禁用
     */
    private Integer accountState;

    /**
     * 店铺状态 -1：全部,0:开启,1:关店,2:过期
     */
    private Integer storeState;

    /**
     * 审核状态 -1全部 ,0:待审核,1:已审核,2:审核未通过
     */
    private Integer auditState;

    /**
     * 商家删除状态
     */
    private DeleteFlag deleteFlag;

    /**
     * 申请入驻时间 开始时间
     */
    private String applyEnterTimeStart;

    /**
     * 申请入驻时间 结束时间
     */
    private String applyEnterTimeEnd;

    /**
     * 是否确认打款 (-1:全部,0:否,1:是)
     */
    private Integer remitAffirm;

    /**
     * 商家类型 0、供应商 1、商家
     */
    @ApiModelProperty(value = "商家类型 0、供应商 1、商家")
    private StoreType storeType;

    @ApiModelProperty(value = "公司类型 0、平台自营 1、第三方商家 2、统仓统配 3、零售超市 4、新散批")
    private Integer companyType;

    /**
     * 员工ID
     */
    private List<String> employeeIds;

    // 签约开始时间
    private String applyTimeStart;
    // 签约结束时间
    private String applyTimeEnd;

    /**
     * 封装公共条件
     *
     * @return
     */
    public Specification<CompanyInfo> getWhereCriteria() {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<CompanyInfo, Store> companyInfoStoreJoin = root.join("storeList", JoinType.LEFT);
            Join<CompanyInfo, Employee> companyInfoEmployeeJoin = root.join("employeeList", JoinType.LEFT);
            if (CollectionUtils.isNotEmpty(companyInfoIds)) {
                CriteriaBuilder.In in = cbuild.in(root.get("companyInfoId"));
                companyInfoIds.forEach(id -> {
                    in.value(id);
                });
                predicates.add(in);
            }
            if (CollectionUtils.isNotEmpty(companyInfoIdsNotIn)) {
                CriteriaBuilder.In in = cbuild.in(root.get("companyInfoId"));
                companyInfoIdsNotIn.forEach(id -> {
                    in.value(id);
                });
                predicates.add(in.not());
            }

            if (StringUtils.isNotBlank(equalSupplierName)) {
                predicates.add(cbuild.equal(root.get("supplierName"), XssUtils.replaceLikeWildcard(equalSupplierName)));
            }
            if (StringUtils.isNotBlank(supplierName)) {
                predicates.add(cbuild.like(root.get("supplierName"), "%" + XssUtils.replaceLikeWildcard(supplierName)
                        + "%"));
            }
            if (StringUtils.isNotBlank(storeName)) {
                predicates.add(cbuild.like(companyInfoStoreJoin.get("storeName"), "%" + XssUtils.replaceLikeWildcard
                        (storeName) + "%"));
            }
            if (StringUtils.isNotBlank(accountName)) {

                predicates.add(cbuild.like(companyInfoEmployeeJoin.get("accountName"), "%" + XssUtils
                        .replaceLikeWildcard(accountName) + "%"));
            }
            if (StringUtils.isNotBlank(companyCode)) {
                predicates.add(cbuild.like(root.get("companyCodeNew"), "%" + XssUtils.replaceLikeWildcard(companyCode) +
                        "%"));
            }
            if (StringUtils.isNotBlank(applyEnterTimeStart)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                predicates.add(cbuild.greaterThan(companyInfoStoreJoin.get("applyEnterTime"), LocalDateTime.of
                        (LocalDate.parse(applyEnterTimeStart, formatter), LocalTime.MIN)));
            }
            if (StringUtils.isNotBlank(applyEnterTimeEnd)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                predicates.add(cbuild.lessThan(companyInfoStoreJoin.get("applyEnterTime"), LocalDateTime.of(LocalDate
                        .parse(applyEnterTimeEnd, formatter), LocalTime.MAX)));
            }

            if (StringUtils.isNotBlank(applyTimeStart)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                predicates.add(cbuild.greaterThan(companyInfoStoreJoin.get("applyTime"), LocalDateTime.of
                        (LocalDate.parse(applyTimeStart, formatter), LocalTime.MIN)));
            }
            if (StringUtils.isNotBlank(applyTimeEnd)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                predicates.add(cbuild.lessThan(companyInfoStoreJoin.get("applyTime"), LocalDateTime.of(LocalDate
                        .parse(applyTimeEnd, formatter), LocalTime.MAX)));
            }

            if (StringUtils.isNotBlank(contractEndDate)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                predicates.add(cbuild.lessThan(companyInfoStoreJoin.get("contractEndDate"), LocalDateTime.of
                        (LocalDate.parse(contractEndDate, formatter), LocalTime.MIN)));
            }
            if (Objects.nonNull(remitAffirm) && remitAffirm >= 0) {
                predicates.add(cbuild.equal(root.get("remitAffirm"), remitAffirm));
            }
            if (Objects.nonNull(accountState) && accountState >= 0) {
                predicates.add(cbuild.equal(companyInfoEmployeeJoin.get("accountState"), accountState));
            }
            if (Objects.nonNull(storeState) && storeState >= 0) {
                if (storeState == 2) {
                    predicates.add(cbuild.lessThan(companyInfoStoreJoin.get("contractEndDate"), LocalDateTime.now()));
                } else if (storeState == 0) {
                    predicates.add(cbuild.greaterThan(companyInfoStoreJoin.get("contractEndDate"), LocalDateTime.now
                            ()));
                    predicates.add(cbuild.equal(companyInfoStoreJoin.get("storeState"), storeState));
                } else {
                    predicates.add(cbuild.equal(companyInfoStoreJoin.get("storeState"), storeState));
                }
            }
            if (Objects.nonNull(auditState) && auditState >= 0) {
                predicates.add(cbuild.equal(companyInfoStoreJoin.get("auditState"), auditState));
            }
            if (Objects.nonNull(deleteFlag)) {
                predicates.add(cbuild.equal(root.get("delFlag"), deleteFlag.toValue()));
            }
            if (Objects.nonNull(storeType)) {
                predicates.add(cbuild.equal(companyInfoStoreJoin.get("storeType"), storeType));
            }

            if (Objects.nonNull(companyType)) {
                predicates.add(cbuild.equal(root.get("companyType"), companyType));
            }
            if (CollectionUtils.isNotEmpty(employeeIds)) {
                CriteriaBuilder.In in = cbuild.in(companyInfoEmployeeJoin.get("employeeId"));
                employeeIds.forEach(eid -> {
                    in.value(eid);
                });
                predicates.add(in);
            }
            predicates.add(cbuild.greaterThan(root.get("companyInfoId"), 0));

            List<Order> orderList = new LinkedList<>();

            // 全部 || 待审核
            if (Objects.nonNull(auditState) && (Objects.equals(auditState, -1) || Objects.equals(auditState, 0))) {
                Order orderApplyEnterTime = cbuild.desc(companyInfoStoreJoin.get("applyTime"));
                orderList.add(orderApplyEnterTime);
            }

            // 已审核 || 审核未通过
            if (Objects.nonNull(auditState) && (Objects.equals(auditState, 1) || Objects.equals(auditState, 2))) {
                Order orderApplyEnterTime = cbuild.desc(companyInfoStoreJoin.get("applyEnterTime"));
                orderList.add(orderApplyEnterTime);
            }

            Order orderCreateTime = cbuild.desc(root.get("createTime"));
            orderList.add(orderCreateTime);

            cquery.orderBy(orderList);
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

}
