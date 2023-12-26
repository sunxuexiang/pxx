package com.wanmi.sbc.order.provider.impl.InventoryDetailSamount;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.order.api.provider.InventoryDetailSamount.InventoryDetailSamountProvider;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.InventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.request.InventoryDetailSamount.SubInventoryDetailSamountRequest;
import com.wanmi.sbc.order.api.response.inventorydetailsamount.InventoryDetailSamountResponse;
import com.wanmi.sbc.order.bean.vo.InventoryDetailSamountVO;
import com.wanmi.sbc.order.inventorydetailsamount.model.root.InventoryDetailSamount;
import com.wanmi.sbc.order.inventorydetailsamount.service.InventoryDetailSamountService;
import com.wanmi.sbc.order.trade.model.entity.TradeItem;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
@Validated
public class InventoryDetailSamountQueryController implements InventoryDetailSamountProvider {

	@Autowired
	private InventoryDetailSamountService inventoryDetailSamountService;


	@Override
	public BaseResponse saveGoodsShareMoney(InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		inventoryDetailSamountService.saveGoodsShareMoney
				(KsBeanUtil.convert(inventoryDetailSamountRequest.getTradeItemVOS(), TradeItem.class),inventoryDetailSamountRequest.getOid());
		return BaseResponse.SUCCESSFUL();
	}

	/*@Override
	public BaseResponse updateInventoryDetailSamountFlag(InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		inventoryDetailSamountService.updateInventoryDetailSamountFlag
				(inventoryDetailSamountRequest.getOid(),inventoryDetailSamountRequest.getGoodsInfoId(),inventoryDetailSamountRequest.getNum(),inventoryDetailSamountRequest.getTakeId());
		return BaseResponse.SUCCESSFUL();
	}*/

	@Override
	public BaseResponse<InventoryDetailSamountResponse> getInventoryDetailSamountFlag(InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		List<InventoryDetailSamount> inventoryDetailSamountFlag = inventoryDetailSamountService.getInventoryDetailSamountFlag
				(inventoryDetailSamountRequest.getOid(), inventoryDetailSamountRequest.getGoodsInfoId(), inventoryDetailSamountRequest.getNum());
		return BaseResponse.success(InventoryDetailSamountResponse.builder().inventoryDetailSamountVOS(KsBeanUtil.convert(inventoryDetailSamountFlag, InventoryDetailSamountVO.class)).build());
	}

	@Override
	public BaseResponse subInventoryDetailSamountFlag(SubInventoryDetailSamountRequest subInventoryDetailSamountRequest) {
		inventoryDetailSamountService.subInventoryDetailSamountFlag(
				KsBeanUtil.convert(subInventoryDetailSamountRequest.getInventoryDetailSamountVOList(), InventoryDetailSamount.class),
				subInventoryDetailSamountRequest.getTakeId()
		);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<InventoryDetailSamountResponse> getInventoryByTakeId(@Valid InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		List<InventoryDetailSamount> inventoryByTakeId = inventoryDetailSamountService.getInventoryByTakeId(inventoryDetailSamountRequest.getTakeId());
		return BaseResponse.success(InventoryDetailSamountResponse.builder().inventoryDetailSamountVOS(KsBeanUtil.convert(inventoryByTakeId, InventoryDetailSamountVO.class)).build());
	}

	@Override
	public BaseResponse<InventoryDetailSamountResponse> getInventoryByTakeIds(@Valid InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		List<InventoryDetailSamount> inventoryByTakeId = inventoryDetailSamountService.getInventoryByTakeIds(inventoryDetailSamountRequest.getTakeIds());
		return BaseResponse.success(InventoryDetailSamountResponse.builder().inventoryDetailSamountVOS(KsBeanUtil.convert(inventoryByTakeId, InventoryDetailSamountVO.class)).build());
	}

	@Override
	public BaseResponse<InventoryDetailSamountResponse> getInventoryByOId(@Valid InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		List<InventoryDetailSamount> inventoryByTakeId = inventoryDetailSamountService.getInventoryByOId(inventoryDetailSamountRequest.getOid());
		return BaseResponse.success(InventoryDetailSamountResponse.builder().inventoryDetailSamountVOS(KsBeanUtil.convert(inventoryByTakeId, InventoryDetailSamountVO.class)).build());
	}

	@Override
	public BaseResponse<InventoryDetailSamountResponse> updateInventoryByTakeIdBack(@Valid InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		inventoryDetailSamountService.updateInventoryByTakeId(inventoryDetailSamountRequest.getTakeId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse<InventoryDetailSamountResponse> getNoTiinventoryDetailSamount(InventoryDetailSamountRequest inventoryDetailSamountRequest) {
		List<InventoryDetailSamount> noTiinventoryDetailSamount = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(inventoryDetailSamountRequest.getGoodsInfoIds())){
		noTiinventoryDetailSamount = inventoryDetailSamountService.getNoTiinventoryDetailSamounts
					(inventoryDetailSamountRequest.getOid(), inventoryDetailSamountRequest.getGoodsInfoIds());
		}else if (Objects.nonNull(inventoryDetailSamountRequest.getGoodsInfoId())){
		 noTiinventoryDetailSamount = inventoryDetailSamountService.getNoTiinventoryDetailSamount
					(inventoryDetailSamountRequest.getOid(), inventoryDetailSamountRequest.getGoodsInfoId());
		}
		return BaseResponse.success(InventoryDetailSamountResponse.builder().inventoryDetailSamountVOS(KsBeanUtil.convert(noTiinventoryDetailSamount, InventoryDetailSamountVO.class)).build());
	}

    @Override
    public BaseResponse<Void> unlockAmountByRid(@PathVariable(value = "rid") String rid) {
		inventoryDetailSamountService.unlockAmountByRid(rid);
        return BaseResponse.SUCCESSFUL();
    }

	@Override
	public BaseResponse<Void> returnAmountByRid(@PathVariable(value = "rid") String rid) {
		inventoryDetailSamountService.returnAmountByRid(rid);
		return BaseResponse.SUCCESSFUL();
	}
}

