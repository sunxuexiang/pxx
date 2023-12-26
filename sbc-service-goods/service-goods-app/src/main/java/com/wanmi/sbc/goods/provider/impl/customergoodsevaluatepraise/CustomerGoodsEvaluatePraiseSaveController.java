package com.wanmi.sbc.goods.provider.impl.customergoodsevaluatepraise;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsEvaluateErrorCode;
import com.wanmi.sbc.goods.api.provider.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseSaveProvider;
import com.wanmi.sbc.goods.api.request.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseAddRequest;
import com.wanmi.sbc.goods.api.request.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseDelByIdRequest;
import com.wanmi.sbc.goods.api.request.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseModifyRequest;
import com.wanmi.sbc.goods.api.response.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseAddResponse;
import com.wanmi.sbc.goods.api.response.customergoodsevaluatepraise.CustomerGoodsEvaluatePraiseModifyResponse;
import com.wanmi.sbc.goods.customergoodsevaluatepraise.model.root.CustomerGoodsEvaluatePraise;
import com.wanmi.sbc.goods.customergoodsevaluatepraise.service.CustomerGoodsEvaluatePraiseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>会员商品评价点赞关联表保存服务接口实现</p>
 *
 * @author lvzhenwei
 * @date 2019-05-07 14:25:25
 */
@RestController
@Validated
public class CustomerGoodsEvaluatePraiseSaveController implements CustomerGoodsEvaluatePraiseSaveProvider {
    @Autowired
    private CustomerGoodsEvaluatePraiseService customerGoodsEvaluatePraiseService;

    @Override
    public BaseResponse<CustomerGoodsEvaluatePraiseAddResponse> add(@RequestBody @Valid CustomerGoodsEvaluatePraiseAddRequest customerGoodsEvaluatePraiseAddRequest) {
        CustomerGoodsEvaluatePraise customerGoodsEvaluatePraise = new CustomerGoodsEvaluatePraise();
        KsBeanUtil.copyPropertiesThird(customerGoodsEvaluatePraiseAddRequest, customerGoodsEvaluatePraise);
        CustomerGoodsEvaluatePraise customerGoodsEvaluatePraise1 = new CustomerGoodsEvaluatePraise();
        try {
            customerGoodsEvaluatePraise1 = customerGoodsEvaluatePraiseService.add(customerGoodsEvaluatePraise);
        } catch (DataIntegrityViolationException e) {
            String errMsg = e.getMessage();
            if(StringUtils.isNotBlank(errMsg) && errMsg.indexOf("uni_customer_evaluate")!=-1) {
                throw new SbcRuntimeException(GoodsEvaluateErrorCode.GOODS_EVALUATE_REPEAT_PRAISE);
            }
            e.printStackTrace();
        }
        return BaseResponse.success(new CustomerGoodsEvaluatePraiseAddResponse(
                customerGoodsEvaluatePraiseService.wrapperVo(customerGoodsEvaluatePraise1)));
    }

    @Override
    public BaseResponse<CustomerGoodsEvaluatePraiseModifyResponse> modify(@RequestBody @Valid CustomerGoodsEvaluatePraiseModifyRequest customerGoodsEvaluatePraiseModifyRequest) {
        CustomerGoodsEvaluatePraise customerGoodsEvaluatePraise = new CustomerGoodsEvaluatePraise();
        KsBeanUtil.copyPropertiesThird(customerGoodsEvaluatePraiseModifyRequest, customerGoodsEvaluatePraise);
        return BaseResponse.success(new CustomerGoodsEvaluatePraiseModifyResponse(
                customerGoodsEvaluatePraiseService.wrapperVo(customerGoodsEvaluatePraiseService.modify(customerGoodsEvaluatePraise))));
    }

    @Override
    public BaseResponse deleteById(@RequestBody @Valid CustomerGoodsEvaluatePraiseDelByIdRequest customerGoodsEvaluatePraiseDelByIdRequest) {
        customerGoodsEvaluatePraiseService.deleteById(customerGoodsEvaluatePraiseDelByIdRequest.getId());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteByIdList(@RequestBody @Valid CustomerGoodsEvaluatePraiseDelByIdListRequest customerGoodsEvaluatePraiseDelByIdListRequest) {
        customerGoodsEvaluatePraiseService.deleteByIdList(customerGoodsEvaluatePraiseDelByIdListRequest.getIdList());
        return BaseResponse.SUCCESSFUL();
    }

}

