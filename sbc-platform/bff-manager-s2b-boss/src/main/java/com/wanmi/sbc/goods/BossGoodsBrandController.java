package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandExcelProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandDeleteByIdRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandPageRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandListResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandResponse;
import com.wanmi.sbc.goods.bean.vo.ContractBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.request.GoodsBrandSortImportExcelRequest;
import com.wanmi.sbc.goods.service.GoodsBrandSortImportExcelService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 上午10:05 2017/11/1
 * @Description: 商品品牌Controller
 */
@Api(description = "商品品牌API", tags = "BossGoodsBrandController")
@RestController("bossGoodsBrandController")
@RequestMapping("/goods")
@Validated
@Slf4j
public class BossGoodsBrandController {

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsBrandProvider goodsBrandProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    @Autowired
    private GoodsBrandExcelProvider goodsBrandExcelProvider;

    @Autowired
    private GoodsBrandSortImportExcelService goodsBrandSortImportExcelService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;

    /**
     * 分页商品品牌
     *
     * @param queryRequest 商品品牌参数
     * @return 商品详情
     */
    @ApiOperation(value = "分页商品品牌")
    @RequestMapping(value = "/goodsBrands", method = RequestMethod.POST)
    public BaseResponse<Page<GoodsBrandResponse>> page(@RequestBody GoodsBrandPageRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        if (queryRequest.getBrandSeqFlag() != null && queryRequest.getBrandSeqFlag() == 1) {
            queryRequest.putSort("brandSeqNum", SortType.ASC.toValue());
        }else{
            queryRequest.putSort("createTime", SortType.DESC.toValue());
            queryRequest.putSort("brandId", SortType.ASC.toValue());
        }

        MicroServicePage<GoodsBrandVO> page =
                goodsBrandQueryProvider.page(queryRequest).getContext().getGoodsBrandPage();
        List<ContractBrandVO> contractBrands;
        if (page.getTotalElements() > 0L) {
            List<Long> brandIds = page.getContent().stream().map(GoodsBrandVO::getBrandId).collect(Collectors.toList());
            ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
            contractBrandQueryRequest.setGoodsBrandIds(brandIds);
            contractBrands =
                    contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList();
        } else {
            contractBrands = new ArrayList<>();
        }

        List<GoodsBrandResponse> goodsBrandResponses = new ArrayList<>();
        page.getContent().forEach(info -> {
            GoodsBrandResponse goodsBrandResponse = new GoodsBrandResponse();
            BeanUtils.copyProperties(info, goodsBrandResponse);
            List<Long> storeIds =
                    contractBrands.stream().filter(contractBrand -> info.getBrandId().equals(contractBrand.getGoodsBrand().getBrandId()))
                            .map(ContractBrandVO::getStoreId).collect(Collectors.toList());
            if (!storeIds.isEmpty()) {
//                List<Store> stores = storeService.findList(storeIds);
                List<StoreVO> stores = storeQueryProvider.listNoDeleteStoreByIds(
                        ListNoDeleteStoreByIdsRequest.builder().storeIds(storeIds).build()
                ).getContext().getStoreVOList();
                //过滤出已审核通过的店铺商家信息
                List<String> companyNames =
                        stores.stream().filter(store -> CheckState.CHECKED.equals(store.getAuditState())).map(store -> store.getCompanyInfo().getSupplierName()).distinct().collect(Collectors.toList());
                goodsBrandResponse.setSupplierNames(StringUtils.join(companyNames.toArray(), ","));
            }
            goodsBrandResponses.add(goodsBrandResponse);
        });
        return BaseResponse.success(new PageImpl<>(goodsBrandResponses, queryRequest.getPageable(),
                page.getTotalElements()));
    }


    /**
     * 获取商品品牌详情信息
     *
     * @param brandId 商品品牌编号
     * @return 商品详情
     */
    /*@ApiOperation(value = "获取商品品牌详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long",
            name = "brandId", value = "商品品牌编号", required = true)
    @RequestMapping(value = "/goodsBrand/{brandId}", method = RequestMethod.GET)
    public BaseResponse<GoodsBrandVO> list(@PathVariable Long brandId) {
        if (brandId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(goodsBrandQueryProvider.getById(GoodsBrandByIdRequest.builder().brandId(brandId)
                .build()).getContext());
    }*/

    /**
     * 删除商品品牌
     */
    @ApiOperation(value = "删除商品品牌")
    @ApiImplicitParam(paramType = "path", dataType = "Long",
            name = "brandId", value = "商品品牌编号", required = true)
    @RequestMapping(value = "/goodsBrand/{brandId}", method = RequestMethod.DELETE)
    public BaseResponse delete(@PathVariable Long brandId) {
        if (Objects.isNull(brandId)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsBrandVO goodsBrand = goodsBrandProvider.delete(
                GoodsBrandDeleteByIdRequest.builder().brandId(brandId).build()
        ).getContext();
        esGoodsInfoElasticService.delBrandIds(Collections.singletonList(goodsBrand.getBrandId()), null);
        esRetailGoodsInfoElasticService.delBrandIds(Collections.singletonList(goodsBrand.getBrandId()), null);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "删除品牌", "删除品牌：" + goodsBrand.getBrandName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 获取商品品牌序号最大值
     *
     * @return 最大值
     */
    @ApiOperation(value = "获取商品品牌序号最大值")
    @RequestMapping(value = "/getBrandSeqNumMax", method = RequestMethod.GET)
    public BaseResponse<Integer> getBrandSeqNumMax() {
        GoodsBrandListRequest queryRequest = new GoodsBrandListRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("brandSeqNum", SortType.DESC.toValue());
        List<GoodsBrandVO> goodsBrandVOList = goodsBrandQueryProvider.list(queryRequest).getContext().getGoodsBrandVOList();
        Integer max = 0;
        if (CollectionUtils.isNotEmpty(goodsBrandVOList)) {
            //获取序号最大值
            max = goodsBrandVOList.stream().filter(goodsBrandVO -> Objects.nonNull(goodsBrandVO.getBrandSeqNum())).map(GoodsBrandVO::getBrandSeqNum).max(Integer::compare).get();
        }
        return BaseResponse.success(max);
    }

    /**
     * 导出商品品牌
     *
     * @param encrypted
     * @param response
     */
    @ApiOperation(value = "导出商品品牌")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "加密", required = true)
    @RequestMapping(value = "/brand/export/params/{encrypted}", method = RequestMethod.GET)
    public void exportByParams(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));

        GoodsBrandPageRequest goodsBrandRequest = JSON.parseObject(decrypted, GoodsBrandPageRequest.class);
        goodsBrandRequest.setDelFlag(DeleteFlag.NO.toValue());
        if (goodsBrandRequest.getBrandSeqFlag() != null && goodsBrandRequest.getBrandSeqFlag() == 1) {
            goodsBrandRequest.putSort("brandSeqNum", SortType.ASC.toValue());
        }else{
            goodsBrandRequest.putSort("createTime", SortType.DESC.toValue());
            goodsBrandRequest.putSort("brandId", SortType.ASC.toValue());
        }

        goodsBrandRequest.setPageSize(1000);

        GoodsBrandListResponse goodsBrandResponse = goodsBrandQueryProvider.getGoodsBrands(goodsBrandRequest).getContext();

        String headerKey = "Content-Disposition";
        LocalDateTime dateTime = LocalDateTime.now();
        String fileName = String.format("批量商品品牌_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("/goods/export/params, fileName={}, error={}", fileName, e);
        }
        String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
        response.setHeader(headerKey, headerValue);
        try {
            export(goodsBrandResponse.getGoodsBrandVOList(), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new SbcRuntimeException(e);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "导出商品品牌", "操作成功");
    }

    private void export(List<GoodsBrandVO> goodsBrandList, ServletOutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.addSheet(
                "商品品牌",
                new Column[]{
                        new Column("品牌名称", new SpelColumnRender<GoodsBrandVO>("brandName")),
                        new Column("品牌别名", (cell, object) -> {
                            String nickName = ((GoodsBrandVO) object).getNickName();
                            if (StringUtils.isNotBlank(nickName)) {
                                cell.setCellValue(nickName);
                            } else {
                                cell.setCellValue("-");
                            }
                        }),
                        new Column("品牌排序", (cell, object) -> {
                            Integer brandSeqNum = ((GoodsBrandVO) object).getBrandSeqNum();
                            if (brandSeqNum != null) {
                                cell.setCellValue(brandSeqNum);
                            } else {
                                cell.setCellValue("-");
                            }
                        }),
                },
                goodsBrandList
        );
        excelHelper.write(outputStream);
    }

    @ApiOperation(value = "下载导入品牌排序模板")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted",
            value = "加密", required = true)
    @RequestMapping(value = "brand/sort/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        String file = goodsBrandExcelProvider.exportTemplate().getContext().getFile();
        if (org.apache.commons.lang.StringUtils.isNotBlank(file)) {
            try {
                String fileName = URLEncoder.encode("品牌排序导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            } catch (Exception e) {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            //操作日志记录
            operateLogMQUtil.convertAndSend("商品", "下载导入品牌排序模板", "操作成功");
        }
    }

    /**
     * 上传排序文件
     */
    @ApiOperation(value = "上传排序文件")
    @RequestMapping(value = "/brand/sort/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        String upload = goodsBrandSortImportExcelService.upload(uploadFile, commonUtil.getOperatorId());
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "上传排序文件", "操作成功");
        return BaseResponse.success(upload);
    }

    /**
     * 确认品牌排序导入
     *
     * @param ext 文件格式 {@link String}
     * @return
     */
    @ApiOperation(value = "确认品牌排序导入")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext",
            value = "文件名后缀", required = true)
    @RequestMapping(value = "/brand/sort/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> implGoods(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        GoodsBrandSortImportExcelRequest goodsBrandSortImportExcelRequest = new GoodsBrandSortImportExcelRequest();
        goodsBrandSortImportExcelRequest.setExt(ext);
        goodsBrandSortImportExcelRequest.setUserId(commonUtil.getOperatorId());
        goodsBrandSortImportExcelService.implGoods(goodsBrandSortImportExcelRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("品牌排序", "批量导入", "批量导入");
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
    @RequestMapping(value = "/brand/sort/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsBrandSortImportExcelService.downErrExcel(commonUtil.getOperatorId(), ext);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "下载错误文档", "操作成功");
    }
}
