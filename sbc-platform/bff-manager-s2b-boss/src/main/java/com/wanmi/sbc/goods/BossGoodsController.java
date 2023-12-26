package com.wanmi.sbc.goods;
import com.google.common.collect.Lists;
import java.util.List;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.excel.GoodsExcelProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.relationgoodsimages.RelationGoodsImagesProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardImportProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsCheckRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsImageTypeAddRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoModifyRecommendSortRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.standard.StandardImportStandardRequest;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.request.GoodsSortImportExcelRequest;
import com.wanmi.sbc.goods.service.GoodsSortImportExcelService;
import com.wanmi.sbc.job.CheckInventoryHandler;
import com.wanmi.sbc.order.response.ExportGoodsByStatusExcel;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * S2B的BOSS商品服务
 * Created by daiyitian on 17/4/12.
 */
@RestController
@RequestMapping("/goods")
@Api(description = "S2B的BOSS商品服务", tags = "BossGoodsController")
@Slf4j
public class BossGoodsController {

    @Autowired
    GoodsAresProvider goodsAresProvider;

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private StandardGoodsQueryProvider standardGoodsQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private StandardImportProvider standardImportProvider;

    @Autowired
    private CheckInventoryHandler checkInventoryHandler;

    @Autowired
    private GoodsExcelProvider goodsExcelProvider;

    @Autowired
    private GoodsSortImportExcelService goodsSortImportExcelService;

    @Autowired
    private DevanningGoodsInfoQueryProvider devanningGoodsInfoQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private RelationGoodsImagesProvider relationGoodsImagesProvider;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    /**
     * 审核/驳回商品
     *
     * @param checkRequest 审核参数
     * @return 商品详情
     */
    @ApiOperation(value = "审核/驳回商品")
    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    @LcnTransaction
    public BaseResponse check(@Valid @RequestBody GoodsCheckRequest checkRequest) {
        checkRequest.setChecker(commonUtil.getAccountName());
        goodsProvider.checkGoods(checkRequest);

        //操作日志记录
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(checkRequest.getGoodsIds().get(0));
        GoodsByIdResponse goods = goodsQueryProvider.getById(goodsByIdRequest).getContext();
        if (0 != goods.getGoodsSource()) {
            if (checkRequest.getGoodsIds() == null) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
            }
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().goodsIds(checkRequest.getGoodsIds()).build());
        }
        //ares埋点-商品-后台审核/驳回商品
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpus", checkRequest.getGoodsIds().toArray()));

        if (checkRequest.getAuditStatus() == CheckStatus.CHECKED) {
            operateLogMQUtil.convertAndSend("商品", "审核商品",
                    "审核商品：SPU编码" + goods.getGoodsNo());
        } else if (checkRequest.getAuditStatus() == CheckStatus.NOT_PASS) {
            operateLogMQUtil.convertAndSend("商品", "驳回商品",
                    "驳回商品：SPU编码" + goods.getGoodsNo());
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 禁售商品
     *
     * @param checkRequest 禁售参数
     * @return 商品详情
     */
    @ApiOperation(value = "禁售商品")
    @RequestMapping(value = "/forbid", method = RequestMethod.PUT)
    @LcnTransaction
    public BaseResponse forbid(@Valid @RequestBody GoodsCheckRequest checkRequest) {
        checkRequest.setChecker(commonUtil.getAccountName());
        goodsProvider.checkGoods(checkRequest);
        if (checkRequest.getGoodsIds() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"当前商品ID为空");
        }
        esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().auditStatus(checkRequest.getAuditStatus()).goodsIds(checkRequest.getGoodsIds()).build());
        //ares埋点-商品-后台禁售商品
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpus", checkRequest.getGoodsIds().toArray()));

        //操作日志记录
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(checkRequest.getGoodsIds().get(0));
        GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
        operateLogMQUtil.convertAndSend("商品", "禁售商品",
                "禁售商品：SPU编码" + response.getGoodsNo());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询商品
     *
     * @param pageRequest 商品 {@link GoodsPageRequest}
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> list(@RequestBody GoodsPageRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        if(pageRequest.getGoodsSeqFlag()!=null&&pageRequest.getGoodsSeqFlag()==1){
            pageRequest.putSort("goodsSeqNum", SortType.ASC.toValue());
        }else{
            //按创建时间倒序、ID升序
            pageRequest.putSort("createTime", SortType.DESC.toValue());
        }
        //此条件范围值筛选特价商品
        if (Objects.nonNull(pageRequest.getSpecialPriceFirst()) || Objects.nonNull(pageRequest.getSpecialPriceLast()) || StringUtils.isNotEmpty(pageRequest.getGoodsInfoBatchNo())){
            pageRequest.setGoodsInfoType(1);
        }

        //   pageRequest.putSort("goodsId", SortType.ASC.toValue());
        BaseResponse<GoodsPageResponse> pageResponse = goodsQueryProvider.page(pageRequest);
        GoodsPageResponse goodsPageResponse = pageResponse.getContext();
        List<GoodsVO> goodses = goodsPageResponse.getGoodsPage().getContent();
        if (CollectionUtils.isNotEmpty(goodses)) {
            //列出已导入商品库的商品编号
            StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
            standardGoodsGetUsedGoodsRequest.setGoodsIds(goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList()));
            goodsPageResponse.setImportStandard(standardGoodsQueryProvider.getUsedGoods(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds());
        }
        return pageResponse;
    }



    /**
     * 查询商品
     *
     * @param pageRequest 商品 {@link GoodsPageRequest}
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/devanning/spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> devanningList(@RequestBody GoodsPageRequest pageRequest) {
//        pageRequest.setCompanyType(CompanyType.PLATFORM);
        BaseResponse<GoodsPageResponse> pageResponse = getGoodsPageResponseBaseResponse(pageRequest);
        return pageResponse;
    }

    private BaseResponse<GoodsPageResponse> getGoodsPageResponseBaseResponse(GoodsPageRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        if(pageRequest.getGoodsSeqFlag()!=null&& pageRequest.getGoodsSeqFlag()==1){
            pageRequest.putSort("goodsSeqNum", SortType.ASC.toValue());
        }else{
            //按创建时间倒序、ID升序
            pageRequest.putSort("createTime", SortType.DESC.toValue());
        }
        //此条件范围值筛选特价商品
        if (Objects.nonNull(pageRequest.getSpecialPriceFirst()) || Objects.nonNull(pageRequest.getSpecialPriceLast()) || StringUtils.isNotEmpty(pageRequest.getGoodsInfoBatchNo())){
            pageRequest.setGoodsInfoType(1);
        }

        if (StringUtils.isNotBlank(pageRequest.getStoreName())){
            BaseResponse<ListStoreResponse> listStoreResponseBaseResponse = storeQueryProvider.listStore(ListStoreRequest.builder().storeName(pageRequest.getStoreName()).build());
            if (CollectionUtils.isNotEmpty(listStoreResponseBaseResponse.getContext().getStoreVOList())){
                List<Long> collect = listStoreResponseBaseResponse.getContext().getStoreVOList().stream().map(StoreVO::getStoreId).collect(Collectors.toList());
                pageRequest.setStoreIds(collect);
            }else {
                GoodsPageResponse response = new GoodsPageResponse();
                final GoodsInsidePageResponse goodsInsidePageResponse = new GoodsInsidePageResponse();
                goodsInsidePageResponse.setContent(Lists.newArrayList());
                goodsInsidePageResponse.setTotal(0);
                goodsInsidePageResponse.setTotalElements(0);
                goodsInsidePageResponse.setTotalPages(0);
                goodsInsidePageResponse.setSize(0);
                                response.setGoodsPages(goodsInsidePageResponse);
                return BaseResponse.success(response);
            }
        }

        BaseResponse<GoodsPageResponse> pageResponse = goodsQueryProvider.pagedevanning(pageRequest);

        GoodsPageResponse goodsPageResponse = pageResponse.getContext();
        List<GoodsVO> goodses = goodsPageResponse.getGoodsPages().getContent();
        final Map<Long, String> storeNameMap = mapStoreInfo(goodses);
        goodses.forEach(vo -> vo.setStoreName(storeNameMap.get(vo.getStoreId())));

        if (CollectionUtils.isNotEmpty(goodses)) {
            //列出已导入商品库的商品编号
            StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
            standardGoodsGetUsedGoodsRequest.setGoodsIds(goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList()));
            goodsPageResponse.setImportStandard(standardGoodsQueryProvider.getUsedGoods(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds());
        }
        return pageResponse;
    }

    private Map<Long, String> mapStoreInfo(List<GoodsVO> goodses) {
        try {
            final List<Long> storeIds = goodses.stream().map(GoodsVO::getStoreId).distinct().collect(Collectors.toList());
            final BaseResponse<ListStoreByIdsResponse> storeByIdsResponseBaseResponse = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(storeIds).build());
            final Map<Long, String> storeNameMap = storeByIdsResponseBaseResponse.getContext().getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, StoreVO::getStoreName));
            return storeNameMap;
        } catch (Exception e) {
            log.error("查询店铺信息异常", e);
            return new HashMap<>();
        }
    }

    /**
     * 三方商家运营列表
     *
     * @param pageRequest 商品 {@link GoodsPageRequest}
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/devanning/third/spus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> devanningThirdList(@RequestBody GoodsPageRequest pageRequest) {
        pageRequest.setCompanyType(CompanyType.SUPPLIER);
        BaseResponse<GoodsPageResponse> pageResponse = getGoodsPageResponseBaseResponse(pageRequest);
        return pageResponse;
    }


    /**
     * 查询商品
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品")
    @RequestMapping(value = "/devaning/bossdevanningSpus", method = RequestMethod.POST)
    public BaseResponse<GoodsPageResponse> devanningSpuslist(@RequestBody GoodsPageRequest queryRequest) {
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

//        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = goodsQueryProvider.page(queryRequest);
        BaseResponse<GoodsPageResponse> goodsPageBaseResponse = goodsQueryProvider.bpagedevanning(queryRequest);
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



    @ApiOperation(value = "商品合成图片批量接口")
    @RequestMapping(value = "/goodsImageType/addLsit")
    public BaseResponse goodsImageTypeAddLsit(@RequestBody GoodsImageTypeAddRequest goodsImageTypeAddRequest){

        relationGoodsImagesProvider.addList(goodsImageTypeAddRequest);
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 加入商品库
     *
     * @param request 导入参数
     * @return 成功结果
     */
    @ApiOperation(value = "加入商品库")
    @RequestMapping(value = "/standard", method = RequestMethod.POST)
    public BaseResponse importGoods(@RequestBody StandardImportStandardRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        standardImportProvider.importStandard(request);

        //操作日志记录
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
        GoodsByIdResponse response = goodsQueryProvider.getById(goodsByIdRequest).getContext();
        operateLogMQUtil.convertAndSend("商品", "加入商品库",
                "加入商品库：SPU编码" + response.getGoodsNo());

        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 手动同步商品的库存
     *
     * @return 成功结果
     */
    @ApiOperation(value = "加入商品库")
    @RequestMapping(value = "/sync/stock", method = RequestMethod.GET)
    public BaseResponse syncStock() throws Exception {
        checkInventoryHandler.execute(null);
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "下载导入商品排序模板")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted",
            value = "加密", required = true)
    @RequestMapping(value = "sort/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        String file = goodsExcelProvider.exportSortTemplate().getContext().getFile();
        if (org.apache.commons.lang.StringUtils.isNotBlank(file)) {
            try {
                String fileName = URLEncoder.encode("商品排序导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            } catch (Exception e) {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
    }

    /**
     * 上传排序文件
     */
    @ApiOperation(value = "上传排序文件")
    @RequestMapping(value = "sort/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        return BaseResponse.success(goodsSortImportExcelService.upload(uploadFile, commonUtil.getOperatorId()));
    }

    /**
     * 确认商品排序导入
     *
     * @param ext 文件格式 {@link String}
     * @return
     */
    @ApiOperation(value = "确认商品排序导入")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext",
            value = "文件名后缀", required = true)
    @RequestMapping(value = "sort/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> implGoods(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        GoodsSortImportExcelRequest excelRequest = new GoodsSortImportExcelRequest();
        excelRequest.setExt(ext);
        excelRequest.setUserId(commonUtil.getOperatorId());
        goodsSortImportExcelService.implGoods(excelRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品排序", "批量导入", "批量导入");
        return BaseResponse.success(Boolean.TRUE);
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
    @RequestMapping(value = "sort/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsSortImportExcelService.downErrExcel(commonUtil.getOperatorId(), ext);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品排序", "下载错误文档", "下载成功");
    }

    /**
     * 导出商品列表
     *
     * @param encrypted
     * @param response
     */
    @ApiOperation(value = "导出商品列表")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "加密", required = true)
    @RequestMapping(value = "/spu/export/params/{encrypted}", method = RequestMethod.GET)
    public void exportByParams(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));

        GoodsPageRequest goodsPageRequest = JSON.parseObject(decrypted, GoodsPageRequest.class);
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        if(goodsPageRequest.getGoodsSeqFlag()!=null&&goodsPageRequest.getGoodsSeqFlag()==1){
            goodsPageRequest.putSort("goodsSeqNum", SortType.ASC.toValue());
        }else{
            //按创建时间倒序、ID升序
            goodsPageRequest.putSort("createTime", SortType.DESC.toValue());
        }
        goodsPageRequest.setPageNum(0);
        goodsPageRequest.setPageSize(1000);

        StoreGoodsExportListResponse goodsInfoResponse = goodsQueryProvider.getStoreExportGoods(goodsPageRequest).getContext();

        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("批量商品_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/goods/export/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {
            export(goodsInfoResponse.getGoodsExports(), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品列表", "导出商品列表", "导出成功");
    }
    private void export(List<StoreGoodsExportVO> goodsList, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.addSheet(
                "商品列表",
                new Column[]{
                        new Column("商品名称", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoName")),
                        new Column("SKU编码", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoNo")),
                        new Column("erp编码", new SpelColumnRender<StoreGoodsExportVO>("erpNo")),
                        new Column("商品类型", new SpelColumnRender<StoreGoodsExportVO>("goodsType")),
                        new Column("囤货状态", new SpelColumnRender<StoreGoodsExportVO>("pileState")),
                        new Column("销售类型", new SpelColumnRender<StoreGoodsExportVO>("saleType")),
                        new Column("门店价", new SpelColumnRender<StoreGoodsExportVO>("marketPrice")),
                        new Column("大客户价", new SpelColumnRender<StoreGoodsExportVO>("vipPrice")),
                        new Column("特价", new SpelColumnRender<StoreGoodsExportVO>("specialPrice")),
                        new Column("批次号", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoBatchNo")),

                     //   new Column("店铺分类", new SpelColumnRender<GoodsInfoVO>("storeCateNames")),

                        new Column("商品品牌", (cell, object) -> {
                            String brandName = ((StoreGoodsExportVO) object).getBrandName();
                            if(StringUtils.isNotBlank(brandName)){
                                cell.setCellValue(brandName);
                            }else{
                                cell.setCellValue("-");
                            }
                        }),
                        new Column("上下架状态", new SpelColumnRender<StoreGoodsExportVO>("addedFlagInfo")),
                        new Column("仓库", new SpelColumnRender<StoreGoodsExportVO>("wareName")),
//                        new Column("排序", (cell, object) -> {
//                            Integer goodsSeqNum = ((StoreGoodsExportVO) object).getGoodsSeqNum();
//                            if(goodsSeqNum!=null){
//                                cell.setCellValue(goodsSeqNum);
//                            }else{
//                                cell.setCellValue("-");
//                            }
//                        }),
                },
                goodsList
        );
        excelHelper.write(outputStream);
    }

    /**
     * 导出商品列表
     *
     * @param encrypted
     * @param response
     */
    @ApiOperation(value = "导出商品列表通过商品创建时间和上架状态")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "加密", required = true)
    @RequestMapping(value = "/spu/exportGoodsbyTimeAndStaus/params/{encrypted}", method = RequestMethod.GET)
    public void exportGoodsbyTimeAndStaus(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        GoodsPageRequest goodsPageRequest = JSON.parseObject(decrypted, GoodsPageRequest.class);
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        goodsPageRequest.setPageNum(0);
        goodsPageRequest.setPageSize(1000);
        //验证传入参数
        if (goodsPageRequest.getAdded_flag()!=null && !checkAddedFlag(goodsPageRequest.getAdded_flag())){
            throw new SbcRuntimeException("AddedFlag传入的值应在0-2之间");
        }
        //日期格式 yyyy-MM-dd HH:mm:ss
        if (goodsPageRequest.getCreate_timeStart()!=null){
            checkedCreatTime(goodsPageRequest.getCreate_timeStart());
        }
        if (goodsPageRequest.getCreate_timeEnd()!=null){
            checkedCreatTime(goodsPageRequest.getCreate_timeEnd());
        }
        GoodsByCreatTimeAndStaueExportListResponse goodsInfoResponse= goodsQueryProvider.getExportGoodsByCreatetimeAndStaues(goodsPageRequest).getContext();
        try {
            if (!CollectionUtils.isEmpty(goodsInfoResponse.getGoodsExports())){
                SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
                String fileName = URLEncoder.encode("批量商品_" + fDate.format(new Date()), "UTF-8");
                response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
                EasyExcel.write(response.getOutputStream(), ExportGoodsByStatusExcel.class).sheet("商品列表").doWrite(goodsInfoResponse.getGoodsExports());
            }
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品列表", "导出商品列表通过商品创建时间和上架状态", "导出成功");

    }

    private boolean checkAddedFlag(Integer addedFlag){
        if (addedFlag==null) throw new SbcRuntimeException("方法名checkAddedFlag传入空");
        if (0<=addedFlag && addedFlag<=2){
            return true;
        }
        return false;
    }

    private void checkedCreatTime(String Creattime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
        LocalDateTime.parse(Creattime, dateTimeFormatter);
        }catch (Exception e){
            throw new SbcRuntimeException("checkedCreatTime格式验证不正确应为yyyy-MM-dd HH:mm:ss");
        }
    }




}
