package com.wanmi.sbc.customer.company.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.company.CompanyMallReturnGoodsAddressAddRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.customer.company.model.root.CompanyMallReturnGoodsAddress;
import com.wanmi.sbc.customer.company.repository.CompanyMallReturnGoodsAddressRepository;
import com.wanmi.sbc.customer.company.request.CompanyMallReturnGoodsAddressRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CompanyMallReturnGoodsAddressService {

    @Autowired
    private CompanyMallReturnGoodsAddressRepository companyMallReturnGoodsAddressRepository;


    /**
     * @Description:商家市场列表分页
     */
    public Page<CompanyMallReturnGoodsAddress> page(CompanyMallReturnGoodsAddressRequest request) {
        return companyMallReturnGoodsAddressRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }


    public List<CompanyMallReturnGoodsAddressVO> list(CompanyMallReturnGoodsAddressRequest request) {
        return KsBeanUtil.convertList(companyMallReturnGoodsAddressRepository.findAll(request.getWhereCriteria()), CompanyMallReturnGoodsAddressVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public CompanyMallReturnGoodsAddress add(CompanyMallReturnGoodsAddressAddRequest request) {
        if (null == request.getCompanyInfoId() || null == request.getStoreId()){
            throw new SbcRuntimeException("公司ID或店铺ID不能为空");
        }
        CompanyMallReturnGoodsAddress returnGoodsAddress = companyMallReturnGoodsAddressRepository.findOne(CompanyMallReturnGoodsAddressRequest.builder()
                .companyInfoId(request.getCompanyInfoId()).build().getWhereCriteria()).orElse(null);
        CompanyMallReturnGoodsAddress target = new CompanyMallReturnGoodsAddress();
        BeanUtils.copyProperties(request, target);
        target.setUpdateTime(LocalDateTime.now());
        if (Objects.nonNull(returnGoodsAddress)) {
            target.setId(returnGoodsAddress.getId());
            target.setUpdateTime(returnGoodsAddress.getUpdateTime());
        } else {
            target.setCreateTime(LocalDateTime.now());
        }
        target.setDelFlag(DeleteFlag.NO);
        CompanyMallReturnGoodsAddress save = companyMallReturnGoodsAddressRepository.save(target);
        return save;
    }
}
