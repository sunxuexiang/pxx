package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.relationgoodsimages.RelationGoodsImagesProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecDetailProvider;
import com.wanmi.sbc.goods.api.provider.spec.GoodsSpecProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsImageTypeAddRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsSpecialRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNosRequest;
import com.wanmi.sbc.goods.api.request.info.SpecialGoodsModifyRequest;
import com.wanmi.sbc.goods.api.request.info.SpecialGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByNoResponse;
import com.wanmi.sbc.goods.api.response.info.SpecialGoodsInfoResponse;
import com.wanmi.sbc.goods.api.response.info.SpecialGoodsModifyResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListGoodsRelByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.dto.SpecialGoodsAddDTO;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chenjun
 * @Date: Created In 下午5:29 2020/5/25
 * @Description: 特价商品服务
 */
@RestController
@RequestMapping("/goods")
@Api(description = "特价商品服务", tags = "BossSpecialGoodsController")
public class StoreSpecialGoodsController {
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;
    @Autowired
    private GoodsInfoProvider goodsInfoProvider;
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;
    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;
    @Autowired
    private GoodsProvider goodsProvider;
    @Autowired
    private GoodsSpecProvider goodsSpecProvider;
    @Autowired
    private GoodsSpecDetailProvider goodsSpecDetailProvider;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;
    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;
    @Autowired
    private RelationGoodsImagesProvider relationGoodsImagesProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 分页查询特价商品
     *
     * @param pageRequest 商品 {@link SpecialGoodsInfoResponse}
     * @return 特价商品分页
     */
    @ApiOperation(value = "分页查询特价商品")
    @RequestMapping(value = "/special-sku", method = RequestMethod.POST)
    public BaseResponse<SpecialGoodsInfoResponse> page(@RequestBody SpecialGoodsPageRequest pageRequest) {

        pageRequest.setGoodsStatus(BoolFlag.YES.toValue());
        pageRequest.setPageFlag(true);

        return goodsInfoQueryProvider.specialGoodsInfoPage(pageRequest);
    }

    /**
     * 特价商品的设价
     *
     * @param Request 商品 {@link SpecialGoodsInfoResponse}
     * @return 特价商品信息
     */
    @ApiOperation(value = "特价商品的设价")
    @RequestMapping(value = "/special-setGoodPrice", method = RequestMethod.POST)
    public BaseResponse<SpecialGoodsModifyResponse> setGoodPrice(@RequestBody SpecialGoodsModifyRequest Request) {
        BaseResponse<SpecialGoodsModifyResponse> specialGoodsModifyResponseBaseResponse = goodsInfoProvider.setSpecialPrice(Request);
        List<GoodsInfoVO> goodsInfoList = specialGoodsModifyResponseBaseResponse.getContext().getGoodsInfoList();
        esGoodsInfoElasticService.modifySpeciaPriceGoods(goodsInfoList);
        operateLogMQUtil.convertAndSend("特价商品服务","特价商品的设价","操作成功");
        return specialGoodsModifyResponseBaseResponse;
    }

    /**
     * 特价商品的导出
     *
     * @param encrypted 加密信息 {@link SpecialGoodsInfoResponse}
     * @return
     */
    @ApiOperation(value = "导出特价商品列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        SpecialGoodsPageRequest pageRequest = JSON.parseObject(decrypted, SpecialGoodsPageRequest.class);
        //查询导出信息

        //goodsInfoQueryProvider.specialGoodsInfoPage(pageRequest);
        pageRequest.setPageFlag(false);
        pageRequest.setGoodsStatus(1);
        BaseResponse<SpecialGoodsInfoResponse> pageResponse = goodsInfoQueryProvider.specialGoodsInfoPage(pageRequest);
        SpecialGoodsInfoResponse dataRecords = pageResponse.getContext();
//        List<Long> brandsNameList =  dataRecords.getBrands().stream().map(GoodsBrandVO::getBrandId).collect(Collectors.toList());
        //spuMap
        Map<String, List<GoodsVO>> goodsVOMap = dataRecords.getGoods().stream().collect(Collectors.groupingBy(GoodsVO::getGoodsId));
        //类别Map
        Map<Long, List<GoodsCateVO>> goodsCateVOMap = dataRecords.getCates().stream().collect(Collectors.groupingBy(GoodsCateVO::getCateId));
        //品牌Map
        Map<Long, List<GoodsBrandVO>> goodsBrandVOMap = dataRecords.getBrands().stream().collect(Collectors.groupingBy(GoodsBrandVO::getBrandId));
        List<SpecialGoodsExportVo> specialGoodsExportList = Lists.newArrayList();
        dataRecords.getGoodsInfoList().stream().forEach(goodsInfoVO -> {
            SpecialGoodsExportVo specialGoodsExportVo = new SpecialGoodsExportVo();
            GoodsVO goodsVO = goodsVOMap.get(goodsInfoVO.getGoodsId()).stream().findFirst().orElse(null);

            if (goodsVO != null){
                //spu编码
                specialGoodsExportVo.setGoodsNo(goodsVO.getGoodsNo());
                //所属商家
                specialGoodsExportVo.setSupplierName(goodsVO.getSupplierName());
            }
            //商品类目
            GoodsCateVO goodsCateVO = goodsCateVOMap.get(goodsInfoVO.getCateId()).stream().findFirst().orElse(null);
            if (goodsCateVO != null){
                specialGoodsExportVo.setCateName(goodsCateVO.getCateName());
            }
            //品牌名称
            if (Objects.nonNull(goodsInfoVO.getBrandId())){
                GoodsBrandVO goodsBrandVO = goodsBrandVOMap.get(goodsInfoVO.getBrandId()).stream().findFirst().orElse(null);
                if (goodsBrandVO != null){
                    specialGoodsExportVo.setBrandName(goodsBrandVO.getBrandName());
                }
            }
            //图片
            specialGoodsExportVo.setGoodsImg(goodsInfoVO.getGoodsInfoImg());
            //商品名称
            specialGoodsExportVo.setGoodsName(goodsInfoVO.getGoodsInfoName());
            //销售类型
            specialGoodsExportVo.setSaleTypeStr(Objects.nonNull(goodsVO.getSaleType())&&goodsVO.getSaleType()==1?"零售":"批发");
            //门店价
            specialGoodsExportVo.setMarketPrice(goodsInfoVO.getMarketPrice());
            //上下架
            specialGoodsExportVo.setAddedFlagStr(Objects.nonNull(goodsInfoVO.getAddedFlag())&&goodsInfoVO.getAddedFlag()==1?"上架":"下架");
            //特价
            specialGoodsExportVo.setSpecialPrice(goodsInfoVO.getSpecialPrice());
            //批次号
            specialGoodsExportVo.setGoodsInfoBatchNo(goodsInfoVO.getGoodsInfoBatchNo());
            //SKU
            specialGoodsExportVo.setGoodsInfoNo(goodsInfoVO.getGoodsInfoNo());
            //加入列表
            specialGoodsExportList.add(specialGoodsExportVo);


        });


        //导出信息装配到excel
        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format(" 特价商品列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(specialGoodsExportList, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("特价商品服务","导出特价商品列表","操作成功");
    }


    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<SpecialGoodsExportVo> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("商品名称", new SpelColumnRender<SpecialGoodsExportVo>("goodsName")),
                new Column("SKU编码", new SpelColumnRender<SpecialGoodsExportVo>("goodsInfoNo")),
                new Column("批次号",new SpelColumnRender<SpecialGoodsExportVo>("goodsInfoBatchNo")),
                new Column("销售类型", new SpelColumnRender<SpecialGoodsExportVo>("saleTypeStr")),
                new Column("门店价", new SpelColumnRender<SpecialGoodsExportVo>("marketPrice")),
                new Column("商品类目", new SpelColumnRender<SpecialGoodsExportVo>("cateName")),
                new Column("品牌", new SpelColumnRender<SpecialGoodsExportVo>("brandName")),
                new Column("上下架", new SpelColumnRender<SpecialGoodsExportVo>("addedFlagStr")),
                new Column("特价", new SpelColumnRender<SpecialGoodsExportVo>("specialPrice")),
                new Column("所属商家", new SpelColumnRender<SpecialGoodsExportVo>("supplierName"))

        };
        excelHelper.addSheet(" 特价商品列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

    /**
     * 同步特价仓
     *
     * @return
     */
    @ApiOperation(value = "同步特价仓")
    @GetMapping("/syn/special")
    public BaseResponse synSpecialNew() {
        //查询仓库列表
        WareHouseListResponse  wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .storeId(commonUtil.getStoreId())
                .build()).getContext();
        if(CollectionUtils.isNotEmpty(wareHouseListResponse.getWareHouseVOList())){
            List<WareHouseVO> wareHouseVOS = wareHouseListResponse.getWareHouseVOList();
            for(WareHouseVO ware : wareHouseVOS){
                Boolean moreResultFlag = true;
                int pageNo = 0;
                while(moreResultFlag){
                    int pageSize = 200;
                    InventoryQueryResponse inventoryQueryResponse =
                            requestWMSInventoryProvider.queryInventory(InventoryQueryRequest.builder()
                                    .WarehouseID(ware.getWareCode())
                                    .Lotatt04(ware.getSpPriceId())
                                    .PageNo(pageNo)
                                    .PageSize(pageSize)
                                    .build()).getContext();
                    pageNo++;
                    moreResultFlag = false;
                    if(CollectionUtils.isNotEmpty(inventoryQueryResponse.getInventoryQueryReturnVO())
                            && inventoryQueryResponse.getInventoryQueryReturnVO().size() > 0){
                        List<InventoryQueryReturnVO> inventoryQueryReturnVOS = inventoryQueryResponse.getInventoryQueryReturnVO();
                        //查询需要更新的
                        GoodsInfoByNoResponse goodsInfoByNoResponse = goodsInfoQueryProvider
                                .findSpecialGoodsByErpNos(GoodsInfoByErpNosRequest.builder()
                                        .erpGoodsInfoNos(inventoryQueryReturnVOS.stream()
                                                .map(InventoryQueryReturnVO::getSku).collect(Collectors.toList()))
                                        .build()).getContext();
                        List<String> erpGoodsInfoNos = goodsInfoByNoResponse.getGoodsInfo().stream()
                                .map(GoodsInfoVO::getErpGoodsInfoNo).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(goodsInfoByNoResponse.getGoodsInfo())){
                            //需要更新的特价商品
                            Map<String,List<InventoryQueryReturnVO>> modifyParamReqeust = inventoryQueryReturnVOS
                                    .stream().filter(i->erpGoodsInfoNos.contains(i.getSku()))
                                    .collect(Collectors.groupingBy(InventoryQueryReturnVO::getSku));
                            modifyParamReqeust.forEach((s,i)->{
                                List<SpecialGoodsAddDTO> specialGoodsAddDTOS = new ArrayList<>();
                                i.stream().forEach(in->{
                                    SpecialGoodsAddDTO specialGoodsAddDTO = new SpecialGoodsAddDTO();
                                    specialGoodsAddDTO.setStock(Objects.nonNull(in.getQty()) ? in.getQty().longValue() : 0L);
                                    specialGoodsAddDTO.setGoodsInfoBatchNo(in.getLotatt01());
                                    specialGoodsAddDTOS.add(specialGoodsAddDTO);
                                });
                                goodsProvider.modifySpecialGoods(GoodsSpecialRequest.builder()
                                        .erpGoodsInfoNo(s)
                                        .wareId(ware.getWareId())
                                        .specialGoodsList(specialGoodsAddDTOS)
                                        .build());
                            });
                        }else{
                            //新增
                            Map<String,List<InventoryQueryReturnVO>> addParamReqeust = inventoryQueryReturnVOS
                                    .stream().filter(i->!erpGoodsInfoNos.contains(i.getSku()))
                                    .collect(Collectors.groupingBy(InventoryQueryReturnVO::getSku));
                            addParamReqeust.forEach((s,i)->{
                                List<SpecialGoodsAddDTO> specialGoodsAddDTOS = new ArrayList<>();
                                i.stream().forEach(in->{
                                    SpecialGoodsAddDTO specialGoodsAddDTO = new SpecialGoodsAddDTO();
                                    specialGoodsAddDTO.setGoodsInfoBatchNo(in.getLotatt01());
                                    specialGoodsAddDTO.setStock(Objects.nonNull(in.getQty()) ? in.getQty().longValue() : 0L);
                                    specialGoodsAddDTOS.add(specialGoodsAddDTO);
                                });
                                goodsProvider.addSpecial(GoodsSpecialRequest.builder()
                                        .erpGoodsInfoNo(s)
                                        .wareId(ware.getWareId())
                                        .specialGoodsList(specialGoodsAddDTOS)
                                        .build());
                            });
                        }
                    }else{
                        moreResultFlag = false;
                    }

                }
            }
        }
        esGoodsInfoElasticService.modifySpecialGoods();
        operateLogMQUtil.convertAndSend("特价商品服务","同步特价仓","操作成功");
        return BaseResponse.SUCCESSFUL();
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
        operateLogMQUtil.convertAndSend("特价商品服务","商品合成图片批量接口","操作成功");
        return BaseResponse.SUCCESSFUL();
    }




}
