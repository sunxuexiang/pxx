package com.wanmi.sbc.customer.provider.impl.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.merchantregistration.RegistrationApplicationQueryProvider;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationByIdRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationListRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationPageRequest;
import com.wanmi.sbc.customer.api.response.merchantregistration.*;
import com.wanmi.sbc.customer.bean.vo.MerchantRegistrationVO;
import com.wanmi.sbc.customer.merchantregistration.model.root.MerchantRegistrationApplication;
import com.wanmi.sbc.customer.merchantregistration.request.MerchantRegistrationRequest;
import com.wanmi.sbc.customer.merchantregistration.service.MerchantRegistrationApplicationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商家入驻申请查询接口实现</p>
 * @author hudong
 * @date 2023-06-17 11:13
 */
@RestController
@Validated
public class RegistrationApplicationQueryController implements RegistrationApplicationQueryProvider {

    @Autowired
    private MerchantRegistrationApplicationService merchantRegistrationApplicationService;


    @Override

    public BaseResponse<MerchantRegistrationGetResponse> getMerchantApplication() {
        MerchantRegistrationGetResponse response = new MerchantRegistrationGetResponse();
        wraperVo(merchantRegistrationApplicationService.findMerchantRegistrationApplication(), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<MerchantRegistrationGetWithAddResponse> getMerchantApplicationWithAdd() {
        MerchantRegistrationGetWithAddResponse response = new MerchantRegistrationGetWithAddResponse();
        wraperVo(merchantRegistrationApplicationService.findMerchantRegistrationApplication(), response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<MerchantRegistrationByIdResponse> getMerchantApplicationById(@RequestBody MerchantRegistrationByIdRequest request) {
        MerchantRegistrationByIdResponse response = new MerchantRegistrationByIdResponse();
        wraperVo(merchantRegistrationApplicationService.findOne(request.getApplicationId()), response);
        return BaseResponse.success(response);
    }


    @Override
    public BaseResponse<MerchantRegistrationPageResponse> pageMerchantApplication(@RequestBody MerchantRegistrationPageRequest request) {
        MerchantRegistrationRequest merchantRegistrationRequest = new MerchantRegistrationRequest();
        KsBeanUtil.copyPropertiesThird(request, merchantRegistrationRequest);
        Page<MerchantRegistrationApplication> merchantRegistrationApplicationPage = merchantRegistrationApplicationService.findAll(merchantRegistrationRequest);
        List<MerchantRegistrationVO> voList = wraperMerchantApplicationList(merchantRegistrationApplicationPage.getContent());
        MerchantRegistrationPageResponse response = MerchantRegistrationPageResponse.builder()
                .merchantRegistrationVOPage(new MicroServicePage<>(voList, request.getPageable(), merchantRegistrationApplicationPage.getTotalElements()))
                .build();
        return BaseResponse.success(response);

    }

    @Override
    public BaseResponse<MerchantRegistrationListResponse> listMerchantApplication(@RequestBody MerchantRegistrationListRequest request) {
        MerchantRegistrationRequest merchantRegistrationRequest = new MerchantRegistrationRequest();
        KsBeanUtil.copyPropertiesThird(request, merchantRegistrationRequest);
        MerchantRegistrationListResponse response = MerchantRegistrationListResponse.builder()
                .merchantRegistrationVOList(wraperMerchantApplicationList(merchantRegistrationApplicationService.findAllList(merchantRegistrationRequest)))
                .build();
        return BaseResponse.success(response);

    }


    private List<MerchantRegistrationVO> wraperMerchantApplicationList(List<MerchantRegistrationApplication> merchantRegistrationApplications) {
        if (CollectionUtils.isNotEmpty(merchantRegistrationApplications)) {
            return merchantRegistrationApplications.stream().map(merchant -> {
                MerchantRegistrationVO vo = new MerchantRegistrationVO();
                wraperVo(merchant, vo);
                return vo;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private void wraperVo(MerchantRegistrationApplication merchantRegistrationApplication, MerchantRegistrationVO merchantRegistrationVO) {
        if (merchantRegistrationApplication != null) {
            KsBeanUtil.copyPropertiesThird(merchantRegistrationApplication, merchantRegistrationVO);
        }
    }
}
