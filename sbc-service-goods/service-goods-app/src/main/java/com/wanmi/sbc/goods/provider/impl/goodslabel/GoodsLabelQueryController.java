package com.wanmi.sbc.goods.provider.impl.goodslabel;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodslabel.GoodsLabelQueryProvider;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelQueryRequest;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelListRequest;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelListResponse;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelByIdRequest;
import com.wanmi.sbc.goods.api.response.goodslabel.GoodsLabelByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsLabelVO;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>导航配置查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-19 11:09:28
 */
@RestController
@Validated
public class GoodsLabelQueryController implements GoodsLabelQueryProvider {
	@Autowired
	private GoodsLabelService goodsLabelService;


	@Override
	public BaseResponse<GoodsLabelListResponse> list(@RequestBody @Valid GoodsLabelListRequest goodsLabelListReq) {
		GoodsLabelQueryRequest queryReq = KsBeanUtil.convert(goodsLabelListReq, GoodsLabelQueryRequest.class);
		List<GoodsLabel> goodsLabelList = goodsLabelService.list(queryReq);
		List<GoodsLabelVO> newList = goodsLabelList.stream().map(entity -> goodsLabelService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new GoodsLabelListResponse(newList));
	}

	@Override
	public BaseResponse<GoodsLabelByIdResponse> getById(@RequestBody @Valid GoodsLabelByIdRequest goodsLabelByIdRequest) {
		GoodsLabel goodsLabel =
		goodsLabelService.getOne(goodsLabelByIdRequest.getId());
		return BaseResponse.success(new GoodsLabelByIdResponse(goodsLabelService.wrapperVo(goodsLabel)));
	}

}

