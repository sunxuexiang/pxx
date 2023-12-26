package com.wanmi.sbc.customer.company.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.request.company.*;
import com.wanmi.sbc.customer.api.response.company.CompanyAccountResponse;
import com.wanmi.sbc.customer.bean.dto.AutoAuditCompanyRecordDTO;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.company.model.root.AutoAuditCompanyRecord;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.repository.AutoAuditCompanyRecordRepository;
import com.wanmi.sbc.customer.company.repository.CompanyInfoRepository;
import com.wanmi.sbc.customer.company.request.CompanyRequest;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.service.StoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.company.model.root.CompanyInfoUpdate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 公司信息数据层
 * Created by CHENLI on 2017/5/12.
 */
@Service
public class CompanyInfoService {
    @Autowired
    private CompanyInfoRepository companyInfoRepository;

    @Autowired
    private StoreService storeService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AutoAuditCompanyRecordRepository autoAuditCompanyRecordRepository;

    /**
     * 查询公司信息
     *
     * @return
     */
    public CompanyInfo findCompanyInfo() {
        CompanyInfo companyInfo = new CompanyInfo();
        if (CollectionUtils.isEmpty(companyInfoRepository.findAll())) {
            return companyInfo;
        }
        companyInfo = companyInfoRepository.findAll().get(0);
        return companyInfo;
    }

    /**
     * 保存公司信息
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public CompanyInfo saveCompanyInfo(CompanyInfoSaveRequest saveRequest) {
        CompanyInfo companyInfo = new CompanyInfo();
        BeanUtils.copyProperties(saveRequest, companyInfo);
        companyInfo.setDelFlag(DeleteFlag.NO);
        companyInfo.setOperator("");
        companyInfo.setCreateTime(LocalDateTime.now());

        return companyInfoRepository.save(companyInfo);
    }

    /**
     * 修改公司信息
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public CompanyInfo updateCompanyInfo(CompanyInfoSaveRequest saveRequest) {
        CompanyInfo companyInfo = companyInfoRepository.findById(saveRequest.getCompanyInfoId()).orElse(null);
        if (Objects.isNull(companyInfo)){
            return null;
        }
        KsBeanUtil.copyProperties(saveRequest, companyInfo);
        companyInfo.setOperator("");
        companyInfo.setUpdateTime(LocalDateTime.now());

        return companyInfoRepository.save(companyInfo);
    }

    /**
     * 初始化公司信息
     *
     * @return
     */
    @Transactional
    public CompanyInfo getCompanyInfo() {
        List<CompanyInfo> companyInfos = companyInfoRepository.findAll();
        if (CollectionUtils.isEmpty(companyInfos)) {
            CompanyInfo companyInfo = new CompanyInfo();
            companyInfo.setCreateTime(LocalDateTime.now());
            companyInfo.setCompanyType(CompanyType.SUPPLIER);
            return companyInfoRepository.save(companyInfo);
        }
        return companyInfos.get(0);
    }


    /**
     * 修改公司信息
     *
     * @param request
     * @return
     */
    @Transactional
    public CompanyInfo updateCompanyInformation(CompanyInformationSaveRequest request) {
        List<CompanyInfo> companyInfos = companyInfoRepository.
                findBySocialCreditCodeAndCompanyInfoIdNotAndDelFlag(request.getSocialCreditCode(),
                        request.getCompanyInfoId(), DeleteFlag.NO);
        if (CollectionUtils.isNotEmpty(companyInfos)) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.SOCIAL_CREDIT_REPEAT);
        }
        CompanyInfo companyInfo = companyInfoRepository.findById(request.getCompanyInfoId()).
                        orElseThrow(()->new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST));
        companyInfo.setFoundDate(DateUtil.parseDayCanEmpty(request.getFoundDate()));
        companyInfo.setBusinessTermStart(DateUtil.parseDayCanEmpty(request.getBusinessTermStart()));
        companyInfo.setBusinessTermEnd(DateUtil.parseDayCanEmpty(request.getBusinessTermEnd()));
        KsBeanUtil.copyProperties(request, companyInfo);
        companyInfo.setOperator("");
        companyInfo.setUpdateTime(LocalDateTime.now());
        return companyInfoRepository.save(companyInfo);
    }

    /**
     * 查询指定公司信息
     *
     * @return
     */
    public CompanyInfo findOne(Long companyInfoId) {
        CompanyInfo info = companyInfoRepository.findById(companyInfoId).
                        orElseThrow(()->new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST));
        return info;
    }

    /**
     * 查询商家列表
     *
     * @param request
     * @return
     */
    public Page<CompanyInfo> findAll(CompanyRequest request) {
        return companyInfoRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 查询商家列表
     *
     * @param request
     * @return
     */
    public List<CompanyInfo> findAllList(CompanyRequest request) {
        return companyInfoRepository.findAll(request.getWhereCriteria());
    }


    /**
     * 修改公司信息
     *
     * @param request
     * @return
     */
    @LcnTransaction
    @Transactional
    public CompanyInfo updateCompanyType(CompanyTypeRequest request) {
        CompanyInfo companyInfo = companyInfoRepository.
                findByCompanyInfoIdAndDelFlag(request.getCompanyInfoId(), DeleteFlag.NO);
        if (Objects.isNull(companyInfo)) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        List<Store> stores = companyInfo.getStoreList();
        if(CollectionUtils.isNotEmpty(stores)){
            List<Long> storeIds = stores.stream().map(Store::getStoreId).collect(Collectors.toList());
            storeService.updateCompanyType(storeIds,request.getCompanyType());
        }
        KsBeanUtil.copyProperties(request, companyInfo);
        companyInfo.setCompanyType(request.getCompanyType());
        companyInfo.setOperator("");
        companyInfo.setUpdateTime(LocalDateTime.now());
        if (Objects.nonNull(request.getApplyEnterTime())) {
            companyInfo.setApplyEnterTime(request.getApplyEnterTime());
        }
        return companyInfoRepository.save(companyInfo);
    }

    /**
     * 商家收款账户列表
     *
     * @param request
     * @return
     */
    public Page<CompanyAccountResponse> accountList(CompanyRequest request) {
        Page<CompanyInfo> page = companyInfoRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        List<CompanyAccountResponse> list = page.getContent().stream().map(companyInfo -> {
            if (CollectionUtils.isEmpty(companyInfo.getStoreList())) {
                return null;
            }
            Store store = companyInfo.getStoreList().get(0);
            CompanyAccountResponse companyAccountResponse = new CompanyAccountResponse();
            companyAccountResponse.setCompanyInfoId(companyInfo.getCompanyInfoId());
            companyAccountResponse.setSupplierName(companyInfo.getSupplierName());
            companyAccountResponse.setStoreId(store.getStoreId());
            companyAccountResponse.setStoreName(store.getStoreName());
            companyAccountResponse.setApplyEnterTime(companyInfo.getApplyEnterTime());
            companyAccountResponse.setContractStartDate(store.getContractStartDate());
            companyAccountResponse.setContractEndDate(store.getContractEndDate());
            companyAccountResponse.setRemitAffirm(companyInfo.getRemitAffirm());
            return companyAccountResponse;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return new PageImpl<CompanyAccountResponse>(list, request.getPageable(), page.getTotalElements());
    }

    /**
     * 待审核统计
     *
     * @return
     */
    public Long countByTodo() {
        CompanyRequest request = new CompanyRequest();
        request.setAuditState(CheckState.WAIT_CHECK.toValue());
        request.setDeleteFlag(DeleteFlag.NO);
        return companyInfoRepository.count(request.getWhereCriteria());
    }

    /**
     * 修改全量公司信息
     *
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public CompanyInfo modifyAllCompanyInfo(CompanyInfoAllModifyRequest request) {
        CompanyInfo companyInfo = companyInfoRepository.findById(request.getCompanyInfoId()).orElse(null);
        if (Objects.isNull(companyInfo)){
            return null;
        }
        KsBeanUtil.copyProperties(request, companyInfo);
        companyInfo.setUpdateTime(LocalDateTime.now());
        return companyInfoRepository.save(companyInfo);
    }

    public List<CompanyInfo> queryByCompanyinfoIds(CompanyInfoQueryByIdsRequest request){
       return companyInfoRepository.queryByCompanyinfoIds( request.getCompanyInfoIds(),request.getDeleteFlag());
    }

/**
 * 功能描述: <br>
 * 〈〉
 * @Param: 查询存在重复的erpid
 * @Return: java.lang.Boolean
 * @Author: yxb
 * @Date: 2020/12/16 14:26
 */
    public Boolean hasSameErpId(String erpId,Long companyId){
        String erp = companyInfoRepository.countErpId(erpId);
        if (StringUtils.isNotBlank(erp)){
            if (Objects.nonNull(companyId)){
                if (erp.equals(companyId.toString())){
                    return false;
                }else {
                    return true;
                }
            }else {
                return true;
            }
        }else{
            return false;
        }
    }


    /**
     * 修改erpId
     *
     * @param request
     * @return
     */
    @LcnTransaction
    @Transactional
    public CompanyInfo updateCompanyErpId(CompanyErpIdRequest request) {
        CompanyInfo companyInfo = companyInfoRepository.
                findByCompanyInfoIdAndDelFlag(request.getCompanyInfoId(), DeleteFlag.NO);
        if (Objects.isNull(companyInfo)) {
            throw new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST);
        }
        List<Store> stores = companyInfo.getStoreList();
        if(CollectionUtils.isNotEmpty(stores)){
            List<Long> storeIds = stores.stream().map(Store::getStoreId).collect(Collectors.toList());
            storeService.updateCompanyErpId(storeIds,request.getErpId());
        }
        KsBeanUtil.copyProperties(request, companyInfo);
        companyInfo.setErpId(request.getErpId());
        companyInfo.setOperator("");
        companyInfo.setUpdateTime(LocalDateTime.now());
        return companyInfoRepository.save(companyInfo);
    }

    @Transactional
    public void updateIdCardNoIds(List<CompanyInfoVO> companyInfoVOS) {
        companyInfoVOS.stream().map(item->{
            CompanyInfoUpdate companyInfo = new CompanyInfoUpdate();
            KsBeanUtil.copyPropertiesThird(item, companyInfo);
            return companyInfo;
        }).collect(Collectors.toList()).forEach(item->{
            entityManager.merge(item);
        });
        entityManager.flush();
    }

    public void addAutoAuditCompanyRecord(List<AutoAuditCompanyRecordDTO> recordList) {
        List<AutoAuditCompanyRecord> list = KsBeanUtil.convert(recordList, AutoAuditCompanyRecord.class);
        autoAuditCompanyRecordRepository.saveAll(list);
    }

    public void updateDelFlag(CompanyInfoAllModifyRequest companyInfoAllModifyRequest) {
        companyInfoRepository.updateDelFlag(companyInfoAllModifyRequest.getCompanyInfoId());
    }
}
