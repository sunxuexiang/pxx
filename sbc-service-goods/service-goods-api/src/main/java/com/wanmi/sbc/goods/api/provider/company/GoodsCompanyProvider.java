package com.wanmi.sbc.goods.api.provider.company;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandModifyRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyAddRequest;
import com.wanmi.sbc.goods.api.request.company.GoodsCompanyModifyRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandAddResponse;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandModifyResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyAddResponse;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyModifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对厂商操作接口</p>
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "GoodsCompanyProvider")
public interface GoodsCompanyProvider {

    /**
     * 新增厂商
     *
     * @param request 厂商新增结构 {@link GoodsCompanyAddRequest}
     * @return 新增厂商信息 {@link GoodsCompanyAddResponse}
     */
    @PostMapping("/goods/${application.goods.version}/company/add")
    BaseResponse<GoodsCompanyAddResponse> add(@RequestBody @Valid GoodsCompanyAddRequest request);

    /**
     * 修改品牌
     *
     * @param request 品牌修改结构 {@link GoodsBrandModifyRequest}
     * @return 修改品牌信息 {@link GoodsBrandModifyResponse}
     */
    @PostMapping("/goods/${application.goods.version}/company/modify")
    BaseResponse<GoodsCompanyModifyResponse> modify(@RequestBody @Valid GoodsCompanyModifyRequest request);
}
