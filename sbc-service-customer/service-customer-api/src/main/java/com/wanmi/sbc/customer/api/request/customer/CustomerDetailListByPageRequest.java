package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会员详情查询参数
 * Created by CHENLI on 2017/4/19.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetailListByPageRequest extends BaseQueryRequest {

    private static final long serialVersionUID = -4582081187218350168L;
    /**
     * 会员详细信息标识UUID
     */
    @ApiModelProperty(value = "会员详细信息标识UUID")
    private String customerDetailId;

    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private String customerAccount;

    /**
     * 客户IDs
     */
    @ApiModelProperty(value = "客户IDs")
    private List<String> customerIds;

    /**
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Long customerLevelId;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String customerName;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 账号状态 0：启用中  1：禁用中
     */
    @ApiModelProperty(value = "账号状态")
    private CustomerStatus customerStatus;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志", dataType = "com.wanmi.sbc.common.enums.DeleteFlag")
    private Integer delFlag;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @ApiModelProperty(value = "审核状态(0：待审核 1：已审核 2：审核未通过)", dataType = "com.wanmi.sbc.customer.bean.enums.CheckState")
    private Integer checkState;

    /**
     * 负责业务员
     */
    @ApiModelProperty(value = "负责业务员")
    private String employeeId;

    /**
     * 精确查询-账户
     */
    @ApiModelProperty(value = "精确查询-账户")
    private String equalCustomerAccount;

    /**
     * 精确查找-商家下的客户
     */
    @ApiModelProperty(value = "精确查找-商家下的客户")
    private Long companyInfoId;

    /**
     * 禁用原因
     */
    @ApiModelProperty(value = "禁用原因")
    private String forbidReason;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型")
    private CustomerType customerType;


//    /**
//     * 封装公共条件
//     *
//     * @return
//     */
//    public Specification<CustomerDetail> getWhereCriteria() {
//        return (root, cquery, cbuild) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            //联查
//            Join<Customer, CustomerDetail> customerDetailJoin = root.join("customer");
//
//            if (StringUtils.isNotBlank(equalCustomerAccount)) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerAccount"), equalCustomerAccount.trim()));
//            }
//
//            if (Objects.nonNull(customerAccount) && StringUtils.isNotEmpty(customerAccount.trim())) {
//                predicates.add(cbuild.like(customerDetailJoin.get("customerAccount"), buildLike(customerAccount)));
//            }
//            if (customerLevelId != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerLevelId"), customerLevelId));
//            }
//            if (checkState != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("checkState"), checkState));
//            }
//            if (customerType != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerType"), customerType));
//            }
//
//            if (CollectionUtils.isNotEmpty(customerIds)) {
//                predicates.add(root.get("customerId").in(customerIds));
////                predicates.add(root.get("customer").get("customerId").in(customerIds));
//            }
//            if (Objects.nonNull(customerName) && StringUtils.isNotEmpty(customerName.trim())) {
//                predicates.add(cbuild.like(root.get("customerName"), buildLike(customerName)));
//            }
//            if (Objects.nonNull(employeeId) && StringUtils.isNotEmpty(employeeId.trim())) {
//                predicates.add(cbuild.equal(root.get("employeeId"), employeeId));
//            }
//            if (Objects.nonNull(customerDetailId) && StringUtils.isNotEmpty(customerDetailId.trim())) {
//                predicates.add(cbuild.equal(root.get("customerDetailId"), customerDetailId));
//            }
//            if (Objects.nonNull(customerId) && StringUtils.isNotEmpty(customerId.trim())) {
//                predicates.add(cbuild.equal(root.get("customer").get("customerId"), customerId));
//            }
//            if (provinceId != null) {
//                predicates.add(cbuild.equal(root.get("provinceId"), provinceId));
//            }
//            if (regionId != null) {
//                predicates.add(cbuild.equal(root.get("regionId"), regionId));
//            }
//            if (areaId != null) {
//                predicates.add(cbuild.equal(root.get("areaId"), areaId));
//            }
//            if (customerStatus != null) {
//                predicates.add(cbuild.equal(root.get("customerStatus"), customerStatus));
//            }
//            //删除标记
//            predicates.add(cbuild.equal(customerDetailJoin.get("delFlag"), delFlag));
//            predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
//            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
//            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
//        };
//    }
//
//    /**
//     * 封装公共条件
//     *
//     * @return
//     */
//    public Specification<CustomerDetail> getAnyWhereCriteria() {
//        return (root, cquery, cbuild) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            //联查
//            Join<Customer, CustomerDetail> customerDetailJoin = root.join("customer");
//
//            if (StringUtils.isNotBlank(equalCustomerAccount)) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerAccount"), equalCustomerAccount.trim()));
//            }
//
//            if (Objects.nonNull(customerAccount) && StringUtils.isNotEmpty(customerAccount.trim())) {
//                predicates.add(cbuild.like(customerDetailJoin.get("customerAccount"), buildLike(customerAccount)));
//            }
//            if (customerLevelId != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerLevelId"), customerLevelId));
//            }
//            if (checkState != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("checkState"), checkState));
//            }
//
//            if (CollectionUtils.isNotEmpty(customerIds)) {
//                predicates.add(root.get("customerId").in(customerIds));
////                predicates.add(root.get("customer").get("customerId").in(customerIds));
//            }
//            if (Objects.nonNull(customerName) && StringUtils.isNotEmpty(customerName.trim())) {
//                predicates.add(cbuild.like(root.get("customerName"), buildLike(customerName)));
//            }
//            if (Objects.nonNull(employeeId) && StringUtils.isNotEmpty(employeeId.trim())) {
//                predicates.add(cbuild.equal(root.get("employeeId"), employeeId));
//            }
//            if (Objects.nonNull(customerDetailId) && StringUtils.isNotEmpty(customerDetailId.trim())) {
//                predicates.add(cbuild.equal(root.get("customerDetailId"), customerDetailId));
//            }
//            if (Objects.nonNull(customerId) && StringUtils.isNotEmpty(customerId.trim())) {
//                predicates.add(cbuild.equal(root.get("customer").get("customerId"), customerId));
//            }
//            if (provinceId != null) {
//                predicates.add(cbuild.equal(root.get("provinceId"), provinceId));
//            }
//            if (regionId != null) {
//                predicates.add(cbuild.equal(root.get("regionId"), regionId));
//            }
//            if (areaId != null) {
//                predicates.add(cbuild.equal(root.get("areaId"), areaId));
//            }
//            if (customerStatus != null) {
//                predicates.add(cbuild.equal(root.get("customerStatus"), customerStatus));
//            }
//            //删除标记
//            if (delFlag != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("delFlag"), delFlag));
//                predicates.add(cbuild.equal(root.get("delFlag"), delFlag));
//            }
//            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
//            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
//        };
//    }
//
//    private static String buildLike(String field) {
//        StringBuilder stringBuilder = new StringBuilder();
//        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
//    }
}
