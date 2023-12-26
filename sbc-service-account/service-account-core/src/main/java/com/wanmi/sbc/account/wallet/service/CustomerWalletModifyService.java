package com.wanmi.sbc.account.wallet.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.wallet.*;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.bean.enums.*;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.account.mq.MessageMqService;
import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.account.wallet.model.root.CustomerWallet;
import com.wanmi.sbc.account.wallet.model.root.TicketsForm;
import com.wanmi.sbc.account.wallet.model.root.WalletRecord;
import com.wanmi.sbc.account.wallet.repository.CustomerBindBankCardRepository;
import com.wanmi.sbc.account.wallet.repository.CustomerWalletRepository;
import com.wanmi.sbc.account.wallet.repository.TicketsFormRepository;
import com.wanmi.sbc.account.wallet.repository.WalletRecordRepository;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MessageMQRequest;
import com.wanmi.sbc.common.enums.NodeType;
import com.wanmi.sbc.common.enums.node.AccoutAssetsType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.loginregister.CustomerSiteProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.loginregister.CustomerCheckPayPasswordRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
public class CustomerWalletModifyService {
    @Autowired
    CustomerWalletRepository customerWalletRepository;

    @Autowired
    TicketsFormRepository ticketsFormRepository;

    @Autowired
    CustomerBindBankCardRepository customerBindBankCardRepository;

    @Autowired
    WalletRecordRepository walletRecordRepository;

    @Autowired
    GeneratorService generatorService;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerSiteProvider customerSiteProvider;

    @Autowired
    private MessageMqService messageMqService;

    /**
     * 临时逻辑：系统给用户发起提现请求
     * @param request
     * @return
     */
    @Transactional
    @LcnTransaction
    public BaseResponse initiateWithdrawalWithoutCheck(InitiateWithdrawalWithoutCheckRequest request) {

        String customerId = request.getCustomerId();

        //用户钱包
        CustomerWallet customerWalletVO = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByCustomerId(customerId)).orElse(null);

        // ***************** 代码校验 *******************
        //提现金额
        BigDecimal dealPrice = request.getDealPrice();
        //余额
        BigDecimal balance = customerWalletVO.getBalance();
        //充值金额
        BigDecimal rechargeBalance = customerWalletVO.getRechargeBalance();
        //赠送金额
        BigDecimal giveBalance = customerWalletVO.getGiveBalance();
        //提现金额不得大于余额,提现金额不得大于充值金额,若有赠送金额,提现金额不得大于充值金额减去赠送金额
        BigDecimal bigDecimal = new BigDecimal(0);
        //提现金额为0
        if (dealPrice.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "提现金额不能为0");
        }
        if (bigDecimal.compareTo(dealPrice) == 1) {
            throw new SbcRuntimeException("k-210005", "提现金额不能是负数");
        }
        if (balance.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "余额为0");
        }
        //提现金额大于余额
        if (dealPrice.compareTo(balance) == 1) {
            throw new SbcRuntimeException("k-210001", "提现金额大于鲸贴");
        }
        if (dealPrice.compareTo(BigDecimal.valueOf(10)) < 0) {
            throw new SbcRuntimeException("k-210006", "提现金额最低10元");
        }


        //客户账号
        String customerAccount = customerWalletVO.getCustomerAccount();
        //提现金额
        //当前时间
        LocalDateTime dateTime = LocalDateTime.now();
        //===================== 第一步 添加提现记录==================================================
        AddWalletRecordRecordRequest recordRecordRequest = new AddWalletRecordRecordRequest();
        recordRecordRequest.setCustomerId(customerWalletVO.getCustomerId());
        recordRecordRequest.setBudgetType(BudgetType.EXPENDITURE);
        recordRecordRequest.setCurrentBalance(customerWalletVO.getBalance().subtract(dealPrice));
        recordRecordRequest.setCustomerAccount(customerAccount);
        recordRecordRequest.setDealPrice(dealPrice);
        recordRecordRequest.setDealTime(dateTime);
        recordRecordRequest.setExtractType("银行卡");
        //交易类型提现
        recordRecordRequest.setTradeType(WalletRecordTradeType.WITHDRAWAL);
        //交易状态待支付
        recordRecordRequest.setTradeState(TradeStateEnum.NOT_PAID);
        recordRecordRequest.setTradeRemark(WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc());
        recordRecordRequest.setRemark(WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc());
        recordRecordRequest.setBalance(customerWalletVO.getBalance().subtract(dealPrice));
        WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
        convert.setRecordNo(generatorService.generate("W"));
        convert.setRelationOrderId(generatorService.generate("S"));
        WalletRecord walletRecord = walletRecordRepository.saveAndFlush(convert);
        //交易号码
        String recordNo = walletRecord.getRecordNo();

        //========================== 第二步 提交提现工单 =================================================
        TicketsFormModifyRequest modifyRequest = new TicketsFormModifyRequest();
        modifyRequest.setRecordNo(recordNo);
        modifyRequest.setWalletId(customerWalletVO.getWalletId());

        modifyRequest.setBankName(null);
        modifyRequest.setBankBranch(null);
        modifyRequest.setBankCode(null);
        modifyRequest.setApplyType(2);
        modifyRequest.setApplyPrice(dealPrice);
        modifyRequest.setApplyTime(dateTime);
        modifyRequest.setExtractStatus(1);
        modifyRequest.setRemark("客户:" + customerAccount + "提现 " + dealPrice + "元。");
        if(request.getAutoType()){
            modifyRequest.setCustomerName(customerWalletVO.getCustomerName() + "（自动）");
            modifyRequest.setAutoType(1);
        }else{
            modifyRequest.setCustomerName(customerWalletVO.getCustomerName());
            modifyRequest.setAutoType(0);
        }
        modifyRequest.setCustomerPhone(customerWalletVO.getCustomerAccount());
        TicketsForm ticketsForm = ticketsFormRepository.save(KsBeanUtil.convert(modifyRequest, TicketsForm.class));
        if (ticketsForm != null && ticketsForm.getFormId() != null) {
            modifyRequest.setFormId(ticketsForm.getFormId());
        }
        customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(dealPrice));
        customerWalletVO.setBlockBalance(customerWalletVO.getBlockBalance().add(dealPrice));
        customerWalletRepository.saveAndFlush(customerWalletVO);

        return BaseResponse.success(modifyRequest);
    }

    @Transactional
    @LcnTransaction
    public BaseResponse initiateWithdrawal(InitiateWithdrawalRequest request) {
        return this.withdrawal(request);
    }

    //发起提现
    public BaseResponse<TicketsFormModifyRequest> withdrawal(InitiateWithdrawalRequest request) {
        //用户钱包
        CustomerWallet customerWalletVO = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByCustomerId(request.getCustomerId())).orElse(null);
        //校验密码是否可用
        CustomerGetByIdResponse customerGetByIdResponse = customerQueryProvider.getCustomerById(new
                CustomerGetByIdRequest(customerWalletVO.getCustomerId())).getContext();
        if (StringUtils.isBlank(customerGetByIdResponse.getCustomerPayPassword())) {
            throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD, "未设置会员支付密码");
        }
        if (customerGetByIdResponse.getPayErrorTime() != null && customerGetByIdResponse.getPayErrorTime() == 3) {
            Duration duration = Duration.between(customerGetByIdResponse.getPayLockTime(), LocalDateTime.now());
            if (duration.toMinutes() < 30) {
                //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
//                throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_TIME_ERROR, new Object[]{30 - duration.toMinutes()});
                throw new SbcRuntimeException("K-888887", "连续输错密码3次，请" + (30 - duration.toMinutes()) + "分钟后重试！");
            }

        }

        //校验密码是否正确
        CustomerCheckPayPasswordRequest customerCheckPayPasswordRequest = new CustomerCheckPayPasswordRequest();
        customerCheckPayPasswordRequest.setPayPassword(request.getPayPassword());
        customerCheckPayPasswordRequest.setCustomerId(customerWalletVO.getCustomerId());
        customerSiteProvider.checkCustomerPayPwd(customerCheckPayPasswordRequest);


//        if (customerWalletVO.getBalance().compareTo(request.getDealPrice()) < 0) {
//            throw new SbcRuntimeException("K-050408");
//        }

        CustomerBindBankCard bankCardResponse = new CustomerBindBankCard();
        if (StringUtils.isNotEmpty(request.getBankCode())) {
            bankCardResponse = customerBindBankCardRepository.findOneByBankCode(request.getBankCode());
            //获取用户银行卡
//        Long walletId = customerBindBankCardRepository.findOneByBankCode(bankCardResponse.getBankCode()).getWalletId();
            log.info("=================钱包ID验证：{}, 查询出来的:{},传递的ID：{},银行卡ID：{}", request.getBankCode(), bankCardResponse.getBankCode(),
                    customerWalletVO.getWalletId(), bankCardResponse.getWalletId().longValue());
            if (customerWalletVO.getWalletId().longValue() != bankCardResponse.getWalletId().longValue()) {
                throw new SbcRuntimeException("k-210008", "银行卡信息校验错误");
            }
        }

        this.checkWithdrawal(request, customerWalletVO);
        //客户账号
        String customerAccount = customerWalletVO.getCustomerAccount();
        //银行卡号
        String bankCode = bankCardResponse.getBankCode();
        //开户支行
        String bankBranch = bankCardResponse.getBankBranch();
        //开户行
        String bankName = bankCardResponse.getBankName();
        //持卡人姓名
        String cardHolder = bankCardResponse.getCardHolder();
        //提现金额
        BigDecimal dealPrice = request.getDealPrice();
        //当前时间
        LocalDateTime dateTime = LocalDateTime.now();
//        //赠送金额
//        BigDecimal giveBalance = customerWalletVO.getGiveBalance();
        //===================== 第一步 添加提现记录==================================================
        AddWalletRecordRecordRequest recordRecordRequest = new AddWalletRecordRecordRequest();
        recordRecordRequest.setCustomerId(customerWalletVO.getCustomerId());
        recordRecordRequest.setBudgetType(BudgetType.EXPENDITURE);
        recordRecordRequest.setCurrentBalance(customerWalletVO.getBalance().subtract(dealPrice));
        recordRecordRequest.setCustomerAccount(customerAccount);
        recordRecordRequest.setDealPrice(dealPrice);
        recordRecordRequest.setDealTime(dateTime);
        recordRecordRequest.setExtractType("银行卡");
        //交易类型提现
        recordRecordRequest.setTradeType(WalletRecordTradeType.WITHDRAWAL);
        //交易状态待支付
        recordRecordRequest.setTradeState(TradeStateEnum.NOT_PAID);
//        //记录此次提现冻结的赠送金额
//        if (Objects.nonNull(giveBalance)) {
//            //冻结赠送金额
//            recordRecordRequest.setBlockGiveBalance(giveBalance);
//        }
//        recordRecordRequest.setTradeRemark("余额提现-" + bankName + "(" + (bankCode == null ? "" : new StringBuilder(bankCode).substring(bankCode.length() - 4)) + ")");
//        recordRecordRequest.setRemark("余额提现-" + bankName + "(" + (bankCode == null ? "" : new StringBuilder(bankCode).substring(bankCode.length() - 4)) + ")");
        recordRecordRequest.setTradeRemark(WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc());
        recordRecordRequest.setRemark(WalletDetailsType.DEDUCTION_WITHDRAW_AUDIT.getDesc());
        recordRecordRequest.setBalance(customerWalletVO.getBalance().subtract(dealPrice));
        WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
        convert.setRecordNo(generatorService.generate("W"));
        convert.setRelationOrderId(generatorService.generate("S"));
        WalletRecord walletRecord = walletRecordRepository.saveAndFlush(convert);
        //交易号码
        //String recordNo = walletRecord.getRelationOrderId();
        String recordNo = walletRecord.getRecordNo();

        //========================== 第二步 提交提现工单 =================================================
        TicketsFormModifyRequest modifyRequest = new TicketsFormModifyRequest();
        modifyRequest.setRecordNo(recordNo);
        modifyRequest.setWalletId(customerWalletVO.getWalletId());
        modifyRequest.setBankName(bankName);
        modifyRequest.setBankBranch(bankBranch);
        modifyRequest.setBankCode(bankCode);
        modifyRequest.setApplyType(2);
        modifyRequest.setApplyPrice(dealPrice);
        modifyRequest.setApplyTime(dateTime);
        modifyRequest.setExtractStatus(1);
        modifyRequest.setRemark("客户:" + customerAccount + "提现 " + dealPrice + "元。");
        modifyRequest.setCustomerName(request.getCustomerName());
        modifyRequest.setCustomerPhone(request.getCustomerPhone());
        modifyRequest.setAutoType(0);
        TicketsForm ticketsForm = ticketsFormRepository.save(KsBeanUtil.convert(modifyRequest, TicketsForm.class));
        if (ticketsForm != null && ticketsForm.getFormId() != null) {
            modifyRequest.setFormId(ticketsForm.getFormId());
        }
        //========================== 第三步 修改钱包表,冻结提现金额,若有赠送金额,冻结赠送金额 ======================================
        //余额
//        BigDecimal balance = customerWalletVO.getBalance();
//        if (Objects.isNull(balance)) {
//            balance = new BigDecimal(0);
//        }
//        //冻结余额
//        BigDecimal blockBalance = customerWalletVO.getBlockBalance();
//        if (Objects.isNull(blockBalance)) {
//            blockBalance = new BigDecimal(0);
//        }
        //用户钱包
//        BigDecimal finalBalance;
        //可用余额永远等于赠送金额+充值金额
        //可用余额减去提现金额
//        BigDecimal subtract = balance.subtract(dealPrice);
        //如果赠送金额不为空,因为提现需要冻结赠送金额,因此可用余额再减去赠送金额
//        if (Objects.nonNull(giveBalance)) {
//            finalBalance = subtract.subtract(giveBalance);
//        } else {
//            finalBalance = subtract;
//        }
//        //赠送金额全部减掉
//        customerWallet.setGiveBalance(new BigDecimal(0.0));
//        //充值金额减去提现金额
//        customerWallet.setRechargeBalance(customerWallet.getRechargeBalance().subtract(dealPrice));
//        //最终余额finalBalance
//        customerWallet.setBalance(subtract);
//        //冻结余额加上提现金额
////        customerWallet.setBlockBalance(blockBalance.add(dealPrice));
//        //冻结赠送金额
//        customerWallet.setGiveBalanceState(1);
//        customerWalletRepository.save(customerWallet);
        //非法闯入,支付和提现同时进行,可能会出现并发的问题
        customerWalletVO.setBalance(customerWalletVO.getBalance().subtract(dealPrice));
        customerWalletVO.setBlockBalance(customerWalletVO.getBlockBalance().add(dealPrice));
        customerWalletRepository.saveAndFlush(customerWalletVO);
        //customerWalletRepository.balancePay(customerWalletVO.getCustomerId(), dealPrice);

        return BaseResponse.success(modifyRequest);
    }

    private void checkWithdrawal(InitiateWithdrawalRequest request, CustomerWallet customerWallet) {
        //提现金额
        BigDecimal dealPrice = request.getDealPrice();
        //余额
        BigDecimal balance = customerWallet.getBalance();
        //充值金额
        BigDecimal rechargeBalance = customerWallet.getRechargeBalance();
        //赠送金额
        BigDecimal giveBalance = customerWallet.getGiveBalance();
        //提现金额不得大于余额,提现金额不得大于充值金额,若有赠送金额,提现金额不得大于充值金额减去赠送金额
        BigDecimal bigDecimal = new BigDecimal(0);
        //提现金额为0
        if (dealPrice.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "提现金额不能为0");
        }
        if (bigDecimal.compareTo(dealPrice) == 1) {
            throw new SbcRuntimeException("k-210005", "提现金额不能是负数");
        }
        if (balance.compareTo(bigDecimal) == 0) {
            throw new SbcRuntimeException("k-210005", "余额为0");
        }
        //提现金额大于余额
        if (dealPrice.compareTo(balance) == 1) {
            throw new SbcRuntimeException("k-210001", "提现金额大于鲸贴");
        }
        if (dealPrice.compareTo(BigDecimal.valueOf(10)) < 0) {
            throw new SbcRuntimeException("k-210006", "提现金额最低10元");
        }
        //提现金额大于充值金额
//        if (dealPrice.compareTo(rechargeBalance) == 1) {
//            throw new SbcRuntimeException("k-210002", "提现金额大于充值金额");
//        }
        //提现金额大余额减去赠送金额
//        if (dealPrice.compareTo(balance.subtract(giveBalance)) == 1) {
//            throw new SbcRuntimeException("k-210004", "提现金额大余额减去赠送金额");
//        }
    }

    @Transactional
    @LcnTransaction
    public BaseResponse cancelWithdrawal(CancelWithdrawalRequest request) {
        return this.cancel(request);
    }


    //发起提现
    public BaseResponse cancel(CancelWithdrawalRequest request) {

        TicketsForm form = ticketsFormRepository.getByFromId(request.getFormId());
        if (form == null) {
            throw new SbcRuntimeException("k-210009", "申请工单不存在");
        }

        //提现申请单状态【1待审核，2已审核，3已打款，4已拒绝，5打款失败，6，用户撤回】
        if (form.getExtractStatus().intValue() != 1) {
            throw new SbcRuntimeException("k-210009", "当前状态不允许撤销申请工单");
        }

        //用户钱包
        CustomerWallet customerWalletVO = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByCustomerId(request.getCustomerId())).orElse(null);
        //校验密码是否可用
        CustomerGetByIdResponse customerGetByIdResponse = customerQueryProvider.getCustomerById(new
                CustomerGetByIdRequest(customerWalletVO.getCustomerId())).getContext();
        if (StringUtils.isBlank(customerGetByIdResponse.getCustomerPayPassword())) {
            throw new SbcRuntimeException(CustomerErrorCode.NO_CUSTOMER_PAY_PASSWORD);
        }
        if (customerGetByIdResponse.getPayErrorTime() != null && customerGetByIdResponse.getPayErrorTime() == 3) {
            Duration duration = Duration.between(customerGetByIdResponse.getPayLockTime(), LocalDateTime.now());
            if (duration.toMinutes() < 30) {
                //支付密码输错三次，并且锁定时间还未超过30分钟，返回账户冻结错误信息
                throw new SbcRuntimeException(CustomerErrorCode.CUSTOMER_PAY_LOCK_TIME_ERROR, new Object[]{30 - duration.toMinutes()});
            }
        }

        //校验密码是否正确
        CustomerCheckPayPasswordRequest customerCheckPayPasswordRequest = new CustomerCheckPayPasswordRequest();
        customerCheckPayPasswordRequest.setPayPassword(request.getPayPassword());
        customerCheckPayPasswordRequest.setCustomerId(customerWalletVO.getCustomerId());
        customerSiteProvider.checkCustomerPayPwd(customerCheckPayPasswordRequest);


        //===================== 第一步 添加撤销记录==================================================
        AddWalletRecordRecordRequest recordRecordRequest = new AddWalletRecordRecordRequest();
        recordRecordRequest.setCustomerId(customerWalletVO.getCustomerId());
        recordRecordRequest.setBudgetType(BudgetType.INCOME);
        recordRecordRequest.setCurrentBalance(customerWalletVO.getBalance());
        recordRecordRequest.setCustomerAccount(customerWalletVO.getCustomerAccount());
        recordRecordRequest.setDealPrice(form.getApplyPrice());
        recordRecordRequest.setDealTime(LocalDateTime.now());
        recordRecordRequest.setExtractType("撤销申请");
        //交易类型提现
        recordRecordRequest.setTradeType(WalletRecordTradeType.BALANCE_CANCEL);
        //交易状态待支付
        recordRecordRequest.setTradeState(TradeStateEnum.NOT_PAID);
        recordRecordRequest.setTradeRemark(WalletDetailsType.INCREASE_WITHDRAW_CANCEL.getDesc() + "-" + form.getRecordNo());
        recordRecordRequest.setRemark(WalletDetailsType.INCREASE_WITHDRAW_CANCEL.getDesc() + "-" + form.getRecordNo());
        recordRecordRequest.setBalance(customerWalletVO.getBalance().add(form.getApplyPrice()));
        WalletRecord convert = KsBeanUtil.convert(recordRecordRequest, WalletRecord.class);
        convert.setRecordNo(generatorService.generate("W"));
        convert.setRelationOrderId(form.getRecordNo());
        walletRecordRepository.saveAndFlush(convert);
        form.setExtractStatus(6);
        form.setAuditTime(LocalDateTime.now());
        ticketsFormRepository.saveAndFlush(form);
        customerWalletRepository.addAmountReduceBlock(customerWalletVO.getCustomerId(), form.getApplyPrice());
        List<String> params = Lists.newArrayList(String.valueOf(form.getApplyPrice()));
        this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_WITHDRAW_CANCEL, params, null,
                customerWalletVO.getCustomerId(), customerWalletVO.getCustomerAccount());

        TicketsFormQueryVO formQueryVO = new TicketsFormQueryVO();
        BeanUtils.copyProperties(form, formQueryVO);
        return BaseResponse.success(formQueryVO);
    }

    /**
     * 发送消息
     *
     * @param nodeType
     * @param nodeCode
     * @param params
     * @param routeParam
     * @param customerId
     */
    private void sendMessage(NodeType nodeType, AccoutAssetsType nodeCode, List<String> params, String routeParam, String customerId, String mobile) {

        Map<String, Object> map = new HashMap<>();
        map.put("type", nodeType.toValue());
        map.put("node", nodeCode.toValue());
        map.put("id", routeParam);

        MessageMQRequest messageMQRequest = new MessageMQRequest();
        messageMQRequest.setNodeCode(nodeCode.getType());
        messageMQRequest.setNodeType(nodeType.toValue());
        messageMQRequest.setParams(params);
        messageMQRequest.setRouteParam(map);
        messageMQRequest.setCustomerId(customerId);
        messageMQRequest.setMobile(mobile);
        messageMqService.sendMessage(messageMQRequest);
    }


    /**
     * 新增用户钱包信息
     */
    public CustomerWallet addUserWallet(CustomerWallet request) {
        return customerWalletRepository.save(request);
    }

    /**
     * 使用余额，更新余额，可提现金额，支出金额，支出数
     *
     * @param customerFundsId
     * @param expenseAmount
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public int balancePay(String customerFundsId, BigDecimal expenseAmount, String customerAccount) {
        List<String> params = Lists.newArrayList(String.valueOf(expenseAmount));
        this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_WITHDRAW_PAY, params, null,
                customerFundsId, customerAccount);
        return customerWalletRepository.balancePay(customerFundsId, expenseAmount);
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public int balancePayOrder(String storeId, BigDecimal expenseAmount) {
        return customerWalletRepository.balancePayOrder(storeId, expenseAmount);
    }

    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public int balancePayOne(String storeId, BigDecimal expenseAmount) {
        return customerWalletRepository.balancePayOne(Long.valueOf(storeId), expenseAmount);
    }

    /**
     * @param customerFundsId
     * @param expenseAmount
     * @return
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public int addAmount(String customerFundsId, BigDecimal expenseAmount, String customerAccount) {
        return customerWalletRepository.addAmount(customerFundsId, expenseAmount);
    }

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 余额支付订单退款增加账户余额金额
     * @Date 14:54 2019/7/16
     * @Param [customerFundsAddAmount]
     **/
    @Transactional
    @LcnTransaction
    public void addAmount(CustomerAddAmountRequest customerAddAmountRequest) {
        List<String> params = Lists.newArrayList(String.valueOf(customerAddAmountRequest.getAmount()));
        this.sendMessage(NodeType.ACCOUNT_ASSETS, AccoutAssetsType.BALANCE_WITHDRAW_TUI, params, null,
                customerAddAmountRequest.getCustomerId(), customerAddAmountRequest.getCustomerAccount());
        customerWalletRepository.addAmount(customerAddAmountRequest.getCustomerId(), customerAddAmountRequest.getAmount());
    }

    @Autowired
    private WalletRecordService walletRecordService;
    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Transactional
    @LcnTransaction
    public void modifyWalletBalanceForRefundV2(ModifyWalletBalanceForRefundRequest modifyRequest) {
        //用户余额信息
        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(modifyRequest.getBuyerId()).build())
                .getContext().getCustomerWalletVO();
        boolean isExpenditure =BudgetType.EXPENDITURE==modifyRequest.getBudgetType();

        //商户钱包信息
        CustomerWallet storeWallet = customerWalletRepository.findOne(CustomerWalletQueryBuilder.queryByStoreId(modifyRequest.getStoreId())).orElse(null);

        if(isExpenditure) {
            if (modifyRequest.getAmount().compareTo(customerWalletVO.getBalance()) == 1) {
                throw new SbcRuntimeException("k-230004", "鲸币余额不足本次支出");
            }
        }
        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();

        String remark = modifyRequest.getWalletDetailsType().getDesc() + "-" + modifyRequest.getRelationId();
        String tradeRemark = remark;
        if (StringUtils.isNotBlank(modifyRequest.getTradeRemark())) {
            tradeRemark += "-" + modifyRequest.getTradeRemark();
        }
        if (StringUtils.isNotBlank(modifyRequest.getRemark())) {
            remark += "-" + modifyRequest.getRemark();
        }
        request.setTradeRemark(tradeRemark);
        request.setRemark(remark);

        //客户账号
        request.setCustomerAccount(customerWalletVO.getCustomerAccount());
        request.setRelationOrderId(modifyRequest.getRelationId());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(modifyRequest.getBudgetType());
        request.setDealPrice(modifyRequest.getAmount());
        request.setCurrentBalance(customerWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
        if (isExpenditure) {
            request.setBalance(customerWalletVO.getBalance().subtract(modifyRequest.getAmount()));
        } else {
            request.setBalance(modifyRequest.getAmount().add(customerWalletVO.getBalance()));
        }
        walletRecordService.addWalletRecord(request);
        //添加商户记录
        if (isExpenditure){
            this.balancePay(modifyRequest.getBuyerId(),modifyRequest.getAmount(),modifyRequest.getCustomerAccount());
            this.balancePayOrder(modifyRequest.getStoreId(),modifyRequest.getAmount());
            request.setCustomerAccount(storeWallet.getCustomerAccount());
            request.setBalance(storeWallet.getBalance().add(customerWalletVO.getBalance()));
            request.setBudgetType(BudgetType.INCOME);
            walletRecordService.addWalletRecord(request);
        }else {
            this.addAmount(CustomerAddAmountRequest.builder()
                    .amount(modifyRequest.getAmount())
                    .customerId(modifyRequest.getBuyerId())
                    .businessId(modifyRequest.getRelationId())
                    .customerAccount(modifyRequest.getCustomerAccount())
                    .build());
            this.balancePayOne(modifyRequest.getStoreId(),modifyRequest.getAmount());
            request.setCustomerAccount(storeWallet.getCustomerAccount());
            request.setBudgetType(BudgetType.EXPENDITURE);
            request.setBalance(storeWallet.getBalance().subtract(modifyRequest.getAmount()));
            walletRecordService.addWalletRecord(request);
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void modifyWalletBalanceForCoin(ModifyWalletBalanceForCoinActivityRequest request) {
        //用户余额信息
        CustomerWalletVO customerWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(request.getBuyerId()).build())
                .getContext().getCustomerWalletVO();

        boolean isExpenditure = BudgetType.EXPENDITURE == request.getBudgetType();
        if(isExpenditure) {
            if (request.getAmount().compareTo(customerWalletVO.getBalance()) == 1) {
                throw new SbcRuntimeException("k-230004", "鲸币余额不足本次支出");
            }
        }
        AddWalletRecordRecordRequest recordRequest = new AddWalletRecordRecordRequest();

        String remark = request.getWalletDetailsType().getDesc() + "-" + request.getRelationId();

        recordRequest.setTradeRemark(remark);
        recordRequest.setRemark(remark);

        //客户账号
        recordRequest.setCustomerAccount(customerWalletVO.getCustomerAccount());
        recordRequest.setRelationOrderId(request.getRelationId());
        recordRequest.setTradeType(WalletRecordTradeType.ORDER_CASH_BACK);
        recordRequest.setBudgetType(request.getBudgetType());
        recordRequest.setDealPrice(request.getAmount());
        recordRequest.setCurrentBalance(customerWalletVO.getBalance());
        recordRequest.setTradeState(TradeStateEnum.PAID);
        recordRequest.setPayType(1);
        if (isExpenditure) {
            recordRequest.setBalance(customerWalletVO.getBalance().subtract(request.getAmount()));
        } else {
            recordRequest.setBalance(request.getAmount().add(customerWalletVO.getBalance()));
        }
        walletRecordService.addWalletRecord(recordRequest);
        if (isExpenditure){
            this.balancePay(request.getBuyerId(),request.getAmount(),request.getCustomerAccount());
        }else {
            this.addAmount(CustomerAddAmountRequest.builder()
                    .amount(request.getAmount())
                    .customerId(request.getBuyerId())
                    .businessId(request.getRelationId())
                    .customerAccount(request.getCustomerAccount())
                    .build());
        }
    }
}
