package com.wanmi.sbc.marketing.api.provider.pile;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.marketing.api.request.pile.*;
import com.wanmi.sbc.marketing.api.response.pile.PileActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p></p>
 * author: chenchang
 * Date: 2022-09-06
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "PileActivityProvider")
public interface PileActivityProvider {

    /**
     * 创建活动
     * @param request 创建活动请求结构 {@link PileActivityAddRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/add")
    BaseResponse<PileActivityDetailResponse> add(@RequestBody @Valid PileActivityAddRequest request);

    /**
     * 编辑活动
     * @param request 编辑活动请求结构 {@link PileActivityModifyRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/modify")
    BaseResponse modify(@RequestBody @Valid PileActivityModifyRequest request);

    /**
     * 关闭活动
     * @param request 关闭活动请求结构 {@link PileActivityCloseByIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/close")
    BaseResponse close(@RequestBody @Valid PileActivityCloseByIdRequest request);

    /**
     * 获取参与囤货活动商品
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/getStartPileActivityGoodsInfoIds")
    BaseResponse<List<String>> getStartPileActivityGoodsInfoIds();

    /**
     * 获取参与囤货活动商品
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/getStartPileActivity")
    BaseResponse<List<PileActivityVO>> getStartPileActivity();
    /**
     * 获取参与囤货活动商品虚拟库存
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/getStartPileActivityVirtualStock")
    BaseResponse<List<PileActivityGoodsVO>> getStartPileActivityPileActivityGoods(@RequestBody @Valid PileActivityPileActivityGoodsRequest request);


    /**
     * 变更参与囤货活动商品虚拟库存
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/updateVirtualStock")
    BaseResponse updateVirtualStock(@RequestBody @Valid List<PileActivityStockRequest> request);


    /**
     * 变更参与囤货活动商品虚拟库存
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/getByActivityId")
    BaseResponse<PileActivityVO> getByActivityId(@RequestBody @Valid PileActivityCloseByIdRequest activityId);

    /**
     * 删除囤货活动
     * @param request 删除囤货活动 {@link PileActivityDeleteByIdRequest}
     * @return {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/pile/activity/delete")
    BaseResponse deleteById(@RequestBody @Valid PileActivityDeleteByIdRequest request);
}
