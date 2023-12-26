package com.wanmi.sbc.customer.api.provider.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.response.customer.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "${application.customer.name}", url="${feign.url.customer:#{null}}", contextId = "CustomerProvider")
public interface CustomerProvider {
    /**
     * 审核客户状态
     *
     * @param request {@link CustomerCheckStateModifyRequest}
     * @return {@link CustomerCheckStateModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-check-state")
    BaseResponse<CustomerCheckStateModifyResponse> modifyCustomerCheckState(@RequestBody @Valid
                                                                                    CustomerCheckStateModifyRequest request);
    /**
     * 审核企业会员
     *
     * @param request {@link CustomerEnterpriseCheckStateModifyRequest}
     * @return {@link CustomerEnterpriseCheckStateModifyRequest}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-enterprise-check-state")
    BaseResponse<CustomerCheckStateModifyResponse> checkEnterpriseCustomer(@RequestBody @Valid
                                                                                     CustomerEnterpriseCheckStateModifyRequest request);

    /**
     * boss批量删除会员
     * 删除会员
     * 删除会员详情表
     *
     * @param request {@link CustomersDeleteRequest}
     * @return {@link CustomersDeleteResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/delete-customers")
    BaseResponse<CustomersDeleteResponse> deleteCustomers(@RequestBody @Valid CustomersDeleteRequest request);

    /**
     * 新增客户共通
     *
     * @param request {@link CustomerAddRequest}
     * @return {@link CustomerAddResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/save-customer")
    BaseResponse<CustomerAddResponse> saveCustomer(@RequestBody @Valid CustomerAddRequest request);

    /**
     * Boss端修改会员
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer")
    BaseResponse modifyCustomer(@RequestBody @Valid CustomerModifyRequest request);

    /**
     * 修改绑定手机号
     *
     * @param request {@link CustomerAccountModifyRequest}
     * @return {@link CustomerAccountModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-account")
    BaseResponse<CustomerAccountModifyResponse> modifyCustomerAccount(@RequestBody @Valid CustomerAccountModifyRequest request);

    /**
     * 修改已有的业务员
     *
     * @param request {@link CustomerSalesManModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-sales-man")
    BaseResponse modifyCustomerSalesMan(@RequestBody @Valid CustomerSalesManModifyRequest request);


    /**
     * 审核客户状态
     *
     * @param request {@link CustomerCheckStateModifyRequest}
     * @return {@link CustomerCheckStateModifyResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-to-distributor")
    BaseResponse modifyToDistributor(@RequestBody @Valid CustomerToDistributorModifyRequest request);

    /**
     * Boss端修改会员标签
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-tag")
    BaseResponse modifyCustomerTag(@RequestBody CustomerModifyRequest request);

    /**
     * boss端编辑会员是否大客户
     * @param request
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-vip-flag")
    BaseResponse modifyVipFlag(@RequestBody CustomerModifyRequest request);

    /**
     * 修改会员的企业信息
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-enterprise")
    BaseResponse<CustomerGetByIdResponse> modifyEnterpriseInfo(@RequestBody @Valid CustomerEnterpriseRequest request);

    /**
     * 审核企业信息
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/verify-enterprise-customer")
    BaseResponse verifyEnterpriseCustomer(@RequestBody @Valid CustomerEnterpriseCheckStateModifyRequest request);

    /**
     * 解除会员绑定
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/release-customer")
    BaseResponse releaseBindCustomers(@RequestBody @Valid CustomerReleaseByIdRequest request);



    /**
     * 批量新增子账户
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/add-customer-rela")
    BaseResponse<CustomerMergeRelaResponse> addCustomerRela(@RequestBody @Valid CustomerAddRelaRequest request);

    /**
     * 更新会员的erp同步标志
     *
     * @param request {@link CustomerModifyRequest}
     * @return  {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/async-customer-erp-flag")
    BaseResponse asyncErpFlag(@RequestBody @Valid CustomerSynFlagRequest request);

    /**
     * 新增用户渠道
     *
     * @param request {@link CustomerModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-customer-channel")
    BaseResponse modifyCustomerChannel(@RequestBody @Valid CustomerModifyRequest request);

    /**
     * 批量修改用户关系
     * @param batchRequest
     * @return
     */
    @PostMapping("/customer/${application.customer.version}/customer/modify-relation-batch")
    BaseResponse customerRelationBatch(@RequestBody CustomerRelationBatchRequest batchRequest);
}
