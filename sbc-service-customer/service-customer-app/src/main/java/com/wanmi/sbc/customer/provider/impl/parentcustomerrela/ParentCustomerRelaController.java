package com.wanmi.sbc.customer.provider.impl.parentcustomerrela;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaAddResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaModifyRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaModifyResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaDelByIdRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaDelByIdListRequest;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>子主账号关联关系保存服务接口实现</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@RestController
@Validated
public class ParentCustomerRelaController implements ParentCustomerRelaProvider {
	@Autowired
	private ParentCustomerRelaService parentCustomerRelaService;

	@Override
	public BaseResponse<ParentCustomerRelaAddResponse> add(@RequestBody @Valid ParentCustomerRelaAddRequest parentCustomerRelaAddRequest) {
		ParentCustomerRela parentCustomerRela = KsBeanUtil.convert(parentCustomerRelaAddRequest, ParentCustomerRela.class);
		return BaseResponse.success(new ParentCustomerRelaAddResponse(
				parentCustomerRelaService.wrapperVo(parentCustomerRelaService.add(parentCustomerRela))));
	}

	@Override
	public BaseResponse<ParentCustomerRelaModifyResponse> modify(@RequestBody @Valid ParentCustomerRelaModifyRequest parentCustomerRelaModifyRequest) {
		ParentCustomerRela parentCustomerRela = KsBeanUtil.convert(parentCustomerRelaModifyRequest, ParentCustomerRela.class);
		return BaseResponse.success(new ParentCustomerRelaModifyResponse(
				parentCustomerRelaService.wrapperVo(parentCustomerRelaService.modify(parentCustomerRela))));
	}

	@Override
	public BaseResponse deleteById(@RequestBody @Valid ParentCustomerRelaDelByIdRequest parentCustomerRelaDelByIdRequest) {
		parentCustomerRelaService.deleteById(parentCustomerRelaDelByIdRequest.getParentId());
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid ParentCustomerRelaDelByIdListRequest parentCustomerRelaDelByIdListRequest) {
		parentCustomerRelaService.deleteByIdList(parentCustomerRelaDelByIdListRequest.getParentIdList());
		return BaseResponse.SUCCESSFUL();
	}

}

