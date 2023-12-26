package com.wanmi.sbc.customer.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBatchSortEditRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallSupplierTabAddRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyMallSupplierTabResponse;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierTab;
import com.wanmi.sbc.customer.company.repository.CompanyMallContractRelationRepository;
import com.wanmi.sbc.customer.company.repository.CompanyMallSupplierTabRepository;
import com.wanmi.sbc.customer.company.request.CompanyMallSupplierTabRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:21
 **/
@Service
public class CompanyMallSupplierTabService {

    @Autowired
    private CompanyMallSupplierTabRepository companyMallSupplierTabRepository;

    @Autowired
    private CompanyMallContractRelationRepository companyMallContractRelationRepository;


    /**
     * @Description:商家市场列表分页
     */
    public Page<CompanyMallSupplierTab> page(CompanyMallSupplierTabRequest request) {
        return companyMallSupplierTabRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    public List<CompanyMallSupplierTab> list(CompanyMallSupplierTabRequest request) {
        return companyMallSupplierTabRepository.findAll(request.getWhereCriteria());
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyMallSupplierTab add(CompanyMallSupplierTabAddRequest request) {
        request.wrap();
        if (StringUtils.isBlank(request.getTabName())){
            throw new SbcRuntimeException("商城名称不能为空");
        }
        final CompanyMallSupplierTab source = companyMallSupplierTabRepository.findByTabName(request.getTabName());
        if (source != null) {
            throw new SbcRuntimeException("当前商城已经存在");
        }
        CompanyMallSupplierTab target = new CompanyMallSupplierTab();
        BeanUtils.copyProperties(request, target);
        if (target.getOpenStatus() == null){
            target.setOpenStatus(MallOpenStatus.OPEN.toValue());
        }
        target.setDelFlag(DeleteFlag.NO);
        target.setCreateTime(LocalDateTime.now());
        target.setUpdateTime(LocalDateTime.now());
        CompanyMallSupplierTab save = companyMallSupplierTabRepository.save(target);
        BigDecimal maxSort = getMaxSort();
        companyMallSupplierTabRepository.updateSortById(maxSort, save.getId());
        save.setSort(maxSort);
        return save;
    }

    @Transactional
    public CompanyMallSupplierTab edit(CompanyMallSupplierTabAddRequest request) {
        request.wrap();
        CompanyMallSupplierTab target = companyMallSupplierTabRepository.findById(request.getId()).orElse(null);
        if (null == target) {
            throw new SbcRuntimeException("非法的商城ID");
        }
        final CompanyMallSupplierTab check = companyMallSupplierTabRepository
                .findByTabNameAndIdNotAndDelFlag(request.getTabName(), request.getId(), DeleteFlag.NO);
        if (check != null){
            throw new SbcRuntimeException("当前商城已经存在");
        }
        wrapUpdateJustNeed(request, target);
        target.setUpdateTime(LocalDateTime.now());
        CompanyMallSupplierTab save = companyMallSupplierTabRepository.save(target);
        if (request.getDelFlag() != null && request.getDelFlag() == DeleteFlag.YES){
            // 删除签约信息
            companyMallContractRelationRepository.deleteByRelationTypeAndRelationValue(MallContractRelationType.TAB.getValue()
                    , save.getId().toString());
        }
        return save;
    }

    public CompanyMallSupplierTab getById(Long id) {
        CompanyMallSupplierTab target = new CompanyMallSupplierTab();
        CompanyMallSupplierTab source = companyMallSupplierTabRepository.findById(id).orElse(null);
        KsBeanUtil.copyPropertiesThird(source, target);
        return target;
    }

    private static void wrapUpdateJustNeed(CompanyMallSupplierTabAddRequest request, CompanyMallSupplierTab target) {
        String s;
        Long l;
        Integer i;
        // 可以编辑的字段
        if (StringUtils.isNotBlank(s = request.getTabName())) {
            target.setTabName(s);
        }
        if (StringUtils.isNotBlank(s = request.getOperator())) {
            target.setOperator(s);
        }
        if (StringUtils.isNotBlank(s = request.getDeliveryTypes())) {
            target.setDeliveryTypes(s);
        }
        if (null != (l = request.getId())) {
            target.setId(l);
        }
        if (null != (i = request.getOpenStatus())) {
            target.setOpenStatus(i);
        }
        if (Objects.nonNull(request.getDelFlag())) {
            target.setDelFlag(request.getDelFlag());
        }
        if (Objects.nonNull(request.getStoreIds())) {
            target.setStoreIds(request.getStoreIds());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSort(List<CompanyMallBatchSortEditRequest.Sort> sorts) {
        sorts.forEach(o -> companyMallSupplierTabRepository.updateSortById(o.getSort(), o.getSortId()));
    }

    private BigDecimal getMaxSort() {
        BigDecimal maxSort = (maxSort = companyMallSupplierTabRepository.getMaxSort()) == null ? BigDecimal.ZERO : maxSort;
        maxSort = maxSort.add(BigDecimal.ONE);
        return maxSort;
    }

    public CompanyMallSupplierTabResponse getCompanyMallSupplierTabResponseById(Long id) {
        CompanyMallSupplierTabResponse response = new CompanyMallSupplierTabResponse();
        KsBeanUtil.copyPropertiesThird(getById(id), response);
        response.wrap();
        return response;
    }
}
