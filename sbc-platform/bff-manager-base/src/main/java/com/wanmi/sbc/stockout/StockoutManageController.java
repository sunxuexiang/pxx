package com.wanmi.sbc.stockout;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailListByCustomerIdsRequest;
import com.wanmi.sbc.customer.bean.vo.StockOutCustomerVO;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageDelByIdRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageListRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManagePageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.goods.StoreGoodsExportListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManagePageResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.util.CityUtil;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Api(description = "缺货管理管理API", tags = "StockoutManageController")
@RestController
@RequestMapping(value = "/stockoutmanage")
@Slf4j
public class StockoutManageController {

    @Autowired
    private StockoutManageQueryProvider stockoutManageQueryProvider;

    @Autowired
    private StockoutManageProvider stockoutManageProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsProvider goodsProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    @Autowired
    private GoodsQueryProvider goodsQueryProvider;
    private static List<String> specialRegion = Lists.newArrayList("810000", "820000", "710000");

    @Resource
    private CommonUtil commonUtil;

    @ApiOperation(value = "分页查询缺货管理")
    @PostMapping("/page")
    public BaseResponse<StockoutManagePageResponse> getPage(@RequestBody @Valid StockoutManagePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.setSource(1);
        pageReq.putSort("stockoutId", "desc");
        pageReq.setStoreId(commonUtil.getStoreId());
        BaseResponse<StockoutManagePageResponse> page = stockoutManageQueryProvider.page(pageReq);
        if (CollectionUtils.isNotEmpty(page.getContext().getStockoutManageVOPage().getContent())) {
            List<StockoutManageVO> content = page.getContext().getStockoutManageVOPage().getContent();
            Set<String> customerIds = new HashSet<>();
            for (StockoutManageVO inner : content) {
                Set<String> collect = inner.getStockoutDetailList().stream().map(StockoutDetailVO::getCustomerId).collect(Collectors.toSet());
                customerIds.addAll(collect);
            }
            if (CollectionUtils.isNotEmpty(customerIds)) {
                CustomerDetailListByCustomerIdsRequest request = new CustomerDetailListByCustomerIdsRequest();
                request.setCustomerIds(new ArrayList<>(customerIds));
                List<StockOutCustomerVO> customerDetailVOList = customerDetailQueryProvider.stockOutCustomerInfo(request).getContext().getCustomerDetailVOList();
                Map<String, StockOutCustomerVO> stockOutMap = customerDetailVOList.stream().collect(Collectors.toMap(StockOutCustomerVO::getCustomerId, g -> g));
                for (StockoutManageVO inner : content) {
                    for (StockoutDetailVO detailVO : inner.getStockoutDetailList()) {
                        StockOutCustomerVO stockOutCustomerVO = stockOutMap.get(detailVO.getCustomerId());
                        if (Objects.nonNull(stockOutCustomerVO)) {
                            detailVO.setCustomerName(stockOutCustomerVO.getCustomerName());
                            detailVO.setEmployeeName(stockOutCustomerVO.getEmployeeName());
                        }
                    }
                }
            }
        }
        return page;
    }

    @ApiOperation(value = "分页查询缺货记录-运营后台查询")
    @PostMapping("/getOperationPage")
    public BaseResponse<StockoutManagePageResponse> pagingQueryOperation(@RequestBody @Valid StockoutManagePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.setSource(2);
        pageReq.putSort("stockoutId", "desc");
        pageReq.setStoreId(commonUtil.getStoreId());
        try {

            BaseResponse<StockoutManagePageResponse> page = stockoutManageQueryProvider.page(pageReq);
            fillAttachInfo(page);
            return page;
        } catch (Exception e) {
            String error = Throwables.getStackTraceAsString(e);
            log.error("分页查询缺货记录异常:", error);
        }
        return null;
    }

    @ApiOperation(value = "导出缺货管理列表")
    @GetMapping("/exportOperationData/{encrypted}")
    public void exportOperationData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        StockoutManageListRequest listReq = JSON.parseObject(decrypted, StockoutManageListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setSource(2);
        List<StockoutManageVO> dataRecords = stockoutManageQueryProvider.list(listReq).getContext().getStockoutManageVOList();
        initAttachProperties(dataRecords);
        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("缺货记录列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportOperatingDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("缺货管理管理", "导出缺货管理列表", "操作成功");
    }


    private BaseResponse<StockoutManagePageResponse> fillAttachInfo(BaseResponse<StockoutManagePageResponse> page) {
        if (CollectionUtils.isNotEmpty(page.getContext().getStockoutManageVOPage().getContent())) {
            List<String> goodInfoIds = page.getContext().getStockoutManageVOPage().getContent().stream().map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
            GoodsInfoListByIdsRequest goodsInfoRequest = new GoodsInfoListByIdsRequest();
            goodsInfoRequest.setGoodsInfoIds(goodInfoIds);
            GoodsInfoListByConditionRequest conditionRequest = new GoodsInfoListByConditionRequest();
            conditionRequest.setGoodsInfoIds(goodInfoIds);
            log.error("商品缺货管理调用商品接口请求参数对象商品ids:" + goodsInfoRequest.getGoodsInfoIds());
            BaseResponse<GoodsInfoListByConditionResponse> goodsInfoResponse = goodsInfoQueryProvider.listByCondition(conditionRequest);
            log.error("商品缺货管理调用商品接口返回数据内容:" + goodsInfoResponse.getContext().getGoodsInfos());
            List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getContext().getGoodsInfos();

            //获取商品附加属性
            assignmentGoodsAttachProperties(page.getContext().getStockoutManageVOPage().getContent(), goodsInfos);

            List<Long> wareIds = page.getContext().getStockoutManageVOPage().getContent().stream().map(x -> x.getWareId()).collect(Collectors.toList());
            WareHouseListRequest wareHouseListRequest = new WareHouseListRequest();
            wareHouseListRequest.setWareIdList(wareIds);
            BaseResponse<WareHouseListResponse> wareResponse = wareHouseQueryProvider.list(wareHouseListRequest);

            //获取仓库信息
            assignmentWareInfo(page.getContext().getStockoutManageVOPage().getContent(), wareResponse.getContext().getWareHouseVOList());

        }

        return page;
    }

    private void assignmentWareInfo(List<StockoutManageVO> content, List<WareHouseVO> wareHouseVOList) {
        for (StockoutManageVO stockoutManageVO : content) {
            WareHouseVO wareHouseVO = getWareHouseInfo(stockoutManageVO.getWareId(), wareHouseVOList);
            if (wareHouseVO != null) {
                stockoutManageVO.setWareName(wareHouseVO.getWareName());
            }
        }
    }

    private WareHouseVO getWareHouseInfo(Long wareId, List<WareHouseVO> wareHouseVOList) {
        for (WareHouseVO wareHouseVO : wareHouseVOList) {
            if (wareHouseVO.getWareId().equals(wareId)) {
                return wareHouseVO;
            }
        }
        return null;
    }

    private void assignmentGoodsAttachProperties(List<StockoutManageVO> content, List<GoodsInfoVO> goodsInfos) {
        for (StockoutManageVO stockoutManageVO : content) {
            stockoutManageVO.toSetReplenishmentFlag();
            GoodsInfoVO goodsInfoVO = getGoodsInfo(stockoutManageVO.getGoodsInfoId(), goodsInfos);
            if (goodsInfoVO != null) {
                stockoutManageVO.setGoodsType(goodsInfoVO.getGoodsInfoType());
                stockoutManageVO.setWareName(goodsInfoVO.getWareName());
                stockoutManageVO.setSaleType(goodsInfoVO.getSaleType());
              /*  stockoutManageVO.setErpGoodsInfoNo(goodsInfoVO.getErpGoodsInfoNo());
                stockoutManageVO.setGoodsInfoImg(goodsInfoVO.getGoodsInfoImg());
                stockoutManageVO.setAddedFlag(goodsInfoVO.getAddedFlag()); 20200829 修改取值缺货记录表的数据，搜索与查询保持一致 xjl*/
                stockoutManageVO.toSetAddedFlagName();
                stockoutManageVO.toSetSaleName();
            }
        }
    }

    private GoodsInfoVO getGoodsInfo(String goodsInfoId, List<GoodsInfoVO> goodsInfos) {
        for (GoodsInfoVO goodsInfoVO : goodsInfos) {
            if (goodsInfoVO.getGoodsInfoId().equals(goodsInfoId)) {
                return goodsInfoVO;
            }
        }
        return null;
    }


    @ApiOperation(value = "根据id删除缺货管理")
    @DeleteMapping("/{stockoutId}")
    public BaseResponse deleteById(@PathVariable String stockoutId) {
        if (stockoutId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("缺货管理管理", "根据id删除缺货管理", "根据id删除缺货管理：缺货id" + stockoutId);
        StockoutManageDelByIdRequest delByIdReq = new StockoutManageDelByIdRequest();
        delByIdReq.setStockoutId(stockoutId);
        return stockoutManageProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除缺货管理")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid StockoutManageDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("缺货管理管理", "根据idList批量删除缺货管理", "根据idList批量删除缺货管理");
        return stockoutManageProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出缺货管理列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        StockoutManageListRequest listReq = JSON.parseObject(decrypted, StockoutManageListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.setSource(1);
        List<StockoutManageVO> dataRecords = stockoutManageQueryProvider.list(listReq).getContext().getStockoutManageVOList();

        Map<String, CityCode> cityName = CityUtil.getCityNameMap();
        Map<String, CityCode> provinces = CityUtil.getProvincesMap();
        if (Objects.nonNull(cityName) && Objects.nonNull(provinces)) {
            dataRecords.forEach(param -> {
                String[] split = param.getStockoutCity().split(",");
                StringBuffer sb = new StringBuffer();
                for (String inner : split) {
                    sb.append(parseAreaName(inner, cityName, provinces));
                    if (ArrayUtils.indexOf(split, inner) < split.length - 1) {
                        sb.append(",");
                    }
                }
                param.setStockoutCity(sb.toString());
            });

        }
        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("缺货管理列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("缺货管理管理", "导出缺货管理列表", "操作成功");
    }



    /**
     * 导出商品列表
     *
     * @param encrypted
     * @param response
     */

    @ApiOperation(value = "导出商品列表")
    @GetMapping("/export/params/{encrypted}")
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
        //测试导出，容易出问题
        goodsPageRequest.setPageSize(5000);
        goodsPageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        goodsPageRequest.setStoreId(commonUtil.getStoreId());
        goodsPageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        goodsPageRequest.putSort("createTime", SortType.DESC.toValue());
        goodsPageRequest.putSort("goodsId", SortType.ASC.toValue());

        StoreGoodsExportListResponse goodsInfoResponse = goodsQueryProvider.getStoreExportGoods(goodsPageRequest).getContext();

        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("批量商品_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //  log.error("/goods/export/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {
            export(goodsInfoResponse.getGoodsExports(), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        operateLogMQUtil.convertAndSend("缺货管理管理", "导出商品列表", "操作成功");
    }

    private void export(List<StoreGoodsExportVO> goodsList, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.addSheet(
                "商品列表",
                new Column[]{
                        new Column("商品名称", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoName")),
                        new Column("平台类目", (cell, object) -> {
                            List<String> storeCateNames = ((StoreGoodsExportVO) object).getStoreCateNames();
                            if (CollectionUtils.isNotEmpty(storeCateNames)){
                                cell.setCellValue(storeCateNames.get(Constants.no));
                            }else{
                                cell.setCellValue("-");
                            }

                        }),
                        new Column("商品类目", new SpelColumnRender<StoreGoodsExportVO>("cateName")),
                        new Column("商品品牌", (cell, object) -> {
                            String brandName = ((StoreGoodsExportVO) object).getBrandName();
                            if(StringUtils.isNotBlank(brandName)){
                                cell.setCellValue(brandName);
                            }else{
                                cell.setCellValue("-");
                            }
                        }),
                        new Column("生产日期（批次号）", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoBatchNo")),
                        new Column("保质期", new SpelColumnRender<StoreGoodsExportVO>("shelflife")),
                        new Column("包装类型", new SpelColumnRender<StoreGoodsExportVO>("isScatteredQuantitative")),
                        new Column("SKU编码", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoNo")),
                        new Column("erp编码", new SpelColumnRender<StoreGoodsExportVO>("erpNo")),
                        new Column("副标题", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoSubtitle")),
                        new Column("规格", new SpelColumnRender<StoreGoodsExportVO>("specText")),
                        new Column("门店价", new SpelColumnRender<StoreGoodsExportVO>("marketPrice")),
                        new Column("大客户价", new SpelColumnRender<StoreGoodsExportVO>("vipPrice")),
                        new Column("成本价", new SpelColumnRender<StoreGoodsExportVO>("costPrice")),
                        new Column("销售库存", new SpelColumnRender<StoreGoodsExportVO>("stock")),
                        new Column("条形码", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoBarcode")),
                        new Column("物流体积", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoCubage")),
                        new Column("物流重量", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoWeight")),
                        new Column("销售单位", new SpelColumnRender<StoreGoodsExportVO>("goodsInfoUnit")),

                        new Column("最小销售规格", (cell, object) -> {
                            String DevanningUnit = ((StoreGoodsExportVO) object).getDevanningUnit();
                            BigDecimal addStep = ((StoreGoodsExportVO) object).getAddStep();
                            if(StringUtils.isNotBlank(DevanningUnit)){
                                cell.setCellValue(addStep+"-"+DevanningUnit);
                            }else{
                                cell.setCellValue("-");
                            }
                        }),
                        new Column("商品类型", new SpelColumnRender<StoreGoodsExportVO>("goodsType")),
                        new Column("囤货状态", new SpelColumnRender<StoreGoodsExportVO>("pileState")),
                        new Column("销售类型", new SpelColumnRender<StoreGoodsExportVO>("saleType")),
                        new Column("上下架状态", new SpelColumnRender<StoreGoodsExportVO>("addedFlagInfo")),
                        new Column("仓库", new SpelColumnRender<StoreGoodsExportVO>("wareName")),
                        new Column("运费模板", new SpelColumnRender<StoreGoodsExportVO>("freightTemp")),

                },
                goodsList
        );
        excelHelper.write(outputStream);
    }
    private void initAttachProperties(List<StockoutManageVO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> goodInfoIds = list.stream().map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
            GoodsInfoListByConditionRequest conditionRequest = new GoodsInfoListByConditionRequest();
            conditionRequest.setGoodsIds(goodInfoIds);
            BaseResponse<GoodsInfoListByConditionResponse> goodsInfoResponse = goodsInfoQueryProvider.listByCondition(conditionRequest);
            List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getContext().getGoodsInfos();

            //获取商品附加属性
            assignmentGoodsAttachProperties(list, goodsInfos);
            List<Long> wareIds = list.stream().map(x -> x.getWareId()).collect(Collectors.toList());
            WareHouseListRequest wareHouseListRequest = new WareHouseListRequest();
            wareHouseListRequest.setWareIdList(wareIds);
            BaseResponse<WareHouseListResponse> wareResponse = wareHouseQueryProvider.list(wareHouseListRequest);
            assignmentWareInfo(list, wareResponse.getContext().getWareHouseVOList());
        }
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<StockoutManageVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("商品名称", new SpelColumnRender<StockoutManageVO>("goodsName")),
                new Column("sku id", new SpelColumnRender<StockoutManageVO>("goodsInfoId")),
                new Column("sku 编码", new SpelColumnRender<StockoutManageVO>("goodsInfoNo")),
                new Column("品牌id", new SpelColumnRender<StockoutManageVO>("brandId")),
                new Column("品牌名称", new SpelColumnRender<StockoutManageVO>("brandName")),
                new Column("缺货数量", new SpelColumnRender<StockoutManageVO>("stockoutNum")),
                new Column("缺货地区", new SpelColumnRender<StockoutManageVO>("stockoutCity")),
                new Column("补货标识,0:暂未补齐1:已经补齐:2缺货提醒", (cell, object) -> {
                    StockoutManageVO stockoutManageVO = (StockoutManageVO) object;
                    cell.setCellValue(ReplenishmentFlag.getName(stockoutManageVO.getReplenishmentFlag()));
                })
        };

        excelHelper.addSheet("缺货管理列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportOperatingDataList(List<StockoutManageVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        //商品名称、ERP编码、商品类目、销售类型、品牌、所属仓库、上架状态、缺货时间、缺货天数、商品状态
        Column[] columns = {
                new Column("商品名称", new SpelColumnRender<StockoutManageVO>("goodsName")),
                new Column("ERP编码", new SpelColumnRender<StockoutManageVO>("erpGoodsInfoNo")),
                new Column("商品类目", new SpelColumnRender<StockoutManageVO>("cateName")),
                new Column("销售类型", new SpelColumnRender<StockoutManageVO>("saleName")),
                new Column("品牌名称", new SpelColumnRender<StockoutManageVO>("brandName")),
                new Column("所属仓库", new SpelColumnRender<StockoutManageVO>("wareName")),
                new Column("上架状态", new SpelColumnRender<StockoutManageVO>("addedFlagName")),
                new Column("缺货时间", new SpelColumnRender<StockoutManageVO>("stockoutTime")),
                new Column("缺货天数", new SpelColumnRender<StockoutManageVO>("stockoutDay")),
                new Column("商品状态,0:暂未补齐1:已经补齐:2缺货提醒", new SpelColumnRender<StockoutManageVO>("replenishmentFlagName")),
                new Column("补货时间", new SpelColumnRender<StockoutManageVO>("replenishmentTime"))
        };
        excelHelper.addSheet("缺货记录列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

    /**
     * 获取省市区名字
     *
     * @param cityCode cityCode
     * @return name
     */
    private StringBuffer parseAreaName(String cityCode, Map<String, CityCode> cityCodeMap, Map<String, CityCode> provinceMap) {
        StringBuffer areaName = new StringBuffer();
        if (specialRegion.contains(cityCode)) {
            switch (cityCode) {
                case "810000":
                    areaName.append("香港特别行政区");
                    break;
                case "820000":
                    areaName.append("澳门特别行政区");
                    break;
                case "710000":
                    areaName.append("台湾省");
                    break;
            }
        } else if (cityCode.equals("-1")) {
            areaName.append("其他");
        } else {
            CityCode cityCode1 = cityCodeMap.get(cityCode);
            if (Objects.nonNull(cityCode1)) {
                CityCode province = provinceMap.get(cityCode1.getParentCode());
                if (Objects.nonNull(province)) {
                    areaName.append(province.getName()).append("/").append(cityCode1.getName());
                }
            }
        }
        if (StringUtils.isBlank(areaName)) {
            areaName.append("其他");
        }
        return areaName;
    }
}
