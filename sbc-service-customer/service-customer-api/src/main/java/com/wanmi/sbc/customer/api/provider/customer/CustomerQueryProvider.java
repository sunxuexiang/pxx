package com.wanmi.sbc.customer.api.provider.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerByGrowthValueRequest;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.bean.vo.CustomerPageVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "CustomerQueryProvider")
public interface CustomerQueryProvider {
    /**
     * 条件查询会员带分页
     *
     * @param request {@link CustomerDetailPageRequest}
     * @return {@link CustomerDetailPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/page")
    BaseResponse<CustomerDetailPageResponse> page(@RequestBody @Valid CustomerDetailPageRequest request);

    /**
     * 企业购条件查询会员带分页
     *
     * @param request {@link IEPCustomerPageRequest}
     * @return {@link CustomerDetailPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/page-for-enterprise")
    BaseResponse<CustomerDetailPageResponse> pageForEnterpriseCustomer(@RequestBody @Valid EnterpriseCustomerPageRequest request);

    /**
     * 条件查询会员带分页(S2b)
     *
     * @param request {@link CustomerDetailPageForSupplierRequest}
     * @return {@link CustomerDetailPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/page-for-s2b-supplier")
    BaseResponse<CustomerDetailPageForSupplierResponse> pageForS2bSupplier(@RequestBody @Valid
                                                                                   CustomerDetailPageForSupplierRequest request);

    /**
     * 多条件查询会员详细信息
     *
     * @param request {@link CustomerDetailListByConditionRequest}
     * @return {@link CustomerDetailListByConditionResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-detail-by-condition")
    BaseResponse<CustomerDetailListByConditionResponse> listCustomerDetailByCondition(@RequestBody @Valid
                                                                                               CustomerDetailListByConditionRequest request);

    /**
     * 多条件查询会员详细信息
     *
     * @param request {@link CustomerDetailListForOrderRequest}
     * @return {@link CustomerDetailListForOrderResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-detail-for-order")
    BaseResponse<CustomerDetailListForOrderResponse> listCustomerDetailForOrder(@RequestBody @Valid CustomerDetailListForOrderRequest request);

    /**
     * 多条件查询会员详细信息
     *
     * @param request {@link CustomerDetailListByPageRequest}
     * @return {@link CustomerDetailListByPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-detail-by-page")
    BaseResponse<CustomerDetailListByPageResponse> listCustomerDetailByPage(@RequestBody @Valid CustomerDetailListByPageRequest request);

    /**
     * 根据会员的名称和账号模糊查询会员详细信息
     *
     * @param request {@link CustomerDetailListByPageRequest}
     * @return {@link CustomerDetailListByPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-detail-for-distribution-record")
    BaseResponse<CustomerDetailListByPageResponse> listCustomerDetailForDistributionRecord(@RequestBody @Valid
                                                                                                   CustomerDetailListByPageFordrRequest request);


    /**
     * 根据业务员id获取客户列表
     *
     * @param request {@link CustomerIdListRequest}
     * @return {@link CustomerIdListResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-id-by-employeeId")
    BaseResponse<CustomerIdListResponse> listCustomerId(@RequestBody @Valid CustomerIdListRequest request);

    /**
     * 获取id客户列表
     *
     * @return {@link CustomerIdListResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-id")
    BaseResponse<CustomerIdListResponse> listCustomerId();

    /**
     * 模糊查询会员信息
     *
     * @param request {@link CustomerListByConditionRequest}
     * @return {@link CustomerListByConditionResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-by-condition")
    BaseResponse<CustomerListByConditionResponse> listCustomerByCondition(@RequestBody @Valid CustomerListByConditionRequest request);

    /**
     * 查询单条会员信息（任意状态）
     *
     * @param request {@link CustomerGetByIdRequest}
     * @return {@link CustomerGetByIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-by-id")
    BaseResponse<CustomerGetByIdResponse> getCustomerById(@RequestBody @Valid CustomerGetByIdRequest request);

    /**
     * 查询单条会员信息（任意状态）无第三方
     *
     * @param request {@link CustomerGetByIdRequest}
     * @return {@link CustomerGetByIdResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-by-idNoThird")
    BaseResponse<CustomerGetByIdResponse> getCustomerByIdNoThird(@RequestBody @Valid CustomerGetByIdRequest request);

    /**
     * @description  是否是VIP
     * @author  shiy
     * @date    2023/3/20 9:49
     * @params  [com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest]
     * @return  com.wanmi.sbc.common.base.BaseResponse<java.lang.Boolean>
    */
    @PostMapping("/customer/${application.customer.version}/customer/check-customer-is-vip")
    BaseResponse<Boolean> checkCustomerIsVip(@RequestBody @Valid CustomerGetByIdRequest request);

    /**
     * 删除标记
     *
     * @param request {@link CustomerDelFlagGetRequest}
     * @return {@link CustomerDelFlagGetResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-del-flag")
    BaseResponse<CustomerDelFlagGetResponse> getCustomerDelFlag(@RequestBody @Valid CustomerDelFlagGetRequest request);

    /**
     * 查询是否有客户获取了成长值
     *
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/has-obtained-growth-value")
    BaseResponse<Boolean> hasObtainedGrowthValue();

    /**
     * 根据账号获取会员信息
     *
     * @param request {@link NoDeleteCustomerGetByAccountRequest}
     * @return {@link NoDeleteCustomerGetByAccountResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-by-condition")
    BaseResponse<NoDeleteCustomerGetByAccountResponse> getNoDeleteCustomerByAccount(@RequestBody @Valid NoDeleteCustomerGetByAccountRequest request);

    /**
     * 检查账户是否禁用
     *
     * @param request {@link DisableCustomerDetailGetByAccountRequest}
     * @return {@link DisableCustomerDetailGetByAccountResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-detail-by-condition")
    BaseResponse<DisableCustomerDetailGetByAccountResponse> getDisableCustomerDetailByAccount(@RequestBody @Valid DisableCustomerDetailGetByAccountRequest request);

    /**
     *
     * @param request {@link DisableCustomerDetailGetByAccountRequest}
     * @return {@link DisableCustomerDetailGetByAccountResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-detail-by-account")
    BaseResponse<DisableCustomerDetailGetByAccountResponse> getCustomerDetailByAccount(@RequestBody @Valid DisableCustomerDetailGetByAccountRequest request);

//
//    /**
//     * S2b-supplier 新增客户
//     *
//     * @param request {@link }
//     * @return {@link }
//     */
//    @PostMapping("/customer/${application.customer.version}/save-customer-for-supplier")
//    BaseResponse<CustomerAddResponse> saveCustomerForSupplier(CustomerAddRequest request);
//
//
//    /**
//     * S2b-boss , b2b-boss 新增客户共通方法
//     *
//     * @param request {@link CustomerAddRequest}
//     * @return {@link CustomerAddResponse}
//     */
//    @PostMapping("/customer/${application.customer.version}/save-customer-for-boss")
//    BaseResponse<CustomerAddResponse> saveCustomerForBoss(CustomerAddRequest request);

    /**
     * 根据审核状态统计用户
     *
     * @param request {@link CustomerCountByStateRequest}
     * @return {@link CustomerCountByStateResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/count-customer-by-state")
    BaseResponse<CustomerCountByStateResponse> countCustomerByState(@RequestBody @Valid CustomerCountByStateRequest request);

    /**
     * 根据审核状态统计增票资质
     *
     * @param request {@link InvoiceCountByStateRequest}
     * @return {@link InvoiceCountByStateResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/count-invoice-by-state")
    BaseResponse<InvoiceCountByStateResponse> countInvoiceByState(@RequestBody @Valid InvoiceCountByStateRequest request);

    /**
     * 按照客户名称模糊匹配，当前商家未关联的平台客户
     *
     * @param request {@link CustomerNotRelatedListRequest}
     * @return {@link CustomerNotRelatedListResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-not-related")
    BaseResponse<CustomerNotRelatedListResponse> listCustomerNotRelated(@RequestBody @Valid CustomerNotRelatedListRequest request);

    /**
     * 获取客户详情信息，包括业务员名称，等级，等级扣率
     *
     * @param request {@link CustomerGetForSupplierRequest}
     * @return {@link CustomerGetForSupplierResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-customer-for-supplier")
    BaseResponse<CustomerGetForSupplierResponse> getCustomerForSupplier(@RequestBody @Valid CustomerGetForSupplierRequest request);

    /**
     * 根据成长值获取客户列表
     *
     * @param request {@link CustomerIdListRequest}
     * @return {@link CustomerIdListResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-id-by-growth-value")
    BaseResponse<CustomerIdListResponse> listCustomerIdByGrowthValue(@RequestBody @Valid CustomerByGrowthValueRequest request);

    /**
     * 根据会员ID、是否删除查询会员基础信息
     * @param request  {@link CustomerIdListRequest}
     * @return  {@link CustomerBaseByCustomerIdAndDeleteFlagResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-base-customer")
    BaseResponse<CustomerBaseByCustomerIdAndDeleteFlagResponse> getBaseCustomer(@RequestBody @Valid CustomerBaseByCustomerIdAndDeleteFlagRequest request);

    /**
     * 查询未删除、已审核的会员ID集合
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-id-by-pageable")
    BaseResponse<CustomerListCustomerIdByPageableResponse> listCustomerIdByPageable(@RequestBody @Valid CustomerListCustomerIdByPageableRequest request);

    /**
     * 查询未删除、已审核主账号的会员ID集合
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/list-customer-id-by-pageable-parent")
    BaseResponse<CustomerListCustomerIdByPageableResponse> findCustomerIdNoParentByPageable(@RequestBody @Valid CustomerListCustomerIdByPageableRequest request);

    /**
     * 根据ids查询会员列表（不涉及关联查询）
     *
     * @return 第三方登录方式列表 {@link CustomerIdsListRequest}
     */
    @PostMapping("/customer/${application.customer.version}/customer/get-base-customer-by-ids")
     BaseResponse<CustomerBaseListByIdsResponse> getCustomerListByIds(@RequestBody @Valid CustomerIdsListRequest
                                                                                  request);

    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-tag-by-customer-id")
    BaseResponse modifyCustomerTag(@RequestBody @Valid CustomerModifyTagRequest request);


    /**
     * 根具会员的Id查询已绑定的子账户
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/query-childs-by-customer-id")
    BaseResponse<CustomerListByConditionResponse> queryChildsByParentId(@RequestBody @Valid CustomerGetByIdRequest request);


    /**
     * 批量校验会员的绑定状态
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/validate-customer-bind-status")
    BaseResponse<List<String>> batchValidateCustomerBindStatus(@RequestBody @Valid CustomerValidateByIdsRequest request);

    /**
     * 批量校验会员的绑定状态
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/validate-customer-binded-statu")
    BaseResponse validateCustomerBindStatus(@RequestBody @Valid CustomerValidateByIdRequest request);

    /**
     * 校验社会信用代码的是否重复
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/verify-social-code")
    BaseResponse verifySocialCode(@RequestBody @Valid CustomerSocialCodeValidateRequest request);

    /**
     * 校验被绑定的子账户是否能被添加为子账户
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/verify-customer-rela")
    BaseResponse<CustomerVerifyRelaResponse> verifyCustomerRela(@RequestBody @Valid CustomerAddRelaRequest request);


    /**
     * 多条件查询会员详细信息
     *
     * @param request {@link CustomerDetailListByConditionRequest}
     * @return {@link CustomerDetailListByConditionResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/page-query-child-account")
    BaseResponse<CustomerDetailListPageResponse> pageQueryChildAccountByCondition(@RequestBody @Valid
                                                                                              CustomerDetailListByConditionRequest request);

    /**
     * 需要比较出新用户
     * 查询所有用户的ID
     */
    @PostMapping("/customer/${application.customer.version}/customer/all-customer-id")
    BaseResponse<CustomerIdAllResponse> getAllCustomerId();


    /**
     * 多条件查询会员详细信息
     *
     * @param request {@link CustomerDetailListForOrderRequest}
     * @return {@link CustomerDetailListForOrderResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/listCustomerDetailByIds")
    BaseResponse<CustomerDetailCopyByIdResponse> listCustomerDetailByIds(@RequestBody @Valid CustomerIdsListRequest  request);

    @PostMapping("/customer/${application.customer.version}/customer/listCustomerDetailCopy")
    BaseResponse<CustomerDetailCopyListResponse> listCustomerDetailCopy(@RequestBody CustomerDetailListForOrderRequest customerDetailRequest);

    /**
     * 邀新分页查询
     *
     * @param request {@link CustomerDetailPageRequest}
     * @return {@link CustomerDetailPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/invitationPage")
    BaseResponse<CustomerPageVO> invitationPage(@RequestBody @Valid CustomerInvitationPageRequest request);

    /**
     * 邀新统计分页查询
     *
     * @param request {@link CustomerDetailPageRequest}
     * @return {@link CustomerDetailPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/invitationCountPage")
    BaseResponse<CustomerPageVO> invitationCountPage(@RequestBody @Valid CustomerInvitationPageRequest request);

    /**
     * 根据LevelId查询用户ID
     * @param levelIds
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/CustomerIdsByLevelIds")
    BaseResponse<List<String>> getCustomerIdsByLevelIds(@RequestBody List<Long> levelIds);
}
