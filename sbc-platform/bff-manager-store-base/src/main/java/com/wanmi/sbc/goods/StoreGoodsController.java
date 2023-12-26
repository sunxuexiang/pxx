package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.company.CompanyIntoPlatformQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.company.CompanyMallReturnGoodsAddressRequest;
import com.wanmi.sbc.customer.api.request.store.StoreQueryRequest;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandProvider;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.cate.ContractCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.contract.ContractProvider;
import com.wanmi.sbc.goods.api.provider.excel.GoodsSupplierExcelProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsHandlerProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateGoodsRelaQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandTransferByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandByNameRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.cate.ContractCateListCateByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.contract.ContractSaveRequest;
import com.wanmi.sbc.goods.api.request.excel.GoodsSupplierExcelExportTemplateByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateGoodsRelaListByGoodsIdsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateAddRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.response.brand.ContractBrandListResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandByNameResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandListResponse;
import com.wanmi.sbc.goods.api.response.cate.ContractCateListCateByStoreIdResponse;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoListResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsAddResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsSkuInfoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateGoodsRelaListByGoodsIdsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateAddResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsAttributeKeyDTO;
import com.wanmi.sbc.goods.bean.dto.ContractBrandSaveDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsImageDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.GoodsSupplierExcelImportRequest;
import com.wanmi.sbc.goods.service.SupplierGoodsExcelService;
import com.wanmi.sbc.marketing.api.provider.grouponactivity.GrouponActivityQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeBatchAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeByScopeIdRequest;
import com.wanmi.sbc.marketing.api.request.grouponactivity.GrouponActivityListSpuIdRequest;
import com.wanmi.sbc.marketing.bean.dto.CouponMarketingScopeDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponMarketingScopeVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "StoreGoodsController", description = "商品服务 API")
@RestController
@Slf4j
@RequestMapping("/goods")
public class StoreGoodsController {
    @Autowired
    GoodsAresProvider goodsAresProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private GoodsSupplierExcelProvider goodsSupplierExcelProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private SupplierGoodsExcelService supplierGoodsExcelService;

    @Autowired
    private GrouponActivityQueryProvider grouponActivityQueryProvider;

    @Value("${goods.defaultBrandId}")
    private Long goodsDefaultBrandId;

    @Value("${goods.defaultCatId}")
    private Long goodsDefaultCatId;

    @Value("${goods.defaultAttributeId:null}")
    private String defaultAttributeId;

    @Autowired
    private CompanyIntoPlatformQueryProvider companyIntoPlatformQueryProvider;

    @Autowired
    private ContractCateQueryProvider contractCateQueryProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StoreCateGoodsRelaQueryProvider storeCateGoodsRelaQueryProvider;

    @Autowired
    private ContractBrandProvider contractBrandProvider;

    @Autowired
    private StoreCateProvider storeCateProvider;

    @Autowired
    private ContractProvider contractProvider;

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    /**
     * 查询商品
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> list(@RequestBody GoodsPageRequest queryRequest) {
        queryRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        queryRequest.setStoreId(commonUtil.getStoreId());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        queryRequest.putSort("goodsId", SortType.ASC.toValue());


        //补充店铺分类
        if(queryRequest.getStoreCateId() != null) {
            BaseResponse<StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse>  baseResponse = storeCateQueryProvider.listGoodsRelByStoreCateIdAndIsHaveSelf(new StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest(queryRequest.getStoreCateId(), true));
            StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse cateIdAndIsHaveSelfResponse = baseResponse.getContext();
            if (Objects.nonNull(cateIdAndIsHaveSelfResponse)) {
                List<StoreCateGoodsRelaVO> relas = cateIdAndIsHaveSelfResponse.getStoreCateGoodsRelaVOList();
                if (CollectionUtils.isEmpty(relas)) {
                    GoodsPageResponse response = new GoodsPageResponse();
                    response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                    return BaseResponse.success(response);
                }
                queryRequest.setStoreCateGoodsIds(relas.stream().map(StoreCateGoodsRelaVO::getGoodsId).collect(Collectors.toList()));
            }else{
                GoodsPageResponse response = new GoodsPageResponse();
                response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                return BaseResponse.success(response);
            }
        }

        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = goodsQueryProvider.page(queryRequest);
        GoodsPageResponse response = goodsPageBaseResponse.getContext();
        List<GoodsVO> goodses = response.getGoodsPage().getContent();
        if(CollectionUtils.isNotEmpty(goodses)) {
            List<String> goodsIds = goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
            BaseResponse<StoreCateListByGoodsResponse> baseResponse = storeCateQueryProvider.listByGoods(new StoreCateListByGoodsRequest(goodsIds));
            StoreCateListByGoodsResponse cateListByGoodsResponse = baseResponse.getContext();
            if (Objects.isNull(cateListByGoodsResponse)){
                return BaseResponse.success(response);
            }
            Map<String, List<StoreCateGoodsRelaVO>> storeCateMap = cateListByGoodsResponse.getStoreCateGoodsRelaVOList()
                    .stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId));
            //为每个spu填充店铺分类编号
            if(MapUtils.isNotEmpty(storeCateMap)){
                goodses.stream()
                        .filter(goods -> storeCateMap.containsKey(goods.getGoodsId()))
                        .forEach(goods -> {
                            goods.setStoreCateIds(storeCateMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).filter(id -> id != null).collect(Collectors.toList()));
                        });
            }
        }
        return BaseResponse.success(response);
    }





    /**
     * 查询商品
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/devaning/devanningSpus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> devanningSpuslist(@RequestBody GoodsPageRequest queryRequest) {
        queryRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        queryRequest.setStoreId(commonUtil.getStoreId());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        if (commonUtil.getCompanyType()==1){
            queryRequest.setCompanyType(CompanyType.SUPPLIER);
        }

        //按店铺内商品排序升序
        if(queryRequest.getStoreGoodsSeqFlag() != null && queryRequest.getStoreGoodsSeqFlag() == 1){
        	queryRequest.putSort("storeGoodsSeqNum", SortType.ASC.toValue());
        }else{
            //按创建时间倒序
        	queryRequest.putSort("createTime", SortType.DESC.toValue());
        }
//        queryRequest.putSort("goodsId", SortType.ASC.toValue());

        //补充店铺分类
        if(queryRequest.getStoreCateId() != null) {
            BaseResponse<StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse>  baseResponse = storeCateQueryProvider.listGoodsRelByStoreCateIdAndIsHaveSelf(new StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest(queryRequest.getStoreCateId(), true));
            StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse cateIdAndIsHaveSelfResponse = baseResponse.getContext();
            if (Objects.nonNull(cateIdAndIsHaveSelfResponse)) {
                List<StoreCateGoodsRelaVO> relas = cateIdAndIsHaveSelfResponse.getStoreCateGoodsRelaVOList();
                if (CollectionUtils.isEmpty(relas)) {
                    GoodsPageResponse response = new GoodsPageResponse();
                    response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                    return BaseResponse.success(response);
                }
                queryRequest.setStoreCateGoodsIds(relas.stream().map(StoreCateGoodsRelaVO::getGoodsId).collect(Collectors.toList()));
            }else{
                GoodsPageResponse response = new GoodsPageResponse();
                response.setGoodsPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageable(), 0));
                return BaseResponse.success(response);
            }
        }

//        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = goodsQueryProvider.page(queryRequest);
        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = goodsQueryProvider.pagedevanning(queryRequest);
        GoodsPageResponse response = goodsPageBaseResponse.getContext();
        if(Objects.nonNull(response)){
            if(Objects.nonNull(response.getGoodsPages())){
                List<GoodsVO> goodses = response.getGoodsPages().getContent();
                if(CollectionUtils.isNotEmpty(goodses)) {
                    List<String> goodsIds = goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
                    BaseResponse<StoreCateListByGoodsResponse> baseResponse = storeCateQueryProvider.listByGoods(new StoreCateListByGoodsRequest(goodsIds));
                    StoreCateListByGoodsResponse cateListByGoodsResponse = baseResponse.getContext();
                    if (Objects.isNull(cateListByGoodsResponse)){
                        return BaseResponse.success(response);
                    }
                    Map<String, List<StoreCateGoodsRelaVO>> storeCateMap = cateListByGoodsResponse.getStoreCateGoodsRelaVOList()
                            .stream().collect(Collectors.groupingBy(StoreCateGoodsRelaVO::getGoodsId));
                    //为每个spu填充店铺分类编号
                    if(MapUtils.isNotEmpty(storeCateMap)){
                        goodses.stream()
                                .filter(goods -> storeCateMap.containsKey(goods.getGoodsId()))
                                .forEach(goods -> {
                                    goods.setStoreCateIds(storeCateMap.get(goods.getGoodsId()).stream().map(StoreCateGoodsRelaVO::getStoreCateId).filter(id -> id != null).collect(Collectors.toList()));
                                });
                    }
                }
                return BaseResponse.success(response);
            }
        }
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 查询商品列表(供添加拼团活动中选择商品用)
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "查询商品列表(供添加拼团活动中选择商品用)")
    @RequestMapping(value = "/groupon-spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> listForGroupon(@RequestBody GoodsPageForGrouponRequest request) {
        // 1.查询商品列表接口
        GoodsPageRequest pageRequest = KsBeanUtil.convert(request, GoodsPageRequest.class);
        pageRequest.setAddedFlags(Arrays.asList(1, 2));
        pageRequest.setAuditStatus(CheckStatus.CHECKED);
        BaseResponse<GoodsPageResponse> response = this.list(pageRequest);
        List<GoodsVO> goodses = response.getContext().getGoodsPage().getContent();

        // 2.标记不可选择的商品
        if (goodses.size() != 0
                && request.getActivityStartTime() != null
                && request.getActivityEndTime() != null) {
            List<String> goodsIds = grouponActivityQueryProvider.listActivityingSpuIds(
                    new GrouponActivityListSpuIdRequest(
                            goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList()),
                            request.getActivityStartTime(),
                            request.getActivityEndTime())
            ).getContext().getGoodsIds();

            if (CollectionUtils.isNotEmpty(goodsIds)) {
                goodses.forEach(goods -> {
                    if (goodsIds.contains(goods.getGoodsId())) {
                        goods.setGrouponForbiddenFlag(true);
                    }
                });
            }
        }

        return response;
    }


    /**
     * 批量设置分类
     */
    @ApiOperation(value = "批量设置分类")
    @RequestMapping(value = "/spu/cate", method = RequestMethod.PUT)
    public BaseResponse updateCate(@RequestBody GoodsModifyCateRequest request) {

        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (CollectionUtils.isEmpty(request.getStoreCateIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsProvider.modifyCate(request);
        operateLogMQUtil.convertAndSend("商品","批量设置店铺分类","批量设置店铺分类");
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 下载模板
     */
    @ApiOperation(value = "下载模板")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "加密", required = true)
    @RequestMapping(value = "/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        GoodsSupplierExcelExportTemplateByStoreIdRequest request =
                new GoodsSupplierExcelExportTemplateByStoreIdRequest();
        request.setStoreId(commonUtil.getStoreId());
        String file = goodsSupplierExcelProvider.supplierExportTemplate(request).getContext().getFile();
        if(StringUtils.isNotBlank(file)){
            try {
                String fileName = URLEncoder.encode("商品导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            }catch (Exception e){
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            operateLogMQUtil.convertAndSend("商品","下载模板","操作成功");
        }

    }

    /**
     * 确认导入商品
     */
    @ApiOperation(value = "确认导入商品")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext", value = "后缀", required = true)
    @RequestMapping(value = "/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> implGoods(@PathVariable String ext) {
        if(!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        if(companyInfo == null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        GoodsSupplierExcelImportRequest importRequest = new GoodsSupplierExcelImportRequest();
        importRequest.setExt(ext);
        importRequest.setUserId(commonUtil.getOperatorId());
        importRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        importRequest.setStoreId(commonUtil.getStoreId());
        importRequest.setCompanyType(companyInfo.getCompanyType());
        importRequest.setSupplierName(companyInfo.getSupplierName());
        importRequest.setType(StoreType.SUPPLIER);
        List<String> skuIds = supplierGoodsExcelService.implGoods(importRequest);

        //加入ES
        if(CollectionUtils.isNotEmpty(skuIds)){
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(skuIds).build());
            //ares埋点-商品-后台导入商品sku
            goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSkuIds",skuIds.toArray()));
        }

        operateLogMQUtil.convertAndSend("商品","商品模板导入","商品模板导入");

        return BaseResponse.success(Boolean.TRUE);
    }



    /**
     * 新增商品
     */
    @ApiOperation(value = "新增商品")
    @RequestMapping(value = "/addSpu", method = RequestMethod.POST)
    public BaseResponse<String> addSpu(@RequestBody GoodsAddRequest request) {
        addStoreCatNameBrandName(request);
        checkAddSpu(request);
        Long fId = request.getGoods().getFreightTempId();
        if (CollectionUtils.isEmpty(request.getGoodsInfos()) || StringUtils
                .isEmpty(String.valueOf(fId))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();

        if (companyInfo == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        request.getGoods().setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.getGoods().setCompanyType(companyInfo.getCompanyType());
        request.getGoods().setStoreId(commonUtil.getStoreId());
        request.getGoods().setSupplierName(companyInfo.getSupplierName());
        BaseResponse<GoodsAddResponse> baseResponse = goodsProvider.merchantGoodsAdd(request);
        GoodsAddResponse response = baseResponse.getContext();
        String goodsId = Optional.ofNullable(response)
                .map(GoodsAddResponse::getResult)
                .orElse(null);
        //ares埋点-商品-后台添加商品sku
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSpu", new String[]{goodsId}));
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).build());

        operateLogMQUtil.convertAndSend("商品", "直接发布",
                "直接发布：SPU编码" + request.getGoods().getGoodsNo());
        //推送到erp
        request.getGoods().setGoodsId(goodsId);
        GoodsAddRequest goodsAddRequest =new GoodsAddRequest();
        GoodsDTO goodsDTO=new GoodsDTO();
        goodsDTO.setGoodsId(goodsId);
        goodsAddRequest.setGoods(goodsDTO);
        try {
            goodsProvider.sysnErp(goodsAddRequest);
        }catch (Exception e){
            operateLogMQUtil.convertAndSend("直接发布","直接发布","直接发布异常"+ JSONObject.toJSON(request.getGoods().getGoodsId()));
        }
        return BaseResponse.success(goodsId);
    }

    //
    @PostMapping("/refreshGoods")
    public BaseResponse<List<String>> freshGoods(@RequestBody RefreshGoodsDTO dto) {
        final Long storeId = dto.getStoreId();
        if (null == storeId) {
            BaseResponse.success(Lists.newArrayList("参数错误"));
        }
        String key = "refreshGoodsAdd_"+storeId;
        final StoreQueryRequest storeQueryRequest = new StoreQueryRequest();
        storeQueryRequest.setStoreIds(Lists.newArrayList(storeId));
        final BaseResponse<List<StoreSimpleResponse>> listBaseResponse = storeQueryProvider.listSimple(storeQueryRequest);
        final StoreSimpleResponse store = listBaseResponse.getContext().get(0);
        // 查找商品
        final GoodsInfoListByConditionRequest goodsByConditionRequest = new GoodsInfoListByConditionRequest();
        goodsByConditionRequest.setGoodsIds(dto.getGoodsIds());
        goodsByConditionRequest.setStoreId(storeId);
        final BaseResponse<GoodsInfoListByConditionResponse> goodsByConditionResponseBaseResponse = goodsInfoQueryProvider.listByCondition(goodsByConditionRequest);
        final List<GoodsInfoVO> goodsInfos = goodsByConditionResponseBaseResponse.getContext().getGoodsInfos();
        final Map<String, List<GoodsInfoVO>> goodsMap = goodsInfos.stream().collect(Collectors.groupingBy(GoodsInfoVO::getGoodsId));
        goodsMap.forEach((k, v) -> {
            if (v.size() < 2) {
                return;
            }
            DevanningGoodsInfoListByConditionRequest devanningGoodsInfoListByConditionRequest = new DevanningGoodsInfoListByConditionRequest();
            devanningGoodsInfoListByConditionRequest.setGoodsInfoIds(v.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList()));
            final BaseResponse<DevanningGoodsInfoListResponse> devanningGoodsInfoListResponseBaseResponse = devanningGoodsInfoQueryProvider.listByCondition(devanningGoodsInfoListByConditionRequest);
            final Map<String, DevanningGoodsInfoVO> dMap = devanningGoodsInfoListResponseBaseResponse.getContext().getDevanningGoodsInfoVOS().stream().collect(Collectors.toMap(DevanningGoodsInfoVO::getGoodsInfoId, Function.identity(), (o, n) -> o));
            final GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(k);
            final BaseResponse<GoodsByIdResponse> goodsResponse = goodsQueryProvider.getById(goodsByIdRequest);
            final StoreCateGoodsRelaListByGoodsIdsRequest storeCateGoodsRelaListByGoodsIdsRequest = new StoreCateGoodsRelaListByGoodsIdsRequest();
            storeCateGoodsRelaListByGoodsIdsRequest.setGoodsIds(Lists.newArrayList(k));
            final BaseResponse<StoreCateGoodsRelaListByGoodsIdsResponse> storeCatResponse = storeCateGoodsRelaQueryProvider.listByGoodsIds(storeCateGoodsRelaListByGoodsIdsRequest);
            final List<Long> storeCatIds = storeCatResponse.getContext().getStoreCateGoodsRelaVOList().stream().map(StoreCateGoodsRelaVO::getStoreCateId).collect(Collectors.toList());
            final GoodsByIdResponse goods = goodsResponse.getContext();
            v.forEach(info -> {
                final String rValue = stringRedisTemplate.opsForValue().get(key);
                if (StringUtils.isNotBlank(rValue)){
                    final List<String> rInfoIds = JSON.parseArray(rValue, String.class);
                    if (rInfoIds.contains(info.getGoodsInfoId())){
                        return;
                    }
                }
                // 添加商
                final GoodsAddRequest goodsAddRequest = new GoodsAddRequest();
                // 商品
                final GoodsDTO goodsDTO = new GoodsDTO();
                goodsDTO.setGoodsName(goods.getGoodsName());
                goodsDTO.setCateId(goods.getCateId());
                goodsDTO.setBrandId(goods.getBrandId());
                goodsDTO.setStoreCateIds(storeCatIds);
                goodsDTO.setGoodDate(goods.getGoodDate());
                goodsDTO.setShelflife(goods.getShelflife());
                goodsDTO.setIsScatteredQuantitative(goods.getIsScatteredQuantitative());
                goodsDTO.setAddedFlag(info.getAddedFlag());
                goodsDTO.setGoodsImg(goods.getGoodsImg());
                goodsDTO.setGoodsNo(generateOrderNo());
                goodsDTO.setGoodsVideo(goods.getGoodsVideo());
                goodsDTO.setFreightTempId(goods.getFreightTempId());
                // 限购区域
                goodsDTO.setGoodsDetail(goods.getGoodsDetail());
                goodsDTO.setAuditStatus(goods.getAuditStatus());
                goodsDTO.setSaleType(goods.getSaleType());
                goodsDTO.setGoodsType(goods.getGoodsType());
                goodsDTO.setGoodsSource(goods.getGoodsSource());
                goodsDTO.setAllowPriceSet(goods.getAllowPriceSet());
                goodsDTO.setSupplyPrice(goods.getSupplyPrice());
                goodsDTO.setSubmitTime(goods.getSubmitTime());
                goodsDTO.setGoodsSeqNum(goods.getGoodsSeqNum());
                goodsDTO.setStoreGoodsSeqNum(goods.getStoreGoodsSeqNum());
                goodsDTO.setGoodsWeight(goods.getGoodsWeight());
                goodsAddRequest.setGoods(goodsDTO);

                String image = StringUtils.isNotBlank(info.getGoodsInfoImg()) ? info.getGoodsInfoImg() : goods.getGoodsImg();
                // 图片
//                goodsAddRequest.setImages(KsBeanUtil.convertList(goods.getImages(), GoodsImageDTO.class));
                List<GoodsImageDTO> images = new ArrayList<>();
                final GoodsImageDTO goodsImageDTO = new GoodsImageDTO();
                images.add(goodsImageDTO);
                goodsImageDTO.setArtworkUrl(image);
                goodsAddRequest.setImages(images);
                List<GoodsInfoDTO> infos = new ArrayList<>();
                goodsAddRequest.setGoodsInfos(infos);
                final GoodsInfoDTO goodsInfoDTO = new GoodsInfoDTO();
                infos.add(goodsInfoDTO);
                goodsInfoDTO.setGoodsInfoImg(info.getGoodsInfoImg());
                goodsInfoDTO.setGoodsInfoBatchNo(info.getGoodsInfoBatchNo());
                goodsInfoDTO.setIsScatteredQuantitative(info.getIsScatteredQuantitative());
                goodsInfoDTO.setStock(info.getStock());
                goodsInfoDTO.setLockStock(info.getLockStock());
                goodsInfoDTO.setMarketPrice(info.getMarketPrice());
                goodsInfoDTO.setCostPrice(info.getCostPrice());
                goodsInfoDTO.setLockStock(info.getLockStock());
                goodsInfoDTO.setAddedFlag(info.getAddedFlag());
                goodsInfoDTO.setGoodsStatus(info.getGoodsStatus());
                goodsInfoDTO.setGoodsInfoBatchNo(info.getGoodsInfoBatchNo());
                goodsInfoDTO.setAddStep(info.getAddStep());
                goodsInfoDTO.setDateUnit(info.getDateUnit());
                goodsInfoDTO.setGoodsInfoWeight(info.getGoodsInfoWeight());
                goodsInfoDTO.setShelflife(info.getShelflife());
                goodsInfoDTO.setIsCheck(info.getIsCheck());
                goodsInfoDTO.setHostSku(1);
                goodsInfoDTO.setGoodsInfoBarcode(info.getGoodsInfoBarcode());
                goodsInfoDTO.setVipPrice(info.getVipPrice());
                goodsInfoDTO.setGoodsInfoUnit(info.getGoodsInfoUnit());
                goodsInfoDTO.setGoodsInfoCubage(info.getGoodsInfoCubage());
                goodsInfoDTO.setDevanningId(info.getDevanningId());
                goodsInfoDTO.setGoodsInfoWeight(info.getGoodsInfoWeight());
                goodsInfoDTO.setDevanningUnit(dMap.get(info.getGoodsInfoId()).getDevanningUnit());
                List<GoodsAttributeKeyDTO> goodsAttributeKeys = new ArrayList<>();
                final GoodsAttributeKeyDTO goodsAttributeKeyDTO = new GoodsAttributeKeyDTO();
                goodsAttributeKeyDTO.setAttributeId(defaultAttributeId);
                goodsAttributeKeyDTO.setAttributeName("规格");
                goodsAttributeKeyDTO.setGoodsAttributeValue("1");
                goodsAttributeKeys.add(goodsAttributeKeyDTO);
                goodsInfoDTO.setGoodsAttributeKeys(goodsAttributeKeys);
                final GoodsGetSkuRequest goodsGetSkuRequest = new GoodsGetSkuRequest();
                List<GoodsAttributeVo> attributeList = new ArrayList<>();
                final GoodsAttributeVo goodsAttributeVo = new GoodsAttributeVo();
                goodsAttributeVo.setAttributeId(defaultAttributeId);
                goodsAttributeVo.setAttribute("规格");
                goodsAttributeVo.setAttributes(Lists.newArrayList("1"));
                attributeList.add(goodsAttributeVo);
                goodsGetSkuRequest.setAttributeList(attributeList);
                final BaseResponse<GoodsSkuInfoResponse> skuInfo = goodsProvider.getSkuInfo(goodsGetSkuRequest);
                goodsInfoDTO.setGoodsInfoNo(skuInfo.getContext().getGoodsInfoList().get(0).getGoodsInfoNo());
                final BaseResponse<String> stringBaseResponse = addSpuV2(goodsAddRequest, store);
                if (stringBaseResponse.getContext() != null) {
                    List<String> rInfoIds;
                    final String rValue2 = stringRedisTemplate.opsForValue().get(key);
                    if (StringUtils.isNotBlank(rValue2)){
                        rInfoIds = JSON.parseArray(rValue, String.class);
                    }else {
                        rInfoIds = new ArrayList<>();
                    }
                    assert rInfoIds != null;
                    rInfoIds.add(info.getGoodsInfoId());
                    stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(rInfoIds));
                }
            });
            // 下架商品
            offItems(k);
        });
        return BaseResponse.success(new ArrayList<>());
    }

    @Data
    public static class RefreshGoodsDTO implements Serializable {

        private Long storeId;

        private List<String> goodsIds;
    }

    // 后面改批量
    private String offItems(String goodsId) {
        if (null == goodsId) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        final GoodsModifyAddedStatusRequest request = new GoodsModifyAddedStatusRequest();

        request.setAddedFlag(AddedFlag.NO.toValue());
        //如果下架商品是供应商商品，商家商品同步下架
        request.setGoodsIds(Lists.newArrayList(goodsId));
        goodsProvider.modifyAddedStatus(request);
        //更新ES
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), request.getGoodsIds(), null, null);

        //ares埋点-商品-后台批量修改商品spu的所有sku上下架状态
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpuUp", new Object[]{AddedFlag.NO.toValue(), request.getGoodsIds()}));

        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "下架",
                    "下架：SPU编码" + response.getGoodsNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量下架", "批量下架");
        }
        return "成功";
    }


    public BaseResponse<String> addSpuV2(@RequestBody GoodsAddRequest request,StoreSimpleResponse store) {
        checkAddSpu(request);
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null || CollectionUtils.isEmpty(request.getGoodsInfos()) || StringUtils
                .isEmpty(String.valueOf(fId))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.getGoods().setCompanyInfoId(store.getCompanyInfoId());
        request.getGoods().setCompanyType(store.getCompanyType());
        request.getGoods().setStoreId(store.getStoreId());
        request.getGoods().setSupplierName(store.getSupplierName());
        BaseResponse<GoodsAddResponse> baseResponse = goodsProvider.merchantGoodsAdd(request);
        GoodsAddResponse response = baseResponse.getContext();
        String goodsId = Optional.ofNullable(response)
                .map(GoodsAddResponse::getResult)
                .orElse(null);
        //ares埋点-商品-后台添加商品sku
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSpu", new String[]{goodsId}));
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsId(goodsId).build());

        operateLogMQUtil.convertAndSend("商品", "直接发布",
                "直接发布：SPU编码" + request.getGoods().getGoodsNo());
        //推送到erp
        request.getGoods().setGoodsId(goodsId);
        GoodsAddRequest goodsAddRequest =new GoodsAddRequest();
        GoodsDTO goodsDTO=new GoodsDTO();
        goodsDTO.setGoodsId(goodsId);
        goodsAddRequest.setGoods(goodsDTO);
        try {
            goodsProvider.sysnErp(goodsAddRequest);
        }catch (Exception e){
            operateLogMQUtil.convertAndSend("直接发布","直接发布","直接发布异常"+ JSONObject.toJSON(request.getGoods().getGoodsId()));
        }
        return BaseResponse.success(goodsId);
    }

    private void addStoreCatNameBrandName(GoodsAddRequest request) {
        final GoodsDTO goods = request.getGoods();
        final String addBrandName = goods.getAddBrandName();
        final String addStoreCatName = goods.getAddStoreCatName();
        if (StringUtils.isNotBlank(addBrandName)) {
            final ContractSaveRequest contractRequest = new ContractSaveRequest();
            contractRequest.setStoreId(commonUtil.getStoreId());
            List<ContractBrandSaveDTO> brandSaveRequests = Lists.newArrayList();
            final ContractBrandSaveDTO contractBrandSaveDTO = new ContractBrandSaveDTO();
            contractBrandSaveDTO.setName(addBrandName);
            brandSaveRequests.add(contractBrandSaveDTO);
            contractRequest.setBrandSaveRequests(brandSaveRequests);
            contractProvider.save(contractRequest);
            contractBrandProvider.transferByStoreId(ContractBrandTransferByStoreIdRequest.builder().storeId(contractRequest.getStoreId()).build());
            final GoodsBrandListRequest goodsBrandByNameRequest = new GoodsBrandListRequest();
            goodsBrandByNameRequest.setBrandName(addBrandName);
            goodsBrandByNameRequest.setDelFlag(DeleteFlag.NO.toValue());
            final BaseResponse<GoodsBrandListResponse> goodsName = goodsBrandQueryProvider.list(goodsBrandByNameRequest);
            if (goodsName == null || goodsName.getContext() == null ||
                    null == goodsName.getContext().getGoodsBrandVOList()) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自动添加品牌失败，" + addStoreCatName);
            }
            goods.setBrandId(goodsName.getContext().getGoodsBrandVOList().get(0).getBrandId());
        }
        if (StringUtils.isNotBlank(addStoreCatName)) {
            final StoreCateAddRequest saveRequest = new StoreCateAddRequest();
            Long storeId = commonUtil.getStoreId();
            saveRequest.setStoreId(storeId);
            saveRequest.setCateName(addStoreCatName);
            saveRequest.setCateImg("https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311130944243951.png");
            StoreCateAddResponse cateResponse = storeCateProvider.add(saveRequest).getContext();
            if (null == cateResponse || null == cateResponse.getStoreCateResponseVO()
                    || null == cateResponse.getStoreCateResponseVO().getStoreCateId()) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自动添加商品分类失败，" + addStoreCatName);
            }
            final StoreCateAddRequest auto = new StoreCateAddRequest();
            auto.setStoreId(storeId);
            auto.setCateImg(saveRequest.getCateImg());
            auto.setCateParentId(cateResponse.getStoreCateResponseVO().getStoreCateId());
            auto.setCateName(addStoreCatName);
            auto.setCateImg("https://xyytest-image01.oss-cn-hangzhou.aliyuncs.com/202311130944243951.png");
            saveRequest.setAutoInitLeaf(true);
            final StoreCateAddResponse leafResponse = storeCateProvider.add(auto).getContext();
            if (null == leafResponse || null == leafResponse.getStoreCateResponseVO()
                    || null == leafResponse.getStoreCateResponseVO().getStoreCateId()) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "自动添加商品分类失败，" + addStoreCatName);
            }
            goods.setStoreCateIds(Lists.newArrayList(leafResponse.getStoreCateResponseVO().getStoreCateId()));
        }
    }

    private void checkAddSpu(GoodsAddRequest request) {
        Long storeId = commonUtil.getStoreId();
        if (null == storeId) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家不能为空");
        }
        final GoodsDTO goods = request.getGoods();
        if (null == goods.getCateId()) {
            final ContractCateListCateByStoreIdRequest contractCateListCateByStoreIdRequest = new ContractCateListCateByStoreIdRequest();
            contractCateListCateByStoreIdRequest.setStoreId(storeId);
            final BaseResponse<ContractCateListCateByStoreIdResponse> cateByStoreId = contractCateQueryProvider.listCateByStoreId(contractCateListCateByStoreIdRequest);
            List<GoodsCateVO> goodsCateList = cateByStoreId.getContext().getGoodsCateList();
            goodsCateList = goodsCateList.stream().filter(u -> Objects.equals(u.getCateGrade(),3)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(goodsCateList)){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "没有签约类目，无法默认");
            }
            goods.setCateId(goodsCateList.get(0).getCateId());
        }

        if (null == goods.getBrandId()){
            final ContractBrandListRequest contractBrandListRequest = new ContractBrandListRequest();
            contractBrandListRequest.setStoreId(storeId);
            final BaseResponse<ContractBrandListResponse> listResponseBaseResponse = contractBrandQueryProvider.list(contractBrandListRequest);
            final List<ContractBrandVO> contractBrandVOList = listResponseBaseResponse.getContext().getContractBrandVOList();
            if (CollectionUtils.isEmpty(contractBrandVOList)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "没有签约品牌，无法默认");
            }
            goods.setBrandId(contractBrandVOList.get(0).getGoodsBrand().getBrandId());
        }
        final CompanyMallReturnGoodsAddressRequest addressRequest = new CompanyMallReturnGoodsAddressRequest();
        addressRequest.setDeleteFlag(DeleteFlag.NO);
        addressRequest.setStoreId(storeId);
        final BaseResponse<List<CompanyMallReturnGoodsAddressVO>> listBaseResponse = companyIntoPlatformQueryProvider.listReturnGoodsAddress(addressRequest);
        if (CollectionUtils.isEmpty(listBaseResponse.getContext())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "请先设置退货地址");
        }
    }


    /**
     * 根据商品属性生产对应的表格
     */
    @ApiOperation(value = " 根据商品属性生产对应的表格")
    @RequestMapping(value = "/getSkuInfo", method = RequestMethod.POST)
    public BaseResponse<GoodsSkuInfoResponse> getSkuInfo(@RequestBody GoodsGetSkuRequest request) {
        return goodsProvider.getSkuInfo(request);
    }

    public static void main(String[] args) {
       String orderNo = generateOrderNo();
        System.out.println(orderNo);
    }

    private static String generateOrderNo() {
        // 生产orderNo和前端的逻辑保持一致，之前前端传的
        return "P" +
                String.valueOf(new Date().getTime())
                        .substring(4, 10) +
                String.valueOf(Math.random())
                        .substring(2, 5);
    }
}
