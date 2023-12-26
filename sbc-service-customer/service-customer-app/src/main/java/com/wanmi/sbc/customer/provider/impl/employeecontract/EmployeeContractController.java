package com.wanmi.sbc.customer.provider.impl.employeecontract;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseQueryResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employeecontract.EmployeeContractProvider;
import com.wanmi.sbc.customer.api.request.employeecontract.CustomerBlackRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractFindRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.api.response.employeecontract.CustomerBlackResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractListResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractPageResponese;
import com.wanmi.sbc.customer.api.response.employeecontract.EmployeeContractResponese;
import com.wanmi.sbc.customer.api.response.enterpriseinfo.EnterpriseInfoListResponse;
import com.wanmi.sbc.customer.contract.model.root.CustomerBlack;
import com.wanmi.sbc.customer.contract.model.root.CustomerContract;
import com.wanmi.sbc.customer.contract.service.CustomerContractService;
import com.wanmi.sbc.customer.usercontract.model.root.EmployeeContract;
import com.wanmi.sbc.customer.usercontract.service.EmployeeContractService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Validated
public class EmployeeContractController implements EmployeeContractProvider {

    @Autowired
    private EmployeeContractService employeeContractService;
    @Autowired
    private CustomerContractService customerContractService;


    public BaseResponse<Void> saveEmployeeContract(@RequestBody EmployeeContractSaveRequest employeeContract) {
        try {
            EmployeeContract contract = new EmployeeContract();
            BeanUtils.copyProperties(employeeContract,contract);
            // 查询员工姓名公司名称
            contract.setCreateTime(LocalDateTime.now());
            EmployeeContract byEmployeeContract = employeeContractService.findByEmployeeContract(employeeContract.getEmployeeId());
            if (null == byEmployeeContract) {
                employeeContractService.saveEmployeeContract(contract);
            } else {
                employeeContractService.updateEmployeeContract(contract);
            }
        } catch (Exception e) {
            return BaseResponse.FAILED();
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    @Transactional
    @LcnTransaction
    public BaseResponse<Void> save(EmployeeContractSaveRequest employeeContract) {
        EmployeeContract contract = new EmployeeContract();
        BeanUtils.copyProperties(employeeContract,contract);
        employeeContractService.saveEmployeeContract(contract);
        return BaseResponse.SUCCESSFUL();
    }

    public BaseResponse<String> updateEmployeeContract(@RequestBody EmployeeContractSaveRequest employeeContract) {
        try
        {
            EmployeeContract contract = new EmployeeContract();
            BeanUtils.copyProperties(employeeContract,contract);
            employeeContractService.updateEmployeeContract(contract);
            return BaseResponse.success("更新成功");
        } catch (Exception e) {
            throw new SbcRuntimeException("更新失败");
        }
    }

    public BaseResponse<String> updateEmployeeContractByEmlpoyeeName(@RequestBody EmployeeContractSaveRequest employeeContract) {
        try
        {
            EmployeeContract contract = new EmployeeContract();
            BeanUtils.copyProperties(employeeContract,contract);
            employeeContractService.updateEmployeeContractByEmlpoyeeName(contract);
            return BaseResponse.success("更新成功");
        } catch (Exception e) {
            throw new SbcRuntimeException("更新失败");
        }
    }

    public BaseResponse<EmployeeContractResponese> findByUserContractId(String userContractId) {
        EmployeeContract employeeContracts = employeeContractService.findByUserContractId(userContractId);
        EmployeeContractResponese employeeContractResponese = new EmployeeContractResponese();
        BeanUtils.copyProperties(employeeContracts, employeeContractResponese);
        return BaseResponse.success(employeeContractResponese);
    }

    public List<EmployeeContractResponese> convertToEmployeeContractResponseList(List<EmployeeContract> employeeContracts) {
        List<EmployeeContractResponese> employeeContractResponses = new ArrayList<>();
        for (EmployeeContract employeeContract : employeeContracts) {
            EmployeeContractResponese employeeContractResponse = new EmployeeContractResponese();
            BeanUtils.copyProperties(employeeContract, employeeContractResponse);
            employeeContractResponses.add(employeeContractResponse);
        }
        return employeeContractResponses;
    }

    public BaseResponse<EmployeeContractPageResponese> findByUserContract(EmployeeContractFindRequest request) {
        // 通过批发市场查询
        List<CustomerContract> byTabRelationIds;
        if (!CollectionUtils.isEmpty(request.getTabRelationValue())) {
            byTabRelationIds = customerContractService.findByTabRelationIds(request.getTabRelationValue(),null);
        } else {
            byTabRelationIds = null;
        }
        if (CollectionUtils.isNotEmpty(byTabRelationIds)) {
            List<String> collect = byTabRelationIds.stream().map(CustomerContract::getContractPhone).collect(Collectors.toList());
            request.setAccountNames(collect);
        }
        Optional<Page<EmployeeContract>> byUserContract = employeeContractService.findByUserContract(request);
        EmployeeContractPageResponese employeeContractPageResponese = new EmployeeContractPageResponese();
        byUserContract.ifPresent(page ->{
            BaseQueryResponse<EmployeeContract> baseQueryResponse = new BaseQueryResponse<>(page);
            List<CustomerContract> tabRelationIds;
            if (CollectionUtils.isEmpty(byTabRelationIds)) {
                List<String> phones = baseQueryResponse.getData().stream().map(contract -> {
                    return contract.getEmployeeName();
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(phones)) {
                    tabRelationIds = customerContractService.findByTabRelationIds(null,phones);
                } else {
                    tabRelationIds = null;
                }
            } else {
                tabRelationIds = null;
            }
            List<EmployeeContractResponese> collect = baseQueryResponse.getData().stream().map(contract -> {
                EmployeeContractResponese contractResponese = KsBeanUtil.convert(contract, EmployeeContractResponese.class);
                if (CollectionUtils.isNotEmpty(byTabRelationIds)) {
                    CustomerContract customerContract = byTabRelationIds.stream().filter(i -> i.getContractPhone().equals(contract.getEmployeeName()))
                            .findFirst().orElse(null);
                    if (customerContract != null) {
                        contractResponese.setTabRelationName(customerContract.getTabRelationName());
                    }
                }
                if (CollectionUtils.isNotEmpty(tabRelationIds)) {
                    CustomerContract customerContract = tabRelationIds.stream().filter(i -> i.getContractPhone().equals(contract.getEmployeeName()))
                            .findFirst().orElse(null);
                    if (customerContract != null) {
                        contractResponese.setTabRelationName(customerContract.getTabRelationName());
                    }
                }
                return contractResponese;
            }).collect(Collectors.toList());
            employeeContractPageResponese.setPageVo(new MicroServicePage<>(collect,request.getPageable(),page.getTotalElements()));
        });

        return BaseResponse.success(employeeContractPageResponese);
    }

    public BaseResponse<EmployeeContractResponese> findByEmployeeId (String employeeId) {
        EmployeeContract byEmployeeContract = employeeContractService.findByEmployeeContract(employeeId);
        if (byEmployeeContract == null ) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(byEmployeeContract,EmployeeContractResponese.class));
    }

    public BaseResponse<EmployeeContractListResponese> findByEmployeeIdList (List<String> employeeIdList) {
        List<EmployeeContract> employeeContractList = employeeContractService.findAllByEmployeeIdList(employeeIdList);
        if (CollectionUtils.isEmpty(employeeContractList)) {
            return BaseResponse.success(new EmployeeContractListResponese(new ArrayList<>()));
        }
        List<EmployeeContractResponese> list=employeeContractList.stream().map(entity -> KsBeanUtil.convert(entity,EmployeeContractResponese.class)).collect(Collectors.toList());
        return BaseResponse.success(new EmployeeContractListResponese(list));
    }

    public BaseResponse<EmployeeContractResponese> findByEmployeeName (String employeeName) {
        EmployeeContract byEmployeeContract = employeeContractService.findByEmployeeContractName(employeeName);
        if (byEmployeeContract == null ) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(byEmployeeContract,EmployeeContractResponese.class));
    }

    public BaseResponse<EmployeeContractResponese> findByAppCustomerId (String employeeName) {
        EmployeeContract byEmployeeContract = employeeContractService.findByEmployeeContractAppCustomerId(employeeName);
        if (byEmployeeContract == null ) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(byEmployeeContract,EmployeeContractResponese.class));
    }

    public BaseResponse<EmployeeContractResponese> findByInverstmentIdAndSupplierName (EmployeeContractFindRequest employeeContractFindRequest) {
        List<EmployeeContract> byInverstmentIdAndSupplierName = employeeContractService.findByInverstmentIdAndSupplierName(employeeContractFindRequest.getInvestmentManagerId(), employeeContractFindRequest.getSupplierName());
        if (CollectionUtils.isEmpty(byInverstmentIdAndSupplierName) || byInverstmentIdAndSupplierName.size() == 0 ) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(byInverstmentIdAndSupplierName.get(0),EmployeeContractResponese.class));
    }

    @Override
    public BaseResponse<CustomerBlackResponese> findByStoreName(CustomerBlackRequest request) {
        CustomerBlack byStoreName = employeeContractService.findByStoreName(request.getStoreName());
        CustomerBlackResponese convert = null;
        if (byStoreName != null) {
            convert = KsBeanUtil.convert(byStoreName, CustomerBlackResponese.class);
        }
        return BaseResponse.success(convert);
    }

    @Override
    public BaseResponse<CustomerBlackResponese> saveCustomerBlack(CustomerBlackRequest customerBlackRequest) {
        CustomerBlackResponese customerBlackResponese = null;
        CustomerBlack customerBlack = employeeContractService.saveCustomerBlack(customerBlackRequest);
        if (customerBlack != null) {
            customerBlackResponese = KsBeanUtil.convert(customerBlack,CustomerBlackResponese.class);
        }
        return BaseResponse.success(customerBlackResponese);
    }

    public BaseResponse<EmployeeContractResponese> findByAppId (String appId) {
        EmployeeContract byEmployeeContract = employeeContractService.findByAppId(appId);
        if (byEmployeeContract == null ) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(byEmployeeContract,EmployeeContractResponese.class));
    }

    @Override
    @LcnTransaction
    public BaseResponse delEmployeeContract(EmployeeContractSaveRequest request) {

        return employeeContractService.delEmployeeContract(request);
    }
}
