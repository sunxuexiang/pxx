package com.wanmi.sbc.goods;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreResponse;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.excel.GoodsExcelProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.standard.StandardGoodsGetUsedGoodsRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByCreatTimeAndStaueExportListResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsExportListResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsExportVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.request.GoodsSortImportExcelRequest;
import com.wanmi.sbc.goods.service.GoodsSortImportExcelService;
import com.wanmi.sbc.order.response.ExportGoodsByStatusExcel;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: Boss零售商品服务spu
 * @author: XinJiang
 * @time: 2022/3/8 16:05
 */
@Slf4j
@RestController
@RequestMapping(value = "/boss/retail/goods")
@Api(tags = "BossRetailGoodsController",description = "Boss零售商品服务spu")
public class BossRetailGoodsController {

    @Autowired
    private GoodsExcelProvider goodsExcelProvider;

    @Autowired
    private GoodsSortImportExcelService goodsSortImportExcelService;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private RetailGoodsQueryProvider retailGoodsQueryProvider;

    @Autowired
    private StandardGoodsQueryProvider standardGoodsQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
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
        BaseResponse<GoodsPageResponse> pageResponse = retailGoodsQueryProvider.retailpage(pageRequest);
        GoodsPageResponse goodsPageResponse = pageResponse.getContext();
        List<GoodsVO> goodses = goodsPageResponse.getGoodsPage().getContent();
        goodses.forEach(vo -> {
            BaseResponse<StoreInfoResponse> baseResponse = storeQueryProvider.getStoreInfoById(StoreInfoByIdRequest.builder().storeId(vo.getStoreId()).build());
            if (Objects.nonNull(baseResponse.getContext())){
                vo.setStoreName(baseResponse.getContext().getStoreName());
            }
        });
        if (CollectionUtils.isNotEmpty(goodses)) {
            //列出已导入商品库的商品编号
            StandardGoodsGetUsedGoodsRequest standardGoodsGetUsedGoodsRequest = new StandardGoodsGetUsedGoodsRequest();
            standardGoodsGetUsedGoodsRequest.setGoodsIds(goodses.stream().map(GoodsVO::getGoodsId).collect(Collectors.toList()));
            goodsPageResponse.setImportStandard(standardGoodsQueryProvider.getUsedGoods(standardGoodsGetUsedGoodsRequest).getContext().getGoodsIds());
        }
        return pageResponse;
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
            //操作日志记录
            operateLogMQUtil.convertAndSend("Boss零售商品服务spu", "下载导入商品排序模板", "操作成功");
        }
    }

    /**
     * 上传排序文件
     */
    @ApiOperation(value = "上传排序文件")
    @RequestMapping(value = "sort/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        String upload = goodsSortImportExcelService.upload(uploadFile, commonUtil.getOperatorId());
        //操作日志记录
        operateLogMQUtil.convertAndSend("Boss零售商品服务spu", "上传排序文件", "操作成功");
        return BaseResponse.success(upload);
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
        goodsSortImportExcelService.implRetailGoods(excelRequest);
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
        operateLogMQUtil.convertAndSend("Boss零售商品服务spu", "下载错误文档", "操作成功");
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

        GoodsExportListResponse goodsInfoResponse= goodsQueryProvider.getExportGoods(goodsPageRequest).getContext();

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
        operateLogMQUtil.convertAndSend("Boss零售商品服务spu", "导出商品列表", "操作成功");
    }

    private void export(List<GoodsExportVO> goodsList, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.addSheet(
                "商品列表",
                new Column[]{
                        new Column("SKU编码", new SpelColumnRender<GoodsInfoVO>("goodsInfoNo")),
                        new Column("SPU编码", new SpelColumnRender<GoodsInfoVO>("goodsNo")),
                        new Column("ERP编码", new SpelColumnRender<GoodsInfoVO>("erpGoodsInfoNo")),
                        new Column("商品品牌", (cell, object) -> {
                            String brandName = ((GoodsExportVO) object).getBrandName();
                            if(StringUtils.isNotBlank(brandName)){
                                cell.setCellValue(brandName);
                            }else{
                                cell.setCellValue("-");
                            }
                        }),
                        new Column("商品排序", (cell, object) -> {
                            Integer goodsSeqNum = ((GoodsExportVO) object).getGoodsSeqNum();
                            if(goodsSeqNum!=null){
                                cell.setCellValue(goodsSeqNum);
                            }else{
                                cell.setCellValue("-");
                            }
                        }),
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
        operateLogMQUtil.convertAndSend("Boss零售商品服务spu", "导出商品列表通过商品创建时间和上架状态", "操作成功");

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
