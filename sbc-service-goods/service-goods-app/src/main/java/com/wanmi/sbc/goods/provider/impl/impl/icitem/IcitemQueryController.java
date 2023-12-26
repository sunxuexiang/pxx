package com.wanmi.sbc.goods.provider.impl.impl.icitem;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.icitem.IcitemQueryProvider;
import com.wanmi.sbc.goods.api.request.icitem.IcitemPageRequest;
import com.wanmi.sbc.goods.api.request.icitem.IcitemQueryRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemPageResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemListRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemListResponse;
import com.wanmi.sbc.goods.api.request.icitem.IcitemByIdRequest;
import com.wanmi.sbc.goods.api.response.icitem.IcitemByIdResponse;
import com.wanmi.sbc.goods.bean.vo.IcitemVO;
import com.wanmi.sbc.goods.icitem.service.IcitemService;
import com.wanmi.sbc.goods.icitem.model.root.Icitem;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>配送到家查询服务接口实现</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@RestController
@Validated
public class IcitemQueryController implements IcitemQueryProvider {
	@Autowired
	private IcitemService icitemService;

	@Override
	public BaseResponse<IcitemPageResponse> page(@RequestBody @Valid IcitemPageRequest icitemPageReq) {
		IcitemQueryRequest queryReq = KsBeanUtil.convert(icitemPageReq, IcitemQueryRequest.class);
		Page<Icitem> icitemPage = icitemService.page(queryReq);
		Page<IcitemVO> newPage = icitemPage.map(entity -> icitemService.wrapperVo(entity));
		MicroServicePage<IcitemVO> microPage = new MicroServicePage<>(newPage, icitemPageReq.getPageable());
		IcitemPageResponse finalRes = new IcitemPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<IcitemListResponse> list(@RequestBody @Valid IcitemListRequest icitemListReq) {
		IcitemQueryRequest queryReq = KsBeanUtil.convert(icitemListReq, IcitemQueryRequest.class);
		List<Icitem> icitemList = icitemService.list(queryReq);
		List<IcitemVO> newList = icitemList.stream().map(entity -> icitemService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new IcitemListResponse(newList));
	}

	@Override
	public BaseResponse<IcitemByIdResponse> getById(@RequestBody @Valid IcitemByIdRequest icitemByIdRequest) {
		Icitem icitem =
		icitemService.getOne(icitemByIdRequest.getSku());
		return BaseResponse.success(new IcitemByIdResponse(icitemService.wrapperVo(icitem)));
	}

}

