package com.wanmi.sbc.pay;

import com.alipay.api.internal.util.StringUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AccountType;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.NoDeleteCustomerGetByAccountRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByAccountNameRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByCompanyInfoIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByAccountNameResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByCompanyInfoIdResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.manualrefund.RefundForClaimsRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderByReturnOrderNoRequest;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderOnlineByWalletRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.response.refund.RefundOrderByReturnOrderNoResponse;
import com.wanmi.sbc.order.bean.enums.ClaimsApplyType;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.pay.api.provider.AliPayProvider;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.provider.WxPayProvider;
import com.wanmi.sbc.pay.api.request.CcbPayOrderRequest;
import com.wanmi.sbc.pay.api.request.CcbSubPayOrderRequest;
import com.wanmi.sbc.pay.api.request.WxPayForNativeRequest;
import com.wanmi.sbc.pay.api.response.WxPayForNativeResponse;
//import com.wanmi.sbc.pay.bean.enums.CcbSubOrderType;
import com.wanmi.sbc.pay.bean.enums.WxPayTradeType;
import com.wanmi.sbc.pay.reponse.PayQRCodeReponse;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunGetResourceRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.*;
import com.wanmi.sbc.wallet.api.response.wallet.BalanceByCustomerIdResponse;
import com.wanmi.sbc.wallet.api.response.wallet.CustomerWalletStorePgResponse;
import com.wanmi.sbc.wallet.bean.enums.WalletDetailsType;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.TicketsFormQueryVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Api(tags = "PayController", description = "交易支付")
@RestController
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private AliPayProvider aliPayProvider;
    @Autowired
    private WalletMerchantProvider walletMerchantProvider;
    @Autowired
    private AuditQueryProvider auditQueryProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;
    @Autowired
    private CustomerWalletProvider customerWalletProvider;
    @Autowired
    private CustomerWalletQueryProvider customerWalletQueryProvider;
    @Autowired
    private WalletRecordProvider walletRecordProvider;
    @Autowired
    private GeneratorService generatorService;
    @Autowired
    private RefundOrderProvider refundOrderProvider;
    @Autowired
    TradeQueryProvider tradeQueryProvider;

    @Autowired
    private CcbPayProvider ccbPayProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private ReturnOrderProvider returnOrderProvider;
    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    // 支付宝充值逻辑（网页扫码支付）
    @ApiOperation(value = "商户充值鲸币，调用接口返回支付宝/微信充值二维码")
    @RequestMapping(value = "/performAlipayRecharge", method = RequestMethod.POST)
    public BaseResponse<PayQRCodeReponse> performAlipayRecharge(@RequestBody CustomerWalletAddRequest walletRecord, HttpServletRequest request) {
            String openId = "app_WbTCuHyjD4W9bDC8";
            // 生成订单，记录流水
            String storeId = ((Claims) request.getAttribute("claims")).get("storeId").toString();
            walletRecord.setStoreId(storeId);
            if (walletRecord.getRechargeBalance() == null || !isValidAmount(String.valueOf(walletRecord.getRechargeBalance()))) {
                throw new SbcRuntimeException("K-100002","请输入正确充值金额");
            }
            StoreByIdRequest storeByIdRequest = new StoreByIdRequest();
            storeByIdRequest.setStoreId(Long.valueOf(storeId));
            StoreVO storeVO = storeQueryProvider.getById(storeByIdRequest).getContext().getStoreVO();
            CompanyType companyType = storeVO.getCompanyType();
//            if (companyType.toValue() != CompanyType.PLATFORM.toValue() || companyType.toValue() != CompanyType.UNIFIED.toValue()) {
//                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"第三方商家不支持微信充值");
//            }
            // 查询商户钱包是否开通
            WalletInfoRequest walletInfoRequest = new WalletInfoRequest();
            walletInfoRequest.setStoreId(storeId);
            walletInfoRequest.setStoreFlag(true);
            CusWalletVO context1 = customerWalletProvider.queryCustomerWallet(walletInfoRequest).getContext();
            if (context1 == null) {
                throw new SbcRuntimeException("K-111116","商家未开通钱包功能，请联系平台运营开通");
            }
            TicketsFormQueryVO context = walletMerchantProvider.generateTicketAndRecord(walletRecord).getContext();
            // 创建微信支付二维码
            WxPayForNativeRequest nativeRequest = new WxPayForNativeRequest();
            nativeRequest.setOut_trade_no(context.getRecordNo());
            nativeRequest.setTotal_fee(String.valueOf(walletRecord.getRechargeBalance()));
            nativeRequest.setSpbill_create_ip(HttpUtil.getIpAddr());
            nativeRequest.setBody("订单充值");
            nativeRequest.setTrade_type(WxPayTradeType.NATIVE.toString());
            nativeRequest.setStoreId(-1L);
            nativeRequest.setPay_order_no(context.getRecordNo());
//            WxPayForNativeResponse context2 = wxPayProvider.wxPayForNativeStoreNotify(nativeRequest).getContext();
//            AliPayFormResponse context2 = aliPayProvider.getPayForm(payExtraRequest).getContext();

        BigDecimal rechargeBalance = walletRecord.getRechargeBalance();
        String recordNo = context.getRecordNo();
        CcbPayOrderRequest ccbRequest = new CcbPayOrderRequest();
        ccbRequest.setMainOrderNo(recordNo);
        ccbRequest.setOrderAmount(rechargeBalance);
        ccbRequest.setTxnAmt(rechargeBalance);

        List<CcbSubPayOrderRequest> subOrderList = new ArrayList<>();
        CcbSubPayOrderRequest subOrder = new CcbSubPayOrderRequest();
        subOrder.setCmdtyOrderNo(recordNo + "01");
        subOrder.setOrderAmount(rechargeBalance);
        subOrder.setTxnAmt(rechargeBalance);
        subOrder.setTid(String.valueOf(context.getWalletId()));

//        subOrder.setCommissionFlag(CcbSubOrderType.MERCHANT);
        subOrderList.add(subOrder);

        ccbRequest.setSubOrderList(subOrderList);
        ccbRequest.setClientIp(HttpUtil.getIpAddr());
        ccbRequest.setChannelId(32L);
        ccbRequest.setPayOrderNo(context.getRecordNo());
        ccbRequest.setBusinessId(recordNo);
        if (walletRecord.getPayType() == 1) {
            ccbRequest.setPayType(8);
            String Cshdk_Url = ccbPayProvider.ccbPayOrder(ccbRequest).getContext().getCshdk_Url();
            //操作日志添加
            operateLogMQUtil.convertAndSend("交易支付", "鲸币支付对公支付","商户充值鲸币，调用接口鲸币支付对公支付");
            return BaseResponse.success(PayQRCodeReponse.builder().Cshdk_Url(Cshdk_Url).build());
        } else {
            ccbRequest.setPayType(4);
            String pay_qr_code = ccbPayProvider.ccbPayOrder(ccbRequest).getContext().getPay_Qr_Code();
            //操作日志添加
            operateLogMQUtil.convertAndSend("交易支付", "商户充值鲸币，调用接口返回支付宝/微信充值二维码","商户充值鲸币，调用接口返回支付宝/微信充值二维码");
            return BaseResponse.success(PayQRCodeReponse.builder().WxPayQRCode(pay_qr_code).build());
        }
        // return BaseResponse.success(PayQRCodeReponse.builder().WxPayQRCode(context2.getCode_url()).build());
    }

        /***
         * 商户赠送鲸币给用户
         */
        @ApiOperation(value = "商户赠送金币给用户")
        @RequestMapping(value = "/merchantGiveUser", method = RequestMethod.POST)
        @LcnTransaction
        @Transactional(noRollbackFor = SbcRuntimeException.class)
        public BaseResponse merchantGiveUser (@RequestBody CustomerWalletGiveRequest customerWalletGiveRequest, HttpServletRequest
        request) throws SbcRuntimeException{
            try {
                log.info("当前用户账号======{}",customerWalletGiveRequest.getCustomerAccount());
                //操作日志添加
                operateLogMQUtil.convertAndSend("交易支付", "商户赠送金币给用户","商户赠送金币给用户：当前用户账号" + (Objects.nonNull(customerWalletGiveRequest) ? customerWalletGiveRequest.getCustomerAccount() : ""));
                String storeId = ((Claims) request.getAttribute("claims")).get("storeId").toString();
                if (StringUtils.isEmpty(storeId)) {
                    return BaseResponse.error("未找到登录用户");
                }
                if(!StringUtils.isEmpty(customerWalletGiveRequest.getRelationOrderId())) {
                    isCommitStore(customerWalletGiveRequest.getRelationOrderId(),storeId);
                }
                customerWalletGiveRequest.setStoreId(storeId);
                if (!StringUtils.isEmpty(customerWalletGiveRequest.getRelationOrderId()) && StringUtils.isEmpty(customerWalletGiveRequest.getChangOverToStoreAccount())) {
                    return refundForClaims(customerWalletGiveRequest);
                } else {
                    String j = generatorService.generate("J");
                    if (!StringUtils.isEmpty(customerWalletGiveRequest.getChangOverToStoreAccount())) {
                        customerWalletGiveRequest.setRelationOrderId(generatorService.generate("SZS"));
                    }
                    if (customerWalletGiveRequest.getOpertionType() == 0) {
                        customerWalletGiveRequest.setTradeRemark(WalletDetailsType.GIVE_JINGBI.getDesc()+"-"+customerWalletGiveRequest.getRelationOrderId());
                        customerWalletGiveRequest.setRemark(WalletDetailsType.GIVE_JINGBI.getDesc()+"-"+customerWalletGiveRequest.getRemark());
                    } else {
                        customerWalletGiveRequest.setTradeRemark(WalletDetailsType.BACK_JINGBI.getDesc()+"-"+customerWalletGiveRequest.getRelationOrderId());
                        customerWalletGiveRequest.setRemark(WalletDetailsType.BACK_JINGBI.getDesc());
                        customerWalletGiveRequest.setWalletRecordTradeType(WalletRecordTradeType.BACK);
                    }
                    BaseResponse<WalletRecordVO> walletRecordVOBaseResponse = walletMerchantProvider.merchantGiveUser(customerWalletGiveRequest);
//                    pushKingDee(walletRecordVOBaseResponse.getContext());
                return walletRecordVOBaseResponse;
                }
            } catch (SbcRuntimeException e) {
                log.info("商家赠送金额异常==={}",e.getResult());
                throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
            }
        }

        @ApiOperation(value = "平台鲸币充值")
        @RequestMapping(value = "/platRecharge",method = RequestMethod.POST)
        public BaseResponse platRecharge(@RequestBody  CustomerWalletOrderByRequest customerWalletOrderByRequest) throws Exception {
            return walletMerchantProvider.platRecharge(customerWalletOrderByRequest);
        }

        @ApiOperation(value = "刷新记录表中StoreId")
        @RequestMapping(value = "/refreshCustomerWalletRecordAll", method = RequestMethod.POST)
        public BaseResponse refreshCustomerWalletRecordAll(){
//            BaseResponse baseResponse = walletRecordProvider.refreshCustomerWalletRecordAll();
            return BaseResponse.SUCCESSFUL();
        }
//        private void pushKingDee(WalletRecordVO walletRecordVO) {
//            String recordNo = walletRecordVO.getRecordNo();
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.FMT_TIME_3, Locale.UK);
//            String sendNo = "ZS" + LocalDateTime.now().format(dateTimeFormatter) + RandomStringUtils.randomNumeric(4);
//            // 推送金蝶
//            CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
//                    .builder()
//                    .tid(recordNo)
//                    .buyerAccount(walletRecordVO.getCustomerAccount())
//                    .applyPrice(walletRecordVO.getDealPrice())
//                    .saleType(SaleType.RETAIL)
//                    .sendNo(sendNo)
//                    .build();
//            tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
//        }

        private BaseResponse refundForClaims(CustomerWalletGiveRequest customerWalletGiveRequest) {
            RefundForClaimsRequest refund = new RefundForClaimsRequest();
            refund.setOperator(commonUtil.getOperator());
            refund.setOrderNo(customerWalletGiveRequest.getRelationOrderId());
            refund.setRechargeBalance(customerWalletGiveRequest.getBalance());
            refund.setRemark(customerWalletGiveRequest.getRemark());
            refund.setCustomerAccount(customerWalletGiveRequest.getCustomerAccount());
            refund.setReturnOrderNo(customerWalletGiveRequest.getReturnOrderCode());
            refund.setClaimsApplyType(customerWalletGiveRequest.getOpertionType()==0?ClaimsApplyType.INCREASE_RECHARGE:ClaimsApplyType.DEDUCTION_RECHARGE);
            try {
                BaseResponse baseResponse = refundOrderProvider.refundForClaims(refund);
                boolean isNP=customerWalletGiveRequest.getReturnOrderCode().startsWith("R");
                if (isNP) {
                    RefundOrderByReturnOrderNoRequest refundOrder = new RefundOrderByReturnOrderNoRequest();
                    refundOrder.setReturnOrderCode(customerWalletGiveRequest.getReturnOrderCode());
                    RefundOrderByReturnOrderNoResponse context = refundOrderQueryProvider.getByReturnOrderNo(refundOrder).getContext();
                    if (context!= null && context.getRefundStatus().toValue()!= RefundStatus.FINISH.toValue()) {
                        ReturnOrderOnlineByWalletRequest request = new ReturnOrderOnlineByWalletRequest();
                        WalletRecordVO walletRecordVO = new WalletRecordVO();
                        walletRecordVO.setRelationOrderId(customerWalletGiveRequest.getReturnOrderCode());
                        walletRecordVO.setDealPrice(customerWalletGiveRequest.getBalance());
                        request.setWalletRecordVO(walletRecordVO);
                        request.setOperator(commonUtil.getOperator());
                        returnOrderProvider.refundOnlineByWalletRecordVO(request);
                    }
                }
                return BaseResponse.SUCCESSFUL();
            } catch (SbcRuntimeException e) {
                throw new SbcRuntimeException(e.getErrorCode(),e.getResult());
            }
        }

        private void isCommitStore(String relationOrderId,String storeId) {
            TradeVO tradeVO = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(relationOrderId).build()).getContext().getTradeVO();
            if (null == tradeVO) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"未找到对应订单，请填写正确订单号");
            }
            if (!tradeVO.getSupplier().getStoreId().toString().equals(storeId)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"订单商家与当前登录商家不一致，请检查订单");
            }
        }

        /**
         * 鲸币提现
         * */
        @ApiOperation(value = "商户鲸币提现")
        @RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
        @LcnTransaction
        @Transactional(noRollbackFor = SbcRuntimeException.class)
        public BaseResponse withdrawal (@RequestBody InitiateWithdrawalRequest request) throws SbcRuntimeException{
            BaseResponse baseResponse = null;
            String storeId = commonUtil.getStoreId().toString();

            RLock rLock = redissonClient.getFairLock(storeId);
            rLock.lock();
            try {
                request.setStoreId(storeId);
                //根据逻辑,先保存
                baseResponse = walletMerchantProvider.withdrawal(request);
            } catch (SbcRuntimeException e) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,e.getResult());
            } finally {
                rLock.unlock();
            }
            //操作日志添加
            operateLogMQUtil.convertAndSend("交易支付", "商户鲸币提现","操作成功");
            return baseResponse;
        }

        @ApiOperation(value = "新增用户钱包")
        @PostMapping(value = "/addUserWallet" )
        @LcnTransaction
        @Transactional(noRollbackFor = Exception.class)
        public BaseResponse<BalanceByCustomerIdResponse> addUserWallet (HttpServletRequest request){
            String storeId = ((Claims) request.getAttribute("claims")).get("storeId").toString();
            String operatorId = commonUtil.getOperatorId();
            String account = commonUtil.getOperator().getAccount();
            WalletByWalletIdAddRequest wallet = new WalletByWalletIdAddRequest();
            wallet.setCustomerId(operatorId);
            wallet.setCustomerAccount(account);
            wallet.setMerchantFlag(true);
            wallet.setStoreId(storeId);
            //操作日志添加
            operateLogMQUtil.convertAndSend("交易支付", "新增用户钱包","新增用户钱包：账户" + (StringUtils.isEmpty(account) ? "" : account));
            return customerWalletProvider.addUserWallet(wallet);
        }

    @ApiOperation(value = "平台手动充值鲸币接口")
    @PostMapping(value = "/platoToStore"  )
    @LcnTransaction
    @Transactional(noRollbackFor = Exception.class)
    public BaseResponse platoToStore (@RequestBody StoreAddRequest request) throws Exception {
        //操作日志添加
        operateLogMQUtil.convertAndSend("交易支付", "平台手动充值鲸币接口","平台手动充值鲸币接口：商家编号" + (Objects.nonNull(request) ? request.getStoreAccount() : ""));
        return walletMerchantProvider.platoToStore(request);
    }

    @ApiOperation(value = "查询单个详情得充值记录")
    @RequestMapping(value = "/getBalanceByStoreId", method = RequestMethod.POST)
    public BaseResponse getBalanceByStoreId (@RequestBody WalletByCustomerIdQueryRequest request) throws Exception {
        return customerWalletQueryProvider.getBalanceByStoreId(request);
    }

    @ApiOperation(value = "查询鲸币充值列表")
    @RequestMapping(value = "/queryPageCustomerWalletSupplier", method = RequestMethod.POST)
    public BaseResponse<CustomerWalletStorePgResponse> queryPageCustomerWalletSupplier (@RequestBody CustomerWalletSupplierRequest customerWalletSupplierRequest) throws Exception {
        return customerWalletQueryProvider.queryPageCustomerWalletSupplier(customerWalletSupplierRequest);
    }

    @ApiOperation(value = "查询鲸币，总余额，昨天总收入，昨天总支出")
    @RequestMapping(value = "/queryWalletMoney", method = RequestMethod.POST)
    public BaseResponse queryWalletMoney (@RequestBody WalletByWalletIdAddRequest request) throws Exception {
        request.setCustomerAccount("11");
        request.setCustomerId("11");
        return customerWalletQueryProvider.queryWalletMoney(request);
    }

    @ApiOperation(value = "查询资金池，总余额，昨天总收入，昨天总支出")
    @RequestMapping(value = "/queryWalletCountMoney", method = RequestMethod.POST)
    public BaseResponse queryWalletCountMoney () throws Exception {
        return customerWalletQueryProvider.queryWalletCountMoney();
    }

    @ApiOperation(value = "用户鲸币列表查询")
    @RequestMapping(value = "/userStoreList", method = RequestMethod.POST)
    public BaseResponse userStoreList (@RequestBody WalletUserPageQueryRequest request) throws Exception {
        return customerWalletQueryProvider.userAndStoredList(request);
    }

    @ApiOperation(value = "查询当个用户鲸币余额详情，头部那一块")
    @RequestMapping(value = "/userByWalletId", method = RequestMethod.POST)
    public BaseResponse userByWalletId (@RequestBody WalletInfoRequest request) throws Exception {
        return customerWalletQueryProvider.userByWalletId(request);
    }

    @ApiOperation(value = "查询当个鲸币详情列表接口")
    @RequestMapping(value = "/queryRecordList", method = RequestMethod.POST)
    public BaseResponse queryRecordList (@RequestBody WalletRecordQueryRequest request) throws Exception {
        Long storeId = commonUtil.getStoreId();
        if (storeId != null) {
            request.setStoreId(storeId);
        }
        if (request.getStoreFlag() != null && request.getStoreFlag()) {
            // 查询商家账号
            EmployeeByAccountNameResponse context = employeeQueryProvider.getByAccountName(EmployeeByAccountNameRequest.builder().accountName(request.getCustomerAccount()).accountType(AccountType.s2bSupplier).build()).getContext();
            StoreByCompanyInfoIdResponse context1 = storeQueryProvider.getStoreByCompanyInfoId(StoreByCompanyInfoIdRequest.builder().companyInfoId(context.getEmployee().getCompanyInfoId()).build()).getContext();
            request.setStoreId(context1.getStoreVO().getStoreId());
        }
        return walletRecordProvider.queryRecordList(request);
    }

    @ApiOperation(value = "查询资金池鲸币详情记录")
    @RequestMapping(value = "/queryRecordCountList", method = RequestMethod.POST)
    public BaseResponse queryRecordCountList (@RequestBody WalletRecordQueryRequest request) throws Exception {
        request.setCustomerAccount("13966668888");
        return walletRecordProvider.queryRecordList(request);
    }

    @ApiOperation(value = "查看鲸币余额")
    @RequestMapping(value = "/queryCustomerWallet", method = RequestMethod.POST)
    public BaseResponse queryCustomerWallet (@RequestBody WalletInfoRequest request) {
        CusWalletVO context = customerWalletProvider.queryCustomerWallet(request).getContext();
        if (context == null) {
            throw new SbcRuntimeException("K-111116","商家未开通钱包功能，请联系平台运营开通");
        }
        return BaseResponse.success(context);
    }


    @ApiOperation(value = "查询用户账户余额")
    @RequestMapping(value = "/findWalletByBalance", method = RequestMethod.POST)
    public BaseResponse findWalletByBalance() {
        customerWalletQueryProvider.findWalletByBalance(CustomerWalletSchedQueryRequest.builder().balance(new BigDecimal(100.00)).build());
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "下载鲸币余额文件")
    @RequestMapping(value = "/downLoadFileName/{fileName}",method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) throws IOException {
        String downLoadfileName = "wanmi-ares/dev/wallet/schedTasks/钱包余额明细-"+fileName+".xlsx";
        byte[] context = customerWalletQueryProvider.downLoadFile(DownLoadFileRequest.builder().fileName(downLoadfileName).build()).getContext();
        // 将字节数组包装成输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(context);

        // 创建一个 HSSFWorkbook
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

        // 将工作簿内容写入字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] excelBytes = outputStream.toByteArray();

        // 文件名中包含中文字符
        String outFileName ="钱包余额明细_"+fileName+".xlsx";
        outFileName = URLEncoder.encode(outFileName, "UTF-8");
        // 设置响应头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", outFileName); // 设置下载的文件名，使用 .xls 扩展名

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }


    public static boolean isValidAmount(String amount) {
        try {
            BigDecimal value = new BigDecimal(amount);
            int scale = value.scale();
            return scale<=2 && value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }



}
