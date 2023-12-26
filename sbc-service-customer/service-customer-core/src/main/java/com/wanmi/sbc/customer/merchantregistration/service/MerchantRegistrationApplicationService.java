package com.wanmi.sbc.customer.merchantregistration.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CompanyInfoErrorCode;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationSaveRequest;
import com.wanmi.sbc.customer.bean.enums.HandleFlag;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.merchantregistration.model.root.MerchantRegistrationApplication;
import com.wanmi.sbc.customer.merchantregistration.repository.MerchantRegistrationApplicationRepository;
import com.wanmi.sbc.customer.merchantregistration.request.MerchantRegistrationRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 商家入驻申请数据层
 * @author hudong
 * @date 2023-06-17 11:34
 */
@Service
public class MerchantRegistrationApplicationService {
    @Autowired
    private MerchantRegistrationApplicationRepository merchantRegistrationApplicationRepository;

    /**
     * 查询商家入驻申请信息
     *
     * @return
     */
    public MerchantRegistrationApplication findMerchantRegistrationApplication() {
        MerchantRegistrationApplication merchantRegistrationApplication = new MerchantRegistrationApplication();
        if (CollectionUtils.isEmpty(merchantRegistrationApplicationRepository.findAll())) {
            return merchantRegistrationApplication;
        }
        merchantRegistrationApplication = merchantRegistrationApplicationRepository.findAll().get(0);
        return merchantRegistrationApplication;
    }

    /**
     * 根据商家名称查询商家入驻申请信息
     * @param merchantName
     * @return
     */
    public MerchantRegistrationApplication findByMerchantNameAndDelFlag(String merchantName) {
        MerchantRegistrationApplication merchantRegistrationApplication = merchantRegistrationApplicationRepository.findByMerchantNameAndDelFlag(merchantName, DeleteFlag.NO);
        if(Objects.isNull(merchantRegistrationApplication)) {
            return null;
        }
        return merchantRegistrationApplication;
    }

    /**
     * 根据商家联系方式查询商家入驻申请信息
     * @param merchantPhone
     * @return
     */
    public MerchantRegistrationApplication findByMerchantPhoneAndDelFlag(String merchantPhone) {
        MerchantRegistrationApplication merchantRegistrationApplication = merchantRegistrationApplicationRepository.findByMerchantPhoneAndDelFlag(merchantPhone);
        if(Objects.isNull(merchantRegistrationApplication)) {
            return null;
        }
        return merchantRegistrationApplication;
    }

    /**
     * 查询指定商家入驻申请信息
     *
     * @return
     */
    public MerchantRegistrationApplication findOne(Long applicationId) {
        MerchantRegistrationApplication info = merchantRegistrationApplicationRepository.findById(applicationId).
                orElseThrow(()->new SbcRuntimeException(CompanyInfoErrorCode.NOT_EXIST));
        return info;
    }

    /**
     * 保存商家入驻申请信息
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public MerchantRegistrationApplication saveMerchantRegistrationApplication(MerchantRegistrationSaveRequest saveRequest) {
        MerchantRegistrationApplication merchantRegistrationApplication = new MerchantRegistrationApplication();
        BeanUtils.copyProperties(saveRequest, merchantRegistrationApplication);
        merchantRegistrationApplication.setDelFlag(DeleteFlag.NO);
        //默认都是未处理的 等工作人员沟通完成以后更新为已处理
        merchantRegistrationApplication.setHandleFlag(HandleFlag.NO_HANDLE.toValue());
        merchantRegistrationApplication.setCreateTime(LocalDateTime.now());
        return merchantRegistrationApplicationRepository.save(merchantRegistrationApplication);
    }

    /**
     * 修改商家入驻申请信息
     *
     * @param saveRequest
     * @return
     */
    @Transactional
    public MerchantRegistrationApplication updateMerchantRegistrationApplication(MerchantRegistrationSaveRequest saveRequest) {
        MerchantRegistrationApplication merchantRegistrationApplication = merchantRegistrationApplicationRepository.findById(saveRequest.getApplicationId()).orElse(null);
        if (Objects.isNull(merchantRegistrationApplication)){
            return null;
        }
        KsBeanUtil.copyProperties(saveRequest, merchantRegistrationApplication);
        //工作人员处理完成 状态更新为已处理
        merchantRegistrationApplication.setHandleFlag(HandleFlag.HANDLE.toValue());
        merchantRegistrationApplication.setUpdateTime(LocalDateTime.now());
        return merchantRegistrationApplicationRepository.save(merchantRegistrationApplication);
    }


    /**
     * 查询入驻申请列表(分页)
     *
     * @param request
     * @return
     */
    public Page<MerchantRegistrationApplication> findAll(MerchantRegistrationRequest request) {
        return merchantRegistrationApplicationRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 查询入驻申请列表
     *
     * @param request
     * @return
     */
    public List<MerchantRegistrationApplication> findAllList(MerchantRegistrationRequest request) {
        return merchantRegistrationApplicationRepository.findAll(request.getWhereCriteria());
    }



}
