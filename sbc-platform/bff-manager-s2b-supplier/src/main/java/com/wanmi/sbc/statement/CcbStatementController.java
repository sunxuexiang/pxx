package com.wanmi.sbc.statement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.pay.api.provider.CcbStatementProvider;
import com.wanmi.sbc.pay.api.request.CcbClrgSummaryPageRequest;
import com.wanmi.sbc.pay.api.request.CcbStatementDetPageRequest;
import com.wanmi.sbc.pay.api.response.CcbClrgSummaryResponse;
import com.wanmi.sbc.pay.api.response.CcbStatementDetResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 建行对账单API
 * @author hudong
 * 2023/9/11 14:36
 */
@Api(description = "建行对账单API",tags = "CcbStatementController")
@RestController
@RequestMapping(value = "/ccb/statement")
public class CcbStatementController {
    @Autowired
    private CcbStatementProvider ccbStatementProvider;

    @ApiOperation(value = "分页获取对账单汇总列表信息")
    @PostMapping("/sum/page")
    public BaseResponse<CcbClrgSummaryResponse> sumPage(@RequestBody @Valid CcbClrgSummaryPageRequest request) {
        return BaseResponse.success(ccbStatementProvider.pageBySummary(request).getContext());
    }

    @ApiOperation(value = "分页获取对账单明细信息")
    @PostMapping("/detail/page")
    public BaseResponse<CcbStatementDetResponse> detailPage(@RequestBody @Valid CcbStatementDetPageRequest request) {
        return BaseResponse.success(ccbStatementProvider.page(request).getContext());
    }


}
