package com.wanmi.sbc.saas;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaQueryProvider;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaByStoreIdRequest;
import com.wanmi.sbc.saas.api.response.domainstorerela.DomainStoreRelaByStoreIdResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songhanlin
 * @Date: Created In 19:22 2020/1/15
 * @Description: Saas配置
 */
@Api(tags = "SaasSettingController", description = "Saas配置 Api")
@RestController
@RequestMapping("/saas")
public class SaasSettingController {

    @Autowired
    private DomainStoreRelaQueryProvider domainStoreRelaQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "查询Saas化开启状态")
    @GetMapping("/status")
    public BaseResponse<Boolean> queryStatus() {
        return BaseResponse.success(commonUtil.getSaasStatus());
    }   

    @ApiOperation(value = "查询门店域名映射")
    @GetMapping("/getDomainStoreRela")
    public BaseResponse<DomainStoreRelaByStoreIdResponse> getDomainStoreRela() {
        if(commonUtil.getSaasStatus()){
            return domainStoreRelaQueryProvider.findByStoreId(DomainStoreRelaByStoreIdRequest.builder()
                    .storeId(commonUtil.getStoreId())
                    .build());
        }
        return BaseResponse.SUCCESSFUL();
    }
}
