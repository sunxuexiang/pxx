package com.wanmi.sbc.customer.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBatchSortEditRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierRecommendAddRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierRecommendBatchAddRequest;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
import com.wanmi.sbc.customer.company.repository.CompanyMallSupplierRecommendRepository;
import com.wanmi.sbc.customer.company.request.CompanyMallSupplierRecommendRequest;
import com.wanmi.sbc.customer.store.model.root.Store;
import com.wanmi.sbc.customer.store.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:21
 **/
@Service
@Slf4j
public class CompanyMallSupplierRecommendService {

    @Autowired
    private CompanyMallSupplierRecommendRepository companyMallSupplierRecommendRepository;

    @Autowired
    private StoreRepository storeRepository;


    /**
     * @Description:商家市场列表分页
     */
    public Page<CompanyMallSupplierRecommend> page(CompanyMallSupplierRecommendRequest request) {
        return companyMallSupplierRecommendRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyMallSupplierRecommend add(CompanyMallSupplierRecommendAddRequest request) {
        if (null == request.getCompanyInfoId()) {
            throw new SbcRuntimeException("参数异常");
        }
        // 插入店铺Id
        final Store store = storeRepository.findStoreByCompanyInfoId(request.getCompanyInfoId(), DeleteFlag.NO);
        if (null == store){
            throw new SbcRuntimeException("店铺不存在");
        }
        final CompanyMallSupplierRecommend source = companyMallSupplierRecommendRepository
                .findByCompanyInfoIdAndDelFlag(request.getCompanyInfoId(), DeleteFlag.NO);
        if (null != source) {
            // throw new SbcRuntimeException("当前店铺已经存在");
            log.info("推荐商家已存在，companyId:{},companyName:{}", source.getCompanyInfoId(), source.getCompanyInfoName());
            return source;
        }
        CompanyMallSupplierRecommend target = new CompanyMallSupplierRecommend();
        BeanUtils.copyProperties(request, target);
        if (target.getOpenStatus() == null){
            target.setOpenStatus(MallOpenStatus.OPEN.toValue());
        }
        target.setDelFlag(DeleteFlag.NO);
        target.setCreateTime(LocalDateTime.now());
        target.setUpdateTime(LocalDateTime.now());
        target.setStoreId(store.getStoreId());
        target.setCompanyInfoName(store.getStoreName());
        CompanyMallSupplierRecommend save = companyMallSupplierRecommendRepository.save(target);
        BigDecimal maxSort = getMaxSort();
        companyMallSupplierRecommendRepository.updateSortById(save.getId(), maxSort);
        save.setSort(maxSort);
        return save;
    }

    private BigDecimal getMaxSort() {
        BigDecimal maxSort = (maxSort = companyMallSupplierRecommendRepository.getMaxSort()) == null ? BigDecimal.ZERO : maxSort;
        maxSort = maxSort.add(BigDecimal.ONE);
        return maxSort;
    }

    public List<CompanyMallSupplierRecommend> findByStoreIdNotAndStoreIdInAndDelFlagAndAssignSort(Long storeId, List<Long> storeIds, Integer assignSort) {
        return companyMallSupplierRecommendRepository.findByStoreIdNotAndStoreIdInAndDelFlagAndAssignSort(storeId, storeIds, DeleteFlag.NO, assignSort);
    }


    public CompanyMallSupplierRecommend edit(CompanyMallSupplierRecommendAddRequest request) {
        CompanyMallSupplierRecommend target = companyMallSupplierRecommendRepository.findById(request.getId()).orElse(null);
        if (null == target) {
            throw new SbcRuntimeException("参数异常");
        }
        wrapUpdateJustNeed(request, target);
        target.setUpdateTime(LocalDateTime.now());
        return companyMallSupplierRecommendRepository.save(target);
    }

    public CompanyMallSupplierRecommend getById(Long id) {
        CompanyMallSupplierRecommend target = new CompanyMallSupplierRecommend();
        CompanyMallSupplierRecommend source = companyMallSupplierRecommendRepository.findById(id).orElse(null);
        KsBeanUtil.copyPropertiesThird(source, target);
        return target;
    }

    private static void wrapUpdateJustNeed(CompanyMallSupplierRecommendAddRequest request, CompanyMallSupplierRecommend target) {
        Long l;
        Integer i;

        if (null != (l = request.getCompanyInfoId())){
            target.setCompanyInfoId(l);
        }
        if (null != (l = request.getId())){
            target.setId(l);
        }
        if (null != (i = request.getOpenStatus())){
            target.setOpenStatus(i);
        }
        if (Objects.nonNull(request.getDelFlag())){
            target.setDelFlag(request.getDelFlag());
        }
        target.setAssignSort(request.getAssignSort());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSort(List<CompanyMallBatchSortEditRequest.Sort> sorts) {
        sorts.forEach(o -> companyMallSupplierRecommendRepository.updateSortById(o.getSortId(), o.getSort()));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<CompanyMallSupplierRecommend> batchAdd(CompanyMallSupplierRecommendBatchAddRequest request) {
        List<CompanyMallSupplierRecommend> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(request.getCompanyInfoIds())) return new ArrayList<>();
        request.getCompanyInfoIds().forEach(companyInfoId -> {
            final CompanyMallSupplierRecommendAddRequest addRequest = new CompanyMallSupplierRecommendAddRequest();
            addRequest.setCompanyInfoId(companyInfoId);
            addRequest.setOperator(request.getOperator());
            list.add(add(addRequest));
        });
        return list;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean sort(Long id, BigDecimal sort) {
        CompanyMallSupplierRecommend companyMallSupplierRecommend = companyMallSupplierRecommendRepository.findById(id).orElse(null);
        if (null == companyMallSupplierRecommend || null == companyMallSupplierRecommend.getSort()) {
            throw new SbcRuntimeException("参数异常");
        }
        final CompanyMallSupplierRecommendRequest query = new CompanyMallSupplierRecommendRequest();
        query.setDeleteFlag(DeleteFlag.NO);
        query.setPageNum(0);
        query.setPageSize(Integer.MAX_VALUE);
        query.putSort("sort", SortType.ASC.toValue());
        final Page<CompanyMallSupplierRecommend> page = page(query);
        final List<CompanyMallSupplierRecommend> content = page.getContent();
        List<Long> ids = new ArrayList<>();
        content.forEach(o -> {
            if (o.getId().equals(id)) return;
            ids.add(o.getId());
        });
        ids.add(sort.subtract(BigDecimal.ONE).intValue(), id);
        for (int i = 0; i < ids.size(); i++) {
            companyMallSupplierRecommendRepository.updateSortById(ids.get(i), new BigDecimal(i + 1));
        }
        return true;
    }
}
