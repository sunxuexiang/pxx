package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.TxTransaction;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomListRequest;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsHandlerProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitSaveProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsExistsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.goodsattributekey.GoodsAttributeKeyQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitQueryRequest;
import com.wanmi.sbc.goods.api.request.info.DistributionGoodsChangeRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.goodsattributekey.GoodsAttributeKeyListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByGoodsIdresponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.service.GoodsExcelService;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "GoodsController", description = "商品服务 Api")
@RestController
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;


    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private GoodsExcelService goodsExcelService;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    GoodsAresProvider goodsAresProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private FreightTemplateGoodsQueryProvider freightTemplateGoodsQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private LiveGoodsQueryProvider liveGoodsQueryProvider;

    @Autowired
    private LiveRoomQueryProvider liveRoomQueryProvider;

    @Autowired
    private EsGoodsModifyInventoryService esGoodsModifyInventoryService;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;
    @Autowired
    private GoodsAttributeKeyQueryProvider goodsAttributeKeyQueryProvider;
    @Autowired
    private MerchantConfigGoodsQueryProvider merchantConfigGoodsQueryProvider;

    @Autowired
    private GoodsUnitSaveProvider goodsUnitSaveProvider;

    @Autowired
    private GoodsUnitQueryProvider goodsUnitQueryProvider;

    @Autowired
    private GoodsHandlerProvider goodsHandlerProvider;
    /**
     * 新增商品
     */
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/spu", method = RequestMethod.POST)
    public BaseResponse<String> add(@RequestBody GoodsAddRequest request) {
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null || CollectionUtils.isEmpty(request.getGoodsInfos()) || StringUtils
                .isEmpty(String.valueOf(fId))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();

        if (companyInfo == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //判断运费模板是否存在
        // 商家费用模板弃用 采取平台模板  无单品运费模板  所以这里目前被注释
//        freightTemplateGoodsQueryProvider.existsById(
//                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());

        request.getGoods().setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.getGoods().setCompanyType(companyInfo.getCompanyType());
        request.getGoods().setStoreId(commonUtil.getStoreId());
        request.getGoods().setSupplierName(companyInfo.getSupplierName());
        BaseResponse<GoodsAddResponse> baseResponse = goodsProvider.add(request);
        GoodsAddResponse response = baseResponse.getContext();
        String goodsId = Optional.ofNullable(response)
                .map(GoodsAddResponse::getResult)
                .orElse(null);
        //ares埋点-商品-后台添加商品sku
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSpu", new String[]{goodsId}));

        if(Objects.nonNull(goodsId)){
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).build());
        }else {
            
            operateLogMQUtil.convertAndSend("商品", "发布错误",
                    "商品添加为空" + request.getGoods().getGoodsNo());
        }
        operateLogMQUtil.convertAndSend("商品", "直接发布",
                "直接发布：SPU编码" + request.getGoods().getGoodsNo());
        return BaseResponse.success(goodsId);
    }

    /**
     * 新增商品
     */
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/copySpu", method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<List<String>> copySpu(@RequestBody GoodsCopyByStoreRequest request) {
        BaseResponse<List<String>> baseResponse = goodsHandlerProvider.copyGoodsByStore(request);
        final List<String> goodsIds = baseResponse.getContext();
        if (CollectionUtils.isEmpty(goodsIds)) {
            log.error("copySpu error");
            return BaseResponse.success(new ArrayList<>());
        }
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(goodsIds).build());
        //推送到erp
        goodsIds.forEach(goodsId -> {
            GoodsAddRequest goodsAddRequest = new GoodsAddRequest();
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setGoodsId(goodsId);
            goodsAddRequest.setGoods(goodsDTO);
            try {
                goodsProvider.sysnErp(goodsAddRequest);
            } catch (Exception e) {
                log.error("copySpu sysnErp error,goodsId:{}", goodsId, e);
            }
        });
        return BaseResponse.success(goodsIds);
    }

    /**
     * 同时新增商品基本和商品设价
     */
    @ApiOperation(value = "同时新增商品基本和商品设价")
    @RequestMapping(value = "/spu/price", method = RequestMethod.POST)
    public BaseResponse<String> spuDetail(@RequestBody GoodsAddAllRequest request) {
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null || CollectionUtils.isEmpty(request.getGoodsInfos()) || StringUtils
                .isEmpty(String.valueOf(fId))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();

        if (companyInfo == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //判断运费模板是否存在
        freightTemplateGoodsQueryProvider.existsById(
                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());
        request.getGoods().setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.getGoods().setCompanyType(companyInfo.getCompanyType());
        request.getGoods().setStoreId(commonUtil.getStoreId());
        request.getGoods().setSupplierName(companyInfo.getSupplierName());

        BaseResponse<GoodsAddAllResponse> baseResponse = goodsProvider.addAll(request);
        GoodsAddAllResponse response = baseResponse.getContext();
        String goodsId = Optional.ofNullable(response).map(GoodsAddAllResponse::getGoodsId).orElse(null);
        //ares埋点-商品-后台添加商品sku
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSpu", new String[]{goodsId}));
        if (goodsId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }

        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).build());

        operateLogMQUtil.convertAndSend("商品", "直接发布",
                "直接发布：SPU编码" + request.getGoods().getGoodsNo());
        return BaseResponse.success(goodsId);
    }

    /**
     * 编辑商品
     */
    @ApiOperation(value = "编辑商品")
    @RequestMapping(value = "/spu", method = RequestMethod.PUT)
    @TxTransaction
    public BaseResponse edit(@RequestBody GoodsModifyRequest request) {
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null || CollectionUtils.isEmpty(request.getGoodsInfos()) || request
                .getGoods().getGoodsId() == null || StringUtils.isEmpty(String.valueOf(fId)
        )) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //判断运费模板是否存在
        freightTemplateGoodsQueryProvider.existsById(
                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());


        if (request.getGoods().getAddedFlag() == AddedFlag.NO.toValue()) {
            //保持下架状态
            request.getGoodsInfos().stream().forEach(goodsInfoVO -> {
                goodsInfoVO.setAddedFlag(AddedFlag.NO.toValue());
            });
        }else {
            request.getGoodsInfos().stream().forEach(goodsInfoVO -> {
                goodsInfoVO.setAddedFlag(AddedFlag.YES.toValue());
            });
        }
        request.getGoods().setCompanyInfoId(commonUtil.getCompanyInfoId());
        if (commonUtil.getCompanyType()==1){
            request.getGoods().setCompanyType(CompanyType.SUPPLIER);
        }


        request.getGoods().setDelFlag(DeleteFlag.NO);
        request.getGoods().setStoreId(commonUtil.getStoreId());
        Map<String, Object> returnMap = goodsProvider.modify(request).getContext().getReturnMap();
        if (CollectionUtils.isNotEmpty((List<String>) returnMap.get("delStoreGoodsInfoIds"))) {
            esGoodsInfoElasticService.delete((List<String>) returnMap.get("delStoreGoodsInfoIds"));
        }
        List<String> skuIds = request.getGoodsInfos().stream().
                filter(goodsInfoVO -> StringUtils.isNotEmpty(goodsInfoVO.getGoodsInfoId())).
                map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        esGoodsInfoElasticService.deleteByGoods(Collections.singletonList(request.getGoods().getGoodsId()));
        esGoodsModifyInventoryService.modifyInventory(skuIds,request.getGoods().getGoodsId());
        if (request.getGoods().getGoodsId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        //初始化当前这个产品
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(request.getGoods().getGoodsId()).build());
        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("商品", "编辑商品",
                "编辑商品：SPU编码" + request.getGoods().getGoodsNo());
//        if (isRemove) {
//            throw new SbcRuntimeException("K-030507");
//        }
        //推送到erp
        GoodsAddRequest goodsAddRequest =new GoodsAddRequest();
        GoodsDTO goodsDTO=new GoodsDTO();
        goodsDTO.setGoodsId(request.getGoods().getGoodsId());
        goodsAddRequest.setGoods(goodsDTO);
        try {
            goodsProvider.sysnErp(goodsAddRequest);
        }catch (Exception e){
            operateLogMQUtil.convertAndSend("编辑商品","编辑商品","编辑商品异常"+ JSONObject.toJSON(request.getGoods().getGoodsId()));
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "批量修改商品类目(整批)")
    @RequestMapping(value = "/spu/cate/batch", method = RequestMethod.PUT)
    public BaseResponse batchModifyCate(@RequestBody GoodsBatchModifyCateRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds()) || Objects.isNull(request.getCateId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsProvider.batchModifyCate(request);
        // 刷ES
        esGoodsInfoElasticService.deleteByGoods(request.getGoodsIds());
        request.getGoodsIds().forEach(goodsId ->{
            BaseResponse<GoodsInfoByGoodsIdresponse> response = goodsInfoQueryProvider.getBygoodsId(DistributionGoodsChangeRequest.builder().goodsId(goodsId).build());
            List<String> skuIds = response.getContext().getGoodsInfoVOList()
                    .stream()
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            esGoodsModifyInventoryService.modifyInventory(skuIds, goodsId);
        });

        operateLogMQUtil.convertAndSend("商品", "批量修改整批类目", "批量修改整批类目: 商品ID:"
                + request.getGoodsIds() + " 类目ID：" + request.getCateId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存商品价格
     */
    @ApiOperation(value = "保存商品价格")
    @RequestMapping(value = "/spu/price", method = RequestMethod.PUT)
    public BaseResponse editSpuPrice(@RequestBody GoodsModifyAllRequest request) {
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null && request.getGoods().getGoodsId() == null || StringUtils.isEmpty
                (String.valueOf(fId))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //判断运费模板是否存在
        freightTemplateGoodsQueryProvider.existsById(
                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());

        goodsProvider.modifyAll(request);
        if (request.getGoods().getGoodsId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        esGoodsInfoElasticService.deleteByGoods(Collections.singletonList(request.getGoods().getGoodsId()));
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(request.getGoods().getGoodsId()).build());

        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("商品", "设价",
                "设价：SPU编码" + request.getGoods().getGoodsNo());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商品详情信息
     *
     * @param goodsId 商品编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsId", value = "商品Id", required = true)
    @RequestMapping(value = "/spu/{goodsId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> info(@PathVariable String goodsId) {
        GoodsViewByIdRequest request = new GoodsViewByIdRequest();
        request.setGoodsId(goodsId);
        GoodsViewByIdResponse response = goodsQueryProvider.getViewById(request).getContext();
        //获取商品店铺分类

        if (osUtil.isS2b()) {
            StoreCateListByGoodsRequest storeCateListByGoodsRequest = new StoreCateListByGoodsRequest(Collections.singletonList(goodsId));
            BaseResponse<StoreCateListByGoodsResponse> baseResponse = storeCateQueryProvider.listByGoods(storeCateListByGoodsRequest);
            StoreCateListByGoodsResponse storeCateListByGoodsResponse = baseResponse.getContext();

            if (Objects.nonNull(storeCateListByGoodsResponse)) {
                List<StoreCateGoodsRelaVO> storeCateGoodsRelaVOList = storeCateListByGoodsResponse.getStoreCateGoodsRelaVOList();
                List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().build()).getContext().getWareHouseVOList();
                response.getGoods().setStoreCateIds(storeCateGoodsRelaVOList.stream()
                        .filter(rela -> rela.getStoreCateId() != null)
                        .map(StoreCateGoodsRelaVO::getStoreCateId)
                        .collect(Collectors.toList()));
                response.getGoods().setWareName(wareHouseVOList.stream().filter(wareHouseVO -> wareHouseVO.getWareId().equals(response.getGoods().getWareId())).findFirst().orElse(new WareHouseVO()).getWareName());
            }
        }

        // 获取直播商品
        Map<String, Long> liveMapLong = new HashMap<>();
        List<String> goodsInfoIds =
                response.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsInfoIds)) {
            // 根据商品id,查询直播商品的id
            List<LiveGoodsVO> liveGoodsVOList = liveGoodsQueryProvider.getRoomInfoByGoodsInfoId(goodsInfoIds).getContext();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liveGoodsVOList)) {
                liveMapLong = liveGoodsVOList.stream().filter(entity -> {
                    return entity.getGoodsId() != null;
                }).collect(Collectors.toMap(LiveGoodsVO::getGoodsInfoId,
                        LiveGoodsVO::getGoodsId));
            }

        }

        if (Objects.nonNull(liveMapLong)) {
            final Map<String, Long> liveMap = liveMapLong;

            // 根据直播房价的id,查询直播信息
            LiveRoomListRequest liveRoomListReq = new LiveRoomListRequest();
            liveRoomListReq.setLiveStatus(LiveRoomStatus.ZERO.toValue());
            liveRoomListReq.setDelFlag(DeleteFlag.NO);
            List<LiveRoomVO> liveRoomVOList = liveRoomQueryProvider.list(liveRoomListReq).getContext().getLiveRoomVOList();
            List<Long> liveRoomIdList = liveRoomVOList.stream().map(LiveRoomVO::getRoomId).collect(Collectors.toList());
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(liveRoomVOList)) {
                response.getGoodsInfos().stream().forEach(item -> {
                    Long wxGoodsId = liveMap.get(item.getGoodsInfoId());
                    if (wxGoodsId != null) {
                        if (redisService.hasKey(RedisKeyConstant.GOODS_LIVE_INFO + wxGoodsId)) {
                            List<GoodsLiveRoomVO> goodsLiveRoomVOList = redisService.getList(RedisKeyConstant.GOODS_LIVE_INFO + wxGoodsId, GoodsLiveRoomVO.class);
                            if (CollectionUtils.isNotEmpty(goodsLiveRoomVOList) && goodsLiveRoomVOList.size() > 0) {
                                for (GoodsLiveRoomVO liveRoomVO : goodsLiveRoomVOList) {
                                    if (liveRoomIdList.contains(liveRoomVO.getRoomId())) {
                                        item.setLiveEndTime(liveRoomVO.getLiveEndTime());
                                        item.setLiveStartTime(liveRoomVO.getLiveStartTime());
                                        item.setRoomId(liveRoomVO.getRoomId());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }

        response.getGoodsInfos().forEach(goodsInfoVO -> {
            //加入商品属性（sku）
            BaseResponse<GoodsAttributeKeyListResponse> skuList = goodsAttributeKeyQueryProvider.getList(GoodsAttributeKeyQueryRequest.builder().goodsInfoId(goodsInfoVO.getGoodsInfoId()).build());
            if (skuList.getContext()!=null && CollectionUtils.isNotEmpty(skuList.getContext().getAttributeKeyVOS())){
                List<GoodsAttributeKeyVO> attributeKeyVOList = skuList.getContext().getAttributeKeyVOS();
                attributeKeyVOList.forEach(goodsAttributeKeyVO -> {
                    StringBuffer attributeNameDesc = new StringBuffer();
                    if(Objects.isNull(goodsInfoVO.getMarketPrice())||Objects.isNull(goodsInfoVO.getAddStep()) ||
                    BigDecimal.ZERO.compareTo(goodsInfoVO.getMarketPrice())>=0 || BigDecimal.ZERO.compareTo(goodsInfoVO.getAddStep())>=0){
                        attributeNameDesc.append("1").append(goodsInfoVO.getGoodsInfoUnit());
                        goodsAttributeKeyVO.setAttributeNameDesc(attributeNameDesc.toString());
                    }else {
                        BigDecimal unitPrice = goodsInfoVO.getMarketPrice().divide(goodsInfoVO.getAddStep(),2, RoundingMode.HALF_UP);
                        attributeNameDesc.append("1").append(goodsInfoVO.getGoodsInfoUnit()).append("(").append(goodsInfoVO.getAddStep().intValue()).append("x")
                                .append(unitPrice).append("元/").append(goodsInfoVO.getDevanningUnit()).append(")");
                        goodsAttributeKeyVO.setAttributeNameDesc(attributeNameDesc.toString());
                    }
                });

                goodsInfoVO.setGoodsAttributeKeys(attributeKeyVOList);
            }else {
                goodsInfoVO.setGoodsAttributeKeys(null);
            }
        });
        //erp 封装前端需要的唯一值
        response.getGoods().setErpNo(response.getGoodsInfos().get(Constants.no).getErpGoodsInfoNo());
        response.getGoods().setIsScatteredQuantitative(response.getGoodsInfos().get(Constants.no).getIsScatteredQuantitative());
        response.getGoods().setShelflife(response.getGoodsInfos().get(Constants.no).getShelflife());
        response.getGoods().setDateUnit(response.getGoodsInfos().get(Constants.no).getDateUnit());
        return BaseResponse.success(response);
    }

    /**
     * 删除商品
     */
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/spu", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody GoodsDeleteByIdsRequest request) {

        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "删除商品",
                    "删除商品：SPU编码" + response.getGoodsNo());

        } else {
            operateLogMQUtil.convertAndSend("商品", "批量删除",
                    "批量删除");
        }

        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsProvider.deleteByIds(request);
        //关联供应商商品下架
//        GoodsListByIdsResponse goodsListByIdsResponse = goodsQueryProvider.listByProviderGoodsId(GoodsListByIdsRequest.builder().goodsIds(request.getGoodsIds()).build()).getContext();
//        if (goodsListByIdsResponse != null && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())) {
//            List<String> providerOfGoodsIds = goodsListByIdsResponse.getGoodsVOList().stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
//            GoodsInfoListByConditionResponse goodsInfoListByConditionResponse = goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder().goodsIds(providerOfGoodsIds).build()).getContext();
//            if (goodsInfoListByConditionResponse != null && CollectionUtils.isNotEmpty(goodsInfoListByConditionResponse.getGoodsInfos())) {
//                List<String> providerOfGoodInfoIds = goodsInfoListByConditionResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
//                //更新上下架状态
//                esGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), providerOfGoodsIds, providerOfGoodInfoIds, null);
//            }
//        }
        //更新ES
        esGoodsInfoElasticService.deleteByGoods(request.getGoodsIds());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量上架商品
     */
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/spu/sale", method = RequestMethod.PUT)
    public BaseResponse onSale(@RequestBody GoodsModifyAddedStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setAddedFlag(AddedFlag.YES.toValue());
        checkGoodsTag(request.getGoodsIds());
        //查询上架商品中是否包含供应商商品(下架状态的)，包含则返回
//        GoodsListByIdsRequest goodsListByIdsRequest = new GoodsListByIdsRequest();
//        goodsListByIdsRequest.setGoodsIds(request.getGoodsIds());
//        List<GoodsVO> goodsVOList = goodsQueryProvider.listByIds(goodsListByIdsRequest).getContext().getGoodsVOList();

        //TODO 吕衡 上架逻辑检索库存需要从新的库存仓库中获取
//        //单独上架时，校验Erp商品的库存
//        if (request.getGoodsIds().size() == 1) {
//            List<GoodsVO> erpGoods = goodsVOList.stream().filter(item -> isErpGoods(item.getGoodsName())).collect(Collectors.toList());
//            //校验ERP商品的库存，是否能够上架
//            if (CollectionUtils.isNotEmpty(erpGoods)) {
//                GoodsVO goodsVO = erpGoods.get(0);
//                if (goodsVO.getStock() == 0) {
//                    throw new SbcRuntimeException("K-180004");
//                }
//            }
//        }


//        List<String> providerGoodsId = new ArrayList<>();
//        goodsVOList.forEach(goodsVO -> {
//            if(StringUtils.isNotEmpty(goodsVO.getProviderGoodsId())){
//                providerGoodsId.add(goodsVO.getProviderGoodsId());
//            }
//        });
//        if(CollectionUtils.isNotEmpty(providerGoodsId)){
//            goodsListByIdsRequest.setGoodsIds(providerGoodsId);
//            List<GoodsVO> providerGoodsVOList = goodsQueryProvider.listByIds(goodsListByIdsRequest).getContext().getGoodsVOList();
//
//
//            for (GoodsVO providerGoods: providerGoodsVOList){
//                if(providerGoods.getDelFlag().equals(DeleteFlag.YES)){
//                    throw new SbcRuntimeException("K-030507","包含供应商删除商品");
//                    //上架的商品中存在  已经下架的供应商商品
//                }else if(providerGoods.getAddedFlag().equals(AddedFlag.NO.toValue())){
//                    throw new SbcRuntimeException("K-030504");
//                //上架的商品中存在  部分商家的供应商商品
//                }else if(providerGoods.getAddedFlag().equals(AddedFlag.PART.toValue())){
//                    throw new SbcRuntimeException("K-030505");
//                }
//            }
//        }


        goodsProvider.modifyAddedStatus(request);
        //更新ES

        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.YES.toValue(), request.getGoodsIds(), null, null);

        //ares埋点-商品-后台批量修改商品spu的所有sku上下架状态

        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpuUp", new Object[]{AddedFlag.YES.toValue(), request.getGoodsIds()}));

        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "上架",
                    "上架：SPU编码" + response.getGoodsNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量上架", "批量上架");
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量下架商品
     */
    @ApiOperation(value = "批量下架商品")
    @RequestMapping(value = "/spu/sale", method = RequestMethod.DELETE)
    public BaseResponse offSale(@RequestBody GoodsModifyAddedStatusRequest request) {
        List<String> goodsIds = request.getGoodsIds();
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setAddedFlag(AddedFlag.NO.toValue());
        //如果下架商品是供应商商品，商家商品同步下架
//        GoodsListByIdsRequest goodsListByIdsRequest = new GoodsListByIdsRequest();
//        goodsListByIdsRequest.setGoodsIds(request.getGoodsIds());
//        List<GoodsVO> goodsVOList = goodsQueryProvider.listByCondition(
//                GoodsByConditionRequest.builder().providerGoodsIds(goodsIds).build()).getContext().getGoodsVOList();
//        if(CollectionUtils.isNotEmpty(goodsVOList)){
//            goodsVOList.forEach(s->{goodsIds.add(s.getGoodsId());});
//        }


        request.setGoodsIds(goodsIds);

        goodsProvider.modifyAddedStatus(request);
        //更新ES
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), request.getGoodsIds(), null,null);

        //ares埋点-商品-后台批量修改商品spu的所有sku上下架状态
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpuUp",new Object[]{AddedFlag.NO.toValue(),request.getGoodsIds()}));

        if(1 == request.getGoodsIds().size()){
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "下架",
                    "下架：SPU编码"+response.getGoodsNo());
        }else {
            operateLogMQUtil.convertAndSend("商品", "批量下架", "批量下架");
        }
        return BaseResponse.SUCCESSFUL();
    }

    private void checkGoodsTag(List<String> goodsIds) {
        final GoodsTagRelReRequest goodsTagRelReRequest = new GoodsTagRelReRequest();
        goodsTagRelReRequest.setGoodsIds(goodsIds);
        goodsTagRelReRequest.setTagId(1L);
        final BaseResponse<List<GoodsTagRelResponse>> goodsTagRel = goodsQueryProvider.listGoodsTagRel(goodsTagRelReRequest);
        if (null != goodsTagRel && CollectionUtils.isNotEmpty(goodsTagRel.getContext())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "存在不可上架商品，请联系平台运营");
        }
    }

    /**
     * 批量编辑运费模板
     */
    @ApiOperation(value = "批量编辑运费模板")
    @RequestMapping(value = "/spu/freight", method = RequestMethod.PUT)
    public BaseResponse setFeight(@RequestBody GoodsModifyFreightTempRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (Objects.isNull(request.getFreightTempId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        Long fId = request.getFreightTempId();
        //判断运费模板是否存在
        freightTemplateGoodsQueryProvider.existsById(
                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());
        goodsProvider.modifyFreightTemp(request);

        FreightTemplateGoodsByIdResponse templateGoods = freightTemplateGoodsQueryProvider.getById(
                FreightTemplateGoodsByIdRequest.builder().freightTempId(fId).build()).getContext();

        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "更换运费模板",
                    "更换运费模板：" + response.getGoodsName()
                            + " 模板名称改为" + templateGoods.getFreightTempName());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量更换运费模板",
                    "批量更换运费模板：模板名称改为" + templateGoods.getFreightTempName());
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("商品", "上传文件","上传文件" );
        return BaseResponse.success(goodsExcelService.upload(uploadFile, commonUtil.getOperatorId()));
    }

    /**
     * 下载错误文档
     */
    @ApiOperation(value = "下载错误文档")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "ext", value = "后缀", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "String",
                    name = "decrypted", value = "解密", required = true)
    })
    @RequestMapping(value = "/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsExcelService.downErrExcel(commonUtil.getOperatorId(), ext);
        operateLogMQUtil.convertAndSend("商品", "下载错误文档","操作成功" );
    }

    /**
     * 编辑商品排序序号
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑商品排序序号")
    @RequestMapping(value = "/spu/modifySeqNum", method = RequestMethod.PUT)
    public BaseResponse modifyGoodsSeqNum(@RequestBody GoodsModifySeqNumRequest request) {
        if (StringUtils.isBlank(request.getGoodsId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (Objects.isNull(request.getGoodsSeqNum())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        if (request.getGoodsSeqNum() != null && request.getGoodsSeqNum() == 0) {
            request.setGoodsSeqNum(null);
        }
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(request.getGoodsId());
        GoodsByIdResponse oldGoods = goodsQueryProvider.getById(goodsByIdRequest).getContext();

        goodsProvider.modifyGoodsSeqNum(request);

        GoodsVO goodsVO = new GoodsVO();
        KsBeanUtil.copyPropertiesThird(request, goodsVO);
        esGoodsInfoElasticService.modifyGoodsSeqNum(goodsVO);

        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("商品", "编辑排序序号",
                "SPU编码:" + oldGoods.getGoodsNo() +
                        "，操作前排序:" + oldGoods.getGoodsSeqNum() +
                        "，操作后排序:" + request.getGoodsSeqNum() +
                        "，操作时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        "，操作人:" + commonUtil.getOperator().getName());

        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 添加商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "添加商品单位")
    @RequestMapping(value = "/base/goodsUnit/add", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> add(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        pageRequest.setCreatePerson(commonUtil.getOperator().getName());
        pageRequest.setCompanyInfoId(-1L);
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.add(pageRequest);
        operateLogMQUtil.convertAndSend("商品", "添加商品单位", "操作成功：商品单位" + (Objects.nonNull(pageRequest) ? pageRequest.getUnit() : ""));
        return pageResponse;
    }

    @ApiOperation(value = "查询商品单位")
    @RequestMapping(value = "/base/goodsUnit/page", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitPageResponse> page(@RequestBody StoreGoodsUnitQueryRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        pageRequest.setCompanyInfoId(-1L);
        BaseResponse<GoodsUnitPageResponse> pageResponse = goodsUnitQueryProvider.page(pageRequest);
        return pageResponse;
    }

    /**
     * 修改商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "修改商品单位")
    @RequestMapping(value = "base/goodsUnit/edit", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> updateUnitById(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        pageRequest.setUpdatePerson(commonUtil.getOperator().getName());
        pageRequest.setCompanyInfoId(-1L);
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.updateUnit(pageRequest);
        operateLogMQUtil.convertAndSend("商品","修改商品单位","操作成功：单位编号" + (Objects.nonNull(pageRequest) ? pageRequest.getStoreGoodsUnitId() : ""));
        return pageResponse;
    }

    /**
     * 删除商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "删除商品单位")
    @RequestMapping(value = "base/goodsUnit/delete", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> deleteById(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        pageRequest.setCompanyInfoId(-1L);
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.deleteById(pageRequest);
        operateLogMQUtil.convertAndSend("商品","删除商品单位","操作成功：单位编号" + (Objects.nonNull(pageRequest) ? pageRequest.getStoreGoodsUnitId() : ""));
        return pageResponse;
    }

}