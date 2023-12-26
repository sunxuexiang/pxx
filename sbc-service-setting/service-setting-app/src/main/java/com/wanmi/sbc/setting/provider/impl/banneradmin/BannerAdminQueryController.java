package com.wanmi.sbc.setting.provider.impl.banneradmin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.banneradmin.BannerAdminQueryProvider;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminPageRequest;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminQueryRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminPageResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminListRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminListResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminByIdRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminByIdResponse;
import com.wanmi.sbc.setting.bean.vo.BannerAdminVO;
import com.wanmi.sbc.setting.banneradmin.service.BannerAdminService;
import com.wanmi.sbc.setting.banneradmin.model.root.BannerAdmin;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>轮播管理查询服务接口实现</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@RestController
@Validated
public class BannerAdminQueryController implements BannerAdminQueryProvider {
	@Autowired
	private BannerAdminService bannerAdminService;

	@Override
	public BaseResponse<BannerAdminPageResponse> page(@RequestBody @Valid BannerAdminPageRequest bannerAdminPageReq) {
		BannerAdminQueryRequest queryReq = KsBeanUtil.convert(bannerAdminPageReq, BannerAdminQueryRequest.class);
		Page<BannerAdmin> bannerAdminPage = bannerAdminService.page(queryReq);
		Page<BannerAdminVO> newPage = bannerAdminPage.map(entity -> bannerAdminService.wrapperVo(entity));
		MicroServicePage<BannerAdminVO> microPage = new MicroServicePage<>(newPage, bannerAdminPageReq.getPageable());
		BannerAdminPageResponse finalRes = new BannerAdminPageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<BannerAdminListResponse> list(@RequestBody @Valid BannerAdminListRequest bannerAdminListReq) {
		BannerAdminQueryRequest queryReq = KsBeanUtil.convert(bannerAdminListReq, BannerAdminQueryRequest.class);
		List<BannerAdmin> bannerAdminList = bannerAdminService.list(queryReq);
		List<BannerAdminVO> newList = bannerAdminList.stream().map(entity -> bannerAdminService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new BannerAdminListResponse(newList));
	}

	@Override
	public BaseResponse<BannerAdminByIdResponse> getById(@RequestBody @Valid BannerAdminByIdRequest bannerAdminByIdRequest) {
		BannerAdmin bannerAdmin =
		bannerAdminService.getOne(bannerAdminByIdRequest.getId());
		return BaseResponse.success(new BannerAdminByIdResponse(bannerAdminService.wrapperVo(bannerAdmin)));
	}

}

