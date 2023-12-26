package com.wanmi.sbc.customer.contract.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUpdateRequest;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.response.fadada.UploadContractResponese;
import com.wanmi.sbc.customer.contract.model.root.Contract;
import com.wanmi.sbc.customer.contract.repository.ContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ContractService {

    @Autowired
    private ContractRepository repository;
    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    public BaseResponse<String> uploadContract(ContractUploadRequest request){
        try {
            // 查询是否有相同名称的合同
            if (repository.findByContractName(request.getContractName()) != null) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"请勿上传相同合同");
            }
            // 如果新添加的合同需要启动，先关闭其他合同
            if (request.getContractFlag() == 1) {
                List<Long> contractIds = repository.findByContractFlagAndIsPerson(1,request.getIsPerson()).stream().map(Contract::getContractId)
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(contractIds) || contractIds.size() != 0) {
                    // 修改其他合同标识
                    repository.updateContractFlagByIds(contractIds,0,request.getIsPerson());
                }
            }
            Contract contract = convertToContract(request);
            EmployeeOptionalByIdRequest employeeOptionalByIdRequest = new EmployeeOptionalByIdRequest();
            employeeOptionalByIdRequest.setEmployeeId(request.getCreatePerson());
            String employeeName = employeeQueryProvider.getOptionalById(employeeOptionalByIdRequest).getContext().getEmployeeName();
            contract.setCreatePerson(employeeName);
            contract.setCreateTime(LocalDateTime.now());
            repository.save(contract);
        } catch (SbcRuntimeException e) {
            throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
        }
        return BaseResponse.success("上传成功");
    }

    public String seachValidContract () {
        // 无用代码
        List<Contract> byContractFlag = null;//repository.findByContractFlag(1);
        if (CollectionUtils.isEmpty(byContractFlag)) {
            throw new SbcRuntimeException("等待运营人员上传合同");
        }
        return byContractFlag.get(0).getFadadaId();
    }

    public List<UploadContractResponese> seachIsPersonContract(ContractUpdateRequest contractUpdateRequest){
        List<Contract> all = repository.findByContractFlagAndIsPerson(1, contractUpdateRequest.getIsPerson());
        List<UploadContractResponese> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(item->{
                UploadContractResponese ur = new UploadContractResponese();
                BeanUtils.copyProperties(item,ur);
                list.add(ur);
            });
        }
        return list;
    }

    public BaseResponse<String> updateContractStatus(ContractUpdateRequest contractUpdateRequest) {
        // 如果新添加的合同需要启动，先关闭其他合同
        try {
            if (contractUpdateRequest.getContractFlag() == 1) {
                List<Long> contractIds = repository.findByContractFlagAndIsPerson(1,contractUpdateRequest.getIsPerson()).stream().map(Contract::getContractId)
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(contractIds) || contractIds.size() != 0) {
                    // 修改其他合同标识
                    repository.updateContractFlagByIds(contractIds,0,contractUpdateRequest.getIsPerson());
                }
            }
            repository.updateContractFlagByIds(Arrays.asList(contractUpdateRequest.getContractId()),contractUpdateRequest.getContractFlag(),contractUpdateRequest.getIsPerson());
            return BaseResponse.success("修改成功");
        } catch (Exception e) {
            throw new SbcRuntimeException("操作异常{}", ExceptionUtils.getMessage(e));
        }
    }

    public BaseResponse<List<UploadContractResponese>> viewContract() {
        List<Contract> all = repository.findAll();
        List<UploadContractResponese> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(item->{
                UploadContractResponese ur = new UploadContractResponese();
                BeanUtils.copyProperties(item,ur);
                list.add(ur);
            });
        }
        return BaseResponse.success(list) ;
    }

    public BaseResponse<List<UploadContractResponese>> findByIsPerson (ContractUploadRequest request) {
        List<Contract> all = repository.findByIsPerson(request.getIsPerson());
        List<UploadContractResponese> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(all)) {
            all.forEach(item->{
                UploadContractResponese ur = new UploadContractResponese();
                BeanUtils.copyProperties(item,ur);
                list.add(ur);
            });
        }
        return BaseResponse.success(list) ;
    }

    public BaseResponse<String> delContractStatus(ContractUpdateRequest contractUpdateRequest) {
        try {
            Contract contract = new Contract();
            contract.setContractId(contractUpdateRequest.getContractId());
            repository.delete(contract);
            return BaseResponse.SUCCESSFUL();
        }catch (Exception e) {
            throw new SbcRuntimeException("K-000001");
        }

    }

    public Contract convertToContract(ContractUploadRequest request) {
        Contract contract = new Contract();
        contract.setContractName(request.getContractName());
        contract.setContractFlag(request.getContractFlag());
        contract.setFadadaId(request.getFadadaId());
        contract.setContractUrl(request.getContractUrl());
        contract.setIsPerson(request.getIsPerson());
        // 将其他属性设置为相应的值
        return contract;
    }

}
