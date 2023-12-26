package com.wanmi.sbc.marketing.provider.impl.coupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.levelrights.CustomerLevelRightsCouponAnalyseProvider;
import com.wanmi.sbc.customer.api.provider.levelrights.CustomerLevelRightsRelQueryProvider;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerByGrowthValueRequest;
import com.wanmi.sbc.customer.api.request.level.CustomerLevelByIdsRequest;
import com.wanmi.sbc.customer.api.request.levelrights.CustomerLevelRightsRelRequest;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelRightsRelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelRightsVO;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCustomerRightsProvider;
import com.wanmi.sbc.marketing.api.response.coupon.GetCouponGroupResponse;
import com.wanmi.sbc.marketing.coupon.model.root.CouponActivityConfig;
import com.wanmi.sbc.marketing.coupon.model.root.CouponInfo;
import com.wanmi.sbc.marketing.coupon.repository.CouponInfoRepository;
import com.wanmi.sbc.marketing.coupon.service.CouponActivityConfigService;
import com.wanmi.sbc.marketing.coupon.service.CouponCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>会员等级权益发放优惠券</p>
 */
@Validated
@RestController
public class CouponCustomerRightsController implements CouponCustomerRightsProvider {

    @Autowired
    private CustomerLevelRightsCouponAnalyseProvider customerLevelRightsCouponAnalyseProvider;

    @Autowired
    private CustomerLevelRightsRelQueryProvider customerLevelRightsRelQueryProvider;

    @Autowired
    private CustomerLevelQueryProvider customerLevelQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CouponActivityConfigService couponActivityConfigService;

    @Autowired
    private CouponInfoRepository couponInfoRepository;

    @Autowired
    private CouponCodeService couponCodeService;

    /**
     * 会员等级权益发放优惠券
     *
     * @return
     */
    @Override
    public BaseResponse customerRightsIssueCoupons() {
        // 查询需要发放优惠券的权益
        List<CustomerLevelRightsVO> rightsList = customerLevelRightsCouponAnalyseProvider.queryIssueCouponsData()
                .getContext().getCustomerLevelRightsVOList();
        rightsList.forEach(rights -> {
            // 查询包含该权益的等级id
            List<Long> levelIds = customerLevelRightsRelQueryProvider
                    .listByRightsId(CustomerLevelRightsRelRequest.builder().rightsId(rights.getRightsId()).build())
                    .getContext()
                    .getCustomerLevelRightsRelVOList()
                    .stream()
                    .map(CustomerLevelRightsRelVO::getCustomerLevelId)
                    .distinct()
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(levelIds)) {
                // 根据等级id查询等级详情信息
                List<CustomerLevelVO> customerLevels = customerLevelQueryProvider
                        .listCustomerLevelByIds(CustomerLevelByIdsRequest.builder().customerLevelIds(levelIds).build())
                        .getContext()
                        .getCustomerLevelVOList();
                // 高等级必然拥有低等级所有权益，所以只需找出包含该权益的会员等级中最低成长值即可
                Long growthValue = customerLevels.stream()
                        .min(Comparator.comparing(CustomerLevelVO::getGrowthValue))
                        .get().getGrowthValue();
                // 查询达到该成长值的会员id列表
                List<String> customerIds = customerQueryProvider
                        .listCustomerIdByGrowthValue(new CustomerByGrowthValueRequest(growthValue))
                        .getContext().getCustomerIdList();

                // 查询券礼包权益关联的优惠券活动配置列表
                List<CouponActivityConfig> couponActivityConfigList = couponActivityConfigService.queryByActivityId(rights.getActivityId());
                // 根据配置查询需要发放的优惠券列表
                List<CouponInfo> couponInfoList = couponInfoRepository.queryByIds(couponActivityConfigList.stream().map(
                        CouponActivityConfig::getCouponId).collect(Collectors.toList()));
                // 组装优惠券发放数据
                List<GetCouponGroupResponse> getCouponGroupResponse = KsBeanUtil.copyListProperties(couponInfoList, GetCouponGroupResponse.class);
                getCouponGroupResponse = getCouponGroupResponse.stream().peek(item -> couponActivityConfigList.forEach(config -> {
                    if (item.getCouponId().equals(config.getCouponId())) {
                        item.setTotalCount(config.getTotalCount());
                    }
                })).collect(Collectors.toList());
                // 遍历用户列表，批量发放优惠券
                List<GetCouponGroupResponse> finalGetCouponGroupResponse = getCouponGroupResponse;
                customerIds.forEach(customerId -> couponCodeService.sendBatchCouponCodeByCustomer(finalGetCouponGroupResponse, customerId, rights.getActivityId()));
            }
        });
        return BaseResponse.SUCCESSFUL();
    }

}
