package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.CustomerBankCardProvider;
import com.wanmi.sbc.account.api.request.wallet.BankCardPageRequest;
import com.wanmi.sbc.account.api.request.wallet.BankCardSendMobilCodeRequest;
import com.wanmi.sbc.account.api.request.wallet.BankCardValidateSendMobileCodeRequest;
import com.wanmi.sbc.account.api.request.wallet.CustomerBankCardRequest;
import com.wanmi.sbc.account.api.response.wallet.*;
import com.wanmi.sbc.account.bean.vo.BindBankCardVo;
import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.account.wallet.service.CustomerBankCardService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-20 15:52
 */
@RestController
@Validated
public class CustomerBankCardController implements CustomerBankCardProvider {

    @Autowired
    private CustomerBankCardService bankCardService;

    @Override
    public BaseResponse<BankCardResponse> bindBankCard(CustomerBankCardRequest request) {
        CustomerBindBankCard  bankCard = bankCardService.bindBankCard(request);
        BankCardResponse response = new BankCardResponse();
        KsBeanUtil.copyProperties(bankCard,response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BankCardResponse> updateBankCard(CustomerBankCardRequest request) {
        CustomerBindBankCard bankCard = bankCardService.updateBankCard(request);
        BankCardResponse response = new BankCardResponse();
        KsBeanUtil.copyProperties(bankCard,response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BankCardResponse> delOrUnboundBankCard(CustomerBankCardRequest request) {
        CustomerBindBankCard bankCard = bankCardService.delOrUnboundBankCard(request);
        BankCardResponse response = new BankCardResponse();
        KsBeanUtil.copyProperties(bankCard,response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BankCardInfoListResponse> getBankCardsByWalletId(CustomerBankCardRequest request) {
        List<BindBankCardVo> bankCardVos = bankCardService.getBankCardsByWalletId(request).stream()
                .map(bankCard ->{
                    BindBankCardVo vo = new BindBankCardVo();
                    KsBeanUtil.copyProperties(bankCard,vo);
                    return vo;
                }).collect(Collectors.toList());
        return BaseResponse.success(BankCardInfoListResponse.builder().bindBankCardVoList(bankCardVos).build());
    }

    @Override
    public BaseResponse<BankCardResponse> getBankCardByCode(CustomerBankCardRequest request) {
        CustomerBindBankCard bankCard = bankCardService.getBankCardByCode(request);
        if(null == bankCard){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"银行卡信息不存在");
        }
        BankCardResponse response = new BankCardResponse();
        KsBeanUtil.copyPropertiesThird(bankCard,response);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BankCardPageResponse> getBankCardsPage(BankCardPageRequest pageRequest) {
        CustomerBankCardRequest request = new CustomerBankCardRequest();
        KsBeanUtil.copyProperties(pageRequest,request);
        Page<CustomerBindBankCard> bankCardPage = bankCardService.getBankCardsPage(request);
        Page<BindBankCardVo> map = bankCardPage.map(bankCard -> {
            BindBankCardVo vo = new BindBankCardVo();
            KsBeanUtil.copyProperties(bankCard,vo);
            return vo;
        });
        MicroServicePage<BindBankCardVo> newPage = new MicroServicePage<>(map.getContent(),pageRequest.getPageable(),map.getTotalElements());
        BankCardPageResponse response = new BankCardPageResponse(newPage);
        return BaseResponse.success(response);
    }

    @Override
    public BaseResponse<BankCardValidateSendMobileCodeResponse> validateSendMobileCode(BankCardValidateSendMobileCodeRequest request) {
        Boolean result = bankCardService.isSendSms(request.getMobile());
        return BaseResponse.success(new BankCardValidateSendMobileCodeResponse(result));
    }

    @Override
    public BaseResponse<BankCardSendMobilCodeResponse> sendMobileCode(BankCardSendMobilCodeRequest request) {
        Integer result = bankCardService.doMobileSms(request.getRedisKey(),request.getMobile(),request.getSmsTemplate());
        return BaseResponse.success(new BankCardSendMobilCodeResponse(result));
    }

    @Override
    public BaseResponse<Integer> isBindingBankCard(CustomerBankCardRequest request) {
        return BaseResponse.success(bankCardService.isBindingBankCard(request.getBankCode()));
    }
}
