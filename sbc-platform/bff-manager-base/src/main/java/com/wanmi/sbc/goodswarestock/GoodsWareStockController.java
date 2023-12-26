package com.wanmi.sbc.goodswarestock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateByStoreCateIdRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreCateIdAndIsHaveSelfRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByStoreCateIdAndIsHaveSelfResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsBatchNoDTO;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


/**
 * @author zhangwenchang
 */
@Slf4j
@Api(description = "sku分仓库存表管理API", tags = "GoodsWareStockController")
@RestController
@RequestMapping(value = "/goods/ware/stock")
public class GoodsWareStockController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("classpath:supplier_goods_stock_export_template.xls")
    private Resource templateFile;

    private static final int pageSize = 500;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private GoodsWareStockProvider goodsWareStockProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询sku分仓库存表")
    @PostMapping("/page")
    public BaseResponse<GoodsWareStockPageResponse> getPage(@RequestBody @Valid GoodsWareStockPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.setStoreId(commonUtil.getStoreIdWithDefault());
        pageReq.putSort("id", "desc");
        //补充店铺分类
        if (pageReq.getCateId() != null) {
            BaseResponse<StoreCateListByStoreCateIdAndIsHaveSelfResponse> cateBaseResponse =
                    storeCateQueryProvider.listByStoreCateIdAndIsHaveSelf(new StoreCateListByStoreCateIdAndIsHaveSelfRequest(pageReq.getCateId(), false));
            StoreCateListByStoreCateIdAndIsHaveSelfResponse storeCateListByStoreCateIdAndIsHaveSelfResponse =
                    cateBaseResponse.getContext();
            if (Objects.nonNull(storeCateListByStoreCateIdAndIsHaveSelfResponse)) {
                List<StoreCateVO> storeCateVOList = storeCateListByStoreCateIdAndIsHaveSelfResponse.getStoreCateVOList();
                List<Long> cateIdList = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(storeCateVOList)) {
                    cateIdList = storeCateVOList.stream().map(StoreCateVO::getStoreCateId).collect(Collectors.toList());
                } else {
                    cateIdList.add(pageReq.getCateId());
                }
                pageReq.setCateIds(cateIdList);
            }
        }
        return goodsWareStockQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询sku分仓库存表")
    @PostMapping("/list")
    public BaseResponse<GoodsWareStockListResponse> getList(@RequestBody @Valid GoodsWareStockListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setStoreId(commonUtil.getStoreIdWithDefault());
        listReq.putSort("id", "desc");
        return goodsWareStockQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询sku分仓库存表")
    @GetMapping("/{id}")
    public BaseResponse<GoodsWareStockByIdResponse> getById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsWareStockByIdRequest idReq = new GoodsWareStockByIdRequest();
        idReq.setId(id);
        idReq.setStoreId(commonUtil.getStoreIdWithDefault());
        return goodsWareStockQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增sku分仓库存表")
    @PostMapping("/add")
    public BaseResponse<GoodsWareStockAddResponse> add(@RequestBody @Valid GoodsWareStockAddRequest addReq) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "新增sku分仓库存表","新增sku分仓库存表：sku编码" + (Objects.nonNull(addReq) ? addReq.getGoodsInfoNo() : ""));
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setStoreId(commonUtil.getStoreIdWithDefault());
        addReq.setCreatePerson(commonUtil.getOperatorId());
        return goodsWareStockProvider.add(addReq);
    }

    @ApiOperation(value = "修改sku分仓库存表")
    @PutMapping("/modify")
    public BaseResponse<GoodsWareStockModifyResponse> modify(@RequestBody @Valid GoodsWareStockModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "修改sku分仓库存表","修改sku分仓库存表：sku编码" + (Objects.nonNull(modifyReq) ? modifyReq.getGoodsInfoNo() : ""));
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        return goodsWareStockProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除sku分仓库存表")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "根据id删除sku分仓库存表","根据id删除sku分仓库存表：id" + id);
        GoodsWareStockDelByIdRequest delByIdReq = new GoodsWareStockDelByIdRequest();
        delByIdReq.setId(id);
        return goodsWareStockProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除sku分仓库存表")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid GoodsWareStockDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "根据idList批量删除sku分仓库存表","根据idList批量删除sku分仓库存表");
        return goodsWareStockProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出sku分仓库存表列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        GoodsWareStockListRequest listReq = JSON.parseObject(decrypted, GoodsWareStockListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("商品库存列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            listReq.setStoreId(commonUtil.getStoreId());
            //补充店铺分类
            if (listReq.getCateId() != null) {
                BaseResponse<StoreCateListByStoreCateIdAndIsHaveSelfResponse> cateBaseResponse =
                        storeCateQueryProvider.listByStoreCateIdAndIsHaveSelf(new StoreCateListByStoreCateIdAndIsHaveSelfRequest(listReq.getCateId(), false));
                StoreCateListByStoreCateIdAndIsHaveSelfResponse storeCateListByStoreCateIdAndIsHaveSelfResponse =
                        cateBaseResponse.getContext();
                if (Objects.nonNull(storeCateListByStoreCateIdAndIsHaveSelfResponse)) {
                    List<StoreCateVO> storeCateVOList = storeCateListByStoreCateIdAndIsHaveSelfResponse.getStoreCateVOList();
                    List<Long> cateIdList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(storeCateVOList)) {
                        cateIdList = storeCateVOList.stream().map(StoreCateVO::getStoreCateId).collect(Collectors.toList());
                    } else {
                        cateIdList.add(listReq.getCateId());
                    }
                    listReq.setCateIds(cateIdList);
                }
            }
            exportDataList(response.getOutputStream(), listReq);
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "导出sku分仓库存表列表","操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(OutputStream outputStream, GoodsWareStockListRequest listReq) throws IOException {
        Column[] columns = {
                new Column("商品名称", new SpelColumnRender<GoodsWareStockVO>("goodsInfoName")),
                new Column("分类", new SpelColumnRender<GoodsWareStockVO>("cateId")),
                new Column("品牌", new SpelColumnRender<GoodsWareStockVO>("brandId")),
                new Column("规格", new SpelColumnRender<GoodsWareStockVO>("stock")),
                new Column("sku编码", new SpelColumnRender<GoodsWareStockVO>("goodsInfoNo")),
                new Column("仓库", new SpelColumnRender<GoodsWareStockVO>("wareName")),
                new Column("库存", new SpelColumnRender<GoodsWareStockVO>("stock"))
        };
        addSheet(columns, outputStream, listReq);
    }

    public void addSheet(Column[] columns, OutputStream op, GoodsWareStockListRequest goodsWareStockListRequest) throws IOException {
        Resource file = templateFile;
        InputStream is = null;
        try {
            is = file.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            //根据店铺获取品牌
            ContractBrandListRequest contractBrandListRequest = new ContractBrandListRequest();
            contractBrandListRequest.setStoreId(commonUtil.getStoreId());
            List<ContractBrandVO> contractBrandVOList = contractBrandQueryProvider.list(contractBrandListRequest).getContext().getContractBrandVOList();
            //根据店铺获取店铺分类
            StoreCateListByStoreIdRequest storeCateListByStoreIdRequest = new StoreCateListByStoreIdRequest();
            storeCateListByStoreIdRequest.setStoreId(commonUtil.getStoreId());
            List<StoreCateResponseVO> storeCates = storeCateQueryProvider.listByStoreId(storeCateListByStoreIdRequest)
                    .getContext().getStoreCateResponseVOList();
            //获取仓库信息
            List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                    .storeId(commonUtil.getStoreId()).delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();
            //填放分类数据
            Sheet storeCateSheet = workbook.getSheetAt(3);
            int storeCateSize = storeCates.size();
            for (int i = 0; i < storeCateSize; i++) {
                StoreCateResponseVO cate = storeCates.get(i);
                storeCateSheet.createRow(i).createCell(0).setCellValue(String.valueOf(cate.getStoreCateId()).concat("_").concat(cate.getCateName()));
            }
            //品牌
            Sheet brandSheet = workbook.getSheetAt(2);
            int brandSize = contractBrandVOList.size();
            for (int i = 0; i < brandSize; i++) {
                ContractBrandVO brand = contractBrandVOList.get(i);
                if (Objects.nonNull(brand.getGoodsBrand())) {
                    brandSheet.createRow(i).createCell(0).setCellValue(String.valueOf(brand.getGoodsBrand().getBrandId()).concat("_").concat(brand.getGoodsBrand().getBrandName()));
                }
            }
            //仓库信息
            Sheet wareHouseSheet = workbook.getSheetAt(1);
            int wareHouseSize = wareHouseVOList.size();
            for (int i = 0; i < wareHouseSize; i++) {
                WareHouseVO wareHouse = wareHouseVOList.get(i);
                wareHouseSheet.createRow(i).createCell(0).setCellValue(String.valueOf(wareHouse.getWareId()).concat("_").concat(wareHouse.getWareName()));
            }
            GoodsWareStockPageRequest goodsWareStockPageRequest = KsBeanUtil.convert(goodsWareStockListRequest, GoodsWareStockPageRequest.class);
            goodsWareStockPageRequest.setPageSize(pageSize);
            MicroServicePage<GoodsWareStockPageVO> goodsWareStockPageVOS = goodsWareStockQueryProvider.page(goodsWareStockPageRequest).getContext().getGoodsWareStockVOPage();
            HSSFSheet sheet = workbook.getSheetAt(0);
            long total = goodsWareStockPageVOS.getTotal();
            //最多支持导出1000条
            if (total > 1000) {
                total = 1000;
            }
            int num = (int) Math.ceil((float) total / (float) pageSize);
            int rowIndex = 1;
            for (int pageNum = 0; pageNum < num; pageNum++) {
                if (pageNum > 0) {
                    goodsWareStockPageRequest.setPageNum(pageNum);
                    goodsWareStockPageVOS = goodsWareStockQueryProvider.page(goodsWareStockPageRequest).getContext().getGoodsWareStockVOPage();
                }
                List<GoodsWareStockPageVO> goodsWareStockPageVOList = goodsWareStockPageVOS.getContent();
                for (GoodsWareStockPageVO data : goodsWareStockPageVOList) {
                    int cellIndex = 0;
                    HSSFRow row = sheet.createRow(rowIndex++);
                    for (Column column : columns) {
                        HSSFCell cell = row.createCell(cellIndex++);
                        if (column.getHeader().equals("商品名称")) {
                            cell.setCellValue(data.getGoodsInfoName());
                        } else if (column.getHeader().equals("分类")) {
                            cell.setCellValue(data.getCateId() + "_" + data.getCateName());
                        } else if (column.getHeader().equals("品牌")) {
                            cell.setCellValue(getBrandInfo(data));
                        } else if (column.getHeader().equals("规格")) {
                            cell.setCellValue(StringUtils.isNotBlank(data.getSpecName()) ? data.getSpecName() : "无");
                        } else if (column.getHeader().equals("sku编码")) {
                            cell.setCellValue(data.getGoodsInfoNo());
                        } else if (column.getHeader().equals("仓库")) {
                            cell.setCellValue(data.getWareId() + "_" + data.getWareName());
                        } else if (column.getHeader().equals("库存")) {
                            cell.setCellValue(data.getStock());
                        }
                    }
                }
            }
            workbook.write(op);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            is.close();
            op.close();
        }
    }

    private String getBrandInfo(GoodsWareStockPageVO goodsWareStockPageVO) {
        String brandInfoStr = "无";
        if (StringUtils.isNotBlank(goodsWareStockPageVO.getBrandId()) && StringUtils.isNotBlank(goodsWareStockPageVO.getBrandName())) {
            brandInfoStr = goodsWareStockPageVO.getBrandId() + "_" + goodsWareStockPageVO.getBrandName();
        }
        return brandInfoStr;
    }

    /**
     * 递归查询所有店铺分类所有父分类名称
     *
     * @param storeCateId
     * @return
     */
    private String queryStoreCate(Long storeCateId) {
        StoreCateByStoreCateIdRequest storeCateByStoreCateIdRequest = new StoreCateByStoreCateIdRequest();
        storeCateByStoreCateIdRequest.setStoreCateId(storeCateId);
        StoreCateResponseVO storeCateResponseVO = storeCateQueryProvider.getByStoreCateId(storeCateByStoreCateIdRequest).getContext().getStoreCateResponseVO();
        if (storeCateResponseVO.getCateParentId() != 0) {
            String queryParentCate = queryStoreCate(storeCateResponseVO.getCateParentId());
            return queryParentCate + "-" + storeCateResponseVO.getCateName();
        }
        return storeCateResponseVO.getCateName();
    }

    @ApiOperation(value = "列表查询sku分仓库存表")
    @PostMapping("/list/init")
    public BaseResponse<GoodsWareStockInitListResponse> getListInit(@RequestBody @Valid GoodsWareStockListRequest listReq) {
        listReq.setStoreId(commonUtil.getStoreIdWithDefault());
        listReq.setDelFlag(DeleteFlag.NO);
        return goodsWareStockQueryProvider.initList(listReq);
    }

    /**
     * 同步获取最新商品库存信息
     */
    @ApiOperation(value = "同步获取最新商品库存信息")
    @RequestMapping(value = "/syncStock", method = RequestMethod.POST)
    public BaseResponse<GoodsWareStockSyncResponse> syncStock(@RequestBody GoodsWareStockSyncRequest request) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "同步获取最新商品库存信息","同步获取最新商品库存信息");
        Long storeId = commonUtil.getStoreId();
        try {

            List<GoodsInfoVO> goodsInfoVOS = new ArrayList<>();
            request.getGoodsInfoIds().forEach(goodsInfoId -> {
                GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                        .goodsInfoId(goodsInfoId).storeId(storeId).build()).getContext();
                BigDecimal addStep = response.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);
                GoodsInfoVO infoVO = new GoodsInfoVO().setGoodsInfoId(goodsInfoId);
                List<GoodsWareStockVO> goodsWareStockVOS = new ArrayList<>();
                //查询库存信息
                String goodsInfoNo = goodsWareStockQueryProvider.
                        list(new GoodsWareStockListRequest().setGoodsInfoId(goodsInfoId)).
                        getContext().getGoodsWareStockVOList().get(0).getGoodsInfoNo();
                //查询WMS库存信息
                List<InventoryQueryReturnVO> inventoryList = requestWMSInventoryProvider.
                        queryInventory(new InventoryQueryRequest().setSKU(goodsInfoNo)).
                        getContext().getInventoryQueryReturnVO();
                inventoryList.forEach(inventory -> {
                    BigDecimal stock =  inventory.getStockNum().divide(addStep,0,
                            BigDecimal.ROUND_DOWN);
                    GoodsWareStockVO vo = new GoodsWareStockVO();
                    vo.setGoodsInfoId(goodsInfoId);
                    vo.setStock(stock);
                    //查询仓库信息
                    WareHouseVO wareHouse = wareHouseQueryProvider.
                            list(new WareHouseListRequest().setWareCode(inventory.getWarehouseId())).
                            getContext().getWareHouseVOList().get(0);
                    vo.setWareName(wareHouse.getWareName());
                    vo.setWareId(wareHouse.getWareId());
                    goodsWareStockVOS.add(vo);
                });

                infoVO.setGoodsWareStocks(goodsWareStockVOS);
                goodsInfoVOS.add(infoVO);
            });
            return BaseResponse.success(GoodsWareStockSyncResponse.builder().goodsInfoVOS(goodsInfoVOS).build());
        } catch (Exception e) {
            throw new SbcRuntimeException(GoodsErrorCode.GOODS_ASYNC_ERROR, e);
        }
    }

    /**
     * 同步获取最新商品库存信息
     */
    @ApiOperation(value = "同步获取最新商品库存信息")
    @RequestMapping(value = "/syncStockNew", method = RequestMethod.POST)
    public BaseResponse<GoodsWareStockSyncNewResponse> syncErpStock(@RequestBody GoodsWareStockSyncNewRequest request) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "同步获取最新商品库存信息","同步获取最新商品库存信息：sku编码" + (Objects.nonNull(request) ? request.getGoodsInfoId() : ""));
        Long storeId = commonUtil.getStoreId();
        GoodsInfoByIdResponse goodsInfoByIdResponse = goodsInfoQueryProvider.getById(GoodsInfoByIdRequest.builder()
                .goodsInfoId(request.getGoodsInfoId()).storeId(storeId).build()).getContext();
        BigDecimal addStep = goodsInfoByIdResponse.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);

        //获取所有的分仓信息
        List<WareHouseVO> wareHouseVOS = commonUtil.queryAllWareHouses();
        Map<Long,WareHouseVO> wareCodeHouseMap = wareHouseVOS.stream().collect(Collectors.toMap(WareHouseVO::getWareId,g->g));

        //查询系统的库存
        GoodsWareStockListResponse stockListResponse = goodsWareStockQueryProvider
                .list(GoodsWareStockListRequest.builder()
                        .goodsInfoId(request.getGoodsInfoId())
                        .build()).getContext();
        //查询WMS库存信息
        //查询WMS 需去除前方仓库编码前缀

        String[] split = request.getErpGoodsInfoNo().split("-");
        InventoryQueryResponse response = requestWMSInventoryProvider.
                queryInventoryBySku(new InventoryQueryRequest()
                        .setSKU(split[split.length-1])
                        .setWareHouseCode(wareHouseVOS.stream().map(WareHouseVO::getSelfErpId).collect(Collectors.toList()))
                ).getContext();

        //查询商品锁定库存
        List<String> goodsIdList = Lists.newArrayList();
        goodsIdList.add(goodsInfoByIdResponse.getGoodsId());
        GoodsListByIdsResponse goodsListByIdsResponse = goodsQueryProvider.listByGoodsIdsNoValid(GoodsListByIdsRequest.builder().goodsIds(goodsIdList).build()).getContext();

        if(Objects.nonNull(stockListResponse) && CollectionUtils.isNotEmpty(stockListResponse.getGoodsWareStockVOList())){
            List<GoodsWareStockVO> stockVOS = stockListResponse.getGoodsWareStockVOList();
            if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getInventoryQueryReturnVO())){
                List<InventoryQueryReturnVO> inventoryQueryReturnVOS = response.getInventoryQueryReturnVO();
                Map<String ,InventoryQueryReturnVO> skuInventorys = inventoryQueryReturnVOS.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getWarehouseId,g->g));
                stockVOS.forEach(s->{
                    WareHouseVO wareHouseVO = wareCodeHouseMap.get(s.getWareId());
                    if(Objects.nonNull(wareHouseVO)){
                        InventoryQueryReturnVO inventoryQueryReturnVO = skuInventorys.get(wareHouseVO.getWareCode());
                        s.setWareName(wareHouseVO.getWareName());

                        if(Objects.nonNull(inventoryQueryReturnVO) && inventoryQueryReturnVO.getStockNum().compareTo(BigDecimal.ZERO)==1){
                            BigDecimal stock =
                                   inventoryQueryReturnVO.getStockNum().divide(addStep,2,
                                            BigDecimal.ROUND_DOWN);
                            /**获取锁定库存*/
                            if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                                List<GoodsVO> goodsVOList = goodsListByIdsResponse.getGoodsVOList();
                                GoodsVO goodsVO = goodsVOList.stream().filter(g -> g.getGoodsId().equals(s.getGoodsId())).findFirst().orElse(null);
                                if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                                    stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock()));
                                }
                            }
                            s.setStock(stock);
                            if(stock.compareTo(BigDecimal.ZERO) < 1){
                                s.setStock(BigDecimal.ZERO);
                            }
                        }
                    }
                });
            }
            stockVOS.forEach(s->{
                wareHouseVOS.forEach(w->{
                    if(s.getWareId().equals(w.getWareId())){
                        s.setWareName(w.getWareName());
                    }
                });
            });
            log.info("stockVOS: {}", JSONObject.toJSONString(stockVOS));
            return BaseResponse.success(GoodsWareStockSyncNewResponse.builder()
                    .goodsWareStockVOS(stockVOS)
                    .build());
        }
        throw new RuntimeException(GoodsErrorCode.GOODS_ASYNC_ERROR);
    }
    /**
     * 测试库存同步接口
     */
    @ApiOperation(value = "测试库存同步接口")
    @GetMapping("/asyncGoodsWareStock/{num}")
    public BaseResponse asyncGoodsWareStock(@PathVariable Integer num){

        long startTime = System.currentTimeMillis();
        log.info("测试库存同步开始========================= {}", JSONObject.toJSONString(System.currentTimeMillis()));

        GoodsInfoPageRequest request = GoodsInfoPageRequest.builder().goodsInfoType(0).delFlag(0).addedFlag(1).build();
        request.setPageSize(num);
        int max=6,min=1;
        request.setPageNum(0);
        GoodsInfoPageResponse infoPageResponse = goodsInfoQueryProvider.page(request).getContext();
        List<GoodsInfoVO> goodsInfoVOList = infoPageResponse.getGoodsInfoPage().getContent();
        List<InventoryQueryReturnVO> inventory = Lists.newArrayList();
        goodsInfoVOList.forEach(g->{
            long stock = (long) (Math.random() * 100 + 1);
            g.setStock(BigDecimal.valueOf(stock));
            //设置wms库存数
            InventoryQueryReturnVO returnVO = new InventoryQueryReturnVO();
            returnVO.setSku(g.getErpGoodsInfoNo());
            //批次号
            returnVO.setLotatt01(g.getGoodsInfoBatchNo());
            returnVO.setStockNum(BigDecimal.valueOf(stock));
            inventory.add(returnVO);
        });

        if(org.apache.commons.collections4.CollectionUtils.isEmpty(goodsInfoVOList)){
            return BaseResponse.SUCCESSFUL();
        }

        ConcurrentMap<String, InventoryQueryReturnVO> map = inventory.stream().collect(Collectors.toConcurrentMap(InventoryQueryReturnVO::getSku, g -> g, (o,n)->n));

        //更新货品的批次号字段
        log.info("1. ========================== 更新批次号：===================");
        List<GoodsBatchNoDTO> goodsBatchNoDTOS = new ArrayList<>();
        goodsInfoVOList.forEach(gi->{
            InventoryQueryReturnVO inventoryQueryReturnVO = map.get(gi.getErpGoodsInfoNo());
            if (Objects.nonNull(inventoryQueryReturnVO)){
                goodsBatchNoDTOS.add(new GoodsBatchNoDTO(gi.getGoodsInfoId(),inventoryQueryReturnVO.getLotatt01()));
            }
        });
        goodsInfoProvider.batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest.builder().goodsBatchNoDTOS(goodsBatchNoDTOS).build());

        log.info(String.format("更新批次号->花费%s毫秒", (System.currentTimeMillis() - startTime)));

        log.info("2. ========================== 更新库存： ====================");
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoNos(GoodsWareStockByGoodsForNoRequest.builder()
                .goodsForIdList(goodsInfoVOList.stream().map(g->g.getGoodsInfoNo()).collect(Collectors.toList()))
                .wareId(1L)
                .build()).getContext().getGoodsWareStockVOList();
        //设置erpNo零时转换
        ConcurrentMap<String,GoodsInfoVO> infoVOMap = goodsInfoVOList.stream().collect(Collectors.toConcurrentMap(GoodsInfoVO::getGoodsInfoId,g->g));
        goodsWareStockVOList.forEach(g->{
            if (Objects.nonNull(infoVOMap.get(g.getGoodsInfoId()))){
                g.setErpNo(infoVOMap.get(g.getGoodsInfoId()).getErpGoodsInfoNo());
            }
        });
        List<GoodsWareStockUpdateRequest> oldRequest =  KsBeanUtil.convertList(goodsWareStockVOList,GoodsWareStockUpdateRequest.class);

        /**查询锁定库存*/
        GoodsListByIdsResponse goodsListByIdsResponse = goodsQueryProvider.listByGoodsIdsNoValid(GoodsListByIdsRequest.builder()
                .goodsIds(goodsWareStockVOList.stream().map(gs -> gs.getGoodsId()).collect(Collectors.toList()))
                .build())
                .getContext();

        //新增记录
        for (GoodsWareStockVO inner : goodsWareStockVOList) {
            Optional<InventoryQueryReturnVO> optional = inventory.stream().filter(i -> i.getSku().equals(inner.getErpNo())).findFirst();
            if (optional.isPresent()) {
                BigDecimal stock = optional.get().getStockNum().setScale(2, BigDecimal.ROUND_DOWN);

                if(Objects.nonNull(goodsListByIdsResponse) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                    GoodsVO goodsVO = goodsListByIdsResponse.getGoodsVOList().stream().filter(g -> g.getGoodsId().equals(inner.getGoodsId())).findFirst().orElse(null);
                    if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                        stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock()));
                    }
                }
                inner.setStock(stock);
                if (stock.compareTo(BigDecimal.ZERO) == -1) {
                    inner.setStock(BigDecimal.ZERO);
                } else {
                    inner.setStock(BigDecimal.ZERO);
                }
            } else {
                inner.setStock(BigDecimal.ZERO);
            }
        }

        //更新库存中间表
        List<GoodsWareStockUpdateRequest> goodsWareStockAddListRequests = KsBeanUtil.convertList(goodsWareStockVOList, GoodsWareStockUpdateRequest.class);
        goodsWareStockProvider.updateList(GoodsWareStockUpdateListRequest.builder()
                .goodsWareStockAddRequestList(goodsWareStockAddListRequests)
                .goodsWareStockOldRequestList(oldRequest).build());

        logger.info(String.format("测试库存同步结束->花费%s毫秒", (System.currentTimeMillis() - startTime)));
        return BaseResponse.SUCCESSFUL();
    }

}
