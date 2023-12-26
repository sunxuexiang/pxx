package com.wanmi.sbc.goods.api.provider.goodsRecommendBackups;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.GoodsRecommendBackups.GoodsRecommendBackupsAddRequest;
import com.wanmi.sbc.goods.api.response.goodsRecommendBackups.GoodsRecommendBackupsListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>商品推荐调用</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@FeignClient(value = "${application.goods.name}", contextId = "GoodsRecommendBackupsSaveProvider")
public interface GoodsRecommendBackupsSaveProvider {


    /**
     * 新增商品推荐备份
     *
     * @param request {@link GoodsRecommendBackupsAddRequest}
     * @return 新增结果 {@link GoodsRecommendBackupsListResponse}
     */
    @PostMapping("/goods/${application.goods.version}/GoodsRecommendBackups/add")
    BaseResponse<GoodsRecommendBackupsListResponse> add(@RequestBody @Valid GoodsRecommendBackupsAddRequest request);

    /**
     *  删除全部
     */
    @PostMapping("/goods/${application.goods.version}/GoodsRecommendBackups/deleteAll")
    BaseResponse<GoodsUnitAddResponse> deleteAll();
}
