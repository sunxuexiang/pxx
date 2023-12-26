package com.wanmi.sbc.goods.provider.impl.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateExcelProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateExcelImportRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateExcelImportResponse;
import com.wanmi.sbc.goods.cate.service.GoodsCateExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: songhanlin
 * @Date: Created In 10:19 2018-12-18
 * @Description: TODO
 */
@RestController
@Validated
public class GoodsCateExcelController implements GoodsCateExcelProvider {

    @Autowired
    private GoodsCateExcelService goodsCateExcelService;

    /**
     * 导入商品分类
     *
     * @param request {@link GoodsCateExcelImportRequest}
     * @return 返回 {@link GoodsCateExcelImportResponse}
     */
    @Override
    public BaseResponse<GoodsCateExcelImportResponse> importGoodsCate(@RequestBody GoodsCateExcelImportRequest request) {
        String ext = request.getExt();
        String userId = request.getUserId();
        GoodsCateExcelImportResponse response = new GoodsCateExcelImportResponse();
        response.setFlag(goodsCateExcelService.importGoodsCate(userId, ext));
        return BaseResponse.success(response);
    }
}
