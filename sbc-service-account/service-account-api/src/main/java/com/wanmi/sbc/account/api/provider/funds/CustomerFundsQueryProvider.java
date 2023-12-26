package com.wanmi.sbc.account.api.provider.funds;

import com.wanmi.sbc.account.api.request.funds.CustomerFundsByCustomerFundsIdRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsByCustomerIdRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsExportRequest;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsPageRequest;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsByCustomerFundsIdResponse;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsExportResponse;
import com.wanmi.sbc.account.api.response.funds.CustomerFundsPageResponse;
import com.wanmi.sbc.common.base.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 会员资金查询接口
 * @author: Geek Wang
 * @createDate: 2019/2/19 14:33
 * @version: 1.0
 */
@FeignClient(value = "${application.account.name}", url="${feign.url.account:#{null}}", contextId = "CustomerFundsQueryProvider")
public interface CustomerFundsQueryProvider {

    /**
     * 会员资金分页查询
     * @param request 分页查询条件 {@link CustomerFundsPageRequest}
     * @return {@link BaseResponse<CustomerFundsPageResponse>}
     */
    @PostMapping("/account/${application.account.version}/funds/page")
    BaseResponse<CustomerFundsPageResponse> page(@RequestBody @Valid CustomerFundsPageRequest request);

    /**
     * 会员资金导出查询
     * @param request 导出查询条件 {@link CustomerFundsExportRequest}
     * @return {@link BaseResponse<CustomerFundsExportResponse>}
     */
    @PostMapping("/account/${application.account.version}/funds/export")
    BaseResponse<CustomerFundsExportResponse> export(@RequestBody @Valid CustomerFundsExportRequest request);

    /**
     * 根据会员资金ID查询会员资金信息
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/funds/get-by-customer-funds-id")
    BaseResponse<CustomerFundsByCustomerFundsIdResponse> getByCustomerFundsId(@RequestBody @Valid CustomerFundsByCustomerFundsIdRequest request);

    /**
     * 根据会员ID查询会员资金信息
     * @param request
     * @return
     */
    @PostMapping("/account/${application.account.version}/funds/get-by-customer-id")
    BaseResponse<CustomerFundsByCustomerIdResponse> getByCustomerId(@RequestBody @Valid CustomerFundsByCustomerIdRequest request);
}
