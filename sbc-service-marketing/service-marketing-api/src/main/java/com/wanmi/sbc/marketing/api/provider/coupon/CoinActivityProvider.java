package com.wanmi.sbc.marketing.api.provider.coupon;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.api.request.coupon.BuyGoodsOrFullOrderSendCouponRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityModifyRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinActivityTerminationRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinAddActiviStoreRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinAddActivitGoodsRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CoinRecordPageRequest;
import com.wanmi.sbc.marketing.api.request.coupon.TakeBackOrderCoinRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityDetailResponse;
import com.wanmi.sbc.marketing.api.response.coupon.CoinActivityPageResponse;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDetailDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityRecordDto;
import com.wanmi.sbc.marketing.bean.dto.CoinActivityStoreRecordDetailDTO;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityVO;
import com.wanmi.sbc.marketing.bean.vo.CoinGoodsVo;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/5/22 9:34
 */
@FeignClient(value = "${application.marketing.name}", contextId = "CoinActivityProvider", url="${feign.url.marketing:#{null}}" )
public interface CoinActivityProvider {

    /**
     * 创建指定商品赠金币
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coin/activity/add")
    BaseResponse add(@RequestBody @Valid CoinActivityAddRequest request);

    /**
     * 修改
     * @param request
     * @return
     */
    @PutMapping("/marketing/${application.marketing.version}/coin/activity/modify")
    BaseResponse update(@RequestBody @Valid CoinActivityModifyRequest request);


    /**
     * 获取详情
     * @param id
     * @return
     */
    @GetMapping("/marketing/${application.marketing.version}/coin/activity/{id}")
    BaseResponse<CoinActivityDetailResponse> detail(@PathVariable("id") String id);

    /**
     * 删除
     *
     * @param id
     * @param operatorId
     * @return
     */
    @DeleteMapping("/marketing/${application.marketing.version}/coin/activity/{id}/{operatorId}")
    BaseResponse deleteById(@PathVariable("id") String id, @PathVariable("operatorId") String operatorId);

    /**
     * 终止
     * @param id
     * @param operatorId
     * @return
     */
    @PutMapping("/marketing/${application.marketing.version}/coin/activity/termination/{id}/{operatorId}")
    BaseResponse terminationById(@PathVariable("id") String id, @PathVariable("operatorId") String operatorId);

    /**
     * 终止单个商品
     * @param request
     * @return
     */
    @PutMapping("/marketing/${application.marketing.version}/coin/activity/termination/goods")
    BaseResponse terminationGoods(@RequestBody @Valid CoinActivityTerminationRequest request);

    /**
     * 列表
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coin/activity/page")
    BaseResponse<CoinActivityPageResponse> page(@RequestBody @Valid CoinActivityPageRequest request);

    /**
     * 添加活动商品
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coin/activity/addActivityGoods")
    BaseResponse addActivityGoods(@RequestBody @Valid CoinAddActivitGoodsRequest request);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/checkSendCoin")
    BaseResponse<List<CoinGoodsVo>> checkSendCoin(@RequestBody @Valid BuyGoodsOrFullOrderSendCouponRequest request);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityBySkuIds")
    BaseResponse<List<CoinActivityVO>> queryCoinActivityBySkuIds(@RequestBody List<String> skuIds);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/saveCoinRecord")
    BaseResponse<List<Long>> saveCoinRecord(@RequestBody List<CoinActivityRecordDto> request);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordByRecordIds")
    BaseResponse<List<CoinActivityRecordDto>> queryCoinActivityRecordByRecordIds(@RequestBody List<Long> recordIds);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordByOrderId")
    BaseResponse<List<CoinActivityRecordDto>> queryCoinActivityRecordByOrderId(@RequestParam("orderId") String orderId);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordByOrderIdAndSkuIds")
    BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordByOrderIdAndSkuIds(@RequestParam("orderId") String orderId, @RequestParam("skuIds") List<String> skuIds);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/recordPage")
    BaseResponse<MicroServicePage<CoinActivityRecordDto>> recordPage(@RequestBody CoinRecordPageRequest request);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordDetailByOrderId")
    BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordDetailByOrderId(@RequestParam("orderId") String orderId);


    @PostMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordDetailByOrderIds")
    BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordDetailByOrderIds(@RequestBody List<String> orderIds);

    @GetMapping("/marketing/${application.marketing.version}/coin/activity/record/{orderId}")
    BaseResponse<CoinActivityRecordDto> recordByOrderId(@PathVariable("orderId") String orderId);
    @GetMapping("/marketing/${application.marketing.version}/coin/activity/record/queryGoodsCancelNum")
    BaseResponse<Integer> queryGoodsCancelNum(@RequestParam("orderId") String orderId, @RequestParam("goodsInfoId") String goodsInfoId);

    @GetMapping("/marketing/${application.marketing.version}/coin/activity/recordSendNo/{sendNo}")
    BaseResponse<CoinActivityRecordDto> recordBySendNo(@PathVariable("sendNo") String sendNo);

    @GetMapping("/marketing/${application.marketing.version}/coin/activity/queryCoinActivityRecordIsExist")
    BaseResponse<Boolean> queryCoinActivityRecordIsExist(@RequestParam("tid") String tid);
    
    @GetMapping("/marketing/${application.marketing.version}/coin/activity/sendOrderCoin")
    BaseResponse sendOrderCoin(@RequestParam("tid") String tid);
    
    @PostMapping("/marketing/${application.marketing.version}/coin/activity/takeBackOrderCoin")
    BaseResponse takeBackOrderCoin(@RequestBody TakeBackOrderCoinRequest request);
    
    @PostMapping("/marketing/${application.marketing.version}/coin/activity/addActivityStore")
    BaseResponse addActivityStore(@RequestBody @Valid CoinAddActiviStoreRequest request);

    @PostMapping("/marketing/${application.marketing.version}/coin/activity/orderCoinTips")
	BaseResponse<String> orderCoinTips();

    @GetMapping("/marketing/${application.marketing.version}/coin/activity/querySendRecord")
	BaseResponse<List<CoinActivityStoreRecordDetailDTO>> querySendRecord(@RequestParam("orderNo") String orderNo);
    
    
}
