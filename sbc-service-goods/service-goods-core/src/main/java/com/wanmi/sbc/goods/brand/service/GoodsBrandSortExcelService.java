package com.wanmi.sbc.goods.brand.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 商品品牌排序
 *
 * @author yang
 * @since 2020/12/32
 */
@Service("GoodsBrandSortExcelService")
public class GoodsBrandSortExcelService {

    @Value("classpath:brand_sort_template.xls")
    private Resource templateFile;

    /**
     * 导出品牌排序模板
     *
     * @return
     */
    public String exportTemlate() {
        if (templateFile == null || !templateFile.exists()) {
            throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
        }
        try (InputStream inputStream = templateFile.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Workbook wk = WorkbookFactory.create(inputStream)) {
            wk.write(byteArrayOutputStream);
            return new BASE64Encoder().encode(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED, e);
        }
    }
}
