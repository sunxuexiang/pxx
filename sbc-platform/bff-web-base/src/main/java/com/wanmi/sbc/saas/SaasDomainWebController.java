package com.wanmi.sbc.saas;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songhanlin
 * @Date: Created In 18:39 2020/1/16
 * @Description: 域名映射Controller
 */
@Api(tags = "SaasDomainWebController", description = "域名映射服务API")
@RestController
@RequestMapping("/domain")
public class SaasDomainWebController {

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 查询域名信息
     *
     * @return
     */
    @ApiOperation(value = "查询域名信息")
    @GetMapping("/domain-store")
    public BaseResponse<DomainStoreRelaVO> queryDomainStore() {
        return BaseResponse.success(commonUtil.getDomainInfo());
    }

}
