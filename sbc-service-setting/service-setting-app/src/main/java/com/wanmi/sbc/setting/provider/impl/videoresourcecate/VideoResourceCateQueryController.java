package com.wanmi.sbc.setting.provider.impl.videoresourcecate;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.request.videoresourcecate.*;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateByIdResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCateListResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.VideoResourceCatePageResponse;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import com.wanmi.sbc.setting.videoresourcecate.model.root.VideoResourceCate;
import com.wanmi.sbc.setting.videoresourcecate.service.VideoResourceCateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频教程资源资源分类表查询服务接口实现
 * @author hudong
 * @date 2023-06-26 09:19
 */
@RestController
@Validated
public class VideoResourceCateQueryController implements VideoResourceCateQueryProvider {
	@Autowired
	private VideoResourceCateService videoResourceCateService;

	@Override
	public BaseResponse<VideoResourceCatePageResponse> page(@RequestBody @Valid VideoResourceCatePageRequest videoResourceCatePageReq) {
		VideoResourceCateQueryRequest queryReq = new VideoResourceCateQueryRequest();
		KsBeanUtil.copyPropertiesThird(videoResourceCatePageReq, queryReq);
		Page<VideoResourceCate> videoResourceCatePage = videoResourceCateService.page(queryReq);
		Page<VideoResourceCateVO> newPage = videoResourceCatePage.map(entity -> videoResourceCateService.wrapperVo(entity));
		MicroServicePage<VideoResourceCateVO> microPage = new MicroServicePage<>(newPage, videoResourceCatePageReq.getPageable());
		VideoResourceCatePageResponse finalRes = new VideoResourceCatePageResponse(microPage);
		return BaseResponse.success(finalRes);
	}

	@Override
	public BaseResponse<VideoResourceCateListResponse> list(@RequestBody @Valid VideoResourceCateListRequest videoResourceCateListReq) {
		VideoResourceCateQueryRequest queryReq = new VideoResourceCateQueryRequest();
		KsBeanUtil.copyPropertiesThird(videoResourceCateListReq, queryReq);
		queryReq.putSort("cateId", SortType.ASC.toValue());
		queryReq.putSort("createTime", SortType.DESC.toValue());
		queryReq.putSort("sort", SortType.ASC.toValue());
		queryReq.setDelFlag( DeleteFlag.NO);
		List<VideoResourceCate> videoResourceCateList = videoResourceCateService.list(queryReq);
		List<VideoResourceCateVO> newList = videoResourceCateList.stream().map(entity -> videoResourceCateService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new VideoResourceCateListResponse(newList));
	}

	@Override
	public BaseResponse<VideoResourceCateListResponse> defaultList(VideoResourceCateListRequest videoResourceCateListReq) {
		VideoResourceCateQueryRequest queryReq = new VideoResourceCateQueryRequest();
		KsBeanUtil.copyPropertiesThird(videoResourceCateListReq, queryReq);
		queryReq.putSort("cateId", SortType.ASC.toValue());
		queryReq.setDelFlag( DeleteFlag.NO);
		//查询默认
		queryReq.setIsDefault(DefaultFlag.YES);
		List<VideoResourceCate> videoResourceCateList = videoResourceCateService.list(queryReq);
		List<VideoResourceCateVO> newList = videoResourceCateList.stream().map(entity -> videoResourceCateService.wrapperVo(entity)).collect(Collectors.toList());
		return BaseResponse.success(new VideoResourceCateListResponse(newList));
	}

	@Override
	public BaseResponse<VideoResourceCateByIdResponse> getById(@RequestBody @Valid VideoResourceCateByIdRequest videoResourceCateByIdRequest) {
		VideoResourceCate videoResourceCate = videoResourceCateService.getById(videoResourceCateByIdRequest.getCateId());
		return BaseResponse.success(new VideoResourceCateByIdResponse(videoResourceCateService.wrapperVo(videoResourceCate)));
	}

	@Override
	public BaseResponse<Integer> checkChild(@RequestBody @Valid VideoResourceCateCheckChildRequest
													request) {
		return BaseResponse.success(videoResourceCateService.checkChild(request.getCateId(),request.getStoreId()));
	}

	@Override
	public BaseResponse<Integer> checkResource(@RequestBody @Valid  VideoResourceCateCheckResourceRequest
													   request) {
		return BaseResponse.success(videoResourceCateService.checkResource(request.getCateId(),request.getStoreId()));
	}
}

