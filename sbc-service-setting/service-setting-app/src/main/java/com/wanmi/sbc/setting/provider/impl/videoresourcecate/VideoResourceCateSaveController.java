package com.wanmi.sbc.setting.provider.impl.videoresourcecate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.constant.SettingConstant;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateSaveProvider;
import com.wanmi.sbc.setting.api.request.AllRoleMenuInfoListRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateAddRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateDelByIdRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateInitRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.VideoResourceCateModifyRequest;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateAddResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateModifyResponse;
import com.wanmi.sbc.setting.authority.service.RoleMenuService;
import com.wanmi.sbc.setting.bean.vo.MenuInfoVO;
import com.wanmi.sbc.setting.videoresourcecate.model.root.VideoResourceCate;
import com.wanmi.sbc.setting.videoresourcecate.service.VideoResourceCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频教程资源资源分类表保存服务接口实现
 * @author hudong
 * @date 2023-06-26 09:19
 */
@RestController
@Validated
public class VideoResourceCateSaveController implements VideoResourceCateSaveProvider {
	@Autowired
	private VideoResourceCateService videoResourceCateService;

	@Autowired
	private RoleMenuService roleMenuService;
	/**
	 * 初始化二三级菜单层级集合
	 */
	private final static List<Integer> gradeList = Arrays.asList(2,3);

	@Override
	public BaseResponse<VideoResourceCateAddResponse> add(@RequestBody @Valid VideoResourceCateAddRequest videoResourceCateAddRequest) {
		VideoResourceCate videoResourceCate = new VideoResourceCate();
		KsBeanUtil.copyPropertiesThird(videoResourceCateAddRequest, videoResourceCate);
		return BaseResponse.success(new VideoResourceCateAddResponse(
				videoResourceCateService.wrapperVo(videoResourceCateService.add(videoResourceCate))));
	}

	@Override
	public BaseResponse<VideoResourceCateModifyResponse> modify(@RequestBody @Valid VideoResourceCateModifyRequest videoResourceCateModifyRequest) {
		VideoResourceCate videoResourceCate = new VideoResourceCate();
		KsBeanUtil.copyPropertiesThird(videoResourceCateModifyRequest, videoResourceCate);
		return BaseResponse.success(new VideoResourceCateModifyResponse(
				videoResourceCateService.wrapperVo(videoResourceCateService.modify(videoResourceCate))));
	}

	@Override
	public BaseResponse delete(@RequestBody @Valid VideoResourceCateDelByIdRequest videoResourceCateDelByIdRequest) {
		videoResourceCateService.delete(videoResourceCateDelByIdRequest.getCateId());
		return BaseResponse.SUCCESSFUL();
	}


	@Override
	public BaseResponse init(@RequestBody @Valid VideoResourceCateInitRequest videoResourceCate) {
		videoResourceCateService.init(videoResourceCate);
		return BaseResponse.SUCCESSFUL();
	}

	@Override
	public BaseResponse initMenuInfoListToCate(AllRoleMenuInfoListRequest request) {
		//查询商家端菜单所有目录
		List<MenuInfoVO> menuInfoVOList = roleMenuService.queryAllRoleMenuInfoList(request.getSystemTypeCd());
		//新增特殊菜单记录
		menuInfoVOList.add(MenuInfoVO.initSpecialMenu(SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getKey(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getDesc(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getGrade(),SettingConstant.MenuInfoEnum.SETTLE_IN_STORE.getParentKey()));
		//过滤层级为2，3级的菜单集合
		List<MenuInfoVO> collectList = menuInfoVOList.stream().filter(m -> gradeList.contains(m.getGrade())).collect(Collectors.toList());
		//初始化目录菜单到videoResourceCate表中
		videoResourceCateService.initMenuInfo(collectList);
		return BaseResponse.SUCCESSFUL();
	}


}

