package com.wanmi.sbc.goods.provider.impl.lastgoodswrite;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.lastgoodswrite.LastGoodsWriteProvider;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteAddRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteAddResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteModifyRequest;
import com.wanmi.sbc.goods.api.response.lastgoodswrite.LastGoodsWriteModifyResponse;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteDelByIdRequest;
import com.wanmi.sbc.goods.api.request.lastgoodswrite.LastGoodsWriteDelByIdListRequest;
import com.wanmi.sbc.goods.lastgoodswrite.service.LastGoodsWriteService;
import com.wanmi.sbc.goods.lastgoodswrite.model.root.LastGoodsWrite;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>用户最后一次商品记录保存服务接口实现</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@RestController
@Validated
public class LastGoodsWriteController implements LastGoodsWriteProvider {
	@Autowired
	private LastGoodsWriteService lastGoodsWriteService;

	@Override
	public BaseResponse<LastGoodsWriteAddResponse> add(@RequestBody @Valid LastGoodsWriteAddRequest lastGoodsWriteAddRequest) {
		LastGoodsWrite lastGoodsWrite = KsBeanUtil.convert(lastGoodsWriteAddRequest, LastGoodsWrite.class);
		return BaseResponse.success(new LastGoodsWriteAddResponse(
				lastGoodsWriteService.wrapperVo(lastGoodsWriteService.add(lastGoodsWrite))));
	}

	@Override
	public BaseResponse<LastGoodsWriteModifyResponse> modify(@RequestBody @Valid LastGoodsWriteModifyRequest lastGoodsWriteModifyRequest) {
		LastGoodsWrite lastGoodsWrite = KsBeanUtil.convert(lastGoodsWriteModifyRequest, LastGoodsWrite.class);
		return BaseResponse.success(new LastGoodsWriteModifyResponse(
				lastGoodsWriteService.wrapperVo(lastGoodsWriteService.modify(lastGoodsWrite))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid LastGoodsWriteDelByIdRequest lastGoodsWriteDelByIdRequest) {
		lastGoodsWriteService.deleteById(lastGoodsWriteDelByIdRequest.getId());
		return BaseResponse.SUCCESSFUL();
	}


}

