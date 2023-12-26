package com.wanmi.sbc.goods.api.provider.goodsRecommendBackups;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.GoodsRecommendBackups.GoodsRecommendBackupsQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsRecommendBackups.GoodsRecommendBackupsListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐备份调用</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "${application.goods.name}", contextId = "GoodsRecommendBackupsQueryProvider")
public interface GoodsRecommendBackupsQueryProvider {

    /**
     *  集合数据
     *
     * @param request {@link StoreGoodsUnitQueryRequest}
     * @return 修改结果 {@link GoodsUnitListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/goods-recommend-backups/get-list")
    BaseResponse<GoodsRecommendBackupsListResponse> getList(@RequestBody @Valid GoodsRecommendBackupsQueryRequest request);


}
