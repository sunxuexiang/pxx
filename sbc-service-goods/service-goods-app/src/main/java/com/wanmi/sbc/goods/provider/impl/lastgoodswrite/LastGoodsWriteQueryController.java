package com.wanmi.sbc.goods.provider.impl.lastgoodswrite;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteQueryProvider;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWritePageRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteQueryRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWritePageResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteListRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteListResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteByIdRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteByIdResponse;
import com.wanmi.sbc.goods.bean.vo.LastGoodsWriteVO;
import com.wanmi.sbc.goods.lastgoodswrite.service.LastGoodsWriteService;
import com.wanmi.sbc.goods.lastgoodswrite.model.root.LastGoodsWrite;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>用户最后一次商品记录查询服务接口实现</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@RestController
@Validated
public class LastGoodsWriteQueryController implements LastGoodsWriteQueryProvider {
	@Autowired
	private LastGoodsWriteService lastGoodsWriteService;

	@Override
	public BaseResponse<LastGoodsWritePageResponse> page(@RequestBody @Valid LastGoodsWritePageRequest lastGoodsWritePageReq) {
		LastGoodsWriteQueryRequest queryReq = KsBeanUtil.convert(lastGoodsWritePageReq, LastGoodsWriteQueryRequest.class);
		Page<LastGoodsWrite> lastGoodsWritePage = lastGoodsWriteService.page(queryReq);
		Page<LastGoodsWriteVO> newPage = lastGoodsWritePage.map(entity -> lastGoodsWriteService.wrapperVo(entity));
		MicroServicePage<LastGoodsWriteVO> microPage = new MicroServicePage<>(newPage, lastGoodsWritePageReq.getPageable());
		LastGoodsWritePageResponse finalRes = new LastGoodsWritePageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<LastGoodsWriteListResponse> list(@RequestBody @Valid LastGoodsWriteListRequest lastGoodsWriteListReq) {
		LastGoodsWriteQueryRequest queryReq = KsBeanUtil.convert(lastGoodsWriteListReq, LastGoodsWriteQueryRequest.class);
		List<LastGoodsWrite> lastGoodsWriteList = lastGoodsWriteService.list(queryReq);
		List<LastGoodsWriteVO> newList = lastGoodsWriteList.stream().map(entity -> lastGoodsWriteService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new LastGoodsWriteListResponse(newList));
	}

	@Override
	public BaseResponse<LastGoodsWriteByIdResponse> getById(@RequestBody @Valid LastGoodsWriteByIdRequest lastGoodsWriteByIdRequest) {
		LastGoodsWrite lastGoodsWrite =
		lastGoodsWriteService.getOne(lastGoodsWriteByIdRequest.getId());
		return BaseResponse.success(new LastGoodsWriteByIdResponse(lastGoodsWriteService.wrapperVo(lastGoodsWrite)));
	}

}

