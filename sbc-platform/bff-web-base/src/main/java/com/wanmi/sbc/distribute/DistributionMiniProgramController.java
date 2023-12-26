package com.wanmi.sbc.distribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeSaveImgUrlByJobNoRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeQueryImgUrlByJobNoRequest;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.request.DistributionMiniProgramRequest;
import com.wanmi.sbc.setting.api.provider.WechatAuthProvider;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Api(tags = "DistributionMiniProgramController", description = "生成各类小程序码")
@RestController
@RequestMapping("/distribution/miniProgram-code")
@Validated
public class DistributionMiniProgramController {

    @Autowired
    private WechatAuthProvider wechatAuthProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;


    @ApiOperation(value = "分销生成各种小程序码")
    @RequestMapping(value = "/distributionMiniProgramQrCode", method = RequestMethod.POST)
    public BaseResponse<String> distributionMiniProgramQrCode(@RequestBody @Valid DistributionMiniProgramRequest request) {
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            request.setSaasStatus(Boolean.TRUE);
            request.setStoreId(domainInfo.getStoreId());
        }
        //验证是否是 导购邀新默认直接读取员工表存储数据
        if(StringUtils.isNotBlank(request.getTag()) && request.getTag().equals("jobNo") && StringUtils.isBlank(request.getRefresh()) ){
            BaseResponse<EmployeeQueryImgUrlByJobNoRequest> imgUrlByJobNo = employeeQueryProvider.findImgUrlByJobNo(EmployeeSaveImgUrlByJobNoRequest.builder().jobNo(request.getJobNo()).build());
            if(StringUtils.isNotEmpty(imgUrlByJobNo.getContext().getWechatImgUrl())){
                return BaseResponse.success(imgUrlByJobNo.getContext().getWechatImgUrl());
            }
        }

        BaseResponse<String> wxaCodeUnlimit = wechatAuthProvider.distributionMiniProgram(request);

        if (Objects.nonNull(wxaCodeUnlimit.getContext())
                && StringUtils.isNotBlank(request.getTag()) && request.getTag().equals("jobNo")) {
            employeeQueryProvider.saveWechatImgUrl(EmployeeSaveImgUrlByJobNoRequest.builder().accountType(AccountType.s2bBoss).jobNo(request.getJobNo()).wechatImgUrl(wxaCodeUnlimit.getContext()).build());
        }
        return wxaCodeUnlimit;
    }

    /**
     * 接续出分销里面生成的二维码携带的真正参数
     * @return
     */
    @ApiOperation(value = "接续出分销里面生成的二维码携带的真正参数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "redisKey", value = "redisKey", required = true)
    @RequestMapping(value = "/decodeParam/{redisKey}", method = RequestMethod.GET)
    public BaseResponse getSkuQrCode(@PathVariable String redisKey) {
        if (!Objects.isNull(redisService.getString(redisKey))) {
            return  BaseResponse.success(redisService.getString(redisKey));
            //return BaseResponse.success( redisTemplate.opsForValue().get(redisKey));
        }
        return BaseResponse.FAILED();
    }
}
