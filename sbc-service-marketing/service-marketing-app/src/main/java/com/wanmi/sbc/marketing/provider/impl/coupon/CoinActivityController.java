package com.wanmi.sbc.marketing.provider.impl.coupon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CoinActivityProvider;
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
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivity;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityRecord;
import com.wanmi.sbc.marketing.coupon.model.root.CoinActivityStoreRecordDetail;
import com.wanmi.sbc.marketing.coupon.model.vo.CoinActivityQueryVo;
import com.wanmi.sbc.marketing.coupon.service.CoinActivityService;

/**
 * Created by IntelliJ IDEA.
 * 指定商品赠金币
 * @Author : Like
 * @create 2023/5/22 9:35
 */
@RestController
public class CoinActivityController implements CoinActivityProvider {

    @Autowired
    private CoinActivityService coinActivityService;
    
    @Autowired
    private StoreQueryProvider storeQueryProvider;


    @Override
    public BaseResponse add(CoinActivityAddRequest request) {
        coinActivityService.add(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse update(CoinActivityModifyRequest request) {
        coinActivityService.update(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CoinActivityDetailResponse> detail(String id) {
        return BaseResponse.success(coinActivityService.detail(id));
    }

    @Override
    public BaseResponse deleteById(String id, String operatorId) {
        coinActivityService.deleteById(id, operatorId);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse terminationById(String id, String operatorId) {
        coinActivityService.terminationById(id, operatorId);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse terminationGoods(CoinActivityTerminationRequest request) {
        coinActivityService.terminationGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<CoinActivityPageResponse> page(CoinActivityPageRequest request) {
        Page<CoinActivity> couponActivities = coinActivityService.pageActivityInfo(request);
        CoinActivityPageResponse coinActivityPageResponse = new CoinActivityPageResponse(KsBeanUtil.convertPage(couponActivities, CoinActivityVO.class));
        
		// 商家ids
		Set<Long> storeIds = coinActivityPageResponse.getCoinActivityVOPage().stream().map(CoinActivityVO::getStoreId).collect(Collectors.toSet());
		if (CollectionUtils.isNotEmpty(storeIds)) {
			// 一次性给商家名称赋值
			BaseResponse<ListStoreByIdsResponse> listByIds = storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(new ArrayList<Long>(storeIds)).build());
			coinActivityPageResponse.getCoinActivityVOPage().forEach(m -> {
				Optional<StoreVO> findFirst = listByIds.getContext().getStoreVOList().stream().filter(s -> s.getStoreId().equals(m.getStoreId())).findFirst();
				findFirst.ifPresent(v -> {
					m.setSupplierName(v.getSupplierName());
					m.setStoreName(v.getStoreName());
				});
			});
		}
		// 订单返鲸币显示配置的商家数量
		for (CoinActivityVO coinActivityVO : coinActivityPageResponse.getCoinActivityVOPage()) {
			Integer queryStoreCount = coinActivityService.queryStoreCount(coinActivityVO.getActivityId());
			coinActivityVO.setStoreCount(queryStoreCount);
		}
        return BaseResponse.success(coinActivityPageResponse);
    }

    @Override
    public BaseResponse addActivityGoods(CoinAddActivitGoodsRequest request) {
        coinActivityService.addActivityGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<List<CoinGoodsVo>> checkSendCoin(BuyGoodsOrFullOrderSendCouponRequest request) {
        return BaseResponse.success(coinActivityService.checkSendCoin(request.getTradeItemInfoDTOS()));
    }

    @Override
    public BaseResponse<List<CoinActivityVO>> queryCoinActivityBySkuIds(List<String> skuIds) {
        List<CoinActivityQueryVo> vos = coinActivityService.queryCoinActivityBySkuIds(skuIds);
        return BaseResponse.success(KsBeanUtil.convert(vos, CoinActivityVO.class));
    }

    @Override
    public BaseResponse<List<Long>> saveCoinRecord(List<CoinActivityRecordDto> request) {
        return BaseResponse.success(coinActivityService.saveCoinRecord(request));
    }

    @Override
    public BaseResponse<List<CoinActivityRecordDto>> queryCoinActivityRecordByRecordIds(List<Long> recordIds) {
        List<CoinActivityRecord> recordList = coinActivityService.queryCoinActivityRecordByRecordIds(recordIds);
        return BaseResponse.success(KsBeanUtil.convert(recordList, CoinActivityRecordDto.class));
    }

    @Override
    public BaseResponse<List<CoinActivityRecordDto>> queryCoinActivityRecordByOrderId(String orderId) {
        return BaseResponse.success(coinActivityService.queryCoinActivityRecordByOrderId(orderId));
    }


    @Override
    public BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordByOrderIdAndSkuIds(String orderId, List<String> skuIds) {
        return BaseResponse.success(coinActivityService.queryCoinActivityRecordByOrderIdAndSkuIds(orderId, skuIds));
    }

    @Override
    public BaseResponse<MicroServicePage<CoinActivityRecordDto>> recordPage(CoinRecordPageRequest request) {
        Page<CoinActivityRecord> coinActivityRecords = coinActivityService.recordPage(request);
        MicroServicePage<CoinActivityRecordDto> respone = KsBeanUtil.convertPage(coinActivityRecords, CoinActivityRecordDto.class);
        return BaseResponse.success(respone);
    }

    @Override
    public BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordDetailByOrderId(String orderId) {
        return BaseResponse.success(coinActivityService.queryCoinActivityRecordDetailByOrderId(orderId));
    }

    @Override
    public BaseResponse<List<CoinActivityRecordDetailDto>> queryCoinActivityRecordDetailByOrderIds(List<String> orderIds) {
        return BaseResponse.success(coinActivityService.queryCoinActivityRecordDetailByOrderIds(orderIds));
    }


    @Override
    public BaseResponse<CoinActivityRecordDto> recordByOrderId(String orderId) {
        return BaseResponse.success(KsBeanUtil.convert(coinActivityService.recordByOrderId(orderId), CoinActivityRecordDto.class));
    }

    @Override
    public BaseResponse<Integer> queryGoodsCancelNum(String orderId, String goodsInfoId) {
        return BaseResponse.success(coinActivityService.queryGoodsCancelNum(orderId, goodsInfoId));
    }

    @Override
    public BaseResponse<CoinActivityRecordDto> recordBySendNo(String sendNo) {
        return BaseResponse.success(KsBeanUtil.convert(coinActivityService.recordBySendNo(sendNo), CoinActivityRecordDto.class));
    }

    @Override
    public BaseResponse<Boolean> queryCoinActivityRecordIsExist(String tid) {
        return BaseResponse.success(coinActivityService.queryCoinActivityRecordIsExist(tid));
    }

	@Override
	public BaseResponse sendOrderCoin(String tid) {
		coinActivityService.sendOrderCoin(tid);
	    return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse takeBackOrderCoin(TakeBackOrderCoinRequest request) {
		coinActivityService.takeBackOrderCoin(request.getRid(), request.getNeedThrowException());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse addActivityStore(CoinAddActiviStoreRequest request) {
		coinActivityService.addActivityStore(request);
		return BaseResponse.SUCCESSFUL();
	}
	
	@Override
	public BaseResponse<String> orderCoinTips() {
		String tips = coinActivityService.orderCoinTips();
	    return BaseResponse.success(tips);
	}
	
	@Override
	public BaseResponse<List<CoinActivityStoreRecordDetailDTO>> querySendRecord(String orderNo) {
		List<CoinActivityStoreRecordDetail> querySendRecord = coinActivityService.querySendRecord(orderNo);
		List<CoinActivityStoreRecordDetailDTO> convert = KsBeanUtil.convert(querySendRecord, CoinActivityStoreRecordDetailDTO.class);
		return BaseResponse.success(convert);
	}

}
