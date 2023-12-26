package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by hht on 2017/11/16.
 */
@ApiModel
@Data
public class CustomerDetailPageForSupplierRequest extends BaseQueryRequest {

    private static final long serialVersionUID = 656085367716653901L;
    /**
     * 客户ID
     */
    @ApiModelProperty(value = "客户ID")
    private String customerId;

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
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Long customerLevelId;

    /**
     * 账号状态 0：启用中  1：禁用中
     */
    @ApiModelProperty(value = "账号状态")
    private CustomerStatus customerStatus;

    /**
     * 客户账户
     */
    @ApiModelProperty(value = "客户账户")
    private String customerAccount;

    /**
     * 商铺Id
     */
    @ApiModelProperty(value = "商铺Id")
    private Long storeId;

    /**
     * 商家Id
     */
    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;

    /**
     * 客户类型
     */
    @ApiModelProperty(value = "客户类型")
    private CustomerType customerType;

    /**
     * 关键字搜索，目前范围：会员名称、客户账户
     */
    @ApiModelProperty(value = "关键字搜索，目前范围：会员名称、客户账户")
    private String keyword;

    /**
     * 所属业务员id
     */
    @ApiModelProperty(value = "所属业务员id")
    private String employeeId;

    /**
     * 所属业务员id集合
     */
    @ApiModelProperty(value = "所属业务员id集合")
    private List<String> employeeIds;


    /**
     * 是否是我的客户（S2b-Supplier使用）
     */
    @ApiModelProperty(value = "是否是我的客户（S2b-Supplier使用")
    private Boolean isMyCustomer;

    /**
     * 是否标星 0否，1是
     */
    @ApiModelProperty(value = "是否标星 0否，1是")
    private DefaultFlag beaconStar;
    
    /**
     * 会员名称集合
     */
    @ApiModelProperty(value = "会员名称集合")
    private List<String>  customerNameList;
    
    /**
     * 客户账户集合
     */
    @ApiModelProperty(value = "客户账户集合")
    private List<String> customerAccountList;
//    /**
//     * 封装公共条件
//     *
//     * @return
//     */
//    public Specification<Customer> getWhereCriteria() {
//        return (root, cquery, cbuild) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            //联查
//            Join<Customer, CustomerDetail> customerDetailJoin = root.join("customerDetail");
//
//            predicates.add(cbuild.equal(root.get("checkState"), CheckState.CHECKED));
//
//            if (StringUtils.isNotEmpty(customerAccount)) {
//                predicates.add(cbuild.like(root.get("customerAccount"), new StringBuffer().append("%").append
//                        (XssUtils.replaceLikeWildcard(customerAccount.trim())).append("%").toString()));
//            }
//            if (StringUtils.isNotEmpty(customerId)) {
//                predicates.add(cbuild.equal(root.get("customerId"), customerId.trim()));
//            }
//            if (StringUtils.isNotEmpty(customerName)) {
//                predicates.add(cbuild.like(customerDetailJoin.get("customerName"), new StringBuffer().append("%")
//                        .append(XssUtils.replaceLikeWildcard(customerName.trim())).append("%").toString()));
//            }
//            if (StringUtils.isNotEmpty(employeeId)) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("employeeId"), employeeId));
//            }
//            if (provinceId != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("provinceId"), provinceId));
//            }
//            if (cityId != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("cityId"), cityId));
//            }
//            if (areaId != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("areaId"), areaId));
//            }
//            if (customerStatus != null) {
//                predicates.add(cbuild.equal(customerDetailJoin.get("customerStatus"), customerStatus));
//            }
//            //删除标记
//            predicates.add(cbuild.equal(root.get("delFlag"), DeleteFlag.NO));
//            predicates.add(cbuild.equal(customerDetailJoin.get("delFlag"), DeleteFlag.NO));
//
//            //关键字搜索
//            if (StringUtils.isNotBlank(keyword)) {
//                String str = StringUtil.SQL_LIKE_CHAR.concat(XssUtils.replaceLikeWildcard(keyword.trim())).concat
//                        (StringUtil.SQL_LIKE_CHAR);
//                predicates.add(cbuild.or(cbuild.like(customerDetailJoin.get("customerName"), str), cbuild.like(root
//                        .get("customerAccount"), str)));
//            }
//
//            //联查
//            Join<Customer, StoreCustomerRela> storeCustomerJoin = root.join("storeCustomerRelaListByAll", JoinType
//                    .LEFT);
//            if (Objects.nonNull(storeId)) {
//                predicates.add(cbuild.equal(storeCustomerJoin.get("storeId"), storeId));
//            }
//            if (customerLevelId != null) {
//                predicates.add(cbuild.equal(storeCustomerJoin.get("customerLevelId"), customerLevelId));
//            }
//            if (Objects.nonNull(companyInfoId)) {
//                predicates.add(cbuild.equal(storeCustomerJoin.get("companyInfoId"), companyInfoId));
//            }
//            if (Objects.nonNull(customerType)) {
//                predicates.add(cbuild.equal(storeCustomerJoin.get("customerType"), customerType));
//            }
//
//            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
//
//            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
//        };
//    }
}
