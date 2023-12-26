package com.wanmi.sbc.wallet.provider.impl.wallet;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.wallet.api.response.wallet.AddWalletRecordResponse;
import com.wanmi.sbc.wallet.api.response.wallet.WalletStatusResponse;
import com.wanmi.sbc.wallet.bean.enums.JingBiState;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import com.wanmi.sbc.wallet.wallet.service.*;
import com.wanmi.sbc.walletorder.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.walletorder.bean.vo.TradeVO;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletTradePriceResponse;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.wallet.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.bean.vo.CheckGoodsInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@Validated
public class CustomerWalletModifyController implements CustomerWalletProvider {
    @Autowired
    private CustomerWalletRepository customerWalletRepository;

    //暂不添加
//    @Autowired
//    private TicketsFormProvider ticketsFormProvider;

    //属于account模块
//    @Autowired
//    private CustomerBankCardProvider customerBankCardProvider;

    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private CustomerWalletModifyService customerWalletModifyService;

    @Autowired
    private CustomerWalletService customerWalletService;

    @Autowired
    private CustomerWalletMerchantService customerWalletMerchantService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CustomerWalletTradeService customerWalletTradeService;

    @Autowired
    private CustomerWalletRefundService customerWalletRefundService;
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    /**
     * 修改钱包信息
     * -- 新增用户钱包请使用下面的方法
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse updateCustomerWalletByWalletId(CustomerWalletModifyRequest request) {
        CustomerWallet wallet = KsBeanUtil.convert(request.getCusWalletVO(), CustomerWallet.class);
        if (Objects.isNull(request.getCusWalletVO().getWalletId())) {
            return BaseResponse.error("修改钱包失败！");
        }
        customerWalletRepository.saveAndFlush(wallet);
        return BaseResponse.SUCCESSFUL();
    }


/*    @Override
    public BaseResponse initiateWithdrawal(InitiateWithdrawalRequest request) {
        return customerWalletModifyService.initiateWithdrawal(request);
    }*/

/*    @Override
    public BaseResponse initiateWithdrawalWhthoutCheck(InitiateWithdrawalWithoutCheckRequest request) {
        return customerWalletModifyService.initiateWithdrawalWithoutCheck(request);
    }*/

/*    @Override
    public BaseResponse cancelWithdrawal(CancelWithdrawalRequest request) {
        return customerWalletModifyService.cancelWithdrawal(request);
    }*/

    /**
     * 新增用户钱包
     *
     * @param request
     * @return
     */
    @Override
    @Transactional(noRollbackFor = Exception.class)
    @LcnTransaction
    public BaseResponse<BalanceByCustomerIdResponse> addUserWallet(WalletByWalletIdAddRequest request) {
        if (request.getMerchantFlag() && StringUtils.isEmpty(request.getStoreId())) {
            return BaseResponse.error("请先登录账号");
        }
        if (!request.getMerchantFlag() && StringUtils.isEmpty(request.getCustomerId())) {
            return BaseResponse.error("请先登陆账号！");
        }
        String queryId = request.getMerchantFlag()?request.getStoreId():request.getCustomerId();
        // 拿到用户的钱包信息
        WalletInfoRequest walletInfoRequest = new WalletInfoRequest();
        walletInfoRequest.setStoreFlag(request.getMerchantFlag());
        walletInfoRequest.setStoreId(queryId);
        CusWalletVO walletVO = queryCustomerWallet(walletInfoRequest).getContext();
        if (request.getMerchantFlag() && walletVO != null) {
            CustomerWallet wallet = new CustomerWallet();
            wallet.setIsEnable(request.getJingBiState().toValue() == 1?JingBiState.OPEN.toValue():JingBiState.NO.toValue());
            wallet.setStoreId(queryId);
            customerWalletMerchantService.updateUserWallet(wallet);
        }
        //判断用户钱包不为空，拿到用户的钱包并返回钱包信息
        if (walletVO != null) {
            BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
            walletVO.setJingBiState(walletVO.getIsEnable() == 1?JingBiState.OPEN:JingBiState.CLOSE);
            BeanUtils.copyProperties(walletVO, walletVO);
            response.setCusWalletVO(walletVO);
            return BaseResponse.success(response);
        }

        RLock rLock = redissonClient.getFairLock(queryId);
        rLock.lock();
        try {
            //用户ID不为空
            if ((Objects.nonNull(request.getCustomerId()) && !request.getMerchantFlag())
                    || (Objects.nonNull(request.getStoreId()) && request.getMerchantFlag())) {
                // 拿到用户的钱包信息
                CusWalletVO wallet = queryCustomerWallet(walletInfoRequest).getContext();
                //判断用户钱包不为空，拿到用户的钱包并返回钱包信息
                if (wallet != null) {
                    BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
                    response.setCusWalletVO(wallet);
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

    @Override
    public BaseResponse<CusWalletVO> queryCustomerWallet(WalletInfoRequest request) {
        CustomerWallet customerWallet = null;
        if (request.getStoreFlag()) {
            customerWallet = customerWalletMerchantService.getWalletByStoreId(request.getStoreId());
        } else {
            customerWallet = customerWalletService.getWalletByCustomerId(request.getStoreId());
        }
        CusWalletVO cusWalletVO = null;
        if (Objects.nonNull(customerWallet)) {
            cusWalletVO = new CusWalletVO();
            KsBeanUtil.copyProperties(customerWallet, cusWalletVO);
            return BaseResponse.success(cusWalletVO);
        }
        return BaseResponse.success(cusWalletVO);
    }

    @Override
    public BaseResponse<WalletRecordVO> orderByGiveStore(CustomerWalletOrderByRequest request) {
        return customerWalletMerchantService.orderByGiveStore(request);
    }

    /**
     * 新增钱包
     *
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public BaseResponse<BalanceByCustomerIdResponse> addCustomerWallet(WalletByWalletIdAddRequest request) {
        //查询商户信息
        if (request.getMerchantFlag()) {
            request.setCustomerId(request.getStoreId());
        }
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
        customerWallet.setWithDrawalBalance(BigDecimal.ZERO);
        customerWallet.setGiveBalanceState(DefaultFlag.NO.toValue());
        customerWallet.setIsEnable(request.getMerchantFlag()?1:0);
        customerWallet.setStoreId(request.getStoreId());
        CustomerWallet userWallet = customerWalletModifyService.addUserWallet(customerWallet);
        if (Objects.nonNull(userWallet.getWalletId())) {
            BalanceByCustomerIdResponse response = new BalanceByCustomerIdResponse();
            CusWalletVO cusWalletVO = new CusWalletVO();
            BeanUtils.copyProperties(customerWallet, cusWalletVO);
            response.setCusWalletVO(cusWalletVO);
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
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder()
                .customerId(customerWalletTradePriceRequest.getCustomerId()).build()).getContext().getCusWalletVO();
        if (cusWalletVO.getBalance().compareTo(customerWalletTradePriceRequest.getTotalPrice()) < 0 &&
                customerWalletTradePriceRequest.getTotalPrice().compareTo(BigDecimal.ONE) >= 0) {
            throw new SbcRuntimeException("K-050408");
        }
        //可用余额
        BigDecimal balance = cusWalletVO.getBalance();

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
     * 使用余额
     *
     * @param customerWalletUseRequest
     * @return
     */
    @Override
    public BaseResponse<List<TradeVO>> useWallet(CustomerWalletUseRequest customerWalletUseRequest) {
        List<TradeVO> trades = customerWalletTradeService.useWallet(customerWalletUseRequest);
        return BaseResponse.success(trades);
    }

    /**
     * 使用余额
     *
     * @param customerWalletUseRequest
     * @return
     */
    @Override
    public BaseResponse<List<NewPileTradeVO>> useWalletForNewPileTrade(CustomerWalletUseRequest customerWalletUseRequest) {
        List<NewPileTradeVO> trades = customerWalletTradeService.useWalletForNewPileTrade(customerWalletUseRequest);
        return BaseResponse.success(trades);
    }

    /**
     * 余额修改
     * - 订单生成余额扣除
     * - 订单取消, 自动取消, 余额增加
     *
     * @param customerWalletModifyWalletRequest
     * @return
     */
    @Override
    public BaseResponse<List<TradeVO>> modifyWalletBalance(CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest) {
        List<TradeVO> trades = customerWalletTradeService.modifyWalletBalance(customerWalletModifyWalletRequest);
        return BaseResponse.success(trades);
    }

    /**
     * 余额修改
     * - 订单生成余额扣除
     * - 订单取消, 自动取消, 余额增加
     *
     * @param customerWalletModifyWalletRequest
     * @return
     */
    @Override
    public BaseResponse<List<NewPileTradeVO>> modifyWalletBalanceForNewPileTrade(CustomerWalletModifyWalletRequest customerWalletModifyWalletRequest) {
        List<NewPileTradeVO> trades = customerWalletTradeService.modifyWalletBalanceForNewPileTrade(customerWalletModifyWalletRequest);
        return BaseResponse.success(trades);
    }

    @Override
    public void modifyWalletBalanceForRefund(CustomerWalletRefundRequest customerWalletRefundRequest) {
        customerWalletRefundService.modifyWalletBalanceForRefund(customerWalletRefundRequest);
    }

    @Override
    public BaseResponse<AddWalletRecordResponse> awardWalletBalance(CustomerWalletAwardRequest customerWalletAwardRequest) {
        BaseResponse<AddWalletRecordResponse> addWalletRecordResponseBaseResponse = customerWalletTradeService.addWalletBalance(customerWalletAwardRequest);
        return addWalletRecordResponseBaseResponse;
    }

    @Override
    public BaseResponse<WalletStatusResponse> queryWalletStatus(WalletByWalletIdAddRequest walletByWalletIdAddRequest) {

        return BaseResponse.success(customerWalletService.queryWalletStatus(walletByWalletIdAddRequest));
    }

    @Override
    @LcnTransaction
    public BaseResponse updateWalletDelById(WalletRequest request) {

        return customerWalletService.updateWalletDelById(request);
    }
}