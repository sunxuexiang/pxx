package com.wanmi.sbc.saas;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaProvider;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaQueryProvider;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaByStoreIdAndCompanyInfoIdRequest;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaModifyRequest;
import com.wanmi.sbc.saas.api.response.domainstorerela.DomainStoreRelaByStoreIdAndCompanyInfoIdResponse;
import com.wanmi.sbc.saas.api.response.domainstorerela.DomainStoreRelaModifyResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: songhanlin
 * @Date: Created In 20:31 2020/1/15
 * @Description: 域名设置
 */
@RestController
@RequestMapping("/domain")
@Api(description = "域名设置 api", tags = "SaasDomainController")
public class SaasDomainController {

    @Autowired
    private DomainStoreRelaQueryProvider domainStoreRelaQueryProvider;

    @Autowired
    private DomainStoreRelaProvider domainStoreRelaProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "查询店铺域名")
    @GetMapping("/setting")
    public BaseResponse<DomainStoreRelaByStoreIdAndCompanyInfoIdResponse> queryDomainSetting() {

        DomainStoreRelaByStoreIdAndCompanyInfoIdRequest request = new DomainStoreRelaByStoreIdAndCompanyInfoIdRequest();
        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        return domainStoreRelaQueryProvider.getOneByStoreIdAndCompanyInfoId(request);
    }


    @ApiOperation(value = "修改店铺域名")
    @PutMapping("/setting")
    public BaseResponse<DomainStoreRelaModifyResponse> modifyDomain(@RequestBody @Valid
                                                                            DomainStoreRelaModifyRequest domainStoreRelaModifyRequest) {
        domainStoreRelaModifyRequest.setUpdatePerson(commonUtil.getOperatorId());
        domainStoreRelaModifyRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        domainStoreRelaModifyRequest.setStoreId(commonUtil.getStoreId());
        //记录操作日志
        operateLogMQUtil.convertAndSend("域名设置", "修改店铺域名", "修改店铺域名");
        return domainStoreRelaProvider.modify(domainStoreRelaModifyRequest);
    }


}
