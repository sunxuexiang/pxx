package com.wanmi.sbc.customer.api.provider.merchantregistration;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationByIdRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationListRequest;
import com.wanmi.sbc.customer.api.request.merchantregistration.MerchantRegistrationPageRequest;
import com.wanmi.sbc.customer.api.response.merchantregistration.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 商家入驻申请记录-商家入驻申请记录查询API
 * @Author: hudong
 * @CreateDate: 2023/6/17 08:25
 * @Version: 1.0
 */
@FeignClient(value = "${application.customer.name}",url="${feign.url.customer:#{null}}", contextId = "RegistrationApplicationQueryProvider")
public interface RegistrationApplicationQueryProvider {


    /**
     * 查询首个商家入驻申请信息
     *
     * @return 商家入驻申请信息 {@link MerchantRegistrationGetResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/get-application-info")
    BaseResponse<MerchantRegistrationGetResponse> getMerchantApplication();

    /**
     * 查询首个商家入驻申请信息，不存在则新增再返回
     *
     * @return 商家入驻申请信息 {@link MerchantRegistrationGetWithAddResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/get-application-info-with-add")
    BaseResponse<MerchantRegistrationGetWithAddResponse> getMerchantApplicationWithAdd();

    /**
     * 根据id查询商家入驻申请信息
     *
     * @param request 包含id的查询参数 {@link MerchantRegistrationByIdResponse}
     * @return 商家入驻申请 {@link MerchantRegistrationByIdRequest}
     */
    @PostMapping("/customer/${application.customer.version}/registration/get-application-info-by-id")
    BaseResponse<MerchantRegistrationByIdResponse> getMerchantApplicationById(@RequestBody @Valid MerchantRegistrationByIdRequest request);

    /**
     * 商家入驻申请分页列表
     *
     * @param request 条件查询参数 {@link MerchantRegistrationPageRequest}
     * @return 商家入驻申请分页列表 {@link MerchantRegistrationPageResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/page-application-info")
    BaseResponse<MerchantRegistrationPageResponse> pageMerchantApplication(@RequestBody MerchantRegistrationPageRequest request);

    /**
     * 商家入驻申请信息列表
     *
     * @param request 条件查询参数 {@link MerchantRegistrationListRequest}
     * @return 商家入驻申请列表 {@link MerchantRegistrationListResponse}
     */
    @PostMapping("/customer/${application.customer.version}/registration/list-application-info")
    BaseResponse<MerchantRegistrationListResponse> listMerchantApplication(@RequestBody MerchantRegistrationListRequest request);




}
