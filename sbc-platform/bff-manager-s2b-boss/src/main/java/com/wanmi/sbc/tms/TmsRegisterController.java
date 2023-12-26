package com.wanmi.sbc.tms;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.customer.BelugaMallProvider;
import com.wanmi.sbc.customer.api.request.contract.BelugaInfoContractFindRequest;
import com.wanmi.sbc.customer.api.request.contract.BelugaMallContractFindRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractRequest;
import com.wanmi.sbc.customer.api.request.fadada.BelugaMallContractSaveRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaNotifyRequest;
import com.wanmi.sbc.customer.api.request.fadada.FadadaRegisterRequest;
import com.wanmi.sbc.customer.api.request.store.AccountDateModifyRequest;
import com.wanmi.sbc.customer.api.response.fadada.BelugaMallContractResponese;
import com.wanmi.sbc.customer.api.response.fadada.FadadaParamsResponese;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.tms.api.RemoteTmsOrderService;
import com.wanmi.sbc.tms.api.domain.vo.SysUser;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@RestController
@RequestMapping("/tms")
@Api(tags = "TmsRegisterController", description = "TMS承运商入驻")
public class TmsRegisterController {

    @Autowired
    RemoteTmsOrderService remoteTmsOrderService;
    @Autowired
    private BelugaMallProvider belugaMallProvider;

    @ApiOperation(value = "编辑合同保存合同")
    @PostMapping(value = "/addBelugaMallContract")
    public BaseResponse<FadadaParamsResponese> addBelugaMallContract(@RequestBody BelugaMallContractRequest request) {

        return belugaMallProvider.addBelugaMallContract(request);
    }

    @ApiOperation(value = "注册法大大回调")
    @GetMapping(value = "/returnRegister")
    public RedirectView returnRegister (@Param("companyName")String companyName,
                                        @Param("transactionNo")String transactionNo,
                                        @Param("authenticationType") String authenticationType,
                                        @Param("status")String status, @Param("sign")String sign) {

        FadadaRegisterRequest fadadaRegisterRequest = new FadadaRegisterRequest();
        fadadaRegisterRequest.setStatus(status);
        fadadaRegisterRequest.setSign(sign);
        fadadaRegisterRequest.setAuthenticationType(authenticationType);
        fadadaRegisterRequest.setCompanyName(companyName);
        fadadaRegisterRequest.setTransactionNo(transactionNo);
        belugaMallProvider.returnRegister(fadadaRegisterRequest);
        return null;
    }

    @ApiOperation(value = "调用法大大异步回调")
    @GetMapping(value = "/astcExtSign")
    public void astcExtSign(@Param("transaction_id") String transaction_id,@Param("result_code")String result_code,@Param("result_desc") String result_desc
            ,@Param("download_url") String download_url,@Param("viewpdf_url")String viewpdf_url,@Param("timestamp")String timestamp,@Param("msg_digest")String msg_digest) {
        FadadaNotifyRequest fadadaNotifyRequest = new FadadaNotifyRequest();
        fadadaNotifyRequest.setDownload_url(download_url);
        fadadaNotifyRequest.setMsg_digest(msg_digest);
        fadadaNotifyRequest.setTimestamp(timestamp);
        fadadaNotifyRequest.setResult_desc(result_desc);
        fadadaNotifyRequest.setTransaction_id(transaction_id);
        fadadaNotifyRequest.setViewpdf_url(viewpdf_url);
        fadadaNotifyRequest.setResult_code(result_code);
        belugaMallProvider.astcExtSign(fadadaNotifyRequest);
        // 查询单个承运商
        BelugaMallContractFindRequest findRequest = new BelugaMallContractFindRequest();
        findRequest.setTransactionId(transaction_id);
        BelugaMallContractResponese context = belugaMallProvider.findBylugaMallContractByTra(findRequest).getContext();

        // 注册承运商
        SysUser user = new SysUser();
        user.setPassword("123456");
        user.setUserName(context.getPhoneNumber());
        remoteTmsOrderService.registerUserInfo(user);
    }

    @ApiOperation(value = "保存承运商基本信息")
    @RequestMapping(value = "/saveFristBelugaMallContract",method = RequestMethod.POST)
    public BaseResponse saveFristBelugaMallContract (@RequestBody BelugaMallContractSaveRequest request) {

        return belugaMallProvider.saveFristBelugaMallContract(request);
    }

    @ApiOperation(value = "查询承运商基本信息")
    @PostMapping(value = "/findBelugaInfo")
    public BaseResponse findBelugaInfo(@RequestBody BelugaInfoContractFindRequest request) {

        return belugaMallProvider.findBelugaInfo(request);
    }



}
