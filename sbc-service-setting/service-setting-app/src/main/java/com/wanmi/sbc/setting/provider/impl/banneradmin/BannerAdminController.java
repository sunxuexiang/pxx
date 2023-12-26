package com.wanmi.sbc.setting.provider.impl.banneradmin;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.setting.api.provider.banneradmin.BannerAdminProvider;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminAddRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminAddResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminModifyRequest;
import com.wanmi.sbc.setting.api.response.banneradmin.BannerAdminModifyResponse;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminDelByIdRequest;
import com.wanmi.sbc.setting.api.request.banneradmin.BannerAdminDelByIdListRequest;
import com.wanmi.sbc.setting.banneradmin.service.BannerAdminService;
import com.wanmi.sbc.setting.banneradmin.model.root.BannerAdmin;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

/**
 * <p>轮播管理保存服务接口实现</p>
 * @author 费传奇
 * @date 2020-12-08 11:44:38
 */
@RestController
@Validated
public class BannerAdminController implements BannerAdminProvider {
	@Autowired
	private BannerAdminService bannerAdminService;

	@Override
	public BaseResponse<BannerAdminAddResponse> add(@RequestBody @Valid BannerAdminAddRequest bannerAdminAddRequest) {
		BannerAdmin bannerAdmin = KsBeanUtil.convert(bannerAdminAddRequest, BannerAdmin.class);
		return BaseResponse.success(new BannerAdminAddResponse(
				bannerAdminService.wrapperVo(bannerAdminService.add(bannerAdmin))));
	}

	@Override
	public BaseResponse<BannerAdminModifyResponse> modify(@RequestBody @Valid BannerAdminModifyRequest bannerAdminModifyRequest) {
		BannerAdmin bannerAdmin = KsBeanUtil.convert(bannerAdminModifyRequest, BannerAdmin.class);
		return BaseResponse.success(new BannerAdminModifyResponse(
				bannerAdminService.wrapperVo(bannerAdminService.modify(bannerAdmin))));
	}

	@Override
	public BaseResponse<BannerAdminModifyResponse> modifyStatus(@RequestBody @Valid BannerAdminModifyRequest bannerAdminModifyRequest) {
		BannerAdmin bannerAdmin = KsBeanUtil.convert(bannerAdminModifyRequest, BannerAdmin.class);
		return BaseResponse.success(new BannerAdminModifyResponse(
				bannerAdminService.wrapperVo(bannerAdminService.modifyStatus(bannerAdmin))));
	}


	@Override
	public BaseResponse deleteById(@RequestBody @Valid BannerAdminDelByIdRequest bannerAdminDelByIdRequest) {
		BannerAdmin bannerAdmin = KsBeanUtil.convert(bannerAdminDelByIdRequest, BannerAdmin.class);
		bannerAdmin.setDelFlag(DeleteFlag.YES);
		bannerAdminService.deleteById(bannerAdmin);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse deleteByIdList(@RequestBody @Valid BannerAdminDelByIdListRequest bannerAdminDelByIdListRequest) {
		List<BannerAdmin> bannerAdminList = bannerAdminDelByIdListRequest.getIdList().stream()
			.map(Id -> {
				BannerAdmin bannerAdmin = KsBeanUtil.convert(Id, BannerAdmin.class);
				bannerAdmin.setDelFlag(DeleteFlag.YES);
				return bannerAdmin;
			}).collect(Collectors.toList());
		bannerAdminService.deleteByIdList(bannerAdminList);
		return BaseResponse.SUCCESSFUL();
	}

}

