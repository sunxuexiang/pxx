package com.wanmi.sbc.customer.provider.impl.parentcustomerrela;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.parentcustomerrela.ParentCustomerRelaQueryProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaPageRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaPageResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaListRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaListResponse;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaByIdRequest;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRelaByIdResponse;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>子主账号关联关系查询服务接口实现</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@RestController
@Validated
public class ParentCustomerRelaQueryController implements ParentCustomerRelaQueryProvider {
	@Autowired
	private ParentCustomerRelaService parentCustomerRelaService;

	@Override
	public BaseResponse<ParentCustomerRelaPageResponse> page(@RequestBody @Valid ParentCustomerRelaPageRequest parentCustomerRelaPageReq) {
		ParentCustomerRelaQueryRequest queryReq = KsBeanUtil.convert(parentCustomerRelaPageReq, ParentCustomerRelaQueryRequest.class);
		Page<ParentCustomerRela> parentCustomerRelaPage = parentCustomerRelaService.page(queryReq);
		Page<ParentCustomerRelaVO> newPage = parentCustomerRelaPage.map(entity -> parentCustomerRelaService.wrapperVo(entity));
		MicroServicePage<ParentCustomerRelaVO> microPage = new MicroServicePage<>(newPage, parentCustomerRelaPageReq.getPageable());
		ParentCustomerRelaPageResponse finalRes = new ParentCustomerRelaPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<ParentCustomerRelaListResponse> list(@RequestBody @Valid ParentCustomerRelaListRequest parentCustomerRelaListReq) {
		ParentCustomerRelaQueryRequest queryReq = KsBeanUtil.convert(parentCustomerRelaListReq, ParentCustomerRelaQueryRequest.class);
		List<ParentCustomerRela> parentCustomerRelaList = parentCustomerRelaService.list(queryReq);
		List<ParentCustomerRelaVO> newList = parentCustomerRelaList.stream().map(entity -> parentCustomerRelaService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new ParentCustomerRelaListResponse(newList));
	}

	@Override
	public BaseResponse<ParentCustomerRelaByIdResponse> getById(@RequestBody @Valid ParentCustomerRelaByIdRequest parentCustomerRelaByIdRequest) {
		ParentCustomerRela parentCustomerRela =
		parentCustomerRelaService.getOne(parentCustomerRelaByIdRequest.getParentId());
		return BaseResponse.success(new ParentCustomerRelaByIdResponse(parentCustomerRelaService.wrapperVo(parentCustomerRela)));
	}

	@Override
	public BaseResponse<ParentCustomerRelaListResponse> findAllByParentId(@RequestBody @Valid ParentCustomerRelaListRequest parentCustomerRelaListReq) {
		List<ParentCustomerRela> parentCustomerRelaList = parentCustomerRelaService.findAllById(parentCustomerRelaListReq.getParentId());
		List<ParentCustomerRelaVO> newList = parentCustomerRelaList.stream().map(entity -> parentCustomerRelaService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new ParentCustomerRelaListResponse(newList));
	}
}

