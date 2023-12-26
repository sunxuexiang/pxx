package com.wanmi.sbc.wallet;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.wanmi.sbc.account.api.provider.wallet.*;
import com.wanmi.sbc.account.api.request.wallet.*;
import com.wanmi.sbc.account.api.response.wallet.*;
import com.wanmi.sbc.account.bean.enums.BudgetType;
import com.wanmi.sbc.account.bean.enums.TradeStateEnum;
import com.wanmi.sbc.account.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.account.bean.vo.CustomerWalletVO;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(description = "钱包api", tags = "CustomerWalletController")
@RestController
@Slf4j
@RequestMapping(value = "/wallet")
public class CustomerWalletController {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    WalletRecordProvider walletRecordProvider;

    @Autowired
    CustomerWalletProvider customerWalletProvider;

    @Autowired
    CustomerWalletQueryProvider customerWalletQueryProvider;

    @Autowired
    VirtualGoodsQueryProvider virtualGoodsQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private TicketsFormProvider ticketsFormProvider;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @ApiOperation(value = "查询用户账户余额")
    @RequestMapping(value = "/getBalanceByCustomerId", method = RequestMethod.GET)
    public BaseResponse<BalanceByCustomerIdResponse> getRecordStatus() {
        return BaseResponse.success(walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(commonUtil.getOperatorId()).build())
                .getContext());
    }


    @ApiOperation(value = "创建充值记录")
    @RequestMapping(value = "/addWalletRecord", method = RequestMethod.POST)
    public BaseResponse<AddWalletRecordResponse> addWalletRecord(@RequestBody AddWalletRecordRecordRequest request) {
        String customerId = commonUtil.getCustomer().getCustomerId();
        if (StringUtils.isEmpty(customerId) || Objects.isNull(request.getVirtualGoodsId())) {
            throw new SbcRuntimeException("k-000009");
        }
        //钱包表
        CustomerWalletVO customerWalletVO = customerWalletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(customerId).build()).getContext().getCustomerWalletVO();
        //查询商品表
        VirtualGoodsVO virtualGoods = virtualGoodsQueryProvider.getVirtualGoods(VirtualGoodsRequest.builder().goodsId(Long.valueOf(request.getVirtualGoodsId())).build()).getContext().getVirtualGoods();
        request.setTradeType(WalletRecordTradeType.RECHARGE);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(virtualGoods.getPrice());
        request.setCurrentBalance(customerWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.NOT_PAID);
        //客户账号
        String customerAccount = customerWalletVO.getCustomerAccount();
        request.setCustomerAccount(customerAccount);
        return walletRecordProvider.addWalletRecord(request);
    }

    @ApiOperation(value = "移动端提现详情页面")
    @RequestMapping(value = "/withdrawalDetails", method = RequestMethod.POST)
    public BaseResponse withdrawalDetails(@RequestBody WithdrawalDetailsRequest request) {
        String customerId = commonUtil.getCustomer().getCustomerId();
        request.setCustomerId(customerId);
        return walletRecordProvider.withdrawalDetails(request);
    }

    @ApiOperation(value = "移动端发起提现")
    @RequestMapping(value = "/initiateWithdrawal", method = RequestMethod.POST)
    public BaseResponse initiateWithdrawal(@RequestBody InitiateWithdrawalRequest request) {
        BaseResponse baseResponse = null;
        String customerId = commonUtil.getCustomer().getCustomerId();

        SbcRuntimeException exception = null;
        RLock rLock = redissonClient.getFairLock(customerId);
        rLock.lock();
        try {
            request.setCustomerId(customerId);
            //根据逻辑,先保存
            baseResponse = customerWalletProvider.initiateWithdrawal(request);
        } catch (SbcRuntimeException e) {
            exception = e;
        } finally {
            rLock.unlock();
        }
        if (exception != null) {
            throw new SbcRuntimeException(exception.getErrorCode(),exception.getResult());
        }
        return baseResponse;
    }

    //todo后面恢复
    @ApiOperation(value = "移动端发起取消提现")
    @RequestMapping(value = "/cancelWithdrawal", method = RequestMethod.POST)
    public BaseResponse cancelWithdrawal(@RequestBody CancelWithdrawalRequest request) {
        //根据逻辑,先保存
        String customerId = commonUtil.getCustomer().getCustomerId();
        request.setCustomerId(customerId);
        return customerWalletProvider.cancelWithdrawal(request);
    }

    @ApiOperation(value = "工单列表")
    @RequestMapping(value = "/ticketsFormAllList", method = RequestMethod.POST)
    public BaseResponse<TicketsFormQueryResponse> ticketsFormAllList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest) {
        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.ticketsFormAllList(ticketsFormPageRequest);
        return response;
    }

    @ApiOperation(value = "提现工单列表")
    @RequestMapping(value = "/withdrawalList", method = RequestMethod.POST)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public BaseResponse<TicketsFormQueryResponse> ticketsFormList(@RequestBody TicketsFormPageRequest ticketsFormPageRequest) {
//        ticketsFormPageRequest.setApplyType(2);
        BaseResponse<TicketsFormQueryResponse> response = ticketsFormProvider.withdrawalList(ticketsFormPageRequest);
        return response;
    }

    /**
     * 分页查询明细列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "分页查询所有记录,并按交易时间倒序排")
    @PostMapping(value = "/queryPageWalletRecord")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    public BaseResponse<WalletRecordResponse> queryPageWalletRecord(@RequestBody WalletRecordRequest request) {
        request.setCustomerAccount(commonUtil.getCustomer().getCustomerAccount());
        request.putSort("dealTime", SortType.DESC.toValue());
        return walletRecordProvider.queryPageWalletRecord(request);
    }

    @ApiOperation(value = "查询交易类型为提现的交易记录--分页")
    @PostMapping(value = "/queryWalletRecordByTradeType")
    public BaseResponse<WalletRecordResponse> queryPageWalletRecordByTradeType(@RequestBody WalletRecordRequest request) {
        request.setCustomerAccountId(commonUtil.getOperatorId());
        return walletRecordProvider.queryPageWalletRecordByTradeType(request);
    }

    @ApiOperation(value = "余额明细,查看接口,传id查看")
    @PostMapping(value = "/queryWalletRecordInfo")
    public BaseResponse<WalletRecordResponse> getWalletRecordByRecordNo(@RequestBody WalletRecordRequest request) {
        return walletRecordProvider.getWalletRecordByRecordNo(request);
    }

    @ApiOperation(value = "充值商品列表")
    @RequestMapping(value = "/virtualGoods", method = RequestMethod.POST)
    public BaseResponse<VirtualGoodsResponse> virtualGoods(@RequestBody VirtualGoodsRequest virtualGoodsRequest) {
        return walletRecordProvider.virtualGoods(virtualGoodsRequest);
    }


    @ApiOperation(value = "新增用户钱包")
    @PostMapping(value = "/addUserWallet")
    public BaseResponse<BalanceByCustomerIdResponse> addUserWallet() {
        String operatorId = commonUtil.getOperatorId();
        String account = commonUtil.getOperator().getAccount();
        WalletByWalletIdAddRequest wallet = new WalletByWalletIdAddRequest();
        wallet.setCustomerId(operatorId);
        wallet.setCustomerAccount(account);
        return customerWalletProvider.addUserWallet(wallet);
    }
}