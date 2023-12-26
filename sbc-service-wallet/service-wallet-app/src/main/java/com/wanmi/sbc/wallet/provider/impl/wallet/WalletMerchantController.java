package com.wanmi.sbc.wallet.provider.impl.wallet;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.bean.enums.PayWalletCallBackType;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.model.root.TicketsForm;
import com.wanmi.sbc.wallet.wallet.service.CustomerWalletMerchantService;
import com.wanmi.sbc.wallet.wallet.service.TicketsFormMerchantService;
import com.wanmi.sbc.wallet.wallet.service.WalletRecordMerchantService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@Validated
@Slf4j
public class WalletMerchantController implements WalletMerchantProvider {

    @Autowired
    private CustomerWalletMerchantService customerWalletMerchantService;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private TicketsFormMerchantService ticketsFormMerchantService;
    @Autowired
    private WalletRecordMerchantService walletRecordMerchantService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private EmployeeQueryProvider  employeeQueryProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private CustomerWalletQueryController walletQueryController;

    /**
     * 鲸币账户生成工单和流水记录
     * */
    @Transactional
    @LcnTransaction
    public BaseResponse<TicketsFormQueryVO> generateTicketAndRecord(CustomerWalletAddRequest walletPageQueryRequest) {
        RLock fairLock = redissonClient.getFairLock(walletPageQueryRequest.getStoreId());
        try {
            fairLock.lock();
            BigDecimal zero = new BigDecimal("0");
            if (walletPageQueryRequest.getRechargeBalance() == null || zero.compareTo(walletPageQueryRequest.getRechargeBalance()) == 0) {
                throw new SbcRuntimeException("K-111112","充值金额必须大于0");
            }
            // 判断是否是商户充值，并且是否有充值权限
            CustomerWallet walletByStoreId = customerWalletMerchantService.getWalletByStoreId(walletPageQueryRequest.getStoreId());
            if (walletByStoreId == null) {
                throw new SbcRuntimeException("K-111113","找不到该商户。请联系客服人员");
            }
            if (walletByStoreId.getIsEnable() == 0) {
                throw new SbcRuntimeException("K-111113","商户未开通鲸币权限,请开通权限后再充值");
            }
            // 生成工单
            String w1 = generatorService.generate("W");
            TicketsFormQueryVO w = TicketsFormQueryVO.builder()
                    .walletId(walletByStoreId.getWalletId())
                    .applyType(1)
                    .applyPrice(walletPageQueryRequest.getRechargeBalance())
                    .applyTime(LocalDateTime.now())
                    .rechargeStatus(1)
                    .remark("商户" + walletByStoreId.getCustomerName() + "充值" + walletPageQueryRequest.getRechargeBalance() + "元")
                    .recordNo(w1)
                    .build();
            BaseResponse<TicketsForm> ticketsFormBaseResponse = ticketsFormMerchantService.addTicKets(w);
            if (null == ticketsFormBaseResponse) {
                return BaseResponse.FAILED();
            }
            TicketsForm context = ticketsFormBaseResponse.getContext();
            TicketsFormQueryVO convert = KsBeanUtil.convert(context, TicketsFormQueryVO.class);
            return BaseResponse.success(convert);
        }finally {
            fairLock.unlock();
        }
    }

    @Override
    public void payMerchantCallBack(TradePayWalletOnlineCallBackRequest tradePayOnlineCallBackRequest) {
        try {
            if (tradePayOnlineCallBackRequest.getPayCallBackType().equals(PayWalletCallBackType.ALI)) {
                customerWalletMerchantService.aliPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }else if (tradePayOnlineCallBackRequest.getPayCallBackType().equals(PayWalletCallBackType.CCB)) {
                customerWalletMerchantService.ccbPayOnlineCallBack(tradePayOnlineCallBackRequest);
            } else {
                customerWalletMerchantService.wxPayOnlineCallBack(tradePayOnlineCallBackRequest);
            }
        } catch (Exception e) {
            log.error("鲸币异步回调异常==={}",e);
        }
    }

    @Override
    public BaseResponse<WalletRecordVO> merchantGiveUser(CustomerWalletGiveRequest request) throws SbcRuntimeException {

        return customerWalletMerchantService.merchantGiveUser(request);
    }

    /**
     * 发起提现
     * */
    public BaseResponse withdrawal(InitiateWithdrawalRequest request) {

        return customerWalletMerchantService.withdrawal(request);
    }

    /**
     * 平台给商家充值金币
     * */
    @Override
    @Transactional
    public BaseResponse platoToStore(StoreAddRequest storeAddRequest) throws Exception {
        Assert.notNull(storeAddRequest.getRechargeBalance(),"充值金额不能为空！");
        // 去掉非必填校验
//        Assert.notNull(storeAddRequest.getPaymentName(),"付款方式不能为空！");
//        Assert.notNull(storeAddRequest.getPayOrderNo(),"支付单号不能为空！");

        // 商户校验
        StoreInfoResponse storeVO = queryStoreInfo(storeAddRequest);
        log.info("进入鲸币开始===={},{}",storeAddRequest.getRechargeBalance(),storeAddRequest.getPayOrderNo());
        // 获取商户钱包
        CustomerWallet context = customerWalletMerchantService.queryWallet(storeVO.getStoreId()).getContext();
        if (StringUtils.isEmpty(storeAddRequest.getPayOrderNo())) {
            storeAddRequest.setPayOrderNo(generatorService.generate("T"));
        }

        //生成工单
        TicketsFormQueryVO w = TicketsFormQueryVO.builder()
                .walletId(context.getWalletId())
                .applyType(1)
                .applyPrice(storeAddRequest.getRechargeBalance())
                .applyTime(LocalDateTime.now())
                .rechargeStatus(1)
                .remark(WalletDetailsType.PLATO_GO.getDesc())
                .recordNo(storeAddRequest.getPayOrderNo())
                .bankName(storeAddRequest.getPaymentName())
                .build();
        TicketsForm context1 = ticketsFormMerchantService.addTicKets(w).getContext();
        Assert.notNull(context1,"工单生成有异常，请重新充值！");
        log.info("平台充值鲸币开始===={},{}",storeAddRequest.getRechargeBalance(),storeAddRequest.getPayOrderNo());
        customerWalletMerchantService.addWallet(storeAddRequest.getRechargeBalance(),storeAddRequest.getPayOrderNo());
        return BaseResponse.success("充值成功");
    }

    @Override
    @Transactional
    public BaseResponse platRecharge(CustomerWalletOrderByRequest request) throws Exception {
        customerWalletMerchantService.platRecharge(request);
        return BaseResponse.SUCCESSFUL();
    }

    private StoreInfoResponse queryStoreInfo(StoreAddRequest request) {
        StoreInfoByIdRequest storeInfoByIdRequest = new StoreInfoByIdRequest();
        storeInfoByIdRequest.setStoreId(request.getStoreId());
        StoreInfoResponse context = storeQueryProvider.getStoreInfoById(storeInfoByIdRequest).getContext();
        Assert.notNull(context,"商家账号未找到！");
        return context;
    }

}
