package com.wanmi.sbc.goods.api.provider.enterprise;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.enterprise.goods.*;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseBatchDeleteResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.enterprise.EnterpriseGoodsAuditResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * com.wanmi.sbc.goods.api.provider.goods.EnterpriseGoodsInfoProvider
 *
 * @author baijianzhong
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "EnterpriseGoodsInfoProvider")
public interface EnterpriseGoodsInfoProvider {

    /**
     * 批量修改商品的企业价格
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/batch-update-enterprise-price")
    BaseResponse<EnterpriseGoodsAddResponse> batchUpdateEnterprisePrice(@RequestBody @Valid EnterprisePriceBatchUpdateRequest request);

    /**
     * 单个修改企业价格的接口
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/update-enterprise-price")
    BaseResponse updateEnterprisePrice(@RequestBody @Valid EnterprisePriceUpdateRequest request);

    /**
     * 审核企业购商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/audit-enterprise")
    BaseResponse<EnterpriseGoodsAuditResponse> auditEnterpriseGoods(@RequestBody @Valid EnterpriseAuditCheckRequest request);

    /**
     * 审核企业购商品
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/batch-audit-enterprise")
    BaseResponse batchAuditEnterpriseGoods(@RequestBody @Valid EnterpriseAuditStatusBatchRequest request);

    /**
     * 删除企业购商品
     * @param deleteRequest
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/enterprise-sku-delete")
    BaseResponse deleteEnterpriseGoods(@RequestBody @Valid EnterpriseSkuDeleteRequest deleteRequest);

    /**
     * 删除企业购商品
     * @param deleteRequest
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/enterprise/enterprise-sku-batch-delete")
    BaseResponse<EnterpriseBatchDeleteResponse> batchDeleteEnterpriseGoods(@RequestBody @Valid EnterpriseSpuDeleteRequest deleteRequest);


}
