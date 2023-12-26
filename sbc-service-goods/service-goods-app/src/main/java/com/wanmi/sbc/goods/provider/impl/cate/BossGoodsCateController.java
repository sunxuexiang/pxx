package com.wanmi.sbc.goods.provider.impl.cate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.provider.cate.BossGoodsCateProvider;
import com.wanmi.sbc.goods.api.request.cate.BossGoodsCateDeleteByIdRequest;
import com.wanmi.sbc.goods.api.response.cate.BossGoodsCateDeleteByIdResponse;
import com.wanmi.sbc.goods.cate.service.S2bGoodsCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: ZhangLingKe
 * @Description:
 * @Date: 2018-11-07 18:47
 */
@Validated
@RestController
public class BossGoodsCateController implements BossGoodsCateProvider {

    @Autowired
    private S2bGoodsCateService s2bGoodsCateService;


    /**
     * @param request 根据主键删除商品分类 {@link BossGoodsCateDeleteByIdRequest}
     * @return  {@link BossGoodsCateDeleteByIdResponse}
     */
    @Override
    public BaseResponse<BossGoodsCateDeleteByIdResponse> deleteById(@RequestBody @Valid BossGoodsCateDeleteByIdRequest request) {
        if(Constants.yes.equals(s2bGoodsCateService.checkSignGoods(request.getCateId()))){
            throw new SbcRuntimeException(GoodsCateErrorCode.NOT_DELETE_FOR_GOODS);
        }
        return BaseResponse.success(new BossGoodsCateDeleteByIdResponse(s2bGoodsCateService.delete(request.getCateId())));
    }
}
