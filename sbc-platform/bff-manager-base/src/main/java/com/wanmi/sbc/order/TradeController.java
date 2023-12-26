package com.wanmi.sbc.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.bean.enums.PayType;
import com.wanmi.sbc.account.bean.enums.PayWay;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.annotation.MultiSubmit;
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
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.ValidStoreByIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListResponse;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaListResponse;
import com.wanmi.sbc.customer.api.response.store.StoreByIdResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CompanyInfoDTO;
import com.wanmi.sbc.customer.bean.dto.StoreInfoDTO;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseStockQueryProvider;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseBySkuIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsByConditionResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseBySkuIdResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponInfoQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeListForUseByCustomerIdRequest;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreRecordDetailDTO;
import com.wanmi.sbc.marketing.bean.dto.TradeItemInfoDTO;
import com.wanmi.sbc.marketing.bean.enums.CouponCodeStatus;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftDetailVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.provider.manualrefund.ManualRefundQueryProvider;
import com.wanmi.sbc.order.api.provider.payorder.PayOrderQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.order.PurchaseApiRequest;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.order.api.provider.refund.RefundOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.ProviderTradeProvider;
import com.wanmi.sbc.order.api.provider.trade.ProviderTradeQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.manualrefund.ManualRefundResponseByOrderCodeRequest;
import com.wanmi.sbc.order.api.request.orderpicking.OrderPickingRequest;
import com.wanmi.sbc.order.api.request.payorder.FindPayOrderRequest;
import com.wanmi.sbc.shopcart.api.request.purchase.*;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderListByTidRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import com.wanmi.sbc.order.api.response.manualrefund.ManualRefundResponse;
import com.wanmi.sbc.order.api.response.orderpicking.OrderPickingListResponse;
import com.wanmi.sbc.order.api.response.orderpicking.OrderPickingResponse;
import com.wanmi.sbc.order.api.response.payorder.FindPayOrderResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseListResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseMarketingResponse;
import com.wanmi.sbc.order.api.response.returnorder.ReturnOrderListByTidResponse;
import com.wanmi.sbc.order.api.response.trade.TradeGetFreightResponse;
import com.wanmi.sbc.order.api.response.trade.TradeTotalFreightPriceResponse;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.*;
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
import com.wanmi.sbc.tms.api.RemoteTmsCarrierService;
import com.wanmi.sbc.tms.api.domain.R;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteShipmentQueryVO;
import com.wanmi.sbc.tms.api.domain.vo.TmsSiteVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.util.SensitiveFieldUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/4/19.
 */
@Api(tags = "TradeController", description = "订单服务 Api")
@RestController
@RequestMapping("/trade")
@Slf4j
@Validated
public class TradeController {

    @Autowired
    private TradeProvider tradeProvider;

    @Autowired
    private ProviderTradeProvider providerTradeProvider;

    @Autowired
    private ProviderTradeQueryProvider providerTradeQueryProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Autowired
    private TradeExportService tradeExportService;

    @Autowired
    private ProviderTradeExportBaseService providerTradeExportBaseService;

    @Autowired
    private OsUtil osUtil;

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
    private CouponInfoQueryProvider couponInfoQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private RefundOrderQueryProvider refundOrderQueryProvider;

    @Autowired
    private ManualRefundQueryProvider manualRefundQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private InventoryDetailSamountProvider inventoryDetailSamountProvider;
    @Autowired
    private WareHouseStockQueryProvider wareHouseStockQueryProvider;

    private AtomicInteger exportCount = new AtomicInteger(0);

    private static final String IMPORT_GOODS_ERROR = "IMPORT_GOODS_ERROR:";

    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private RemoteTmsCarrierService remoteTmsCarrierService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    /**
     * 订单最大打印次数
     */
    @Value("${print.count.max}")
    private Integer printCountMax;



    /**
     * 订单发送邮件测试
     *
     * @return
     */
    @ApiOperation(value = "订单发送邮件测试")
    @RequestMapping(value = "/TradeController", method = RequestMethod.GET)
    public BaseResponse sendEmailTranslate() {
        tradeQueryProvider.sendEmailTranslate();
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
    @RequestMapping(value = "/supplierPage", method = RequestMethod.POST)
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

        //是否存在业务员筛选项
        if (StringUtils.isNotEmpty(tradeQueryRequest.getEmployeeName()) || StringUtils.isNotEmpty(tradeQueryRequest.getEmployeeAccount())) {
            EmployeeListRequest employeeListRequest = new EmployeeListRequest();
            employeeListRequest.setAccountName(tradeQueryRequest.getEmployeeAccount());
            employeeListRequest.setUserName(tradeQueryRequest.getEmployeeName());
            BaseResponse<EmployeeListResponse> list = employeeQueryProvider.list(employeeListRequest);
            if (Objects.nonNull(list) && Objects.nonNull(list.getContext())) {
                List<EmployeeListVO> employeeList = list.getContext().getEmployeeList();
                if (CollectionUtils.isNotEmpty(employeeList)) {
                    List<String> employeeIds = employeeList.stream().map(o -> o.getEmployeeId()).collect(Collectors.toList());
                    tradeQueryRequest.setEmployeeIds(employeeIds);
                } else {
                    tradeQueryRequest.setEmployeeIds(Arrays.asList("99999999999999999999999999999"));
                }
            } else {
                tradeQueryRequest.setEmployeeIds(Arrays.asList("99999999999999999999999999999"));
            }
        }

        // 设置自营标签
        BaseResponse<StoreByIdResponse> byId = storeQueryProvider.getById(StoreByIdRequest.builder().storeId(commonUtil.getStoreId()).build());
        StoreVO storeVO = byId.getContext().getStoreVO();
        if (storeVO != null) {
        	tradeQueryRequest.setSelfManage(storeVO.getSelfManage());
		}
        
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());

        MicroServicePage<TradeVO> tradePage = tradeQueryProvider.supplierPageCriteria(TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        tradePage.getContent().forEach(trade -> {
            if (Objects.nonNull(trade.getWareId())) {
                trade.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(trade.getWareId())).findFirst().get().getWareName());
            }
        });
        List<TradeVO> pageContent = tradePage.getContent();
        pageContent.forEach(tradeVO -> {
            if ("4".equals(tradeVO.getActivityType())) {
                List<String> arr = new ArrayList<>();
                List<TradeItemVO> tradeItems = tradeVO.getTradeItems();
                if (CollectionUtils.isNotEmpty(tradeItems)) {
                    tradeItems.forEach(item -> {
                        List<PickGoodsDTO> pickGoodsList = item.getPickGoodsList();
                        if(CollectionUtils.isNotEmpty(pickGoodsList)){
                            arr.addAll(pickGoodsList.stream().map(g -> g.getNewPileOrderNo()).collect(Collectors.toList()));
                        }
                    });
                    tradeVO.setStockOrder(arr);
                }
            }
//            SensitiveFieldUtil.handleTradeVO(tradeVO);

        });

        tradePage.setContent(tradePage.getContent().stream().filter(v->{
            return  !(StringUtils.isNotBlank(v.getSourceChannel())&&v.getSourceChannel().contains("chains"));
        }).collect(Collectors.toList()));

        List<TradeVO> content = tradePage.getContent();

        if(CollectionUtils.isNotEmpty(content)){
            List<String> stringList =
                    content.stream().map(TradeVO::getId).collect(Collectors.toList());
            Map<String, BigDecimal> pickPrice = getPickPrice(stringList);
            content.forEach(trade->{
                if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
                    BigDecimal pickPriceTotal = BigDecimal.ZERO;
                    if(Objects.nonNull(pickPrice.get(trade.getId()))){
                        pickPriceTotal = pickPrice.get(trade.getId());
                    }
                    trade.getTradePrice().setTotalPrice(pickPriceTotal.add(trade.getTradePrice().getDeliveryPrice()));
                }else{
                    BigDecimal reduce = trade.getTradeItems().stream().map(TradeItemVO::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    AtomicReference<BigDecimal> reduce2 = new AtomicReference<>(BigDecimal.ZERO);
                    trade.getTradeItems().forEach(item->{
                        List<TradeItemVO.WalletSettlementVo> walletSettlements = item.getWalletSettlements();
                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(walletSettlements)){
                            reduce2.set(reduce2.get().add(walletSettlements.stream().map(TradeItemVO.WalletSettlementVo::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
                        }
                    });
                    trade.getTradePrice().setTotalPrice(
                            reduce.add(trade.getTradePrice().getPackingPrice()).add(trade.getTradePrice().getDeliveryPrice())
                                    .add(reduce2.get())
                    );
                }
            });
        }
        return BaseResponse.success(tradePage);
    }

    /**
     * 获取订单提货单金额
     * @param pickNos  提货单号
     */
    private Map<String, BigDecimal> getPickPrice(List<String> pickNos){
        BaseResponse<InventoryDetailSamountResponse> inventoryByTakeIds = inventoryDetailSamountProvider.getInventoryByTakeIds(InventoryDetailSamountRequest.builder().takeIds(pickNos).build());
        List<InventoryDetailSamountVO> inventoryDetailSamountVOS = inventoryByTakeIds.getContext().getInventoryDetailSamountVOS();
        Map<String, BigDecimal> collect = new HashMap<>();
        if(CollectionUtils.isNotEmpty(inventoryDetailSamountVOS)){
            collect = inventoryDetailSamountVOS.stream().collect(
                    Collectors.groupingBy(InventoryDetailSamountVO::getTakeId,
                            Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));
        }
        return collect;
    }

    @Value("${wms.api.flag:false}")
    private Boolean wmsAPIFlag;

    /**
     * boss分页查询
     *
     * @param tradeQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询")
    @EmployeeCheck
    @RequestMapping(value = "/bossPage", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<TradeVO>> bossPage(@RequestBody TradeQueryDTO tradeQueryRequest) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            tradeQueryRequest.setSupplierId(companyInfoId);
        }
        //如果存在子账号，也需要把子账号的账单信息查询出来
        if (StringUtils.isNoneBlank(tradeQueryRequest.getBuyerId())) {
            ParentCustomerRelaListResponse context = parentCustomerRelaQueryProvider
                    .findAllByParentId(ParentCustomerRelaListRequest.builder().parentId(tradeQueryRequest.getBuyerId()).build()).getContext();
            if (Objects.nonNull(context) && CollectionUtils.isNotEmpty(context.getParentCustomerRelaVOList())) {
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

        //是否存在业务员筛选项
        if (StringUtils.isNotEmpty(tradeQueryRequest.getEmployeeName()) || StringUtils.isNotEmpty(tradeQueryRequest.getEmployeeAccount())) {
            EmployeeListRequest employeeListRequest = new EmployeeListRequest();
            employeeListRequest.setAccountName(tradeQueryRequest.getEmployeeAccount());
            employeeListRequest.setUserName(tradeQueryRequest.getEmployeeName());
            BaseResponse<EmployeeListResponse> list = employeeQueryProvider.list(employeeListRequest);
            if (Objects.nonNull(list) && Objects.nonNull(list.getContext())) {
                List<EmployeeListVO> employeeList = list.getContext().getEmployeeList();
                if (CollectionUtils.isNotEmpty(employeeList)) {
                    List<String> employeeIds = employeeList.stream().map(o -> o.getEmployeeId()).collect(Collectors.toList());
                    tradeQueryRequest.setEmployeeIds(employeeIds);
                } else {
                    tradeQueryRequest.setEmployeeIds(Arrays.asList("99999999999999999999999999999"));
                }
            } else {
                tradeQueryRequest.setEmployeeIds(Arrays.asList("99999999999999999999999999999"));
            }
        }

        // 白鲸管家查询
        if (StringUtils.isNotEmpty(tradeQueryRequest.getManagerName()) || StringUtils.isNotEmpty(tradeQueryRequest.getManagerAccount())) {
            EmployeeListRequest employeeListRequest = new EmployeeListRequest();
            employeeListRequest.setAccountName(tradeQueryRequest.getManagerAccount());
            employeeListRequest.setUserName(tradeQueryRequest.getManagerName());
            BaseResponse<EmployeeListResponse> list = employeeQueryProvider.list(employeeListRequest);
            if (Objects.nonNull(list) && Objects.nonNull(list.getContext())) {
                List<EmployeeListVO> managerList = list.getContext().getEmployeeList();
                if (CollectionUtils.isNotEmpty(managerList)) {
                    List<String> managerIds = managerList.stream().map(EmployeeListVO::getEmployeeId).collect(Collectors.toList());
                    tradeQueryRequest.setManagerIds(managerIds);
                } else {
                    tradeQueryRequest.setManagerIds(Collections.singletonList("99999999999999999999999999999"));
                }
            } else {
                tradeQueryRequest.setManagerIds(Collections.singletonList("99999999999999999999999999999"));
            }
        }

        // 是否自由查询
        final List<Long> selfManageStoreIds = mapSelfManageStoreIds();
        if (tradeQueryRequest.getSelfManage() != null){
            if (CollectionUtils.isEmpty(selfManageStoreIds)) {
                selfManageStoreIds.add(-100L);
            }
            if (Objects.equals(tradeQueryRequest.getSelfManage(), 1)) {
                tradeQueryRequest.setStoreIds(selfManageStoreIds);
            } else if (Objects.equals(tradeQueryRequest.getSelfManage(), 0)) {
                tradeQueryRequest.setNotStoreIds(selfManageStoreIds);
            }
        }

        MicroServicePage<TradeVO> tradePage = tradeQueryProvider.pageBossCriteria(TradePageCriteriaRequest.builder().tradePageDTO(tradeQueryRequest).build()).getContext().getTradePage();
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());
        tradePage.getContent().forEach(trade -> {
            if (Objects.nonNull(trade.getWareId())) {
                trade.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(trade.getWareId())).findFirst().get().getWareName());
            }
        });
        tradePage.setContent(tradePage.getContent().stream().filter(v->{
            return  !(StringUtils.isNotBlank(v.getSourceChannel()) && v.getSourceChannel().contains("chains"));
        }).collect(Collectors.toList()));

        List<TradeVO> content = tradePage.getContent();
        if(CollectionUtils.isNotEmpty(content)){
            List<String> stringList =
                    content.stream().map(TradeVO::getId).collect(Collectors.toList());
            Map<String, BigDecimal> pickPrice = getPickPrice(stringList);

            BaseResponse<OrderPickingListResponse> orderPickList = null;
            if (!wmsAPIFlag) {
                orderPickList  =  tradeProvider.queryOrderPicking(OrderPickingRequest.builder().tidList(stringList).build());
            }

            BaseResponse<OrderPickingListResponse> finalOrderPickList = orderPickList;
            content.forEach(trade->{
                if(TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())){
                    BigDecimal pickPriceTotal = BigDecimal.ZERO;
                    if(Objects.nonNull(pickPrice.get(trade.getId()))){
                        pickPriceTotal = pickPrice.get(trade.getId());
                    }
                    trade.getTradePrice().setTotalPrice(pickPriceTotal.add(trade.getTradePrice().getDeliveryPrice()));
                }else{
                    BigDecimal reduce = trade.getTradeItems().stream().map(TradeItemVO::getSplitPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    AtomicReference<BigDecimal> reduce2 = new AtomicReference<>(BigDecimal.ZERO);
                    trade.getTradeItems().forEach(item->{
                        List<TradeItemVO.WalletSettlementVo> walletSettlements = item.getWalletSettlements();
                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(walletSettlements)){
                            reduce2.set(reduce2.get().add(walletSettlements.stream().map(TradeItemVO.WalletSettlementVo::getReduceWalletPrice).reduce(BigDecimal.ZERO, BigDecimal::add)));
                        }
                    });
                    trade.getTradePrice().setTotalPrice(
                            reduce.add(trade.getTradePrice().getPackingPrice()).add(trade.getTradePrice().getDeliveryPrice())
                                    .add(reduce2.get())
                    );
                }

                if (Objects.nonNull(finalOrderPickList)) {
                    List<OrderPickingResponse> pickingResponseList = finalOrderPickList.getContext().getOrderPickingResponseList();
                    Map<String, OrderPickingResponse> responseMap = pickingResponseList.stream().collect(Collectors.toMap(OrderPickingResponse::getTradeId, Function.identity()));
                    OrderPickingResponse pickingResponse = responseMap.get(trade.getId());
                    if (Objects.isNull(pickingResponse)) {
                        trade.setPickingStatus(0);
                    } else {
                        trade.setPickingStatus(pickingResponse.getStatus());
                    }
                }
                if (trade.getSupplier() != null && selfManageStoreIds.contains(trade.getSupplier().getStoreId())) {
                    trade.setSelfManage(1);
                } else {
                    trade.setSelfManage(0);
                }
            });
        }
        return BaseResponse.success(tradePage);
    }

    private List<Long> mapSelfManageStoreIds() {
        try {
            final BaseResponse<List<Long>> listBaseResponse = storeQueryProvider.listStoreIdsBySelfManage();
            final List<Long> context = listBaseResponse.getContext();
            if (CollectionUtils.isEmpty(context)) return new ArrayList<>();
            return context;
        } catch (Exception e) {
            log.error("查询自营商家异常", e);
            return new ArrayList<>();
        }
    }



    /**
     * 根据参数查询某订单的运费
     * a
     *
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
        if (DeliverWay.EXPRESS.equals(tradeParams.getDeliverWay())) {
            return BaseResponse.success(tradeQueryProvider.getFreight(tradeParams).getContext());
        } else {
            TradeGetFreightResponse result = new TradeGetFreightResponse();
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
        List<TradeGetFreightResponse> result = new ArrayList<>(tradeParams.size());
        tradeParams.forEach(params -> {
            params.setSupplier(SupplierDTO.builder().storeId(store.getStoreId())
                    .freightTemplateType(store.getFreightTemplateType())
                    .companyType(store.getCompanyType()).build());
        });
        List<TradeParamsRequest> tradeParamList = new ArrayList<>();
        //计算totalPrice
        for (TradeParamsRequest inner : tradeParams) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (TradeItemDTO inside : inner.getOldTradeItems()) {
                totalPrice = totalPrice.add(inside.getPrice().multiply(new BigDecimal(inside.getNum())));
            }
            TradePriceDTO tradePriceDTO = new TradePriceDTO();
            tradePriceDTO.setTotalPrice(totalPrice);
            inner.setTradePrice(tradePriceDTO);
        }
        //排除所有非快递方式的订单，直接塞0
        Iterator<TradeParamsRequest> iterator = tradeParams.iterator();
        while (iterator.hasNext()) {
            TradeParamsRequest next = iterator.next();
            if (!next.getDeliverWay().equals(DeliverWay.EXPRESS)) {
                TradeGetFreightResponse inner = new TradeGetFreightResponse();
                inner.setStoreId(next.getSupplier().getStoreId());
                inner.setDeliveryPrice(BigDecimal.ZERO);
                result.add(inner);
                iterator.remove();
            } else {
                tradeParamList.add(next);
            }
        }
        BigDecimal totalDeliveryPrice = BigDecimal.ZERO;
        result.addAll(tradeParamList.stream().map(params -> tradeQueryProvider.getFreight(params)
                .getContext()).collect(Collectors.toList()));
        for (TradeGetFreightResponse inner : result) {
            totalDeliveryPrice = totalDeliveryPrice.add(inner.getDeliveryPrice() == null ? BigDecimal.ZERO : inner.getDeliveryPrice());
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
    @MultiSubmit
    public ResponseEntity<BaseResponse> create(@RequestBody @Valid TradeCreateDTO tradeCreateRequest) {
        Operator operator = commonUtil.getOperator();
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        Long storeId = commonUtil.getStoreId();
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId))
                .getContext();
        if (DeliverWay.PICK_SELF.equals(tradeCreateRequest.getDeliverWay())) {
            validPickUpPoint(tradeCreateRequest, storeId);
        }
        if (Objects.isNull(tradeCreateRequest.getWareId())) {
            throw new SbcRuntimeException("K-000009");
        }
        WareHouseVO wareHouseVO1 = commonUtil.getWareHouseByWareId(tradeCreateRequest.getWareId());
        if (Objects.isNull(wareHouseVO1.getWareCode())) {
            throw new SbcRuntimeException("K-000009");
        }
        tradeCreateRequest.setWareHouseCode(wareHouseVO1.getWareCode());
        //1.校验与包装订单信息-与业务员app代客下单公用
        TradeVO trade = tradeQueryProvider.wrapperBackendCommit(TradeWrapperBackendCommitRequest.builder().operator(operator)
                .companyInfo(KsBeanUtil.convert(companyInfo, CompanyInfoDTO.class)).storeInfo(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
                .tradeCreate(tradeCreateRequest).build()).getContext().getTradeVO();
        List<TradeAddDTO> tradeAddDTOS = new ArrayList<>();

        if (DeliverWay.LOGISTICS.equals(tradeCreateRequest.getDeliverWay())) {
            if (Objects.isNull(tradeCreateRequest.getLogisticsId())) {
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
        if (CollectionUtils.isNotEmpty(tradeAddDTOS)) {

            List<String> PurchaseDeleteGoodsInfoIds = Lists.newArrayList();

            tradeAddDTOS.forEach(t -> {
                PurchaseDeleteGoodsInfoIds.addAll(t.getTradeItems().stream().map(ti -> ti.getSkuId()).collect(Collectors.toList()));
            });

            log.info("PurchaseDeleteGoodsInfoIds ====== {}", PurchaseDeleteGoodsInfoIds);

            PurchaseApiRequest request = new PurchaseApiRequest();
            request.setCustomerId(tradeCreateRequest.getCustom());
            request.setInviteeId("0");
            request.setGoodsInfoIds(PurchaseDeleteGoodsInfoIds);
            purchaseProvider.delete(request);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "创建订单", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());

    }

    /**
     * 用于修改订单前的展示订单信息
     *
     * @param tid 订单id
     * @return 返回信息 {@link TradeRemedyDetailsVO}
     */
    @ApiOperation(value = "用于修改订单前的展示订单信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/remedy/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeRemedyDetailsVO> remedy(@PathVariable String tid) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "用于修改订单前的展示订单信息", "用于修改订单前的展示订单信息:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return BaseResponse.success(tradeQueryProvider.getRemedyByTid(TradeGetRemedyByTidRequest.builder().tid(tid).build()).getContext().getTradeRemedyDetailsVO());
    }

    /**
     * 修改订单
     *
     * @param tradeRemedyRequest
     * @return
     */
    @ApiOperation(value = "修改订单")
    @RequestMapping(value = "/remedy", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> remedy(@RequestBody @Valid TradeRemedyDTO tradeRemedyRequest) {
        Operator operator = commonUtil.getOperator();
        if ((osUtil.isS2b() && operator.getPlatform() != Platform.SUPPLIER) || (osUtil.isB2b() && operator.getPlatform()
                != Platform.BOSS)) {
            throw new SbcRuntimeException("K-000018");
        }
        Long storeId = commonUtil.getStoreId();
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId))
                .getContext();
        TradeModifyRemedyRequest tradeModifyRemedyRequest = TradeModifyRemedyRequest.builder()
                .tradeRemedyDTO(KsBeanUtil.convert(tradeRemedyRequest, TradeRemedyDTO.class))
                .operator(operator)
                .storeInfoDTO(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
                .build();
        tradeProvider.remedy(tradeModifyRemedyRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "修改订单", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 修改订单（不修改商品、营销相关信息）
     *
     * @param tradeRemedyRequest
     * @return
     */
    @ApiOperation(value = "修改订单（不修改商品、营销相关信息）")
    @RequestMapping(value = "/remedy-part", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> remedyPart(@RequestBody @Valid TradeRemedyDTO tradeRemedyRequest) {
        Operator operator = commonUtil.getOperator();
        if ((osUtil.isS2b() && operator.getPlatform() != Platform.SUPPLIER) || (osUtil.isB2b() && operator.getPlatform()
                != Platform.BOSS)) {
            throw new SbcRuntimeException("K-000018");
        }
        Long storeId = commonUtil.getStoreId();
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId))
                .getContext();

        TradeRemedyPartRequest tradeRemedyPartRequest = TradeRemedyPartRequest.builder()
                .tradeRemedyDTO(tradeRemedyRequest)
                .operator(operator)
                .storeInfoDTO(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
                .build();

        tradeProvider.remedyPart(tradeRemedyPartRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "修改订单（不修改商品、营销相关信息）", "操作成功");
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 描述：    订单改价
     * 场景：    业务员助手|商家端针对订单运费和总金额重新设价
     *
     * @param request 改价参数结构
     * @return
     */
    @ApiOperation(value = "订单改价")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/price/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse changePrice(@PathVariable String tid, @RequestBody @Valid TradePriceChangeDTO request) {
        Operator operator = commonUtil.getOperator();
        if ((osUtil.isS2b() && operator.getPlatform() != Platform.SUPPLIER) || (osUtil.isB2b() && operator.getPlatform()
                != Platform.BOSS)) {
            throw new SbcRuntimeException("K-000018");
        }

        TradeModifyPriceRequest tradeModifyPriceRequest = TradeModifyPriceRequest.builder()
                .tradePriceChangeDTO(request)
                .tid(tid)
                .operator(operator)
                .build();

        tradeProvider.modifyPrice(tradeModifyPriceRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "订单改价", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 描述：    订单改价
     * 场景：    业务员助手|商家端改订单明细单价格
     *
     * @param request 改价参数结构
     * @return
     */
    @ApiOperation(value = "订单明细改价")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/changeItemPrice/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse changeItemPrice(@PathVariable String tid, @RequestBody @Valid TradeConfirmItemDTO request) {
        Operator operator = commonUtil.getOperator();
        if ((osUtil.isS2b() && operator.getPlatform() != Platform.SUPPLIER) || (osUtil.isB2b() && operator.getPlatform()
                != Platform.BOSS)) {
            throw new SbcRuntimeException("K-000018");
        }
        if(Objects.isNull(tid) || CollectionUtils.isEmpty( request.getTradeItems())){
            return BaseResponse.error("参数错误");
        }
        List<TradeItemDTO> updateItems = new ArrayList<>(request.getTradeItems().size());
        for(TradeItemDTO itemDTO : request.getTradeItems()){
            if(Objects.isNull(itemDTO.getChangedPrice())){
                continue;
            }
            if(itemDTO.getChangedPrice().compareTo(BigDecimal.ZERO)<1){
                return BaseResponse.error("商品修改后单价["+itemDTO.getChangedPrice()+"],需要大于0");
            }
            if(itemDTO.getChangedPrice().compareTo(itemDTO.getPrice())==0){
                continue;
            }
            updateItems.add(itemDTO);
        }
        if(updateItems.size()==0){
            log.info("订单"+tid+"没有改价的商品");
            return BaseResponse.SUCCESSFUL();
        }
        //只传入要修改价格的商品
        request.setTradeItems(updateItems);
        TradeModifyPriceRequest tradeModifyPriceRequest = TradeModifyPriceRequest.builder()
                .tradeConfirmItemDTO(request)
                .tid(tid)
                .operator(operator)
                .build();

        try {
            tradeProvider.changeItemPrice(tradeModifyPriceRequest);
        }catch(SbcRuntimeException e){
            return BaseResponse.info(e.getErrorCode(),e.getResult());
        }catch (Exception e){
            return BaseResponse.error(e.getMessage());
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "订单明细改价", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "初始化历史订单数据")
    @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "flag", value = "标识", required = true)
    @RequestMapping(value = "/initHistoryTradeInfo/{flag}", method = RequestMethod.GET)
    public BaseResponse initHistoryTradeInfo(@PathVariable Integer flag) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "初始化历史订单数据", "初始化历史订单数据");
        return tradeProvider.initHistoryTradeInfo(flag);
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
    @RequestMapping(value = "/remarkBoss/{tid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> sellerRemarkBoss(@PathVariable String tid, @RequestBody TradeRemedyDTO
            tradeRemedyRequest) {

        TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest = TradeRemedySellerRemarkRequest.builder()
                .sellerRemark(tradeRemedyRequest.getSellerRemark())
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        tradeProvider.remedySellerRemark(tradeRemedySellerRemarkRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "修改卖家备注", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
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
    @MultiSubmit
    public ResponseEntity<BaseResponse> sellerRemark(@PathVariable String tid, @RequestBody TradeRemedyDTO
            tradeRemedyRequest) {

        TradeRemedySellerRemarkRequest tradeRemedySellerRemarkRequest = TradeRemedySellerRemarkRequest.builder()
                .sellerRemark(tradeRemedyRequest.getSellerRemark())
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        tradeProvider.remedySellerRemark(tradeRemedySellerRemarkRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "修改卖家备注", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 修改子单备注(供应商订单)
     *
     * @param tid
     * @param tradeRemedyRequest
     * @return
     */
    @ApiOperation(value = "修改子单备注(供应商订单)")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/provider/remark/{tid}", method = RequestMethod.POST)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> sellerProviderTradeRemark(@PathVariable String tid, @RequestBody ProviderTradeRemedyDTO
            tradeRemedyRequest) {

        ProviderTradeRemedyBuyerRemarkRequest tradeRemedySellerRemarkRequest = ProviderTradeRemedyBuyerRemarkRequest.builder()
                .buyRemark(tradeRemedyRequest.getBuyerRemark())
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        providerTradeProvider.remedyBuyerRemark(tradeRemedySellerRemarkRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "修改子单备注(供应商订单)", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
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
    @MultiSubmit
    public ResponseEntity<BaseResponse> audit(@PathVariable String tid, @RequestBody TradeAuditRequest request) {

        com.wanmi.sbc.order.api.request.trade.TradeAuditRequest tradeAuditRequest
                = com.wanmi.sbc.order.api.request.trade.TradeAuditRequest.builder()
                .tid(tid)
                .auditState(request.getAuditState())
                .reason(request.getReason())
                .operator(commonUtil.getOperator())
                .build();
        tradeProvider.audit(tradeAuditRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "审核订单", "操作成功：交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
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
        operateLogMQUtil.convertAndSend("订单服务", "批量审核订单", "操作成功");
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
    @MultiSubmit
    public void export(@PathVariable String encrypted, @PathVariable String encryptedable, Boolean isDetailed, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            String decryptedable = new String(Base64.getUrlDecoder().decode(encryptedable.getBytes()));
            boolean tecryptedDetauled = isDetailed == null ? false : isDetailed;
            TradeExportRequest tradeExportRequest = JSON.parseObject(decrypted, TradeExportRequest.class);
            DisabledExportRequest disabledExportRequest = JSON.parseObject(decryptedable, DisabledExportRequest.class);
            Operator operator = commonUtil.getOperator();
            log.debug(String.format("/trade/export/params, employeeId=%s", operator.getUserId()));

            String adminId = operator.getAdminId();
            if (StringUtils.isNotEmpty(adminId)) {
                tradeExportRequest.setSupplierId(Long.valueOf(adminId));
            }

            //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
            tradeExportRequest.makeAllAuditFlow();
            TradeQueryDTO tradeQueryDTO = KsBeanUtil.convert(tradeExportRequest, TradeQueryDTO.class);
            if(Constants.BOSS_DEFAULT_STORE_ID.compareTo(commonUtil.getStoreIdWithDefault())!=0){
                tradeQueryDTO.setDeletedFlag(0);
            }
            DisabledDTO disabledDTO = KsBeanUtil.convert(disabledExportRequest, DisabledDTO.class);

            log.info("====decrypted:{}，decryptedable:{},isDetailed:{},tradeQueryDTO:{},disabledDTO:{}", decrypted, decryptedable, isDetailed,
                    JSONObject.toJSONString(tradeQueryDTO), JSONObject.toJSONString(disabledDTO));

            BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(WareHouseListRequest.builder().build().setDelFlag(DeleteFlag.NO));

            log.info("====WareHouseListResponse:{}", JSONObject.toJSONString(list));

            List<TradeVO> trades = tradeQueryProvider.listTradeExport(TradeListExportRequest.builder().tradeQueryDTO(tradeQueryDTO).build()).getContext().getTradeVOList();

            log.info("====trades:{}", JSONObject.toJSONString(trades));

            if (CollectionUtils.isNotEmpty(trades)) {

                trades.forEach(trade -> {
                    if (TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(trade.getActivityType())) {
                        InventoryDetailSamountRequest request = new InventoryDetailSamountRequest();
                        request.setTakeId(trade.getId());
                        BaseResponse<InventoryDetailSamountResponse> inventoryByTakeIdResp = inventoryDetailSamountProvider.getInventoryByTakeId(request);
                        if(Objects.nonNull(inventoryByTakeIdResp) && Objects.nonNull(inventoryByTakeIdResp.getContext())){
                            Map<String, BigDecimal> collectPrice = inventoryByTakeIdResp.getContext().getInventoryDetailSamountVOS().stream().collect(
                                    Collectors.groupingBy(item -> item.getTakeId() + ":" + item.getGoodsInfoId(),
                                            Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)));

                            trade.getTradeItems().forEach(tradeItem -> {
//                PickGoodsDTO pickGoodsDTO = tradeItem.getPickGoodsList().get(0);
                                BigDecimal tmpPrice = collectPrice.getOrDefault(trade.getId() + ":" + tradeItem.getSkuId(), BigDecimal.ZERO);
                                tmpPrice = new BigDecimal(String.valueOf(tmpPrice)).setScale(2, RoundingMode.HALF_UP);
                                tradeItem.setSplitPrice(tmpPrice);
                            });
                        }
                    }
                });


                trades.forEach(var -> {
                    if (Objects.nonNull(var.getWareId())) {
                        list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(var.getWareId())).findFirst().ifPresent(item -> {
                            var.setWareName(item.getWareName());
                        });
                    }
                });
                Map<String, String> empNameByEmpIdMap = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList()
                        .stream().collect(Collectors.toMap(EmployeeListVO::getEmployeeId, EmployeeListVO::getEmployeeName));
                if (MapUtils.isNotEmpty(empNameByEmpIdMap)) {
                    trades.stream().forEach(tradeVO -> {
                        tradeVO.setEmployeeName(empNameByEmpIdMap.get(tradeVO.getBuyer().getEmployeeId()));
                        tradeVO.setManagerName("system");
                        if (!TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(tradeVO.getActivityType())) {
                            tradeVO.setManagerName(empNameByEmpIdMap.get(tradeVO.getBuyer().getManagerId()));
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
            List<String> parentIdList = new ArrayList<>();
            if (disabledDTO.getDisabled().equals("true")) {
                trades.forEach(vo -> {
                    parentIdList.add(vo.getId());
                });
                log.info("=====parentIdList:{}", JSONObject.toJSONString(parentIdList));
                List<TradeVO> tradeVOList = providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(parentIdList).build()).getContext().getTradeVOList();
                log.info("=====tradeVOList:{}", JSONObject.toJSONString(tradeVOList));
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
                        if (Objects.nonNull(tradeVO.getWareId())) {
                            exportVO.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(tradeVO.getWareId())).findFirst().get().getWareName());
                        }
                        exportVO.setPayOrderId(tradeVO.getPayOrderId());
                        exportVO.setSkuName(tradeItemVO.getSkuName());
                        exportVO.setSpecDetails(tradeItemVO.getSpecDetails());
                        exportVO.setSkuNo(tradeItemVO.getSkuNo());
                        exportVO.setNum(tradeItemVO.getNum());
                        tradeExportVOs.add(exportVO);
                    }
                });

                log.info("=====tradeExportVOs:{}", JSONObject.toJSONString(tradeExportVOs));

                try {
                    providerTradeExportBaseService.exportProvider(tradeExportVOs, response.getOutputStream(), Platform.BOSS);
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }

            } else {
                try {
                    if (tecryptedDetauled) {
                        Map<String, String> erpNoMap = new HashMap<>();
                        List<String> skuIds = new ArrayList<>();
                        trades.forEach(var -> {
                            skuIds.addAll(var.getTradeItems().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));
                            skuIds.addAll(var.getGifts().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));

                        });

                        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsInfos();
                        goodsInfos.forEach(goodsInfoVO -> {
                            erpNoMap.put(goodsInfoVO.getGoodsInfoId(), goodsInfoVO.getErpGoodsInfoNo());
                        });

                        trades.forEach(tradeVO -> {
                            for (int i = 0; i < tradeVO.getTradeItems().size(); i++) {
                                TradeItemVO tradeItemVO = tradeVO.getTradeItems().get(i);
                                WareHouseBySkuIdRequest wareHouseBySkuIdRequest = new WareHouseBySkuIdRequest();
                                wareHouseBySkuIdRequest.setWareId("WH01");
                                String erpNo = erpNoMap.get(tradeItemVO.getSkuId()) == null ? "" : erpNoMap.get(tradeItemVO.getSkuId()).substring(4);
                                wareHouseBySkuIdRequest.setSkuId(erpNo);
                                String stockName = wareHouseStockQueryProvider.getByWareHouseIdAndSkuId(wareHouseBySkuIdRequest).getContext().getStockName();
                                tradeItemVO.setStockName(stockName);
                            }
                        });
                        //反鲸币
                        List<String> ids=trades.stream().map(item -> item.getId()).collect(Collectors.toList());
                        List<CoinActivityRecordDetailDto> details = coinActivityProvider.queryCoinActivityRecordDetailByOrderIds(ids).getContext();
                        Map<String, BigDecimal> coinMap = details.stream().collect(Collectors.toMap(CoinActivityRecordDetailDto::getGoodsInfoId, CoinActivityRecordDetailDto::getCoinNum, (o1, o2) -> o1));

                        tradeExportService.export(trades, erpNoMap, response.getOutputStream(),
                                Platform.PLATFORM.equals(operator.getPlatform()), tecryptedDetauled,coinMap);
                    } else {
                        tradeExportService.export(trades, response.getOutputStream(),
                                Platform.PLATFORM.equals(operator.getPlatform()));
                    }
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
        operateLogMQUtil.convertAndSend("订单服务", "导出订单", "操作成功");
    }

    @ApiOperation(value = "查看订单详情-运营端")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/{tid}", method = RequestMethod.GET)
    @MultiSubmit
    public BaseResponse<TradeVO> detail(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(tid)
                .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bBoss)
                .build()).getContext().getTradeVO();
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());
        if (Objects.nonNull(trade.getWareId())) {
            trade.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(trade.getWareId())).findFirst().get().getWareName());
        }

        //显示库位信息
        for (int i = 0; i < trade.getTradeItems().size(); i++) {
            TradeItemVO tradeItemVO = trade.getTradeItems().get(i);
            WareHouseBySkuIdRequest wareHouseBySkuIdRequest = new WareHouseBySkuIdRequest();
            wareHouseBySkuIdRequest.setWareId(trade.getWareHouseCode());
            String erpNo = "";
            if(tradeItemVO.getErpSkuNo().indexOf("001-") == 0){
                erpNo = tradeItemVO.getErpSkuNo().substring(4);
            }else{
                erpNo = tradeItemVO.getErpSkuNo();
            }
            tradeItemVO.setErpSkuNo(erpNo);
            wareHouseBySkuIdRequest.setSkuId(erpNo);
            String stockName = wareHouseStockQueryProvider.getByWareHouseIdAndSkuId(wareHouseBySkuIdRequest).getContext().getStockName();
            tradeItemVO.setStockName(stockName);
        }

        if (!wmsAPIFlag) {
            BaseResponse<OrderPickingResponse> response = tradeProvider.getOrderPickingByTid(OrderPickingRequest.builder().tid(trade.getId()).build());
            if (Objects.nonNull(response) && Objects.nonNull(response.getContext())) {
                trade.setPickingStatus(response.getContext().getStatus());
            }else {
                trade.setPickingStatus(0);
            }
        }
        BigDecimal returnCoin = BigDecimal.ZERO;
        List<CoinActivityRecordDetailDto> details = coinActivityProvider.queryCoinActivityRecordDetailByOrderId(tid).getContext();
        if (CollectionUtils.isNotEmpty(details)) {
            returnCoin = details.stream().map(CoinActivityRecordDetailDto::getCoinNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        Map<String, BigDecimal> coinMap = details.stream().collect(Collectors.toMap(CoinActivityRecordDetailDto::getGoodsInfoId, CoinActivityRecordDetailDto::getCoinNum, (o1, o2) -> o1));
        
        BaseResponse<List<CoinActivityStoreRecordDetailDTO>> sendRecord = coinActivityProvider.querySendRecord(tid);
        Map<String, BigDecimal> orderCoinMap = sendRecord.getContext().stream().collect(Collectors.toMap(
        		CoinActivityStoreRecordDetailDTO::getGoodsInfoId, 
        		CoinActivityStoreRecordDetailDTO::getCoinNum, 
        		(o1, o2) -> o1));
        for (TradeItemVO item : trade.getTradeItems()) {
            if (Objects.nonNull(coinMap.get(item.getSkuId()))) {
                item.setReturnCoin(coinMap.get(item.getSkuId()));
            }else {
                item.setReturnCoin(BigDecimal.ZERO);
            }
            
            // 平台返鲸币
            BigDecimal orderReturnCoin = orderCoinMap.get(item.getSkuId());
            if (Objects.nonNull(orderReturnCoin)) {
                item.setPlatformReturnCoin(orderReturnCoin);
            }else {
                item.setPlatformReturnCoin(BigDecimal.ZERO);
            }
        }
        resetTradeDeliveryPrice(trade);
        trade.setReturnCoin(returnCoin);
        return BaseResponse.success(trade);
    }

    private void resetTradeDeliveryPrice(TradeVO trade) {
        if(DeliverWay.isDeliveryToStore(trade.getDeliverWay()) && trade.getConsignee().getVillageFlag() && PayState.isPaid(trade.getTradeState().getPayState())){
            BigDecimal villageAddDelivery = tradeQueryProvider.findVillageAddDeliveryByTradeId(trade.getId()).getContext();
            if(trade.getTradePrice().getDeliveryPrice()!=null) {
                villageAddDelivery = villageAddDelivery.add(trade.getTradePrice().getDeliveryPrice());
            }
            trade.getTradePrice().setDeliveryPrice(villageAddDelivery);
        }
    }

    @ApiOperation(value = "查看订单详情-商家端")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> supplierDetail(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getByIdManager(TradeGetByIdManagerRequest.builder().tid(tid)
                .storeId(commonUtil.getStoreId()).requestFrom(AccountType.s2bSupplier)
                .build()).getContext().getTradeVO();

        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(new WareHouseListRequest());
        if (Objects.nonNull(trade.getWareId())) {
            trade.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(trade.getWareId())).findFirst().get().getWareName());
        }
//        SensitiveFieldUtil.handleTradeVO(trade);
        resetTradeDeliveryPrice(trade);
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
        //填充整单交易金额 @jkp
        fillTradePrice(trade);
        List<String> spuIds = trade.getTradeItems().stream().map(TradeItemVO::getSpuId).collect(Collectors.toList());
        GoodsByConditionResponse response = goodsQueryProvider
                .listByCondition(GoodsByConditionRequest.builder().goodsIds(spuIds).build()).getContext();
        Map<String, DevanningGoodsInfoVO> goodsInfoVOMap = initGoodsInfoVoToMap(trade);
        trade.getTradeItems().stream().forEach(s -> {
            Optional<GoodsVO> goodsInfoVOOptional = response.getGoodsVOList().stream()
                    .filter(f -> f.getGoodsId().equals(s.getSpuId())).findFirst();
            if (goodsInfoVOOptional.isPresent()) {
                GoodsVO goodsVO = goodsInfoVOOptional.get();
                if (Objects.nonNull(goodsVO)) {
                    s.setSubTitle(goodsVO.getGoodsSubtitle());
                }
            }
            //商品匹配库位信息
            matchStock(s,trade.getWareHouseCode());
            s.setGoodsInfoBarcode(goodsInfoVOMap.get(s.getSkuId())!=null? goodsInfoVOMap.get(s.getSkuId()).getGoodsInfoBarcode():null);
        });

        trade.getGifts().stream().forEach(s -> {
            if (Objects.nonNull(s)) {
                //赠品匹配库位信息
                matchStock(s,trade.getWareHouseCode());
            }
            s.setGoodsInfoBarcode(goodsInfoVOMap.get(s.getSkuId())!=null? goodsInfoVOMap.get(s.getSkuId()).getGoodsInfoBarcode():null);
        });

        List<TradeItemVO> TradeListNew = trade.getTradeItems().stream().filter(tradeItemVO -> tradeItemVO != null)
                .sorted(Comparator.comparing(TradeItemVO::getSortStockName,Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
        //统计该订单关联的SKU已退货数量
        TradeListNew.forEach( subTrade -> buildReturnNum(subTrade,trade.getId()));
        trade.setTradeItems(TradeListNew);

        if(DeliverWay.isDeliveryToStore(trade.getDeliverWay())){
            if(CollectionUtils.isNotEmpty(trade.getTradeDelivers())){
                TradeDeliverVO tradeDeliverVO = trade.getTradeDelivers().get(0);
                if(null!=tradeDeliverVO.getLogistics() && null!=tradeDeliverVO.getLogistics().getShipmentSiteId()){
                    log.info("tmsGetSiteById入参[{}]",tradeDeliverVO.getLogistics().getShipmentSiteId());
                    R<TmsSiteVO> r = remoteTmsCarrierService.getSiteById(tradeDeliverVO.getLogistics().getShipmentSiteId());
                    log.info("tmsGetSiteById出参[{}]",JSONObject.toJSONString(r));
                    tradeDeliverVO.getLogistics().setTmsSiteVO(r.getData());
                }
            }
        }

        if (!wmsAPIFlag) {
            // 返回电商拣货状态
            BaseResponse<OrderPickingResponse> pickingResponse = tradeProvider.getOrderPickingByTid(OrderPickingRequest.builder().tid(trade.getId()).build());
            if (Objects.nonNull(pickingResponse) && Objects.nonNull(pickingResponse.getContext())) {
                trade.setPickingStatus(pickingResponse.getContext().getStatus());
            }else {
                trade.setPickingStatus(0);
            }
        }
        //统计打印次数 如果为空 则默认3 赋值打印最多3次
        if (Objects.isNull(trade.getPrintCount())) {
            trade.setPrintCount(printCountMax);
        }

        log.info("==============trade内容：{}",trade);
        return BaseResponse.success(trade);
    }

    private Map<String, DevanningGoodsInfoVO> initGoodsInfoVoToMap(TradeVO vo) {
        Map<String, DevanningGoodsInfoVO> goodsInfoVOMap = new HashMap<>(vo.getTradeItems().size());
        List<String> tradeSkuIds = new ArrayList<>(vo.getTradeItems().size()*2);
        List<String> skuIds = vo.getTradeItems().stream().map(TradeItemVO::getSkuId).distinct().collect(Collectors.toList());
        tradeSkuIds.addAll(skuIds);
        if(CollectionUtils.isNotEmpty(vo.getGifts())){
            skuIds = vo.getGifts().stream().map(TradeItemVO::getSkuId).distinct().collect(Collectors.toList());
            tradeSkuIds.addAll(skuIds);
        }
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(tradeSkuIds)) {
            List<String> newSkuIds = new ArrayList<>(tradeSkuIds.size());
            tradeSkuIds.forEach(skuId->{
                DevanningGoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(skuId);
                if(goodsInfoVO==null){
                    newSkuIds.add(skuId);
                }
            });
            if(newSkuIds.size()>0){
                DevanningGoodsInfoListResponse goodsInfoListResponse = devanningGoodsInfoQueryProvider.listByCondition(DevanningGoodsInfoListByConditionRequest.builder().goodsInfoIds(newSkuIds).build()).getContext();
                if (goodsInfoListResponse != null && org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoListResponse.getDevanningGoodsInfoVOS())) {
                    goodsInfoListResponse.getDevanningGoodsInfoVOS().forEach(devanningGoodsInfoVO -> {
                        goodsInfoVOMap.put(devanningGoodsInfoVO.getGoodsInfoId(),devanningGoodsInfoVO);
                    });
                }
            }
        }
        return goodsInfoVOMap;
    }

    /**
     * 更新订单打印次数
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "更新订单打印次数")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/print/count/{tid}", method = RequestMethod.GET)
    public BaseResponse<TradeVO> countPrint(@PathVariable String tid) {
        TradeVO trade = tradeQueryProvider.getOrderById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        //统计打印次数 如果为空 则默认3 赋值打印最多3次
        int count = Objects.isNull(trade.getPrintCount()) ? printCountMax : trade.getPrintCount();
        //校验打印次数最多3次 超出提示
        if (count == 0) {
            throw new SbcRuntimeException("K-061001");
        }
        //每进来一次打印次数减 1
        count--;
        trade.setPrintCount(count);
        //更新订单打印次数
        TradeUpdateDTO updateDTO = KsBeanUtil.convert(trade, TradeUpdateDTO.class);
        TradeUpdateRequest updateRequest = TradeUpdateRequest.builder().trade(updateDTO).build();
        tradeProvider.update(updateRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "更新订单打印次数", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return BaseResponse.success(trade);
    }


    /**
     * 填充整单交易金额
     *
     * @param trade
     */
    public void fillTradePrice(TradeVO trade) {
        InventoryDetailSamountRequest inventoryDetailSamountRequest = InventoryDetailSamountRequest.builder().takeId(trade.getId()).build();
        List<InventoryDetailSamountVO> inventoryDetailSamountVOS = Optional.ofNullable(inventoryDetailSamountProvider.getInventoryByTakeId(inventoryDetailSamountRequest))
                .map(BaseResponse::getContext)
                .map(InventoryDetailSamountResponse::getInventoryDetailSamountVOS)
                .orElse(Lists.newArrayList());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(inventoryDetailSamountVOS)) {
            Map<Integer, BigDecimal> collect = inventoryDetailSamountVOS.stream()
                    .collect(Collectors.groupingBy(
                            InventoryDetailSamountVO::getMoneyType,
                            Collectors.reducing(BigDecimal.ZERO, InventoryDetailSamountVO::getAmortizedExpenses, BigDecimal::add)
                    ));
            BigDecimal rmb = Optional.ofNullable(collect.get(0)).orElse(BigDecimal.ZERO);     // 金钱类型 0是余额 1真实的钱
            BigDecimal jintie = Optional.ofNullable(collect.get(1)).orElse(BigDecimal.ZERO);  // 金钱类型 0是余额 1真实的钱
            BigDecimal paidPrice = rmb.add(jintie); //实付商品金额（囤货已支付）

            TradePriceVO tradePrice = trade.getTradePrice();
            BigDecimal deliveryPrice = Objects.isNull(tradePrice.getDeliveryPrice()) ? BigDecimal.ZERO : tradePrice.getDeliveryPrice();

            tradePrice.setGoodsPrice(Optional.ofNullable(trade).map(TradeVO::getTradePrice).map(TradePriceVO::getGoodsPrice).orElse(BigDecimal.ZERO)); // 商品金额待处理
            tradePrice.setPaidPrice(paidPrice);
            tradePrice.setBalancePrice(jintie);
            tradePrice.setTotalPayCash(paidPrice);
            tradePrice.setTotalPrice(paidPrice.add(deliveryPrice));
        }
    }


    private void matchStock(TradeItemVO tradeItemVO,String wareHouseCode){
        //显示库位信息
        WareHouseBySkuIdRequest wareHouseBySkuIdRequest = new WareHouseBySkuIdRequest();
        wareHouseBySkuIdRequest.setWareId(wareHouseCode);
        String erpNo = "";
        if(StringUtils.isNotEmpty(tradeItemVO.getErpSkuNo())){
            if(tradeItemVO.getErpSkuNo().indexOf("001-") == 0 || tradeItemVO.getErpSkuNo().indexOf("002-") == 0){
                erpNo = tradeItemVO.getErpSkuNo().substring(4);
            }else{
                erpNo = tradeItemVO.getErpSkuNo();
            }
            tradeItemVO.setErpSkuNo(erpNo);
            wareHouseBySkuIdRequest.setSkuId(erpNo);
            WareHouseBySkuIdResponse wareHouseBySkuIdResponse = wareHouseStockQueryProvider.getByWareHouseIdAndSkuId(wareHouseBySkuIdRequest).getContext();
            String stockName = wareHouseBySkuIdResponse.getStockName();
            String sortStockName = wareHouseBySkuIdResponse.getSortStockName();
            tradeItemVO.setStockName(stockName);
            tradeItemVO.setSortStockName(sortStockName);
        }
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
    @MultiSubmit
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
        operateLogMQUtil.convertAndSend("订单服务", "发货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }

    /**
     * 发货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "商家发货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/deliver/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> supplierDeliver(@PathVariable String tid, @Valid @RequestBody TradeDeliverRequestDTO
            tradeDeliverRequestDTO) {
        if (tradeDeliverRequestDTO==null ||tradeDeliverRequestDTO.getShippingItemList().isEmpty() && tradeDeliverRequestDTO.getGiftItemList().isEmpty()) {
            throw new SbcRuntimeException("K-050314");
        }
        log.info("supplierDeliver.{}参数[{}]", tid,JSONObject.toJSONString(tradeDeliverRequestDTO));
        //托运部、专线
        if(DeliverWay.isLogisticsTYB(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址不能为空");
            }
        }
        else if(DeliverWay.isLogisticsZDZX(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"专线物流地址不能为空");
            }
        }
        //快递到家自费、配送到店
        else if(DeliverWay.isTmsDelivery(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(tradeDeliverRequestDTO.getShipmentSiteId()==null){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址不能为空");
            }
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getShipmentSiteName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址名称不能为空");
            }

        }
        //同城配送
        else if(DeliverWay.isDeliveryTCPS(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"同城配送方式不能为空");
            }
        }
        //快递到家到付
        else if(DeliverWay.isExpressArrived(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"快递公司不能为空");
            }
        }
        //上门自提
        else if(DeliverWay.isDeliverySMZT(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自提地址不能为空");
            }
        }else{
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"发货失败！请联系大白鲸");
        }

        TradeDeliveryCheckRequest tradeDeliveryCheckRequest = TradeDeliveryCheckRequest.builder()
                .tid(tid)
                .tradeDeliver(tradeDeliverRequestDTO)
                .build();
        tradeQueryProvider.deliveryCheck(tradeDeliveryCheckRequest);

        TradeDeliverDTO tradeDeliver = tradeDeliverRequestDTO.getTradeDeliverDTO(tid);
        TradeDeliverRequest tradeDeliverRequest = TradeDeliverRequest.builder()
                .tradeDeliver(tradeDeliver)
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        String deliverId = tradeProvider.deliver(tradeDeliverRequest).getContext().getDeliverId();
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "商家发货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }

    @ApiOperation(value = "商家确认物流")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/pushLogisticsToThrid/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> pushLogisticsToThrid(@PathVariable String tid,@RequestBody TradeDeliverRequestDTO
            tradeDeliverRequestDTO) {
        if (tradeDeliverRequestDTO==null) {
            throw new SbcRuntimeException("K-050314");
        }
        log.info("pushLogisticsToThrid.{}参数[{}]", tid,JSONObject.toJSONString(tradeDeliverRequestDTO));
        if(DeliverWay.isLogisticsTYB(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址不能为空");
            }
        }
        else if(DeliverWay.isLogisticsZDZX(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"专线物流地址不能为空");
            }
        }
        //快递到家自费、配送到店
        else if(DeliverWay.isTmsDelivery(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(tradeDeliverRequestDTO.getShipmentSiteId()==null){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址不能为空");
            }
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getShipmentSiteName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"物流地址名称不能为空");
            }

        }
        //同城配送
        else if(DeliverWay.isDeliveryTCPS(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"同城配送方式不能为空");
            }
        }
        //快递到家到付
        else if(DeliverWay.isExpressArrived(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"快递公司不能为空");
            }
        }
        //上门自提
        else if(DeliverWay.isDeliverySMZT(tradeDeliverRequestDTO.getSupplierDeliverWay())){
            if(StringUtils.isBlank(tradeDeliverRequestDTO.getLogisticCompanyName())) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自提地址不能为空");
            }
        }else{
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"操作失败！请联系大白鲸");
        }
        TradePushLogisticRequestDTO pushLogisticRequestDTO = TradePushLogisticRequestDTO.builder().deliverRequestDTO(tradeDeliverRequestDTO).tradeId(tid).build();
        tradeProvider.pushLogisticsToThrid(pushLogisticRequestDTO).getContext();
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @RequestMapping(value = "/tms/deliver", method = RequestMethod.POST)
    @MultiSubmit
    BaseResponse thirdPartyDeliver(@RequestBody @Valid TmsDeliverRequest tmsDeliverRequest){
        tradeProvider.tmsDeliver(tmsDeliverRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "商家发货获取接货点")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketId", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/marketShipment/{marketId}", method = RequestMethod.GET)
    public ResponseEntity<List<TmsSiteVO>> queryMarketShipment(@PathVariable Long marketId) {
        TmsSiteShipmentQueryVO tmsSiteShipmentQueryVO = new TmsSiteShipmentQueryVO();
        tmsSiteShipmentQueryVO.setMarketId(marketId);
        log.info("listMarketShipment入参[{}]",JSONObject.toJSONString(tmsSiteShipmentQueryVO));
        R<List<TmsSiteVO>> r1 = remoteTmsCarrierService.listMarketShipment(tmsSiteShipmentQueryVO);
        log.info("listMarketShipment出参[{}]",JSONObject.toJSONString(r1));
        List<TmsSiteVO> tmsSiteVOS =r1.getData();
        return ResponseEntity.ok(tmsSiteVOS);
    }

    @ApiOperation(value = "商家发货获取接货点")
    @RequestMapping(value = "/supplier/getSiteByCarrierId", method = RequestMethod.GET)
    public ResponseEntity<List<TmsSiteVO>> getSiteByCarrierId(@RequestParam(value = "marketId") Long marketId,@RequestParam(value = "carrierId") String carrierId) {
        TmsSiteShipmentQueryVO tmsSiteShipmentQueryVO = new TmsSiteShipmentQueryVO();
        tmsSiteShipmentQueryVO.setMarketId(marketId);
        tmsSiteShipmentQueryVO.setCarrierId(carrierId);
        log.info("getSiteByCarrierId入参[{}]",JSONObject.toJSONString(tmsSiteShipmentQueryVO));
        R<List<TmsSiteVO>> r1 = remoteTmsCarrierService.getSiteByCarrierId(tmsSiteShipmentQueryVO);
        log.info("getSiteByCarrierId出参[{}]",JSONObject.toJSONString(r1));
        List<TmsSiteVO> tmsSiteVOS =r1.getData();
        return ResponseEntity.ok(tmsSiteVOS);
    }


    @ApiOperation(value = "商家发货修改运单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/supplier/updateLogistics/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> updateLogistics(@PathVariable String tid, @Valid @RequestBody  TradeDeliverUpdateDTO
            tradeDeliverUpdateDTO) {

        if (Objects.isNull(tradeDeliverUpdateDTO)||Objects.isNull(tradeDeliverUpdateDTO.getOldData())||Objects.isNull(tradeDeliverUpdateDTO.getNewData())) {
            throw new SbcRuntimeException("修改订单物流信息的参数错误");
        }
        boolean isSame=ObjectUtils.equals(tradeDeliverUpdateDTO.getOldData().getDeliverNo(),tradeDeliverUpdateDTO.getNewData().getDeliverNo());
        if(isSame) {
            isSame = ObjectUtils.equals(tradeDeliverUpdateDTO.getOldData().getDeliverId(), tradeDeliverUpdateDTO.getNewData().getDeliverId());
            if (isSame) {
                isSame = ObjectUtils.equals(tradeDeliverUpdateDTO.getOldData().getLogisticCompanyName(), tradeDeliverUpdateDTO.getNewData().getLogisticCompanyName());
                if (isSame) {
                    isSame = ObjectUtils.equals(tradeDeliverUpdateDTO.getOldData().getDeliverTime(), tradeDeliverUpdateDTO.getNewData().getDeliverTime());
                    if (isSame) {
                        isSame = ObjectUtils.equals(tradeDeliverUpdateDTO.getOldData().getEncloses(), tradeDeliverUpdateDTO.getNewData().getEncloses());
                        if (isSame) {
                            return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
                        }
                    }
                }
            }
        }
        TradeDeliverRequestDTO tradeDeliverRequest = tradeDeliverUpdateDTO.getNewData();
        TradeDeliverVO tradeDeliverVO = getTradeDeliverVO(tradeDeliverRequest);
        tradeDeliverVO.setTradeId(tid);
        tradeDeliverUpdateDTO.setTradeDeliverVO(tradeDeliverVO);
        String deliverId = tradeProvider.updateLogistics(tradeDeliverUpdateDTO).getContext().getDeliverId();
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "商家发货修改运单", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }

    private TradeDeliverVO getTradeDeliverVO(TradeDeliverRequestDTO tradeDeliverRequest) {
        TradeDeliverVO tradeDeliver = new TradeDeliverVO();
        if (DeliverWay.isLogistics(tradeDeliverRequest.getSupplierDeliverWay())) {//物流
            /*BaseResponse<LogisticsCompanyByIdResponse> byId = logisticsCompanyQueryProvider
                    .getById(LogisticsCompanyByIdRequest.builder().id(Long.valueOf(tradeDeliverRequest.getDeliverId())).build());*/

            /*if(Objects.isNull(byId.getContext())){
                tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, "物流公司信息缺失", "物流公司信息缺失");
            }else {*/
                //LogisticsCompanyVO logisticsCompanyVO = byId.getContext().getLogisticsCompanyVO();
            tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, tradeDeliverRequest.getLogisticCompanyName(), tradeDeliverRequest.getLogisticCompanyName());
            //}
        }
        else if (DeliverWay.EXPRESS.toValue() == tradeDeliverRequest.getSupplierDeliverWay()) {//快递
            ExpressCompanyVO expressCompanyVO = null;
            if ("-1".equals(tradeDeliverRequest.getDeliverId())) { //其他发货
                expressCompanyVO = new ExpressCompanyVO();
                expressCompanyVO.setExpressCompanyId(-1L);
                expressCompanyVO.setExpressName(tradeDeliverRequest.getLogisticCompanyName());
                expressCompanyVO.setExpressCode("other");
            } else {
                ExpressCompanyByIdRequest request = new ExpressCompanyByIdRequest();
                request.setExpressCompanyId(Long.valueOf(tradeDeliverRequest.getDeliverId()));
                expressCompanyVO = expressCompanyQueryProvider.getById(request).getContext().getExpressCompanyVO();
            }
            if(expressCompanyVO==null){
                tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, "物流公司信息缺失", "物流公司信息缺失");
            }else {
                tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, expressCompanyVO.getExpressName(), expressCompanyVO.getExpressCode());
            }
        }
        else if (DeliverWay.DELIVERY_HOME.toValue() == tradeDeliverRequest.getSupplierDeliverWay()) {//店配
            tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, null, null);
        }else if(DeliverWay.TO_DOOR_PICK.toValue() == tradeDeliverRequest.getSupplierDeliverWay()){//配送到店
            tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, null, null);
        } else if(DeliverWay.isTmsDelivery(tradeDeliverRequest.getSupplierDeliverWay())){//配送到店
            tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, null, null);
        }else{
            tradeDeliverRequest.wrapTradeDevlier(tradeDeliver, null, null);
        }
        tradeDeliver.setShipperType(ShipperType.SUPPLIER);
        return tradeDeliver;
    }


    /**
     * 发货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "发货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/deliver2/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> deliver2(@PathVariable String tid, @Valid @RequestBody TradeDeliverRequestDTO tradeDeliverRequest) {

        if (wmsAPIFlag) {
            throw new SbcRuntimeException("K-050421");
        }

        if (tradeDeliverRequest.getShippingItemList().isEmpty() && tradeDeliverRequest.getGiftItemList().isEmpty()) {
            throw new SbcRuntimeException("K-000009");
        }

        tradeDeliverRequest.setDeliverTime(DateUtil.format(LocalDateTime.now(), DateUtil.FMT_DATE_1));

        //发货校验
        String uuid = UUID.randomUUID().toString();
        String deliveryNo = "xyy-wl-t-" + uuid.substring(29);
        tradeDeliverRequest.setDeliverNo(deliveryNo);

        ExpressCompanyByIdRequest request = new ExpressCompanyByIdRequest();
        request.setExpressCompanyId(7612L);
        ExpressCompanyVO expressCompanyVO = expressCompanyQueryProvider.getById(request).getContext().getExpressCompanyVO();
        TradeDeliverVO tradeDeliver = tradeDeliverRequest.toTradeDevlier(expressCompanyVO);
        tradeDeliver.setShipperType(ShipperType.SUPPLIER);

        TradeDeliverRequest tradeDeliverRequest1 = TradeDeliverRequest.builder()
                .tradeDeliver(KsBeanUtil.convert(tradeDeliver, TradeDeliverDTO.class))
                .tid(tid)
                .operator(commonUtil.getOperator())
                .build();

        String deliverId = tradeProvider.deliver2(tradeDeliverRequest1).getContext().getDeliverId();
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "发货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(deliverId));
    }

    /**
     * 发货
     *
     * @param tid
     * @return
     */
    @ApiOperation(value = "拣货")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/orderPicking/{tid}", method = RequestMethod.PUT)
    @LcnTransaction
    @MultiSubmit
    public ResponseEntity<BaseResponse> orderPicking(@PathVariable String tid) {

        if (wmsAPIFlag) {
            throw new SbcRuntimeException("K-050421");
        }

        OrderPickingRequest request = OrderPickingRequest.builder().tid(tid).build();
        tradeProvider.orderPicking(request);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "拣货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.success(tid));
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
    @MultiSubmit
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
        if ((int) notYetShippedNum == tradeVOList.size()) {
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
        operateLogMQUtil.convertAndSend("订单服务", "子单发货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
    public BaseResponse deliverVerify(@PathVariable String tid) {
        if (tradeQueryProvider.verifyAfterProcessing(TradeVerifyAfterProcessingRequest.builder().tid(tid).build()).getContext().getVerifyResult()) {
            throw new SbcRuntimeException("K-050136", new Object[]{tid});
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "验证订单是否存在售后申请", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
    public ResponseEntity<BaseResponse> deliverVoid(@PathVariable String tid, @PathVariable String tdId,
                                                    HttpServletRequest req) {

        tradeProvider.deliverRecordObsolete(TradeDeliverRecordObsoleteRequest.builder()
                .deliverId(tdId).tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "发货作废", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
        operateLogMQUtil.convertAndSend("订单服务", "子订单发货作废", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 功能描述: 手动推送确认第三方订单(WMS)
     * 〈〉
     *
     * @Param: [tid]
     * @Return: com.wanmi.sbc.common.base.BaseResponse
     * @Date: 2020/5/18 16:49
     */
    @PostMapping(value = "/wmsPushOrder/{tid}")
    @ApiOperation(value = "wms订单第三放订单推送")
    @LcnTransaction
    @MultiSubmit
    public BaseResponse pushWMSOrder(@PathVariable String tid) {
        if (Objects.isNull(tid)) {
            throw new SbcRuntimeException();
        }
        TradeGetByIdRequest tradeGetByIdRequest = new TradeGetByIdRequest();
        tradeGetByIdRequest.setTid(tid);
        tradeProvider.pushConfirmOrder(tradeGetByIdRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "wms订单第三放订单推送", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
    public BaseResponse pushOrderToWms(@PathVariable String tid) {
        if (Objects.isNull(tid)) {
            throw new SbcRuntimeException();
        }
        TradePushRequest tradePushRequest = new TradePushRequest();
        tradePushRequest.setTid(tid);
        tradeProvider.pushOrderToWms(tradePushRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "订单推送到wms", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
        return BaseResponse.SUCCESSFUL();
    }

    @GetMapping(value = "/pushOrderToTms/{tid}")
    @ApiOperation(value = "订单推送到Tms")
    @LcnTransaction
    @MultiSubmit
    public BaseResponse pushOrderToTms(@PathVariable String tid) {
        if (Objects.isNull(tid)) {
            throw new SbcRuntimeException();
        }
        TradePushRequest tradePushRequest = new TradePushRequest();
        tradePushRequest.setTid(tid);
        tradeProvider.pushOrderToTms(tradePushRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @GetMapping(value = "/cancelOrderToTms/{tid}")
    @ApiOperation(value = "订单取消到Tms")
    @LcnTransaction
    @MultiSubmit
    public BaseResponse cancelOrderToTms(@PathVariable String tid) {
        if (Objects.isNull(tid)) {
            throw new SbcRuntimeException();
        }
        TradePushRequest tradePushRequest = new TradePushRequest();
        tradePushRequest.setTid(tid);
        tradeProvider.cancelOrderToTms(tradePushRequest);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 功能描述: 手动推送第三方订单(WMS)
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/pushOrderToWms/Batch")
    @ApiOperation(value = "订单推送到wms")
    @LcnTransaction
    @MultiSubmit
    public BaseResponse pushOrderToWmsBatch(@RequestBody TradePushWmsRequest request) {
        if (Objects.isNull(request) || CollectionUtils.isEmpty(request.getTidList())) {
            throw new SbcRuntimeException();
        }
        for (String tid : request.getTidList()) {
            TradePushRequest tradePushRequest = new TradePushRequest();
            tradePushRequest.setTid(tid);
            tradeProvider.pushOrderToWms2(tradePushRequest);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "手动推送第三方订单(WMS)", "操作成功");
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
    @MultiSubmit
    public ResponseEntity<BaseResponse> confirm(@PathVariable String tid) {

        tradeProvider.confirmReceive(TradeConfirmReceiveRequest.builder().tid(tid).operator(commonUtil.getOperator()).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "确认收货", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
        operateLogMQUtil.convertAndSend("订单服务", "回审", "操作成功:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
    public BaseResponse<Boolean> defaultPay(@PathVariable String tid) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "0元订单默认支付", "0元订单默认支付:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
    @MultiSubmit
    public Boolean verifyAfterProcessing(@PathVariable String tid) {
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "验证", "验证:交易id" + (StringUtils.isNotEmpty(tid) ? tid : ""));
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
        if (response.getStatus() == DefaultFlag.YES.toValue()) {
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
    public void exportProviderTrade(@PathVariable String encrypted, @PathVariable String encryptedable, Boolean isDetailed, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            String decryptedable = new String(Base64.getUrlDecoder().decode(encryptedable.getBytes()));
            TradeExportRequest tradeExportRequest = JSON.parseObject(decrypted, TradeExportRequest.class);
            DisabledExportRequest disabledExportRequest = JSON.parseObject(decryptedable, DisabledExportRequest.class);
            boolean tecryptedDetauled = isDetailed == null ? false : isDetailed;

            Operator operator = commonUtil.getOperator();
            log.debug(String.format("/export/params/providerTrade, employeeId=%s", operator.getUserId()));

            String adminId = operator.getAdminId();
            if (StringUtils.isNotEmpty(adminId)) {
                tradeExportRequest.setSupplierId(Long.valueOf(adminId));
            }

            //设定状态条件逻辑,已审核状态需筛选出已审核与部分发货
            tradeExportRequest.makeAllAuditFlow();
            TradeQueryDTO tradeQueryDTO = KsBeanUtil.convert(tradeExportRequest, TradeQueryDTO.class);
            if(Constants.BOSS_DEFAULT_STORE_ID.compareTo(commonUtil.getStoreIdWithDefault())!=0){
                tradeQueryDTO.setDeletedFlag(0);
            }
            DisabledDTO disabledDTO = KsBeanUtil.convert(disabledExportRequest, DisabledDTO.class);
            List<TradeVO> trades = tradeQueryProvider.listTradeExport(TradeListExportRequest.builder().tradeQueryDTO(tradeQueryDTO).build()).getContext().getTradeVOList();

            BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(WareHouseListRequest.builder().build().setDelFlag(DeleteFlag.NO));
            if (CollectionUtils.isNotEmpty(trades)) {
                trades.forEach(t -> {
                    if (Objects.nonNull(t.getWareId())) {
                        t.setWareName(list.getContext().getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(t.getWareId())).findFirst().get().getWareName());
                    }
                });
            }

            if (CollectionUtils.isNotEmpty(trades)) {
                Map<String, String> empNameByEmpIdMap = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList()
                        .stream().collect(Collectors.toMap(EmployeeListVO::getEmployeeId, EmployeeListVO::getEmployeeName));
                if (MapUtils.isNotEmpty(empNameByEmpIdMap)) {
                    trades.forEach(tradeVO -> {
                        tradeVO.setEmployeeName(empNameByEmpIdMap.get(tradeVO.getBuyer().getEmployeeId()));
                        tradeVO.setManagerName("system");
                        if (!TradeActivityTypeEnum.NEWPICKTRADE.toActivityType().equals(tradeVO.getActivityType())) {
                            tradeVO.setManagerName(empNameByEmpIdMap.get(tradeVO.getBuyer().getManagerId()));
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

            List<String> parentIdList = new ArrayList<>();
            if (disabledDTO.getDisabled().equals("true")) {
                trades.forEach(vo -> {
                    parentIdList.add(vo.getId());
                });
                List<TradeVO> tradeVOList = providerTradeProvider.findByParentIdList(FindProviderTradeRequest.builder().parentId(parentIdList).build()).getContext().getTradeVOList();

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
                        exportVO.setPayOrderId(tradeVO.getPayOrderId());
                        exportVO.setSkuName(tradeItemVO.getSkuName());
                        exportVO.setSpecDetails(tradeItemVO.getSpecDetails());
                        exportVO.setSkuNo(tradeItemVO.getSkuNo());
                        exportVO.setNum(tradeItemVO.getNum());

                        tradeExportVOs.add(exportVO);
                    }
                });


                try {
                    providerTradeExportBaseService.exportProvider(tradeExportVOs, response.getOutputStream(), Platform.SUPPLIER);
                    response.flushBuffer();
                } catch (IOException e) {
                    throw new SbcRuntimeException(e);
                }

            } else {
                try {
                    if (tecryptedDetauled) {
                        Map<String, String> erpNoMap = new HashMap<>();
                        List<String> skuIds = new ArrayList<>();
                        trades.forEach(var -> {
                            skuIds.addAll(var.getTradeItems().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));
                            skuIds.addAll(var.getGifts().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));
                        });
                        log.info("===========skuIds：{}", JSONObject.toJSONString(skuIds));
                        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsInfos();
                        goodsInfos.forEach(goodsInfoVO -> {
                            erpNoMap.put(goodsInfoVO.getGoodsInfoId(), goodsInfoVO.getErpGoodsInfoNo());
                        });
                        tradeExportService.export(trades, erpNoMap, response.getOutputStream(),
                                Platform.PLATFORM.equals(operator.getPlatform()), tecryptedDetauled,new HashMap<String,BigDecimal>());
                    } else {
                        tradeExportService.export(trades, response.getOutputStream(),
                                Platform.PLATFORM.equals(operator.getPlatform()));
                    }
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
        operateLogMQUtil.convertAndSend("订单服务", "导出订单", "操作成功");
    }


    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlag", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<DeliveryHomeListFlagResponse> checkDeliveryHomeFlag(@RequestBody @Valid DeliveryHomeFlagRequest request) {
        DeliveryHomeListFlagResponse deliveryHomeListFlagResponse = new DeliveryHomeListFlagResponse();
        Map<Integer, DefaultFlag> longMap = new HashMap<>();
        request.getWareHouseOrderDTOS().forEach(s -> {
            longMap.put(s.getOrderIndex(), checkDistance(s.getWareId(), request.getCustomerDeliveryAddressId()));
        });
        deliveryHomeListFlagResponse.setFlagMap(longMap);
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "验证配送到家标志位", "验证配送到家标志位");
        return BaseResponse.success(deliveryHomeListFlagResponse);
    }

    @ApiOperation(value = "验证配送到家标志位")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/checkDeliveryHomeFlagOne", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<DeliveryHomeFlagResponse> checkDeliveryHomeFlagOne(@RequestBody @Valid DeliveryHomeFlagRequest request) {
        DeliveryHomeFlagResponse deliveryHomeFlagResponse = new DeliveryHomeFlagResponse();
        deliveryHomeFlagResponse.setFlag(checkDistance(request.getWareId(), request.getCustomerDeliveryAddressId()));
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "验证配送到家标志位", "验证配送到家标志位");
        return BaseResponse.success(deliveryHomeFlagResponse);
    }

    @ApiOperation(value = "代客下单导入商品")
    @PostMapping(value = "/importGoodsInfos/{wareId}/{customerId}")
    @MultiSubmit
    public BaseResponse<ImportGoodsInfosExcel> importGoodsInfos(@RequestParam(value = "file", required = true) MultipartFile file, @PathVariable String wareId, @PathVariable String customerId, HttpServletResponse response) throws Exception {
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "代客下单导入商品", "代客下单导入商品");
        GoodsInfoImportListener goodsInfoImportListener = new GoodsInfoImportListener();
        goodsInfoImportListener.setWareId(wareId);
        goodsInfoImportListener.setCustomerId(customerId);
        EasyExcel.read(file.getInputStream(), ImportGoodsInfo.class, goodsInfoImportListener).sheet().doRead();

        if (goodsInfoImportListener.getExcelFlag()) {
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
    @MultiSubmit
    public void exportGoodsInfos(@PathVariable String uuid, HttpServletResponse response) throws Exception {
        //导入失败，数据写入redis,做导出；
        List<ExportErrorGoodsInfoExcel> exportErrorGoodsInfoExcels = JSONArray.parseArray(redisService.getString(IMPORT_GOODS_ERROR.concat(uuid)), ExportErrorGoodsInfoExcel.class);
        //读取成功删除缓存
        if (CollectionUtils.isNotEmpty(exportErrorGoodsInfoExcels)) {
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
        //记录操作日志
        operateLogMQUtil.convertAndSend("订单服务", "导出报错代客下单商品", "操作成功");
    }

    /**
     * 代客下单计算营销
     */
    @ApiOperation(value = "代客下单计算营销信息")
    @PostMapping(value = "/importGoodsInfos/getMarketing")
    @MultiSubmit
    public BaseResponse<ImportGoodsInfosExcel> getmarketingByImportGoodsInfos(@RequestBody @Valid ImportPurchaseMarketingRequest request) {

        ImportGoodsInfosExcel importGoodsInfosExcelResponse = new ImportGoodsInfosExcel();

        if (CollectionUtils.isEmpty(request.getGoodsInfoIds()) || CollectionUtils.isEmpty(request.getImportGoodsInfosList())) {
            throw new SbcRuntimeException("K-080201");
        }

        //查询用户信息
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();

        if (customer == null) {
            throw new SbcRuntimeException("K-040018");
        }

        //查询商品信息
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        esGoodsInfoQueryRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        esGoodsInfoQueryRequest.setPageNum(0);
        //获取默认最大长度
        esGoodsInfoQueryRequest.setPageSize(10000);
        List<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticService.getEsBaseInfoByParams(esGoodsInfoQueryRequest).getData();

        if (CollectionUtils.isEmpty(esGoodsInfoList)) {
            throw new SbcRuntimeException("K-080201");
        }

        //
        List<GoodsInfoVO> goodsInfoVOS = Lists.newArrayList();
        request.getImportGoodsInfosList().forEach(info -> {
            //填充属性
            EsGoodsInfo goodsInfo = esGoodsInfoList.stream().filter(esGoodsInfo -> esGoodsInfo.getGoodsInfo().getGoodsInfoId().equals(info.getGoodsInfoId())).findFirst().orElse(null);
            if (goodsInfo != null) {
                GoodsInfoVO convert = KsBeanUtil.convert(goodsInfo.getGoodsInfo(), GoodsInfoVO.class);
                //设置购买数量
                convert.setBuyCount(info.getBuyCount());
                convert.setGoodsInfoId(info.getGoodsInfoId());
                convert.setStock(BigDecimal.valueOf(info.getStock()));
                goodsInfoVOS.add(convert);
            }
        });

        PurchaseMarketingRequest purchaseMarketingRequest = new PurchaseMarketingRequest();
        purchaseMarketingRequest.setGoodsInfoIds(request.getGoodsInfoIds());
        purchaseMarketingRequest.setGoodsInfos(goodsInfoVOS);
        purchaseMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
        purchaseMarketingRequest.setWareId(request.getWareId());

        //查询营销
        PurchaseMarketingResponse purchaseMarketingResponse = purchaseProvider.getPurchasesMarketing(purchaseMarketingRequest).getContext();

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
        operateLogMQUtil.convertAndSend("订单服务", "代客下单计算营销信息", "操作成功");
        return BaseResponse.success(importGoodsInfosExcelResponse);
    }

    /**
     * 根据订单号查询退款列表
     *
     * @return BaseResponse<List < com.wanmi.sbc.order.api.response.manualrefund.ManualRefundResponse>>
     */
    @ApiOperation(value = "根据订单编号查询退款列表")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "orderCode", value = "订单编号", required = true)
    @RequestMapping(value = "/manualRefunds/{orderCode}/")
    public BaseResponse<List<ManualRefundResponse>> getManualRefundRespByOrderCode(@PathVariable String orderCode) {
        return manualRefundQueryProvider.getManualRefundRespByOrderCode(ManualRefundResponseByOrderCodeRequest.builder().orderCode(orderCode).build());
    }

    /**
     * 根据订单号查询退款列表
     *
     * @return BaseResponse<List < com.wanmi.sbc.order.api.response.manualrefund.ManualRefundResponse>>
     */
    @ApiOperation(value = "根据订单编号查询退款列表")
    @RequestMapping(value = "/manualRefundByOrderCode")
    public BaseResponse<List<ManualRefundResponse>> manualRefundByOrderCode(@RequestBody @Valid ManualRefundByOrderCodeRequest manualRefundByOrderCodeRequest) {
        ManualRefundResponseByOrderCodeRequest request = KsBeanUtil.convert(manualRefundByOrderCodeRequest, ManualRefundResponseByOrderCodeRequest.class);
        request.setOperator(commonUtil.getOperator());
        return manualRefundQueryProvider.manualRefundByOrderCode(request);
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
    private DefaultFlag checkDistance(Long wareId, String customerDeleiverAddressId) {
        CustomerDeliveryAddressByIdResponse response = customerDeliveryAddressQueryProvider.getById(CustomerDeliveryAddressByIdRequest
                .builder().deliveryAddressId(customerDeleiverAddressId).build()).getContext();
        if (Objects.isNull(response) || response.getDelFlag().equals(DeleteFlag.YES)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        WareHouseVO wareHouseVO = commonUtil.getWareHouseByWareId(wareId);
        if ((Objects.isNull(wareHouseVO) || (DeleteFlag.YES.equals(wareHouseVO.getDelFlag())))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该收货地址不存在");
        }
        if (wareHouseVO.getDistance() == null) {
            return DefaultFlag.NO;
        }
        double distance = LonLatUtil.getDistance(response.getLng(), response.getLat(), wareHouseVO.getLng(), wareHouseVO.getLat());
        if (distance <= (wareHouseVO.getDistance() * 1000)) {
            return DefaultFlag.YES;
        }
        return DefaultFlag.NO;
    }

    /**
     * 功能描述: 验证自提门店信息,如果存在塞入自提信息
     */
    private void validPickUpPoint(TradeCreateDTO inner, Long storeId) {

        if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
            WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                    .wareId(inner.getWareId()).storeId(storeId).build()).getContext().getWareHouseVO();
            if (wareHouseVO.getPickUpFlag().toValue() == PickUpFlag.NO.toValue()) {
                throw new SbcRuntimeException("仓库不合法");
            }
            inner.setWareHouseVO(wareHouseVO);
        }
    }

    /**
     * 统计订单SKU的已退货数量
     * @param tradeItemVO
     * @param tid
     */
    private void buildReturnNum(TradeItemVO tradeItemVO, String tid) {
        //统计该订单关联的已退货数量
        ReturnOrderListByTidRequest request = ReturnOrderListByTidRequest.builder().tid(tid).build();
        BaseResponse<ReturnOrderListByTidResponse> returnOrderListByTidResponseBaseResponse = returnOrderQueryProvider.listByTid(request);
        //退单为空则已退货数量默认都为0 否则根据sku编码查询
        tradeItemVO.setReturnedQuantity(0);
        if(Objects.nonNull(returnOrderListByTidResponseBaseResponse)) {
            List<ReturnOrderVO> collect = returnOrderListByTidResponseBaseResponse.getContext().getReturnOrderList().stream().filter(returnOrderVO -> returnOrderVO.getReturnFlowState().equals(ReturnFlowState.COMPLETED)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(collect)) {
                collect.forEach( returnOrderVO -> {
                    if(CollectionUtils.isNotEmpty(returnOrderVO.getReturnItems())) {
                        returnOrderVO.getReturnItems().forEach(returnItemVO -> {
                            if(tradeItemVO.getSkuNo().equals(returnItemVO.getSkuNo())) {
                                tradeItemVO.setReturnedQuantity(returnItemVO.getNum().intValue());
                            }
                        });
                    }
                });
            }
        }
    }

}
