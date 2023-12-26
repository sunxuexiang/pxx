package com.wanmi.sbc.coupon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.marketing.bean.enums.CouponCodeStatus;
import com.wanmi.sbc.marketing.bean.vo.CouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.CouponVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponCodeVO;
import com.wanmi.sbc.marketing.bean.vo.StoreCouponVO;
import com.wanmi.sbc.order.response.TradeConfirmResponse;

@Service
public class CouponService {
	
    @Autowired
    private StoreQueryProvider storeQueryProvider;

	/**
	 * 转换我的优惠券列表成app端易使用的格式
	 * @author zhangchen
	 * @date 2023年7月7日 下午3:41:24
	 * @param couponViews
	 * @return
	 */
	public List<StoreCouponCodeVO> parseCouponCodeVOList(List<CouponCodeVO> couponViews) {
		List<StoreCouponCodeVO> resList = new ArrayList<>();
		if (CollectionUtils.isEmpty(couponViews)) {
		     return resList;
		}
		
		Map<Long, List<CouponCodeVO>> collect = couponViews.stream().collect(Collectors.groupingBy(CouponCodeVO::getStoreId));
        // 先组装平台券
        List<CouponCodeVO> platformCoupons = collect.remove(-1L);
        if (CollectionUtils.isNotEmpty(platformCoupons)) {
        	StoreCouponCodeVO platform = StoreCouponCodeVO.builder()
            		.storeId(-1L)
            		.couponCodeVOs(platformCoupons)
            		.storeName("平台")
            		.build();
            resList.add(platform);
		}
		
        // 组装店铺券
		collect.forEach((k, v) -> {
			CouponCodeVO couponCodeVO = v.get(0);
			StoreCouponCodeVO res = StoreCouponCodeVO.builder()
					.storeId(k)
					.couponCodeVOs(v)
					.storeName(couponCodeVO.getStoreName())
					.companyType(couponCodeVO.getCompanyType())
					.build();
			resList.add(res);
		});
	   return resList;
	}
	
	/**
	 * 转换优惠券列表成app端易使用的格式
	 * @author zhangchen
	 * @param couponViews
	 * @return
	 */
	public List<StoreCouponVO> parseCouponVOList(List<CouponVO> couponViews) {
		List<StoreCouponVO> resList = new ArrayList<>();
		if (CollectionUtils.isEmpty(couponViews)) {
			return resList;
		}

		Map<Long, List<CouponVO>> collect = couponViews.stream().collect(Collectors.groupingBy(CouponVO::getStoreId));
		// 先组装平台券
		List<CouponVO> platformCoupons = collect.remove(-1L);
		if (CollectionUtils.isNotEmpty(platformCoupons)) {
			StoreCouponVO platform = StoreCouponVO.builder().storeId(-1L).couponVOs(platformCoupons).storeName("平台").build();
			resList.add(platform);
		}

		// 组装店铺券
		BaseResponse<ListStoreByIdsResponse> listByIds = storeQueryProvider
				.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<>(collect.keySet())).build());
		collect.forEach((k, v) -> {
			StoreCouponVO res = StoreCouponVO.builder().storeId(k).couponVOs(v).build();
			resList.add(res);
			Optional<StoreVO> findFirst = listByIds.getContext().getStoreVOList().stream().filter(s -> s.getStoreId().equals(k))
					.findFirst();
			findFirst.ifPresent(s -> {
				res.setStoreName(s.getStoreName());
				res.setCompanyType(s.getCompanyType());
			});

		});
		return resList;
	}
	
	/**
	 * 将我的优惠券处理成商家入驻版本格式，分为可用与不可用
	 * @author zhangchen
	 * @param confirmResponse
	 * @param couponCodeList
	 */
	public void setCouponCodes(TradeConfirmResponse confirmResponse, List<CouponCodeVO> couponCodeList) {
		// 可用的优惠券
        List<CouponCodeVO> availableList = couponCodeList.stream()
        		.filter(v-> v.getStatus() == CouponCodeStatus.AVAILABLE)
        		.collect(Collectors.toList());
        // 不可用的优惠券
        List<CouponCodeVO> unavailableList = couponCodeList.stream()
        		.filter(v-> v.getStatus() != CouponCodeStatus.AVAILABLE)
        		.collect(Collectors.toList());

        List<StoreCouponCodeVO> availableCouponCodeVOList = this.parseCouponCodeVOList(availableList);
        List<StoreCouponCodeVO> unavailableCouponCodeVOList = this.parseCouponCodeVOList(unavailableList);
        confirmResponse.setAvailableCouponCodeVOList(availableCouponCodeVOList);
        confirmResponse.setUnavailableCouponCodeVOList(unavailableCouponCodeVOList);
	}

}
