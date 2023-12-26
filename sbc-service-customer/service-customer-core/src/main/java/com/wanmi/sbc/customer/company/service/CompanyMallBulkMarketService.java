package com.wanmi.sbc.customer.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBatchSortEditRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallBulkMarketAddRequest;
import com.wanmi.sbc.customer.bean.enums.MallContractRelationType;
import com.wanmi.sbc.customer.bean.enums.MallOpenStatus;
import com.wanmi.sbc.customer.company.model.root.CompanyMallBulkMarket;
import com.wanmi.sbc.customer.company.model.root.CompanyMallSupplierRecommend;
import com.wanmi.sbc.customer.company.repository.CompanyMallBulkMarketRepository;
import com.wanmi.sbc.customer.company.repository.CompanyMallContractRelationRepository;
import com.wanmi.sbc.customer.company.request.CompanyMallBulkMarketRequest;
import com.wanmi.sbc.customer.company.request.CompanyMallSupplierRecommendRequest;
import com.wanmi.sbc.customer.util.PinyinUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 15:21
 **/
@Service
@Slf4j
public class CompanyMallBulkMarketService {

    @Autowired
    private CompanyMallBulkMarketRepository companyMallBulkMarketRepository;

    @Autowired
    private CompanyMallContractRelationRepository companyMallContractRelationRepository;


    /**
     * @Description:商家市场列表分页
     */
    public Page<CompanyMallBulkMarket> page(CompanyMallBulkMarketRequest request) {
        return companyMallBulkMarketRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    public List<CompanyMallBulkMarket> lisAll(CompanyMallBulkMarketRequest request) {
        return companyMallBulkMarketRepository.findAll(request.getWhereCriteria());
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyMallBulkMarket addMarket(CompanyMallBulkMarketAddRequest request) {
        CompanyMallBulkMarket target = new CompanyMallBulkMarket();
        BeanUtils.copyProperties(request, target);
        if (target.getOpenStatus() == null){
            target.setOpenStatus(MallOpenStatus.OPEN.toValue());
        }
        target.setDelFlag(DeleteFlag.NO);
        target.setCreateTime(LocalDateTime.now());
        target.setUpdateTime(LocalDateTime.now());
        if (null != request.getProvinceName() && null != request.getCityName()) {
            target.setConcatInfo(request.getProvinceName()+"/"+request.getCityName());
        }
        CompanyMallBulkMarket save = companyMallBulkMarketRepository.save(target);
        final Long marketId = save.getMarketId();
        final BigDecimal maxSort = getMaxSort();
        companyMallBulkMarketRepository.updateSortByMarketId(marketId, maxSort);
        StringBuilder sb = new StringBuilder();
        final String marketCode = sb.append("dbj").append(marketId).toString();
        companyMallBulkMarketRepository.updateMarketCodeByMarketId(marketId, marketCode);
        companyMallBulkMarketRepository.updateConcatInfoByMarketId(save.getMarketId(),wrapSearchName(save));
        save.setMarketCode(marketCode);
        save.setSort(maxSort);
        return save;
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyMallBulkMarket editMarket(CompanyMallBulkMarketAddRequest request) {
        CompanyMallBulkMarket target = companyMallBulkMarketRepository.findById(request.getMarketId()).orElse(null);
        if (null == target) {
            throw new SbcRuntimeException("参数异常");
        }
        final CompanyMallBulkMarket check = companyMallBulkMarketRepository
                .findByMarketNameAndMarketIdNotAndDelFlag(request.getMarketName(), request.getMarketId(), DeleteFlag.NO);
        if (check != null){
            throw new SbcRuntimeException("市场名称重复");
        }
        wrapUpdateJustNeed(request, target);
        target.setUpdateTime(LocalDateTime.now());
        final CompanyMallBulkMarket save = companyMallBulkMarketRepository.save(target);
        if (request.getDelFlag() != null && request.getDelFlag() == DeleteFlag.YES){
            // 删除签约信息
            companyMallContractRelationRepository.deleteByRelationTypeAndRelationValue(MallContractRelationType.MARKET.getValue()
                    , save.getMarketId().toString());
        }
        companyMallBulkMarketRepository.updateConcatInfoByMarketId(save.getMarketId(),wrapSearchName(save));
        return save;
    }

    public CompanyMallBulkMarket getById(Long marketId) {
        CompanyMallBulkMarket target = new CompanyMallBulkMarket();
        CompanyMallBulkMarket source = companyMallBulkMarketRepository.findById(marketId).orElse(null);
        KsBeanUtil.copyPropertiesThird(source, target);
        return target;
    }

    private static void wrapUpdateJustNeed(CompanyMallBulkMarketAddRequest request, CompanyMallBulkMarket target) {
        String s;
        Long l;
        Integer i;
        DeleteFlag d;
        // 可以编辑的字段
        if (StringUtils.isNotBlank(s = request.getMarketName())) {
            target.setMarketName(s);
        }
        if (StringUtils.isNotBlank(s = request.getDetailAddress())) {
            target.setDetailAddress(s);
        }
        if (null != (l = request.getProvinceId())) {
            target.setProvinceId(l);
            target.setProvinceName(request.getProvinceName());
        }
        if (null != (l = request.getCityId())) {
            target.setCityId(l);
            target.setCityName(request.getCityName());
        }
        if (null != request.getProvinceName() && null != request.getCityName()){
            target.setConcatInfo(request.getProvinceName()+"/"+request.getCityName());
        }
        if (null != (i = request.getOpenStatus())) {
            target.setOpenStatus(i);
        }
        if (null != (d = request.getDelFlag())) {
            target.setDelFlag(d);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSort(List<CompanyMallBatchSortEditRequest.Sort> sorts) {
        sorts.forEach(o -> companyMallBulkMarketRepository.updateSortByMarketId(o.getSortId(),o.getSort()));
    }
    private BigDecimal getMaxSort() {
        BigDecimal maxSort = (maxSort = companyMallBulkMarketRepository.getMaxSort()) == null ? BigDecimal.ZERO : maxSort;
        maxSort = maxSort.add(BigDecimal.ONE);
        return maxSort;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sort(Long id, BigDecimal sort) {
        // 先减1
        CompanyMallBulkMarket companyMallBulkMarket = companyMallBulkMarketRepository.findById(id).orElse(null);
        if (null == companyMallBulkMarket || null == companyMallBulkMarket.getSort()) {
            throw new SbcRuntimeException("参数异常");
        }
        final CompanyMallBulkMarketRequest query = new CompanyMallBulkMarketRequest();
        query.setDeleteFlag(DeleteFlag.NO);
        query.setPageNum(0);
        query.setPageSize(Integer.MAX_VALUE);
        query.putSort("sort", SortType.ASC.toValue());
        final Page<CompanyMallBulkMarket> page = page(query);
        final List<CompanyMallBulkMarket> content = page.getContent();
        List<Long> ids = new ArrayList<>();
        content.forEach(o -> {
            if (o.getMarketId().equals(id)) return;
            ids.add(o.getMarketId());
        });
        ids.add(sort.subtract(BigDecimal.ONE).intValue(), id);
        for (int i = 0; i < ids.size(); i++) {
            companyMallBulkMarketRepository.updateSortByMarketId(ids.get(i), new BigDecimal(i + 1));
        }
    }

    public String wrapSearchName(CompanyMallBulkMarket market){
        try {
            final String marketName = wrapSearchNameFilterKeyWord(market.getMarketName());
            final String provinceName = wrapSearchNameFilterKeyWord(market.getProvinceName());
            final String cityName = wrapSearchNameFilterKeyWord(market.getCityName());
//            final Long cityId = market.getCityId();
//            final Long provinceId = market.getProvinceId();
            StringBuilder sb = new StringBuilder();
            sb.append(marketName).append("/").append(PinyinUtil.getFullPinyinString(marketName)).append("/").append(PinyinUtil.getFirstLettersLow(marketName)).append("/");
            sb.append(provinceName).append("/").append(PinyinUtil.getFullPinyinString(provinceName)).append("/").append(PinyinUtil.getFirstLettersLow(provinceName)).append("/");
            sb.append(cityName).append("/").append(PinyinUtil.getFullPinyinString(cityName)).append("/").append(PinyinUtil.getFirstLettersLow(cityName));

//            sb.append(provinceId).append("/").append(cityId);
            return sb.toString();
        } catch (Exception e) {
            log.error("wrapSearchName error",e);
            return market.getMarketName();
        }
    }

    public String wrapSearchNameFilterKeyWord(String filter){
        if (null == filter) return "";
        String sbStr = new StringBuilder(filter).toString();
        sbStr = sbStr.replace("市场", "");
        sbStr = sbStr.replace("批发", "");
        sbStr = sbStr.replace("省", "");
        sbStr = sbStr.replace("市", "");
        return sbStr;
    }

    @Transactional(rollbackFor = Exception.class)
    public void refreshSearchName(CompanyMallBulkMarket o) {
        companyMallBulkMarketRepository.updateConcatInfoByMarketId(o.getMarketId(),wrapSearchName(o));
    }
}
