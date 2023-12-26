package com.wanmi.sbc.goods.provider.impl.goodslabel;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelProvider;
import com.wanmi.sbc.goods.api.request.goodslabel.*;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelAddResponse;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelModifyResponse;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.service.GoodsLabelRelaService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>导航配置保存服务接口实现</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@RestController
@Validated
public class GoodsLabelController implements GoodsLabelProvider {
	@Autowired
	private GoodsLabelService goodsLabelService;

	@Autowired
	private GoodsLabelRelaService goodsLabelRelaService;


	@Override
	public BaseResponse<GoodsLabelAddResponse> add(@RequestBody @Valid GoodsLabelAddRequest goodsLabelAddRequest) {
		GoodsLabel goodsLabel = KsBeanUtil.convert(goodsLabelAddRequest, GoodsLabel.class);
		return BaseResponse.success(new GoodsLabelAddResponse(
				goodsLabelService.wrapperVo(goodsLabelService.add(goodsLabel))));
	}

	@Override
	public BaseResponse<GoodsLabelModifyResponse> modify(@RequestBody @Valid GoodsLabelModifyRequest goodsLabelModifyRequest) {
		GoodsLabel goodsLabel = KsBeanUtil.convert(goodsLabelModifyRequest, GoodsLabel.class);
		return BaseResponse.success(new GoodsLabelModifyResponse(
				goodsLabelService.wrapperVo(goodsLabelService.modify(goodsLabel))));
	}


	@Override
	public BaseResponse modifySort(@RequestBody @Valid GoodsLabelModifySortRequest request) {
		goodsLabelService.modifySort(request.getGoodsLabels());
		return BaseResponse.SUCCESSFUL();
	}


	@Override
	public BaseResponse deleteById(@RequestBody @Valid GoodsLabelDelByIdRequest goodsLabelDelByIdRequest) {
		GoodsLabel goodsLabel = KsBeanUtil.convert(goodsLabelDelByIdRequest, GoodsLabel.class);
		goodsLabel.setDelFlag(DeleteFlag.YES);
		goodsLabelService.deleteById(goodsLabel);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid GoodsLabelDelByIdListRequest goodsLabelDelByIdListRequest) {
		List<GoodsLabel> goodsLabelList = goodsLabelDelByIdListRequest.getIdList().stream()
			.map(Id -> {
				GoodsLabel goodsLabel = KsBeanUtil.convert(Id, GoodsLabel.class);
				goodsLabel.setDelFlag(DeleteFlag.YES);
				return goodsLabel;
			}).collect(Collectors.toList());
		goodsLabelService.deleteByIdList(goodsLabelList);
		return BaseResponse.SUCCESSFUL();
	}

}

