package com.wanmi.sbc.goods.api.provider.postionPicture;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.request.pointsgoodscate.PointsGoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.pointsgoodscate.PointsGoodsCateListRequest;
import com.wanmi.sbc.goods.api.request.pointsgoodscate.PointsGoodsCatePageRequest;
import com.wanmi.sbc.goods.api.response.pointsgoodscate.PointsGoodsCateByIdResponse;
import com.wanmi.sbc.goods.api.response.pointsgoodscate.PointsGoodsCateListResponse;
import com.wanmi.sbc.goods.api.response.pointsgoodscate.PointsGoodsCatePageResponse;
import com.wanmi.sbc.goods.bean.vo.PositionPictureVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@FeignClient(value = "${application.goods.name}", url="${feign.url.goods:#{null}}", contextId = "PostionPictureProvider")
public interface PostionPictureProvider {

	/**
	 * @param param
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/postionpicture/getImages")
	BaseResponse<List<PositionPictureVO>> getImages(@RequestBody @Valid Map<String,Object> param);

	/**
	 * 插入
	 * @param positionPictureVO
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/postionpicture/addAndFlush")
	BaseResponse addAndFlush (@RequestBody @Valid PositionPictureVO positionPictureVO);

	/**
	 * 插入
	 * @param positionPictureVO
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/postionpicture/add")
	BaseResponse add (@RequestBody @Valid PositionPictureVO positionPictureVO);

	/**通过id查询当个图片
	 * @param  positionId
	 * @return
	 */
	@PostMapping("/goods/${application.goods.version}/postionpicture/getImage")
	BaseResponse<PositionPictureVO> getImage(@RequestBody @Valid Long positionId);


}

