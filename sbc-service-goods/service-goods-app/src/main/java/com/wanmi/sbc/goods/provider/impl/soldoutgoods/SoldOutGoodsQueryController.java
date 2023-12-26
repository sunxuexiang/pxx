package com.wanmi.sbc.goods.provider.impl.soldoutgoods;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.soldoutgoods.SoldOutGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsQueryRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsPageResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsListRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsListResponse;
import com.wanmi.sbc.goods.api.request.soldoutgoods.SoldOutGoodsByIdRequest;
import com.wanmi.sbc.goods.api.response.soldoutgoods.SoldOutGoodsByIdResponse;
import com.wanmi.sbc.goods.bean.vo.SoldOutGoodsVO;
import com.wanmi.sbc.goods.soldoutgoods.service.SoldOutGoodsService;
import com.wanmi.sbc.goods.soldoutgoods.model.root.SoldOutGoods;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>类目品牌排序表查询服务接口实现</p>
 * @author lvheng
 * @date 2021-04-10 15:09:50
 */
@RestController
@Validated
public class SoldOutGoodsQueryController implements SoldOutGoodsQueryProvider {
	@Autowired
	private SoldOutGoodsService soldOutGoodsService;


	@Override
	public BaseResponse<SoldOutGoodsListResponse> list(@RequestBody @Valid SoldOutGoodsListRequest soldOutGoodsListReq) {
		SoldOutGoodsQueryRequest queryReq = KsBeanUtil.convert(soldOutGoodsListReq, SoldOutGoodsQueryRequest.class);
		List<SoldOutGoods> soldOutGoodsList = soldOutGoodsService.list(queryReq);
		List<SoldOutGoodsVO> newList = soldOutGoodsList.stream().map(entity -> soldOutGoodsService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new SoldOutGoodsListResponse(newList));
	}

}

