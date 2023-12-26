package com.wanmi.sbc.order.refund.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.provider.funds.CustomerFundsProvider;
import com.wanmi.sbc.account.api.provider.offline.OfflineQueryProvider;
import com.wanmi.sbc.account.api.request.funds.CustomerFundsAddAmountRequest;
import com.wanmi.sbc.account.api.request.offline.OfflineAccountGetByIdRequest;
import com.wanmi.sbc.account.api.response.offline.OfflineAccountGetByIdResponse;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.account.bean.enums.RefundStatus;
import com.wanmi.sbc.account.bean.enums.WalletDetailsType;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.GeneratorService;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.request.company.CompanyListRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByConditionRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.order.api.request.refund.RefundOrderRefundRequest;
import com.wanmi.sbc.order.api.request.refund.RefundOrderRequest;
import com.wanmi.sbc.order.api.response.manualrefund.ManualRefundImgResponse;
import com.wanmi.sbc.order.api.response.refund.RefundOrderPageResponse;
import com.wanmi.sbc.order.bean.enums.ReturnFlowState;
import com.wanmi.sbc.order.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.order.bean.vo.RefundOrderResponse;
import com.wanmi.sbc.order.customer.service.CustomerCommonService;
import com.wanmi.sbc.order.enums.RefundBillTypeEnum;
import com.wanmi.sbc.order.manualrefund.service.ManualRefundImgService;
import com.wanmi.sbc.order.mq.OrderProducerService;
import com.wanmi.sbc.order.refund.model.root.RefundBill;
import com.wanmi.sbc.order.refund.model.root.RefundOrder;
import com.wanmi.sbc.order.refund.repository.RefundOrderRepository;
import com.wanmi.sbc.order.returnorder.model.entity.ReturnItem;
import com.wanmi.sbc.order.returnorder.model.root.NewPileReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnOrder;
import com.wanmi.sbc.order.returnorder.model.root.ReturnPileOrder;
import com.wanmi.sbc.order.returnorder.model.value.RefundOnlineBO;
import com.wanmi.sbc.order.returnorder.model.value.ReturnPrice;
import com.wanmi.sbc.order.returnorder.repository.ReturnOrderRepository;
import com.wanmi.sbc.order.returnorder.repository.ReturnPileOrderRepository;
import com.wanmi.sbc.order.returnorder.service.ReturnOrderService;
import com.wanmi.sbc.order.returnorder.service.ReturnPileOrderService;
import com.wanmi.sbc.order.returnorder.service.newPileOrder.NewPileReturnOrderService;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import com.wanmi.sbc.order.trade.model.entity.value.Buyer;
import com.wanmi.sbc.order.trade.model.entity.value.TradePrice;
import com.wanmi.sbc.order.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.order.trade.model.root.PileTrade;
import com.wanmi.sbc.order.trade.model.root.Trade;
import com.wanmi.sbc.order.trade.service.TradeService;
import com.wanmi.sbc.order.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.order.util.XssUtils;
import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
import com.wanmi.sbc.pay.api.provider.PayProvider;
import com.wanmi.sbc.pay.api.provider.PayQueryProvider;
import com.wanmi.sbc.pay.api.request.RefundByChannelRequest;
import com.wanmi.sbc.pay.api.request.RefundRequest;
import com.wanmi.sbc.pay.api.request.TradeRecordByOrderCodeRequest;
import com.wanmi.sbc.pay.bean.enums.RefundType;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.CustomerWalletQueryProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletMerchantProvider;
import com.wanmi.sbc.wallet.api.provider.wallet.WalletRecordProvider;
import com.wanmi.sbc.wallet.api.request.wallet.AddWalletRecordRecordRequest;
import com.wanmi.sbc.wallet.api.request.wallet.CustomerWalletGiveRequest;
import com.wanmi.sbc.wallet.api.request.wallet.WalletByCustomerIdQueryRequest;
import com.wanmi.sbc.wallet.bean.enums.BudgetType;
import com.wanmi.sbc.wallet.bean.enums.TradeStateEnum;
import com.wanmi.sbc.wallet.bean.enums.WalletRecordTradeType;
import com.wanmi.sbc.wallet.bean.vo.CusWalletVO;
import com.wanmi.sbc.wallet.bean.vo.WalletRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;

/**
 * 退款单服务
 * Created by zhangjin on 2017/4/21.
 */
@Slf4j
@Service
public class RefundOrderService {

    @Autowired
    private CustomerCommonService customerCommonService;

    @Autowired
    private GeneratorService generatorService;

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    private RefundBillService refundBillService;

    @Autowired
    private OfflineQueryProvider offlineQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnOrderService returnOrderService;

    @Autowired
    private ReturnPileOrderRepository returnPileOrderRepository;

    @Autowired
    private CustomerFundsProvider customerFundsProvider;

    @Autowired
    private PayProvider payProvider;

    @Autowired
    private ReturnPileOrderService returnPileOrderService;

    @Autowired
    private NewPileReturnOrderService newPileReturnOrderService;

    @Autowired
    private PayQueryProvider payQueryProvider;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private CustomerWalletQueryProvider walletQueryProvider;

    @Autowired
    private WalletRecordProvider walletRecordProvider;

    @Autowired
    private CustomerWalletProvider customerWalletProvider;

    @Autowired
    private WalletMerchantProvider walletMerchantProvider;

    @Autowired
    private ManualRefundImgService manualRefundImgService;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    @Autowired
    private OrderProducerService orderProducerService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Transactional
    public List<RefundOrder> batchAdd(List<RefundOrder> refundOrderList) {
        return refundOrderRepository.saveAll(refundOrderList);
    }


    /**
     * 根据囤货退单生成退款单 //todo 操作人
     *
     * @param returnOrderCode returnOrderCode
     * @param customerId      customerId
     * @param price           price 应退金额
     * @param price           payType 应退金额
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundPileOrderByReturnOrderCode(String returnOrderCode, String customerId,
                                                                          BigDecimal price, PayType payType) {
        ReturnPileOrder returnOrder = returnPileOrderRepository.findById(returnOrderCode).orElse(null);
        if (Objects.isNull(returnOrder)) {
            log.error("退单编号：{},查询不到退单信息", returnOrderCode);
            return Optional.empty();
        }
        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnOrderCode);
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(price);
        refundOrder.setReturnPoints(Objects.nonNull(returnOrder.getReturnPoints()) ?
                returnOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    /**
     * 根据退单生成退款单 //todo 操作人
     *
     * @param returnOrderCode returnOrderCode
     * @param customerId      customerId
     * @param price           price 应退金额
     * @param price           payType 应退金额
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundOrderByReturnOrderCode(String returnOrderCode, String customerId,
                                                                      BigDecimal price, PayType payType) {
        ReturnOrder returnOrder = returnOrderRepository.findById(returnOrderCode).orElse(null);
        if (Objects.isNull(returnOrder)) {
            log.error("退单编号：{},查询不到退单信息", returnOrderCode);
            return Optional.empty();
        }
        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnOrderCode);
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(price);
        refundOrder.setReturnPoints(Objects.nonNull(returnOrder.getReturnPoints()) ?
                returnOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    /**
     * 根据退单生成退款单 //todo 操作人
     *
     * @param returnOrder returnOrderCode
     * @param customerId  customerId
     * @param price       price 应退金额
     * @param price       payType 应退金额
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundOrderByEnity(ReturnOrder returnOrder, String customerId,
                                                            BigDecimal price, PayType payType) {
        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnOrder.getId());
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(price);
        refundOrder.setReturnPoints(Objects.nonNull(returnOrder.getReturnPoints()) ?
                returnOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    @Transactional
    public Optional<RefundOrder> generateRefundOrderByWalletRecord(ReturnOrder returnOrder, WalletRecordVO walletRecordVO) {
        RefundOrder dbRefundOrder = getRefundOrderByReturnOrderNo(returnOrder.getId());
        RefundOrder refundOrder = new RefundOrder();
        //CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailById(walletRecordVO.get);
        refundOrder.setReturnOrderCode(returnOrder.getId());
        refundOrder.setCustomerDetailId(dbRefundOrder.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(walletRecordVO.getDealPrice());
        refundOrder.setReturnPoints(0L);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(PayType.BALANCER);
        refundOrder.setSupplierId(dbRefundOrder.getSupplierId());

        dbRefundOrder.setDelFlag(DeleteFlag.YES);
        dbRefundOrder.setDelTime(LocalDateTime.now());
        refundOrderRepository.saveAndFlush(dbRefundOrder);

        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }


    /**
     * 根据退单生成退款单
     *
     * @param returnOrder returnOrderCode
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundOrderByEnityNewPile(NewPileReturnOrder returnOrder) {

        ReturnPrice returnPrice = returnOrder.getReturnPrice();
        BigDecimal canReturnPrice = returnPrice.getApplyStatus() ? returnPrice.getApplyPrice() : returnPrice.getTotalPrice();

        String customerId = returnOrder.getBuyer().getId();
        PayType payType = returnOrder.getPayType();

        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnOrder.getId());
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(canReturnPrice);
        refundOrder.setReturnPoints(Objects.nonNull(returnOrder.getReturnPoints()) ?
                returnOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnOrder.getCompany().getCompanyInfoId());
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    /**
     * 根据退单生成退款单 //todo 操作人
     *
     * @param returnPileOrder returnOrderCode
     * @param customerId      customerId
     * @param price           price 应退金额
     * @param price           payType 应退金额
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundPileOrderByEnity(ReturnPileOrder returnPileOrder, String customerId,
                                                                BigDecimal price, PayType payType) {
        log.info("囤货订单根据退单生成退款单===========================>rid：", returnPileOrder.getId());
        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnPileOrder.getId());
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(price);
        refundOrder.setReturnPoints(Objects.nonNull(returnPileOrder.getReturnPoints()) ?
                returnPileOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnPileOrder.getCompany().getCompanyInfoId());
        log.info("囤货订单根据退单生成退款单==========================>{}", refundOrder);
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    /**
     * 根据退单生成退款单 //todo 操作人
     *
     * @param returnPileOrder returnOrderCode
     * @param customerId      customerId
     * @param price           price 应退金额
     * @param price           payType 应退金额
     * @return 退款单
     */
    @Transactional
    public Optional<RefundOrder> generateRefundPileOrderByEnity(NewPileReturnOrder returnPileOrder, String customerId,
                                                                BigDecimal price, PayType payType) {
        log.info("囤货订单根据退单生成退款单===========================>rid：", returnPileOrder.getId());
        RefundOrder refundOrder = new RefundOrder();
        CustomerDetailVO customerDetail = customerCommonService.getCustomerDetailByCustomerId(customerId);
        refundOrder.setReturnOrderCode(returnPileOrder.getId());
        refundOrder.setCustomerDetailId(customerDetail.getCustomerDetailId());
        refundOrder.setCreateTime(LocalDateTime.now());
        refundOrder.setRefundCode(generatorService.generateRid());
        refundOrder.setRefundStatus(RefundStatus.TODO);
        refundOrder.setReturnPrice(price);
        refundOrder.setReturnPoints(Objects.nonNull(returnPileOrder.getReturnPoints()) ?
                returnPileOrder.getReturnPoints().getApplyPoints() : null);
        refundOrder.setDelFlag(DeleteFlag.NO);
        refundOrder.setPayType(payType);
        refundOrder.setSupplierId(returnPileOrder.getCompany().getCompanyInfoId());
        log.info("囤货订单根据退单生成退款单==========================>{}", refundOrder);
        return Optional.ofNullable(refundOrderRepository.saveAndFlush(refundOrder));
    }

    /**
     * 查询退款单
     *
     * @param refundOrderRequest refundOrderRequest
     * @return Page<RefundOrder>
     */
    public RefundOrderPageResponse findByRefundOrderRequest(RefundOrderRequest refundOrderRequest) {
        RefundOrderPageResponse refundOrderPageResponse = new RefundOrderPageResponse();
        refundOrderPageResponse.setData(EMPTY_LIST);
        refundOrderPageResponse.setTotal(0L);

        //模糊匹配会员/商户名称，不符合条件直接返回
        if (!this.likeCustomerAndSupplierName(refundOrderRequest)) {
            return refundOrderPageResponse;
        }

        Page<RefundOrder> refundOrderPage = refundOrderRepository.findAll(findByRequest(refundOrderRequest),
                PageRequest.of(refundOrderRequest.getPageNum(), refundOrderRequest.getPageSize()));

        if (Objects.isNull(refundOrderPage) || CollectionUtils.isEmpty(refundOrderPage.getContent())) {
            return refundOrderPageResponse;
        }
        List<RefundOrderResponse> refundOrderResponses = refundOrderPage.getContent().stream()
                .map(this::generateRefundOrderResponse).collect(Collectors.toList());

        refundOrderPageResponse.setTotal(refundOrderPage.getTotalElements());
        refundOrderPageResponse.setData(refundOrderResponses);
        return refundOrderPageResponse;
    }

    /**
     * 查询不带分页的退款单
     *
     * @param refundOrderRequest refundOrderRequest
     * @return RefundOrderPageResponse
     */
    public RefundOrderPageResponse findByRefundOrderRequestWithNoPage(RefundOrderRequest refundOrderRequest) {
        RefundOrderPageResponse refundOrderPageResponse = new RefundOrderPageResponse();
        refundOrderPageResponse.setData(EMPTY_LIST);
        refundOrderPageResponse.setTotal(0L);

        //模糊匹配会员/商户名称，不符合条件直接返回
        if (!this.likeCustomerAndSupplierName(refundOrderRequest)) {
            return refundOrderPageResponse;
        }

        List<RefundOrder> refundOrders = refundOrderRepository.findAll(findByRequest(refundOrderRequest));
        if (Objects.isNull(refundOrders) || CollectionUtils.isEmpty(refundOrders)) {
            return refundOrderPageResponse;
        }
        List<RefundOrderResponse> refundOrderResponses = refundOrders.stream().map(this::generateRefundOrderResponse)
                .collect(Collectors.toList());
        refundOrderPageResponse.setData(refundOrderResponses);
        return refundOrderPageResponse;
    }

    /**
     * 根据条件查询，不分页
     *
     * @param refundOrderRequest refundOrderRequest
     * @return List
     */
    public List<RefundOrder> findAll(RefundOrderRequest refundOrderRequest) {
        //模糊匹配会员/商户名称，不符合条件直接返回
        if (!this.likeCustomerAndSupplierName(refundOrderRequest)) {
            return EMPTY_LIST;
        }
        return refundOrderRepository.findAll(findByRequest(refundOrderRequest));
    }

    public Optional<RefundOrder> findById(String refundId) {
        return refundOrderRepository.findByRefundIdAndDelFlag(refundId, DeleteFlag.NO);
    }

    /**
     * 根据退单编号查询退款单
     *
     * @param returnOrderCode 退单编号
     * @return 退款单信息
     */
    public RefundOrder findRefundOrderByReturnOrderNo(String returnOrderCode) {
        Optional<RefundOrder> refundOrderOptional =
                refundOrderRepository.findAllByReturnOrderCodeAndDelFlag(returnOrderCode, DeleteFlag.NO);
        if (refundOrderOptional.isPresent()) {
            return refundOrderOptional.get();
        } else {
            throw new SbcRuntimeException("K-050003");
        }
    }

    /**
     * 根据退单编号查询退款单
     *
     * @param returnOrderCode 退单编号
     * @return 退款单信息
     */
    public RefundOrderResponse findRefundOrderRespByReturnOrderNo(String returnOrderCode) {
        return generateRefundOrderResponse(findRefundOrderByReturnOrderNo(returnOrderCode));
    }

    /**
     * 根据退单编号查询囤货退款单
     *
     * @param returnOrderCode 退单编号
     * @return 囤货退款单信息
     */
    public RefundOrderResponse findPileRefundOrderRespByReturnOrderNo(String returnOrderCode) {
        return generatePileRefundOrderResponse(findRefundOrderByReturnOrderNo(returnOrderCode));
    }

    /**
     * 根据 RefundOrder 生成 RefundOrderResponse 对象
     *
     * @param refundOrder refundOrder
     * @return new RefundOrderResponse()
     */
    private RefundOrderResponse generatePileRefundOrderResponse(RefundOrder refundOrder) {
        RefundOrderResponse refundOrderResponse = new RefundOrderResponse();
        BeanUtils.copyProperties(refundOrder, refundOrderResponse);

        if (StringUtils.isNotBlank(refundOrder.getReturnOrderCode())) {
            Buyer buyer = returnOrderService.findReturnPileOrderById(refundOrder.getReturnOrderCode()).getBuyer();
            CustomerDetailVO customerDetailVO = new CustomerDetailVO();
            customerDetailVO.setCustomerId(buyer.getId());
            customerDetailVO.setCustomerName(buyer.getName());
            if (Objects.nonNull(customerDetailVO)) {
                refundOrderResponse.setCustomerName(customerDetailVO.getCustomerName());
                refundOrderResponse.setCustomerId(customerDetailVO.getCustomerId());
            }
        }
        CompanyInfoVO companyInfo = null;
        if (Objects.nonNull(refundOrder.getSupplierId())) {
            companyInfo = customerCommonService.getCompanyInfoById(refundOrder.getSupplierId());
        }
        if (Objects.nonNull(companyInfo)) {
            refundOrderResponse.setSupplierName(companyInfo.getSupplierName());
            if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                StoreVO store = companyInfo.getStoreVOList().get(0);
                refundOrderResponse.setStoreId(store.getStoreId());
            }
        }

        if (Objects.nonNull(refundOrder.getRefundBill()) && DeleteFlag.NO.equals(refundOrder.getRefundBill().getDelFlag())) {
            //从退单冗余信息中获取用户账户信息(防止用户修改后,查询的不是当时退单的账户信息)
            ReturnPileOrder returnOrder = returnPileOrderRepository.findById(refundOrder.getReturnOrderCode()).orElse(null);
            if (returnOrder != null && returnOrder.getCustomerAccount() != null) {
                log.info("客户账户信息customerAccount: {}", returnOrder.getCustomerAccount());
                refundOrderResponse.setCustomerAccountName(returnOrder.getCustomerAccount().getCustomerBankName() + "" +
                        " " + (
                        StringUtils.isNotBlank(returnOrder.getCustomerAccount().getCustomerAccountNo()) ?
                                getDexAccount(returnOrder.getCustomerAccount().getCustomerAccountNo()) : ""
                ));
            }

            refundOrderResponse.setActualReturnPrice(refundOrder.getRefundBill().getActualReturnPrice());
            refundOrderResponse.setActualReturnPoints(refundOrder.getRefundBill().getActualReturnPoints());
            refundOrderResponse.setReturnAccount(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setOfflineAccountId(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setComment(refundOrder.getRefundBill().getRefundComment());
            refundOrderResponse.setRefundBillCode(refundOrder.getRefundBill().getRefundBillCode());
            refundOrderResponse.setReturnAccountName(parseAccount(refundOrder));
            // 退款时间以boss端审核时间为准
            if (Objects.equals(RefundStatus.FINISH, refundOrder.getRefundStatus())) {
                refundOrderResponse.setRefundBillTime(refundOrder.getRefundBill().getCreateTime());
            }
            refundOrderResponse.setPayChannel(refundOrder.getRefundBill().getPayChannel());
            refundOrderResponse.setPayChannelId(refundOrder.getRefundBill().getPayChannelId());
        }

        return refundOrderResponse;
    }

    /**
     * 根据 RefundOrder 生成 RefundOrderResponse 对象
     *
     * @param refundOrder refundOrder
     * @return new RefundOrderResponse()
     */
    private RefundOrderResponse generateRefundOrderResponse(RefundOrder refundOrder) {
        RefundOrderResponse refundOrderResponse = new RefundOrderResponse();
        BeanUtils.copyProperties(refundOrder, refundOrderResponse);
//        ReturnPileOrder returnPileOrder = returnPileOrderService.dealEmptyFindById(refundOrder.getReturnOrderCode());
        NewPileReturnOrder returnPileOrder = newPileReturnOrderService.dealEmptyFindById(refundOrder.getReturnOrderCode());

        if (StringUtils.isNotBlank(refundOrder.getReturnOrderCode())) {
            Buyer buyer;
            //是否在为囤货退单
            if (Objects.nonNull(returnPileOrder)) {
                buyer = returnPileOrder.getBuyer();
                refundOrderResponse.setActivityType(TradeActivityTypeEnum.NEWPILETRADE.toActivityType());
                //填充退单来源
                refundOrderResponse.setPlatform(returnPileOrder.getPlatform());
            } else {
                ReturnOrder returnOrder = returnOrderService.findById(refundOrder.getReturnOrderCode());
                Trade trade = tradeService.detail(returnOrder.getTid());
                refundOrderResponse.setActivityType(trade.getActivityType());
                //填充退单来源
                refundOrderResponse.setPlatform(returnOrder.getPlatform());
                //填充是否是合并单
                refundOrderResponse.setMergFlag(trade.getMergFlag());
                refundOrderResponse.setTids(trade.getTids());
                buyer = returnOrder.getBuyer();
            }
            CustomerDetailVO customerDetailVO = new CustomerDetailVO();
            customerDetailVO.setCustomerId(buyer.getId());
            customerDetailVO.setCustomerName(buyer.getName());
            if (Objects.nonNull(customerDetailVO)) {
                refundOrderResponse.setCustomerName(customerDetailVO.getCustomerName());
                refundOrderResponse.setCustomerId(customerDetailVO.getCustomerId());
            }
        }
        CompanyInfoVO companyInfo = null;
        if (Objects.nonNull(refundOrder.getSupplierId())) {
            companyInfo = customerCommonService.getCompanyInfoById(refundOrder.getSupplierId());
        }
        if (Objects.nonNull(companyInfo)) {
            refundOrderResponse.setSupplierName(companyInfo.getSupplierName());
            if (CollectionUtils.isNotEmpty(companyInfo.getStoreVOList())) {
                StoreVO store = companyInfo.getStoreVOList().get(0);
                refundOrderResponse.setStoreId(store.getStoreId());
                refundOrderResponse.setStoreName(store.getStoreName());
            }
        }

        if (Objects.nonNull(refundOrder.getRefundBill()) && DeleteFlag.NO.equals(refundOrder.getRefundBill().getDelFlag())) {
            //从退单冗余信息中获取用户账户信息(防止用户修改后,查询的不是当时退单的账户信息)
            ReturnOrder returnOrder;
            if (Objects.nonNull(returnPileOrder)) {
                returnOrder = KsBeanUtil.convert(returnPileOrder, ReturnOrder.class);
            } else {
                returnOrder = returnOrderRepository.findById(refundOrder.getReturnOrderCode()).orElse(null);
            }
            if (returnOrder != null && returnOrder.getCustomerAccount() != null) {
                log.info("客户账户信息customerAccount: {}", returnOrder.getCustomerAccount());
                refundOrderResponse.setCustomerAccountName(returnOrder.getCustomerAccount().getCustomerBankName() + "" +
                        " " + (
                        StringUtils.isNotBlank(returnOrder.getCustomerAccount().getCustomerAccountNo()) ?
                                getDexAccount(returnOrder.getCustomerAccount().getCustomerAccountNo()) : ""
                ));
            }

            refundOrderResponse.setActualReturnPrice(refundOrder.getRefundBill().getActualReturnPrice());
            refundOrderResponse.setActualReturnPoints(refundOrder.getRefundBill().getActualReturnPoints());
            refundOrderResponse.setReturnAccount(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setOfflineAccountId(refundOrder.getRefundBill().getOfflineAccountId());
            refundOrderResponse.setComment(refundOrder.getRefundBill().getRefundComment());
            refundOrderResponse.setRefundBillCode(refundOrder.getRefundBill().getRefundBillCode());
            refundOrderResponse.setReturnAccountName(parseAccount(refundOrder));
            // 退款时间以boss端审核时间为准
            if (Objects.equals(RefundStatus.FINISH, refundOrder.getRefundStatus())) {
                refundOrderResponse.setRefundBillTime(refundOrder.getRefundBill().getCreateTime());
            }
            refundOrderResponse.setPayChannel(refundOrder.getRefundBill().getPayChannel());
            refundOrderResponse.setPayChannelId(refundOrder.getRefundBill().getPayChannelId());
        }
        ManualRefundImgResponse build = manualRefundImgService.getManualRefundImgResponse(refundOrderResponse.getRefundId(), RefundBillTypeEnum.RETURN.toStatus());
        refundOrderResponse.setManualRefundImgVOList(build.getManualRefundImgVOList());
        return refundOrderResponse;
    }

    /**
     * 作废退款单
     *
     * @param id id
     */
    @Transactional
    public void destory(String id) {
        refundBillService.deleteByRefundId(id);
        batchDestory(Lists.newArrayList(id));
    }

    /**
     * 批量确认
     *
     * @param ids ids
     */
    @Transactional
    public void batchConfirm(List<String> ids) {
        updateRefundConsumer.accept(ids, RefundStatus.FINISH);
    }

    /**
     * 批量作废
     *
     * @param ids ids
     */
    @Transactional
    public void batchDestory(List<String> ids) {
        updateRefundConsumer.accept(ids, RefundStatus.TODO);
    }


    /**
     * 拒绝退款添加退款原因
     *
     * @param id id
     */
    @Transactional
    public void refuse(String id, String refuseReason) {
        RefundOrder refundOrder = refundOrderRepository.findById(id).orElse(null);
        if (Objects.isNull(refundOrder)) {
            return;
        }
        refundOrder.setRefuseReason(refuseReason);
        refundOrderRepository.saveAndFlush(refundOrder);
        updateRefundConsumer.accept(Lists.newArrayList(id), RefundStatus.REFUSE);
    }


    /**
     * 合计退款金额
     *
     * @return BigDecimal
     */
    public BigDecimal sumReturnPrice(RefundOrderRequest refundOrderRequest) {
        //模糊匹配会员/商户名称，不符合条件直接返回0
        if (!this.likeCustomerAndSupplierName(refundOrderRequest)) {
            return BigDecimal.ZERO;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);

        Root<RefundOrder> root = query.from(RefundOrder.class);
        query.select(builder.sum(root.get("refundBill").get("actualReturnPrice")));
        query.where(buildWhere(refundOrderRequest, root, query, builder));

        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * 修改退款单
     */
    private BiConsumer<List<String>, RefundStatus> updateRefundConsumer = (ids, refundStatus) -> {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        refundOrderRepository.updateRefundOrderStatus(refundStatus, ids);
    };

    /**
     * 替代关联查询-模糊商家名称、模糊会员名称，以并且关系的判断
     *
     * @param refundOrderRequest
     * @return true:有符合条件的数据,false:没有符合条件的数据
     */
    private boolean likeCustomerAndSupplierName(final RefundOrderRequest refundOrderRequest) {
        boolean supplierLike = true;
        //商家名称
        if (StringUtils.isNotBlank(refundOrderRequest.getSupplierName())) {
            CompanyListRequest request = CompanyListRequest.builder()
                    .supplierName(refundOrderRequest.getSupplierName())
                    .build();
            refundOrderRequest.setCompanyInfoIds(customerCommonService.listCompanyInfoIdsByCondition(request));
            if (CollectionUtils.isEmpty(refundOrderRequest.getCompanyInfoIds())) {
                supplierLike = false;
            }
        }
        if (StringUtils.isNotBlank(refundOrderRequest.getStoreName())) {
            CompanyListRequest request = CompanyListRequest.builder()
                    .storeName(refundOrderRequest.getStoreName())
                    .build();
            refundOrderRequest.setCompanyInfoIds(customerCommonService.listCompanyInfoIdsByCondition(request));
            if (CollectionUtils.isEmpty(refundOrderRequest.getCompanyInfoIds())) {
                supplierLike = false;
            }
        }
        //模糊会员名称
        boolean customerLike = true;
        if (StringUtils.isNotBlank(refundOrderRequest.getCustomerName())) {
            CustomerDetailListByConditionRequest request = CustomerDetailListByConditionRequest.builder().customerName
                    (refundOrderRequest.getCustomerName()).build();
            refundOrderRequest.setCustomerDetailIds(customerCommonService.listCustomerDetailIdsByCondition(request));
            if (CollectionUtils.isEmpty(refundOrderRequest.getCustomerDetailIds())) {
                customerLike = false;
            }
        }
        return supplierLike & customerLike;
    }

    private Specification<RefundOrder> findByRequest(final RefundOrderRequest refundOrderRequest) {
        return (Root<RefundOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> buildWhere(refundOrderRequest,
                root, query, cb);
    }

    /**
     * 构造列表查询的where条件
     *
     * @param refundOrderRequest request
     * @param root               root
     * @param query              query
     * @param cb                 cb
     * @return Predicates
     */
    private Predicate buildWhere(RefundOrderRequest refundOrderRequest, Root<RefundOrder> root,
                                 CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<RefundOrder, RefundBill> refundOrderRefundBillJoin = root.join("refundBill", JoinType.LEFT);
//        refundOrderRefundBillJoin.on(cb.equal(refundOrderRefundBillJoin.get("delFlag"), DeleteFlag.NO));


        if (!StringUtils.isEmpty(refundOrderRequest.getAccountId())) {
            predicates.add(cb.equal(refundOrderRefundBillJoin.get("offlineAccountId"),
                    refundOrderRequest.getAccountId()));
        }

        if (!StringUtils.isEmpty(refundOrderRequest.getReturnOrderCode()) && !StringUtils.isEmpty(refundOrderRequest.getReturnOrderCode().trim())) {
            predicates.add(cb.like(root.get("returnOrderCode"), buildLike(refundOrderRequest.getReturnOrderCode())));
        }

        if (CollectionUtils.isNotEmpty(refundOrderRequest.getCompanyInfoIds())) {
            predicates.add(root.get("supplierId").in(refundOrderRequest.getCompanyInfoIds()));
        }

        if (CollectionUtils.isNotEmpty(refundOrderRequest.getCustomerDetailIds())) {
            predicates.add(root.get("customerDetailId").in(refundOrderRequest.getCustomerDetailIds()));
        }

        if (!CollectionUtils.isEmpty(refundOrderRequest.getReturnOrderCodes())) {
            Path path = root.get("returnOrderCode");
            CriteriaBuilder.In in = cb.in(path);
            for (String returnOrderCode : refundOrderRequest.getReturnOrderCodes()) {
                in.value(returnOrderCode);
            }
            predicates.add(in);
        }

        if (!CollectionUtils.isEmpty(refundOrderRequest.getRefundIds())) {
            Path path = root.get("refundId");
            CriteriaBuilder.In in = cb.in(path);
            for (String refundId : refundOrderRequest.getRefundIds()) {
                in.value(refundId);
            }
            predicates.add(in);
        }

        if (!StringUtils.isEmpty(refundOrderRequest.getRefundBillCode()) && !StringUtils.isEmpty(refundOrderRequest.getRefundBillCode().trim())) {
            predicates.add(cb.like(refundOrderRefundBillJoin.get("refundBillCode"),
                    buildLike(refundOrderRequest.getRefundBillCode())));
        }

        if (Objects.nonNull(refundOrderRequest.getPayChannelId())) {
            predicates.add(cb.equal(refundOrderRefundBillJoin.get("payChannelId"),
                    refundOrderRequest.getPayChannelId()));
        }

        if (Objects.nonNull(refundOrderRequest.getPayType())) {
            predicates.add(cb.equal(root.get("payType"), refundOrderRequest.getPayType()));
        }

        //待商家退款，拒绝退款的订单平台不应该看到
        predicates.add(cb.notEqual(root.get("refundStatus"), RefundStatus.TODO.toValue()));
        predicates.add(cb.notEqual(root.get("refundStatus"), RefundStatus.REFUSE.toValue()));
        if (Objects.nonNull(refundOrderRequest.getRefundStatus())) {
            predicates.add(cb.equal(root.get("refundStatus"), refundOrderRequest.getRefundStatus()));
        }

        //收款开始时间
        if (!StringUtils.isEmpty(refundOrderRequest.getBeginTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            predicates.add(cb.greaterThanOrEqualTo(refundOrderRefundBillJoin.get("createTime"),
                    LocalDateTime.of(LocalDate
                            .parse(refundOrderRequest.getBeginTime(), formatter), LocalTime.MIN)));
        }

        //收款
        if (!StringUtils.isEmpty(refundOrderRequest.getEndTime())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            predicates.add(cb.lessThan(refundOrderRefundBillJoin.get("createTime"),
                    LocalDateTime.of(LocalDate
                            .parse(refundOrderRequest.getEndTime(), formatter), LocalTime.MIN).plusDays(1)));
        }

        //删除条件
        predicates.add(cb.equal(root.get("delFlag"), DeleteFlag.NO));
        predicates.add(cb.equal(refundOrderRefundBillJoin.get("delFlag"), DeleteFlag.NO));

        query.orderBy(cb.desc(root.get("createTime")));

        return cb.and(predicates.toArray(new Predicate[]{}));
    }

    private static String buildLike(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("%").append(XssUtils.replaceLikeWildcard(field)).append("%").toString();
    }


    /**
     * 解析收款账号
     *
     * @param refundOrder refundOrder
     * @return string
     */
    private String parseAccount(RefundOrder refundOrder) {
        StringBuilder accountName = new StringBuilder();
        if (PayType.OFFLINE.equals(refundOrder.getPayType()) && Objects.nonNull(refundOrder.getRefundBill().getOfflineAccountId())) {
            OfflineAccountGetByIdResponse offlineAccount = offlineQueryProvider.getById(new OfflineAccountGetByIdRequest
                    (refundOrder
                            .getRefundBill()
                            .getOfflineAccountId())).getContext();

            if (offlineAccount.getAccountId() != null) {
                log.info("解析收款账号offlineAccount: {}", offlineAccount);
                Integer length = offlineAccount.getAccountName().length();
                accountName.append(offlineAccount.getBankName())
                        .append(" ").append(StringUtils.isNotEmpty(offlineAccount.getBankNo()) ?
                        getDexAccount(offlineAccount.getBankNo()) : "");
            }
        }
        return accountName.toString();
    }

    /**
     * 更新退款单的备注字段
     *
     * @param refundId      id
     * @param refundComment comment
     */
    void updateRefundOrderReason(String refundId, String refundComment) {
        refundOrderRepository.updateRefundOrderReason(refundId, refundComment);
    }

    /**
     * 根据退单编号查询退款单
     *
     * @param returnOrderCode 退单编号
     * @return 退款单信息
     */
    public RefundOrder getRefundOrderByReturnOrderNo(String returnOrderCode) {
        return refundOrderRepository.findAllByReturnOrderCodeAndDelFlag(returnOrderCode, DeleteFlag.NO).orElseGet(() -> null);
    }

    /**
     * 返回掩码后的字符串
     *
     * @param bankNo
     * @return
     */
    public static String getDexAccount(String bankNo) {
        String middle = "**********";
        if (bankNo.length() > 4) {
            if (bankNo.length() <= 8) {
                return middle;
            } else {
                bankNo = bankNo.substring(0, 4) + middle + bankNo.substring(bankNo.length() - 4);
            }
        } else {
            return middle;
        }
        return bankNo;
    }

    /**
     * 自动退款
     *
     * @param tradeList
     * @param returnOrderList
     * @param refundOrderList
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Object> autoRefund(List<Trade> tradeList, List<ReturnOrder> returnOrderList, List<RefundOrder> refundOrderList, Operator operator) {
        List<Object> rsultObject = new ArrayList<>();
        Map<String, RefundOrder> refundOrderMap =
                refundOrderList.stream().collect(Collectors.toMap(RefundOrder::getReturnOrderCode,
                        refundOrder -> refundOrder));
        Map<String, Trade> tradeMap = tradeList.stream().collect(Collectors.toMap(Trade::getId, trade -> trade));
        for (ReturnOrder returnOrder : returnOrderList) {
            RefundOrder refundOrder = refundOrderMap.get(returnOrder.getId());
            //拼团订单-非0元订单退商品总金额
            Trade trade = tradeMap.get(returnOrder.getTid());
            RefundOnlineBO refundOnlineBO =null;
            try {
                refundOrder.setRefundStatus(RefundStatus.FINISH);//推erp需要的。由于李科的ccb退款可能异常，代码returnOrderService.refundOnline移到了下面这个参数
                refundOnlineBO = returnOrderService.buildBoByRefundOnline(returnOrder, refundOrder, operator);//兼容建行退款失败
                //DONE: 退款开关，其他提货退款-无需审核
                //退款
                if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                    RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN)
                            .refund(trade, returnOrder);
                } else {
                    RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.TRADE_RETURN)
                            .refund(trade, returnOrder);
                }

                //添加流水 修改退单状态
                returnOrderService.refundOnline(returnOrder, refundOrder, operator,refundOnlineBO);

                if (TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())){
                    ReturnOrder returnOrderTem = returnOrderService.findById(returnOrder.getId());
                    //同步退货退款信息到价格商品列表 (1已退款)
                    returnOrderService.updateInventoryDetailSamountTrade(returnOrderTem, 1, null);
                    //保存退单
                    returnOrderService.addReturnOrder(returnOrderTem);
                }
                tradeService.cancelTradeByRefund(trade);

            } catch (SbcRuntimeException e) {
                log.info("支付退款异常数据：" + e.getErrorCode() + e.getErrorCode().equals("K-100214"));
                // 已退款 更新退单状态
                if (e.getErrorCode() != null && e.getErrorCode().equals("K-100104")) {
                    returnOrderService.refundOnline(returnOrder, refundOrder, operator,refundOnlineBO);
                } else if (e.getErrorCode() != null && (e.getErrorCode().equals("K-100211") || e.getErrorCode()
                        .equals("K-100212") || e.getErrorCode().equals("K-100214") || e.getErrorCode().equals("K-100215"))) {
                    //K-100211、k-100212编码异常是支付宝、微信自定义异常。表示退款异常，详细信息见异常信息K-100214 招商

                    ReturnFlowState flowState = returnOrder.getReturnFlowState();
                    // 如果已是退款失败状态的订单，不做状态扭转处理
                    if (flowState == ReturnFlowState.REFUND_FAILED) {
                        ReturnOrder returnOrderUpdate = returnOrderRepository.findById(returnOrder.getId()).orElse(null);
                        if (Objects.nonNull(returnOrderUpdate) && !Objects.equals("退款失败", e.getResult())) {
                            returnOrderUpdate.setRefundFailedReason(e.getResult());
                            returnOrderService.updateReturnOrder(returnOrderUpdate);
                        }
                    } else {
                        RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
                        refundOrderRefundRequest.setRid(returnOrder.getId());
                        refundOrderRefundRequest.setFailedReason(e.getResult());
                        refundOrderRefundRequest.setOperator(operator);
                        returnOrderService.refundFailed(refundOrderRefundRequest);
                    }
                    refundOrder.setRefundStatus(RefundStatus.TODO);//把退款记录状态退回去
                    refundOrderRepository.saveAndFlush(refundOrder);
                }
                log.error("refund error,", e);
                //throw e;
            }
        }
        return rsultObject;
    }

//    /**
//     * 自动退款(备份)
//     *
//     * @param tradeList
//     * @param returnOrderList
//     * @param refundOrderList
//     * @param operator
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public List<Object> autoRefundBK(List<Trade> tradeList, List<ReturnOrder> returnOrderList, List<RefundOrder> refundOrderList, Operator operator) {
//        List<Object> rsultObject = new ArrayList<>();
//        Map<String, RefundOrder> refundOrderMap =
//                refundOrderList.stream().collect(Collectors.toMap(RefundOrder::getReturnOrderCode,
//                        refundOrder -> refundOrder));
//        Map<String, Trade> tradeMap = tradeList.stream().collect(Collectors.toMap(Trade::getId, trade -> trade));
//        for (ReturnOrder returnOrder : returnOrderList) {
//            RefundOrder refundOrder = refundOrderMap.get(returnOrder.getId());
//            RefundRequest refundRequest = new RefundRequest();
//            //拼团订单-非0元订单退商品总金额
//            Trade trade = tradeMap.get(returnOrder.getTid());
//
////            //是否是好友代付，好友代付，原路返回
////            if (trade.getOrderSource() == OrderSource.SUPPLIER) {
////                refundRequest.setOrderSource(1);
////            }
//
//            refundRequest.setAmount(returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice()
//                    .getApplyPrice() : refundOrder.getReturnPrice());
//            refundRequest.setBusinessId(trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId());
//            refundRequest.setRefundBusinessId(returnOrder.getId());
//            // 拼接描述信息
//            String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
//                    () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
//            if (trade.getTradeItems().size() > 1) {
//                body = body + " 等多件商品";
//            }
//            refundRequest.setDescription(body);
//            refundRequest.setClientIp("127.0.0.1");
//            refundRequest.setStoreId(trade.getSupplier().getStoreId());
//            if(Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())){
//                refundRequest.setRefund(true);//支持原路返回
//            }
//            Object object;
//            try {
//                // 退款金额等于0 直接添加流水 修改退单状态
//                if (refundRequest.getAmount().compareTo(BigDecimal.ZERO) == 0) {
//                    returnOrderService.refundOnline(returnOrder, refundOrder, operator);
//                } else {
//                    BigDecimal totalPrice = returnOrder.getReturnPrice().getApplyPrice();
//                    PayTradeRecordResponse context = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(refundRequest.getBusinessId())).getContext();
//                    if (Objects.nonNull(context)) {
//                        totalPrice = context.getPracticalPrice();
//                        refundRequest.setTotalPrice(totalPrice);
//                        // 调用网关退款，退款公用接口
//                        object = payProvider.refund(refundRequest).getContext().getObject();
//                        log.info("payProvider.refund(refundRequest)" + JSONObject.toJSONString(object));
//                    } else {
//                        object = null;
//                        //推送到金蝶
//                        //delayPushingPayment(trade);
////                        returnOrderService.refundOnline(returnOrder, refundOrder, operator);
//                    }
//
//
//                    //支付宝退款没有回调方法，故支付宝的交易流水在此添加
//                    //TODO
//                    log.info("好友代付自动退款66666：{},{}", null != context ? context.getChannelItemId() : "channel is null", trade.getId());
//                    if (object != null) {
////                        Map result = (Map) object;
//                        //余额退款不需要回调,如果为微信的好友代付就不走下面的逻辑
//                        if (null != context && !context.getChannelItemId().equals(14L) && !context.getChannelItemId().equals(15L) &&
//                                !context.getChannelItemId().equals(16L)) {
//                            //TODO
//                            log.info("好友代付自动退款77777：{},{}", context.getChannelItemId(), context.getChannelItemId().equals(16L));
//                            //退款到鲸贴(如果好友代付)
//                            if(!refundRequest.isRefund()){
//                                modifyWalletBalanceForRefund(trade, returnOrder, refundOrder);
//                            }
//                            returnOrderService.refundOnline(returnOrder, refundOrder, operator);
//                            //todo  是否需要两次推送金蝶
//                            //delayPushingPayment(trade);
////                            refundRequest.setBalancePrice(getRefundBalancePrice(trade, returnOrder));
//                        } else {
//                            log.info("异常订单需要手动处理：{},{}", JSONObject.toJSONString(trade), JSONObject.toJSONString(returnOrder));
//                        }
//                    } else if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {//囤货提货单如果没有运费则是属于0元单，没有支付单走这里
//                        //退款到鲸贴(如果好友代付)
//                        modifyWalletBalanceForRefund(trade, returnOrder, refundOrder);
//                        returnOrderService.refundOnline(returnOrder, refundOrder, operator);
//                    }
//
////                        if (result.containsKey("payType") && ("CMB").equals(result.get("payType"))) {
////                            returnOrderService.refundOnline(returnOrder, refundOrder, operator);
////                        }
//                    rsultObject.add(object);
//
//                }
//
//                if (TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())){
//                    ReturnOrder returnOrderTem = returnOrderService.findById(returnOrder.getId());
//                    //同步退货退款信息到价格商品列表 (1已退款)
//                    returnOrderService.updateInventoryDetailSamountTrade(returnOrderTem, 1, null);
//                    //保存退单
//                    returnOrderService.addReturnOrder(returnOrderTem);
//                }
//
////                }
//            } catch (SbcRuntimeException e) {
//                log.info("支付退款异常数据：" + e.getErrorCode() + e.getErrorCode().equals("K-100214"));
//                // 已退款 更新退单状态
//                if (e.getErrorCode() != null && e.getErrorCode().equals("K-100104")) {
//                    returnOrderService.refundOnline(returnOrder, refundOrder, operator);
//                } else if (e.getErrorCode() != null && (e.getErrorCode().equals("K-100211") || e.getErrorCode()
//                        .equals("K-100212") || e.getErrorCode().equals("K-100214"))) {
//                    //K-100211、k-100212编码异常是支付宝、微信自定义异常。表示退款异常，详细信息见异常信息K-100214 招商
//                    RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
//                    refundOrderRefundRequest.setRid(returnOrder.getId());
//                    refundOrderRefundRequest.setFailedReason(e.getResult());
//                    refundOrderRefundRequest.setOperator(operator);
//                    returnOrderService.refundFailed(refundOrderRefundRequest);
//                }
//                log.error("refund error,", e);
//                //throw e;
//            }
//        }
//        return rsultObject;
//    }

    /**
     * 退款到余额
     *
     * @param trade
     * @param returnOrder
     * @param refundOrder
     */
    public void modifyWalletBalanceForRefund(Trade trade, ReturnOrder returnOrder, RefundOrder refundOrder) {
        //线下付款也是走此退至鲸币，所以trade包含了O单和OPK单，现在需要，O单不退至鲸币，OPK单还退至鲸币，所以过滤掉O单即可
        if(Objects.nonNull(trade.getActivityType()) && TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())){
           return;
        }
        BigDecimal amount = returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice().getApplyPrice() : refundOrder.getReturnPrice();
        TradePrice tradePrice = trade.getTradePrice();
        BigDecimal reduceSplitPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        String tradePriceRemark = "商品费用:" + "(" + reduceSplitPrice + ")" + "-鲸币抵扣:" + "(" + tradePrice.getBalancePrice() + ")"
                + "-运费:" + "(" + tradePrice.getDeliveryPrice() + ")" + "-包装费用:" + "(" + tradePrice.getPackingPrice() + ")";

        //用户余额信息
        CusWalletVO cusWalletVO = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(trade.getBuyer().getId()).build())
                .getContext().getCusWalletVO();

        AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
        request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        //客户账号
        request.setCustomerAccount(cusWalletVO.getCustomerAccount());
        log.info("========================退款的账号：{}", cusWalletVO.getCustomerAccount());
        request.setRelationOrderId(refundOrder.getReturnOrderCode());
        request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
        request.setBudgetType(BudgetType.INCOME);
        request.setDealPrice(amount);
        request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
        request.setCurrentBalance(cusWalletVO.getBalance());
        request.setTradeState(TradeStateEnum.PAID);
        request.setPayType(1);
//                            request.setBalance(amount.add(refundRequest.getBalancePrice()).add(customerWalletVO.getBalance()));
        request.setBalance(amount.add(cusWalletVO.getBalance()));
        //walletRecordProvider.addWalletRecord(request);

        WalletRecordVO returnWalletRecordVO = walletMerchantProvider.merchantGiveUser(CustomerWalletGiveRequest.builder()
                .customerId(request.getCustomerId())
                .customerAccount(request.getCustomerAccount())
                .opertionType(0)
                .storeId(trade.getSupplier().getStoreId().toString())
                .relationOrderId(request.getRelationOrderId())
                .remark(request.getRemark())
                .tradeRemark(request.getTradeRemark())
                .dealTime(LocalDateTime.now())
                .balance(request.getDealPrice())
                .walletRecordTradeType(WalletRecordTradeType.BALANCE_REFUND)
                .build()).getContext();
    }

    /**
     * 获取余额退款总额
     *
     * @param trade
     * @param returnOrder
     * @return
     */
    private BigDecimal getRefundBalancePrice(Trade trade, ReturnOrder returnOrder) {
        BigDecimal balancePrice = BigDecimal.ZERO;
        List<ReturnItem> returnItems = returnOrder.getReturnItems();
        List<TradeItem> tradeItems = trade.getTradeItems();

        List<String> returnItemSkuIds = returnItems.stream().map(ReturnItem::getSkuId).collect(Collectors.toList());
        List<TradeItem> returnItem = tradeItems.stream().filter(tradeItem -> returnItemSkuIds.contains(tradeItem.getSkuId())).collect(Collectors.toList());

        balancePrice = returnItem
                .stream().flatMap(tradeItem -> tradeItem.getWalletSettlements().stream()).collect(Collectors.toList())
                .stream().map(TradeItem.WalletSettlement::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        return balancePrice;
    }


    /**
     * 自动退款
     *
     * @param tradeList
     * @param returnOrderList
     * @param refundOrderList
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Object> autoRefundNewPile(List<NewPileTrade> tradeList, List<NewPileReturnOrder> returnOrderList, List<RefundOrder> refundOrderList, Operator operator) {
        List<Object> rsultObject = new ArrayList<>();
        Map<String, RefundOrder> refundOrderMap =
                refundOrderList.stream().collect(Collectors.toMap(RefundOrder::getReturnOrderCode,
                        refundOrder -> refundOrder));
        Map<String, NewPileTrade> tradeMap = tradeList.stream().collect(Collectors.toMap(NewPileTrade::getId, trade -> trade));

        for (NewPileReturnOrder returnOrder : returnOrderList) {
            RefundOrder refundOrder = refundOrderMap.get(returnOrder.getId());
            //拼团订单-非0元订单退商品总金额
            NewPileTrade trade = tradeMap.get(returnOrder.getTid());
            try {

                //DONE: 退款开关，囤货退款-退单无审核 线上支付
                //退款
                RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_RETURN)
                        .refund(trade, returnOrder);

                //添加流水 修改退单状态
                returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
            } catch (SbcRuntimeException e) {
                log.info("支付退款异常数据：" + e.getErrorCode() + e.getErrorCode().equals("K-100214"));
                // 已退款 更新退单状态
                if (e.getErrorCode() != null && e.getErrorCode().equals("K-100104")) {
                    returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
                } else if (e.getErrorCode() != null && (e.getErrorCode().equals("K-100211") || e.getErrorCode()
                        .equals("K-100212") || e.getErrorCode().equals("K-100214") || e.getErrorCode().equals("K-100215"))) {
                    //K-100211、k-100212编码异常是支付宝、微信自定义异常。表示退款异常，详细信息见异常信息K-100214 招商
                    RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
                    refundOrderRefundRequest.setRid(returnOrder.getId());
                    refundOrderRefundRequest.setFailedReason(e.getResult());
                    refundOrderRefundRequest.setOperator(operator);
                    returnOrderService.refundFailed(refundOrderRefundRequest);
                }
                log.error("refund error,", e);
                //throw e;
            }
        }
        return rsultObject;
    }

//    /**
//     * 自动退款(备份)
//     *
//     * @param tradeList
//     * @param returnOrderList
//     * @param refundOrderList
//     * @param operator
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public List<Object> autoRefundNewPileBK(List<NewPileTrade> tradeList, List<NewPileReturnOrder> returnOrderList, List<RefundOrder> refundOrderList, Operator operator) {
//        List<Object> rsultObject = new ArrayList<>();
//        Map<String, RefundOrder> refundOrderMap =
//                refundOrderList.stream().collect(Collectors.toMap(RefundOrder::getReturnOrderCode,
//                        refundOrder -> refundOrder));
//        Map<String, NewPileTrade> tradeMap = tradeList.stream().collect(Collectors.toMap(NewPileTrade::getId, trade -> trade));
//        for (NewPileReturnOrder returnOrder : returnOrderList) {
//            RefundOrder refundOrder = refundOrderMap.get(returnOrder.getId());
//            RefundRequest refundRequest = new RefundRequest();
//            //拼团订单-非0元订单退商品总金额
//            NewPileTrade trade = tradeMap.get(returnOrder.getTid());
//
//            //是否是好友代付，好友代付，原路返回
//            if (trade.getOrderSource() == OrderSource.SUPPLIER) {
//                refundRequest.setOrderSource(1);
//            }
//
//            refundRequest.setAmount(returnOrder.getReturnPrice().getApplyStatus() ? returnOrder.getReturnPrice()
//                    .getApplyPrice() : refundOrder.getReturnPrice());
//            refundRequest.setBusinessId(trade.getPayInfo().isMergePay() ? trade.getParentId() : trade.getId());
//            refundRequest.setRefundBusinessId(returnOrder.getId());
//            // 拼接描述信息
//            String body = trade.getTradeItems().get(0).getSkuName() + " " + (trade.getTradeItems().get(0).getSpecDetails
//                    () == null ? "" : trade.getTradeItems().get(0).getSpecDetails());
//            if (trade.getTradeItems().size() > 1) {
//                body = body + " 等多件商品";
//            }
//            refundRequest.setDescription(body);
//            refundRequest.setClientIp("127.0.0.1");
//            refundRequest.setStoreId(trade.getSupplier().getStoreId());
//            Object object;
//            try {
//                // 退款金额等于0 直接添加流水 修改退单状态
//                if (refundRequest.getAmount().compareTo(BigDecimal.ZERO) == 0) {
//                    returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
//                } else {
//                    BigDecimal totalPrice = returnOrder.getReturnPrice().getApplyPrice();
//                    PayTradeRecordResponse context = payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(refundRequest.getBusinessId())).getContext();
//                    if (Objects.nonNull(context)) {
//                        totalPrice = context.getPracticalPrice();
//                        refundRequest.setTotalPrice(totalPrice);
//                        // 调用网关退款，退款公用接口
//                        object = payProvider.refund(refundRequest).getContext().getObject();
//                        log.info("payProvider.refund(refundRequest)" + object);
//                    } else {
//                        object = null;
//                        //推送到金蝶
//                        //delayPushingPayment(trade);
//                        returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
//                    }
//
//
//                    //支付宝退款没有回调方法，故支付宝的交易流水在此添加
//                    if (object != null) {
////                        Map result = (Map) object;
//                        //余额退款不需要回调,如果为微信的好友代付就不走下面的逻辑
//                        if (trade.getOrderSource() != OrderSource.SUPPLIER) {
//                            returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
//                            //todo  是否需要两次推送金蝶
//                            //delayPushingPayment(trade);
//
//                            BalanceByCustomerIdResponse balanceByCustomerIdResponse = walletQueryProvider.getBalanceByCustomerId(WalletByCustomerIdQueryRequest.builder().customerId(
//                                    trade.getBuyer().getId()).build()).getContext();
//
//                            TradePrice tradePrice = trade.getTradePrice();
//                            BigDecimal reduceSplitPrice = trade.getTradeItems().stream().map(TradeItem::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
//                            String tradePriceRemark = "商品费用:" + "(" + reduceSplitPrice + ")" + "-鲸币抵扣:" + "(" + tradePrice.getBalancePrice() + ")"
//                                    + "-运费:" + "(" + tradePrice.getDeliveryPrice() + ")" + "-包装费用:" + "(" + tradePrice.getPackingPrice() + ")";
//
//                            AddWalletRecordRecordRequest request = new AddWalletRecordRecordRequest();
//                            request.setTradeRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
//                            //客户账号
//                            request.setCustomerAccount(balanceByCustomerIdResponse.getCustomerWalletVO().getCustomerAccount());
//                            log.info("========================退款的账号：{}", balanceByCustomerIdResponse.getCustomerWalletVO().getCustomerAccount());
//                            request.setRelationOrderId(refundOrder.getReturnOrderCode());
//                            request.setTradeType(WalletRecordTradeType.BALANCE_REFUND);
//                            request.setBudgetType(BudgetType.INCOME);
//                            request.setDealPrice(refundRequest.getAmount());
//                            request.setRemark(WalletDetailsType.INCREASE_ORDER_REFUND.getDesc() + "-" + refundOrder.getReturnOrderCode() + "-" + tradePriceRemark);
//                            request.setCurrentBalance(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance());
//                            request.setTradeState(TradeStateEnum.PAID);
//                            request.setPayType(1);
//                            request.setBalance(refundRequest.getAmount().add(balanceByCustomerIdResponse.getCustomerWalletVO().getBalance()));
//                            walletRecordProvider.addWalletRecord(request);
//                            customerWalletProvider.addAmount(CustomerAddAmountRequest.builder()
//                                    .amount(refundRequest.getAmount())
//                                    .customerId(trade.getBuyer().getId())
//                                    .businessId(trade.getId())
//                                    .customerAccount(trade.getBuyer().getAccount())
//                                    .build());
//                        }
//
//                    }
//
////                        if (result.containsKey("payType") && ("CMB").equals(result.get("payType"))) {
////                            returnOrderService.refundOnline(returnOrder, refundOrder, operator);
////                        }
//                    rsultObject.add(object);
//
//                }
//
////                }
//            } catch (SbcRuntimeException e) {
//                log.info("支付退款异常数据：" + e.getErrorCode() + e.getErrorCode().equals("K-100214"));
//                // 已退款 更新退单状态
//                if (e.getErrorCode() != null && e.getErrorCode().equals("K-100104")) {
//                    returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);
//                } else if (e.getErrorCode() != null && (e.getErrorCode().equals("K-100211") || e.getErrorCode()
//                        .equals("K-100212") || e.getErrorCode().equals("K-100214"))) {
//                    //K-100211、k-100212编码异常是支付宝、微信自定义异常。表示退款异常，详细信息见异常信息K-100214 招商
//                    RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
//                    refundOrderRefundRequest.setRid(returnOrder.getId());
//                    refundOrderRefundRequest.setFailedReason(e.getResult());
//                    refundOrderRefundRequest.setOperator(operator);
//                    returnOrderService.refundFailed(refundOrderRefundRequest);
//                }
//                log.error("refund error,", e);
//                //throw e;
//            }
//        }
//        return rsultObject;
//    }

    /**
     * 囤货自动退款
     *
     * @param pileTradeList
     * @param returnPileOrderList
     * @param refundOrderList
     * @param operator
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Object> autoRefundPile(List<PileTrade> pileTradeList, List<ReturnPileOrder> returnPileOrderList,
                                       List<RefundOrder> refundOrderList, Operator operator) {
        List<Object> rsultObject = new ArrayList<>();
        Map<String, RefundOrder> refundOrderMap =
                refundOrderList.stream().collect(Collectors.toMap(RefundOrder::getReturnOrderCode,
                        refundOrder -> refundOrder));
        Map<String, PileTrade> tradeMap = pileTradeList.stream().collect(Collectors.toMap(PileTrade::getId, pileTrade -> pileTrade));
        for (ReturnPileOrder returnPileOrder : returnPileOrderList) {
            RefundOrder refundOrder = refundOrderMap.get(returnPileOrder.getId());
            RefundRequest refundRequest = new RefundRequest();
            //拼团订单-非0元订单退商品总金额
            PileTrade pileTrade = tradeMap.get(returnPileOrder.getTid());
            refundRequest.setAmount(returnPileOrder.getReturnPrice().getApplyStatus() ? returnPileOrder.getReturnPrice()
                    .getApplyPrice() : refundOrder.getReturnPrice());
            refundRequest.setBusinessId(pileTrade.getPayInfo().isMergePay() ? pileTrade.getParentId() : pileTrade.getId());
            refundRequest.setRefundBusinessId(returnPileOrder.getId());
            // 拼接描述信息
            String body = pileTrade.getTradeItems().get(0).getSkuName() + " " + (pileTrade.getTradeItems().get(0).getSpecDetails
                    () == null ? "" : pileTrade.getTradeItems().get(0).getSpecDetails());
            if (pileTrade.getTradeItems().size() > 1) {
                body = body + " 等多件商品";
            }
            refundRequest.setDescription(body);
            refundRequest.setClientIp("127.0.0.1");
            refundRequest.setStoreId(pileTrade.getSupplier().getStoreId());
            Object object;
            try {
                // 退款金额等于0 直接添加流水 修改退单状态
                if (refundRequest.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                    returnPileOrderService.refundOnline(returnPileOrder, refundOrder, operator);
                } else {
                    BigDecimal totalPrice =
                            payQueryProvider.getTradeRecordByOrderCode(new TradeRecordByOrderCodeRequest(refundRequest.getBusinessId()))
                                    .getContext().getPracticalPrice();
                    refundRequest.setTotalPrice(totalPrice);
                    // 调用网关退款，退款公用接口
                    object = payProvider.pileRefund(refundRequest).getContext().getObject();
                    //支付宝退款没有回调方法，故支付宝的交易流水在此添加
                    if (object != null) {
                        Map result = (Map) object;
                        if (result.containsKey("payType") && ("ALIPAY").equals(result.get("payType"))) {
                            returnPileOrderService.refundOnline(returnPileOrder, refundOrder, operator);
                        }
                        //余额退款不需要回调
                        if (result.containsKey("payType") && ("BALANCE").equals(result.get("payType"))) {
                            returnPileOrderService.refundOnline(returnPileOrder, refundOrder, operator);
                            customerFundsProvider.addAmount(CustomerFundsAddAmountRequest.builder()
                                    .amount(refundRequest.getAmount())
                                    .customerId(pileTrade.getBuyer().getId())
                                    .businessId(pileTrade.getId())
                                    .build());

                        }
                    }
                    rsultObject.add(object);
                }
            } catch (SbcRuntimeException e) {
                // 已退款 更新退单状态
                if (e.getErrorCode() != null && e.getErrorCode().equals("K-100104")) {
                    returnPileOrderService.refundOnline(returnPileOrder, refundOrder, operator);
                } else if (e.getErrorCode() != null && (e.getErrorCode().equals("K-100211") || e.getErrorCode()
                        .equals("K-100212") || e.getErrorCode().equals("K-100214") || e.getErrorCode().equals("K-100215"))) {
                    //K-100211、k-100212编码异常是支付宝、微信自定义异常。表示退款异常，详细信息见异常信息
                    RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
                    refundOrderRefundRequest.setRid(returnPileOrder.getId());
                    refundOrderRefundRequest.setFailedReason(e.getResult());
                    refundOrderRefundRequest.setOperator(operator);
                    returnPileOrderService.refundFailed(refundOrderRefundRequest);
                }
                log.error("refund error,", e);
                //throw e;
            }
        }
        return rsultObject;
    }

    @Autowired
    private CcbPayProvider ccbPayProvider;

    public void refundOrderSuccess(String businessId, String rid, Boolean refunded, String msg) {
        Operator operator = Operator.builder().ip(HttpUtil.getIpAddr()).adminId("1").name("system").account("system").platform(Platform.BOSS).build();
        if (!businessId.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)) {

            ReturnOrder returnOrder = returnOrderService.findById(rid);
            if (Objects.nonNull(returnOrder) && Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.REFUND_FAILED)) {
                String tid = returnOrder.getTid();
                Trade trade = tradeService.getById(tid);

                if (refunded) {
                    RefundOrder refundOrder = this.findRefundOrderByReturnOrderNo(rid);
                    returnOrderService.refundOnline(returnOrder, refundOrder, operator);

                    ccbPayProvider.addRefundAmt(tid, trade.getPayOrderNo(), returnOrder.getId());

                    if (TradeActivityTypeEnum.TRADE.toActivityType().equals(trade.getActivityType())) {
                        ReturnOrder returnOrderTem = returnOrderService.findById(returnOrder.getId());
                        //同步退货退款信息到价格商品列表 (1已退款)
                        returnOrderService.updateInventoryDetailSamountTrade(returnOrderTem, 1, null);
                        //保存退单
                        returnOrderService.addReturnOrder(returnOrderTem);
                    }

                    AbstractRefund<Trade, ReturnOrder> tradeRefundImpl;
                    if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                        tradeRefundImpl = RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.NEW_PICK_RETURN);
                    } else {
                        tradeRefundImpl = RefundFactory.getTradeRefundImpl(RefundFactory.TradeRefundType.TRADE_RETURN);
                    }

                    Map<String, List<RefundByChannelRequest.RefundItem>> channelRefundMap = tradeRefundImpl.getRefundMapForReturn(trade, returnOrder);

                    List<RefundByChannelRequest.RefundItem> amountGtZeroRefundItems = channelRefundMap.values().stream().flatMap(Collection::stream)
                            .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(amountGtZeroRefundItems)) {
                        //汇总在线退款总金额，余额退款总金额，保存到退单
                        tradeRefundImpl.saveTotalChannelRefundAmount(returnOrder, channelRefundMap);
                    }

                    Map<String, List<RefundByChannelRequest.RefundItem>> balanceReturnMap = channelRefundMap.entrySet().stream()
                            .map(entry -> {
                                String key = entry.getKey();
                                List<RefundByChannelRequest.RefundItem> filteredItems = entry.getValue().stream()
                                        .filter(item -> item.getRefundType() != RefundType.ONLINE)
                                        .collect(Collectors.toList());
                                return new AbstractMap.SimpleEntry<>(key, filteredItems);
                            })
                            .filter(entry -> !entry.getValue().isEmpty())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    List<RefundByChannelRequest.RefundItem> balanceItems = balanceReturnMap.values().stream().flatMap(Collection::stream)
                            .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(balanceItems)) {
                        // 查询是否有退鲸币记录
                        Boolean exist = walletRecordProvider.queryReturnOrderExistWalletRecord(returnOrder.getId()).getContext();
                        log.info("建行退款回调退单是否存在退鲸币记录：{}", exist);
                        if (!exist) {
                            // 退金币
                            payProvider.refundByChannel(
                                    RefundByChannelRequest
                                            .builder()
                                            .payTypeRefundItemsByOrderMap(balanceReturnMap)
                                            .build()
                            );
                        }
                    }

                    returnOrderService.pushKingdeeEntry(trade, returnOrder, refundOrder);
                    String returnOrderId = returnOrder.getId();
                    String tradeId = trade.getId();
                    String sendStr = tradeId + "#" + returnOrderId;
                    // 非自营订单 退款 佣金部分 推送金蝶
                    if (wmsAPIFlag && Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                        log.info("非自营商家，佣金部分推送ERP退款单：{}", sendStr);
                        orderProducerService.pushRefundCommisionToKingdee(sendStr, 60 * 1000L);
                    }

                    if (Objects.equals(trade.getPayWay(), PayWay.CCB) && DeliverWay.isTmsDelivery(trade.getDeliverWay())) {
                        log.info("订单退款,处理运费加收，发送消息：{}", sendStr);
                        orderProducerService.pushRefundExtra(sendStr, 5 * 1000L);
                        tradeService.cancelTradeByRefund(trade);
                    }

                } else {
                    ReturnFlowState flowState = returnOrder.getReturnFlowState();
                    // 如果已是退款失败状态的订单，不做状态扭转处理
                    if (flowState == ReturnFlowState.REFUND_FAILED) {
                        ReturnOrder returnOrderUpdate = returnOrderRepository.findById(returnOrder.getId()).orElse(null);
                        if (Objects.nonNull(returnOrderUpdate)) {
                            String failedMsg = StringUtils.isBlank(msg) ? "建行：退款失败" : msg;
                            returnOrderUpdate.setRefundFailedReason(failedMsg);
                            returnOrderService.updateReturnOrder(returnOrderUpdate);
                        }
                    }
                }

            } else {
                log.info("建行退款通知，退单不存在。退单号{}", rid);
            }

        }else {
            // 囤货
            NewPileReturnOrder returnOrder = newPileReturnOrderService.findById(rid);
            if (Objects.nonNull(returnOrder)) {
                String tid = returnOrder.getTid();
                NewPileTrade trade = newPileTradeService.getNewPileTradeById(tid);

                if (refunded) {
                    RefundOrder refundOrder = this.findRefundOrderByReturnOrderNo(rid);
                    //添加流水 修改退单状态
                    returnOrderService.refundOnlineNewPile(returnOrder, refundOrder, operator);

                    ccbPayProvider.addRefundAmt(tid, trade.getPayOrderNo(), returnOrder.getId());

                    AbstractRefund<NewPileTrade, NewPileReturnOrder> newPileRefundImpl = RefundFactory.getNewPileRefundImpl(RefundFactory.NewPileRefundType.NEW_PILE_RETURN);
                    Map<String, List<RefundByChannelRequest.RefundItem>> channelRefundMap = newPileRefundImpl.getRefundMapForReturn(trade, returnOrder);

                    List<RefundByChannelRequest.RefundItem> amountGtZeroRefundItems = channelRefundMap.values().stream().flatMap(Collection::stream)
                            .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(amountGtZeroRefundItems)) {
                        //汇总在线退款总金额，余额退款总金额，保存到退单
                        newPileRefundImpl.saveTotalChannelRefundAmount(returnOrder, channelRefundMap);
                    }

                    Map<String, List<RefundByChannelRequest.RefundItem>> balanceReturnMap = channelRefundMap.entrySet().stream()
                            .map(entry -> {
                                String key = entry.getKey();
                                List<RefundByChannelRequest.RefundItem> filteredItems = entry.getValue().stream()
                                        .filter(item -> item.getRefundType() != RefundType.ONLINE)
                                        .collect(Collectors.toList());
                                return new AbstractMap.SimpleEntry<>(key, filteredItems);
                            })
                            .filter(entry -> !entry.getValue().isEmpty())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                    List<RefundByChannelRequest.RefundItem> balanceItems = balanceReturnMap.values().stream().flatMap(Collection::stream)
                            .filter(it -> Objects.nonNull(it.getRefundAmount()) && it.getRefundAmount().compareTo(BigDecimal.ZERO) > 0)
                            .collect(Collectors.toList());

                    if (CollectionUtils.isNotEmpty(balanceItems)) {
                        // 查询是否有退鲸币记录
                        Boolean exist = walletRecordProvider.queryReturnOrderExistWalletRecord(returnOrder.getId()).getContext();
                        log.info("建行退款回调退单是否存在退鲸币记录：{}", exist);
                        if (!exist) {
                            // 退鲸币
                            payProvider.refundByChannel(
                                    RefundByChannelRequest
                                            .builder()
                                            .payTypeRefundItemsByOrderMap(balanceReturnMap)
                                            .build()
                            );
                        }
                    }

                    returnOrderService.pushKingdeeEntryPile(trade,returnOrder,refundOrder);
                    // 订单 退款 佣金部分 推送金蝶
                    if (wmsAPIFlag && Objects.equals(trade.getPayWay(), PayWay.CCB)) {
                        String returnOrderId = returnOrder.getId();
                        String tradeId = trade.getId();
                        String sendStr = tradeId + "#" + returnOrderId;
                        log.info("非自营商家，佣金部分推送ERP退款单：{}", sendStr);
                        orderProducerService.pushRefundCommisionToKingdee(sendStr,  60 * 1000L);
                    }
                }else {
                    RefundOrderRefundRequest refundOrderRefundRequest = new RefundOrderRefundRequest();
                    refundOrderRefundRequest.setRid(returnOrder.getId());
                    refundOrderRefundRequest.setFailedReason(msg);
                    refundOrderRefundRequest.setOperator(operator);
                    returnOrderService.refundFailed(refundOrderRefundRequest);
                }
            }else {
                log.info("建行退款通知，退单不存在。退单号{}", rid);
            }
        }
    }

}
