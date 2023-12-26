package com.wanmi.sbc.account.wallet.service;

import com.wanmi.sbc.account.api.request.wallet.CustomerBankCardRequest;
import com.wanmi.sbc.account.bean.enums.SmsTemplate;
import com.wanmi.sbc.account.redis.RedisService;
import com.wanmi.sbc.account.sms.SmsSendUtil;
import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.account.wallet.repository.CustomerBindBankCardRepository;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-21 11:56
 */
@Service
@Slf4j
@Transactional
public class CustomerBankCardService {

    @Autowired
    private CustomerBindBankCardRepository customerBindBankCardRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsSendUtil smsSendUtil;


    /**
     * 绑定银行卡
     * @param request
     * @return
     */
    public CustomerBindBankCard bindBankCard(CustomerBankCardRequest request){

        CustomerBindBankCard bindBankCard = customerBindBankCardRepository.findOneByBankCode(request.getBankCode());
        CustomerBindBankCard saveBankCard = new CustomerBindBankCard();
        //之前没有绑定过直接绑定，如果存在绑定的卡号直接修改原记录
        if(bindBankCard==null){
            KsBeanUtil.copyProperties(request,saveBankCard);
        }else{
            KsBeanUtil.copyProperties(request,bindBankCard);
            KsBeanUtil.copyProperties(bindBankCard,saveBankCard);
        }
        saveBankCard.setDelFlag(DefaultFlag.NO);
        saveBankCard.setCreateTime(LocalDateTime.now());
        saveBankCard.setBindTime(LocalDateTime.now());
        return customerBindBankCardRepository.save(saveBankCard);
    }

    /**
     * 修改银行卡信息
     * @param request
     * @return
     */
    public CustomerBindBankCard updateBankCard(CustomerBankCardRequest request){
        CustomerBindBankCard bankCard = customerBindBankCardRepository.findOneByBankCodeAndDelFlag(request.getBankCode(),DefaultFlag.NO);
        if(null == bankCard){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"银行卡信息不存在");
        }
        KsBeanUtil.copyProperties(request,bankCard);
        bankCard.setUpdateTime(LocalDateTime.now());
        return customerBindBankCardRepository.save(bankCard);
    }

    /**
     * 删除或解绑银行卡
     * @param request
     */
    public CustomerBindBankCard delOrUnboundBankCard(CustomerBankCardRequest request){
        CustomerBindBankCard bankCard = customerBindBankCardRepository.findOneByBankCodeAndDelFlag(request.getBankCode(),DefaultFlag.NO);
        if (null == bankCard){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"银行卡信息不存在");
        }
        bankCard.setDelFlag(DefaultFlag.YES);
        bankCard.setDelTime(LocalDateTime.now());
        return customerBindBankCardRepository.save(bankCard);
    }

    /**
     * 查询银行卡列表
     * @param request
     * @return
     */
    public List<CustomerBindBankCard> getBankCardsByWalletId(CustomerBankCardRequest request){
        List<CustomerBindBankCard> bankCardList = customerBindBankCardRepository.findAllByWalletIdAndDelFlag(request.getWalletId(),DefaultFlag.NO);
        return bankCardList;
    }

    /**
     * 查询银行卡详细信息
     * @param request
     * @return
     */
    public CustomerBindBankCard getBankCardByCode(CustomerBankCardRequest request){
        return customerBindBankCardRepository.findOneByBankCode(request.getBankCode());
    }

    /**
     * 分页查询银行卡信息
     * @param request
     * @return
     */
    public Page<CustomerBindBankCard> getBankCardsPage(CustomerBankCardRequest request){
        return customerBindBankCardRepository.findAll(CustomerBankCardQueryBuilder.build(request),request.getPageRequest());
    }

    /**
     * 发送手机验证码
     * @param redisKey 存入redis的验证码key
     * @param mobile 要发送短信的手机号码
     * @param smsTemplate 短信内容模版
     * @return
     */
    public Integer doMobileSms(String redisKey, String mobile, SmsTemplate smsTemplate){
        //记录发送时间
        redisService.hset(CacheKeyConstant.YZM_MOBILE_LAST_TIME, mobile, DateUtil.nowTime());
        //生成验证码
        String verifyCode = String.valueOf((int)(Math.random()*900000+100000));
        smsSendUtil.send(smsTemplate, new String[]{mobile}, verifyCode);
        log.info(" ==================== 短信验证码：=============== :::" + verifyCode);
        redisService.setString(redisKey.concat(mobile), verifyCode);
        redisService.expireByMinutes(redisKey.concat(mobile), Constants.SMS_TIME);

        return Constants.yes;
    }

    /**
     * 是否可以发送验证码
     * @param mobile 要发送短信的手机号码
     * @return true:可以发送，false:不可以
     */
    public boolean isSendSms(String mobile) {
        String timeStr = redisService.hget(CacheKeyConstant.YZM_MOBILE_LAST_TIME, mobile);
        if (StringUtils.isBlank(timeStr)) {
            return true;
        }
        //如果当前时间 > 上一次发送时间+1分钟
        return LocalDateTime.now().isAfter(DateUtil.parse(timeStr, DateUtil.FMT_TIME_1).plusMinutes(1));
    }

    /**
     * 验证银行卡是否绑定
     * @param BankCard
     * @return
     */
    public Integer isBindingBankCard(String BankCard){
        Integer result = customerBindBankCardRepository.countByBankCodeAndDelFlag(BankCard,DefaultFlag.NO);
        return result;
    }
}
