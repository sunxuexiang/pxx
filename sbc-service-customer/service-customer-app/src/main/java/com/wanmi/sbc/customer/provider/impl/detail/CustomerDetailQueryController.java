package com.wanmi.sbc.customer.provider.impl.detail;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.*;
import com.wanmi.sbc.customer.api.response.detail.*;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailBaseVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailBase;
import com.wanmi.sbc.customer.detail.service.CustomerDetailService;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import com.wanmi.sbc.customer.model.entity.CustomerDetailQueryRequest;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.quicklogin.service.ThirdLoginRelationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会员明细-会员明细查询API
 * @Author: daiyitian
 * @CreateDate: 2018/9/11 16:25
 * @Version: 1.0
 */
@RestController
@Validated
public class CustomerDetailQueryController implements CustomerDetailQueryProvider {

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private ThirdLoginRelationService thirdLoginRelationService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public BaseResponse<CustomerDetailGetWithNotDeleteByCustomerIdResponse> getCustomerDetailWithNotDeleteByCustomerId(@RequestBody @Valid
                                                                                                                               CustomerDetailWithNotDeleteByCustomerIdRequest
                                                                                                                               request) {
        CustomerDetailGetWithNotDeleteByCustomerIdResponse response = new CustomerDetailGetWithNotDeleteByCustomerIdResponse();
        wraperVo(customerDetailService.findByCustomerId(request.getCustomerId()), response);
        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<CustomerDetailGetCustomerIdResponse> getCustomerDetailByCustomerId(@RequestBody @Valid
                                                                                                   CustomerDetailByCustomerIdRequest
                                                                                                   request) {
        CustomerDetailGetCustomerIdResponse response = new CustomerDetailGetCustomerIdResponse();
        wraperVo(customerDetailService.findAnyByCustomerId(request.getCustomerId()), response);
        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<CustomerDetailGetByIdResponse> getCustomerDetailById(@RequestBody @Valid CustomerDetailByIdRequest
                                                                                     request) {
        CustomerDetailGetByIdResponse response = new CustomerDetailGetByIdResponse();
        wraperVo(customerDetailService.findOne(request.getCustomerDetailId()), response);
        return BaseResponse.success(response);
    }

    @Override

    public BaseResponse<CustomerDetailListByConditionResponse> listCustomerDetailByCondition(@RequestBody
                                                                                                     CustomerDetailListByConditionRequest request) {
        CustomerDetailListByConditionResponse response = new CustomerDetailListByConditionResponse();
        CustomerDetailQueryRequest queryRequest = new CustomerDetailQueryRequest();
        KsBeanUtil.copyPropertiesThird(request, queryRequest);
        response.setCustomerDetailVOList(wraperVos(customerDetailService.findDetailByCondition(queryRequest)));
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<CustomerStockOutListResponse> stockOutCustomerInfo(@Valid CustomerDetailListByCustomerIdsRequest request) {
        return BaseResponse.success( new CustomerStockOutListResponse(customerDetailService.queryStockOutCustomerInfo(request.getCustomerIds())));
    }

    private void wraperVo(CustomerDetail detail, CustomerDetailVO detailVO) {
        if (Objects.nonNull(detail)) {
            KsBeanUtil.copyPropertiesThird(detail, detailVO);
            detailVO.setBeaconStar(detail.getBeaconStar());
            Customer customer = detail.getCustomer();
            if (Objects.isNull(customer)) {
                return;
            }

            CustomerVO customerVO = new CustomerVO();
            KsBeanUtil.copyPropertiesThird(customer, customerVO);
            detailVO.setCustomerVO(customerVO);
            if (Objects.nonNull(detailVO.getManagerId())) {
                Optional<Employee> employeeOptional = employeeService.findEmployeeById(detailVO.getManagerId());
                EmployeeOptionalByIdResponse response = employeeOptional
                        .map(employee -> {
                            EmployeeOptionalByIdResponse employeeOptionalByIdResponse = new EmployeeOptionalByIdResponse();
                            KsBeanUtil.copyPropertiesThird(employee, employeeOptionalByIdResponse);
                            return employeeOptionalByIdResponse;
                        })
                        .orElseGet(EmployeeOptionalByIdResponse::new);

                detailVO.setManagerName(response.getEmployeeName());
            }
        }
    }

    private List<CustomerDetailVO> wraperVos(List<CustomerDetail> details) {
        if (CollectionUtils.isNotEmpty(details)) {
            return details.stream().map(detail -> {
                CustomerDetailVO vo = new CustomerDetailVO();
                KsBeanUtil.copyPropertiesThird(detail, vo);
                Customer customer = detail.getCustomer();
                if (Objects.nonNull(customer)) {
                    CustomerVO customerVO = new CustomerVO();
                    KsBeanUtil.copyPropertiesThird(customer, customerVO);
                    vo.setCustomerVO(customerVO);
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 批量查询会员信息
     * @param customerIds
     * @return
     */
    public List<CustomerDetail> findAnyByCustomerIds(List<String> customerIds) {
        return customerDetailService.findAnyByCustomerIds(customerIds);
    }

    @Override
    public  BaseResponse<CustomerDetailListByCustomerIdsResponse> listCustomerDetailBaseByCustomerIds(@RequestBody @Valid CustomerDetailListByCustomerIdsRequest request) {
        List<CustomerDetailBase> list =  customerDetailService.listCustomerDetailBaseByCustomerIds(request.getCustomerIds());
        if (CollectionUtils.isEmpty(list)){
            return BaseResponse.success(new CustomerDetailListByCustomerIdsResponse(Collections.EMPTY_LIST));
        }
        return BaseResponse.success(new CustomerDetailListByCustomerIdsResponse(KsBeanUtil.convertList(list, CustomerDetailBaseVO.class)));
    }

    @Override
    public BaseResponse<CustomerDetailListByConditionResponse> listCustomerDetailByCustomerIds(@RequestBody CustomerDetailListByCustomerIdsRequest request) {
        List<CustomerDetail> customerDetails = customerDetailService.listCustomerDetailByCustomerIds(request.getCustomerIds());
        List<CustomerDetailVO> convert = KsBeanUtil.convert(customerDetails, CustomerDetailVO.class);

        CustomerDetailListByConditionResponse response = new CustomerDetailListByConditionResponse();
        response.setCustomerDetailVOList(convert);
        return BaseResponse.success(response);
    }

}
