package com.wanmi.sbc.setting.onlineservice.service;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationGetRequest;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceStoreRelationRequest;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationChildResponse;
import com.wanmi.sbc.setting.api.response.imonlineservice.CustomerServiceStoreRelationResponse;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStore;
import com.wanmi.sbc.setting.onlineservice.model.root.CustomerServiceStoreRelation;
import com.wanmi.sbc.setting.onlineservice.repository.CustomerServiceStoreRelationRepository;
import com.wanmi.sbc.setting.onlineservice.repository.CustomerServiceStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceStoreRelationService {

    @Autowired
    private CustomerServiceStoreRelationRepository customerServiceStoreRelationRepository;

    @Autowired
    private CustomerServiceStoreRepository customerServiceStoreRepository;

    @Transactional
    public boolean addRelation(CustomerServiceStoreRelationRequest request) {
        customerServiceStoreRelationRepository.deleteByParentStoreId(request.getStoreId());

        List<CustomerServiceStore> dbList = customerServiceStoreRepository.findByStoreId(request.getStoreId());
        if (ObjectUtils.isEmpty(dbList)) {
            CustomerServiceStore customerServiceStore = KsBeanUtil.convert(request, CustomerServiceStore.class);
            customerServiceStoreRepository.save(customerServiceStore);
        }

        if (ObjectUtils.isEmpty(request.getChildList())) {
            return true;
        }

        List<CustomerServiceStoreRelation> relationList = new ArrayList<>();
        request.getChildList().forEach(store -> {
            CustomerServiceStoreRelation relation = new CustomerServiceStoreRelation();
            relation.setParentStoreId(request.getStoreId());
            relation.setParentCompanyInfoId(request.getCompanyInfoId());
            relation.setStoreId(store.getStoreId());
            relation.setCompanyInfoId(store.getCompanyInfoId());
            relationList.add(relation);
        });
        customerServiceStoreRelationRepository.saveAll(relationList);
        return true;
    }

    @Transactional
    public boolean updateRelation(CustomerServiceStoreRelationRequest request) {
        return addRelation(request);
    }

    @Transactional
    public boolean deleteRelation(CustomerServiceStoreRelationRequest request) {
        customerServiceStoreRepository.deleteByStoreId(request.getStoreId());
        customerServiceStoreRelationRepository.deleteByParentStoreId(request.getStoreId());
        return true;
    }

    public MicroServicePage<CustomerServiceStoreRelationResponse> getList(CustomerServiceStoreRelationGetRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPageNum(), request.getPageSize());
        Page<CustomerServiceStore> storePage = customerServiceStoreRepository.findAll(getWhereCriteria(request), pageRequest);
        if (ObjectUtils.isEmpty(storePage.getContent())) {
            return KsBeanUtil.convertPage(storePage, CustomerServiceStoreRelationResponse.class);
        }
        List<Long> storeIds = storePage.getContent().stream().map(CustomerServiceStore::getStoreId).collect(Collectors.toList());
        List<CustomerServiceStoreRelation> itemList = customerServiceStoreRelationRepository.findByStoreId(storeIds);
        MicroServicePage<CustomerServiceStoreRelationResponse> microServicePage = KsBeanUtil.convertPage(storePage, CustomerServiceStoreRelationResponse.class);
        for (CustomerServiceStoreRelation relation : itemList) {
            for (CustomerServiceStoreRelationResponse response : microServicePage.getContent()) {
                if (relation.getParentStoreId().equals(response.getStoreId())) {
                    response.getChildList().add(KsBeanUtil.convert(relation, CustomerServiceStoreRelationChildResponse.class));
                }
            }
        }
        return microServicePage;
    }

    public static Specification<CustomerServiceStore> getWhereCriteria(CustomerServiceStoreRelationGetRequest request) {
        return (root, cquery, cbuild) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!ObjectUtils.isEmpty(request.getStoreIds())) {
                CriteriaBuilder.In<Object> in = cbuild.in(root.get("storeId"));
                for (Long store : request.getStoreIds()) {
                    in.value(store);
                }
                predicates.add(in);
            }
            if (!ObjectUtils.isEmpty(request.getCompanyInfoIds())) {
                CriteriaBuilder.In<Object> in = cbuild.in(root.get("companyInfoId"));
                for (Long store : request.getCompanyInfoIds()) {
                    in.value(store);
                }
                predicates.add(in);
            }
            cquery.orderBy(cbuild.asc(root.get("relationId")));
            Predicate[] p = predicates.toArray(new Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : cbuild.and(p);
        };
    }

    public List<CustomerServiceStoreRelation> getByCompanyInfoId(Long companyInfoId) {
        return customerServiceStoreRelationRepository.findByCompanyInfoId(companyInfoId);
    }

    public List<Long> getChildStoreIds(Long storeId) {
        return customerServiceStoreRelationRepository.findChildStoreIdByParentStoreId(storeId);
    }

    public List<CustomerServiceStore> getStoreRelationByStoreId(Long storeId) {
        return customerServiceStoreRepository.findByStoreId(storeId);
    }

    public List<Long> getAllRelationStoreIds() {
        return customerServiceStoreRepository.getAllRelationStoreIds();
    }

    public List<CustomerServiceStoreRelationResponse> getAllRelationStores() {
        List<CustomerServiceStore> list = customerServiceStoreRepository.findAll();
        return KsBeanUtil.convert(list, CustomerServiceStoreRelationResponse.class);
    }

    public List<CustomerServiceStore> getParentStoreByCompanyId(Long companyInfoId) {
        return customerServiceStoreRepository.findByCompanyInfoId(companyInfoId);
    }
}
