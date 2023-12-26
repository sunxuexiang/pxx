package com.wanmi.sbc.customer.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.request.company.CompanyMallContactRelationBatchSaveRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.company.model.root.CompanyMallContractRelation;
import com.wanmi.sbc.customer.company.repository.CompanyMallContractRelationRepository;
import com.wanmi.sbc.customer.company.request.CompanyMallContractRelationRequest;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:21
 **/
@Service
public class CompanyMallContractRelationService {

    @Autowired
    private CompanyMallContractRelationRepository companyMallContractRelationRepository;

    @Autowired
    private StoreRepository storeRepository;

    /**
     *
     */
    public Page<CompanyMallContractRelation> page(CompanyMallContractRelationRequest request) {
        return companyMallContractRelationRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    public List<CompanyMallContractRelation> list(CompanyMallContractRelationRequest request){
        return companyMallContractRelationRepository.findAll(request.getWhereCriteria());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CompanyMallContractRelation> batchAdd(CompanyMallContactRelationBatchSaveRequest request) {
        // 查看店铺是否存在
        final Store store = storeRepository.findByStoreIdAndDelFlag(request.getStoreId(), DeleteFlag.NO);
        if (null == store || null == store.getCompanyInfo() || null == store.getCompanyInfo().getCompanyInfoId()){
            throw new RuntimeException("店铺不存在:" + request.getStoreId());
        }
        CompanyMallContractRelationRequest queryRequest = new CompanyMallContractRelationRequest();
        queryRequest.setRelationType(request.getRelationType());
        queryRequest.setStoreId(request.getStoreId());
        List<CompanyMallContractRelation> deleteAll = companyMallContractRelationRepository.findAll(queryRequest.getWhereCriteria());
        Map<String,Integer> originalSortMap;
        if (CollectionUtils.isNotEmpty(deleteAll)){
            originalSortMap = deleteAll.stream().filter(u -> u.getSort() != null)
                    .collect(Collectors.toMap(CompanyMallContractRelation::getRelationValue, CompanyMallContractRelation::getSort,(o,n) -> o));
            companyMallContractRelationRepository.deleteAll(deleteAll);
        }else {
            originalSortMap = new HashMap<>();
        }
        List<CompanyMallContractRelation> batch = new ArrayList<>();
        request.getContactRelationList().forEach(o -> {
            CompanyMallContractRelation target = new CompanyMallContractRelation();
            batch.add(target);
            target.setStoreId(request.getStoreId());
            target.setCompanyInfoId(store.getCompanyInfo().getCompanyInfoId());
            target.setRelationType(request.getRelationType());
            target.setRelationName(o.getRelationName());
            target.setRelationValue(o.getRelationValue());
            target.setCreateTime(LocalDateTime.now());
            target.setOperator("admin");
            target.setUpdateTime(LocalDateTime.now());
            Integer sort = wrapSort(request, originalSortMap, o);
            target.setDelFlag(DeleteFlag.NO);
            target.setSort(sort);
        });
        return companyMallContractRelationRepository.saveAll(batch);
    }

    private Integer wrapSort(CompanyMallContactRelationBatchSaveRequest request,
                             Map<String, Integer> originalSortMap,
                             CompanyMallContractRelationVO o) {
        Integer sort = originalSortMap.get(o.getRelationValue());
        if (null == sort) {
            Integer sortMax = companyMallContractRelationRepository.getMaxSortByTypeAndValue(request.getRelationType(), o.getRelationValue());
            if (null == sortMax) {
                sort = 100;
            } else {
                sort = sortMax + 1;
            }
        }
        return sort;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean batchSort(List<CompanyMallContractRelationVO> contactRelationList) {
        if (CollectionUtils.isEmpty(contactRelationList)){
            return false;
        }
        contactRelationList.forEach(o -> companyMallContractRelationRepository.updateSortById(o.getId(),o.getSort()));
        return true;
    }
}
