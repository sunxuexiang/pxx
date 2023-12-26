package com.wanmi.sbc.catebrandsortrel;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
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
import com.wanmi.sbc.goods.api.provider.brand.ContractBrandQueryProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandExcelProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelQueryProvider;
import com.wanmi.sbc.goods.api.provider.catebrandsortrel.CateBrandSortRelProvider;
import com.wanmi.sbc.goods.api.request.brand.ContractBrandListRequest;
import com.wanmi.sbc.goods.api.request.catebrandsortrel.*;
import com.wanmi.sbc.goods.api.request.excel.GoodsSupplierExcelExportTemplateByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandResponse;
import com.wanmi.sbc.goods.api.response.catebrandsortrel.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;

import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.wanmi.sbc.goods.bean.vo.CateBrandSortRelVO;
import com.wanmi.sbc.goods.bean.vo.ContractBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;


/**
 * @author lvheng
 */
@Api(description = "类目品牌排序表管理API", tags = "CateBrandSortRelController")
@RestController
@RequestMapping(value = "/brandSort/rel")
public class CateBrandSortRelController {

    @Autowired
    private CateBrandSortRelQueryProvider cateBrandSortRelQueryProvider;

    @Autowired
    private CateBrandSortRelProvider cateBrandSortRelProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BrandExcelService brandExcelService;

    @Autowired
    private GoodsBrandExcelProvider goodsBrandExcelProvider;

    @Autowired
    private ContractBrandQueryProvider contractBrandQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;


    @ApiOperation(value = "分页查询类目品牌排序表")
    @PostMapping("/page")
    public BaseResponse<CateBrandSortRelPageResponse> getPage(@RequestBody @Valid CateBrandSortRelPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("serialNo", "asc");
//        pageReq.putSort("cateId", "desc");
        MicroServicePage<CateBrandSortRelVO> page = cateBrandSortRelQueryProvider.page(pageReq).getContext().getCateBrandSortRelVOPage();


        List<ContractBrandVO> contractBrands;
        if (page.getTotalElements() > 0L) {
            List<Long> brandIds = page.getContent().stream().map(CateBrandSortRelVO::getBrandId).collect(Collectors.toList());
            ContractBrandListRequest contractBrandQueryRequest = new ContractBrandListRequest();
            contractBrandQueryRequest.setGoodsBrandIds(brandIds);
            contractBrands =
                    contractBrandQueryProvider.list(contractBrandQueryRequest).getContext().getContractBrandVOList();
        } else {
            contractBrands = new ArrayList<>();
        }

        page.getContent().forEach(info -> {
            GoodsBrandResponse goodsBrandResponse = new GoodsBrandResponse();
            BeanUtils.copyProperties(info, goodsBrandResponse);
            List<Long> storeIds =
                    contractBrands.stream().filter(contractBrand -> info.getBrandId().equals(contractBrand.getGoodsBrand().getBrandId()))
                            .map(ContractBrandVO::getStoreId).collect(Collectors.toList());
            if (!storeIds.isEmpty()) {
                List<StoreVO> stores = storeQueryProvider.listNoDeleteStoreByIds(
                        ListNoDeleteStoreByIdsRequest.builder().storeIds(storeIds).build()
                ).getContext().getStoreVOList();
                //过滤出已审核通过的店铺商家信息
                List<String> companyNames =
                        stores.stream().filter(store -> CheckState.CHECKED.equals(store.getAuditState())).map(store -> store.getCompanyInfo().getSupplierName()).distinct().collect(Collectors.toList());
                info.setStoreNames(companyNames);
            }
        });

        CateBrandSortRelPageResponse cateBrandSortRelPageResponse = new CateBrandSortRelPageResponse();
        cateBrandSortRelPageResponse.setCateBrandSortRelVOPage(new MicroServicePage<>(page.getContent(), pageReq.getPageable(), page.getTotalElements()));
        return BaseResponse.success(cateBrandSortRelPageResponse);
    }


    @ApiOperation(value = "根据id查询类目品牌排序表")
    @GetMapping("/{cateId}")
    public BaseResponse<CateBrandSortRelByIdResponse> getById(@PathVariable Long cateId) {
        if (cateId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        CateBrandSortRelByIdRequest idReq = new CateBrandSortRelByIdRequest();
        idReq.setCateId(cateId);
        return cateBrandSortRelQueryProvider.getById(idReq);
    }


    @ApiOperation(value = "修改类目品牌排序表")
    @PutMapping("/modify")
    public BaseResponse<CateBrandSortRelModifyResponse> modify(@RequestBody @Valid CateBrandSortRelModifyRequest modifyReq) {
        modifyReq.setUpdatePerson(commonUtil.getOperatorId());
        modifyReq.setUpdateTime(LocalDateTime.now());
        BaseResponse<CateBrandSortRelModifyResponse> modify = cateBrandSortRelProvider.modify(modifyReq);
        if (modify.getCode().equals(CommonErrorCode.SUCCESSFUL) && Objects.nonNull(modify.getContext().getCateBrandSortRelVO())) {
            // 品牌同步es操作
            // 根据商品的品牌id集合，修改所有的商品品牌排序序号
            GoodsBrandVO goodsBrandVO = new GoodsBrandVO();
            goodsBrandVO.setBrandId(modifyReq.getBrandId());
            goodsBrandVO.setBrandSeqNum(modifyReq.getSerialNo());
            goodsBrandVO.setBrandName(modify.getContext().getCateBrandSortRelVO().getName());
            esGoodsInfoElasticService.updateBrandSerialNo(goodsBrandVO, modifyReq.getCateId());
        }
        operateLogMQUtil.convertAndSend("类目品牌排序表管理", "修改类目品牌排序表", "操作成功");
        return modify;
    }


    @ApiOperation(value = "导出类目品牌排序表列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        CateBrandSortRelListRequest listReq = JSON.parseObject(decrypted, CateBrandSortRelListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        List<CateBrandSortRelVO> dataRecords = cateBrandSortRelQueryProvider.list(listReq).getContext().getCateBrandSortRelVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("类目品牌排序表列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        operateLogMQUtil.convertAndSend("类目品牌排序表管理", "导出类目品牌排序表列表", "操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<CateBrandSortRelVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("品牌名称", new SpelColumnRender<CateBrandSortRelVO>("name")),
            new Column("品牌别名", new SpelColumnRender<CateBrandSortRelVO>("alias")),
            new Column("排序序号", new SpelColumnRender<CateBrandSortRelVO>("serialNo"))
        };
        excelHelper.addSheet("类目品牌排序表列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }


    @ApiOperation(value = "下载导入品牌排序模板")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted",
            value = "加密", required = true)
    @RequestMapping(value = "/template/{encrypted}", method = RequestMethod.GET)
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
            operateLogMQUtil.convertAndSend("类目品牌排序表管理", "下载导入品牌排序模板", "操作成功");
        }
    }

    /**
     * 上传文件
     */
    @ApiOperation(value = "上传文件")
    @RequestMapping(value = "/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("类目品牌排序表管理", "上传文件", "上传文件");
        return BaseResponse.success(brandExcelService.upload(uploadFile, commonUtil.getOperatorId()));
    }


    @ApiOperation(value = "确认导入品牌排序")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext", value = "后缀", required = true)
    @RequestMapping(value = "/import/{ext}/{cateId}", method = RequestMethod.GET)
    public BaseResponse<Boolean> implBrands(@PathVariable String ext,@PathVariable Long cateId) {
        if(!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        brandExcelService.implBrands(cateId,commonUtil.getOperator().getUserId(),ext);
        operateLogMQUtil.convertAndSend("类目品牌排序表管理", "确认导入品牌排序", "操作成功");
        return BaseResponse.success(true);
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
        brandExcelService.downErrExcel(commonUtil.getOperatorId(), ext);
        operateLogMQUtil.convertAndSend("类目品牌排序表管理", "下载错误文档", "操作成功");
    }



}
