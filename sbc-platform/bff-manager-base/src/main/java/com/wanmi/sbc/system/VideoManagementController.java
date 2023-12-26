package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.response.info.GiftGoodsInfoListByIdsResponse;
import com.wanmi.sbc.goods.bean.vo.GiftGoodsInfoVO;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementProvider;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementQueryProvider;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementAddResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementByIdResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.setting.bean.enums.StateType;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Api(description = "视频管理管理API", tags = "VideoManagementController")
@RestController
@RequestMapping(value = "/videomanagement")
public class VideoManagementController {

    @Autowired
    private VideoManagementQueryProvider videoManagementQueryProvider;

    @Autowired
    private VideoManagementProvider videoManagementProvider;

    @Autowired
    private YunServiceProvider yunServiceProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;



    @ApiOperation(value = "分页查询视频管理")
        @PostMapping("/page")
    public BaseResponse<VideoManagementPageResponse> getPage(@RequestBody @Valid VideoManagementPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        if (pageReq.getStoreId() == null || pageReq.equals(0)) {
            pageReq.setStoreId(commonUtil.getStoreId());
        }
        BaseResponse<VideoManagementPageResponse> baseResponse = videoManagementQueryProvider.page(pageReq);
        MicroServicePage<VideoManagementVO> microServicePage = baseResponse.getContext().getVideoManagementVOPage();

        // 店铺列表，填充视频发布店铺名称
        if (!ObjectUtils.isEmpty(microServicePage) && !ObjectUtils.isEmpty(microServicePage.getContent())) {
            List<VideoManagementVO> videoList = microServicePage.getContent();
            fullVideoGoodsAndStoreInfo(videoList);
        }
        return baseResponse;
    }

    /**
     * 填充视频店铺名称、商品名称信息
     * @param videoList
     */
    private void fullVideoGoodsAndStoreInfo(List<VideoManagementVO> videoList) {
        if (ObjectUtils.isEmpty(videoList)) {
            return;
        }
        // 填充店铺名称
        List<Long> storeIds = videoList.stream().map(VideoManagementVO::getStoreId).distinct().collect(Collectors.toList());
        ListStoreByIdsRequest listStoreByIdsRequest = ListStoreByIdsRequest.builder().storeIds(storeIds).build();
        BaseResponse<ListStoreByIdsResponse> storeResponse = storeQueryProvider.listByIds(listStoreByIdsRequest);
        for (VideoManagementVO videoManagementVO : videoList) {
            for (StoreVO storeVO : storeResponse.getContext().getStoreVOList()) {
                if (storeVO.getStoreId().equals(videoManagementVO.getStoreId())) {
                    videoManagementVO.setStoreName(storeVO.getStoreName());
                    videoManagementVO.setStoreLogo(storeVO.getStoreLogo());
                    break;
                }
            }
        }
        // 填充sku名称
        List<String> goodsIds = videoList.stream().map(VideoManagementVO::getGoodsInfoId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(goodsIds)) {
            BaseResponse<GiftGoodsInfoListByIdsResponse> goodsResponse = goodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(goodsIds).build());
            if (goodsResponse.getContext() != null && !ObjectUtils.isEmpty(goodsResponse.getContext().getGoodsInfos())) {
                for (VideoManagementVO videoManagementVO : videoList) {
                    if (StringUtils.isEmpty(videoManagementVO.getGoodsInfoId())) {
                        continue;
                    }
                    for (GiftGoodsInfoVO giftGoodsInfoVO : goodsResponse.getContext().getGoodsInfos()) {
                        if (videoManagementVO.getGoodsInfoId().equals(giftGoodsInfoVO.getGoodsInfoId())) {
                            videoManagementVO.setGoodsInfoName(giftGoodsInfoVO.getGoodsInfoName());
                            break;
                        }
                    }
                }
            }
        }
    }

    @ApiOperation(value = "根据视频id获取详情")
    @RequestMapping(value = "/getDetailsById/{videoId}", method = RequestMethod.GET)
    public BaseResponse<VideoManagementByIdResponse> getDetailsById(@PathVariable Long videoId) {
        if (videoId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        VideoManagementByIdRequest idReq = new VideoManagementByIdRequest();
        idReq.setVideoId(videoId);
        BaseResponse<VideoManagementByIdResponse> response = videoManagementQueryProvider.getDetailsById(idReq);
        if (response == null || response.getContext() == null || response.getContext().getVideoManagementVO() == null) {
            return response;
        }
        List<VideoManagementVO> list = new ArrayList<>();
        list.add(response.getContext().getVideoManagementVO());
        fullVideoGoodsAndStoreInfo(list);
        return response;
    }

    @ApiOperation(value = "新增视频管理")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse<VideoManagementAddResponse> add(@RequestBody @Valid VideoManagementAddRequest addReq) {
        operateLogMQUtil.convertAndSend("视频管理管理", "新增视频管理", "新增视频管理：视频名称" + (Objects.nonNull(addReq) ? addReq.getVideoName() : ""));
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setState(StateType.PUT_SHELF);
        String userId = commonUtil.getOperator().getUserId();
        addReq.setCoverFollowCustomerId(userId);
        addReq.setStoreId(commonUtil.getStoreId());
        return videoManagementProvider.add(addReq);
    }

    @ApiOperation(value = "根据视频素材地址获取封面地址")
    @PostMapping(value = "/getCoverImg")
    public BaseResponse<String> getCoverImg(@RequestBody GetCoverImgRequest request){
        return videoManagementProvider.getCoverImg(request);
    }

    @ApiOperation(value = "修改视频管理")
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@RequestBody @Valid VideoManagementModifyRequest modifyReq) {
        operateLogMQUtil.convertAndSend("视频管理管理", "修改视频管理", "修改视频管理：视频名称" + (Objects.nonNull(modifyReq) ? modifyReq.getVideoName() : ""));
        return videoManagementProvider.modify(modifyReq);
    }

    @ApiOperation(value = "上下架状态修改")
    @RequestMapping(value = "/updateStateById", method = RequestMethod.PUT)
    public BaseResponse updateStateById(@RequestBody @Valid VideoManagementUpdateStateRequest request) {
        operateLogMQUtil.convertAndSend("视频管理管理", "上下架状态修改", "上下架状态修改" );
        return videoManagementProvider.updateStateById(request);
    }

    @ApiOperation(value = "根据id删除视频管理")
    @RequestMapping(value = "/deleteById/{videoId}", method = RequestMethod.DELETE)
    public BaseResponse deleteById(@PathVariable Long videoId) {
        if (videoId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("视频管理管理", "根据id删除视频管理", "根据id删除视频管理:视频id" + videoId);
        VideoManagementDelByIdRequest delByIdReq = new VideoManagementDelByIdRequest();
        delByIdReq.setVideoId(videoId);
        return videoManagementProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "视频上传")
    @RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam("uploadFile") List<MultipartFile> multipartFiles) {
        //验证上传参数
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> resourceUrls = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (file == null || file.getSize() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            try {
                // 上传
                String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                        .resourceType(ResourceType.SHORT_VIDEO)
                        .content(file.getBytes())
                        .resourceName(file.getOriginalFilename())
                        .build()).getContext();
                resourceUrls.add(resourceUrl);
            } catch (Exception e) {
                throw new SbcRuntimeException(e);
            }
        }
        operateLogMQUtil.convertAndSend("视频管理管理", "视频上传", "操作成功");
        return ResponseEntity.ok(resourceUrls);
    }

}
