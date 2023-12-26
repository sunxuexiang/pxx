package com.wanmi.sbc.coupon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.coupon.dto.CoinActivityExportDetailDto;
import com.wanmi.sbc.coupon.dto.CoinActivityExportDto;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdsRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListByAccountTypeResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByAccountTypeVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNosRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoViewPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByNoResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoResponseVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityCompensationRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinAddActiviStoreRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinRecordPageRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.enums.CoinActivityType;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityGoodsVo;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityVO;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.request.trade.CoinActivityPushKingdeeRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/22 8:28
 */
@Api(tags = "CoinActivityController")
@RestController
@RequestMapping("/coinActivity")
@Slf4j
public class CoinActivityController {


    @Autowired
    private CoinActivityProvider coinActivityProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private TradeProvider tradeProvider;
    
    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;
    
    @Autowired
    CommonUtil commonUtil;
    

    @ApiOperation(value = "运营平台添加活动商家")
    @MultiSubmit
    @PutMapping("/addActivityStore")
    public BaseResponse addActivityStore(@RequestBody @Valid CoinAddActiviStoreRequest request) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("运营端", "鲸币活动", "添加活动商家");
        return coinActivityProvider.addActivityStore(request);
    }

    
    @ApiOperation(value = "运营平台新增金币活动")
    @MultiSubmit
    @PostMapping("/add")
    public BaseResponse add(@RequestBody @Valid CoinActivityAddRequest request) {
        request.setCreatePerson(commonUtil.getOperatorId());
        //操作日志记录
        operateLogMQUtil.convertAndSend("运营端", "鲸币活动", "新增金币活动");
        // 运营平台只支持订单赠金币
        request.setActivityType(CoinActivityType.ORDER);
        return coinActivityProvider.add(request);
    }
    
    @ApiOperation(value = "修改金币活动")
    @MultiSubmit
    @PutMapping("/modify")
    public BaseResponse modify(@RequestBody @Valid CoinActivityModifyRequest request) {
        request.setUpdatePerson(commonUtil.getOperatorId());
        //操作日志记录
        operateLogMQUtil.convertAndSend("运营端", "鲸币活动", "修改金币活动");
        return coinActivityProvider.update(request);
    }
    
    @ApiOperation(value = "删除")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable String id) {
        String operatorId = commonUtil.getOperatorId();
        //操作日志记录
        operateLogMQUtil.convertAndSend("运营端", "鲸币活动", "删除鲸币活动");
        return coinActivityProvider.deleteById(id, operatorId);
    }
    
    @ApiOperation(value = "终止")
    @PutMapping("/termination/{id}")
    public BaseResponse terminationById(@PathVariable String id) {
        String operatorId = commonUtil.getOperatorId();
        //操作日志记录
        operateLogMQUtil.convertAndSend("运营端", "鲸币活动", "终止鲸币活动");
        return coinActivityProvider.terminationById(id, operatorId);
    }


    @ApiOperation(value = "获取详情")
    @GetMapping("/{id}")
    public BaseResponse<CoinActivityDetailResponse> detail(@PathVariable String id) {
        CoinActivityDetailResponse response = coinActivityProvider.detail(id).getContext();
        if (Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getCoinActivityGoodsVoList())) {

            List<String> goodsInfoIds = response.getCoinActivityGoodsVoList().stream().map(CoinActivityGoodsVo::getGoodsInfoId).collect(Collectors.toList());
            GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
            queryRequest.setPageSize(10000);
            queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
            queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
            queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
            queryRequest.setGoodsInfoIds(goodsInfoIds);
            GoodsInfoViewPageResponse pageResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();

            response.setGoodsList(GoodsInfoResponseVO.builder()
                    .goodsInfoPage(pageResponse.getGoodsInfoPage())
                    .goodses(pageResponse.getGoodses())
                    .brands(CollectionUtils.isEmpty(pageResponse.getBrands()) ? Collections.emptyList() : pageResponse.getBrands())
                    .cates(pageResponse.getCates())
                    .build());

            List<CoinActivityGoodsVo> coinActivityGoodsVoList = response.getCoinActivityGoodsVoList();
            Map<String, BoolFlag> coinActivityGoodsMap = coinActivityGoodsVoList.stream().collect(Collectors.toMap(CoinActivityGoodsVo::getGoodsInfoId, CoinActivityGoodsVo::getTerminationFlag, (k1, k2) -> k1));

            if (Objects.nonNull(response.getGoodsList())) {
                GoodsInfoResponseVO goodsList = response.getGoodsList();
                MicroServicePage<GoodsInfoVO> goodsInfoPage = goodsList.getGoodsInfoPage();
                List<GoodsInfoVO> content = goodsInfoPage.getContent();
                if (CollectionUtils.isNotEmpty(content)) {
                    List<WareHouseVO> wareHouseVOList = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList();
                    content.forEach(var -> {
                        WareHouseVO wareHouseVO = wareHouseVOList.stream().filter(wareHouseVOOne -> wareHouseVOOne.getWareId().equals(var.getWareId())).findFirst().get();
                        var.setWareName(wareHouseVO.getWareName());
                    });
                }

                //折扣商品：将折扣价设置为市场价
                for (GoodsInfoVO goodsInfoVO : goodsList.getGoodsInfoPage()) {
                    if (goodsInfoVO.getGoodsInfoType() == 1 && Objects.nonNull(goodsInfoVO.getSpecialPrice())) {
                        goodsInfoVO.setSalePrice(goodsInfoVO.getSpecialPrice());
                    }
                    BoolFlag boolFlag = coinActivityGoodsMap.get(goodsInfoVO.getGoodsInfoId());
                    goodsInfoVO.setTerminationFlag(boolFlag);
                }
            }

            LocalDateTime beginTime = response.getStartTime();
            LocalDateTime endTime = response.getEndTime();
            BoolFlag isPause = response.getPauseFlag();
            BoolFlag terminationFlag = response.getTerminationFlag();


            LocalDateTime now = LocalDateTime.now();
            boolean start = now.isAfter(beginTime) && now.isBefore(endTime)
                    && BoolFlag.NO.equals(isPause)
                    && BoolFlag.NO.equals(terminationFlag);

            if (start) {
                response.setIsShowActiveStatus(Boolean.TRUE);
            }
        }
        return BaseResponse.success(response);
    }


    /**
     * 活动列表分页
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "金币活动列表分页")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CoinActivityVO>> page(@RequestBody CoinActivityPageRequest request) {

        if (!StringUtils.isEmpty(request.getErpGoodsInfoNo())) {
            BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider.findAllGoodsByErpNos(
                    GoodsInfoByErpNosRequest.builder()
                            .erpGoodsInfoNos(Collections.singletonList(request.getErpGoodsInfoNo()))
                            .build()
            );
            GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
            if (CollectionUtils.isNotEmpty(context.getGoodsInfo())) {
                List<String> goodsInfoIds = context.getGoodsInfo().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                request.setGoodsInfoIds(goodsInfoIds);
            } else {
                request.setGoodsInfoIds(Collections.singletonList("*****"));
            }
        }

        MicroServicePage<CoinActivityVO> response = coinActivityProvider.page(request).getContext().getCoinActivityVOPage();
        return BaseResponse.success(response);
    }
    


    /**
     * 鲸币活动导出明细
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "鲸币活动导出明细")
    @MultiSubmit
    @RequestMapping(value = "/export/detail", method = RequestMethod.POST)
    public void exportDetail(@RequestBody CoinActivityPageRequest request, HttpServletResponse response) {
        request.setPageSize(3000);
        if (!StringUtils.isEmpty(request.getErpGoodsInfoNo())) {
            BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider.findAllGoodsByErpNos(
                    GoodsInfoByErpNosRequest.builder()
                            .erpGoodsInfoNos(Collections.singletonList(request.getErpGoodsInfoNo()))
                            .build()
            );
            GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
            if (CollectionUtils.isNotEmpty(context.getGoodsInfo())) {
                List<String> goodsInfoIds = context.getGoodsInfo().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                request.setGoodsInfoIds(goodsInfoIds);
            } else {
                request.setGoodsInfoIds(Collections.singletonList("*****"));
            }
        }

        MicroServicePage<CoinActivityVO> pageResponse = coinActivityProvider.page(request).getContext().getCoinActivityVOPage();

        List<CoinActivityExportDetailDto> importDetailDtos = buildImportDetailDtos(pageResponse.getContent());


        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("指定商品赠鲸币活动记录_%s.xls",
                dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/coinActivity/export/detail, fileName={},", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);

        try {
            exportDetail(importDetailDtos, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        operateLogMQUtil.convertAndSend("鲸币", "鲸币活动导出明细", "操作成功");
    }


    /**
     * 鲸币活动导出明细
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "鲸币活动导出")
    @MultiSubmit
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void export(@RequestBody CoinActivityPageRequest request, HttpServletResponse response) {
        request.setPageSize(1000);
        List<String> goodsInfoIds = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getErpGoodsInfoNo())) {
            BaseResponse<GoodsInfoByNoResponse> allGoodsByErpNos = goodsInfoQueryProvider.findAllGoodsByErpNos(
                    GoodsInfoByErpNosRequest.builder()
                            .erpGoodsInfoNos(Arrays.asList(request.getErpGoodsInfoNo().split(",")))
                            .build()
            );
            GoodsInfoByNoResponse context = allGoodsByErpNos.getContext();
            if (CollectionUtils.isNotEmpty(context.getGoodsInfo())) {
                goodsInfoIds = context.getGoodsInfo().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                request.setGoodsInfoIds(goodsInfoIds);
            } else {
                request.setGoodsInfoIds(Collections.singletonList("*****"));
            }
        }

        MicroServicePage<CoinActivityVO> pageResponse = coinActivityProvider.page(request).getContext().getCoinActivityVOPage();


        List<CoinActivityExportDto> importDtos = buildImportDtos(pageResponse.getContent(), goodsInfoIds);


        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("指定商品赠鲸币活动导出_%s.xls",
                dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/coinActivity/export, fileName={},", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);

        try {
            export(importDtos, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        operateLogMQUtil.convertAndSend("鲸币", "鲸币活动导出", "操作成功");
    }

    private void export(List<CoinActivityExportDto> vos, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
                new Column("ERP编号", new SpelColumnRender<CoinActivityExportDto>("erpNo")),
                new Column("SKU编号", new SpelColumnRender<CoinActivityExportDto>("skuNo")),
                new Column("商品名称", new SpelColumnRender<CoinActivityExportDto>("goodsName")),
                new Column("商品分类", new SpelColumnRender<CoinActivityExportDto>("goodsCate")),
                new Column("品牌", new SpelColumnRender<CoinActivityExportDto>("goodsBrand")),
                new Column("活动类型", new SpelColumnRender<CoinActivityExportDto>("activityType")),
                new Column("活动名称", new SpelColumnRender<CoinActivityExportDto>("activityName")),
                new Column("活动起止时间", new SpelColumnRender<CoinActivityExportDto>("activityTime")),
                new Column("活动力度", new SpelColumnRender<CoinActivityExportDto>("coinNum")),
                new Column("适用区域", new SpelColumnRender<CoinActivityExportDto>("activityArea")),
                new Column("是否叠加", new SpelColumnRender<CoinActivityExportDto>("isSupper")),
                new Column("是否终止", new SpelColumnRender<CoinActivityExportDto>("isTermination")),
                new Column("最后操作人", new SpelColumnRender<CoinActivityExportDto>("lastOperator")),
                new Column("最后操作时间", new SpelColumnRender<CoinActivityExportDto>("lastUpdateTime")),
        };
        excelHelper
                .addSheet(
                        "指定商品赠鲸币导出",
                        columns,
                        vos
                );
        excelHelper.write(outputStream);
    }

    private List<CoinActivityExportDto> buildImportDtos(List<CoinActivityVO> vos, List<String> goodsInfoIds) {
        List<CoinActivityExportDto> result = new ArrayList<>();
        Map<Long, WareHouseVO> wareHouseVOMap = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList().stream().collect(Collectors.toMap(WareHouseVO::getWareId, Function.identity(), (o1, o2) -> o1));

        Map<String, String> updatePersonMap = null;
        List<String> updatePersonIds = vos.stream().map(CoinActivityVO::getUpdatePerson).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updatePersonIds)) {
            EmployeeListByAccountTypeResponse context = employeeQueryProvider.getByIds(EmployeeByIdsRequest.builder().employeeIds(updatePersonIds).build()).getContext();
            if (Objects.nonNull(context)) {
                updatePersonMap = context.getEmployeeList().stream().collect(Collectors.toMap(EmployeeListByAccountTypeVO::getEmployeeId, EmployeeListByAccountTypeVO::getAccountName, (o1, o2) -> o1));
            }
        }
        GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
        queryRequest.setPageSize(10000);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        GoodsInfoViewPageResponse pageResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();

        Map<String, GoodsInfoVO> goodsInfoVOMap = pageResponse.getGoodsInfoPage().getContent().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity(), (o1, o2) -> o1));
        Map<Long, GoodsCateVO> goodsCateVOMap = pageResponse.getCates().stream().collect(Collectors.toMap(GoodsCateVO::getCateId, Function.identity(), (o1, o2) -> o1));
        Map<Long, GoodsBrandVO> goodsBrandVOMap = pageResponse.getBrands().stream().collect(Collectors.toMap(GoodsBrandVO::getBrandId, Function.identity(), (o1, o2) -> o1));

        Map<String, String> finalUpdatePersonMap = updatePersonMap;
        vos.forEach(o -> {
            CoinActivityDetailResponse response = coinActivityProvider.detail(o.getActivityId()).getContext();
            if (Objects.nonNull(response)) {
                List<CoinActivityGoodsVo> coinActivityGoodsVoList = response.getCoinActivityGoodsVoList().stream().filter(obj -> goodsInfoIds.contains(obj.getGoodsInfoId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(coinActivityGoodsVoList)) {

                    String isTermination = BoolFlag.YES.equals(o.getTerminationFlag()) ? "是" : "否";
                    String lastOperator = "";
                    if (Objects.nonNull(o.getUpdatePerson()) && Objects.nonNull(finalUpdatePersonMap)) {
                        if (StringUtils.isNotBlank(finalUpdatePersonMap.get(o.getUpdatePerson()))) {
                            lastOperator = finalUpdatePersonMap.get(o.getUpdatePerson());
                        }
                    }
                    String lastUpdateTime = "";
                    if (Objects.nonNull(o.getUpdateTime())) {
                        lastUpdateTime = DateUtil.format(o.getUpdateTime(), DateUtil.FMT_TIME_1);
                    }
                    String finalLastOperator = lastOperator;
                    String finalLastUpdateTime = lastUpdateTime;
                    coinActivityGoodsVoList.forEach(vo -> {

                        CoinActivityExportDto dto = new CoinActivityExportDto();
                        dto.setActivityName(o.getActivityName());
                        dto.setActivityType("指定商品赠鲸币");
                        dto.setActivityTime(DateUtil.format(o.getStartTime(), DateUtil.FMT_TIME_1) + " ~ " + DateUtil.format(o.getEndTime(), DateUtil.FMT_TIME_1));

                        WareHouseVO wareHouseVO = wareHouseVOMap.get(o.getWareId());
                        if (Objects.nonNull(wareHouseVO)) {
                            dto.setActivityArea(wareHouseVO.getWareName());
                        } else {
                            dto.setActivityArea("");
                        }
                        String isSupper = DefaultFlag.YES.equals(o.getIsOverlap()) ? "是" : "否";
                        dto.setIsSupper(isSupper);
                        dto.setCoinNum(o.getCoinNum().toString());

                        String goodsInfoId = vo.getGoodsInfoId();
                        GoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(goodsInfoId);
                        dto.setErpNo("");
                        dto.setGoodsName("");
                        // dto.setPrice("");
                        dto.setGoodsCate("");
                        dto.setGoodsBrand("");
                        dto.setSkuNo("");
                        if (Objects.nonNull(goodsInfoVO)) {
                            dto.setErpNo(goodsInfoVO.getErpGoodsInfoNo());
                            dto.setGoodsName(goodsInfoVO.getGoodsInfoName());
                            dto.setSkuNo(goodsInfoVO.getGoodsInfoNo());
                            // dto.setPrice(String.valueOf(goodsInfoVO.getMarketPrice()));
                            Long cateId = goodsInfoVO.getCateId();
                            Long brandId = goodsInfoVO.getBrandId();
                            if (Objects.nonNull(cateId) && Objects.nonNull(goodsCateVOMap.get(cateId))) {
                                GoodsCateVO goodsCateVO = goodsCateVOMap.get(cateId);
                                dto.setGoodsCate(goodsCateVO.getCateName());
                            }

                            if (Objects.nonNull(brandId) && Objects.nonNull(goodsBrandVOMap.get(brandId))) {
                                GoodsBrandVO goodsBrandVO = goodsBrandVOMap.get(brandId);
                                dto.setGoodsBrand(goodsBrandVO.getBrandName());
                            }
                        }


                        dto.setIsTermination(isTermination);
                        dto.setLastOperator(finalLastOperator);
                        dto.setLastUpdateTime(finalLastUpdateTime);

                        result.add(dto);
                    });
                }
            }
        });
        return result;
    }

    public void exportDetail(List<CoinActivityExportDetailDto> vos, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {new Column("活动名称", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getActivityName());

        }), new Column("活动类型", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getActivityType());

        }), new Column("活动起止时间", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getActivityTime());

        }), new Column("活动状态", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getActivityStatus());

        }), new Column("适用区域", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getActivityArea());

        }), new Column("是否叠加", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getIsSupper());

        }), new Column("返还力度", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getCoinNum());

        }), new Column("ERP编号", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getErpNo());

        }), new Column("商品名称", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getGoodsName());

        }), new Column("类目", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getGoodsCate());


        }), new Column("品牌", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getGoodsBrand());

        }), new Column("单价", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getPrice());

        }), new Column("商品参与状态", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getGoodsStatus());

        }), new Column("店铺名称", (cell, object) -> {
            CoinActivityExportDetailDto vo = (CoinActivityExportDetailDto) object;
            cell.setCellValue(vo.getStoreName());

        })};
        excelHelper.addSheet("指定商品赠鲸币导出明细", columns, vos);
        excelHelper.write(outputStream);
    }


    public List<CoinActivityExportDetailDto> buildImportDetailDtos(List<CoinActivityVO> vos) {
        List<CoinActivityExportDetailDto> result = new ArrayList<>();
        Map<Long, WareHouseVO> wareHouseVOMap = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext().getWareHouseVOList().stream().collect(Collectors.toMap(WareHouseVO::getWareId, Function.identity(), (o1, o2) -> o1));

        vos.forEach(o -> {
            CoinActivityDetailResponse response = coinActivityProvider.detail(o.getActivityId()).getContext();
            if (Objects.nonNull(response)) {

                List<String> goodsInfoIds = response.getCoinActivityGoodsVoList().stream().map(CoinActivityGoodsVo::getGoodsInfoId).collect(Collectors.toList());
                GoodsInfoViewPageRequest queryRequest = new GoodsInfoViewPageRequest();
                queryRequest.setPageSize(10000);
                queryRequest.setAddedFlag(AddedFlag.YES.toValue());//上架
                queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
                queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
                queryRequest.setGoodsInfoIds(goodsInfoIds);
                GoodsInfoViewPageResponse pageResponse = goodsInfoQueryProvider.pageView(queryRequest).getContext();

                Map<String, GoodsInfoVO> goodsInfoVOMap = pageResponse.getGoodsInfoPage().getContent().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId, Function.identity(), (o1, o2) -> o1));
                Map<Long, GoodsCateVO> goodsCateVOMap = pageResponse.getCates().stream().collect(Collectors.toMap(GoodsCateVO::getCateId, Function.identity(), (o1, o2) -> o1));
                Map<Long, GoodsBrandVO> goodsBrandVOMap = pageResponse.getBrands().stream().collect(Collectors.toMap(GoodsBrandVO::getBrandId, Function.identity(), (o1, o2) -> o1));

                response.getCoinActivityGoodsVoList().forEach(vo -> {

                    CoinActivityExportDetailDto dto = new CoinActivityExportDetailDto();
                    dto.setActivityName(o.getActivityName());
                    dto.setActivityType("指定商品赠鲸币");
                    dto.setActivityTime(DateUtil.format(o.getStartTime(), DateUtil.FMT_TIME_1) + " ~ " + DateUtil.format(o.getEndTime(), DateUtil.FMT_TIME_1));
                    LocalDateTime now = LocalDateTime.now();
                    String activityStatus = "进行中";
                    if (now.isBefore(o.getStartTime())) {
                        activityStatus = "未开始";
                    }
                    if (now.isAfter(o.getEndTime())) {
                        activityStatus = "已结束";
                    }
                    if (now.isAfter(o.getStartTime()) && now.isBefore(o.getEndTime())) {
                        activityStatus = "进行中";
                    }
                    if (BoolFlag.YES.equals(o.getTerminationFlag())) {
                        activityStatus = "已终止";
                    }
                    dto.setActivityStatus(activityStatus);
                    WareHouseVO wareHouseVO = wareHouseVOMap.get(o.getWareId());
                    if (Objects.nonNull(wareHouseVO)) {
                        dto.setActivityArea(wareHouseVO.getWareName());
                    } else {
                        dto.setActivityArea("");
                    }
                    String isSupper = DefaultFlag.YES.equals(o.getIsOverlap()) ? "是" : "否";
                    dto.setIsSupper(isSupper);
                    dto.setCoinNum(o.getCoinNum().toString());

                    String goodsInfoId = vo.getGoodsInfoId();
                    GoodsInfoVO goodsInfoVO = goodsInfoVOMap.get(goodsInfoId);
                    dto.setErpNo("");
                    dto.setGoodsName("");
                    dto.setPrice("");
                    dto.setGoodsCate("");
                    dto.setGoodsBrand("");
                    if (Objects.nonNull(goodsInfoVO)) {
                        dto.setErpNo(goodsInfoVO.getErpGoodsInfoNo());
                        dto.setGoodsName(goodsInfoVO.getGoodsInfoName());
                        dto.setPrice(String.valueOf(goodsInfoVO.getMarketPrice()));
                        Long cateId = goodsInfoVO.getCateId();
                        Long brandId = goodsInfoVO.getBrandId();
                        if (Objects.nonNull(cateId) && Objects.nonNull(goodsCateVOMap.get(cateId))) {
                            GoodsCateVO goodsCateVO = goodsCateVOMap.get(cateId);
                            dto.setGoodsCate(goodsCateVO.getCateName());
                        }

                        if (Objects.nonNull(brandId) && Objects.nonNull(goodsBrandVOMap.get(brandId))) {
                            GoodsBrandVO goodsBrandVO = goodsBrandVOMap.get(brandId);
                            dto.setGoodsBrand(goodsBrandVO.getBrandName());
                        }
                    }

                    BoolFlag terminationFlag = vo.getTerminationFlag();
                    String goodsStatus = BoolFlag.YES.equals(terminationFlag) ? "已终止" : "参与中";
                    dto.setGoodsStatus(goodsStatus);
                    
                    dto.setStoreName(o.getStoreName());
                    dto.setSupplierName(o.getSupplierName());

                    result.add(dto);
                });


            }
        });
        return result;
    }

    /**
     * 活动列表分页
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "金币活动领取记录分页")
    @RequestMapping(value = "/record/page", method = RequestMethod.POST)
    public BaseResponse<MicroServicePage<CoinActivityRecordDto>> recordPage(@RequestBody CoinRecordPageRequest request) {
        MicroServicePage<CoinActivityRecordDto> response = coinActivityProvider.recordPage(request).getContext();
        return BaseResponse.success(response);
    }

    /**
     * 鲸币活动导出明细
     *
     * @param request
     * @param response
     */
    @ApiOperation(value = "鲸币活动记录导出")
    @MultiSubmit
    @RequestMapping(value = "/record/export", method = RequestMethod.POST)
    public void export(@RequestBody CoinRecordPageRequest request, HttpServletResponse response) {
        request.setPageSize(3000);
        MicroServicePage<CoinActivityRecordDto> pageResponse = coinActivityProvider.recordPage(request).getContext();
        List<CoinActivityRecordDto> content = pageResponse.getContent();

        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("活动赠鲸币记录导出_%s.xls",
                dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/coinActivity/record/export, fileName={},", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);

        try {
            ExcelHelper excelHelper = new ExcelHelper();
            Column[] columns = {
                    new Column("用户账号", new SpelColumnRender<CoinActivityRecordDto>("customerAccount")),
                    new Column("订单编号", new SpelColumnRender<CoinActivityRecordDto>("orderNo")),
                    new Column("下单时间", (cell, object) -> {
                        CoinActivityRecordDto vo = (CoinActivityRecordDto) object;
                        cell.setCellValue(DateUtil.format(vo.getOrderTime(), DateUtil.FMT_TIME_1));
                    }),
                    new Column("订单金额", new SpelColumnRender<CoinActivityRecordDto>("orderPrice")),
                    new Column("返还金额", new SpelColumnRender<CoinActivityRecordDto>("coinNum")),
            };
            excelHelper
                    .addSheet(
                            "活动赠鲸币记录导出",
                            columns,
                            content
                    );
            excelHelper.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        operateLogMQUtil.convertAndSend("鲸币", "鲸币活动记录导出", "操作成功");
    }

    /**
     * 根据订单查记录详情
     *
     * @param orderId
     * @return
     */
    @ApiOperation(value = "根据订单查记录详情")
    @GetMapping(value = "/record/{orderId}")
    public BaseResponse<CoinActivityRecordDto> recordByOrderId(@PathVariable String orderId) {
        return coinActivityProvider.recordByOrderId(orderId);
    }


    @ApiOperation(value = "根据订单号或者推送单号 补推金蝶")
    @PostMapping(value = "/push/compensation")
    @MultiSubmit
    public BaseResponse pushCoinActivityToKingdee(@RequestBody CoinActivityCompensationRequest request) {

        if (CollectionUtils.isEmpty(request.getOrderNoList())) {
            return BaseResponse.error("单号不能为空");
        }

        int type = Objects.isNull(request.getType()) ? 1 : request.getType();
        if (type == 1) {
            // 根据订单号
            for (String orderNo : request.getOrderNoList()) {
                CoinActivityRecordDto recordDto = coinActivityProvider.recordByOrderId(orderNo).getContext();
                if (Objects.isNull(recordDto.getSendNo())) {
                    log.info("补偿鲸币活动推送金蝶，订单号：{} 不存在", orderNo);
                }else {
                    pushKindee(recordDto);
                }
            }
        }

        if (type == 2) {
            // 根据订单号
            for (String sendNo : request.getOrderNoList()) {
                CoinActivityRecordDto recordDto = coinActivityProvider.recordBySendNo(sendNo).getContext();
                if (Objects.isNull(recordDto.getSendNo())) {
                    log.info("补偿鲸币活动推送金蝶，推送号：{} 不存在", sendNo);
                }else {
                    pushKindee(recordDto);
                }
            }
        }
        operateLogMQUtil.convertAndSend("鲸币", "根据订单号或者推送单号补推金蝶", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    private void pushKindee(CoinActivityRecordDto recordDto) {
        CoinActivityPushKingdeeRequest kingdeeRequest = CoinActivityPushKingdeeRequest
                .builder()
                .tid(recordDto.getOrderNo())
                .buyerAccount(recordDto.getCustomerAccount())
                .applyPrice(recordDto.getCoinNum())
                .saleType(SaleType.WHOLESALE)
                .sendNo(recordDto.getSendNo())
                .pushDateStr(DateUtil.format(recordDto.getRecordTime(),DateUtil.FMT_DATE_1))
                .build();

        if (recordDto.getRecordType() == 1) {
            // 赠送推 收款单
            tradeProvider.pushOrderKingdeeForCoin(kingdeeRequest);
        }else if (recordDto.getRecordType() == 2){
            // 退回 推收款退款单
            tradeProvider.pushRefundOrderKingdeeForCoin(kingdeeRequest);
        }else {
            log.info("补偿鲸币活动推送金蝶失败，单号：{} , 记录：{}", recordDto.getOrderNo(), JSON.toJSONString(recordDto));
        }

    }
}
