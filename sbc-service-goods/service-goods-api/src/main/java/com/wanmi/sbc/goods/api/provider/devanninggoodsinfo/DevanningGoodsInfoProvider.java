package com.wanmi.sbc.goods.api.provider.devanninggoodsinfo;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoListRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @author: wanggang
 * @createDate: 2018/11/6 10:08
 * @version: 1.0
 */
@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "DevanningGoodsInfoProvider")
public interface DevanningGoodsInfoProvider {

    /**
     * 添加DevanningGoodsInfoProvider数据
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/add")
    BaseResponse add(@RequestBody @Valid DevanningGoodsInfoRequest devanningGoodsInfoRequest);

    /**
     * 批量添加DevanningGoodsInfoProvider数据
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/addBatch")
    BaseResponse addBatch(@RequestBody @Valid DevanningGoodsInfoListRequest devanningGoodsInfoRequest);

    /**
     * 修改DevanningGoodsInfoProvider数据
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/update")
    BaseResponse update(@RequestBody @Valid DevanningGoodsInfoRequest devanningGoodsInfoRequest);


    /**
     * 添加DevanningGoodsInfoProvider数据
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getQueryList")
    BaseResponse<DevanningGoodsInfoResponse> getQueryList(@RequestBody @Valid DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest);


    /**
     * 添加DevanningGoodsInfoProvider数据
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getBQueryList")
    BaseResponse<DevanningGoodsInfoResponse> getBQueryList(@RequestBody @Valid DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest);


    /**
     * 获取步长最大的数据
     * @param request
     * @return
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getmaxdata")
    BaseResponse<DevanningGoodsInfoResponse> getmaxdata(@RequestBody @Valid DevanningGoodsInfoPageRequest request);

    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/batchUpdateBatchNos")
    BaseResponse batchUpdateBatchNos(@RequestBody @Valid GoodsInfoBatchNosModifyRequest request);

    /**
     * 查询StoreId
     */
    @PostMapping("/goods/${application.goods.version}/devanninggoodsinfo/getIdsByStoreId")
    BaseResponse<List<Long>> getIdsByStoreId(@RequestBody @Valid DevanningGoodsInfoPageRequest request);
}
