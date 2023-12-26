package com.wanmi.sbc.returnorder.common;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeOptionalByIdRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeOptionalByIdResponse;
import com.wanmi.sbc.goods.api.provider.standard.StandardSkuQueryProvider;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.returnorder.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.returnorder.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.returnorder.bean.enums.TradeActivityTypeEnum;
import com.wanmi.sbc.returnorder.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.returnorder.enums.KingdeeOrderStateEnums;
import com.wanmi.sbc.returnorder.enums.PushKingdeeOrderStatusEnum;
import com.wanmi.sbc.returnorder.enums.PushKingdeeStatusEnum;
import com.wanmi.sbc.returnorder.trade.model.entity.PileStockRecordAttachment;
import com.wanmi.sbc.returnorder.trade.model.entity.TradeItem;
import com.wanmi.sbc.returnorder.trade.model.entity.value.FSaleOrderEntry;
import com.wanmi.sbc.returnorder.trade.model.entity.value.KingDeeResult;
import com.wanmi.sbc.returnorder.trade.model.entity.value.KingdeeSalesOrder;
import com.wanmi.sbc.returnorder.trade.model.newPileTrade.NewPileTrade;
import com.wanmi.sbc.returnorder.trade.model.root.Trade;
import com.wanmi.sbc.returnorder.trade.model.root.TradePushKingdeeOrder;
import com.wanmi.sbc.returnorder.trade.repository.PileStockRecordAttachmentRepostory;
import com.wanmi.sbc.returnorder.trade.repository.TradePushKingdeeOrderRepository;
import com.wanmi.sbc.returnorder.trade.repository.TradePushKingdeePayRepository;
import com.wanmi.sbc.returnorder.trade.service.TradeService;
import com.wanmi.sbc.returnorder.trade.service.newPileTrade.NewPileTradeService;
import com.wanmi.sbc.returnorder.util.KingdeeLoginUtils;
import com.wanmi.sbc.wms.bean.vo.ERPWMSConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Service
@Slf4j

public class KingdeePushOrder {
    @Autowired
    private TradePushKingdeeOrderRepository tradePushKingdeeOrderRepository;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private KingdeeLoginUtils kingdeeLoginUtils;

    @Autowired
    private TradePushKingdeePayRepository tradePushKingdeePayRepository;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private NewPileTradeService newPileTradeService;

    @Autowired
    private PileStockRecordAttachmentRepostory pileStockRecordAttachmentRepostory;

    @Autowired
    private StandardSkuQueryProvider standardSkuQueryProvider;

    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;
    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;


    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.Order.url}")
    private String orderUrl;

    @Value("${kingdee.New.pile.order.url}")
    private String newPileOrderUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    @Value("${kingdee.freight.material}")
    private String kingdeeFreight;

    @Value("${kingdee.freight.packing}")
    private String kingdeePacking;


    @Value("${retail.retailWareId}")
    private Long retailWareId;



    /**
     * 向金蝶推送订单数据（异步）
     * @param tid
     */
//    @Async("orderExecutor")
    public void asyncPushSalesOrderkingdee(String tid){
        Trade trade = tradeService.detail(tid);
        //查询关联表
        List<PileStockRecordAttachment> stockRecordAttachmentList = pileStockRecordAttachmentRepostory.findStockRecordAttachment(tid);
        log.info("KingdeePushOrder.asyncPushSalesOrderkingdee :{} tid:{}",JSONObject.toJSONString(stockRecordAttachmentList),tid);
        Long stockNum = trade.getTradeItems().stream().mapToLong(TradeItem::getNum).sum();
        Long deliveryNum = stockRecordAttachmentList.stream().mapToLong(PileStockRecordAttachment::getNum).sum();
        if (!stockNum.equals(deliveryNum)) {
            log.info("提货数量:{}件，不等于发货数量:{}件，请联系客服处理！",stockNum,deliveryNum);
            return;
        }
        pushSalesOrderkingdee(trade,stockRecordAttachmentList);
    }

    /**
     * 向金蝶推送订单数据（同步）
     * @param trade
     */
    public void pushSalesOrderkingdee(Trade trade,List<PileStockRecordAttachment> stockRecordAttachmentList){
        //查找销售订单是否存在
        Integer number = tradePushKingdeeOrderRepository.selcetPushKingdeeOrderNumber(trade.getId());
        TradePushKingdeeOrder tradePushKingdeeOrder = new TradePushKingdeeOrder();
        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        tradePushKingdeeOrder.setOrderCode(trade.getId());
        tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CREATE.toOrderStatus());
        KingdeeSalesOrder order = new KingdeeSalesOrder();
        boolean isStartMultiSpeci = startMultiSpeci();
        try {
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("TradeService.pushSalesOrderkingdee Lack employeeResponse");
                tradePushKingdeeOrder.setInstructions("Lack employeeResponse");
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            Map FCustId = new HashMap();
            FCustId.put("FNumber",trade.getBuyer().getAccount());
            order.setFCustId(FCustId);
            if(Objects.nonNull(trade.getTradeState().getCreateTime())){
                order.setFDate(DateUtil.getDate(trade.getTradeState().getCreateTime()));
            }else{
                order.setFDate(DateUtil.nowDate());
            }
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            order.setFSaleOrgId(fSaleOrgId);//组织
            order.setFFreight(trade.getTradePrice().getDeliveryPrice());
            Map FSalerId = new HashMap();
            FSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            order.setFSalerId(FSalerId);
            order.setOrderNumber(trade.getId());
            Map orderState = new HashMap();
            orderState.put("FNumber", KingdeeOrderStateEnums.PAYMENTHASBEEN.toValue());
            order.setOrderState(orderState);
            Map province = new HashMap();
            province.put("FNumber",trade.getConsignee().getProvinceId());
            order.setProvinceId(province);
            Map city = new HashMap();
            city.put("FNumber",trade.getConsignee().getCityId());
            order.setCityId(city);
            Map area = new HashMap();
            area.put("FNumber",trade.getConsignee().getAreaId());
            order.setAreaId(area);
            order.setDetailAddress(trade.getConsignee().getDetailAddress());
            //收货人
            if (StringUtils.isNotEmpty(trade.getConsignee().getName())){
                order.setFContact(trade.getConsignee().getName());
            }
            //联系电话
            if (StringUtils.isNotEmpty(trade.getConsignee().getPhone())){
                order.setFLinkPhone(trade.getConsignee().getPhone());
            }
            //销售类型
            Map fSaleType = new HashMap();
            if (Objects.nonNull(trade.getSaleType())) {
                fSaleType.put("FNumber",String.valueOf(trade.getSaleType().toValue()));
                order.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber",String.valueOf(SaleType.WHOLESALE.toValue()));
                order.setFSaleType(fSaleType);
            }
            String comanyCodeNew =companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(trade.getSupplier().getSupplierId()).build()).getContext().getCompanyCodeNew();
            Map fOraBase5 = new HashMap();
            fOraBase5.put("FNumber",comanyCodeNew);//商家编码
            order.setFOraBase5(fOraBase5);
            Map fTranType = new HashMap();
            fTranType.put("FNumber",switchWMSPushOrderType(trade.getDeliverWay()));
            order.setFTranType(fTranType);
            if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
                order.setFLogName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
                order.setFLogPhone(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
                order.setFLogAddress(trade.getLogisticsCompanyInfo().getLogisticsAddress());
                order.setFLogSite(trade.getLogisticsCompanyInfo().getReceivingPoint());
            }
            order.setFLogNote(trade.getBuyerRemark());
            //仓库编码
            Map fStock = new HashMap();
            if(trade.getWareId() == 1L || trade.getWareId() == 45L){
                fStock.put("FNumber",ERPWMSConstants.MAIN_WH);
            }else if(trade.getWareId() == 46L){
                fStock.put("FNumber",ERPWMSConstants.SUB_WH);
            }else if(trade.getWareId() == 47L){
                fStock.put("FNumber",ERPWMSConstants.STORE_WH);
            }else{
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"推送金蝶找不到对应仓库ID.");
            }
            order.setFStock(fStock);
            List<FSaleOrderEntry> fSaleOrderEntry = new ArrayList<>();
            Map<String, String> goodsIdskeyMap = null;
            if(trade.getTradeItems().size() > 0 || trade.getGifts().size() > 0){
                List<String> skuIds = new ArrayList<>();
                if(trade.getTradeItems().size() > 0){
                    skuIds.addAll(trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()));
                }
                if(trade.getGifts().size() > 0){
                    skuIds.addAll(trade.getGifts().stream().map(TradeItem::getSkuId).collect(Collectors.toList()));
                }
                if(null != skuIds && !skuIds.isEmpty()){
                    StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
                    standardGoodsGetUsedGoodsRequest.setGoodsIds(skuIds);
                    goodsIdskeyMap = standardSkuQueryProvider.findBySkuIds(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds();
                    log.info("请求获取sku对应的税率数据：" + goodsIdskeyMap == null ? "" : JSONObject.toJSONString(goodsIdskeyMap).toString());
                }
            }

            //购买商品
            if (trade.getTradeItems().size() > 0 && stockRecordAttachmentList.size() > 0) {
                for (TradeItem item : trade.getTradeItems()) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    for (PileStockRecordAttachment stockRecordAttachment : stockRecordAttachmentList) {
                        if (item.getSkuId().equals(stockRecordAttachment.getSkuId())) {
                            log.info("KingdeePushOrder.pushSalesOrderkingdee item.getSkuId:{} stockRecordAttachment.getSkuId:{}",item.getSkuId(),stockRecordAttachment.getSkuId());
                            if (StringUtils.isEmpty(item.getErpSkuNo()) || stockRecordAttachment.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())) {
                                tradePushKingdeeOrder.setInstructions("Lack TradeItems item");
                                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                                return;
                            }
//                          BigDecimal price = item.getSplitPrice().divide(BigDecimal.valueOf(item.getNum()), 6, BigDecimal.ROUND_DOWN);
                            BigDecimal price = stockRecordAttachment.getPrice();
                            FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                            Map FMaterialId = new HashMap();
                            orderEntry.setFQty(BigDecimal.valueOf(stockRecordAttachment.getNum()));
                            orderEntry.setFPrice(price);
                            orderEntry.setFTaxPrice(price);
                            orderEntry.setFIsFree("0");
                            FMaterialId.put("FNumber", item.getErpSkuNo());
                            orderEntry.setFMaterialId(FMaterialId);
                            orderEntry.setFOraText(item.getSkuNo());
                            orderEntry.setF_According(stockRecordAttachment.getOrderCode());
                            //新增税率字段
//                            orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                            //新增打包字段
                            orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                            fSaleOrderEntry.add(orderEntry);

                        }
                    }
                }
            }else if (trade.getTradeItems().size() > 0){
                for (TradeItem item : trade.getTradeItems()) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack TradeItems item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }

                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    if(!isStartMultiSpeci && Objects.nonNull(item.getDevanningId())){
                        orderEntry.setFQty(item.getDivisorFlag().multiply(BigDecimal.valueOf(item.getNum())));
                    }
                    BigDecimal balance = (null != item.getWalletSettlements() && !item.getWalletSettlements().isEmpty()) ?  item.getWalletSettlements().get(0).getReduceWalletPrice()
                            : BigDecimal.ZERO;
                    BigDecimal price = item.getSplitPrice().add(balance);
                    price = price.divide(orderEntry.getFQty(),6,BigDecimal.ROUND_DOWN);

                    orderEntry.setFPrice(price);
                    orderEntry.setFTaxPrice(price);
                    if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
                        List<InventoryDetailSamountVO> inventoryDetailSamountVOS =
                                inventoryDetailSamountProvider.getInventoryByTakeId(InventoryDetailSamountRequest.builder()
                                        .takeId(trade.getId()).build()).getContext().getInventoryDetailSamountVOS();
                        if(null != inventoryDetailSamountVOS && !inventoryDetailSamountVOS.isEmpty()){
                            int index = 1;
                            BigDecimal inPrice = BigDecimal.ZERO;
                            for (InventoryDetailSamountVO vo : inventoryDetailSamountVOS){
                                if(item.getSkuId().equals(vo.getGoodsInfoId()) && vo.getTakeFlag() == 1 && (orderEntry.getFQty().intValue()*2) >= index){
                                    index++;
                                    inPrice = inPrice.add(vo.getAmortizedExpenses());
                                }
                            }
                            if(inPrice.compareTo(BigDecimal.ZERO) == 1){
                                inPrice = inPrice.divide(orderEntry.getFQty(), 6, BigDecimal.ROUND_DOWN);
                            }
                            orderEntry.setFPrice(inPrice);
                            orderEntry.setFTaxPrice(inPrice);
                        }
                    }
                    orderEntry.setFIsFree("0");
                    FMaterialId.put("FNumber",item.getErpSkuNo());
                    orderEntry.setFMaterialId(FMaterialId);
                    orderEntry.setFOraText(item.getSkuNo());
                    //新增税率字段
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                    //拆箱id
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }
                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //赠送商品
            if (trade.getGifts().size() > 0) {
                for (TradeItem item : trade.getGifts()) {
                    String prefix = Constants.ERP_NO_PREFIX.get(trade.getWareId());
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack Gifts item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }
                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    orderEntry.setFPrice(item.getPrice());
                    orderEntry.setFTaxPrice(item.getPrice());
                    orderEntry.setFIsFree("1");
                    FMaterialId.put("FNumber",item.getErpSkuNo());
                    orderEntry.setFMaterialId(FMaterialId);
                    orderEntry.setFOraText(item.getSkuNo());
                    //新增税率字段
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }
                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //判断是否有邮费
            if (trade.getTradePrice() != null && trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1){
                FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                Map FMaterialId = new HashMap();
                FMaterialId.put("FNumber",kingdeeFreight);
                orderEntry.setFMaterialId(FMaterialId);
                orderEntry.setFPrice(trade.getTradePrice().getDeliveryPrice());
                orderEntry.setFTaxPrice(trade.getTradePrice().getDeliveryPrice());
                orderEntry.setFQty(BigDecimal.ONE);
                fSaleOrderEntry.add(orderEntry);
                Map paymentOrderState = new HashMap();
                paymentOrderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
                order.setOrderState(paymentOrderState);
            }
            order.setFSaleOrderEntry(fSaleOrderEntry);
            //登录财务系统
            Map<String,Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user",kingdeeUser);
            requestLogMap.put("pwd",kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
            if (StringUtils.isNotEmpty(loginToken)){
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",order);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(orderUrl, requestMap, loginToken);
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushSalesOrderkingdee result1:{} code:{}", result1.getResultData(),kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                }else {
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeeOrder.setInstructions(result1.getResultData());
            }else {
                log.error("TradeService.pushSalesOrderkingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeeOrder.setInstructions(res);
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        }catch (Exception e){
            log.error("TradeService.pustSalesOrderkingdee error:{}",e);
            String res = "金蝶推送失败";
            tradePushKingdeeOrder.setInstructions(res);
            tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        }finally {
            if (number == 0) {
                tradePushKingdeeOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setCancelOperation("创建");
                tradePushKingdeeOrderRepository.save(tradePushKingdeeOrder);
            }else {
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrderRepository.updatePushKingdeeOrderState(tradePushKingdeeOrder);
            }
        }
    }

    //获取多规格上线时间
    private boolean startMultiSpeci() {
        LocalDateTime multiStartTime= tradeService.getMultiSpeciStartTime();
        return multiStartTime.isBefore(DateUtil.parse(DateUtil.format(new Date(),DateUtil.FMT_TIME_1),DateUtil.FMT_TIME_1));
    }

    /**
     * 购物车向金蝶推送订单数据
     * @param tid
     */
    public void shoppingCartPushSalesOrderkingdee(String tid){
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee tid:{}",tid);
        Trade trade = tradeService.detail(tid);
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee trade:{}",JSONObject.toJSONString(trade));
        //查找销售订单是否存在
        Integer number = tradePushKingdeeOrderRepository.selcetPushKingdeeOrderNumber(trade.getId());
        TradePushKingdeeOrder tradePushKingdeeOrder = new TradePushKingdeeOrder();
        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        tradePushKingdeeOrder.setOrderCode(trade.getId());
        tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CREATE.toOrderStatus());
        KingdeeSalesOrder order = new KingdeeSalesOrder();
        try {
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("TradeService.pushSalesOrderkingdee Lack employeeResponse");
                tradePushKingdeeOrder.setInstructions("Lack employeeResponse");
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            Map FCustId = new HashMap();
            FCustId.put("FNumber",trade.getBuyer().getAccount());
            order.setFCustId(FCustId);
            if(Objects.nonNull(trade.getTradeState().getCreateTime())){
                order.setFDate(DateUtil.getDate(trade.getTradeState().getCreateTime()));
            }else{
                order.setFDate(DateUtil.nowDate());
            }
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            order.setFSaleOrgId(fSaleOrgId);//组织
            order.setFFreight(trade.getTradePrice().getDeliveryPrice());
            Map FSalerId = new HashMap();
            FSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            order.setFSalerId(FSalerId);
            order.setOrderNumber(trade.getId());
            Map orderState = new HashMap();
            if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
                orderState.put("FNumber", KingdeeOrderStateEnums.PAYMENTHASBEEN.toValue());
            }else{
                orderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
            }

            order.setOrderState(orderState);
            Map province = new HashMap();
            province.put("FNumber",trade.getConsignee().getProvinceId());
            order.setProvinceId(province);
            Map city = new HashMap();
            city.put("FNumber",trade.getConsignee().getCityId());
            order.setCityId(city);
            Map area = new HashMap();
            area.put("FNumber",trade.getConsignee().getAreaId());
            order.setAreaId(area);
            order.setDetailAddress(trade.getConsignee().getDetailAddress());
            //收货人
            if (StringUtils.isNotEmpty(trade.getConsignee().getName())){
                order.setFContact(trade.getConsignee().getName());
            }
            //联系电话
            if (StringUtils.isNotEmpty(trade.getConsignee().getPhone())){
                order.setFLinkPhone(trade.getConsignee().getPhone());
            }
            //销售类型
            Map fSaleType = new HashMap();
            if (Objects.nonNull(trade.getSaleType())) {
                fSaleType.put("FNumber",String.valueOf(trade.getSaleType().toValue()));
                order.setFSaleType(fSaleType);
            } else {
                fSaleType.put("FNumber",String.valueOf(SaleType.WHOLESALE.toValue()));
                order.setFSaleType(fSaleType);
            }
            String comanyCodeNew =companyInfoQueryProvider.getCompanyInfoById(CompanyInfoByIdRequest.builder().companyInfoId(trade.getSupplier().getSupplierId()).build()).getContext().getCompanyCodeNew();
            Map fOraBase5 = new HashMap();
            fOraBase5.put("FNumber",comanyCodeNew);//商家编码
            order.setFOraBase5(fOraBase5);
            Map fTranType = new HashMap();
            fTranType.put("FNumber",switchWMSPushOrderType(trade.getDeliverWay()));
            order.setFTranType(fTranType);
            if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
                order.setFLogName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
                order.setFLogPhone(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
                order.setFLogAddress(trade.getLogisticsCompanyInfo().getLogisticsAddress());
                order.setFLogSite(trade.getLogisticsCompanyInfo().getReceivingPoint());
            }
            order.setFLogNote(trade.getBuyerRemark());
            //仓库编码
            Map fStock = new HashMap();
//            fStock.put("FNumber",ERPWMSConstants.MAIN_WH);
            if(trade.getWareId() == 1L || trade.getWareId() == 45L || trade.getWareId() == 49L){
                fStock.put("FNumber",ERPWMSConstants.MAIN_WH);
            }else if(trade.getWareId() == 46L || trade.getWareId() == 50L){
                fStock.put("FNumber",ERPWMSConstants.SUB_WH);
            }else if(trade.getWareId() == 47L || trade.getWareId() == 51L){
                fStock.put("FNumber",ERPWMSConstants.STORE_WH);
            }
            else{
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"推送金蝶找不到对应仓库ID");
            }
            order.setFStock(fStock);
            boolean isStartMultiSpeci = startMultiSpeci();
            List<FSaleOrderEntry> fSaleOrderEntry = new ArrayList<>();
//            Map<String, String> goodsIdskeyMap = null;
//            if(trade.getTradeItems().size() > 0 || trade.getGifts().size() > 0){
//                List<String> skuIds = new ArrayList<>();
//                if(trade.getTradeItems().size() > 0){
//                    skuIds.addAll(trade.getTradeItems().stream().map(TradeItem::getSkuId).collect(Collectors.toList()));
//                }
//                if(trade.getGifts().size() > 0){
//                    skuIds.addAll(trade.getGifts().stream().map(TradeItem::getSkuId).collect(Collectors.toList()));
//                }
//                if(null != skuIds && !skuIds.isEmpty()){
//                    StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
//                    standardGoodsGetUsedGoodsRequest.setGoodsIds(skuIds);
//                    goodsIdskeyMap = standardSkuQueryProvider.findBySkuIds(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds();
//                    log.info("请求获取sku对应的税率数据1：" + goodsIdskeyMap == null ? "" : JSONObject.toJSONString(goodsIdskeyMap).toString());
//                }
//            }
            //拆箱散批标识
            Boolean spflag = trade.getSaleType().equals(SaleType.BULK);
            //购买商品
            if (trade.getTradeItems().size() > 0){
                for (TradeItem item : trade.getTradeItems()) {

                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    log.info("========prefix:{}，ErpSkuNo：{}",prefix,item.getErpSkuNo());
                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack TradeItems item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }

                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    if(!isStartMultiSpeci) {
                        if (Objects.nonNull(item.getDevanningId())) {
                            orderEntry.setFQty(item.getDivisorFlag().multiply(BigDecimal.valueOf(item.getNum())));
                        }
                        if (spflag) {
                            orderEntry.setFQty(BigDecimal.valueOf(item.getNum()).multiply(item.getAddStep()));
                        }
                    }
                    BigDecimal price = item.getSplitPrice().add(
                            null != item.getWalletSettlements() && ! item.getWalletSettlements().isEmpty() ?  item.getWalletSettlements().get(0).getReduceWalletPrice()
                                    : BigDecimal.ZERO
                    );
                    price = price.divide(orderEntry.getFQty(),6,BigDecimal.ROUND_DOWN);
                    orderEntry.setFPrice(price);
                    orderEntry.setFTaxPrice(price);

                    if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
                        List<InventoryDetailSamountVO> inventoryDetailSamountVOS =
                                inventoryDetailSamountProvider.getInventoryByTakeId(InventoryDetailSamountRequest.builder()
                                        .takeId(trade.getId()).build()).getContext().getInventoryDetailSamountVOS();
                        if(null != inventoryDetailSamountVOS && !inventoryDetailSamountVOS.isEmpty()){
                            int index = 1;
                            BigDecimal inPrice = BigDecimal.ZERO;
                            for (InventoryDetailSamountVO vo : inventoryDetailSamountVOS){
                                if(item.getSkuId().equals(vo.getGoodsInfoId()) && vo.getTakeFlag() == 1 && (orderEntry.getFQty().intValue() * 2) >= index){
                                    index++;
                                    inPrice = inPrice.add(vo.getAmortizedExpenses());
                                }
                            }
                            if(inPrice.compareTo(BigDecimal.ZERO) == 1){
                                inPrice = inPrice.divide(orderEntry.getFQty(), 6, BigDecimal.ROUND_DOWN);
                            }
                            orderEntry.setFPrice(inPrice);
                            orderEntry.setFTaxPrice(inPrice);
                        }
                    }

                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    if (price.compareTo(BigDecimal.ZERO)<=0){
                        orderEntry.setFIsFree("1");
                    }else {
                        orderEntry.setFIsFree("0");
                    }
                    orderEntry.setFOraText(item.getSkuNo());
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                    //拆箱id
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }

                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //赠送商品
            if (trade.getGifts().size() > 0) {
                for (TradeItem item : trade.getGifts()) {
                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));

                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack Gifts item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }
                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    orderEntry.setFPrice(item.getPrice());
                    orderEntry.setFTaxPrice(item.getPrice());
                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    orderEntry.setFIsFree("1");
                    orderEntry.setFOraText(item.getSkuNo());
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());

                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }
                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //判断是否有邮费
            if (trade.getTradePrice() != null && trade.getTradePrice().getDeliveryPrice().compareTo(BigDecimal.ZERO) == 1){
                FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                Map FMaterialId = new HashMap();
                FMaterialId.put("FNumber",kingdeeFreight);
                orderEntry.setFMaterialId(FMaterialId);
                orderEntry.setFPrice(trade.getTradePrice().getDeliveryPrice());
                orderEntry.setFTaxPrice(trade.getTradePrice().getDeliveryPrice());
                orderEntry.setFQty(BigDecimal.ONE);
                fSaleOrderEntry.add(orderEntry);
                Map paymentOrderState = new HashMap();
                paymentOrderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
                order.setOrderState(paymentOrderState);
            }
            //判断是否有打包费
            if (Objects.nonNull(trade.getTradePrice()) && trade.getTradePrice().getPackingPrice().compareTo(BigDecimal.ZERO) == 1){
                FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                Map FMaterialId = new HashMap();
                FMaterialId.put("FNumber",kingdeePacking);
                orderEntry.setFMaterialId(FMaterialId);
                orderEntry.setFPrice(trade.getTradePrice().getPackingPrice());
                orderEntry.setFTaxPrice(trade.getTradePrice().getPackingPrice());
                orderEntry.setFQty(BigDecimal.ONE);
                fSaleOrderEntry.add(orderEntry);
                Map paymentOrderState = new HashMap();
                paymentOrderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
                order.setOrderState(paymentOrderState);
            }


            order.setFSaleOrderEntry(fSaleOrderEntry);
            log.info("==========TradeService.pushSalesOrderkingdee order:{}",JSONObject.toJSONString(order));
            //登录财务系统
            Map<String,Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user",kingdeeUser);
            requestLogMap.put("pwd",kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
            if (StringUtils.isNotEmpty(loginToken)){
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",order);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(orderUrl, requestMap, loginToken);
                log.info("==========TradeService.pushSalesOrderkingdee result1:{}",result1.getResultData());
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushSalesOrderkingdee result1:{} code:{}", result1.getResultData(),kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                }else {
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeeOrder.setInstructions(result1.getResultData());
            }else {
                log.error("TradeService.pushSalesOrderkingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeeOrder.setInstructions(res);
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        }catch (Exception e){
            log.error("TradeService.pustSalesOrderkingdee error:{}",e);
            String res = "金蝶推送失败";
            tradePushKingdeeOrder.setInstructions(res);
            tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        }finally {
            if (number == 0) {
                tradePushKingdeeOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setCancelOperation("创建");
                tradePushKingdeeOrderRepository.save(tradePushKingdeeOrder);
            }else {
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrderRepository.updatePushKingdeeOrderState(tradePushKingdeeOrder);
            }
        }
    }

    /**
     *
     * @param tid
     */
    public void newPileShoppingCartPushSalesOrderkingdee(String tid){
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee tid:{}",tid);
        NewPileTrade trade = newPileTradeService.detail(tid);
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee trade:{}",JSONObject.toJSONString(trade));
        //查找销售订单是否存在
        Integer number = tradePushKingdeeOrderRepository.selcetPushKingdeeOrderNumber(trade.getId());
        TradePushKingdeeOrder tradePushKingdeeOrder = new TradePushKingdeeOrder();
        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        tradePushKingdeeOrder.setOrderCode(trade.getId());
        tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CREATE.toOrderStatus());
        KingdeeSalesOrder order = new KingdeeSalesOrder();
        try {
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("TradeService.pushSalesOrderkingdee Lack employeeResponse");
                tradePushKingdeeOrder.setInstructions("Lack employeeResponse");
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            Map FCustId = new HashMap();
            FCustId.put("FNumber",trade.getBuyer().getAccount());
            order.setFCustId(FCustId);
            order.setFDate(DateUtil.nowDate());
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            order.setFSaleOrgId(fSaleOrgId);//组织
            order.setFFreight(trade.getTradePrice().getDeliveryPrice());
            Map FSalerId = new HashMap();
            FSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            order.setFSalerId(FSalerId);
            order.setOrderNumber(trade.getId());
            Map orderState = new HashMap();
            orderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
            order.setOrderState(orderState);
            Map province = new HashMap();
            province.put("FNumber",trade.getConsignee().getProvinceId());
            order.setProvinceId(province);
            Map city = new HashMap();
            city.put("FNumber",trade.getConsignee().getCityId());
            order.setCityId(city);
            Map area = new HashMap();
            area.put("FNumber",trade.getConsignee().getAreaId());
            order.setAreaId(area);
            order.setDetailAddress(trade.getConsignee().getDetailAddress());
            //收货人
            if (StringUtils.isNotEmpty(trade.getConsignee().getName())){
                order.setFContact(trade.getConsignee().getName());
            }
            //联系电话
            if (StringUtils.isNotEmpty(trade.getConsignee().getPhone())){
                order.setFLinkPhone(trade.getConsignee().getPhone());
            }
            Map fTranType = new HashMap();
            fTranType.put("FNumber",switchWMSPushOrderType(trade.getDeliverWay()));
            order.setFTranType(fTranType);
            if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
                order.setFLogName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
                order.setFLogPhone(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
                order.setFLogAddress(trade.getLogisticsCompanyInfo().getLogisticsAddress());
                order.setFLogSite(trade.getLogisticsCompanyInfo().getReceivingPoint());
            }
            order.setFLogNote(trade.getBuyerRemark());
            //仓库编码
            Map fStock = new HashMap();
            if(trade.getWareId() == 1L || trade.getWareId() == 45L){
                fStock.put("FNumber",ERPWMSConstants.MAIN_WH);
            }else if(trade.getWareId() == 46L){
                fStock.put("FNumber",ERPWMSConstants.SUB_WH);
            }else if(trade.getWareId() == 47L){
                fStock.put("FNumber",ERPWMSConstants.STORE_WH);
            }else{
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"推送金蝶找不到对应仓库ID");
            }
            order.setFStock(fStock);
            List<FSaleOrderEntry> fSaleOrderEntry = new ArrayList<>();
            //购买商品
            if (trade.getTradeItems().size() > 0){
                for (TradeItem item : trade.getTradeItems()) {

                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    log.info("========prefix:{}，ErpSkuNo：{}",prefix,item.getErpSkuNo());
                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack TradeItems item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }

                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setFQty(item.getDivisorFlag().multiply(BigDecimal.valueOf(item.getNum())));
                    }
                    BigDecimal balance = (null != item.getWalletSettlements() && !item.getWalletSettlements().isEmpty()) ?  item.getWalletSettlements().get(0).getReduceWalletPrice()
                            : BigDecimal.ZERO;
                    BigDecimal price = item.getSplitPrice().add(balance);
                    price = price.divide(orderEntry.getFQty(),6,BigDecimal.ROUND_DOWN);
                    orderEntry.setFPrice(price);
                    orderEntry.setFTaxPrice(price);
                    if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
                        List<InventoryDetailSamountVO> inventoryDetailSamountVOS =
                                inventoryDetailSamountProvider.getInventoryByTakeId(InventoryDetailSamountRequest.builder()
                                        .takeId(trade.getId()).build()).getContext().getInventoryDetailSamountVOS();
                        if(null != inventoryDetailSamountVOS && !inventoryDetailSamountVOS.isEmpty()){
                            int index = 1;
                            BigDecimal inPrice = BigDecimal.ZERO;
                            for (InventoryDetailSamountVO vo : inventoryDetailSamountVOS){
                                if(item.getSkuId().equals(vo.getGoodsInfoId()) && vo.getTakeFlag() == 1 && (orderEntry.getFQty().intValue() * 2) >= index){
                                    index++;
                                    inPrice = inPrice.add(vo.getAmortizedExpenses());
                                }
                            }
                            if(inPrice.compareTo(BigDecimal.ZERO) == 1){
                                inPrice = inPrice.divide(orderEntry.getFQty(), 6, BigDecimal.ROUND_DOWN);
                            }
                            orderEntry.setFPrice(inPrice);
                            orderEntry.setFTaxPrice(inPrice);
                        }
                    }

                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    if (price.compareTo(BigDecimal.ZERO)<=0){
                        orderEntry.setFIsFree("1");
                    }else {
                        orderEntry.setFIsFree("0");
                    }
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                    //拆箱id
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }

                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //赠送商品
            if (trade.getGifts().size() > 0) {
                for (TradeItem item : trade.getGifts()) {
                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));

                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack Gifts item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }
                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    orderEntry.setFPrice(item.getPrice());
                    orderEntry.setFTaxPrice(item.getPrice());
                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    orderEntry.setFIsFree("1");
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());

                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }
                    fSaleOrderEntry.add(orderEntry);
                }
            }

            order.setFSaleOrderEntry(fSaleOrderEntry);
            log.info("==========TradeService.pushSalesOrderkingdee order:{}",JSONObject.toJSONString(order));
            //登录财务系统
            Map<String,Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user",kingdeeUser);
            requestLogMap.put("pwd",kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
            if (StringUtils.isNotEmpty(loginToken)){
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",order);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(newPileOrderUrl, requestMap, loginToken);
                log.info("==========TradeService.pushSalesOrderkingdee result1:{}",result1.getResultData());
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushSalesOrderkingdee result1:{} code:{}", result1.getResultData(),kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                }else {
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeeOrder.setInstructions(result1.getResultData());
            }else {
                log.error("TradeService.pushSalesOrderkingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeeOrder.setInstructions(res);
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        }catch (Exception e){
            log.error("TradeService.pustSalesOrderkingdee error:{}",e);
            String res = "金蝶推送失败";
            tradePushKingdeeOrder.setInstructions(res);
            tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        }finally {
            if (number == 0) {
                tradePushKingdeeOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setCancelOperation("创建");
                tradePushKingdeeOrderRepository.save(tradePushKingdeeOrder);
            }else {
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrderRepository.updatePushKingdeeOrderState(tradePushKingdeeOrder);
            }
        }
    }

    /**
     * 购物车向金蝶推送订单数据
     * @param
     */
    /*public void newPileShoppingCartPushSalesOrderkingdee(String tid){
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee tid:{}",tid);
        NewPileTrade trade = newPileTradeService.detail(tid);
        log.info("KingdeePushOrder.shoppingCartPushSalesOrderkingdee trade:{}",JSONObject.toJSONString(trade));
        //查找销售订单是否存在
        Integer number = tradePushKingdeeOrderRepository.selcetPushKingdeeOrderNumber(trade.getId());
        TradePushKingdeeOrder tradePushKingdeeOrder = new TradePushKingdeeOrder();
        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.CREATE.toStatus());
        tradePushKingdeeOrder.setOrderCode(trade.getId());
        tradePushKingdeeOrder.setOrderStatus(PushKingdeeOrderStatusEnum.CREATE.toOrderStatus());
        KingdeeSalesOrder order = new KingdeeSalesOrder();
        try {
            //获取销售员erp中的id
            EmployeeOptionalByIdResponse employeeResponse = employeeQueryProvider.getOptionalById(EmployeeOptionalByIdRequest.builder().
                    employeeId(trade.getBuyer().getEmployeeId()).build()).getContext();
            if (Objects.isNull(employeeResponse) || StringUtils.isEmpty(employeeResponse.getErpEmployeeId())){
                log.info("TradeService.pushSalesOrderkingdee Lack employeeResponse");
                tradePushKingdeeOrder.setInstructions("Lack employeeResponse");
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                return;
            }
            Map FCustId = new HashMap();
            FCustId.put("FNumber",trade.getBuyer().getAccount());
            order.setFCustId(FCustId);
            order.setFDate(DateUtil.nowDate());
            Map fSaleOrgId = new HashMap();
            fSaleOrgId.put("FNumber",kingdeeOrganization);
            order.setFSaleOrgId(fSaleOrgId);//组织
            order.setFFreight(trade.getTradePrice().getDeliveryPrice());
            Map FSalerId = new HashMap();
            FSalerId.put("FNumber",employeeResponse.getErpEmployeeId());
            order.setFSalerId(FSalerId);
            order.setOrderNumber(trade.getId());
            Map orderState = new HashMap();
            orderState.put("FNumber", KingdeeOrderStateEnums.NOTPAYING.toValue());
            order.setOrderState(orderState);
            Map province = new HashMap();
            province.put("FNumber",trade.getConsignee().getProvinceId());
            order.setProvinceId(province);
            Map city = new HashMap();
            city.put("FNumber",trade.getConsignee().getCityId());
            order.setCityId(city);
            Map area = new HashMap();
            area.put("FNumber",trade.getConsignee().getAreaId());
            order.setAreaId(area);
            order.setDetailAddress(trade.getConsignee().getDetailAddress());
            //收货人
            if (StringUtils.isNotEmpty(trade.getConsignee().getName())){
                order.setFContact(trade.getConsignee().getName());
            }
            //联系电话
            if (StringUtils.isNotEmpty(trade.getConsignee().getPhone())){
                order.setFLinkPhone(trade.getConsignee().getPhone());
            }
            Map fTranType = new HashMap();
            fTranType.put("FNumber",switchWMSPushOrderType(trade.getDeliverWay()));
            order.setFTranType(fTranType);
            if (DeliverWay.LOGISTICS.equals(trade.getDeliverWay()) && Objects.nonNull(trade.getLogisticsCompanyInfo())) {
                order.setFLogName(trade.getLogisticsCompanyInfo().getLogisticsCompanyName());
                order.setFLogPhone(trade.getLogisticsCompanyInfo().getLogisticsCompanyPhone());
                order.setFLogAddress(trade.getLogisticsCompanyInfo().getLogisticsAddress());
                order.setFLogSite(trade.getLogisticsCompanyInfo().getReceivingPoint());
            }
            order.setFLogNote(trade.getBuyerRemark());
            //仓库编码
            Map fStock = new HashMap();
            if(trade.getWareId() == 1L || trade.getWareId() == 45L){
                fStock.put("FNumber",ERPWMSConstants.MAIN_WH);
            }else if(trade.getWareId() == 46L){
                fStock.put("FNumber",ERPWMSConstants.SUB_WH);
            }else if(trade.getWareId() == 47L){
                fStock.put("FNumber",ERPWMSConstants.STORE_WH);
            }else{
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"推送金蝶找不到对应仓库ID");
            }
            order.setFStock(fStock);
            List<FSaleOrderEntry> fSaleOrderEntry = new ArrayList<>();
            //购买商品
            if (trade.getTradeItems().size() > 0){
                for (TradeItem item : trade.getTradeItems()) {

                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));
                    log.info("========prefix:{}，ErpSkuNo：{}",prefix,item.getErpSkuNo());
                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack TradeItems item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }

                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setFQty(item.getDivisorFlag().multiply(BigDecimal.valueOf(item.getNum())));
                    }
                    BigDecimal balance = (null != item.getWalletSettlements() && !item.getWalletSettlements().isEmpty()) ?  item.getWalletSettlements().get(0).getReduceWalletPrice()
                            : BigDecimal.ZERO;
                    BigDecimal price = item.getSplitPrice().add(balance);
                    price = price.divide(orderEntry.getFQty(),6,BigDecimal.ROUND_DOWN);
                    orderEntry.setFPrice(price);
                    orderEntry.setFTaxPrice(price);
                    if (StringUtils.isNotEmpty(trade.getActivityType()) && trade.getActivityType().equals(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType())) {
                        List<InventoryDetailSamountVO> inventoryDetailSamountVOS =
                                inventoryDetailSamountProvider.getInventoryByTakeId(InventoryDetailSamountRequest.builder()
                                        .takeId(trade.getId()).build()).getContext().getInventoryDetailSamountVOS();
                        if(null != inventoryDetailSamountVOS && !inventoryDetailSamountVOS.isEmpty()){
                            int index = 1;
                            BigDecimal inPrice = BigDecimal.ZERO;
                            for (InventoryDetailSamountVO vo : inventoryDetailSamountVOS){
                                if(item.getSkuId().equals(vo.getGoodsInfoId()) && vo.getTakeFlag() == 1 && (orderEntry.getFQty().intValue() * 2) >= index){
                                    index++;
                                    inPrice = inPrice.add(vo.getAmortizedExpenses());
                                }
                            }
                            if(inPrice.compareTo(BigDecimal.ZERO) == 1){
                                inPrice = inPrice.divide(orderEntry.getFQty(), 6, BigDecimal.ROUND_DOWN);
                            }
                            orderEntry.setFPrice(inPrice);
                            orderEntry.setFTaxPrice(inPrice);
                        }
                    }

                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    if (price.compareTo(BigDecimal.ZERO)<=0){
                        orderEntry.setFIsFree("1");
                    }else {
                        orderEntry.setFIsFree("0");
                    }
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());
                    //拆箱id
                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }

                    fSaleOrderEntry.add(orderEntry);
                }
            }
            //赠送商品
            if (trade.getGifts().size() > 0) {
                for (TradeItem item : trade.getGifts()) {
                    Long wareId = trade.getWareId();
                    if(wareId == null || wareId.longValue() == retailWareId.longValue()){
                        wareId = 1L;
                    }
                    String prefix = Constants.ERP_NO_PREFIX.get(wareId);
                    item.setErpSkuNo(item.getErpSkuNo().replace(prefix,""));

                    if (StringUtils.isEmpty(item.getErpSkuNo()) || item.getNum() == null || item.getPrice() == null || StringUtils.isEmpty(item.getSkuNo())){
                        tradePushKingdeeOrder.setInstructions("Lack Gifts item");
                        tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PARAMETERERROR.toStatus());
                        return;
                    }
                    FSaleOrderEntry orderEntry = new FSaleOrderEntry();
                    Map FMaterialId = new HashMap();
                    orderEntry.setFQty(BigDecimal.valueOf(item.getNum()));
                    orderEntry.setFPrice(item.getPrice());
                    orderEntry.setFTaxPrice(item.getPrice());
                    FMaterialId.put("FNumber",item.getErpSkuNo());

                    orderEntry.setFMaterialId(FMaterialId);
                    orderEntry.setFIsFree("1");
                    //新增税率
//                    orderEntry.setFEntryTaxRate(new BigDecimal(null != goodsIdskeyMap && null != goodsIdskeyMap.get(item.getSkuId()+"") ? goodsIdskeyMap.get(item.getSkuId()+""):"0"));
                    //新增打包字段
                    orderEntry.setFPackPrice(Objects.isNull(trade.getTradePrice().getPackingPrice())?BigDecimal.ZERO:trade.getTradePrice().getPackingPrice());

                    if(Objects.nonNull(item.getDevanningId())){
                        orderEntry.setF_ora_SPZJ(item.getDevanningId().toString());
                    }
                    fSaleOrderEntry.add(orderEntry);
                }
            }

            order.setFSaleOrderEntry(fSaleOrderEntry);
            log.info("==========TradeService.pushSalesOrderkingdee order:{}",JSONObject.toJSONString(order));
            //登录财务系统
            Map<String,Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user",kingdeeUser);
            requestLogMap.put("pwd",kingdeePwd);
            String loginToken = kingdeeLoginUtils.userLoginKingdee(requestLogMap,loginUrl);
            if (StringUtils.isNotEmpty(loginToken)){
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",order);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(newPileOrderUrl, requestMap, loginToken);
                log.info("==========TradeService.pushSalesOrderkingdee result1:{}",result1.getResultData());
                KingDeeResult kingDeeResult = JSONObject.parseObject(result1.getResultData(), KingDeeResult.class);
                log.info("TradeService.pushSalesOrderkingdee result1:{} code:{}", result1.getResultData(),kingDeeResult.getCode());
                if (Objects.nonNull(kingDeeResult) && kingDeeResult.getCode().equals("0")){
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.PUSHSUCCESS.toStatus());
                }else {
                    tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
                }
                tradePushKingdeeOrder.setInstructions(result1.getResultData());
            }else {
                log.error("TradeService.pushSalesOrderkingdee push kingdee error");
                String res = "金蝶登录失败";
                tradePushKingdeeOrder.setInstructions(res);
                tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            }
        }catch (Exception e){
            log.error("TradeService.pustSalesOrderkingdee error:{}",e);
            String res = "金蝶推送失败";
            tradePushKingdeeOrder.setInstructions(res);
            tradePushKingdeeOrder.setPushStatus(PushKingdeeStatusEnum.FAILEDPUSH.toStatus());
            return;
        }finally {
            if (number == 0) {
                tradePushKingdeeOrder.setCreateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrder.setCancelOperation("创建");
                tradePushKingdeeOrderRepository.save(tradePushKingdeeOrder);
            }else {
                tradePushKingdeeOrder.setUpdateTime(LocalDateTime.now());
                tradePushKingdeeOrderRepository.updatePushKingdeeOrderState(tradePushKingdeeOrder);
            }
        }
    }*/

    //物流类型
    private String switchWMSPushOrderType(DeliverWay deliverWay) {
        return tradeService.switchWMSPushOrderType(deliverWay);
        /*if (DeliverWay.PICK_SELF.equals(deliverWay)) {
            return "ZTCK";
        } else if (DeliverWay.EXPRESS.equals(deliverWay)) {
            return "KDCK";
        } else if (DeliverWay.DELIVERY_HOME.equals(deliverWay)) {
            return "PSCK";
        } else {
            return "WLCK";
        }*/
    }

}
