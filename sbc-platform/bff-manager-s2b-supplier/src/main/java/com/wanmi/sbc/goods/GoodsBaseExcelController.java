package com.wanmi.sbc.goods;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.StoreType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.excel.GoodsSupplierExcelProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.excel.GoodsSupplierExcelExportTemplateByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsAddRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsDTO;
import com.wanmi.sbc.goods.request.GoodsSupplierExcelImportRequest;
import com.wanmi.sbc.goods.service.GoodsBaseExcelService;
import com.wanmi.sbc.goods.service.SupplierGoodsExcelService;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadVideoResourceRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

/**
 * @author baijianzhong
 * @ClassName GoodsBaseExcelController
 * @Date 2020-12-06 00:46
 * @Description TODO
 **/
@Slf4j
@RestController(value = "/goods")
@RequestMapping(value = "/goods")
public class GoodsBaseExcelController {


    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsBaseExcelService goodsBaseExcelService;

    @Autowired
    private GoodsProvider goodsProvider;
    @Autowired
    GoodsAresProvider goodsAresProvider;
    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private GoodsSupplierExcelProvider goodsSupplierExcelProvider;
    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;
    @Autowired
    private SupplierGoodsExcelService supplierGoodsExcelService;
    @Autowired
    private YunServiceProvider yunServiceProvider;
    /**
     * 下载模板
     */
    @ApiOperation(value = "下载模板")
    @RequestMapping(value = "/goodsModify/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        goodsBaseExcelService.exportTemplate();
        operateLogMQUtil.convertAndSend("商品模板","下载模板","操作成功");
    }


    /**
     * 下载错误文档
     */
    @ApiOperation(value = "下载错误文档")
    @RequestMapping(value = "/goodsModify/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsBaseExcelService.downErrExcel(commonUtil.getOperatorId(), ext,"goodsModify");
        operateLogMQUtil.convertAndSend("商品模板","下载错误文档","操作成功");
    }

    /**
     * 上传模板
     *
     * @param uploadFile
     * @return
     */
    @ApiOperation(value = "上传模板")
    @RequestMapping(value = "/goodsModify/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> upload(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("商品模板","上传模板","上传模板");
        return BaseResponse.success(goodsBaseExcelService.upload(uploadFile, commonUtil.getOperatorId(),"goodsModify"));
    }

    /**
     * 商品修改导入
     *
     * @param ext
     * @return
     */
    @ApiOperation(value = "类目导入")
    @RequestMapping(value = "/goodsModify/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> importGoodsCate(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> strings = goodsBaseExcelService.importGoodsModify(commonUtil.getOperatorId(), ext, commonUtil.getStoreId());
        if (CollectionUtils.isNotEmpty(strings)){
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(strings).build());
            operateLogMQUtil.convertAndSend("商品模板","类目导入","操作成功");
        }
        return BaseResponse.success(Boolean.TRUE);
    }

    /**
     * 下载代客下单商品模板
     */
    @ApiOperation(value = "下载代客下单商品模板")
    @RequestMapping(value = "/goodsBatchImport/excel/template/{encrypted}", method = RequestMethod.GET)
    public void goodsBatchImportTemplate(@PathVariable String encrypted) {
        goodsBaseExcelService.goodsBatchImportTemplate();
        operateLogMQUtil.convertAndSend("商品模板","下载代客下单商品模板","操作成功");
    }


    /**
     * 上传模板
     *
     * @param uploadFile
     * @return
     */
    @ApiOperation(value = "上传模板")
    @RequestMapping(value = "/goodsAdd/excel/upload", method = RequestMethod.POST)
    public BaseResponse<String> goodsAdd(@RequestParam("uploadFile") MultipartFile uploadFile) {
        operateLogMQUtil.convertAndSend("商品模板","上传模板","上传模板");
        //验证上传参数
        if (Objects.isNull(uploadFile)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        String resourceUrl;
            if (uploadFile == null || uploadFile.getSize() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            try {
                String fileKey = commonUtil.getCompanyInfoId() + commonUtil.getStoreId() + "/" + commonUtil.getOperatorId()+ ".xls";
                // 上传
                 resourceUrl = yunServiceProvider.uploadExclFile(YunUploadVideoResourceRequest.builder()
                        .storeId(commonUtil.getStoreId())
                        .companyInfoId(commonUtil.getCompanyInfoId())
                        .resourceType(ResourceType.EXCEL)
                        .resourceName(uploadFile.getOriginalFilename())
                         .resourceKey(fileKey)
                        .content(uploadFile.getBytes())
                        .build()).getContext();
            } catch (Exception e) {
                log.error("uploadExcelResource error: {}", e.getMessage());
                return BaseResponse.FAILED();
            }
            log.info("阿里云上传完成："+resourceUrl);
        return BaseResponse.success(resourceUrl);


    }
    /**
     * 下载错误文档
     */
    @ApiOperation(value = "下载错误文档")
    @RequestMapping(value = "/goodsAdd/excel/err/{ext}/{decrypted}", method = RequestMethod.GET)
    public void downAddErrExcel(@PathVariable String ext, @PathVariable String decrypted) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsBaseExcelService.downErrExcel(commonUtil.getOperatorId(), ext,"goodsAdd");
        operateLogMQUtil.convertAndSend("商品模板","下载错误文档","操作成功");
    }
    /**
     * 商户下载模板
     */
    @ApiOperation(value = "下载模板")
    @RequestMapping(value = "/goodsAdd/storeExcel/template/{encrypted}", method = RequestMethod.GET)
    public void storeTemplate(@PathVariable String encrypted) {
        GoodsSupplierExcelExportTemplateByStoreIdRequest request =
                new GoodsSupplierExcelExportTemplateByStoreIdRequest();
        request.setStoreId(commonUtil.getStoreId());
        String file = goodsSupplierExcelProvider.storeExportTemplate(request).getContext().getFile();
        if(StringUtils.isNotBlank(file)){
            try {
                String fileName = URLEncoder.encode("新增商品导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            }catch (Exception e){
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
        }
        operateLogMQUtil.convertAndSend("商品模板","新增商品导入模板下载","操作成功");
    }

    /**
     * 确认导入商品
     */
    @SneakyThrows
    @ApiOperation(value = "确认导入商品")
    @RequestMapping(value = "/storeImport/{url}", method = RequestMethod.GET)
    public BaseResponse<Boolean> storeImport(@PathVariable String url) {
        if(StringUtils.isEmpty(url)){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        if(companyInfo == null){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        GoodsSupplierExcelImportRequest importRequest = new GoodsSupplierExcelImportRequest();
        //修改成阿里云地址
        final BASE64Decoder decoder = new BASE64Decoder();
        importRequest.setExt(new String(decoder.decodeBuffer(url), "UTF-8"));
        importRequest.setUserId(commonUtil.getOperatorId());
        importRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        importRequest.setStoreId(commonUtil.getStoreId());
        importRequest.setCompanyType(companyInfo.getCompanyType());
        importRequest.setSupplierName(companyInfo.getSupplierName());
        importRequest.setType(StoreType.SUPPLIER);
        List<String> skuIds = supplierGoodsExcelService.storeImplGoods(importRequest);
        //加入ES
        if(org.apache.commons.collections.CollectionUtils.isNotEmpty(skuIds)){
            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(skuIds).build());
            //ares埋点-商品-后台导入商品sku
            goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("addGoodsSkuIds",skuIds.toArray()));
        }
        operateLogMQUtil.convertAndSend("商品","商品模板导入","商品模板导入");

        GoodsAddRequest goodsAddRequest =new GoodsAddRequest();
        GoodsDTO goodsDTO=new GoodsDTO();
        goodsDTO.setGoodsInfoIds(skuIds);
        goodsAddRequest.setGoods(goodsDTO);
        try {
            goodsProvider.sysnErpSku(goodsAddRequest);
        }catch (Exception e){
            operateLogMQUtil.convertAndSend("商品","商品模板导入","商品模板导入异常"+ JSONObject.toJSON(skuIds));
        }
        return BaseResponse.success(Boolean.TRUE);
    }
}