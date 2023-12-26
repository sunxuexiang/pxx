package com.wanmi.sbc.goods.provider.impl.enterprise;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.enterprise.EnterpriseGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.enterprise.goods.*;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseBatchDeleteResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsAuditResponse;
import com.wanmi.sbc.goods.bean.dto.BatchEnterPrisePriceDTO;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>企业购商品的增删改接口</p>
 * @Author baijianzhong
 */
@RestController
public class EnterpriseGoodsInfoController implements EnterpriseGoodsInfoProvider {

    @Autowired
    private GoodsInfoService goodsInfoService;

    /**
     * 批量修改商品的企业价格
     * @param request
     * @return
     */
    @Override
    public BaseResponse<EnterpriseGoodsAddResponse> batchUpdateEnterprisePrice(@RequestBody @Valid EnterprisePriceBatchUpdateRequest request) {
        //校验参数
        if(CollectionUtils.isEmpty(request.getBatchEnterPrisePriceDTOS())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //校验添加企业购商品的合法性
        List<String> goodsInfoIds = request.getBatchEnterPrisePriceDTOS().stream().map(BatchEnterPrisePriceDTO::getGoodsInfoId).collect(Collectors.toList());
        List<String> errorGoodsInfoIds = goodsInfoService.getInvalidEnterpriseByGoodsInfoIds(goodsInfoIds);
        if(CollectionUtils.isNotEmpty(errorGoodsInfoIds)){
            return BaseResponse.success(EnterpriseGoodsAddResponse.builder().goodsInfoIds(errorGoodsInfoIds).build());
        }
        goodsInfoService.batchUpdateEnterPrisePrice(request.getBatchEnterPrisePriceDTOS(),request.getEnterpriseGoodsAuditFlag());
        return BaseResponse.success(EnterpriseGoodsAddResponse.builder().build());
    }

    /**
     * 单个修改企业价格的接口
     * @param request
     * @return
     */
    @Override
    @LcnTransaction
    public BaseResponse updateEnterprisePrice(@RequestBody @Valid EnterprisePriceUpdateRequest request) {
        goodsInfoService.updateEnterPrisePrice(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 审核企业购商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse<EnterpriseGoodsAuditResponse> auditEnterpriseGoods(@RequestBody @Valid EnterpriseAuditCheckRequest request) {
        return BaseResponse.success(EnterpriseGoodsAuditResponse.builder()
                .backErrorCode(goodsInfoService.auditEnterpriseGoodsInfo(request))
                .build());
    }

    /**
     * 批量审核企业购商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse batchAuditEnterpriseGoods(@RequestBody @Valid EnterpriseAuditStatusBatchRequest request) {
        goodsInfoService.batchAuditEnterpriseGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除企业购商品
     * @param deleteRequest
     * @return
     */
    @Override
    public BaseResponse deleteEnterpriseGoods(@RequestBody @Valid EnterpriseSkuDeleteRequest deleteRequest) {
        goodsInfoService.deleteEnterpriseGoods(deleteRequest.getGoodsInfoId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据goodsId批量删除企业购商品
     * @param deleteRequest
     * @return
     */
    @Override
    public BaseResponse<EnterpriseBatchDeleteResponse> batchDeleteEnterpriseGoods(@RequestBody @Valid EnterpriseSpuDeleteRequest deleteRequest) {
        List<String> goodsInfoIds = goodsInfoService.batchDeleteEnterpriseGoods(deleteRequest.getGoodsId());
        return BaseResponse.success(EnterpriseBatchDeleteResponse.builder().goodsInfoIds(goodsInfoIds).build());
    }
}
