package com.wanmi.sbc.account;

import com.wanmi.sbc.account.api.provider.company.CompanyAccountQueryProvider;
import com.wanmi.sbc.account.api.request.company.CompanyAccountByCompanyInfoIdAndDefaultFlagRequest;
import com.wanmi.sbc.account.bean.vo.CompanyAccountVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商家结算银行账户
 * Created by sunkun on 2017/11/2.
 */
@RestController("bossOfflineAccountController")
@RequestMapping("/account")
@Api(tags = "OfflineAccountController", description = "S2B 平台端-商家结算银行账户API")
public class OfflineAccountController {

    @Autowired
    private CompanyAccountQueryProvider companyAccountQueryProvider;

    /**
     * 获取商家结算银行账户
     *
     * @return
     */
    @ApiOperation(value = "S2B 平台端-获取商家结算银行账户")
    @ApiImplicitParam(paramType = "path", dataType = "Long",name = "companyInfoId", value = "商家的公司id", required = true)
    @RequestMapping(value = "/list/{companyInfoId}", method = RequestMethod.GET)
    public BaseResponse<List<CompanyAccountVO>> list(@PathVariable Long companyInfoId) {
        return BaseResponse.success(companyAccountQueryProvider.listByCompanyInfoIdAndDefaultFlag(
                CompanyAccountByCompanyInfoIdAndDefaultFlagRequest.builder()
                        .companyInfoId(companyInfoId).defaultFlag(DefaultFlag.NO).build()
        ).getContext().getCompanyAccountVOList());
    }
}
