// package com.wanmi.sbc.returnorder.mq;
//
// import com.alibaba.fastjson.JSONObject;
// import com.wanmi.sbc.account.bean.enums.PayWay;
// import com.wanmi.sbc.common.enums.DefaultFlag;
// import com.wanmi.sbc.common.enums.DeleteFlag;
// import com.wanmi.sbc.common.util.DateUtil;
// import com.wanmi.sbc.common.util.GeneratorService;
// import com.wanmi.sbc.common.util.KsBeanUtil;
// import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
// import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
// import com.wanmi.sbc.customer.api.request.employee.EmployeeHandoverRequest;
// import com.wanmi.sbc.goods.bean.enums.DeliverWay;
// import com.wanmi.sbc.returnorder.api.constant.JmsDestinationConstants;
// import com.wanmi.sbc.returnorder.api.request.trade.HistoryLogisticCompanyRequest;
// import com.wanmi.sbc.returnorder.bean.enums.NewPileReturnFlowState;
// import com.wanmi.sbc.returnorder.bean.enums.PayState;
// import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
// import com.wanmi.sbc.returnorder.bean.enums.ReturnType;
// import com.wanmi.sbc.returnorder.historylogisticscompany.model.root.HistoryLogisticsCompany;
// import com.wanmi.sbc.returnorder.historylogisticscompany.service.HistoryLogisticsCompanyService;
// import com.wanmi.sbc.returnorder.payorder.response.PayOrderResponse;
// import com.wanmi.sbc.returnorder.returnorder.model.root.NewPileReturnOrder;
// import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrder;
// import com.wanmi.sbc.returnorder.returnorder.request.ReturnQueryRequest;
// import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderService;
// import com.wanmi.sbc.returnorder.returnorder.service.newPileOrder.NewPileReturnOrderService;
// import com.wanmi.sbc.returnorder.trade.model.entity.TradeDeliver;
// import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
// import com.wanmi.sbc.returnorder.trade.model.root.Trade;
// import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
// import com.wanmi.sbc.returnorder.trade.service.TradeService;
// import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
// import com.wanmi.sbc.pay.api.provider.CcbPayProvider;
// import com.wanmi.sbc.pay.api.response.CcbPayOrderRecordResponse;
// import com.wanmi.sbc.pay.api.response.CcbPayRecordResponse;
// import com.wanmi.sbc.pay.api.response.CcbRefundRecordResponse;
// import com.wanmi.sbc.returnorder.shopcart.*;
// import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsBaseSiteProvider;
// import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsBaseSiteRequest;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang3.StringUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.stream.annotation.EnableBinding;
// import org.springframework.cloud.stream.annotation.StreamListener;
// import org.springframework.data.domain.Page;
// import org.springframework.stereotype.Service;
//
// import java.math.BigDecimal;
// import java.math.RoundingMode;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Objects;
// import java.util.Optional;
//
// @Service
// @Slf4j
// @EnableBinding(OrderSink.class)
// public class OrderConsumerService {
//
//     @Autowired
//     private TradeService tradeService;
//
//     @Autowired
//     private ReturnOrderService returnOrderService;
//
//     @Autowired
//     private HistoryLogisticsCompanyService historyLogisticsCompanyService;
//
//     @Autowired
//     private ShopCartRepository shopCartRepository;
//
//     @Autowired
//     private ShopCartNewPileTradeRepository shopCartNewPileTradeRepository;
//
//     @Autowired
//     private RetailShopCartRepository retailShopCartRepository;
//
//     @Autowired
//     private BulkShopCartRepository bulkShopCartRepository;
//
//     @Autowired
//     private NewPileTradeService newPileTradeService;
//
//     @Autowired
//     private CcbPayProvider ccbPayProvider;
//
//     @Autowired
//     private NewPileReturnOrderService newPileReturnOrderService;
//
//     @Autowired
//     private CompanyInfoQueryProvider companyInfoQueryProvider;
//
//     @Autowired
//     private LogisticsBaseSiteProvider logisticsBaseSiteProvider;
//
//     @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_MODIFY_EMPLOYEE_DATA)
//     public void modifyEmployeeData(EmployeeHandoverRequest request){
//         Integer pageNum = 0;
//         Integer pageSize = 1000;
//         while(true){
//             TradeQueryRequest tradeQueryRequest = TradeQueryRequest.builder().employeeIds(request.getEmployeeIds()).build();
//             tradeQueryRequest.setPageNum(pageNum);
//             tradeQueryRequest.setPageSize(pageSize);
//             Page<Trade> page = tradeService.page(tradeQueryRequest.getWhereCriteria(), tradeQueryRequest);
//             if(page.getTotalElements() == 0){
//                 break;
//             }
//             List<Trade> tradeList = page.getContent();
//             tradeList.stream().forEach(trade -> {
//                 tradeService.updateEmployeeId(request.getNewEmployeeId(), trade.getBuyer().getId());
//             });
//             log.info("业务员交接订单数量：" + tradeList.size());
//             pageNum ++;
//         }
//
//         while(true){
//             ReturnQueryRequest returnQueryRequest = new ReturnQueryRequest();
//             returnQueryRequest.setEmployeeIds(request.getEmployeeIds());
//             Page<ReturnOrder> page = returnOrderService.page(returnQueryRequest);
//             if(page.getTotalElements() == 0){
//                 break;
//             }
//             List<ReturnOrder> returnOrders = page.getContent();
//             returnOrders.stream().forEach(returnOrder -> {
//                 returnOrderService.updateEmployeeId(request.getNewEmployeeId(), returnOrder.getBuyer().getId());
//             });
//             log.info("业务员交接订单数量：" + returnOrders.size());
//             pageNum ++;
//         }
//
//     }
//
//
//     @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_ADD_CUSTOMER)
//     public void pushShopCarGoods(ShopCartVO shopCart){
//
//         if(Objects.isNull(shopCart)){
//             return;
//         }
//         log.info("购物车======="+ JSONObject.toJSONString(shopCart));
//         if (!shopCart.getIsTunhuo()){
//             if (Boolean.FALSE.equals(shopCart.getIsDelFlag())){
//                 ShopCart convert = KsBeanUtil.convert(shopCart, ShopCart.class);
//                 Optional<ShopCart> byId = shopCartRepository.findById(convert.getCartId());
//                 if(byId.isPresent()){
//                     convert.setCreateTime(byId.get().getCreateTime());
//                 }else{
//                     convert.setCreateTime(LocalDateTime.now());
//                 }
//                 convert.setIsCheck(DefaultFlag.YES);
//                 shopCartRepository.save(convert);
//             } else {
//                 if(Objects.nonNull(shopCart.getCartId())){
//                     shopCartRepository.deleteByCartId(shopCart.getCartId());
//                 }
//             }
//         }else {
//             this.pushShopCarStoreGoods(shopCart);
//         }
//
//     }
//
//
//
//     //    @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_STORE_ADD_CUSTOMER)
//     public void pushShopCarStoreGoods(ShopCartVO shopCart){
//
//         if(Objects.isNull(shopCart)){
//             return;
//         }
//         log.info("囤货购物车======="+ JSONObject.toJSONString(shopCart));
//
//         if (Boolean.FALSE.equals(shopCart.getIsDelFlag())){
//             ShopCart convert = KsBeanUtil.convert(shopCart, ShopCart.class);
//             Optional<ShopCartNewPileTrade> byId = shopCartNewPileTradeRepository.findById(convert.getCartId());
//             if(byId.isPresent()){
//                 convert.setCreateTime(byId.get().getCreateTime());
//             }else{
//                 convert.setCreateTime(LocalDateTime.now());
//             }
//             convert.setIsCheck(DefaultFlag.YES);
//             shopCartNewPileTradeRepository.save(KsBeanUtil.convert(convert,ShopCartNewPileTrade.class));
//         } else {
//             if(Objects.nonNull(shopCart.getCartId())){
//                 shopCartNewPileTradeRepository.deleteByCartId(shopCart.getCartId());
//             }
//         }
//     }
//
//
//
//
//     @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_RETAIL_ADD_CUSTOMER)
//     public void pushRetailShopCarGoods(RetailShopCartVo retailShopCartVo){
//
//         if(Objects.isNull(retailShopCartVo)){
//             return;
//         }
//         log.info("购物车======="+ JSONObject.toJSONString(retailShopCartVo));
//
//         if (Boolean.FALSE.equals(retailShopCartVo.getIsDelFlag())){
//             RetailShopCart convert = KsBeanUtil.convert(retailShopCartVo, RetailShopCart.class);
//             Optional<RetailShopCart> byId = retailShopCartRepository.findById(convert.getCartId());
//             if(byId.isPresent()){
//                 convert.setCreateTime(byId.get().getCreateTime());
//             }else{
//                 convert.setCreateTime(LocalDateTime.now());
//             }
//             convert.setIsCheck(DefaultFlag.YES);
//             log.info("最终插入数据========"+convert);
//             if (Objects.isNull(convert.getCartId())){
//                 convert.setCartId(retailShopCartVo.getCartId());
//             }
//             retailShopCartRepository.save(convert);
//         } else {
//             if(Objects.nonNull(retailShopCartVo.getCartId())){
//                 retailShopCartRepository.deleteByCartId(retailShopCartVo.getCartId());
//             }
//         }
//     }
//
//     @StreamListener(JmsDestinationConstants.Q_SHOP_CAR_BULK_ADD_CUSTOMER)
//     public void pushShopCarGoods(BulkShopCartVO bulkShopCartVO){
//
//         if(Objects.isNull(bulkShopCartVO)){
//             return;
//         }
//         log.info("购物车======="+ JSONObject.toJSONString(bulkShopCartVO));
//
//         if (Boolean.FALSE.equals(bulkShopCartVO.getIsDelFlag())){
//             BulkShopCart bulkShopCart = KsBeanUtil.convert(bulkShopCartVO, BulkShopCart.class);
//
//             Optional<BulkShopCart> byId = bulkShopCartRepository.findById(bulkShopCart.getCartId());
//             if(byId.isPresent()){
//                 bulkShopCart.setCreateTime(byId.get().getCreateTime());
//             }else{
//                 bulkShopCart.setCreateTime(LocalDateTime.now());
//             }
//             bulkShopCart.setIsCheck(DefaultFlag.YES);
//             log.info("最终插入数据========"+bulkShopCart);
//             if (Objects.isNull(bulkShopCart.getCartId())){
//                 bulkShopCart.setCartId(bulkShopCartVO.getCartId());
//             }
//             bulkShopCartRepository.save(bulkShopCart);
//         } else {
//             if(Objects.nonNull(bulkShopCartVO.getCartId())){
//                 bulkShopCartRepository.deleteByCartId(bulkShopCartVO.getCartId());
//             }
//         }
//     }
//
//
//     @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_INSERT_HISTORY_COMPANY_DATA)
//     public void insertCompanyInfo(HistoryLogisticCompanyRequest company){
//         HistoryLogisticsCompany logisticsCompany=new HistoryLogisticsCompany();
//         logisticsCompany.setCreateTime(company.getCreateTime());
//         logisticsCompany.setLogisticsPhone(company.getLogisticsPhone());
//         logisticsCompany.setOrderId(company.getOrderId());
//         logisticsCompany.setCustomerId(company.getCustomerId());
//         logisticsCompany.setReceivingSite(company.getReceivingSite());
//         logisticsCompany.setLogisticsName(company.getLogisticsName());
//         logisticsCompany.setDelFlag(DeleteFlag.NO);
//         logisticsCompany.setSelfFlag(company.getSelFlag()==0? DefaultFlag.NO:DefaultFlag.YES);
//         logisticsCompany.setCompanyId(company.getCompanyId());
//         logisticsCompany.setLogisticsAddress(company.getLogisticsAddress());
//         logisticsCompany.setLogisticsType(company.getLogisticsType());
//         logisticsCompany.setMarketId(company.getMarketId());
//         logisticsCompany = historyLogisticsCompanyService.add(logisticsCompany);
//
//         logisticsBaseSiteProvider.add(LogisticsBaseSiteRequest.builder().logisticsId(logisticsCompany.getCompanyId())
//                 .siteName(logisticsCompany.getReceivingSite())
//                 .siteCrtType(0).createPerson(logisticsCompany.getCustomerId()).build());
//     }
//
//
//     /**
//      * 延时向wms推送非自营商家佣金支付订单
//      * @param tid
//      */
//     @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_PAYMENT_CONSUMER)
//     public void delayPushingCommissionPaymentConsumer(String tid){
//         log.info("非自营商家佣金推送ERP : {}", tid);
//         try {
//             if(tid.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)){
//
//                 NewPileTrade trade = newPileTradeService.detail(tid);
//
//                 if (Objects.nonNull(trade) && Objects.equals(trade.getTradeState().getPayState(), PayState.PAID)) {
//                     log.info("推送非自营囤货订单佣金部分到ERP：{}", tid);
//
//                     // 业务时间使用惠市宝清算时间
//                     String fdate =  null != trade.getTradeState().getPayTime() ? DateUtil.getDate(trade.getTradeState().getPayTime()) : DateUtil.nowDate();
//                     if (StringUtils.isNotBlank(trade.getPayOrderNo())) {
//                         CcbPayRecordResponse ccbPayRecordResponse = ccbPayProvider.queryCcbPayRecordByPayOrderNo(trade.getPayOrderNo()).getContext();
//                         if (Objects.nonNull(ccbPayRecordResponse) && Objects.nonNull(ccbPayRecordResponse.getClrgDt())) {
//                             fdate =  DateUtil.getDate(DateUtil.parseDate(ccbPayRecordResponse.getClrgDt(), DateUtil.FMT_TIME_5));
//                         }
//                     }
//
//                     BigDecimal commission = BigDecimal.ZERO;
//                     BigDecimal freightCommission = BigDecimal.ZERO;
//
//                     String tradeId = trade.getId();
//                     String payOrderNo = trade.getPayOrderNo();
//                     CcbPayOrderRecordResponse record = ccbPayProvider.queryCcbPayOrderRecord(tradeId, payOrderNo).getContext();
//                     if (Objects.nonNull(record) && Objects.nonNull(record.getCommission()) && record.getCommission().compareTo(BigDecimal.ZERO) > 0) {
//                         commission = record.getCommission();
//                     }
//
//                     if (Objects.nonNull(record) && Objects.nonNull(record.getFreightCommission()) && record.getFreightCommission().compareTo(BigDecimal.ZERO) > 0) {
//                         freightCommission = record.getFreightCommission();
//                     }
//
//                     if (commission.compareTo(BigDecimal.ZERO) > 0) {
//                         PayOrderResponse payOrdersCommission = new PayOrderResponse();
//                         String suffix = "YJ";
//                         payOrdersCommission.setPayOrderId(payOrderNo + suffix);
//                         payOrdersCommission.setTotalPrice(commission);
//                         Long supplierId = trade.getSupplier().getSupplierId();
//                         String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(supplierId).build()).getContext().getCompanyCodeNew();
//                         newPileTradeService.pushPayOrderCommissionKingdee(trade, payOrdersCommission, PayWay.CCB, fdate, companyCodeNew, suffix);
//                     }
//
//                     // 承运商运费推送ERP
//                     if (freightCommission.compareTo(BigDecimal.ZERO) > 0) {
//                         TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                         if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                             String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                             PayOrderResponse payOrdersFreight = new PayOrderResponse();
//                             String suffix = "YFYJ";
//                             payOrdersFreight.setPayOrderId(payOrderNo + suffix);
//                             payOrdersFreight.setTotalPrice(freightCommission);
//                             newPileTradeService.pushPayOrderCommissionKingdee(trade, payOrdersFreight, PayWay.CCB, fdate, logisticCompanyId, suffix);
//                         }
//                     }
//
//                 }
//
//             }else {
//                 Trade trade = tradeService.detail(tid);
//                 if (Objects.nonNull(trade) && Objects.equals(trade.getTradeState().getPayState(), PayState.PAID)) {
//                     log.info("推送非自营囤货订单佣金部分到ERP：{}", tid);
//
//                     // 业务时间使用惠市宝清算时间
//                     String fdate =  null != trade.getTradeState().getPayTime() ? DateUtil.getDate(trade.getTradeState().getPayTime()) : DateUtil.nowDate();
//                     if (StringUtils.isNotBlank(trade.getPayOrderNo())) {
//                         CcbPayRecordResponse ccbPayRecordResponse = ccbPayProvider.queryCcbPayRecordByPayOrderNo(trade.getPayOrderNo()).getContext();
//                         if (Objects.nonNull(ccbPayRecordResponse) && Objects.nonNull(ccbPayRecordResponse.getClrgDt())) {
//                             fdate =  DateUtil.getDate(DateUtil.parseDate(ccbPayRecordResponse.getClrgDt(), DateUtil.FMT_TIME_5));
//                         }
//                     }
//
//                     BigDecimal commission = BigDecimal.ZERO;
//                     BigDecimal freightCommission = BigDecimal.ZERO;
//
//                     String tradeId = trade.getId();
//                     String payOrderNo = trade.getPayOrderNo();
//                     CcbPayOrderRecordResponse record = ccbPayProvider.queryCcbPayOrderRecord(tradeId, payOrderNo).getContext();
//                     if (Objects.nonNull(record) && Objects.nonNull(record.getCommission()) && record.getCommission().compareTo(BigDecimal.ZERO) > 0) {
//                         commission = record.getCommission();
//                     }
//
//                     if (Objects.nonNull(record) && Objects.nonNull(record.getFreightCommission()) && record.getFreightCommission().compareTo(BigDecimal.ZERO) > 0) {
//                         freightCommission = record.getFreightCommission();
//                     }
//
//                     if (commission.compareTo(BigDecimal.ZERO) > 0) {
//                         PayOrderResponse payOrdersCommission = new PayOrderResponse();
//                         String suffix = "YJ";
//                         payOrdersCommission.setPayOrderId(payOrderNo + suffix);
//                         payOrdersCommission.setTotalPrice(commission);
//                         Long supplierId = trade.getSupplier().getSupplierId();
//                         String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(supplierId).build()).getContext().getCompanyCodeNew();
//                         tradeService.pushPayOrderCommissionKingdee(trade, payOrdersCommission, PayWay.CCB, fdate, companyCodeNew, suffix);
//                     }
//
//                     // 承运商运费推送ERP
//                     if (freightCommission.compareTo(BigDecimal.ZERO) > 0) {
//                         TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                         if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                             String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                             PayOrderResponse payOrdersFreight = new PayOrderResponse();
//                             String suffix = "YFYJ";
//                             payOrdersFreight.setPayOrderId(payOrderNo + suffix);
//                             payOrdersFreight.setTotalPrice(freightCommission);
//                             tradeService.pushPayOrderCommissionKingdee(trade, payOrdersFreight, PayWay.CCB, fdate, logisticCompanyId, suffix);
//                         }
//                     }
//                 }
//             }
//         }catch (Exception e){
//             log.error("非自营商家佣金推送ERP 错误：", e);
//         }
//     }
//
//     /**
//      * 延时向ERP推送非自营商家佣金退单
//      * @param sendStr
//      */
//     @StreamListener(JmsDestinationConstants.Q_ORDER_SERVICE_PUSH_COMMISSION_REFUND_CONSUMER)
//     public void delayPushingCommissionRefundConsumer(String sendStr){
//         log.info("非自营商家佣金退款推送ERP : {}", sendStr);
//         try {
//             String[] strs = sendStr.split("#");
//             String tid = strs[0];
//             String rid = strs[1];
//
//             if(tid.startsWith(GeneratorService._NEW_PILE_PREFIX_TRADE_ID)){
//
//                 NewPileTrade trade = newPileTradeService.detail(tid);
//
//                 if (Objects.nonNull(trade) && Objects.equals(trade.getTradeState().getPayState(), PayState.PAID)) {
//                     NewPileReturnOrder returnOrder = newPileReturnOrderService.findById(rid);
//
//                     if (Objects.nonNull(returnOrder) && Objects.equals(returnOrder.getReturnFlowState(), NewPileReturnFlowState.COMPLETED)) {
//
//                         CcbRefundRecordResponse refundRecordResponse = ccbPayProvider.queryCcbRefundRecordByRid(rid).getContext();
//                         if (Objects.nonNull(refundRecordResponse)) {
//                             // 有退款记录 使用退款记录金额推送
//                             BigDecimal commissionPrice = refundRecordResponse.getCommissionPrice();
//                             if (Objects.nonNull(commissionPrice) && commissionPrice.compareTo(BigDecimal.ZERO) > 0) {
//                                 // 收款单佣推送金蝶
//                                 Long companyInfoId = returnOrder.getCompany().getCompanyInfoId();
//                                 String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(companyInfoId).build()).getContext().getCompanyCodeNew();
//                                 log.info("退单佣金推送金蝶，退单号：{}，商家编码：{}", rid, companyCodeNew);
//                                 returnOrderService.newPilePushRefundOrderCommissionKingdee(commissionPrice, rid, tid, companyCodeNew,"YJ");
//                             }
//                             BigDecimal freightCommissionPrice = refundRecordResponse.getFreightCommissionPrice();
//                             if (Objects.nonNull(freightCommissionPrice) && freightCommissionPrice.compareTo(BigDecimal.ZERO) > 0) {
//                                 //  承运商退款佣金推送金蝶
//                                 TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                                 if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                                     String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                                     returnOrderService.newPilePushRefundOrderCommissionKingdee(freightCommissionPrice, rid, tid, logisticCompanyId,"YFYJ");
//                                 }
//                             }
//                             log.info("使用退款记录金额推送金蝶，退单号：{}，退总佣金金额：{}，退承运商佣金金额：{}", rid, commissionPrice, freightCommissionPrice);
//                         }else {
//                             // 退款收款单 佣金分开推送金蝶
//                             String payOrderNo = trade.getPayOrderNo();
//                             // BigDecimal price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, RoundingMode.DOWN);
//                             BigDecimal price;
//                             if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) == 0 &&
//                                     returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) == 0 ){
//                                 if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) > 0) {
//                                     //申请金额
//                                     price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, RoundingMode.DOWN);
//                                 } else {
//                                     //商品总金额
//                                     price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, RoundingMode.DOWN);
//                                 }
//                             }else{
//                                 price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, RoundingMode.DOWN);
//                             }
//
//                             CcbPayOrderRecordResponse recordResponse = ccbPayProvider.queryCcbPayOrderRecord(tid, payOrderNo).getContext();
//                             BigDecimal freight = BigDecimal.ZERO;
//                             BigDecimal freightCommission = BigDecimal.ZERO;
//                             BigDecimal commission = BigDecimal.ZERO;
//                             if (Objects.nonNull(recordResponse)) {
//                                 log.info("推金蝶退款收款单非自营商家，佣金部分，比例：{},退单金额：{}",recordResponse.getRatio(), price);
//                                 log.info("推金蝶退款收款单非自营商家，配送到店运费部分，退单类型：{}，订单配置方式：{}", returnOrder.getReturnType(), trade.getDeliverWay());
//                                 if (Objects.equals(ReturnType.REFUND, returnOrder.getReturnType())
//                                         && (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())) {
//                                     if (Objects.equals(DeliverWay.DELIVERY_TO_STORE, trade.getDeliverWay())) {
//                                         // 售前 退配送到店的运费
//                                         freight = Objects.nonNull(recordResponse.getFreight()) ? recordResponse.getFreight() : BigDecimal.ZERO;
//                                         freightCommission = Objects.nonNull(recordResponse.getFreightCommission()) ? recordResponse.getFreightCommission() : BigDecimal.ZERO;
//                                     }
//                                     commission = recordResponse.getCommission();
//                                 }else {
//                                     BigDecimal ratio = recordResponse.getRatio();
//                                     BigDecimal amt = price.multiply(ratio).setScale(2, RoundingMode.UP);
//                                     BigDecimal txnAmt = recordResponse.getTxnAmt();
//                                     if (amt.compareTo(txnAmt) >= 0) {
//                                         amt = txnAmt;
//                                     }
//                                     commission = price.subtract(amt).setScale(6, RoundingMode.DOWN);
//                                 }
//
//                             }
//                             log.info("推金蝶退款收款单非自营商家,佣金部分,退单号：{}计算佣金：{}",rid, commission);
//
//                             if (commission.compareTo(BigDecimal.ZERO) > 0) {
//                                 Long companyInfoId = returnOrder.getCompany().getCompanyInfoId();
//                                 String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(companyInfoId).build()).getContext().getCompanyCodeNew();
//                                 log.info("退单佣金推送金蝶，退单号：{}，商家编码：{}", rid, companyCodeNew);
//                                 returnOrderService.newPilePushRefundOrderCommissionKingdee(commission, rid, tid, companyCodeNew, "YJ");
//                             }
//
//                             if (Objects.nonNull(freightCommission) && freightCommission.compareTo(BigDecimal.ZERO) > 0) {
//                                 //  承运商退款佣金推送金蝶
//                                 TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                                 if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                                     String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                                     returnOrderService.newPilePushRefundOrderCommissionKingdee(freightCommission, rid, tid, logisticCompanyId,"YFYJ");
//                                 }
//                             }
//                         }
//
//                     }
//                 }
//
//             }else {
//                 Trade trade = tradeService.detail(tid);
//
//                 if (Objects.nonNull(trade) && Objects.equals(trade.getTradeState().getPayState(), PayState.PAID)) {
//                     ReturnOrder returnOrder = returnOrderService.findById(rid);
//
//                     if (Objects.nonNull(returnOrder) && Objects.equals(returnOrder.getReturnFlowState(), ReturnFlowState.COMPLETED)) {
//
//                         CcbRefundRecordResponse refundRecordResponse = ccbPayProvider.queryCcbRefundRecordByRid(rid).getContext();
//                         if (Objects.nonNull(refundRecordResponse)) {
//                             // 有退款记录 使用退款记录金额推送
//
//                             BigDecimal commissionPrice = refundRecordResponse.getCommissionPrice();
//                             if (Objects.nonNull(commissionPrice) && commissionPrice.compareTo(BigDecimal.ZERO) > 0) {
//                                 // 收款单佣推送金蝶
//                                 Long companyInfoId = returnOrder.getCompany().getCompanyInfoId();
//                                 String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(companyInfoId).build()).getContext().getCompanyCodeNew();
//                                 log.info("退单佣金推送金蝶，退单号：{}，商家编码：{}", rid, companyCodeNew);
//                                 returnOrderService.pushRefundOrderCommissionKingdee(commissionPrice, rid, tid, companyCodeNew, "YJ");
//                             }
//                             BigDecimal freightCommissionPrice = refundRecordResponse.getFreightCommissionPrice();
//                             if (Objects.nonNull(freightCommissionPrice) && freightCommissionPrice.compareTo(BigDecimal.ZERO) > 0) {
//                                 //  承运商退款佣金推送金蝶
//                                 TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                                 if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                                     String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                                     log.info("退单承运商佣金推送金蝶，退单号：{}，商家编码：{}", rid, logisticCompanyId);
//                                     returnOrderService.pushRefundOrderCommissionKingdee(freightCommissionPrice, rid, tid, logisticCompanyId,"YFYJ");
//                                 }
//
//                             }
//                             log.info("使用退款记录金额推送金蝶，退单号：{}，退总佣金金额：{}，退承运商佣金金额：{}", rid, commissionPrice, freightCommissionPrice);
//                         }else {
//                             // 退款收款单 佣金分开推送金蝶
//                             String payOrderNo = trade.getPayOrderNo();
//                             // BigDecimal price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, RoundingMode.DOWN);
//                             BigDecimal price;
//                             if(returnOrder.getReturnPrice().getTotalBalanceRefundAmount().compareTo(BigDecimal.ZERO) == 0 &&
//                                     returnOrder.getReturnPrice().getTotalOnlineRefundAmount().compareTo(BigDecimal.ZERO) == 0 ){
//                                 if (returnOrder.getReturnPrice().getApplyPrice().compareTo(BigDecimal.ZERO) > 0) {
//                                     //申请金额
//                                     price = returnOrder.getReturnPrice().getApplyPrice().setScale(2, RoundingMode.DOWN);
//                                 } else {
//                                     //商品总金额
//                                     price = returnOrder.getReturnPrice().getTotalPrice().setScale(2, RoundingMode.DOWN);
//                                 }
//                             }else{
//                                 price = returnOrder.getReturnPrice().getTotalOnlineRefundAmount().setScale(2, RoundingMode.DOWN);
//                             }
//
//                             CcbPayOrderRecordResponse recordResponse = ccbPayProvider.queryCcbPayOrderRecord(tid, payOrderNo).getContext();
//                             BigDecimal freight = BigDecimal.ZERO;
//                             BigDecimal freightCommission = BigDecimal.ZERO;
//                             BigDecimal commission = BigDecimal.ZERO;
//                             if (Objects.nonNull(recordResponse)) {
//                                 log.info("推金蝶退款收款单非自营商家，佣金部分，比例：{},退单金额：{}",recordResponse.getRatio(), price);
//                                 log.info("推金蝶退款收款单非自营商家，配送到店运费部分，退单类型：{}，订单配置方式：{}", returnOrder.getReturnType(), trade.getDeliverWay());
//                                 if (Objects.equals(ReturnType.REFUND, returnOrder.getReturnType())
//                                         && (Objects.isNull(returnOrder.getWmsStats()) || !returnOrder.getWmsStats())) {
//                                     if (Objects.equals(DeliverWay.DELIVERY_TO_STORE, trade.getDeliverWay())) {
//                                         // 售前 退配送到店的运费
//                                         freight = Objects.nonNull(recordResponse.getFreight()) ? recordResponse.getFreight() : BigDecimal.ZERO;
//                                         freightCommission = Objects.nonNull(recordResponse.getFreightCommission()) ? recordResponse.getFreightCommission() : BigDecimal.ZERO;
//                                     }
//                                     commission = recordResponse.getCommission();
//                                 }else {
//                                     BigDecimal ratio = recordResponse.getRatio();
//                                     BigDecimal amt = price.multiply(ratio).setScale(2, RoundingMode.UP);
//                                     BigDecimal txnAmt = recordResponse.getTxnAmt();
//                                     if (amt.compareTo(txnAmt) >= 0) {
//                                         amt = txnAmt;
//                                     }
//                                     commission = price.subtract(amt).setScale(6, RoundingMode.DOWN);
//                                 }
//                             }
//                             log.info("推金蝶退款收款单非自营商家,佣金部分,退单号：{}计算佣金：{}",rid, commission);
//
//                             if (commission.compareTo(BigDecimal.ZERO) > 0) {
//                                 Long companyInfoId = returnOrder.getCompany().getCompanyInfoId();
//                                 String companyCodeNew = companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(companyInfoId).build()).getContext().getCompanyCodeNew();
//                                 log.info("退单佣金推送金蝶，退单号：{}，商家编码：{}", rid, companyCodeNew);
//                                 returnOrderService.pushRefundOrderCommissionKingdee(commission, rid, tid, companyCodeNew, "YJ");
//                             }
//
//                             if (Objects.nonNull(freightCommission) && freightCommission.compareTo(BigDecimal.ZERO) > 0) {
//                                 //  承运商退款佣金推送金蝶
//                                 TradeDeliver tradeDeliver = trade.getTradeDelivers().stream().findFirst().orElse(null);
//                                 if (Objects.nonNull(tradeDeliver) && Objects.nonNull(tradeDeliver.getLogistics().getLogisticCompanyId())) {
//                                     String logisticCompanyId = tradeDeliver.getLogistics().getLogisticCompanyId();
//                                     log.info("退单承运商佣金推送金蝶，退单号：{}，商家编码：{}", rid, logisticCompanyId);
//                                     returnOrderService.pushRefundOrderCommissionKingdee(freightCommission, rid, tid, logisticCompanyId,"YFYJ");
//                                 }
//                             }
//                         }
//
//                     }
//                 }
//             }
//         }catch (Exception e){
//             log.error("非自营商家退单佣金推送ERP 错误：", e);
//         }
//     }
//
// }
