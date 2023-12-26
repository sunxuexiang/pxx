package com.wanmi.sbc.customer;


import com.wanmi.sbc.account.api.provider.customerdrawcash.CustomerDrawCashProvider;
import com.wanmi.sbc.account.api.provider.customerdrawcash.CustomerDrawCashQueryProvider;
import com.wanmi.sbc.account.api.provider.customerdrawcash.CustomerDrawCashSaveProvider;
import com.wanmi.sbc.account.api.request.customerdrawcash.*;
import com.wanmi.sbc.account.api.response.customerdrawcash.CustomerDrawCashAddResponse;
import com.wanmi.sbc.account.api.response.customerdrawcash.CustomerDrawCashByIdResponse;
import com.wanmi.sbc.account.api.response.customerdrawcash.CustomerDrawCashModifyResponse;
import com.wanmi.sbc.account.bean.enums.AuditStatus;
import com.wanmi.sbc.account.bean.enums.CustomerOperateStatus;
import com.wanmi.sbc.account.bean.enums.DrawCashSource;
import com.wanmi.sbc.account.bean.vo.CustomerDrawCashVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerCheckPayPasswordRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.GatewayConfigByGatewayRequest;
import com.wanmi.sbc.pay.api.response.PayGatewayConfigResponse;
import com.wanmi.sbc.pay.bean.enums.PayGatewayEnum;
import com.wanmi.sbc.third.login.api.WechatApi;
import com.wanmi.sbc.third.login.response.GetAccessTokeResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 会员提现管理
 */
@RestController(value = "CustomerDrawCashController")
@RequestMapping("/draw/cash")
@Api(tags = "CustomerDrawCashController",description = "S2B 会员提现管理API")
public class CustomerDrawCashController {

    @Autowired
    private CustomerDrawCashQueryProvider customerDrawCashQueryProvider;

    @Autowired
    private CustomerDrawCashProvider customerDrawCashProvider;

    @Autowired
    private CustomerDrawCashSaveProvider customerDrawCashSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private WechatApi wechatApi;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    /**
     * 会员提现管理列表
     * @param request
     * @return
     */
    @ApiOperation(value = "S2B 会员提现管理列表")
    @RequestMapping(value = "/page",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CustomerDrawCashVO> > page(@RequestBody CustomerDrawCashQueryRequest request){

        CustomerDrawCashPageRequest cashPageRequest = CustomerDrawCashPageRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .auditStatus(request.getAuditStatus())
                .finishStatus(request.getFinishStatus())
                .customerOperateStatus(request.getCustomerOperateStatus())
                .drawCashStatus(request.getDrawCashStatus())
                .sourceFromPlatForm(Boolean.FALSE)
                .build();
        cashPageRequest.setPageNum(request.getPageNum());
        cashPageRequest.setPageSize(request.getPageSize());

        MicroServicePage<CustomerDrawCashVO> page = customerDrawCashQueryProvider.page(cashPageRequest).getContext().getCustomerDrawCashVOPage();

        return BaseResponse.success(page);
    }



    /**
     * 查询提现单详情
     */
    @ApiOperation(value = "查询提现单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "dcId", value = "提现单ID", required = true)
    @RequestMapping(value = "/detail/{dcId}", method = RequestMethod.GET)
    public BaseResponse<CustomerDrawCashByIdResponse> detail(@PathVariable String dcId) {

        return customerDrawCashQueryProvider.getById(CustomerDrawCashByIdRequest.builder().drawCashId(dcId).build());
    }


    /**
     * 查询提现单详情
     */
    @ApiOperation(value = "查询提现单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "dcId", value = "提现单ID", required = true)
    @RequestMapping(value = "/cancel/{dcId}", method = RequestMethod.GET)
    public BaseResponse<CustomerDrawCashModifyResponse> cancel(@PathVariable String dcId) {
        CustomerDrawCashVO cash = customerDrawCashQueryProvider.getById(CustomerDrawCashByIdRequest.builder()
                .drawCashId(dcId)
                .build())
                .getContext().getCustomerDrawCashVO();

        if(!cash.getCustomerId().equals(commonUtil.getOperatorId())){
            return BaseResponse.error("非法越权操作");
        }

        if(cash.getAuditStatus() != AuditStatus.WAIT){
            throw new SbcRuntimeException("K-090102");
        }

        return customerDrawCashSaveProvider.modify(
                CustomerDrawCashModifyRequest.builder()
                        .drawCashId(dcId)
                        .drawCashNo(cash.getDrawCashNo())
                        .applyTime(cash.getApplyTime())
                        .customerId(cash.getCustomerId())
                        .customerName(cash.getCustomerName())
                        .customerAccount(cash.getCustomerAccount())
                        .drawCashChannel(cash.getDrawCashChannel())
                        .drawCashAccountName(cash.getDrawCashAccountName())
                        .drawCashAccount(cash.getDrawCashAccount())
                        .drawCashSum(cash.getDrawCashSum())
                        .drawCashRemark(cash.getDrawCashRemark())
                        .auditStatus(cash.getAuditStatus())
                        .rejectReason(cash.getRejectReason())
                        .drawCashStatus(cash.getDrawCashStatus())
                        .drawCashFailedReason(cash.getDrawCashFailedReason())
                        .customerOperateStatus(CustomerOperateStatus.CANCEL)
                        .finishStatus(cash.getFinishStatus())
                        .finishTime(cash.getFinishTime())
                        .supplierOperateId(cash.getSupplierOperateId())
                        .updateTime(cash.getUpdateTime())
                        .delFlag(cash.getDelFlag())
                        .accountBalance(cash.getAccountBalance())
                        .build()
        );
    }

    /**
     * 新增提现记录
     * @param customerDrawCashAddRequest
     * @return
     */
    @ApiOperation(value = "新增提现记录")
    @RequestMapping(value = "/addCustomerDrawCash",method = RequestMethod.POST)
    public BaseResponse<CustomerDrawCashAddResponse> addCustomerDrawCash(@RequestBody CustomerDrawCashAddRequest customerDrawCashAddRequest){
        CustomerVO customerVO = commonUtil.getCustomer();
        RLock rLock = redissonClient.getFairLock(customerVO.getCustomerId());
        rLock.lock();
        try {
            CustomerCheckPayPasswordRequest customerCheckPayPasswordRequest = new CustomerCheckPayPasswordRequest();
            customerCheckPayPasswordRequest.setPayPassword(customerDrawCashAddRequest.getPayPassword());
            customerCheckPayPasswordRequest.setCustomerId(customerVO.getCustomerId());
            //校验密码
            customerSiteProvider.checkCustomerPayPwd(customerCheckPayPasswordRequest);
            // 应用唯一标识，在微信开放平台提交应用审核通过后获得
            String appId;
            // 应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
            String secret;
            // APP提现
            if (DrawCashSource.APP == customerDrawCashAddRequest.getDrawCashSource()) {
                PayGatewayConfigResponse payGatewayConfig = payQueryProvider.getGatewayConfigByGateway(new
                        GatewayConfigByGatewayRequest(PayGatewayEnum.WECHAT, Constants.BOSS_DEFAULT_STORE_ID)).getContext();
                appId = payGatewayConfig.getOpenPlatformAppId();
                secret = payGatewayConfig.getOpenPlatformSecret();
                // 使用code换取access_token和openId
                GetAccessTokeResponse resp = wechatApi.getWeChatAccessToken(appId, secret, customerDrawCashAddRequest.getCode());
                // 保存提现终端的openId
                customerDrawCashAddRequest.setOpenId(resp.getOpenid());
            }
            customerDrawCashAddRequest.setCustomerId(customerVO.getCustomerId());
            customerDrawCashAddRequest.setCustomerName(customerVO.getCustomerDetail().getCustomerName());
            customerDrawCashAddRequest.setCustomerAccount(customerVO.getCustomerAccount());

            return customerDrawCashSaveProvider.add(customerDrawCashAddRequest);
        } catch(SbcRuntimeException e){
            throw e;
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return BaseResponse.FAILED();
    }

    /**
     * 获取当天提现金额
     * @return
     */
    @RequestMapping(value = "/countDrawCashSumByCustId", method = RequestMethod.GET)
    public BaseResponse<BigDecimal> countDrawCashSumByCustId(){
        CustomerDrawCashQueryRequest request = new CustomerDrawCashQueryRequest();
        request.setCustomerId(commonUtil.getCustomer().getCustomerId());
        return customerDrawCashQueryProvider.countDrawCashSum(request);
    }


}
