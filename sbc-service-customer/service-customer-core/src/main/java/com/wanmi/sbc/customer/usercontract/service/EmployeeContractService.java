package com.wanmi.sbc.customer.usercontract.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.contract.ContractUploadRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.CustomerBlackRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractFindRequest;
import com.wanmi.sbc.customer.api.request.employeecontract.EmployeeContractSaveRequest;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.contract.model.root.Contract;
import com.wanmi.sbc.customer.contract.model.root.CustomerBlack;
import com.wanmi.sbc.customer.contract.repository.CustomerBlackRepository;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.usercontract.model.root.EmployeeContract;
import com.wanmi.sbc.customer.usercontract.repository.EmployeeContractRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeContractService {

    @Autowired
    private EmployeeContractRepository employeeContractRepository;
    @Autowired
    private CustomerBlackRepository customerBlackRepository;


    /**
     * 保存商家和法大大平台有ID
     *  @param employeeContract 员工合同实体
     */
    @Transactional
    @LcnTransaction
    public void saveEmployeeContract(EmployeeContract employeeContract) {
        employeeContractRepository.save(employeeContract);
    }


    public EmployeeContract findByEmployeeContract(String employeeId) {
        return employeeContractRepository.findByEmployeeId(employeeId);
    }

    public EmployeeContract findByEmployeeContractAppCustomerId (String customerId) {
        return employeeContractRepository.findByAppCustomerId(customerId);
    }

    public EmployeeContract findByEmployeeContractName(String employeeName) {
        return employeeContractRepository.findByEmployeeName(employeeName);
    }

    public List<EmployeeContract> findByInverstmentIdAndSupplierName(String investmentId,String supplierName ) {
        return employeeContractRepository.findByInverstmentIdAndSupplierName(investmentId,supplierName);
    }
    /**
     * 更新商家合同实体中的合同URL
     * @param employeeContract 员工合同实体
     */
    public void updateEmployeeContract(EmployeeContract employeeContract) {
        try {
            EmployeeContract byEmployeeId = employeeContractRepository.findByEmployeeId(employeeContract.getEmployeeId());
            if (byEmployeeId != null ) {
                byEmployeeId.setContractUrl(employeeContract.getContractUrl());
                byEmployeeId.setStatus(employeeContract.getStatus());
                log.info(byEmployeeId.toString());
                employeeContractRepository.save(byEmployeeId);
            } else {
                throw new SbcRuntimeException("未找到对应合同记录");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("更新失败");
        }
    }

    public void updateEmployeeContractByEmlpoyeeName(EmployeeContract employeeContract) {
        try {
            EmployeeContract byEmployeeId = null;
            if (StringUtils.isNotEmpty(employeeContract.getAppCustomerId())) {
                byEmployeeId = employeeContractRepository.findByAppCustomerId(employeeContract.getAppCustomerId());
            } else {
                byEmployeeId = employeeContractRepository.findByEmployeeName(employeeContract.getEmployeeName());
            }
            if (byEmployeeId != null ) {
                byEmployeeId.setContractUrl(employeeContract.getContractUrl());
                byEmployeeId.setStatus(employeeContract.getStatus());
                byEmployeeId.setEmployeeId(employeeContract.getEmployeeId());
                log.info(byEmployeeId.toString());
                employeeContractRepository.save(byEmployeeId);
            } else {
                throw new SbcRuntimeException("未找到对应合同记录");
            }
        } catch (Exception e) {
            throw new SbcRuntimeException("更新失败");
        }
    }
    /**
     * 根据商家合同ID查询员工合同实体列表
     * @param userContractId 用户合同ID
     * @return 符合条件的员工合同实体列表
     */
    public EmployeeContract findByUserContractId(String userContractId) {
        return employeeContractRepository.findByUserContractId(userContractId);
    }

    public Optional<Page<EmployeeContract>> findByUserContract(EmployeeContractFindRequest request) {
        Page<EmployeeContract> allByOptionalParameters = employeeContractRepository.findAll(findRequest(request),request.getPageable());
        return Optional.ofNullable(allByOptionalParameters);
    }

    public List<EmployeeContract> findAllByInvestmentManagerLikeList(String investmentManager) {
        return employeeContractRepository.findAllByInvestmentManagerLike(investmentManager);
    }

    public List<EmployeeContract> findAllByEmployeeIdList(List<String> empIdList) {
        return employeeContractRepository.findAllByEmployeeIdIn(empIdList);
    }

    private static Specification<EmployeeContract> findRequest(final EmployeeContractFindRequest request){
        return (root,query,criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (StringUtils.isNotEmpty(request.getSupplierName()) ) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.like(root.get("supplierName"),"%" + request.getSupplierName() + "%"));
            }
            if (StringUtils.isNotEmpty(request.getInvestmentManager())) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.like(root.get("investmentManager"),"%" + request.getInvestmentManager() + "%"));
            }
            if (StringUtils.isNotEmpty(request.getAccountName())) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.like(root.get("employeeName"),"%"+request.getAccountName()+"%"));
            }
            if (request.getSignType()!= null ) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("signType"),request.getSignType()));
            }
            if (StringUtils.isNotEmpty(request.getContractNo())) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("userContractId"),request.getContractNo()));
            }
            if (!CollectionUtils.isEmpty(request.getAccountNames())) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.and(root.get("employeeName").in(request.getAccountNames())));
            }
            if (request.getStatus() == 0) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.or(root.get("status").in("0","9999")));
            } else if (request.getStatus() == 1){
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("status"),"3000"));
            }
            if (request.getBeginTime() != null && request.getEndTime() == null) {
                LocalDateTime startOfDay = request.getBeginTime().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            if (request.getBeginTime() != null && request.getEndTime() != null) {
                LocalDateTime startOfDay = request.getBeginTime().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = request.getEndTime().toLocalDate().atStartOfDay();
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            if (request.getEndTime() == null && request.getEndTime() != null) {
                LocalDateTime endOfDay = request.getEndTime().toLocalDate().atStartOfDay();
                LocalDateTime startOfDay = endOfDay.plusDays(1).minusNanos(1);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("createTime"), startOfDay, endOfDay));
            }
            query.orderBy(criteriaBuilder.desc(root.get("createTime")));
            return predicate;
        };
    }

    public CustomerBlack findByStoreName(String storeName) {

        return customerBlackRepository.findByStoreName(storeName);
    }

    public CustomerBlack saveCustomerBlack (CustomerBlackRequest customerBlackRequest) {
        CustomerBlack customerBlack = KsBeanUtil.convert(customerBlackRequest,CustomerBlack.class);
        return customerBlackRepository.save(customerBlack);
    }

    public EmployeeContract findByAppId (String appId) {
        return employeeContractRepository.findByAppId(appId);
    }

    public BaseResponse delEmployeeContract(EmployeeContractSaveRequest request) {
        employeeContractRepository.delEmployeeContract(request.getEmployeeId());
        return BaseResponse.SUCCESSFUL();
    }

}
