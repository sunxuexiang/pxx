package com.wanmi.sbc.account.provider.impl.wallet;

import com.wanmi.sbc.account.api.provider.wallet.*;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.account.api.response.wallet.CustomerWalletTradePriceResponse;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.account.wallet.service.CustomerWalletModifyService;
import com.wanmi.sbc.account.wallet.service.CustomerWalletService;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Validated
@RefreshScope
@Slf4j
public class CustomerWalletModifyController implements CustomerWalletProvider {
    @Autowired
    CustomerWalletRepository customerWalletRepository;

    @Autowired
    TicketsFormProvider ticketsFormProvider;

    @Autowired
    CustomerBankCardProvider customerBankCardProvider;

    @Autowired
    CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    WalletRecordProvider walletRecordProvider;

    @Autowired
    CustomerWalletProvider customerWalletProvider;

    @Autowired
    CustomerWalletModifyService customerWalletModifyService;

    @Autowired
    CustomerWalletService customerWalletService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Value("${wallet.authWithdrawAmount:100.00}")
    private Double authWithdrawAmount;

    /**
     * 修改钱包信息
     * -- 新增用户钱包请使用下面的方法
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateCustomerWalletByWalletId(CustomerWalletModifyRequest request) {
        CustomerWallet wallet = KsBeanUtil.convert(request.getCustomerWalletVO(), CustomerWallet.class);
        if (Objects.isNull(request.getCustomerWalletVO().getWalletId())) {
            return BaseResponse.error("修改钱包失败！");
        }
        customerWalletRepository.saveAndFlush(wallet);
        return BaseResponse.SUCCESSFUL();
    }


    @Override
    public BaseResponse initiateWithdrawal(InitiateWithdrawalRequest request) {
        return customerWalletModifyService.initiateWithdrawal(request);
    }

    @Override
    public BaseResponse initiateWithdrawalWhthoutCheck(InitiateWithdrawalWithoutCheckRequest request) {
        return customerWalletModifyService.initiateWithdrawalWithoutCheck(request);
    }

    @Override
    public BaseResponse cancelWithdrawal(CancelWithdrawalRequest request) {
        return customerWalletModifyService.cancelWithdrawal(request);
    }

    /**
     * 新增用户钱包
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<BalanceByCustomerIdResponse> addUserWallet(WalletByWalletIdAddRequest request) {
        if (StringUtils.isEmpty(request.getCustomerId())) {
            return BaseResponse.error("请先登陆账号！");
        }
        // 拿到用户的钱包信息
        CustomerWallet walletTem = customerWalletService.getWalletByCustomerId(request.getCustomerId());
        //判断用户钱包不为空，拿到用户的钱包并返回钱包信息
        if (Objects.nonNull(walletTem)) {
            BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
            CustomerWalletVO customerWalletVO = new CustomerWalletVO();
            BeanUtils.copyProperties(walletTem, customerWalletVO);
            response.setCustomerWalletVO(customerWalletVO);
            return BaseResponse.success(response);
        }

        RLock rLock = redissonClient.getFairLock(request.getCustomerId());
        rLock.lock();
        try {
            //用户ID不为空
            if (Objects.nonNull(request.getCustomerId())) {
                // 拿到用户的钱包信息
                CustomerWallet wallet = customerWalletService.getWalletByCustomerId(request.getCustomerId());
                //判断用户钱包不为空，拿到用户的钱包并返回钱包信息
                if (Objects.nonNull(wallet)) {
                    BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
                    CustomerWalletVO customerWalletVO = new CustomerWalletVO();
                    BeanUtils.copyProperties(wallet, customerWalletVO);
                    response.setCustomerWalletVO(customerWalletVO);
                    return BaseResponse.success(response);
                } else {
                    return addCustomerWallet(request);
                }
            } else {
                return BaseResponse.error("请先登陆账号！");
            }
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 新增钱包
     *
     * @param request
     * @return
     */
    private BaseResponse<BalanceByCustomerIdResponse> addCustomerWallet(WalletByWalletIdAddRequest request) {
        //用户钱包信息为空，给用户新增一条钱包信息
        CustomerWallet customerWallet = new CustomerWallet();
        customerWallet.setCustomerId(request.getCustomerId());
        customerWallet.setCustomerAccount(request.getCustomerAccount());
        customerWallet.setCustomerName(StringUtils.isEmpty(request.getCustomerName()) ? request.getCustomerAccount() : request.getCustomerName());
        customerWallet.setBalance(BigDecimal.ZERO);
        customerWallet.setRechargeBalance(BigDecimal.ZERO);
        customerWallet.setGiveBalance(BigDecimal.ZERO);
        customerWallet.setBlockBalance(BigDecimal.ZERO);
        customerWallet.setCustomerStatus(DefaultFlag.NO);
        customerWallet.setCreateTime(LocalDateTime.now());
        customerWallet.setCreateId(request.getCustomerId());
        customerWallet.setUpdateTime(LocalDateTime.now());
        customerWallet.setUpdateId(request.getCustomerId());
        customerWallet.setDelFlag(DefaultFlag.NO);
        customerWallet.setGiveBalanceState(DefaultFlag.NO.toValue());
        CustomerWallet userWallet = customerWalletModifyService.addUserWallet(customerWallet);
        if (Objects.nonNull(userWallet.getWalletId())) {
            BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
            CustomerWalletVO customerWalletVO = new CustomerWalletVO();
            BeanUtils.copyProperties(customerWallet, customerWalletVO);
            response.setCustomerWalletVO(customerWalletVO);
            return BaseResponse.success(response);
        } else {
            return BaseResponse.error("插入失败");
        }
    }

    /**
     * 用户支付, 使用余额
     *
     * @param customerWalletTradePriceRequest
     * @return
     */
    @Override
    public BaseResponse<CustomerWalletTradePriceResponse> useCustomerWallet(CustomerWalletTradePriceRequest customerWalletTradePriceRequest) {
        //仅计算不扣减, 生成订单时扣减余额
        if (!customerWalletTradePriceRequest.getUseWallet()) {
            return null;
        }
        //校验
        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder()
                .customerId(customerWalletTradePriceRequest.getCustomerId()).build()).getContext().getCustomerWalletVO();
        if (customerWalletVO.getBalance().compareTo(customerWalletTradePriceRequest.getTotalPrice()) < 0 &&
                customerWalletTradePriceRequest.getTotalPrice().compareTo(BigDecimal.ONE) >= 0) {
            throw new SbcRuntimeException("K-050408");
        }
        //可用余额
        BigDecimal balance = customerWalletVO.getBalance();

        //均摊商品价格
        List<CheckGoodsInfoVO> checkGoodsInfos = customerWalletTradePriceRequest.getCheckGoodsInfos();
        //商品总价
        BigDecimal totalPrice = customerWalletTradePriceRequest.getTotalPrice();

        //使用余额 新-商品总价
        BigDecimal newTotalPrice = customerWalletTradePriceRequest.getIsDeliveryPrice()
                ? totalPrice.compareTo(balance) > 0 ? totalPrice.subtract(balance) : BigDecimal.ZERO
                : totalPrice.subtract(BigDecimal.ONE).compareTo(balance) > 0 ? totalPrice.subtract(balance) : BigDecimal.ONE;

        //此次扣减的余额
        BigDecimal deductionTotal = totalPrice.subtract(newTotalPrice);

        //计算商品均摊价
        this.calcSplitPrice(checkGoodsInfos, newTotalPrice, totalPrice);

        CustomerWalletTradePriceResponse couponCheckoutResponseTemplate = new CustomerWalletTradePriceResponse();
        couponCheckoutResponseTemplate.setTotalPrice(newTotalPrice);
        couponCheckoutResponseTemplate.setWalletTotalPrice(deductionTotal);
        couponCheckoutResponseTemplate.setCheckGoodsInfos(checkGoodsInfos);
        return BaseResponse.success(couponCheckoutResponseTemplate);
    }

    /**
     * 计算商品均摊价
     *
     * @param checkGoodsInfos 待计算的商品列表
     * @param newTotal        新的总价
     * @param total           旧的商品总价
     */
    public void calcSplitPrice(List<CheckGoodsInfoVO> checkGoodsInfos, BigDecimal newTotal, BigDecimal total) {
        //内部总价为零或相等不用修改
        if (total.equals(newTotal)) {
            return;
        }
        //均摊价总价
        BigDecimal reduceSplitPrice = checkGoodsInfos.stream().map(CheckGoodsInfoVO::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (reduceSplitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        //此次扣减的余额
        BigDecimal deductionTotal = total.subtract(newTotal);

        for (CheckGoodsInfoVO checkGoodsInfo : checkGoodsInfos) {
            BigDecimal divide = checkGoodsInfo.getSplitPrice().divide(reduceSplitPrice, 10, BigDecimal.ROUND_DOWN);
            checkGoodsInfo.setSplitPrice(
                    checkGoodsInfo.getSplitPrice().subtract(divide.multiply(deductionTotal).setScale(2, BigDecimal.ROUND_HALF_UP))
            );
        }
    }

    /**
     * 使用余额付款，更新资金信息
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse balancePay(WalletRequest request) {
        int result = customerWalletModifyService.balancePay(request.getCustomerId(), request.getExpenseAmount(), request.getCustomerAccount());
        return result > 0 ? BaseResponse.SUCCESSFUL() : BaseResponse.FAILED();
    }

    /**
     * 使用余额付款，更新资金信息
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse addAmount(WalletRequest request) {
        int result = customerWalletModifyService.addAmount(request.getCustomerId(), request.getExpenseAmount(), request.getCustomerAccount());
        return result > 0 ? BaseResponse.SUCCESSFUL() : BaseResponse.FAILED();
    }

    /**
     * @return com.wanmi.sbc.common.base.BaseResponse
     * @Author lvzhenwei
     * @Description 余额支付订单退款增加账户余额金额
     * @Date 11:16 2019/7/24
     * @Param [request]
     **/
    @Override
    public BaseResponse addAmount(CustomerAddAmountRequest request) {
        customerWalletModifyService.addAmount(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 退款到余额
     *
     * @param modifyRequest
     */
    @Override
    public BaseResponse modifyWalletBalanceForRefundV2(ModifyWalletBalanceForRefundRequest modifyRequest) {
        customerWalletModifyService.modifyWalletBalanceForRefundV2(modifyRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse aotuWalletWithdrawalBalance() {
        List<CustomerWalletVO> customerWalletVOS = customerWalletQueryProvider.queryAutoWalletWithdrawaAccountBalance().getContext();

        log.info("自动提现钱包余额门槛：{}", authWithdrawAmount);
        customerWalletVOS = customerWalletVOS.stream().filter(o -> !(o.getBalance().compareTo(BigDecimal.valueOf(authWithdrawAmount)) < 0)).collect(Collectors.toList());

        for (CustomerWalletVO vo: customerWalletVOS) {
            /*if(vo.getBalance().compareTo(new BigDecimal(10)) == -1){
                continue;
            }*/
            try{
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                InitiateWithdrawalWithoutCheckRequest request = InitiateWithdrawalWithoutCheckRequest
                                        .builder().customerId(vo.getCustomerId()).customerPhone(vo.getContactPhone()).dealPrice(vo.getBalance()).autoType(true).build();
                                initiateWithdrawalWhthoutCheck(request);
                            }
                        }
                ).start();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyWalletBalanceForCoin(ModifyWalletBalanceForCoinActivityRequest request) {
        customerWalletModifyService.modifyWalletBalanceForCoin(request);
        return BaseResponse.SUCCESSFUL();
    }


}