package com.wanmi.sbc.marketing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CompanyType;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandByIdRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNosRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandByIdResponse;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByNoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.service.GoodsMarketingService;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountQueryProvider;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionQueryProvider;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdRequest;
import com.wanmi.sbc.marketing.api.request.market.*;
import com.wanmi.sbc.marketing.api.response.market.MarketingGetByIdResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPageResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingScopeByMarketingIdResponse;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.marketing.bean.dto.MarketingPageDTO;
import com.wanmi.sbc.marketing.bean.dto.SkuExistsDTO;
import com.wanmi.sbc.marketing.bean.enums.GiftType;
import com.wanmi.sbc.marketing.bean.enums.MarketingStatus;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.enums.MarketingType;
import com.wanmi.sbc.marketing.bean.vo.*;
import com.wanmi.sbc.marketing.request.MarketingGoodsRequest;
import com.wanmi.sbc.marketing.request.MarketingOneGoodsTerminationRequest;
import com.wanmi.sbc.marketing.request.MarketingPageListRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Api(tags = "MarketingController", description = "营销服务API")
@RestController
@RequestMapping("/marketing")
@Slf4j
public class MarketingController {

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private MarketingScopeQueryProvider marketingScopeQueryProvider;


    @Autowired
    private MarketingProvider marketingProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private MarketingScopeProvider marketingScopeProvider;

    @Autowired
    private EsGoodsModifyInventoryService esGoodsModifyInventoryService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsMarketingService goodsMarketingService;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private MarketingFullDiscountQueryProvider marketingFullDiscountQueryProvider;

    @Autowired
    private MarketingFullReductionQueryProvider marketingFullReductionQueryProvider;

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    private AtomicInteger exportCount = new AtomicInteger(0);

    /**
     * 删除营销活动 Done
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "删除营销活动")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/deleteMarketingById/{marketingId}", method = RequestMethod.DELETE)
    @Transactional
    public BaseResponse deleteMarketingId(@PathVariable("marketingId")Long marketingId){
        MarketingDeleteByIdRequest marketingDeleteByIdRequest = new MarketingDeleteByIdRequest();
        marketingDeleteByIdRequest.setMarketingId(marketingId);
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        log.info("deleteMarketingId enter");
        log.info("deleteMarketingId:{}",commonUtil.getOperatorId());
        marketingDeleteByIdRequest.setOperatorId(commonUtil.getOperatorId());
        marketingProvider.deleteById(marketingDeleteByIdRequest);

        String name = getMarketingName(marketingId);

        operateLogMQUtil.convertAndSend("营销","删除促销活动",EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name())+"-"+ name);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 终止营销活动 Done
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "终止营销活动")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/termination/{marketingId}", method = RequestMethod.PUT)
    @Transactional
    public BaseResponse terminationMarketingId(@PathVariable("marketingId")Long marketingId){

        MarketingStartByIdRequest marketingStartByIdRequest = new MarketingStartByIdRequest();
        marketingStartByIdRequest.setMarketingId(marketingId);
        marketingStartByIdRequest.setOperatorId(commonUtil.getOperatorId());
        marketingProvider.terminationMarketingById(marketingStartByIdRequest);

        MarketingVO marketingVo = getMarketingVo(marketingId);
        operateLogMQUtil.convertAndSend("营销","终止促销活动",
                 EnumTranslateUtil.getFieldAnnotation(marketingVo.getSubType()).get(marketingVo.getSubType().name())+"-"+marketingVo.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取营销活动列表
     * @param marketingPageListRequest {@link MarketingPageRequest}
     * @return
     */
    @ApiOperation(value = "获取营销活动列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<MarketingPageVO>> getMarketingList(@RequestBody MarketingPageListRequest marketingPageListRequest) {
        log.info("====营销活动列表=============");
        MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
        marketingPageRequest.setStoreId(commonUtil.getStoreId());
        MarketingPageDTO request = KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class);
        if(!StringUtils.isEmpty(marketingPageListRequest.getErpGoodsInfoNo())){
            BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider.findAllGoodsByErpNos(
                    GoodsInfoByErpNosRequest.builder()
                            .erpGoodsInfoNos(Arrays.asList(marketingPageListRequest.getErpGoodsInfoNo()))
                            .build()
            );
            GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
            if(CollectionUtils.isNotEmpty(context.getGoodsInfo())){
                List<String> goodsInfoIds = context.getGoodsInfo().stream().map(o -> o.getGoodsInfoId()).collect(Collectors.toList());
                request.setGoodsInfoIds(goodsInfoIds);
            }else{
                request.setGoodsInfoIds(Arrays.asList("*****"));
            }
        }

        marketingPageRequest.setMarketingPageDTO(request);
        BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.page(marketingPageRequest);
        return BaseResponse.success(pageResponse.getContext().getMarketingVOS());
    }


    /**
     * 根据营销Id获取营销详细信息
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "根据营销Id获取营销详细信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/{marketingId}", method = RequestMethod.GET)
    public BaseResponse<MarketingForEndVO> getMarketingById(@PathVariable("marketingId")Long marketingId){
        MarketingGetByIdByIdRequest marketingGetByIdRequest = new MarketingGetByIdByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingForEndVO marketingResponse = marketingQueryProvider.getByIdForSupplier(marketingGetByIdRequest)
                .getContext().getMarketingForEndVO();
        if(Objects.nonNull(marketingResponse.getGoodsList())){
            GoodsInfoResponseVO goodsList = marketingResponse.getGoodsList();
            MicroServicePage<GoodsInfoVO> goodsInfoPage = goodsList.getGoodsInfoPage();
            List<GoodsInfoVO> content = goodsInfoPage.getContent();
            if(CollectionUtils.isNotEmpty(content)){
                List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();
                content.forEach(var->{
                    WareHouseVO wareHouseVO = wareHouseVOList.stream().filter(wareHouseVOOne -> wareHouseVOOne.getWareId().equals(var.getWareId())).findFirst().orElse(new WareHouseVO());
                    var.setWareName(wareHouseVO.getWareName());
                });
            }
        }
        if(marketingResponse.getStoreId() != null && commonUtil.getStoreId() != null && commonUtil.getStoreId().longValue() != marketingResponse.getStoreId()){
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_NO_AUTH_TO_VIEW);
        }else{
            List<MarketingScopeVO> marketingScopeList = marketingResponse.getMarketingScopeList();
            Map<String, BoolFlag> scopeMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(marketingScopeList)) {
                scopeMap =
                        marketingScopeList.stream().collect(Collectors.toMap(MarketingScopeVO::getScopeId,
                                MarketingScopeVO::getTerminationFlag, (k1, k2) -> k1));
            }
            //折扣商品：将折扣价设置为市场价
            for (GoodsInfoVO goodsInfoVO : marketingResponse.getGoodsList().getGoodsInfoPage()) {
                if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
                    goodsInfoVO.setSalePrice(goodsInfoVO.getSpecialPrice());
                }
                BoolFlag boolFlag = scopeMap.get(goodsInfoVO.getGoodsInfoId());
                goodsInfoVO.setTerminationFlag(boolFlag);
            }

            /**
             * isShowActiveStatus决定营销活动详情页，商品条目操作拦是否展示操作"终止"按钮
             */
            LocalDateTime beginTime = marketingResponse.getBeginTime();
            LocalDateTime endTime = marketingResponse.getEndTime();
            BoolFlag isPause = marketingResponse.getIsPause();
            DeleteFlag delFlag = marketingResponse.getDelFlag();
            BoolFlag isDraft = marketingResponse.getIsDraft();

            LocalDateTime now = LocalDateTime.now();
            boolean start = now.isAfter(beginTime) && now.isBefore(endTime)
                    && BoolFlag.NO.equals(isPause)
                    && DeleteFlag.NO.equals(delFlag)
                    && BoolFlag.NO.equals(isDraft);
            log.info("getMarketingById.start:{}", start);
            log.info("getMarketingById.isPause:{}", isPause);
            log.info("getMarketingById.delFlag:{}", delFlag);
            log.info("getMarketingById.isDraft:{}", isDraft);
            log.info("getMarketingById.beginTime:{}", beginTime);
            log.info("getMarketingById.endTime:{}", endTime);
            log.info("getMarketingById.now:{}", now);
            if (start){
                marketingResponse.setIsShowActiveStatus(Boolean.TRUE);
            }

            //填充套装活动关联商品的营销活动信息
            if (marketingResponse.getMarketingType().equals(MarketingType.SUIT)) {
                marketingResponse.getMarketingSuitDetialVOList().forEach(marketingSuitDetialVO -> {
                    MarketingGetByIdRequest request = new MarketingGetByIdRequest();
                    request.setMarketingId(marketingSuitDetialVO.getGoodsMarketingId());
                    marketingSuitDetialVO.setMarketingVO(marketingQueryProvider.getById(request).getContext().getMarketingVO());
                });
            }
            return BaseResponse.success(marketingResponse);
        }
    }

    /**
     * 查询在相同类型的营销活动中，skuList是否存在重复 Done
     * @param skuExistsDTO {@link SkuExistsDTO}
     * @return
     */
    @ApiOperation(value = "查询在相同类型的营销活动中，skuList是否存在重复")
    @RequestMapping(value = "/sku/exists", method = RequestMethod.POST)
    public BaseResponse<List<String>> ifSkuExists(@RequestBody @Valid SkuExistsDTO skuExistsDTO) {

        // 目前促销活动只包括满减，满折，满赠
        // 自营商家一个商品在一个时间段可以有多个不同类型的促销活动。比如可以有满减满赠，但不能同时满金额减，满数量减
        // 第三方商家一个商品在一个时间段只能有一个促销活动。比如有满减了，就不能满折、满赠
        ExistsSkuByMarketingTypeRequest existsSkuByMarketingTypeRequest = ExistsSkuByMarketingTypeRequest.builder()
                .storeId(commonUtil.getStoreId())
                .skuExistsDTO(skuExistsDTO)
                .build();
        List<String> result = null;
        int companyType = commonUtil.getCompanyType();
        if (companyType == CompanyType.SUPPLIER.toValue()) {
			skuExistsDTO.setMarketingType(MarketingType.REDUCTION);
			result = marketingQueryProvider.queryExistsSkuByMarketingType(existsSkuByMarketingTypeRequest).getContext();
			
			skuExistsDTO.setMarketingType(MarketingType.DISCOUNT);
			List<String> discountResult = marketingQueryProvider.queryExistsSkuByMarketingType(existsSkuByMarketingTypeRequest).getContext();
			result.addAll(discountResult);
			
			skuExistsDTO.setMarketingType(MarketingType.GIFT);
			List<String> giftResult = marketingQueryProvider.queryExistsSkuByMarketingType(existsSkuByMarketingTypeRequest).getContext();
			result.addAll(giftResult);
		}else {
			 result = marketingQueryProvider.queryExistsSkuByMarketingType(existsSkuByMarketingTypeRequest).getContext();
		}
        return BaseResponse.success(result);

        // 以下代码注释的原因：新版营销活动没有关于订单相关的营销活动
//        if(MarketingSubType.GIFT_FULL_ORDER.equals(skuExistsDTO.getMarketingSubType())
//                || MarketingSubType.REDUCTION_FULL_ORDER.equals(skuExistsDTO.getMarketingSubType())
//           || MarketingSubType.DISCOUNT_FULL_ORDER.equals(skuExistsDTO.getMarketingSubType())){
//            return BaseResponse.success(marketingQueryProvider.queryExistsSkuByMarketingTypeFullOrder(ExistsSkuByMarketingTypeRequest.builder()
//                    .storeId(commonUtil.getStoreId())
//                    .skuExistsDTO(skuExistsDTO)
//                    .build()).getContext());
//        } else if (MarketingSubType.SUIT_TO_BUY.equals(skuExistsDTO.getMarketingSubType())) {
//            return BaseResponse.success(marketingQueryProvider.queryExistsMarketingByMarketingType(ExistsSkuByMarketingTypeRequest.builder()
//                    .storeId(commonUtil.getStoreId())
//                    .skuExistsDTO(skuExistsDTO)
//                    .build())).getContext();
//        }else {
//            if(MarketingType.GIFT.equals(skuExistsDTO.getMarketingType())) {
//                MarketingListResponse response = marketingQueryProvider.list(MarketingBaseRequest.builder()
//                        .marketingType(skuExistsDTO.getMarketingType())
//                        .subType(MarketingSubType.GIFT_FULL_ORDER)
//                        .build()).getContext();
//                if (Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getMarketingVOList())) {
//                    List<MarketingVO> marketingVOS = response.getMarketingVOList().stream().filter(m ->
//                            (skuExistsDTO.getStartTime().isAfter(m.getBeginTime()) && skuExistsDTO.getStartTime().isBefore(m.getEndTime()))
//                                    || (skuExistsDTO.getEndTime().isAfter(m.getBeginTime()) && skuExistsDTO.getEndTime().isBefore(m.getEndTime()))).collect(Collectors.toList());
//                    if (CollectionUtils.isNotEmpty(marketingVOS)) {
//                        throw new SbcRuntimeException(MarketingErrorCode.MARKETING_FULL_GIFT_ORDER_EXCEPTION, "时间与满订单赠活动冲突！！");
//                    }
//                }
//            }
//            return BaseResponse.success(marketingQueryProvider.queryExistsSkuByMarketingType(ExistsSkuByMarketingTypeRequest.builder()
//                    .storeId(commonUtil.getStoreId())
//                    .skuExistsDTO(skuExistsDTO)
//                    .build()).getContext());
//        }
    }


    /**
     * 单个赠品终止营销活动 Done
     * @param request
     * @return
     */
    @ApiOperation(value = "单个赠品终止营销活动")
    @PostMapping(value = "/stopGiveGoods")
    @Transactional
    public BaseResponse stopGiveGoods(@RequestBody MarketingStopGiveGoodsRequest request){

        marketingQueryProvider.stopGiveGoods(request);

        //根据商品与营销关联表获取商品名称和ERP编码
        String goodsName = "";
        String goodsErpCode = "";
        GoodsInfoByIdRequest goodsInfoByIdRequest = new GoodsInfoByIdRequest();
        goodsInfoByIdRequest.setGoodsInfoId(request.getProductId());
        GoodsInfoVO goodsInfo = goodsInfoQueryProvider.getById(goodsInfoByIdRequest).getContext().getGoodsInfos().get(0);
        if(Objects.nonNull(goodsInfo)){
            goodsName = goodsInfo.getGoodsInfoName();
            goodsErpCode = goodsInfo.getErpGoodsInfoNo();
        }

        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        operateLogMQUtil.convertAndSend("营销","单个赠品终止营销活动",
                EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name()) +
                        "-"+ ( Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " ")+
                        "-" + goodsName + "-" + goodsErpCode + "-终止时间：" + DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1));

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 单个商品终止营销活动
     * @param request
     * @return
     */
    @ApiOperation(value = "单个商品终止营销活动")
    @PostMapping(value = "/terminationGoods")
    @Transactional
    public BaseResponse terminationByOneGoods(@RequestBody MarketingOneGoodsTerminationRequest request){

        // 这是幂等操作吗
        if (BoolFlag.YES.equals(request.getTerminationFlag())){
            return BaseResponse.SUCCESSFUL();
        }

        // 查询营销信息
        Long marketingId = request.getMarketingId();
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        Optional<MarketingVO> marketingVOOptional = Optional.ofNullable(marketingQueryProvider.getById(marketingGetByIdRequest))
                .map(BaseResponse::getContext)
                .map(MarketingGetByIdResponse::getMarketingVO);
        if(!marketingVOOptional.isPresent()){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
        MarketingVO marketing = marketingVOOptional.get();
        
        // 查询营销关联的所有商品
        MarketingScopeByMarketingIdRequest idRequest = new MarketingScopeByMarketingIdRequest();
        idRequest.setMarketingId(marketingId);
        Optional<List<MarketingScopeVO>> marketingScopeVOOptional = Optional.ofNullable(marketingScopeQueryProvider.listByMarketingId(idRequest))
                .map(BaseResponse::getContext)
                .map(MarketingScopeByMarketingIdResponse::getMarketingScopeVOList);
        if(!marketingScopeVOOptional.isPresent()){
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
        List<MarketingScopeVO> marketingScopeVOList = marketingScopeVOOptional.get();


        // 过滤当前商品 看否存在未终止的营销活动商品
        String scopeId = request.getScopeId();
        List<MarketingScopeVO> marketingScopeVOS = marketingScopeVOList.stream()
                        .filter((c) -> {
                            return !StringUtils.equals(scopeId, c.getScopeId())
                                        && BoolFlag.NO.equals(c.getTerminationFlag());
                        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(marketingScopeVOS)) {
            //终止单个商品
            Long marketingScopeId = request.getMarketingScopeId();
            TerminationMarketingScopeRequest scopeRequest = new TerminationMarketingScopeRequest();
            scopeRequest.setMarketingScopeId(marketingScopeId);
            marketingScopeProvider.terminationMarketingIdAndScopeId(scopeRequest);
            //根据商品与营销关联表获取商品名称和ERP编码
            String goodsName = "";
            String goodsErpCode = "";
            GoodsInfoByIdRequest goodsInfoByIdRequest = new GoodsInfoByIdRequest();
            goodsInfoByIdRequest.setGoodsInfoId(scopeId);
            Optional<List<GoodsInfoVO>> goodsInfoVOOptional = Optional.ofNullable(goodsInfoQueryProvider.getById(goodsInfoByIdRequest))
                    .map(BaseResponse::getContext)
                    .map(GoodsInfoByIdResponse::getGoodsInfos);
            if(goodsInfoVOOptional.isPresent()){
                List<GoodsInfoVO> goodsInfoVOList = goodsInfoVOOptional.get();
                goodsName = goodsInfoVOList.get(0).getGoodsInfoName();
                goodsErpCode = goodsInfoVOList.get(0).getErpGoodsInfoNo();
            }
            operateLogMQUtil.convertAndSend("营销","单个终止促销活动",
                    ( Objects.nonNull(marketing.getSubType()) ? EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name()) : " ")
                            +"-"+ ( Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " ")
                            +"-"+ goodsName + "-" + goodsErpCode);
            return BaseResponse.SUCCESSFUL();
        }

        // 该营销活动关联的商品都终止了，当前营销活动页需要终止
        MarketingStartByIdRequest marketingStartByIdRequest = new MarketingStartByIdRequest();
        marketingStartByIdRequest.setMarketingId(marketingId);
        marketingStartByIdRequest.setOperatorId(commonUtil.getOperatorId());
        marketingProvider.terminationMarketingById(marketingStartByIdRequest);

        operateLogMQUtil.convertAndSend("营销","终止促销活动",
                ( Objects.nonNull(marketing.getSubType()) ? EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name()) : " ")
                        +"-"+ ( Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " "));
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 导出订单
     *
     * @return
     */
    @ApiOperation(value = "导出订单")
//    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params", method = RequestMethod.POST)
    public void export(@RequestBody MarketingGoodsRequest marketingGoodsRequest, HttpServletResponse response) {
        try {
            if (exportCount.incrementAndGet() > 1) {
                throw new SbcRuntimeException(SiteResultCode.ERROR_000016);
            }

            if(null == marketingGoodsRequest.getEncrypted()){
                throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
            }

            Operator operator = commonUtil.getOperator();
            log.debug(String.format("/marketing/export/params, employeeId=%s", operator.getUserId()));

            String decrypted = new String(Base64.getUrlDecoder().decode(marketingGoodsRequest.getEncrypted().getBytes()));
            MarketingGoodsExportRequest marketingGoodsExportRequest = JSON.parseObject(decrypted, MarketingGoodsExportRequest.class);


//            long startTimeLocal = StringUtils.isEmpty(marketingGoodsExportRequest.getBeginTime()) ? 0 : DateUtil.parse(marketingGoodsExportRequest.getBeginTime(),DateUtil.FMT_TIME_1).toInstant(ZoneOffset.of("+8")).getEpochSecond();
//            long endTimeLocal = StringUtils.isEmpty(marketingGoodsExportRequest.getEndTime()) ? 0 : DateUtil.parse(marketingGoodsExportRequest.getEndTime(),DateUtil.FMT_TIME_1).toInstant(ZoneOffset.of("+8")).getEpochSecond();
            String adminId = operator.getAdminId();
            String arr[] = marketingGoodsExportRequest.getGoodsErpNos().split(",");
            List<GoodsMarketingExportVo> goodsMarketingExportVoList = new ArrayList<>();
            //根据ERPNO查出对应的goodsInfo
            List<GoodsInfoVO> goodsInfos =  new ArrayList<>();
            for (String erpNo: arr) {
                List<GoodsInfoVO> goods =  goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder()
                        .likeErpNo(erpNo).delFlag(0).build()).getContext().getGoodsInfos();
                if(goods != null && !goods.isEmpty()){
                    goodsInfos.addAll(goods);
                }
            }
            log.info("====GoodsErpNos:{},goodsInfos:{}",marketingGoodsExportRequest.getGoodsErpNos(), JSONObject.toJSONString(goodsInfos));
            if(goodsInfos != null && !goodsInfos.isEmpty()){
              List<String> goodsInfoIds =  goodsInfos.stream().map(m -> m.getGoodsInfoId()).collect(Collectors.toList());
               Map<String, List<MarketingVO>> listMap = marketingQueryProvider.
                      getOrderMarketingMap(MarketingMapGetByGoodsIdRequest.builder().goodsInfoIdList(goodsInfoIds).
                              startTime(marketingGoodsExportRequest.getStartTime()).endTime(marketingGoodsExportRequest.getEndTime()).build()).getContext().getListMap();
                log.info("====goodsInfoIds:{},listMap:{}",JSONObject.toJSONString(goodsInfoIds), JSONObject.toJSONString(listMap));
              goodsInfos.forEach(var->{
                  GoodsMarketingExportVo vo = new GoodsMarketingExportVo();
                  vo.setErpGoodsInfoNo(var.getErpGoodsInfoNo());
                  vo.setGoodsInfoNo(var.getGoodsInfoNo());
                  vo.setGoodsInfoName(var.getGoodsInfoName());
                  if(var.getCateId() != null){
                      GoodsCateByIdResponse cate  = goodsCateQueryProvider.getById(GoodsCateByIdRequest.builder().cateId(var.getCateId()).build()).getContext();
                      if(Objects.nonNull(cate)){
                          vo.setCateName(cate.getCateName());
                      }
                  }
                  if(var.getBrandId() != null){
                      GoodsBrandByIdResponse brand  = goodsBrandQueryProvider.getById(GoodsBrandByIdRequest.builder().brandId(var.getBrandId()).build()).getContext();
                      if(Objects.nonNull(brand)){
                          vo.setBrandName(brand.getBrandName());
                      }
                  }

                  List<MarketingVO> marketings = listMap.get(var.getGoodsInfoId());

                  log.info("====marketings:{},var:{}",JSONObject.toJSONString(marketings),JSONObject.toJSONString(var));
                  boolean isAdd = false;
                  if(marketings != null && !marketings.isEmpty()){
                        for(int i = 0; i < marketings.size(); i++){
                            MarketingVO marketing = marketings.get(i);
                            log.info("====MarketingVO:{},marketing.getUpdatePerson():{}",JSONObject.toJSONString(marketing),marketing.getUpdatePerson());
                            //时间校验
//                            long makStartTime = marketing.getBeginTime().toInstant(ZoneOffset.of("+8")).getEpochSecond();
//                            long makEndTime = marketing.getEndTime().toInstant(ZoneOffset.of("+8")).getEpochSecond();
//                            log.info("====makStartTime:{},startTimeLocal:{},makEndTime:{},endTimeLocal:{}",makStartTime,startTimeLocal,makEndTime,endTimeLocal);
//                            if(!(makStartTime >= startTimeLocal  && makEndTime <= endTimeLocal)) {
//                                continue;
//                            }
                            //规则
                            StringBuffer rule = new StringBuffer();
                            //满折（marketing_full_discount_level）
                            if(MarketingType.DISCOUNT.name().equals(marketing.getMarketingType().name())){
                                MarketingFullDiscountByMarketingIdRequest marketingFullDiscountByMarketingIdRequest = new MarketingFullDiscountByMarketingIdRequest();
                                marketingFullDiscountByMarketingIdRequest.setMarketingId(marketing.getMarketingId());
                                List<MarketingFullDiscountLevelVO> marketingFullDiscountLevelVOList =  marketingFullDiscountQueryProvider.
                                        listByMarketingId(marketingFullDiscountByMarketingIdRequest).getContext().getMarketingFullDiscountLevelVOList();
                                for (MarketingFullDiscountLevelVO discountLevelVO: marketingFullDiscountLevelVOList) {
                                   if(MarketingSubType.DISCOUNT_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
                                        rule.append("满：" + discountLevelVO.getFullAmount() + "元 打：" + discountLevelVO.getDiscount() + "折;");
                                        //                                subType = "满金额折";
                                    }else if(MarketingSubType.DISCOUNT_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                        rule.append("满：" + discountLevelVO.getFullCount() + "件 打：" + discountLevelVO.getDiscount() + "折;");
                                        //                                subType = "满数量折";
                                    }
                                }
                            }
                            //满减（marketing_full_reduction_level）
                            else if(MarketingType.REDUCTION.name().equals(marketing.getMarketingType().name())){
                                MarketingFullReductionByMarketingIdRequest marketingFullReductionByMarketingIdRequest = new MarketingFullReductionByMarketingIdRequest();
                                marketingFullReductionByMarketingIdRequest.setMarketingId(marketing.getMarketingId());
                                List<MarketingFullReductionLevelVO> marketingFullReductionLevelVOList = marketingFullReductionQueryProvider
                                        .listByMarketingId(marketingFullReductionByMarketingIdRequest).getContext().getMarketingFullReductionLevelVOList();
                                for (MarketingFullReductionLevelVO reductionLevelVO: marketingFullReductionLevelVOList) {
                                    if(MarketingSubType.REDUCTION_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
//                                     subType = "满金额减";
                                        rule.append("满：" + reductionLevelVO.getFullAmount() + "元 减：" + reductionLevelVO.getReduction() + "元;");
                                    }else if(MarketingSubType.REDUCTION_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                        rule.append("满：" + reductionLevelVO.getFullCount() + "件 减：" + reductionLevelVO.getReduction() + "元;");
                                        //                                subType = "满数量减";
                                    }
                                }

                            }
                            //满赠
                            else{
                                FullGiftLevelListByMarketingIdRequest fullGiftLevelListByMarketingIdRequest = new FullGiftLevelListByMarketingIdRequest();
                                fullGiftLevelListByMarketingIdRequest.setMarketingId(marketing.getMarketingId());
                                List<MarketingFullGiftLevelVO> fullGiftLevelVOList = fullGiftQueryProvider
                                        .listLevelByMarketingId(fullGiftLevelListByMarketingIdRequest).getContext().getFullGiftLevelVOList();
                                for (MarketingFullGiftLevelVO giftLevelVO: fullGiftLevelVOList) {
                                    if(MarketingSubType.GIFT_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
                                        rule.append("满：" + giftLevelVO.getFullAmount() + "元 赠："+(giftLevelVO.getGiftType().name().equals(GiftType.ALL.name())
                                                ? "默认全赠" : "可选一种") +";");
                                        //                                subType = "满金额赠";
                                    }else if(MarketingSubType.GIFT_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                        rule.append("满：" + giftLevelVO.getFullCount() + "件 赠："+(giftLevelVO.getGiftType().name().equals(GiftType.ALL.name())
                                                ? "默认全赠" : "可选一种") +";");
                                        //                                subType = "满数量赠";
                                    }
                                }
                            }
                            String subType = "";
                            //0：满金额减 1：满数量减 2:满金额折 3:满数量折
                            if(MarketingSubType.REDUCTION_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
                                subType = "满金额减";
                            }else if(MarketingSubType.REDUCTION_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                subType = "满数量减";
                            }else if(MarketingSubType.DISCOUNT_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
                                subType = "满金额折";
                            }else if(MarketingSubType.DISCOUNT_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                subType = "满数量折";
                            }else if(MarketingSubType.GIFT_FULL_AMOUNT.name().equals(marketing.getSubType().name())){
                                subType = "满金额赠";
                            }else if(MarketingSubType.GIFT_FULL_COUNT.name().equals(marketing.getSubType().name())){
                                subType = "满数量赠";
                            }else if(MarketingSubType.GIFT_FULL_ORDER.name().equals(marketing.getSubType().name())){
                                subType = "满订单赠";
                            }else if(MarketingSubType.REDUCTION_FULL_ORDER.name().equals(marketing.getSubType().name())){
                                subType = "满订单减";
                            }else if(MarketingSubType.DISCOUNT_FULL_ORDER.name().equals(marketing.getSubType().name())){
                                subType = "满订单折";
                            }else if(MarketingSubType.SUIT_TO_BUY.name().equals(marketing.getSubType().name())){
                                subType = "套装购买";
                            }
                            String beginAndEndTime = DateUtil.getDateTime(marketing.getBeginTime()) + "~" + DateUtil.getDateTime(marketing.getEndTime());
                            String wareName = var.getWareId().longValue() == 46L  ? "南昌仓" : "长沙仓";
                            String isOverlapStr = marketing.getIsOverlap().name().equals(BoolFlag.NO.name()) ? "否" : "是";
                            String terminationFlag = marketing.getTerminationFlag().name().equals(BoolFlag.NO.name()) ? "未终止" : "已终止";
                            String updateTime = marketing.getUpdateTime() != null ? DateUtil.getDateTime(marketing.getUpdateTime()) : "";
                            EmployeeByIdResponse employee = null;
                            if(!StringUtils.isEmpty(marketing.getUpdatePerson())){
                                employee = employeeQueryProvider.getById(EmployeeByIdRequest.builder().employeeId(marketing.getUpdatePerson()).build()).getContext();
                            }
                            log.info(" =====marketing.getUpdatePerson():{}===employee:{}",marketing.getUpdatePerson(),JSONObject.toJSONString(employee));
                            if(i == 0 || (i != 0 && !isAdd)){
                                vo.setSubType(subType);
                                vo.setMarketingName(marketing.getMarketingName());
                                vo.setBeginAndEndTime(beginAndEndTime);
                                vo.setSuitCouponDesc(rule.toString());
                                vo.setWareName(wareName);
                                vo.setIsOverlap(isOverlapStr);
                                vo.setTerminationFlag(terminationFlag);
                                vo.setUpdatePerson(Objects.nonNull(employee) ? employee.getAccountName() : "");
                                vo.setUpdateTime(updateTime);
                                isAdd = true;
                                goodsMarketingExportVoList.add(vo);
                            }else{
                                GoodsMarketingExportVo voTow = new GoodsMarketingExportVo();
                                voTow.setSubType(subType);
                                voTow.setMarketingName(marketing.getMarketingName());
                                voTow.setBeginAndEndTime(beginAndEndTime);
                                voTow.setSuitCouponDesc(rule.toString());
                                voTow.setWareName(wareName);
                                voTow.setIsOverlap(isOverlapStr);
                                voTow.setTerminationFlag(terminationFlag);
                                voTow.setUpdatePerson(Objects.nonNull(employee) ? employee.getAccountName() : "");
                                voTow.setUpdateTime(updateTime);
                                goodsMarketingExportVoList.add(voTow);
                            }
                        }
                  }else{
                      goodsMarketingExportVoList.add(vo);
                  }
              });
            }

            String headerKey = "Content-Disposition";
            LocalDateTime dateTime = LocalDateTime.now();

            String fileName = String.format("批量导出订单_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("/marketing/export/params, fileName={},", fileName, e);
            }
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);
            log.info("=========goodsMarketingExportVoList:{}",JSONObject.toJSONString(goodsMarketingExportVoList));
            goodsMarketingService.export(goodsMarketingExportVoList, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            log.error("/marketing/export/params error: ", e);
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        } finally {
            exportCount.set(0);
        }
        operateLogMQUtil.convertAndSend("营销","导出订单","操作成功");
    }

    /**
     * 获取套装购买活动及商品信息列表[关联活动商品只允许一个活动设置一个商品]
     * @param marketingPageListRequest
     * @return
     */
    @ApiOperation(value = "获取套装购买活动及商品信息列表")
    @RequestMapping(value = "/listForSuit",method = RequestMethod.POST)
    @Deprecated
    public BaseResponse<MicroServicePage<MarketingPageVO>> getMarketingSuitList(@RequestBody MarketingPageListRequest marketingPageListRequest) {
        marketingPageListRequest.setQueryTab(MarketingStatus.S_NS);
        MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
         marketingPageRequest.setStoreId(commonUtil.getStoreId());
        marketingPageRequest.setMarketingPageDTO(KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class));
        BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.pageForSuit(marketingPageRequest);
        //填充关联商品名称
        pageResponse.getContext().getMarketingVOS().getContent().forEach(marketingPageVO -> {
            marketingPageVO.setGoodsInfoName(goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                    .goodsInfoId(marketingPageVO.getMarketingScopeList().get(0)
                            .getScopeId()).build()).getContext().getGoodsInfoName());
        });

        return BaseResponse.success(pageResponse.getContext().getMarketingVOS());
    }

    /**
     * 暂停营销活动
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "暂停营销活动")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/pause/{marketingId}", method = RequestMethod.PUT)
    @Transactional
    @Deprecated
    public BaseResponse pauseMarketingId(@PathVariable("marketingId")Long marketingId){
        //进行中的营销活动，才能暂停
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        if(marketing != null){
            //如果现在时间在活动开始之前或者活动已经结束
            if(LocalDateTime.now().isBefore(marketing.getBeginTime()) || LocalDateTime.now().isAfter(marketing.getEndTime())){
                throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_PAUSE);
            }else{
                MarketingPauseByIdRequest marketingPauseByIdRequest = MarketingPauseByIdRequest.builder().build();
                marketingPauseByIdRequest.setMarketingId(marketingId);
                marketingProvider.pauseById(marketingPauseByIdRequest);

                List<String> skuIds = marketing.getMarketingScopeList().stream().map(MarketingScopeVO::getScopeId).collect(Collectors.toList());

                // 更新es
                esGoodsModifyInventoryService.modifyInventory(skuIds);

                operateLogMQUtil.convertAndSend("营销","暂停促销活动",
                        EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name())+"-"+ getMarketingName(marketingId));
                return BaseResponse.SUCCESSFUL();
            }
        }else{
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
    }

    /**
     * 开始营销活动
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "开始营销活动")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/start/{marketingId}", method = RequestMethod.PUT)
    @Transactional
    @Deprecated
    public BaseResponse startMarketingId(@PathVariable("marketingId")Long marketingId){
        //进行中的营销活动，才能暂停
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        if(marketing != null){
            //如果现在时间在活动开始之前或者活动已经结束，或者当前状态是开始状态
            if(LocalDateTime.now().isBefore(marketing.getBeginTime()) || LocalDateTime.now().isAfter(marketing.getEndTime())
                    || marketing.getIsPause() == BoolFlag.NO){
                throw new SbcRuntimeException(MarketingErrorCode.MARKETING_CANNOT_START);
            }else{
                MarketingStartByIdRequest marketingStartByIdRequest = new MarketingStartByIdRequest();
                marketingStartByIdRequest.setMarketingId(marketingId);
                marketingProvider.startById(marketingStartByIdRequest);
                List<String> skuIds = marketing.getMarketingScopeList().stream().map(MarketingScopeVO::getScopeId).collect(Collectors.toList());
                // 更新es

                esGoodsModifyInventoryService.modifyInventory(skuIds);

                operateLogMQUtil.convertAndSend("营销","开启促销活动",
                        "开启促销活动："+ getMarketingName(marketingId));
                return BaseResponse.SUCCESSFUL();
            }
        }else{
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
    }


    @ApiOperation(value = "增加参与活动商品")
    @PostMapping(value = "/addActivityGoods")
    @Transactional
    public BaseResponse addActivityGoods(@RequestBody AddActivitGoodsRequest request){

        StringBuilder goodsInfoAppendStr = new StringBuilder();
        String goodsInfo = "";
        List<String> addList = new ArrayList<>();
        List<String> existList = new ArrayList<>();
        /**
         * 过滤为-1的情况
         */
        List<ActivitGoodsRequest> addActivitGoodsRequests = request.getAddActivitGoodsRequest();
        if(CollectionUtils.isNotEmpty(addActivitGoodsRequests)){
            MarketingScopeByMarketingIdRequest marketingScopeByMarketingIdRequest = new MarketingScopeByMarketingIdRequest();
            marketingScopeByMarketingIdRequest.setMarketingId(request.getMarketingId());
            List<MarketingScopeVO> marketingScopeVOList = marketingScopeQueryProvider.listByMarketingId(marketingScopeByMarketingIdRequest).getContext().getMarketingScopeVOList();
            for(MarketingScopeVO marketingScopeVO : marketingScopeVOList){
                existList.add(marketingScopeVO.getScopeId());
            }
            log.info("existList的内容：{}",existList);
            for (ActivitGoodsRequest activitGoodsRequest : addActivitGoodsRequests){
                Long purchaseNum = activitGoodsRequest.getPurchaseNum();
                if(null == purchaseNum || purchaseNum < 0){
                    activitGoodsRequest.setPurchaseNum(null);
                }
                addList.add(activitGoodsRequest.getScopeId());
            }
            log.info("addList的内容：{}",addList);

            List<String> logList = ListUtil.subList(addList,existList);
            log.info("logList的内容：{}",logList);
            for(String scopeId : logList){
                //根据商品与营销关联表获取商品名称和ERP编码
                GoodsInfoByIdRequest goodsInfoByIdRequest = new GoodsInfoByIdRequest();
                goodsInfoByIdRequest.setGoodsInfoId(scopeId);
                Optional<List<GoodsInfoVO>> goodsInfoVOOptional = Optional.ofNullable(goodsInfoQueryProvider.getById(goodsInfoByIdRequest))
                        .map(BaseResponse::getContext)
                        .map(GoodsInfoByIdResponse::getGoodsInfos);
                if(goodsInfoVOOptional.isPresent()){
                    List<GoodsInfoVO> goodsInfoVOList = goodsInfoVOOptional.get();
                    goodsInfoAppendStr.append(goodsInfoVOList.get(0).getGoodsInfoName()).append("-").append(goodsInfoVOList.get(0).getErpGoodsInfoNo()).append(",");
                }
            }
            log.info("goodsInfoAppendStr的内容：{}",goodsInfoAppendStr);
            if(goodsInfoAppendStr.length() > 0){
                goodsInfo = goodsInfoAppendStr.substring(0,goodsInfoAppendStr.length()-1);
            }
        }




        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        if(Objects.nonNull(marketing)){
            //活动正在进行中
            if(LocalDateTime.now().isAfter(marketing.getBeginTime()) && LocalDateTime.now().isBefore(marketing.getEndTime())){
                BaseResponse<List<String>> listBaseResponse = marketingQueryProvider.addActivityGoods(request);
                if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
//                    throw new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT, new Object[]{listBaseResponse.getContext().size(),listBaseResponse.getContext()});
                    // 需要把重复的商品id返回
                    return BaseResponse.success(listBaseResponse.getContext());
                    
                }

                operateLogMQUtil.convertAndSend("营销","增加参与活动商品",
                        EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name())+
                        "-"+ ( Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " ")+
                        "-"+ goodsInfo);
            }else{
                return BaseResponse.error("活动未开始不可使用");
            }
        }else{
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "增加参与活动赠品（仅满赠使用）")
    @PostMapping(value = "/addActivityGiveGoods")
    @Transactional
    public BaseResponse addActivityGiveGoods(@RequestBody AddActivityGiveGoodsRequest request){
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(request.getMarketingId());
        MarketingVO marketing = marketingQueryProvider.getById(marketingGetByIdRequest).getContext().getMarketingVO();
        if(Objects.nonNull(marketing)){
            if(!MarketingType.GIFT.equals(marketing.getMarketingType())){
                return BaseResponse.error("非满赠活动！");
            }
            //活动正在进行中
            if(LocalDateTime.now().isAfter(marketing.getBeginTime()) && LocalDateTime.now().isBefore(marketing.getEndTime())){
                BaseResponse<List<String>> listBaseResponse = marketingQueryProvider.addActivityGiveGoods(request);
                if(CollectionUtils.isNotEmpty(listBaseResponse.getContext())){
                    throw new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT, new Object[]{listBaseResponse.getContext().size(),listBaseResponse.getContext()});
                }

                StringBuilder goodsInfoAppendStr = new StringBuilder();
                String goodsInfo = "";
                List<String> existList = new ArrayList<>();
                List<String> addList = new ArrayList<>();
                List<ActivityGiveGoodsRequest> addActivityGoodsRequest = request.getAddActivitGoodsRequest();
                if(CollectionUtils.isNotEmpty(addActivityGoodsRequest)){
                    for (ActivityGiveGoodsRequest activityGoodsRequest : addActivityGoodsRequest){
                         addList.add(activityGoodsRequest.getProductId());
                    }
                    MarketingScopeByMarketingIdRequest marketingScopeIdRequest = new MarketingScopeByMarketingIdRequest();
                    marketingScopeIdRequest.setMarketingId(request.getMarketingId());
                    List<MarketingScopeVO> marketingScopeVOList = marketingScopeQueryProvider.listByMarketingId(marketingScopeIdRequest).getContext().getMarketingScopeVOList();
                    for(MarketingScopeVO marketingScopeVO : marketingScopeVOList){
                        existList.add(marketingScopeVO.getScopeId());
                    }

                    List<String> logList = ListUtil.subList(addList,existList);

                    for(String giftId : logList){
                        //根据商品ID获取商品名称和ERP编码和添加时间
                        GoodsInfoByIdRequest goodsInfoByIdRequest = new GoodsInfoByIdRequest();
                        goodsInfoByIdRequest.setGoodsInfoId(giftId);
                        Optional<List<GoodsInfoVO>> goodsInfoVOOptional = Optional.ofNullable(goodsInfoQueryProvider.getById(goodsInfoByIdRequest))
                                .map(BaseResponse::getContext)
                                .map(GoodsInfoByIdResponse::getGoodsInfos);
                        if(goodsInfoVOOptional.isPresent()){
                            List<GoodsInfoVO> goodsInfoVOList = goodsInfoVOOptional.get();
                            goodsInfoAppendStr.append(goodsInfoVOList.get(0).getGoodsInfoName()).append("-")
                                    .append(goodsInfoVOList.get(0).getErpGoodsInfoNo()).append("-").append(DateUtil.format(LocalDateTime.now(),DateUtil.FMT_TIME_1)).append(",");
                        }
                    }

                    if(goodsInfoAppendStr.length() > 0){
                        goodsInfo = goodsInfoAppendStr.substring(0,goodsInfoAppendStr.length()-1);
                    }
                }

                operateLogMQUtil.convertAndSend("营销","增加参与活动赠品（仅满赠使用）",EnumTranslateUtil.getFieldAnnotation(marketing.getSubType()).get(marketing.getSubType().name())+
                        "-"+ ( Objects.nonNull(marketing.getMarketingName()) ? marketing.getMarketingName() : " ")+
                        "-"+ goodsInfo);
            }else{
                return BaseResponse.error("活动未开始不可使用");
            }
        }else{
            throw new SbcRuntimeException(MarketingErrorCode.NOT_EXIST);
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据营销Id获取关联商品信息
     * @param marketingId
     * @return
     */
    @ApiOperation(value = "根据营销Id获取关联商品信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "marketingId", value = "营销Id", required = true)
    @RequestMapping(value = "/goods/{marketingId}", method = RequestMethod.GET)
    public BaseResponse<GoodsInfoResponse> getGoodsByMarketingId(@PathVariable("marketingId")Long marketingId){
        MarketingGetByIdRequest marketingGetByIdRequest = new MarketingGetByIdRequest();
        marketingGetByIdRequest.setMarketingId(marketingId);
        GoodsInfoResponseVO response = marketingQueryProvider.getGoodsById(marketingGetByIdRequest)
                .getContext().getGoodsInfoResponseVO();
        if(Objects.nonNull(response.getGoodsInfoPage()) && CollectionUtils.isNotEmpty(response.getGoodsInfoPage().getContent())) {
            GoodsIntervalPriceResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(response.getGoodsInfoPage().getContent());
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            response.setGoodsInfoPage(new MicroServicePage<>(priceResponse.getGoodsInfoVOList()));
        }
        return BaseResponse.success(KsBeanUtil.convert(response, GoodsInfoResponse.class));
    }

    /**
     * 根据营销Id获取正进行中的营销Id
     * @param request 参数
     * @return
     */
    @ApiOperation(value = "根据营销Id获取正进行中的营销Id")
    @RequestMapping(value = "/isStart", method = RequestMethod.POST)
    public BaseResponse<List<String>> getGoodsByMarketingId(@RequestBody @Valid MarketingQueryByIdsRequest request){
        return BaseResponse.success(marketingQueryProvider.queryStartingByIds(request).getContext().getMarketingIdList());
    }

    /**
     *  公共方法获取促销活动名称
     * @param marketId
     * @return
     */
    private String getMarketingName(long marketId){
        MarketingGetByIdRequest request = new MarketingGetByIdRequest();
        request.setMarketingId(marketId);
        MarketingGetByIdResponse marketing = marketingQueryProvider.getById(request).getContext();
        return Objects.nonNull(marketing) ? marketing.getMarketingVO().getMarketingName() : " ";
    }

    /**
     *  公共方法获取促销活动实体
     * @param marketId
     * @return
     */
    private MarketingVO getMarketingVo(long marketId){
        MarketingGetByIdRequest request = new MarketingGetByIdRequest();
        request.setMarketingId(marketId);
        MarketingGetByIdResponse marketing = marketingQueryProvider.getById(request).getContext();
        return Objects.nonNull(marketing) ? marketing.getMarketingVO() : defaultMarketing();
    }

    /**
     * 返回默认促销活动实体
     * @return
     */
    private MarketingVO defaultMarketing(){
        MarketingVO marketingVO = new MarketingVO();
        marketingVO.setMarketingName("");
        marketingVO.setSubType(MarketingSubType.REDUCTION_FULL_AMOUNT);
        return marketingVO;
    }

}
