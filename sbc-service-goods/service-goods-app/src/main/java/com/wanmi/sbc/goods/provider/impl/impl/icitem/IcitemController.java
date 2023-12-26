package com.wanmi.sbc.goods.provider.impl.impl.icitem;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.icitem.IcitemProvider;
import com.wanmi.sbc.goods.api.request.icitem.IcitemAddRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemDelByIdListRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemDelByIdRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemModifyRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemAddResponse;
import com.wanmi.sbc.goods.api.response.icitem.IcitemModifyResponse;
import com.wanmi.sbc.goods.icitem.model.root.Icitem;
import com.wanmi.sbc.goods.icitem.service.IcitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>配送到家保存服务接口实现</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@RestController
@Validated
public class IcitemController implements IcitemProvider {
	@Autowired
	private IcitemService icitemService;

	@Override
	public BaseResponse<IcitemAddResponse> add(@RequestBody @Valid IcitemAddRequest icitemAddRequest) {
		Icitem icitem = KsBeanUtil.convert(icitemAddRequest, Icitem.class);
		return BaseResponse.success(new IcitemAddResponse(
				icitemService.wrapperVo(icitemService.add(icitem))));
	}

	@Override
	public BaseResponse<IcitemModifyResponse> modify(@RequestBody @Valid IcitemModifyRequest icitemModifyRequest) {
		Icitem icitem = KsBeanUtil.convert(icitemModifyRequest, Icitem.class);
		return BaseResponse.success(new IcitemModifyResponse(
				icitemService.wrapperVo(icitemService.modify(icitem))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid IcitemDelByIdRequest icitemDelByIdRequest) {
		icitemService.deleteById(icitemDelByIdRequest.getSku());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid IcitemDelByIdListRequest icitemDelByIdListRequest) {
		icitemService.deleteByIdList(icitemDelByIdListRequest.getSkuList());
		return BaseResponse.SUCCESSFUL();
	}

}

