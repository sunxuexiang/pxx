package com.wanmi.sbc.customer.provider.impl.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.response.customer.*;
import com.wanmi.sbc.customer.bean.vo.CustomerAddRelaVO;
import com.wanmi.sbc.customer.bean.vo.CustomerMergeVO;
import com.wanmi.sbc.customer.service.CustomerService;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class CustomerController implements CustomerProvider {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 审核客户状态
     * <p>
     * {@link CustomerService#updateCheckState}
     */
    @Override
    public BaseResponse<CustomerCheckStateModifyResponse> modifyCustomerCheckState(@RequestBody @Valid
                                                                                           CustomerCheckStateModifyRequest request) {
        int count = customerService.updateCheckState(request);

        return BaseResponse.success(new CustomerCheckStateModifyResponse(count));
    }

    /**
     * 审核企业会员
     */
    @Override
    public BaseResponse<CustomerCheckStateModifyResponse> checkEnterpriseCustomer(@RequestBody @Valid
                                                                                          CustomerEnterpriseCheckStateModifyRequest request) {
        int count = customerService.checkEnterpriseCustomer(request);

        return BaseResponse.success(new CustomerCheckStateModifyResponse(count));
    }

    /**
     * boss批量删除会员
     * 删除会员
     * 删除会员详情表
     * <p>
     * {@link CustomerService#delete}
     */
    @Override

    public BaseResponse<CustomersDeleteResponse> deleteCustomers(@RequestBody @Valid CustomersDeleteRequest request) {

        int count = customerService.delete(request.getCustomerIds());

        return BaseResponse.success(new CustomersDeleteResponse(count));
    }

    /**
     * 新增客户共通
     * <p>
     * {@link CustomerService#saveCustomerAll}
     */
    @Override

    public BaseResponse<CustomerAddResponse> saveCustomer(@RequestBody @Valid CustomerAddRequest request) {
        CustomerAddResponse response = customerService.saveCustomerAll(request);
        return BaseResponse.success(response);
    }


    /**
     * Boss端修改会员
     * 修改会员表，修改会员详细信息
     * <p>
     * {@link CustomerService#updateCustomerAll}
     */
    @Override

    public BaseResponse modifyCustomer(@RequestBody @Valid CustomerModifyRequest request) {
        customerService.updateCustomerAll(request);

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改绑定手机号
     * <p>
     * {@link CustomerService#updateCustomerAccount}
     */
    @Override

    public BaseResponse<CustomerAccountModifyResponse> modifyCustomerAccount(@RequestBody @Valid CustomerAccountModifyRequest request) {
        int count = customerService.updateCustomerAccount(request);

        return BaseResponse.success(new CustomerAccountModifyResponse(count));
    }

    /**
     * 修改已有的业务员
     * <p>
     * {@link CustomerService#updateCustomerSalesMan}
     */
    @Override

    public BaseResponse modifyCustomerSalesMan(@RequestBody @Valid CustomerSalesManModifyRequest request) {
        customerService.updateCustomerSalesMan(request.getEmployeeId(), request.getAccountType());

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 更新会员为销售员
     *
     * @param request {@link CustomerToDistributorModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modifyToDistributor(@RequestBody @Valid CustomerToDistributorModifyRequest request) {
        customerService.updateCustomerToDistributor(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 编辑会员标签
     *
     * @param request {@link CustomerModifyRequest}
     * @return
     */
    @Override
    public BaseResponse modifyCustomerTag(@RequestBody CustomerModifyRequest request) {
        customerService.modifyCustomerTag(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 编辑是否大客户
     * @param request
     * @return
     */
    @Override
    public BaseResponse modifyVipFlag(CustomerModifyRequest request) {
        customerService.modifyVipFlag(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CustomerGetByIdResponse> modifyEnterpriseInfo(@RequestBody @Valid CustomerEnterpriseRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(customerService.modifyCustomerEnterprise(request),CustomerGetByIdResponse.class));
    }

    @Override
    public BaseResponse verifyEnterpriseCustomer(@RequestBody @Valid CustomerEnterpriseCheckStateModifyRequest request) {
        customerService.verifyEnterpriseCustomer(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse releaseBindCustomers(@RequestBody @Valid CustomerReleaseByIdRequest request) {
        customerService.releaseBindCustomers(request.getCustomerId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CustomerMergeRelaResponse> addCustomerRela(@RequestBody @Valid CustomerAddRelaRequest request) {
        List<CustomerMergeVO> list = new ArrayList<>();

        //校验父账户是否是企业用户
        customerService.verifyEnterprise(request.getCustomerId());

        //工单验证
        List<WorkOrder> workOrders = workOrderService.queryMergeStatus(DefaultFlag.NO);
        request.getCustomerAccountList().forEach(s -> {
            List<CustomerAddRelaVO> relaVO = customerService.saveCustomerRela(request.getCustomerId(), s,workOrders);
            relaVO.forEach(inner->{
                if (inner.getFlag()){
                    CustomerMergeVO customerMergeVO = new CustomerMergeVO();
                    customerMergeVO.setCustomerId(inner.getCustomerId());
                    customerMergeVO.setFailingPhone(inner.getPhone());
                    list.add(customerMergeVO);
                }
            });
        });
        return BaseResponse.success(CustomerMergeRelaResponse.builder().customerMergeVOS(list).build());
    }

    @Override
    public BaseResponse asyncErpFlag(@RequestBody @Valid CustomerSynFlagRequest request) {
        customerService.updateCustomerAsyncFlag(request.getCustomerAccount());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyCustomerChannel(CustomerModifyRequest request) {
        customerService.modifyCustomerChannel(request.getCustomerId(), request.getSourceChannel());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse customerRelationBatch(CustomerRelationBatchRequest batchRequest) {
        customerService.customerRelationBatch(batchRequest);
        return BaseResponse.SUCCESSFUL();
    }

}
