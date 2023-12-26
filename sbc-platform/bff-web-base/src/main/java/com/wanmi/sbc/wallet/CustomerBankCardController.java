package com.wanmi.sbc.wallet;

import com.wanmi.sbc.account.api.provider.wallet.CustomerBankCardProvider;
import com.wanmi.sbc.account.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.wallet.BankCardInfoListResponse;
import com.wanmi.sbc.account.api.response.wallet.BankCardResponse;
import com.wanmi.sbc.account.bean.enums.SmsTemplate;
import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: chenxiuzhu
 * @create: 2022-04-09 8:43
 */
@Api(tags = "CustomerBindBankCardController", description = "用户钱包银行卡API")
@RestController
@RequestMapping("/bankCard")
@Slf4j
@Validated
public class CustomerBankCardController {


    @Autowired
    private CustomerBankCardProvider customerBankCardProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private CommonUtil commonUtil;


    /**
     * 绑定银行卡
     */
    @ApiOperation(value = "绑定银行卡")
    @RequestMapping(value = "/bindBankCard", method = RequestMethod.POST)
    public BaseResponse<BankCardResponse> bindBankCard(@Valid @RequestBody CustomerBankCardRequest request){
        //银行卡是否绑定
        Integer result = customerBankCardProvider.isBindingBankCard(request).getContext();
        if(result>0){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"此银行卡已绑定！");
        }

//        //验证码验证
//        String smsKey = CacheKeyConstant.YZM_BIND_BANK_CARD.concat(request.getBindPhone());
//        String t_verifyCode = redisService.getString(smsKey);
//        if(StringUtils.isBlank(t_verifyCode) || !t_verifyCode.equalsIgnoreCase(request.getVerifyCode())){
//            throw new SbcRuntimeException(SiteResultCode.ERROR_000010);
//        }

        return BaseResponse.success(customerBankCardProvider.bindBankCard(request).getContext());
    }

    /**
     * 修改银行卡信息
     * @param request
     * @return
     */
    @ApiOperation(value = "修改银行卡信息")
    @RequestMapping(value = "/updateBankCard", method = RequestMethod.PUT)
    public BaseResponse<BankCardResponse> updateBankCard(@Valid @RequestBody CustomerBankCardRequest request){
        return BaseResponse.success(customerBankCardProvider.updateBankCard(request).getContext());
    }

    /**
     * 解绑/删除银行卡
     * @param request
     * @return
     */
    @ApiOperation(value = "解绑/删除银行卡")
    @RequestMapping(value = "/delOrUnboundBankCard", method = RequestMethod.DELETE)
    public BaseResponse<BankCardResponse> delOrUnboundBankCard(@Valid @RequestBody CustomerBankCardRequest request){
        return BaseResponse.success(customerBankCardProvider.delOrUnboundBankCard(request).getContext());
    }

    /**
     * 获取客户所有银行卡列表信息
     * @param request
     * @return
     */
    @ApiOperation(value = "查询银行卡列表")
    @RequestMapping(value = "getBankCardsByWalletId", method = RequestMethod.POST)
    public BaseResponse<BankCardInfoListResponse> getBankCardsByWalletId(@Valid @RequestBody CustomerBankCardRequest request){
        return BaseResponse.success(customerBankCardProvider.getBankCardsByWalletId(request).getContext());
    }

    /**
     * 根据银行卡号获取客户银行卡详细信息
     * @param request
     * @return
     */
    @ApiOperation(value = "查询银行卡详细信息-bankCode")
    @RequestMapping(value = "getBankCardByCode", method = RequestMethod.POST)
    public BaseResponse<BankCardResponse> getBankCardByCode(@Valid @RequestBody CustomerBankCardRequest request){
        return BaseResponse.success(customerBankCardProvider.getBankCardByCode(request).getContext());
    }

    /**
     * 分页查询银行卡列表根据钱包id-walletId
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询银行卡列表根据钱包id-walletId")
    @RequestMapping(value = "getBankCardsPage", method = RequestMethod.POST)
    public BaseResponse<Page<BindBankCardVo>> getBankCardsPage(@Valid @RequestBody BankCardPageRequest request){
        request.setDelFlag(DefaultFlag.NO);
        request.putSort("createTime", SortType.DESC.toValue());
        MicroServicePage<BindBankCardVo> bankCardVoPage = customerBankCardProvider.getBankCardsPage(request).getContext().getBindBankCardVoPage();
        return BaseResponse.success(bankCardVoPage);
    }

    /**
     * 绑定银行卡发送手机验证码
     * @param bindPhone
     * @return
     */
    @ApiOperation(value = "绑定银行卡发送手机验证码")
    @ApiImplicitParam(paramType = "path",dataType = "String", name = "bindPhone", value = "绑定手机号",required = true)
    @RequestMapping(value = "bindBankCardSendMobileCode/{bindPhone}", method = RequestMethod.POST)
    public BaseResponse sendMobileCode(@PathVariable String bindPhone){
        //同一手机是否操作频繁
        BankCardValidateSendMobileCodeRequest request = new BankCardValidateSendMobileCodeRequest();
        request.setMobile(bindPhone);
        if(!customerBankCardProvider.validateSendMobileCode(request).getContext().getResult()){
            throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
        }

        //发送验证码
        BankCardSendMobilCodeRequest sendRequest = new BankCardSendMobilCodeRequest();
        sendRequest.setMobile(bindPhone);
        sendRequest.setRedisKey(CacheKeyConstant.YZM_BIND_BANK_CARD);
        sendRequest.setSmsTemplate(SmsTemplate.CUSTOMER_BIND_BANK_CARD);
        if(Constants.yes.equals(customerBankCardProvider.sendMobileCode(sendRequest).getContext().getResult())){
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.FAILED();
    }
}
