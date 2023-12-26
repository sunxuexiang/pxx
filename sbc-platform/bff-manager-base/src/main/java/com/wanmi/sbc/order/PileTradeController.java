package com.wanmi.sbc.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.configure.GoodsInfoImportListener;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByIdRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.ValidStoreByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaListResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CompanyInfoDTO;
import com.wanmi.sbc.customer.bean.dto.StoreInfoDTO;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByConditionResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.GiftGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeListForUseByCustomerIdRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponInfoQueryRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponsByCodeIdsRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponCodeListByConditionResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CouponInfosQueryResponse;
import com.wanmi.sbc.marketing.bean.dto.CouponCodeDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponCodeStatus;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftDetailVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.order.PurchaseApiRequest;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.order.api.provider.trade.*;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.order.api.request.purchase.ImportPurchaseMarketingRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseCalcAmountRequest;

import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseListResponse;
import com.wanmi.sbc.order.api.response.purchase.PurchaseMarketingResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetFreightResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGroupByGroupIdsResponse;
import com.wanmi.sbc.order.api.response.trade.TradeTotalFreightPriceResponse;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.FlowState;
import com.wanmi.sbc.order.bean.enums.ShipperType;
import com.wanmi.sbc.order.bean.vo.*;
import com.wanmi.sbc.shopcart.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.shopcart.bean.dto.PurchaseCalcAmountDTO;
import com.wanmi.sbc.order.request.DeliveryHomeFlagRequest;
import com.wanmi.sbc.order.request.DisabledExportRequest;
import com.wanmi.sbc.order.request.TradeExportRequest;
import com.wanmi.sbc.order.response.*;
import com.wanmi.sbc.order.service.ProviderTradeExportBaseService;
import com.wanmi.sbc.order.service.TradeExportService;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.DeliveryQueryProvider;
import com.wanmi.sbc.setting.api.provider.expresscompany.ExpressCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.logisticscompany.LogisticsCompanyQueryProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.DeliveryQueryRequest;
import com.wanmi.sbc.setting.api.request.expresscompany.ExpressCompanyByIdRequest;
import com.wanmi.sbc.setting.api.request.logisticscompany.LogisticsCompanyByIdRequest;
import com.wanmi.sbc.setting.api.response.logisticscompany.LogisticsCompanyByIdResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ExpressCompanyVO;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description: s2b囤货订单api
 * @author: jiangxin
 * @create: 2021-09-29 9:10
 */
@Api(tags = "PileTradeController", description = "囤货订单服务 Api")
@RestController
@RequestMapping("/pile/trade")
@Slf4j
@Validated
public class PileTradeController {

    @Autowired
    private PileTradeProvider tradeProvider;

    @Autowired
    private ProviderTradeProvider providerTradeProvider;

    @Autowired
    private ProviderTradeQueryProvider providerTradeQueryProvider;

    @Autowired
    private PileTradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeExportService tradeExportService;

    @Autowired
    private ProviderTradeExportBaseService providerTradeExportBaseService;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private PayOrderQueryProvider payOrderQueryProvider;

    @Autowired
    private DeliveryQueryProvider deliveryQueryProvider;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Autowired
    private ExpressCompanyQueryProvider expressCompanyQueryProvider;

    @Autowired
    private ParentCustomerRelaQueryProvider parentCustomerRelaQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private LogisticsCompanyQueryProvider logisticsCompanyQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private PurchaseProvider purchaseProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private TradeGroupQueryProvider tradeGroupQueryProvider;

    @Autowired
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Autowired
    private PileTradeGroupQueryProvider pileTradeGroupQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    private AtomicInteger exportCount = new AtomicInteger(0);

    private static final String IMPORT_GOODS_ERROR = "IMPORT_GOODS_ERROR:";

    /**
     * 订单发送邮件测试
     *
     * @return
     */
    @ApiOperation(value = "订单发送邮件测试")
    @RequestMapping(value = "/TradeController", method = RequestMethod.GET)
    public BaseResponse sendEmailTranslate() {
        tradeQueryProvider.sendEmailTranslate();
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "订单发送邮件测试", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 分页查询
     *
     * @param tradeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询")
    @EmployeeCheck
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<TradeVO>> page(@RequestBody TradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }

        if (!tradeQueryRequest.getIsBoss()) {
            if (Objects.nonNull(tradeQueryRequest.getTradeState()) && Objects.nonNull(tradeQueryRequest.getTradeState
                    ().getPayState())) {
                tradeQueryRequest.setNotFlowStates(Arrays.asList(FlowState.VOID, FlowState.INIT));
            }
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
        tradeQueryRequest.makeAllAuditFlow();
        return BaseResponse.success(tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage());
    }

    /**
     * 分页查询supplier
     *
     * @param tradeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询")
    @EmployeeCheck
    @RequestMapping(value = "/supplierPage",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<TradeVO>> supplierPage(@RequestBody TradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }

        if (!tradeQueryRequest.getIsBoss()) {
            if (Objects.nonNull(tradeQueryRequest.getTradeState()) && Objects.nonNull(tradeQueryRequest.getTradeState
                    ().getPayState())) {
                tradeQueryRequest.setNotFlowStates(Arrays.asList(FlowState.VOID, FlowState.INIT));
            }
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
        tradeQueryRequest.makeAllAuditFlow();
        return BaseResponse.success(tradeQueryProvider.supplierPageCriteria(TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage());
    }

    /**
     * boss分页查询
     *
     * @param tradeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询")
    @EmployeeCheck
    @RequestMapping(value = "/bossPage",method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<TradeVO>> bossPage(@RequestBody TradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }
        //如果存在子账号，也需要把子账号的账单信息查询出来
        if (StringUtils.isNoneBlank(tradeQueryRequest.getBuyerId())){
            ParentCustomerRelaListResponse context = parentCustomerRelaQueryProvider
                    .findAllByParentId(ParentCustomerRelaListRequest.builder().parentId(tradeQueryRequest.getBuyerId()).build()).getContext();
            if (Objects.nonNull(context)&&CollectionUtils.isNotEmpty(context.getParentCustomerRelaVOList())){
                List<String> collect = context.getParentCustomerRelaVOList()
                        .stream().map(ParentCustomerRelaVO::getCustomerId).collect(Collectors.toList());
                collect.add(tradeQueryRequest.getBuyerId());
                tradeQueryRequest.setBuyerId(null);
                tradeQueryRequest.setCustomerIds(collect.toArray());
            }
        }

        if (!tradeQueryRequest.getIsBoss()) {
            if (Objects.nonNull(tradeQueryRequest.getTradeState()) && Objects.nonNull(tradeQueryRequest.getTradeState
                    ().getPayState())) {
                tradeQueryRequest.setNotFlowStates(Arrays.asList(FlowState.VOID, FlowState.INIT));
            }
        }
        //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
        tradeQueryRequest.makeAllAuditFlow();

        return BaseResponse.success(tradeQueryProvider.pageBossCriteria(TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage());
    }



    /**
     * 根据参数查询某订单的运费
     *a
     * @param tradeParams
     * @return
     */
    @ApiOperation(value = "根据参数查询某订单的运费")
    @RequestMapping(value = "/getFreight", method = RequestMethod.POST)
    public BaseResponse<TradeGetFreightResponse> getFreight(@RequestBody TradeParamsRequest tradeParams) {
        Operator operator = commonUtil.getOperator();
        StoreVO store = storeQueryProvider.getValidStoreById(new ValidStoreByIdRequest(Long.parseLong(operator
                .getStoreId()))).getContext().getStoreVO();
        tradeParams.setSupplier(SupplierDTO.builder().storeId(store.getStoreId()).freightTemplateType(store
                .getFreightTemplateType()).build());
        if (DeliverWay.EXPRESS.equals(tradeParams.getDeliverWay())){
            return BaseResponse.success(tradeQueryProvider.getFreight(tradeParams).getContext());
        }else {
            TradeGetFreightResponse result=new TradeGetFreightResponse();
            result.setStoreId(store.getStoreId());
            result.setDeliveryPrice(BigDecimal.ZERO);
            return BaseResponse.success(result);
        }

    }

    /**
     * 根据参数查询某订单的运费
     *
     * @param tradeParams
     * @return
     */
    @ApiOperation(value = "根据参数查询某订单的运费APP端(代客下单)")
    @RequestMapping(value = "/getFreightForAppByList", method = RequestMethod.POST)
    public BaseResponse<TradeTotalFreightPriceResponse> getFreight(@RequestBody List<TradeParamsRequest> tradeParams) {
        Operator operator = commonUtil.getOperator();
        StoreVO store = storeQueryProvider.getValidStoreById(new ValidStoreByIdRequest(Long.parseLong(operator
                .getStoreId()))).getContext().getStoreVO();
        List<TradeGetFreightResponse> result=new ArrayList<>(tradeParams.size());
        tradeParams.forEach(params -> {
            params.setSupplier(SupplierDTO.builder().storeId(store.getStoreId())
                    .freightTemplateType(store.getFreightTemplateType())
                    .companyType(store.getCompanyType()).build());
        });
        List<TradeParamsRequest> tradeParamList = new ArrayList<>();
        //计算totalPrice
        for (TradeParamsRequest inner:tradeParams){
            BigDecimal totalPrice=BigDecimal.ZERO;
            for (TradeItemDTO inside:inner.getOldTradeItems()){
                totalPrice=totalPrice.add(inside.getPrice().multiply(new BigDecimal(inside.getNum())));
            }
            TradePriceDTO tradePriceDTO = new TradePriceDTO();
            tradePriceDTO.setTotalPrice(totalPrice);
            inner.setTradePrice(tradePriceDTO);
        }
        //排除所有非快递方式的订单，直接塞0
        Iterator<TradeParamsRequest> iterator = tradeParams.iterator();
        while (iterator.hasNext()){
            TradeParamsRequest next = iterator.next();
            if (!next.getDeliverWay().equals(DeliverWay.EXPRESS)){
                TradeGetFreightResponse inner=new TradeGetFreightResponse();
                inner.setStoreId(next.getSupplier().getStoreId());
                inner.setDeliveryPrice(BigDecimal.ZERO);
                result.add(inner);
                iterator.remove();
            }else{
                tradeParamList.add(next);
            }
        }
        BigDecimal totalDeliveryPrice=BigDecimal.ZERO;
        result.addAll(tradeParamList.stream().map(params -> tradeQueryProvider.getFreight(params)
                .getContext()).collect(Collectors.toList()));
        for (TradeGetFreightResponse inner: result){
            totalDeliveryPrice = totalDeliveryPrice.add(inner.getDeliveryPrice()==null?BigDecimal.ZERO:inner.getDeliveryPrice());
        }
        return BaseResponse.success(new TradeTotalFreightPriceResponse(totalDeliveryPrice));
    }


    /**
     * 创建订单
     *
     * @param tradeCreateRequest
     * @return
     */
    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    @LcnTransaction
    public ResponseEntity<BaseResponse> create(@RequestBody @Valid TradeCreateDTO tradeCreateRequest) {
        Operator operator = commonUtil.getOperator();
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        Long storeId = commonUtil.getStoreId();
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId))
                .getContext();
        if (DeliverWay.PICK_SELF.equals(tradeCreateRequest.getDeliverWay())){
            validPickUpPoint(tradeCreateRequest,storeId);
        }
        if (Objects.isNull(tradeCreateRequest.getWareId())){
            throw new SbcRuntimeException("K-000009");
        }
        WareHouseVO wareHouseVO1 = commonUtil.getWareHouseByWareId(tradeCreateRequest.getWareId());
        if (Objects.isNull(wareHouseVO1.getWareCode())){
            throw new SbcRuntimeException("K-000009");
        }
        tradeCreateRequest.setWareHouseCode(wareHouseVO1.getWareCode());
        //1.校验与包装订单信息-与业务员app代客下单公用
        TradeVO trade = tradeQueryProvider.wrapperBackendCommit(TradeWrapperBackendCommitRequest.builder().operator(operator)
                .companyInfo(KsBeanUtil.convert(companyInfo, CompanyInfoDTO.class)).storeInfo(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
                .tradeCreate(tradeCreateRequest).build()).getContext().getTradeVO();
        List<TradeAddDTO> tradeAddDTOS = new ArrayList<>();

        if (DeliverWay.LOGISTICS.equals(tradeCreateRequest.getDeliverWay())){
            if (Objects.isNull(tradeCreateRequest.getLogisticsId())){
                throw new SbcRuntimeException("K-000009");
            }
            BaseResponse<LogisticsCompanyByIdResponse> byId = logisticsCompanyQueryProvider
                    .getById(LogisticsCompanyByIdRequest.builder().id(tradeCreateRequest.getLogisticsId()).build());
            if (Objects.isNull(byId.getContext())) {
                throw new SbcRuntimeException("K-170001");
            }
            LogisticsCompanyVO logisticsCompanyVO = byId.getContext().getLogisticsCompanyVO();
            LogisticsInfoVO logisticsInfoVo = new LogisticsInfoVO();
            logisticsInfoVo.setId(tradeCreateRequest.getLogisticsId().toString());
            logisticsInfoVo.setCompanyNumber(logisticsCompanyVO.getCompanyNumber());
            logisticsInfoVo.setLogisticsAddress(logisticsCompanyVO.getLogisticsAddress());
            logisticsInfoVo.setLogisticsCompanyName(logisticsCompanyVO.getLogisticsName());
            logisticsInfoVo.setLogisticsCompanyPhone(logisticsCompanyVO.getLogisticsPhone());
            trade.setLogisticsCompanyInfo(logisticsInfoVo);
        }

        if (Objects.isNull(tradeCreateRequest.getTradePrice())
                || Objects.isNull(tradeCreateRequest.getTradePrice().getDeliveryPrice())
                || tradeCreateRequest.getTradePrice().isEnableDeliveryPrice()) {
            List<TradeVO> tradeVO = tradeProvider.queryDelivery(TradeQueryDeliveryBatchRequest.builder()
                    .tradeDTOList(Collections.singletonList(trade)).build()).getContext().getTradeVO();
            tradeAddDTOS = KsBeanUtil.convert(tradeVO, TradeAddDTO.class);
        } else {
            tradeAddDTOS = Collections.singletonList(KsBeanUtil.convert(trade, TradeAddDTO.class, SerializerFeature.DisableCircularReferenceDetect));
        }


        //2.订单入库(转换成list,传入批量创建订单的service方法,同一套逻辑,能够回滚)
        TradeAddBatchRequest tradeAddBatchRequest = TradeAddBatchRequest.builder().tradeDTOList(tradeAddDTOS)
                .operator(operator)
                .build();


        tradeProvider.addBatch(tradeAddBatchRequest);

        /**
         * 创建订单成功删除购物车数据
         */
        if(CollectionUtils.isNotEmpty(tradeAddDTOS)){

            List<String> PurchaseDeleteGoodsInfoIds = Lists.newArrayList();

            tradeAddDTOS.forEach(t->{
                PurchaseDeleteGoodsInfoIds.addAll(t.getTradeItems().stream().map(ti->ti.getSkuId()).collect(Collectors.toList()));
            });

            // log.info("PurchaseDeleteGoodsInfoIds ====== {}",PurchaseDeleteGoodsInfoIds);

            PurchaseApiRequest request = new PurchaseApiRequest();
            request.setCustomerId(tradeCreateRequest.getCustom());
            request.setInviteeId("0");
            request.setGoodsInfoIds(PurchaseDeleteGoodsInfoIds);
            purchaseProvider.delete(request);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "创建订单", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());

    }

    /**
     * 查询订单付款记录
     */
    @ApiOperation(value = "查询订单付款记录")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/payOrder/{tid}", method = RequestMethod.GET)
    public BaseResponse<FindPayOrderResponse> payOrder(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        FindPayOrderResponse payOrderResponse = null;
        try {
            BaseResponse<FindPayOrderResponse> response = payOrderQueryProvider.findPayOrder(FindPayOrderRequest.builder().value(trade.getId()).build());

            payOrderResponse = response.getContext();

        } catch (SbcRuntimeException e) {
            if ("K-070001".equals(e.getErrorCode())) {
                payOrderResponse = new FindPayOrderResponse();
                payOrderResponse.setPayType(PayType.fromValue(Integer.valueOf(trade.getPayInfo().getPayTypeId())));
                payOrderResponse.setTotalPrice(trade.getTradePrice().getTotalPrice());
            }
        }
        return BaseResponse.success(payOrderResponse);
    }

    /**
     * 修改卖家备注
     *
     * @param tid
     * @param tradeRemedyRequest
     * @return
     */
    @ApiOperation(value = "修改卖家备注")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/remark/{tid}", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> sellerRemark(@PathVariable String tid, @RequestBody TradeRemedyDTO
            tradeRemedyRequest) {

        TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest = TradeRemedySellerRemarkRequest.builder()
                .sellerRemark(tradeRemedyRequest.getSellerRemark())
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        tradeProvider.remedySellerRemark(tradeRemedySellerRemarkRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "修改卖家备注", "操作成功：交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 导出订单
     *
     * @return
     */
    @ApiOperation(value = "导出订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params/{encrypted}/{encryptedable}", method = RequestMethod.GET)
    public void export(@PathVariable String encrypted,@PathVariable String encryptedable, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            String decryptedable=new String(Base64.getUrlDecoder().decode(encryptedable.getBytes()));
            TradeExportRequest tradeExportRequest = JSON.parseObject(decrypted, TradeExportRequest.class);
            DisabledExportRequest disabledExportRequest =JSON.parseObject(decryptedable, DisabledExportRequest.class);
            Operator operator = commonUtil.getOperator();
            log.debug(String.format("/trade/export/params, employeeId=%s", operator.getUserId()));

            String adminId = operator.getAdminId();
            if (StringUtils.isNotEmpty(adminId)) {
                tradeExportRequest.setSupplierId(Long.valueOf(adminId));
            }

            //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
            tradeExportRequest.makeAllAuditFlow();
            TradeQueryDTO tradeQueryDTO = KsBeanUtil.convert(tradeExportRequest, TradeQueryDTO.class);
            DisabledDTO disabledDTO=KsBeanUtil.convert(disabledExportRequest,DisabledDTO.class);

            List<TradeVO> trades = tradeQueryProvider.listTradeExport(TradeListExportRequest.builder().tradeQueryDTO(tradeQueryDTO).build()).getContext().getTradeVOList();
            if(CollectionUtils.isNotEmpty(trades)){
                List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();

                //查询所有订单groupId;
                List<String> groupIds = trades.stream().filter(t->Objects.nonNull(t.getGroupId())).map(t -> t.getGroupId()).collect(Collectors.toList());
                TradeGroupByGroupIdsResponse tradeGroupIdsResponse = tradeGroupQueryProvider.getTradeGroupByGroupIds(TradeGroupByIdsRequest.builder().groupId(groupIds).build()).getContext();

                TradeGroupByGroupIdsResponse byGroupIdsResponse = pileTradeGroupQueryProvider.getTradeGroupByGroupIds(TradeGroupByIdsRequest
                        .builder().groupId(groupIds).build()).getContext();

                List<TradeGroupVO> groupVOArrayList = Lists.newArrayList();

                if(Objects.nonNull(tradeGroupIdsResponse) && CollectionUtils.isNotEmpty(tradeGroupIdsResponse.getTradeGroupVOS())){
                    groupVOArrayList.addAll(tradeGroupIdsResponse.getTradeGroupVOS());
                }

                if(Objects.nonNull(byGroupIdsResponse) && CollectionUtils.isNotEmpty(byGroupIdsResponse.getTradeGroupVOS())){
                    groupVOArrayList.addAll(byGroupIdsResponse.getTradeGroupVOS());
                }

                if(CollectionUtils.isNotEmpty(employeeList)){
                    List<TradeGroupVO> finalGroupVOArrayList = groupVOArrayList;
                    List<CouponCodeDTO> couponCodeDTOList = null;
                    List<CouponInfoVO> couponInfoList = null;
                    if(CollectionUtils.isNotEmpty(finalGroupVOArrayList)){

                        List<String> codeIdList = finalGroupVOArrayList.stream().map(g -> g.getCommonCoupon()).collect(Collectors.toList())
                                .stream().map(tc -> tc.getCouponCodeId()).collect(Collectors.toList());
                        //查询店铺、商品优惠券
                        List<String> storeCouponCodeIds = trades.stream().filter(t -> Objects.nonNull(t.getTradeCoupon())).map(t -> t.getTradeCoupon().getCouponCodeId()).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(storeCouponCodeIds)){
                            codeIdList.addAll(storeCouponCodeIds);
                        }

                        if(CollectionUtils.isNotEmpty(codeIdList)){
                            CouponCodeListByConditionResponse context = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest.builder().couponCodeIds(codeIdList).build()).getContext();

                            couponCodeDTOList = context.getCouponCodeList();

                            CouponInfosQueryResponse infosQueryResponse = couponInfoQueryProvider.queryCouponInfos(CouponInfoQueryRequest
                                    .builder()
                                    .couponIds(context.getCouponCodeList().stream().map(cc->cc.getCouponId()).collect(Collectors.toList()))
                                    .build()).getContext();
                            couponInfoList = infosQueryResponse.getCouponCodeList();
                        }
                    }

                    List<CouponCodeDTO> finalCouponCodeDTOList = couponCodeDTOList;
                    List<CouponInfoVO> finalCouponInfoList = couponInfoList;
                    trades.forEach(t->{
                        if(Objects.nonNull(t.getBuyer()) && Objects.nonNull(t.getBuyer().getEmployeeId())){
                            EmployeeListVO employeeListVO = employeeList.stream().filter(e -> e.getEmployeeId().equals(t.getBuyer().getEmployeeId()))
                                    .findFirst().orElse(null);
                            if(Objects.nonNull(employeeListVO)){
                                t.setEmployeeName(employeeListVO.getEmployeeName());
                            }
                        }

                        //平台优惠券展示
                        if(Objects.isNull(t.getTradeCoupon())){
                            TradeGroupVO groupVO = null;
                            if(CollectionUtils.isNotEmpty(finalGroupVOArrayList)){
                                groupVO = finalGroupVOArrayList.stream().filter(f->f.getId().equals(t.getGroupId())).findFirst().orElse(null);
                            }
                            if(Objects.nonNull(groupVO)){
                                t.setTradeCoupon(groupVO.getCommonCoupon());
                                if(CollectionUtils.isNotEmpty(finalCouponCodeDTOList)){
                                    TradeGroupVO finalGroupVO = groupVO;
                                    CouponCodeDTO couponCodeDTO = finalCouponCodeDTOList.stream().filter(fc -> finalGroupVO.getCommonCoupon().getCouponCodeId().equals(fc.getCouponCodeId())).findFirst().orElse(null);
                                    if(Objects.nonNull(couponCodeDTO) && CollectionUtils.isNotEmpty(finalCouponInfoList)){
                                        CouponInfoVO couponInfoVO = finalCouponInfoList.stream().filter(ci -> couponCodeDTO.getCouponId().equals(ci.getCouponId())).findFirst().orElse(null);
                                        if(Objects.nonNull(couponInfoVO)){
                                            t.getTradeCoupon().setCouponName(couponInfoVO.getCouponName());
                                        }
                                    }
                                }
                            }
                        }else{
                            //店铺、单品优惠券
                            if(CollectionUtils.isNotEmpty(finalCouponCodeDTOList)){
                                CouponCodeDTO couponCodeDTO = finalCouponCodeDTOList.stream().filter(fc -> t.getTradeCoupon().getCouponCodeId().equals(fc.getCouponCodeId())).findFirst().orElse(null);
                                if(Objects.nonNull(couponCodeDTO) && CollectionUtils.isNotEmpty(finalCouponInfoList)){
                                    CouponInfoVO couponInfoVO = finalCouponInfoList.stream().filter(ci -> couponCodeDTO.getCouponId().equals(ci.getCouponId())).findFirst().orElse(null);
                                    if(Objects.nonNull(couponInfoVO)){
                                        t.getTradeCoupon().setCouponName(couponInfoVO.getCouponName());
                                    }
                                }
                            }
                        }
                    });
                }
            }
            //按下单时间降序排列
            Comparator<TradeVO> c = Comparator.comparing(a -> a.getTradeState().getCreateTime());
            trades = trades.stream().sorted(
                    c.reversed()
            ).collect(Collectors.toList());

            String headerKey = "Content-Disposition";
            LocalDateTime dateTime = LocalDateTime.now();
            String fileName = String.format("批量导出订单_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("/trade/export/params, fileName={},", fileName, e);
            }
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);

            //只导出子订单
            List<String> parentIdList=new ArrayList<>();
            if(disabledDTO.getDisabled().equals("true")){
                trades.forEach(vo->{
                    parentIdList.add(vo.getId());
                });
                List<TradeVO> tradeVOList=providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(parentIdList).build()).getContext().getTradeVOList();
//                System.out.println(")))))))))))))))))))))))))"+tradeVOList);
//                try {
//                    providerTradeExportBaseService.export(tradeVOList, response.getOutputStream(),
//                            Platform.PLATFORM.equals(operator.getPlatform()));
//                    response.flushBuffer();
//                } catch (IOException e) {
//                    throw new SbcRuntimeException(e);
//                }

                // 遍历封装导出信息
                List<ProviderTradeExportVO> tradeExportVOs = new ArrayList<>();
                tradeVOList.forEach(tradeVO -> {
                    ProviderTradeExportVO exportVO;
                    // 商家信息
                    String supplierName = StringUtils.isNotEmpty(tradeVO.getSupplierName()) ? tradeVO.getSupplierName() : "";
                    String supplierCode = StringUtils.isNotEmpty(tradeVO.getSupplierCode()) ? tradeVO.getSupplierCode() : "";
                    String supplierInfo = supplierName + "  " + supplierCode;
                    for (int i = 0; i < tradeVO.getTradeItems().size(); i++) {
                        TradeItemVO tradeItemVO = tradeVO.getTradeItems().get(i);
                        exportVO = new ProviderTradeExportVO();
                        if (i == 0) {
                            KsBeanUtil.copyProperties(tradeVO, exportVO);
                            // 下单时间
                            exportVO.setCreateTime(tradeVO.getTradeState().getCreateTime());
                            // 商家信息
                            exportVO.setSupplierInfo(supplierInfo);
                            // 供应商名称
                            exportVO.setSupplierName(supplierName);
                            // 订单商品金额
                            exportVO.setOrderGoodsPrice(tradeVO.getTradePrice().getTotalPrice());
                            // 订单状态
                            exportVO.setFlowState(tradeVO.getTradeState().getFlowState());
                            // 发货状态
                            exportVO.setDeliverStatus(tradeVO.getTradeState().getDeliverStatus());
                            exportVO.setConsigneeName(tradeVO.getConsignee().getName());
                            exportVO.setDetailAddress(tradeVO.getConsignee().getDetailAddress());
                            exportVO.setConsigneePhone(tradeVO.getConsignee().getPhone());
                        }

                        exportVO.setSkuName(tradeItemVO.getSkuName());
                        exportVO.setSpecDetails(tradeItemVO.getSpecDetails());
                        exportVO.setSkuNo(tradeItemVO.getSkuNo());
                        exportVO.setNum(tradeItemVO.getNum());

                        tradeExportVOs.add(exportVO);
                    }
                });

                try {
                    providerTradeExportBaseService.exportProvider(tradeExportVOs, response.getOutputStream(),Platform.BOSS);
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }

            }else{
                try {
                    tradeExportService.export(trades, response.getOutputStream(),
                            Platform.PLATFORM.equals(operator.getPlatform()));
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }
            }
        } catch (Exception e) {
            log.error("/trade/export/params error: ", e);
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        } finally {
            exportCount.set(0);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "导出订单", "操作成功");
    }


    @ApiOperation(value = "查看订单详情-运营端")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> detail(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(tid)
                .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bBoss)
                .build()).getContext().getTradeVO();
        return BaseResponse.success(trade);
    }

    @ApiOperation(value = "查看订单详情-商家端")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> supplierDetail(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(tid)
                .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bSupplier)
                .build()).getContext().getTradeVO();
        return BaseResponse.success(trade);
    }

    /**
     * 获取打印订单详情
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "获取打印订单详情")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/print/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> detailPrint(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        List<String> spuIds = trade.getTradeItems().stream().map(TradeItemVO::getSpuId).collect(Collectors.toList());
        GoodsByConditionResponse response = goodsQueryProvider
                .listByCondition(GoodsByConditionRequest.builder().goodsIds(spuIds).build()).getContext();
        trade.getTradeItems().stream().forEach(s->{
            Optional<GoodsVO> goodsInfoVOOptional = response.getGoodsVOList().stream()
                    .filter(f->f.getGoodsId().equals(s.getSpuId())).findFirst();
            if(goodsInfoVOOptional.isPresent()){
                GoodsVO goodsVO = goodsInfoVOOptional.get();
                if(Objects.nonNull(goodsVO)){
                    s.setSubTitle(goodsVO.getGoodsSubtitle());
                }
            }
        });
        return BaseResponse.success(trade);
    }


    /**
     * 发货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "发货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/deliver/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    public ResponseEntity<BaseResponse> deliver(@PathVariable String tid, @Valid @RequestBody TradeDeliverRequestDTO
            tradeDeliverRequest) {
        if (tradeDeliverRequest.getShippingItemList().isEmpty() && tradeDeliverRequest.getGiftItemList().isEmpty()) {
            throw new SbcRuntimeException("K-050314");
        }

        TradeDeliveryCheckRequest tradeDeliveryCheckRequest = TradeDeliveryCheckRequest.builder()
                .tid(tid)
                .tradeDeliver(tradeDeliverRequest)
                .build();

        tradeQueryProvider.deliveryCheck(tradeDeliveryCheckRequest);


//        CompositeResponse<ExpressCompany> response
//                = sdkClient.buildClientRequest().post(queryRopRequest, ExpressCompany.class, "expressCompany.detail",
//                "1.0.0");
//        if (!response.isSuccessful()) {
//            throw new SbcRuntimeException(ResultCode.FAILED);
//        }
//
        //发货校验
        ExpressCompanyByIdRequest request = new ExpressCompanyByIdRequest();
        request.setExpressCompanyId(Long.valueOf(tradeDeliverRequest.getDeliverId()));
        ExpressCompanyVO expressCompanyVO = expressCompanyQueryProvider.getById(request).getContext().getExpressCompanyVO();
        TradeDeliverVO tradeDeliver = tradeDeliverRequest.toTradeDevlier(expressCompanyVO);
        tradeDeliver.setShipperType(ShipperType.SUPPLIER);

        TradeDeliverRequest tradeDeliverRequest1 = TradeDeliverRequest.builder()
                .tradeDeliver(KsBeanUtil.convert(tradeDeliver, TradeDeliverDTO.class))
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        String deliverId = tradeProvider.deliver(tradeDeliverRequest1).getContext().getDeliverId();
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "发货", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }


    /**
     * 子单(子单是商家的)发货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "子单发货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/provider/deliver/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    public ResponseEntity<BaseResponse> providerTradedeliver(@PathVariable String tid, @Valid @RequestBody TradeDeliverRequestDTO
            tradeDeliverRequest) {
        if (tradeDeliverRequest.getShippingItemList().isEmpty() && tradeDeliverRequest.getGiftItemList().isEmpty()) {
            throw new SbcRuntimeException("K-050314");
        }

        TradeDeliveryCheckRequest tradeDeliveryCheckRequest = TradeDeliveryCheckRequest.builder()
                .tid(tid)
                .tradeDeliver(tradeDeliverRequest)
                .build();

        providerTradeProvider.providerDeliveryCheck(tradeDeliveryCheckRequest);

        // 发货校验
        ExpressCompanyByIdRequest request = new ExpressCompanyByIdRequest();
        request.setExpressCompanyId(Long.valueOf(tradeDeliverRequest.getDeliverId()));
        ExpressCompanyVO expressCompanyVO = expressCompanyQueryProvider.getById(request).getContext().getExpressCompanyVO();
        TradeDeliverVO tradeDeliver = tradeDeliverRequest.toTradeDevlier(expressCompanyVO);
        tradeDeliver.setShipperType(ShipperType.SUPPLIER);

        TradeDeliverRequest deliverRequest = TradeDeliverRequest.builder()
                .tradeDeliver(KsBeanUtil.convert(tradeDeliver, TradeDeliverDTO.class))
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();
        // 供应商发货处理
        String deliverId = providerTradeProvider.providerDeliver(deliverRequest).getContext().getDeliverId();

        // 供应商订单信息
        TradeVO privateTradeVO = providerTradeQueryProvider.providerGetById(
                        TradeGetByIdRequest.builder()
                                .tid(tid)
                                .build())
                .getContext()
                .getTradeVO();

        // 查询所有子订单信息
        List<TradeVO> tradeVOList = providerTradeQueryProvider.getProviderListByParentId(
                        TradeListByParentIdRequest.builder()
                                .parentTid(privateTradeVO.getParentId())
                                .build())
                .getContext()
                .getTradeVOList();

        // 未发货订单数
        long notYetShippedNum = tradeVOList.stream().filter(tradeVO -> tradeVO.getTradeState().getDeliverStatus().equals(DeliverStatus.NOT_YET_SHIPPED)).count();
        // 已发货订单数
        long shippedNum = tradeVOList.stream().filter(tradeVO -> tradeVO.getTradeState().getDeliverStatus().equals(DeliverStatus.SHIPPED)).count();

        // 父订单发货状态
        DeliverStatus deliverStatus;
        if ((int) notYetShippedNum == tradeVOList.size()){
            deliverStatus = DeliverStatus.NOT_YET_SHIPPED;
        } else if ((int) shippedNum == tradeVOList.size()) {
            deliverStatus = DeliverStatus.SHIPPED;
        } else {
            deliverStatus = DeliverStatus.PART_SHIPPED;
        }

        TradeDeliverDTO parentTradeDeliverDTO = KsBeanUtil.convert(tradeDeliver, TradeDeliverDTO.class);
        parentTradeDeliverDTO.setStatus(deliverStatus);
        parentTradeDeliverDTO.setSunDeliverId(deliverId);
        parentTradeDeliverDTO.setShipperType(ShipperType.SUPPLIER);

        // 添加商家发货信息
        tradeProvider.deliver(TradeDeliverRequest.builder()
                .tradeDeliver(parentTradeDeliverDTO)
                .tid(privateTradeVO.getParentId())
                .operator(commonUtil.getOperator())
                .build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "子单发货", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }


    /**
     * 验证订单是否存在售后申请
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "验证订单是否存在售后申请")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/deliver/verify/{tid}", method = RequestMethod.GET)
    public BaseResponse deliverVerify(@PathVariable String tid) {
        if (tradeQueryProvider.verifyAfterProcessing(TradeVerifyAfterProcessingRequest.builder().tid(tid).build()).getContext().getVerifyResult()) {
            throw new SbcRuntimeException("K-050136", new Object[]{tid});
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "验证订单是否存在售后申请", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 发货作废
     *
     * @param tid
     * @param tdId
     * @return
     */
    @ApiOperation(value = "发货作废")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tid", value = "订单Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tdId", value = "发货单Id", required = true)
    })
    @RequestMapping(value = "/deliver/{tid}/void/{tdId}", method = RequestMethod.GET)
    @LcnTransaction
    public ResponseEntity<BaseResponse> deliverVoid(@PathVariable String tid, @PathVariable String tdId,
                                                    HttpServletRequest req) {

        tradeProvider.deliverRecordObsolete(TradeDeliverRecordObsoleteRequest.builder()
                .deliverId(tdId).tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "发货作废", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 子订单发货作废
     *
     * @param tid
     * @param tdId
     * @return
     */
    @ApiOperation(value = "子订单发货作废")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tid", value = "订单Id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "tdId", value = "发货单Id", required = true)
    })
    @RequestMapping(value = "/providerTrade/deliver/{tid}/void/{tdId}", method = RequestMethod.GET)
    @LcnTransaction
    public ResponseEntity<BaseResponse> providerDeliverVoid(@PathVariable String tid, @PathVariable String tdId,
                                                            HttpServletRequest req) {

        providerTradeProvider.deliverRecordObsolete(TradeDeliverRecordObsoleteRequest.builder()
                .deliverId(tdId).tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "子订单发货作废", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**

     * 功能描述: 手动推送确认第三方订单(WMS)
     * 〈〉
     * @Param: [tid]
     * @Return: com.wanmi.sbc.common.base.BaseResponse
     * @Date: 2020/5/18 16:49
     */
    @PostMapping(value = "/wmsPushOrder/{tid}")
    @ApiOperation(value = "wms订单第三放订单推送")
    @LcnTransaction
    public BaseResponse pushWMSOrder(@PathVariable String tid){
        if (Objects.isNull(tid)){
            throw new SbcRuntimeException();
        }
        TradeGetByIdRequest tradeGetByIdRequest = new TradeGetByIdRequest();
        tradeGetByIdRequest.setTid(tid);
        tradeProvider.pushConfirmOrder(tradeGetByIdRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "wms订单第三放订单推送", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 功能描述: 手动推送第三方订单(WMS)
     *
     * @param tid
     * @return
     */
    @PostMapping(value = "/pushOrderToWms/{tid}")
    @ApiOperation(value = "订单推送到wms")
    @LcnTransaction
    public BaseResponse pushOrderToWms(@PathVariable String tid) {
        if (Objects.isNull(tid)){
            throw new SbcRuntimeException();
        }
        TradePushRequest tradePushRequest = new TradePushRequest();
        tradePushRequest.setTid(tid);
        tradeProvider.pushOrderToWms(tradePushRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "订单推送到wms", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 确认收货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "确认收货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/confirm/{tid}", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> confirm(@PathVariable String tid) {

        tradeProvider.confirmReceive(TradeConfirmReceiveRequest.builder().tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "确认收货", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 回审
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "回审")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/retrial/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public ResponseEntity<BaseResponse> retrial(@PathVariable String tid) {

        tradeProvider.retrial(TradeRetrialRequest.builder().tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "回审", "操作成功:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 0元订单默认支付
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "0元订单默认支付")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/default/pay/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    public BaseResponse<Boolean> defaultPay(@PathVariable String tid) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "0元订单默认支付", "0元订单默认支付:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return BaseResponse.success(tradeProvider.defaultPay(TradeDefaultPayRequest.builder()
                        .tid(tid)
                        .payWay(PayWay.UNIONPAY)
                        .build())
                .getContext().getPayResult());
    }

    /**
     * 验证
     *
     * @param tid tid
     * @return boolean
     */
    @ApiOperation(value = "验证")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单Id", required = true)
    @RequestMapping(value = "/verifyAfterProcessing/{tid}")
    public Boolean verifyAfterProcessing(@PathVariable String tid) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "验证", "验证:交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return tradeQueryProvider.verifyAfterProcessing(TradeVerifyAfterProcessingRequest.builder().tid(tid).build()).getContext().getVerifyResult();
    }

    /**
     * 根据快递公司及快递单号查询物流详情
     */
    @ApiOperation(value = "根据快递公司及快递单号查询物流详情", notes = "返回: 物流详情")
    @RequestMapping(value = "/deliveryInfos", method = RequestMethod.POST)
    public BaseResponse<List<Map<Object, Object>>> logistics(@RequestBody DeliveryQueryRequest queryRequest) {
        List<Map<Object, Object>> result = new ArrayList<>();

//        CompositeResponse<ConfigRopResponse> response = sdkClient.buildClientRequest().post(ConfigRopResponse.class,
//                "logistics.config", "1.0.0");
//        //如果快递设置为启用
//        if (Objects.nonNull(response.getSuccessResponse()) && DefaultFlag.YES.toValue() == response
//                .getSuccessResponse().getStatus()) {
//            result = deliveryQueryProvider.queryExpressInfoUrl(queryRequest).getContext().getOrderList();
//        }
        //获取快递100配置信息
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setConfigType(ConfigType.KUAIDI100.toValue());
        LogisticsRopResponse response = systemConfigQueryProvider.findKuaiDiConfig(request).getContext();
        //已启用
        if(response.getStatus()== DefaultFlag.YES.toValue()){
            result = deliveryQueryProvider.queryExpressInfoUrl(queryRequest).getContext().getOrderList();
        }
        return BaseResponse.success(result);
    }

    /**
     * 分页查询拼团订单
     */
    @ApiOperation(value = "分页查询拼团订单")
    @RequestMapping(value = "/groupon/page", method = RequestMethod.POST)
    public BaseResponse<Page<TradeVO>> grouponOrderPage(@RequestBody TradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }
        tradeQueryRequest.setGrouponFlag(Boolean.TRUE);
        tradeQueryRequest.putSort("grouponSuccessTime", "desc");
        tradeQueryRequest.putSort("createTime", "desc");
        Page<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        return BaseResponse.success(tradePage);
    }


    /**
     * 导出订单
     *
     * @return
     */
    @ApiOperation(value = "导出订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params/providerTrade/{encrypted}/{encryptedable}", method = RequestMethod.GET)
    public void exportProviderTrade(@PathVariable String encrypted,@PathVariable String encryptedable, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            String decryptedable = new String(Base64.getUrlDecoder().decode(encryptedable.getBytes()));
            TradeExportRequest tradeExportRequest = JSON.parseObject(decrypted, TradeExportRequest.class);
            DisabledExportRequest disabledExportRequest =JSON.parseObject(decryptedable, DisabledExportRequest.class);

            Operator operator = commonUtil.getOperator();
            log.debug(String.format("/export/params/providerTrade, employeeId=%s", operator.getUserId()));

            String adminId = operator.getAdminId();
            if (StringUtils.isNotEmpty(adminId)) {
                tradeExportRequest.setSupplierId(Long.valueOf(adminId));
            }

            //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
            tradeExportRequest.makeAllAuditFlow();
            TradeQueryDTO tradeQueryDTO = KsBeanUtil.convert(tradeExportRequest, TradeQueryDTO.class);
            DisabledDTO disabledDTO=KsBeanUtil.convert(disabledExportRequest,DisabledDTO.class);
            List<TradeVO> trades = tradeQueryProvider.listTradeExport(TradeListExportRequest.builder().tradeQueryDTO(tradeQueryDTO).build()).getContext().getTradeVOList();
            if(CollectionUtils.isNotEmpty(trades)){
                List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();

                //查询所有订单groupId;
                List<String> groupIds = trades.stream().filter(t->Objects.nonNull(t.getGroupId())).map(t -> t.getGroupId()).collect(Collectors.toList());
                TradeGroupByGroupIdsResponse tradeGroupIdsResponse = tradeGroupQueryProvider.getTradeGroupByGroupIds(TradeGroupByIdsRequest.builder().groupId(groupIds).build()).getContext();

                TradeGroupByGroupIdsResponse byGroupIdsResponse = pileTradeGroupQueryProvider.getTradeGroupByGroupIds(TradeGroupByIdsRequest
                        .builder().groupId(groupIds).build()).getContext();
                List<TradeGroupVO> groupVOArrayList = Lists.newArrayList();

                if(Objects.nonNull(tradeGroupIdsResponse) && CollectionUtils.isNotEmpty(tradeGroupIdsResponse.getTradeGroupVOS())){
                    groupVOArrayList.addAll(tradeGroupIdsResponse.getTradeGroupVOS());
                }

                if(Objects.nonNull(byGroupIdsResponse) && CollectionUtils.isNotEmpty(byGroupIdsResponse.getTradeGroupVOS())){
                    groupVOArrayList.addAll(byGroupIdsResponse.getTradeGroupVOS());
                }


                if(CollectionUtils.isNotEmpty(employeeList)){
                    List<TradeGroupVO> finalGroupVOArrayList = groupVOArrayList;
                    List<CouponCodeDTO> couponCodeDTOList = null;
                    List<CouponInfoVO> couponInfoList = null;
                    if(CollectionUtils.isNotEmpty(finalGroupVOArrayList)){

                        List<String> codeIdList = finalGroupVOArrayList.stream().map(g -> g.getCommonCoupon()).collect(Collectors.toList())
                                .stream().map(tc -> tc.getCouponCodeId()).collect(Collectors.toList());
                        //查询店铺、商品优惠券
                        List<String> storeCouponCodeIds = trades.stream().filter(t -> Objects.nonNull(t.getTradeCoupon())).map(t -> t.getTradeCoupon().getCouponCodeId()).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(storeCouponCodeIds)){
                            codeIdList.addAll(storeCouponCodeIds);
                        }

                        if(CollectionUtils.isNotEmpty(codeIdList)){
                            CouponCodeListByConditionResponse context = couponCodeQueryProvider.findCouponsByCouponCodeIds(CouponsByCodeIdsRequest.builder().couponCodeIds(codeIdList).build()).getContext();

                            couponCodeDTOList = context.getCouponCodeList();

                            CouponInfosQueryResponse infosQueryResponse = couponInfoQueryProvider.queryCouponInfos(CouponInfoQueryRequest
                                    .builder()
                                    .couponIds(context.getCouponCodeList().stream().map(cc->cc.getCouponId()).collect(Collectors.toList()))
                                    .build()).getContext();
                            couponInfoList = infosQueryResponse.getCouponCodeList();
                        }
                    }

                    List<CouponCodeDTO> finalCouponCodeDTOList = couponCodeDTOList;
                    List<CouponInfoVO> finalCouponInfoList = couponInfoList;
                    trades.forEach(t->{
                        if(Objects.nonNull(t.getBuyer()) && Objects.nonNull(t.getBuyer().getEmployeeId())){
                            EmployeeListVO employeeListVO = employeeList.stream().filter(e -> e.getEmployeeId().equals(t.getBuyer().getEmployeeId()))
                                    .findFirst().orElse(null);
                            if(Objects.nonNull(employeeListVO)){
                                t.setEmployeeName(employeeListVO.getEmployeeName());
                            }
                        }

                        //平台优惠券展示
                        if(Objects.isNull(t.getTradeCoupon())){
                            TradeGroupVO groupVO = null;
                            if(CollectionUtils.isNotEmpty(finalGroupVOArrayList)){
                                groupVO = finalGroupVOArrayList.stream().filter(f->f.getId().equals(t.getGroupId())).findFirst().orElse(null);
                            }
                            if(Objects.nonNull(groupVO)){
                                t.setTradeCoupon(groupVO.getCommonCoupon());
                                if(CollectionUtils.isNotEmpty(finalCouponCodeDTOList)){
                                    TradeGroupVO finalGroupVO = groupVO;
                                    CouponCodeDTO couponCodeDTO = finalCouponCodeDTOList.stream().filter(fc -> finalGroupVO.getCommonCoupon().getCouponCodeId().equals(fc.getCouponCodeId())).findFirst().orElse(null);
                                    if(Objects.nonNull(couponCodeDTO) && CollectionUtils.isNotEmpty(finalCouponInfoList)){
                                        CouponInfoVO couponInfoVO = finalCouponInfoList.stream().filter(ci -> couponCodeDTO.getCouponId().equals(ci.getCouponId())).findFirst().orElse(null);
                                        if(Objects.nonNull(couponInfoVO)){
                                            t.getTradeCoupon().setCouponName(couponInfoVO.getCouponName());
                                        }
                                    }
                                }
                            }
                        }else{
                            //店铺、单品优惠券
                            if(CollectionUtils.isNotEmpty(finalCouponCodeDTOList)){
                                CouponCodeDTO couponCodeDTO = finalCouponCodeDTOList.stream().filter(fc -> t.getTradeCoupon().getCouponCodeId().equals(fc.getCouponCodeId())).findFirst().orElse(null);
                                if(Objects.nonNull(couponCodeDTO) && CollectionUtils.isNotEmpty(finalCouponInfoList)){
                                    CouponInfoVO couponInfoVO = finalCouponInfoList.stream().filter(ci -> couponCodeDTO.getCouponId().equals(ci.getCouponId())).findFirst().orElse(null);
                                    if(Objects.nonNull(couponInfoVO)){
                                        t.getTradeCoupon().setCouponName(couponInfoVO.getCouponName());
                                    }
                                }
                            }
                        }
                    });
                }
            }
            //按下单时间降序排列
//            trades = trades.stream().sorted(
//                    Comparator.comparing(a -> a.getTradeState().getCreateTime()).reversed()
//            ).collect(Collectors.toList());
//            trades.sort((t1, t2) -> t2.getTradeState().getCreateTime().compareTo(t1.getTradeState().getCreateTime()));
            String headerKey = "Content-Disposition";
            LocalDateTime dateTime = LocalDateTime.now();
            String fileName = String.format("批量导出订单_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("/prioviderTrade/export/params, fileName={},", fileName, e);
            }
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);

            List<String> parentIdList=new ArrayList<>();
            if(disabledDTO.getDisabled().equals("true")){
                trades.forEach(vo->{
                    parentIdList.add(vo.getId());
                });
                List<TradeVO> tradeVOList=providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(parentIdList).build()).getContext().getTradeVOList();

                // 遍历封装导出信息
                List<ProviderTradeExportVO> tradeExportVOs = new ArrayList<>();
                tradeVOList.forEach(tradeVO -> {
                    ProviderTradeExportVO exportVO;
                    // 商家信息
                    String supplierName = StringUtils.isNotEmpty(tradeVO.getSupplierName()) ? tradeVO.getSupplierName() : "";
                    String supplierCode = StringUtils.isNotEmpty(tradeVO.getSupplierCode()) ? tradeVO.getSupplierCode() : "";
                    String supplierInfo = supplierName + "  " + supplierCode;
                    for (int i = 0; i < tradeVO.getTradeItems().size(); i++) {
                        TradeItemVO tradeItemVO = tradeVO.getTradeItems().get(i);
                        exportVO = new ProviderTradeExportVO();
                        if (i == 0) {
                            KsBeanUtil.copyProperties(tradeVO, exportVO);
                            // 下单时间
                            exportVO.setCreateTime(tradeVO.getTradeState().getCreateTime());
                            // 商家信息
                            exportVO.setSupplierInfo(supplierInfo);
                            // 供应商名称
                            exportVO.setSupplierName(supplierName);
                            // 订单商品金额
                            exportVO.setOrderGoodsPrice(tradeVO.getTradePrice().getTotalPrice());
                            // 订单状态
                            exportVO.setFlowState(tradeVO.getTradeState().getFlowState());
                            // 发货状态
                            exportVO.setDeliverStatus(tradeVO.getTradeState().getDeliverStatus());
                            exportVO.setConsigneeName(tradeVO.getConsignee().getName());
                            exportVO.setDetailAddress(tradeVO.getConsignee().getDetailAddress());
                            exportVO.setConsigneePhone(tradeVO.getConsignee().getPhone());
                        }

                        exportVO.setSkuName(tradeItemVO.getSkuName());
                        exportVO.setSpecDetails(tradeItemVO.getSpecDetails());
                        exportVO.setSkuNo(tradeItemVO.getSkuNo());
                        exportVO.setNum(tradeItemVO.getNum());

                        tradeExportVOs.add(exportVO);
                    }
                });

                try {
                    providerTradeExportBaseService.exportProvider(tradeExportVOs, response.getOutputStream(),Platform.SUPPLIER);
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }

            }else{
                try {
                    tradeExportService.export(trades, response.getOutputStream(),
                            Platform.PLATFORM.equals(operator.getPlatform()));
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }
            }
        } catch (Exception e) {
            log.error("/prioviderTrade/export/params error: ", e);
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        } finally {
            exportCount.set(0);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "导出订单", "操作成功");
    }


    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlag", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeListFlagResponse> checkDeliveryHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest request){
        DeliveryHomeListFlagResponse deliveryHomeListFlagResponse = new DeliveryHomeListFlagResponse();
        Map<Integer,DefaultFlag> longMap = new HashMap<>();
        request.getWareHouseOrderDTOS().forEach(s->{
            longMap.put(s.getOrderIndex(),checkDistance(s.getWareId(), request.getCustomerDeliveryAddressId()));
        });
        deliveryHomeListFlagResponse.setFlagMap(longMap);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "验证配送到家标志位", "操作成功");
        return BaseResponse.success(deliveryHomeListFlagResponse);
    }

    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlagOne", method = RequestMethod.POST)
    public BaseResponse<DeliveryHomeFlagResponse> checkDeliveryHomeFlagOne(@RequestBody @Valid DeliveryHomeFlagRequest request){
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        deliveryHomeFlagResponse.setFlag(checkDistance(request.getWareId(), request.getCustomerDeliveryAddressId()));
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "验证配送到家标志位", "操作成功");
        return BaseResponse.success(deliveryHomeFlagResponse);
    }

    @ApiOperation(value = "代客下单导入商品")
    @PostMapping(value = "/importGoodsInfos/{wareId}/{customerId}")
    public BaseResponse<ImportGoodsInfosExcel> importGoodsInfos(@RequestParam(value = "file", required = true) MultipartFile file, @PathVariable String wareId, @PathVariable String customerId, HttpServletResponse response) throws Exception{
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "代客下单导入商品", "代客下单导入商品");
        GoodsInfoImportListener goodsInfoImportListener = new GoodsInfoImportListener();
        goodsInfoImportListener.setWareId(wareId);
        goodsInfoImportListener.setCustomerId(customerId);
        EasyExcel.read(file.getInputStream(), ImportGoodsInfo.class, goodsInfoImportListener).sheet().doRead();

        if(goodsInfoImportListener.getExcelFlag()){
            return BaseResponse.success(goodsInfoImportListener.getResult());
        }
        //导入失败，数据写入redis,做导出；
        String uuid = UUIDUtil.getUUID();
        String errorRequest = IMPORT_GOODS_ERROR.concat(uuid);
        List<ImportGoodsInfo> listExcels = goodsInfoImportListener.getListExcels();
        List<ExportErrorGoodsInfoExcel> exportErrorGoodsInfoExcels = KsBeanUtil.convertList(listExcels, ExportErrorGoodsInfoExcel.class);
        redisService.setString(errorRequest, JSONArray.toJSONString(exportErrorGoodsInfoExcels));
        return BaseResponse.error(uuid);
    }

    @ApiOperation(value = "导出报错代客下单商品")
    @GetMapping(value = "/exportGoodsInfos/{uuid}")
    public void exportGoodsInfos(@PathVariable String uuid,HttpServletResponse response) throws Exception{
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "导出报错代客下单商品", "导出报错代客下单商品");
        //导入失败，数据写入redis,做导出；
        List<ExportErrorGoodsInfoExcel> exportErrorGoodsInfoExcels = JSONArray.parseArray(redisService.getString(IMPORT_GOODS_ERROR.concat(uuid)), ExportErrorGoodsInfoExcel.class);
        //读取成功删除缓存
        if(CollectionUtils.isNotEmpty(exportErrorGoodsInfoExcels)){
            redisService.delete(IMPORT_GOODS_ERROR.concat(uuid));
        }
        //文件命名
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("导入失败" + fDate.format(new Date()), "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExportErrorGoodsInfoExcel.class).sheet("模板").doWrite(exportErrorGoodsInfoExcels);
    }

    /**
     * 代客下单计算营销
     */
    @ApiOperation(value = "代客下单计算营销信息")
    @PostMapping(value = "/importGoodsInfos/getMarketing")
    public BaseResponse<ImportGoodsInfosExcel> getmarketingByImportGoodsInfos(@RequestBody @Valid ImportPurchaseMarketingRequest request){

        ImportGoodsInfosExcel importGoodsInfosExcelResponse = new ImportGoodsInfosExcel();

        if(CollectionUtils.isEmpty(request.getGoodsInfoIds()) || CollectionUtils.isEmpty(request.getImportGoodsInfosList())){
            throw new SbcRuntimeException("K-080201");
        }

        //查询用户信息
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();

        if(customer == null){
            throw new SbcRuntimeException("K-040018");
        }

        //查询商品信息
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        esGoodsInfoQueryRequest.setPageNum(0);
        //获取默认最大长度
        esGoodsInfoQueryRequest.setPageSize(10000);
        List<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticService.getEsBaseInfoByParams(esGoodsInfoQueryRequest).getData();

        if(CollectionUtils.isEmpty(esGoodsInfoList)){
            throw new SbcRuntimeException("K-080201");
        }

        //
        List<GoodsInfoVO> goodsInfoVOS = Lists.newArrayList();
        request.getImportGoodsInfosList().forEach(info->{
            //填充属性
            EsGoodsInfo goodsInfo = esGoodsInfoList.stream().filter(esGoodsInfo -> esGoodsInfo.getGoodsInfo().getGoodsInfoId().equals(info.getGoodsInfoId())).findFirst().orElse(null);
            if(goodsInfo != null){
                GoodsInfoVO convert = KsBeanUtil.convert(goodsInfo.getGoodsInfo(), GoodsInfoVO.class);
                //设置购买数量
                convert.setBuyCount(info.getBuyCount());
                convert.setGoodsInfoId(info.getGoodsInfoId());
                convert.setStock(BigDecimal.valueOf(info.getStock()));
                goodsInfoVOS.add(convert);
            }
        });

        com.wanmi.sbc.shopcart.api.request.purchase.PurchaseMarketingRequest purchaseMarketingRequest = new com.wanmi.sbc.shopcart.api.request.purchase.PurchaseMarketingRequest();
        purchaseMarketingRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        purchaseMarketingRequest.setGoodsInfos(goodsInfoVOS);
        purchaseMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
        purchaseMarketingRequest.setWareId(request.getWareId());

        //查询营销
        com.wanmi.sbc.shopcart.api.response.purchase.PurchaseMarketingResponse purchaseMarketingResponse = purchaseProvider.getPurchasesMarketing(purchaseMarketingRequest).getContext();

        log.info("purchaseMarketingResponse {}", purchaseMarketingResponse);

        //设置商品营销信息
        importGoodsInfosExcelResponse.setGoodsMarketingMap(purchaseMarketingResponse.getMap());
        //设置店铺营销信息
        importGoodsInfosExcelResponse.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
        //设置默认的选择营销
        importGoodsInfosExcelResponse.setGoodsMarketings(purchaseMarketingResponse.getGoodsMarketings());
        // 获取店铺对应的营销信息
        List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
        //商品选择的营销(这里需要调整)
        if (CollectionUtils.isNotEmpty(importGoodsInfosExcelResponse.getGoodsMarketings())) {
            importGoodsInfosExcelResponse.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
            //过滤已选营销
            HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
            purchaseMarketingResponse.getStoreMarketingMap().forEach((k, v) -> {
                List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
                map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
                calcResponses.forEach(purchaseMarketingCalcVO -> {
                    if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
                        giftMarketing.add(purchaseMarketingCalcVO);
                    }
                });
            });
            importGoodsInfosExcelResponse.setStoreMarketingMap(map);
        } else {
            importGoodsInfosExcelResponse.setStoreMarketingMap(new HashMap<>());
        }
        //组装赠品信息
        if (CollectionUtils.isNotEmpty(giftMarketing)) {
            //TODO:这里暂时写死，能匹配到仓
            setGiftMarketingShop(giftMarketing, true);
            importGoodsInfosExcelResponse.setGiftList(giftMarketing);
        }

        //计算金额
        PurchaseCalcAmountRequest purchaseCalcAmountRequest = new PurchaseCalcAmountRequest();
        //设置查询参数
        PurchaseCalcAmountDTO purchaseCalcAmountDTO = new PurchaseCalcAmountDTO();
        purchaseCalcAmountDTO = KsBeanUtil.convert(importGoodsInfosExcelResponse, PurchaseCalcAmountDTO.class);
        purchaseCalcAmountDTO.setGoodsInfos(goodsInfoVOS);
        purchaseCalcAmountRequest.setPurchaseCalcAmount(purchaseCalcAmountDTO);
        purchaseCalcAmountRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        purchaseCalcAmountRequest.setCustomerVO(customer);

        PurchaseListResponse purchaseListResponse = purchaseProvider.calcAmount(purchaseCalcAmountRequest).getContext();
        importGoodsInfosExcelResponse.setTotalPrice(purchaseListResponse.getTotalPrice());
        importGoodsInfosExcelResponse.setTradePrice(purchaseListResponse.getTradePrice());
        importGoodsInfosExcelResponse.setDiscountPrice(purchaseListResponse.getDiscountPrice());

        //计算优惠券
        List<TradeItemInfoDTO> tradeDtos = Lists.newArrayList();
        goodsInfoVOS.stream().forEach(importGoods -> {
            TradeItemInfoDTO itemInfoDTO = new TradeItemInfoDTO();
            itemInfoDTO.setGoodsInfoType(importGoods.getGoodsInfoType());
            itemInfoDTO.setBrandId(importGoods.getBrandId());
            itemInfoDTO.setNum(importGoods.getBuyCount().longValue());
            BigDecimal price = Objects.isNull(importGoods.getSalePrice()) ? importGoods.getVipPrice() : importGoods.getSalePrice();
            itemInfoDTO.setPrice(price.multiply(BigDecimal.valueOf(importGoods.getBuyCount())));
            itemInfoDTO.setCateId(importGoods.getCateId());
            itemInfoDTO.setSkuId(importGoods.getGoodsInfoId());
            itemInfoDTO.setSpuId(importGoods.getGoodsId());
            itemInfoDTO.setStoreId(importGoods.getStoreId());
            itemInfoDTO.setStoreCateIds(importGoods.getStoreCateIds());
            itemInfoDTO.setDistributionCommission(importGoods.getDistributionCommission());
            itemInfoDTO.setDistributionGoodsAudit(importGoods.getDistributionGoodsAudit());
            itemInfoDTO.setGoodsBatchNo(importGoods.getGoodsInfoBatchNo());

            tradeDtos.add(itemInfoDTO);
        });

        log.info("tradeDtos ============{}", tradeDtos);

        CouponCodeListForUseByCustomerIdRequest requ = CouponCodeListForUseByCustomerIdRequest.builder()
                .customerId(Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getParentCustomerId())
                        ? customer.getParentCustomerId() : request.getCustomerId())
                .tradeItems(tradeDtos).build();
        requ.setStoreId(commonUtil.getStoreId());

        log.info("requ==============={}", requ);
        //设置优惠券
        List<CouponCodeVO> couponCodeList = couponCodeQueryProvider.listForUseByCustomerId(requ).getContext().getCouponCodeList();
        //所有可用优惠券
        List<CouponCodeVO> availableCouponList = couponCodeList.stream().filter(cc -> CouponCodeStatus.AVAILABLE.equals(cc.getStatus())).collect(Collectors.toList());
        //获取面值最大
        if (CollectionUtils.isNotEmpty(availableCouponList)) {
            CouponCodeVO couponCodeVO = availableCouponList.stream().max(Comparator.comparing(c -> c.getDenomination())).get();
            //有优惠券增加优惠券减免金额
            importGoodsInfosExcelResponse.setCouponCode(couponCodeVO);
            importGoodsInfosExcelResponse.setTradePrice(importGoodsInfosExcelResponse.getTradePrice().subtract(couponCodeVO.getDenomination()));
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "代客下单计算营销信息", "操作成功");
        return BaseResponse.success(importGoodsInfosExcelResponse);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     *
     * @Param: 查询补充赠品信息
     * @Return: void
     * @Author: yxb
     * @Date: 2021/2/2 19:50
     */
    private void setGiftMarketingShop(List<PurchaseMarketingCalcVO> giftMarketing, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isNotEmpty(giftMarketing)) {
            Set<String> giftSkus = new HashSet<>();
            for (PurchaseMarketingCalcVO inner : giftMarketing) {
                List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                    List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                    for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                        giftSkus.add(gift.getProductId());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(giftSkus)) {
                List<GiftGoodsInfoVO> goodsInfos = goodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder()
                        .goodsInfoIds(new ArrayList<>(giftSkus)).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                    Map<String, GiftGoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GiftGoodsInfoVO::getGoodsInfoId, goods -> goods));
                    for (PurchaseMarketingCalcVO inner : giftMarketing) {
                        List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                        for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                            List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                            for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                                if (Objects.nonNull(goodsInfoVOMap.get(gift.getProductId()))) {
                                    gift.setGiftGoodsInfoVO(goodsInfoVOMap.get(gift.getProductId()));
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    /*
     * 功能描述: 计算经纬度是否符合配送范围
     * 〈〉
     * @Param: [wareId, customerDeleiverAddressId]
     * @Return: com.wanmi.sbc.common.enums.DefaultFlag
     * @Author: yxb
     * @Date: 2020/8/3 16:18
     */
    private DefaultFlag checkDistance(Long wareId,String customerDeleiverAddressId) {
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response)||response.getDelFlag().equals(DeleteFlag.YES)){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO)||(DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))){
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"该收货地址不存在");
        }
        if(wareHouseVO.getDistance() == null){
            return DefaultFlag.NO;
        }
        double distance = LonLatUtil.getDistance(response.getLng(), response.getLat(), wareHouseVO.getLng(), wareHouseVO.getLat());
        if (distance<=(wareHouseVO.getDistance()*1000)){
            return DefaultFlag.YES;
        }
        return DefaultFlag.NO;
    }
    /**
     * 功能描述: 验证自提门店信息,如果存在塞入自提信息
     */
    private void validPickUpPoint(TradeCreateDTO inner,Long storeId){

        if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)){
            WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                    .wareId(inner.getWareId()).storeId(storeId).build()).getContext().getWareHouseVO();
            if (wareHouseVO.getPickUpFlag().toValue()== PickUpFlag.NO.toValue()){
                throw new SbcRuntimeException("仓库不合法");
            }
            inner.setWareHouseVO(wareHouseVO);
        }

    }

    /**
     * 审核订单
     *
     * @param tid
     * @param request 订单审核参数结构
     * @return
     */
    @ApiOperation(value = "审核订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/audit/{tid}", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> audit(@PathVariable String tid, @RequestBody TradeAuditRequest request) {

        com.wanmi.sbc.order.api.request.trade.TradeAuditRequest tradeAuditRequest
                = com.wanmi.sbc.order.api.request.trade.TradeAuditRequest.builder()
                .tid(tid)
                .auditState(request.getAuditState())
                .reason(request.getReason())
                .financialFlag(request.getFinancialFlag())
                .operator(commonUtil.getOperator())
                .build();
        tradeProvider.audit(tradeAuditRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "审核订单", "操作成功：交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "审核新囤货订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/newAudit/{tid}", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> newAudit(@PathVariable String tid, @RequestBody TradeAuditRequest request) {
        log.info("新囤货订单审核，订单号：{}",tid);
        TradeAuditRequest tradeAuditRequest  = TradeAuditRequest.builder()
                .tid(tid)
                .auditState(request.getAuditState())
                .reason(request.getReason())
                .financialFlag(request.getFinancialFlag())
                .operator(commonUtil.getOperator())
                .build();
        tradeProvider.newAudit(tradeAuditRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "审核新囤货订单", "操作成功：交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 批量审核订单
     *
     * @param request 批量审核请求参数结构
     * @return
     */
    @ApiOperation(value = "批量审核订单")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> batchAudit(@RequestBody TradeAuditBatchRequest request) {


        com.wanmi.sbc.order.api.request.trade.TradeAuditBatchRequest tradeAuditBatchRequest =

                com.wanmi.sbc.order.api.request.trade.TradeAuditBatchRequest.builder()
                        .auditState(request.getAuditState())
                        .ids(request.getIds())
                        .reason(request.getReason())
                        .operator(commonUtil.getOperator())
                        .build();

        tradeProvider.auditBatch(tradeAuditBatchRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "批量审核订单", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

//    @ApiOperation(value = "清洗囤货历史数据")
//    @RequestMapping(value = "/writingHistoricalData",method = RequestMethod.GET)
//    public BaseResponse writingHistoricalData(){
//        return pileTradeProvider.writingHistoricalData();
//    }
}
