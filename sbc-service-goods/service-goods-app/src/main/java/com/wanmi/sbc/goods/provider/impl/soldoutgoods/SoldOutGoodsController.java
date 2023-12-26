package com.wanmi.sbc.goods.provider.impl.soldoutgoods;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.soldoutgoods.SoldOutGoodsProvider;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsAddRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsAddResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsModifyRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsModifyResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsDelByIdRequest;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsDelByIdListRequest;
import com.wanmi.sbc.goods.soldoutgoods.service.SoldOutGoodsService;
import com.wanmi.sbc.goods.soldoutgoods.model.root.SoldOutGoods;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>类目品牌排序表保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@RestController
@Validated
public class SoldOutGoodsController implements SoldOutGoodsProvider {
	@Autowired
	private SoldOutGoodsService soldOutGoodsService;

	@Override
	public BaseResponse batchAdd(@RequestBody @Valid SoldOutGoodsAddRequest soldOutGoodsAddRequest) {
		soldOutGoodsAddRequest.getGoodsIds().forEach(item->{
			SoldOutGoods soldOutGoods = new SoldOutGoods();
			soldOutGoods.setGoodsId(item);
			soldOutGoods.setCreateTime(LocalDateTime.now());
			soldOutGoodsService.add(soldOutGoods);
		});
		return BaseResponse.SUCCESSFUL();
	}


	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid SoldOutGoodsDelByIdListRequest soldOutGoodsDelByIdListRequest) {
		soldOutGoodsService.deleteByIdList(soldOutGoodsDelByIdListRequest.getGoodsIdList());
		return BaseResponse.SUCCESSFUL();
	}


}

