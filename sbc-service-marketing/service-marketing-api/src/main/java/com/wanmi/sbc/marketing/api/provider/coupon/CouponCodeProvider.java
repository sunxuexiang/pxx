package com.wanmi.sbc.marketing.api.provider.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.api.request.coupon.*;
import com.wanmi.sbc.marketing.bean.vo.CouponInfoGetRecordVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>对优惠券码操作接口</p>
 * Created by daiyitian on 2018-11-23-下午6:23.
 */
@FeignClient(value = "${application.marketing.name}", url="${feign.url.marketing:#{null}}", contextId = "CouponCodeProvider")
public interface CouponCodeProvider {

    /**
     * 领取优惠券
     *
     * @param request 优惠券领取请求结构 {@link CouponFetchRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/fetch")
    BaseResponse fetch(@RequestBody @Valid CouponFetchRequest request);

    /**
     * 批量更新券码使用状态
     *
     * @param request 批量修改请求结构 {@link CouponCodeBatchModifyRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/batch-modify")
    BaseResponse batchModify(@RequestBody @Valid CouponCodeBatchModifyRequest request);

    /**
     * 根据id撤销优惠券使用
     *
     * @param request 包含id的撤销使用请求结构 {@link CouponCodeReturnByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/return-by-id")
    BaseResponse returnById(@RequestBody @Valid CouponCodeReturnByIdRequest request);

    /**
     * 精准发券
     * @param request
     * @return 操作结果 {@link BaseResponse}
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/precision-vouchers")
    BaseResponse precisionVouchers(@RequestBody @Valid CouponCodeBatchSendCouponRequest request);

    /**
     * 数据迁移：旧coupon_code按照新的分表规则进行拆分保存至新表中
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/data-migration")
    BaseResponse dataMigrationFromCouponCode();

    /**
     * 批量发券
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/send-batch")
    BaseResponse sendBatchCouponCodeByCustomerList(@RequestBody @Valid CouponCodeBatchSendCouponRequest request);

    /**
     * 优惠券领取记录
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/get-record")
    BaseResponse<MicroServicePage<CouponInfoGetRecordVO>> getRecord(@RequestBody @Valid CouponInfoGetRecordRequest request);

    /**
     * 删除用户优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/del-by-customerId-and-couponIds")
    BaseResponse delByCustomerIdAndCouponIds(@RequestBody @Valid CouponCodeDeleteRequest request);

    /**
     * 撤回删除用户的优惠券
     * @param request
     * @return
     */
    @PostMapping("/marketing/${application.marketing.version}/coupon/code/recall-del")
    BaseResponse recallDelCouponCodes(@RequestBody @Valid CouponCodeDeleteRequest request);
}
