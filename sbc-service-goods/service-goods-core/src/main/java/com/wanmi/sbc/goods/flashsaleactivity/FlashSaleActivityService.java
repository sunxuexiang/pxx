package com.wanmi.sbc.goods.flashsaleactivity;

import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.goods.api.request.flashsaleactivity.FlashSaleActivityQueryRequest;
import com.wanmi.sbc.goods.api.response.flashsaleactivity.FlashSaleActivityResponse;
import com.wanmi.sbc.goods.flashsalegoods.repository.FlashSaleGoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>抢购活动业务逻辑</p>
 * @author yxz
 * @date 2019-06-11 14:54:31
 */
@Service("FlashSaleActivityService")
public class FlashSaleActivityService {

	@Autowired
	private FlashSaleGoodsRepository flashSaleGoodsRepository;
	
	/** 
	 * 分页查询抢购活动
	 * @author yxz
	 */
	public Page<FlashSaleActivityResponse> page(FlashSaleActivityQueryRequest queryReq){
        String fullTimeBegin = null;
        String fullTimeEnd = null;
        if (Objects.nonNull(queryReq.getFullTimeBegin())) {
            fullTimeBegin = DateUtil.format(queryReq.getFullTimeBegin(), DateUtil.FMT_TIME_1);
        }
        if (Objects.nonNull(queryReq.getFullTimeEnd())) {
            fullTimeEnd = DateUtil.format(queryReq.getFullTimeEnd(), DateUtil.FMT_TIME_1);
        }
		Page<Object> objectPage = flashSaleGoodsRepository.queryGroupByFullTime(fullTimeBegin, fullTimeEnd,
                queryReq.getStoreId(), queryReq.getPageRequest());
		List<FlashSaleActivityResponse> responses = objectPage.getContent().stream()
				.map(o -> new FlashSaleActivityResponse().convertFromNativeSQLResult(o))
				.collect(Collectors.toList());
		return new PageImpl<>(responses, queryReq.getPageable(), objectPage.getTotalElements());
	}

	/**
	 * 列表查询抢购活动
	 * @author yxz
	 */
	public List<FlashSaleActivityResponse> list(FlashSaleActivityQueryRequest queryReq){
        String fullTimeBegin = null;
        String fullTimeEnd = null;
        if (Objects.nonNull(queryReq.getFullTimeBegin())) {
            fullTimeBegin = DateUtil.format(queryReq.getFullTimeBegin(), DateUtil.FMT_TIME_1);
        }
        if (Objects.nonNull(queryReq.getFullTimeEnd())) {
            fullTimeEnd = DateUtil.format(queryReq.getFullTimeEnd(), DateUtil.FMT_TIME_1);
        }
		List<Object> objectList = flashSaleGoodsRepository.queryGroupByFullTime(fullTimeBegin, fullTimeEnd,
                queryReq.getStoreId());
		List<FlashSaleActivityResponse> responses = objectList.stream()
				.map(o -> new FlashSaleActivityResponse().convertFromNativeSQLResult(o))
				.collect(Collectors.toList());
		return responses;
	}

}
