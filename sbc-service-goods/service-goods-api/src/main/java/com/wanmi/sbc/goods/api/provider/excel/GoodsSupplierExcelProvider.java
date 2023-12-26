package com.wanmi.sbc.goods.api.provider.excel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.excel.GoodsSupplierExcelExportTemplateByStoreIdRequest;
import com.wanmi.sbc.goods.api.request.excel.GoodsSupplierExcelExportTemplateIEPByStoreIdRequest;
import com.wanmi.sbc.goods.api.response.excel.GoodsSupplierExcelExportTemplateIEPResponse;
import com.wanmi.sbc.goods.api.response.excel.GoodsSupplierExcelExportTemplateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * com.wanmi.sbc.goods.api.provider.goodsexcel.GoodsExcelProvider
 * 商家商品excel操作接口，对应改造之前的S2bGoodsExcelService
 * @author lipeng
 * @dateTime 2018/11/2 上午9:49
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsSupplierExcelProvider")
public interface GoodsSupplierExcelProvider {

    /**
     * 获取商家excel模板
     *
     * @param request {@link GoodsSupplierExcelExportTemplateByStoreIdRequest}
     * @return base64位文件流字符串 {@link GoodsSupplierExcelExportTemplateResponse}
     */
    @PostMapping("/goods/${application.goods.version}/supplier/excel/export-template")
    BaseResponse<GoodsSupplierExcelExportTemplateResponse> supplierExportTemplate(
            @RequestBody @Valid GoodsSupplierExcelExportTemplateByStoreIdRequest request);

    /**
     * 获取商家excel模板
     *
     * @param request {@link GoodsSupplierExcelExportTemplateIEPByStoreIdRequest}
     * @return base64位文件流字符串 {@link GoodsSupplierExcelExportTemplateIEPResponse}
     */
    @PostMapping("/goods/${application.goods.version}/supplier/excel/export-template-iep")
    BaseResponse<GoodsSupplierExcelExportTemplateIEPResponse> supplierExportTemplateIEP(
            @RequestBody @Valid GoodsSupplierExcelExportTemplateIEPByStoreIdRequest request);


    /**
     * 第三方获取新增商家excel模板
     *
     * @param request {@link GoodsSupplierExcelExportTemplateByStoreIdRequest}
     * @return base64位文件流字符串 {@link GoodsSupplierExcelExportTemplateResponse}
     */
    @PostMapping("/goods/${application.goods.version}/supplier/excel/store-export-template")
    BaseResponse<GoodsSupplierExcelExportTemplateIEPResponse> storeExportTemplate(
            @RequestBody @Valid GoodsSupplierExcelExportTemplateByStoreIdRequest request);
}
