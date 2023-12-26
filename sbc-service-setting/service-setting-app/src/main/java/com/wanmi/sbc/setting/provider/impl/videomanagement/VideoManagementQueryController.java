package com.wanmi.sbc.setting.provider.impl.videomanagement;

import com.google.common.collect.Lists;
import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementQueryProvider;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementByIdResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementListResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.bean.enums.StateType;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoFollow;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoLike;
import com.wanmi.sbc.setting.videomanagement.model.root.VideoManagement;
import com.wanmi.sbc.setting.videomanagement.service.VideoSearchRecordService;
import com.wanmi.sbc.setting.videomanagement.service.VideoFollowService;
import com.wanmi.sbc.setting.videomanagement.service.VideoLikeService;
import com.wanmi.sbc.setting.videomanagement.service.VideoManagementService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>视频管理查询服务接口实现</p>
 *
 * @author zhaowei
 * @date 2021-04-17 17:47:22
 */
@RestController
@Validated
public class VideoManagementQueryController implements VideoManagementQueryProvider {
    @Autowired
    private VideoManagementService videoManagementService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private VideoLikeService videoLikeService;

    @Autowired
    private VideoFollowService videoFollowService;

    @Autowired
    private VideoSearchRecordService videoSearchRecordService;

    @Override
    public BaseResponse<VideoManagementPageResponse> page(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq) {
        VideoManagementQueryRequest queryReq = KsBeanUtil.convert(videoManagementPageReq, VideoManagementQueryRequest.class);
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<VideoManagement> videoManagementPage = videoManagementService.page(queryReq);

        List<Long> followStoreIdList = Lists.newArrayList();
        //查询当前用户所有关注的人
        if(Objects.nonNull(videoManagementPageReq.getCustomerId())){
            VideoFollowQueryRequest request = new VideoFollowQueryRequest();
            request.setFollowCustomerId(videoManagementPageReq.getCustomerId());
            request.setCustomerId(videoManagementPageReq.getCustomerId());
            //所有关注的人
            followStoreIdList = videoFollowService.getVideoFollowByFollowCustomerId(request);
        }

        final List<Long> finaFlollowStoreIdList = followStoreIdList;
        Page<VideoManagementVO> newPage = videoManagementPage.map(entity -> {
                    VideoManagementVO videoManagementVO = processVideoList(videoManagementPageReq, osdClientParam, entity, finaFlollowStoreIdList);
                    return videoManagementVO;
                }
        );
        MicroServicePage<VideoManagementVO> microPage = new MicroServicePage<>(newPage, videoManagementPageReq.getPageable());
        VideoManagementPageResponse finalRes = new VideoManagementPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    public static void main(String[] args) {
        VideoManagementQueryController controller = new VideoManagementQueryController();
        controller.getAppletsPage(VideoManagementPageRequest.builder().customerId("2c9fb80f79a8cb5f0179eaaf7f0e0086").build());
    }

    @Override
    public BaseResponse<VideoManagementPageResponse> getAppletsPage(@Valid VideoManagementPageRequest videoManagementPageReq) {
        VideoManagementQueryRequest queryReq = KsBeanUtil.convert(videoManagementPageReq, VideoManagementQueryRequest.class);
        List<Long> ids = new ArrayList<>();
        //如果用户登陆了，查询关注的人
        if(!StringUtils.isEmpty(videoManagementPageReq.getCustomerId())){
            VideoFollowQueryRequest request = new VideoFollowQueryRequest();
            request.setFollowCustomerId(videoManagementPageReq.getCustomerId());
            //所有关注的人
            ids = videoFollowService.getVideoFollowByFollowCustomerId(request);
        }
        final List<Long> ids1 = ids;
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<VideoManagement> videoManagementPage = videoManagementService.page(queryReq);
        Page<VideoManagementVO> newPage = videoManagementPage.map(entity -> {
                    VideoManagementVO videoManagementVO = processVideoList(videoManagementPageReq, osdClientParam, entity, ids1);
                    return videoManagementVO;
                }
        );
        MicroServicePage<VideoManagementVO> microPage = new MicroServicePage<>(newPage, videoManagementPageReq.getPageable());
        VideoManagementPageResponse finalRes = new VideoManagementPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<VideoManagementListResponse> getAppletsList(VideoManagementPageRequest videoManagementPageReq) {
//        VideoManagementQueryRequest queryReq = KsBeanUtil.convert(videoManagementPageReq, VideoManagementQueryRequest.class);
        List<Long> ids = new ArrayList<>();
        //如果用户登陆了，查询关注的人
        if(!StringUtils.isEmpty(videoManagementPageReq.getCustomerId())){
            VideoFollowQueryRequest request = new VideoFollowQueryRequest();
            request.setFollowCustomerId(videoManagementPageReq.getCustomerId());
            //所有关注的人
            ids = videoFollowService.getVideoFollowByFollowCustomerId(request);
        }
        final List<Long> ids1 = ids;
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        List<VideoManagement> list = videoManagementService.list(VideoManagementQueryRequest.builder().delFlag(DeleteFlag.NO).build());
        if(CollectionUtils.isEmpty(list)){
            return BaseResponse.success(VideoManagementListResponse.builder().build());
        }
        List<Long> videoIds = videoManagementPageReq.getVideoIds();
        if(CollectionUtils.isNotEmpty(videoIds)){
            list = list.stream().filter(videoManagement -> !videoIds.contains(videoManagement.getVideoId())).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(list)){
            return BaseResponse.success(VideoManagementListResponse.builder().build());
        }
        //随机获取五十个,如果小于则随机获取当前数量打乱
        if(list.size() >= 100){
            list = getRandomList(list,100);
        }else {
            Collections.shuffle(list);
        }
        List<VideoManagementVO> videoManagementVOs = list.stream().map(entity -> {
                    VideoManagementVO videoManagementVO = processVideoList(videoManagementPageReq, osdClientParam, entity, ids1);
                    return videoManagementVO;
                }
        ).collect(Collectors.toList());
        return BaseResponse.success(VideoManagementListResponse.builder().videoManagementVOList(videoManagementVOs).build());
    }

    @Override
    public BaseResponse<VideoManagementPageResponse> getAppletsListAndroid(VideoManagementPageRequest videoManagementPageReq) {
        videoSearchRecordService.saveSearchRecord(videoManagementPageReq);
        List<Long> ids = new ArrayList<>();
        //如果用户登陆了，查询关注的人
        if(!StringUtils.isEmpty(videoManagementPageReq.getCustomerId())){
            VideoFollowQueryRequest request = new VideoFollowQueryRequest();
            request.setFollowCustomerId(videoManagementPageReq.getCustomerId());
            //所有关注的人
            ids = videoFollowService.getVideoFollowByFollowCustomerId(request);
        }
        final List<Long> ids1 = ids;
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        List<VideoManagement> list = videoManagementService.list(VideoManagementQueryRequest.builder().state(StateType.PUT_SHELF)
                .videoName(videoManagementPageReq.getVideoName())
                .storeIdList(videoManagementPageReq.getStoreIdList())
                .searchType(videoManagementPageReq.getSearchType())
                .delFlag(DeleteFlag.NO).build());;

        if(CollectionUtils.isEmpty(list)){
            return BaseResponse.success(VideoManagementPageResponse.builder().build());
        }
        List<Long> videoIds = videoManagementPageReq.getVideoIds();
        if(CollectionUtils.isNotEmpty(videoIds)){
            list = list.stream().filter(videoManagement -> !videoIds.contains(videoManagement.getVideoId())).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(list)){
            return BaseResponse.success(VideoManagementPageResponse.builder().build());
        }
        //随机获取五十个,如果小于则随机获取当前数量打乱
        if(list.size() >= 100){
            list = getRandomList(list,100);
        }else {
            Collections.shuffle(list);
        }
        List<VideoManagementVO> videoManagementVOs = list.stream().map(entity -> {
                    return processVideoList(videoManagementPageReq, osdClientParam, entity, ids1);
                }
        ).collect(Collectors.toList());
        Page<VideoManagementVO> videoManagementVOS = new PageImpl<>(videoManagementVOs);
        MicroServicePage<VideoManagementVO> vos = KsBeanUtil.convertPageCopy(videoManagementVOS, VideoManagementVO.class);
        return BaseResponse.success(VideoManagementPageResponse.builder().videoManagementVOPage(vos).build());
    }

    private VideoManagementVO processVideoList(VideoManagementPageRequest videoManagementPageReq, OsdClientParam osdClientParam, VideoManagement entity, List<Long> followList) {
        //获取url
        String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, entity.getArtworkUrl());
        entity.setArtworkUrl(resourceUrl);
        VideoManagementVO videoManagementVO = videoManagementService.wrapperVo(entity);
        if (StringUtils.isNotEmpty(videoManagementPageReq.getCustomerId())) {
            videoManagementVO.setLikeIt(Optional.ofNullable(videoLikeService.queryByIdOrCustomerId(videoManagementVO.getVideoId(), videoManagementPageReq.getCustomerId())).orElse(0));
        }
        videoManagementVO.setVideoLikeNum(Optional.ofNullable(videoLikeService.getVideoLikeNumById(videoManagementVO.getVideoId())).orElse(0l));

        if (videoManagementVO.getStoreId() == null) {
            return videoManagementVO;
        }

        for (Long id : followList) {
            if (videoManagementVO.getStoreId().equals(id)) {
                videoManagementVO.setIsFollow(1);
                break;
            }
        }

        return videoManagementVO;
    }

    /**
     *正则表达式:验证字符串数字
     *两种方式：
     *1.pattern.matcher(number.trim()).find()
     *2.Pattern.matches(numberRegexp,number.trim())
     **/
    public static boolean matchNumber(String number) {
        if (org.springframework.util.StringUtils.isEmpty(number)) {
            return false;
        };
        String numberRegexp = "^([0-9]+)$";
        try {
            Pattern pattern = Pattern.compile(numberRegexp);
            if (pattern.matcher(number.trim()).find()) {
                return true;
            }

        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @Description list 随机取数据
     * @params     list    list集合
     *           num     随机取多少条
     **/
    public static List getRandomList(List list, int num) {
        List olist = new ArrayList<>();
        if (list.size() <= num) {
            return list;
        } else {
            Random random = new Random();
            for (int i = 0 ;i<num;i++){
                int intRandom = random.nextInt(list.size() - 1);
                olist.add(list.get(intRandom));
                list.remove(list.get(intRandom));
            }
            return olist;
        }
    }

    @Override
    public BaseResponse<VideoManagementPageResponse> likePage(@RequestBody @Valid VideoManagementPageRequest videoManagementPageReq) {
        VideoLikeQueryRequest queryReq = KsBeanUtil.convert(videoManagementPageReq, VideoLikeQueryRequest.class);
        List<Long> ids = new ArrayList<>();
        //如果用户登陆了，查询关注的人
        if(!StringUtils.isEmpty(videoManagementPageReq.getCustomerId())){
            VideoFollowQueryRequest request = new VideoFollowQueryRequest();
            request.setFollowCustomerId(videoManagementPageReq.getCustomerId());
            //所有关注的人
            ids = videoFollowService.getVideoFollowByFollowCustomerId(request);
        }
        final List<Long> ids1 = ids;
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<VideoLike> videoManagementPage = videoLikeService.page(queryReq);
        Page<VideoManagementVO> newPage = videoManagementPage.map(entity -> {
                    //获取url
                    VideoManagementVO videoManagementVO = processVideoList(videoManagementPageReq, osdClientParam, entity.getVideoManagement(), ids1);
                    videoManagementVO.setLikeIt(1);
                    return videoManagementVO;
                }
        );
        MicroServicePage<VideoManagementVO> microPage = new MicroServicePage<>(newPage, videoManagementPageReq.getPageable());
        VideoManagementPageResponse finalRes = new VideoManagementPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    /**
     * 关注列表
     * @param videoManagementPageReq
     * @return
     */
    @Override
    public BaseResponse<VideoManagementPageResponse> followPage(VideoManagementPageRequest videoManagementPageReq) {
        VideoFollowQueryRequest convert = KsBeanUtil.convert(videoManagementPageReq, VideoFollowQueryRequest.class);
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        convert.setFollowCustomerId(videoManagementPageReq.getCustomerId());
        //获取我关注的人
        List<Long> ids = videoFollowService.getVideoFollowByFollowCustomerId(convert);
        if(ids==null || ids.size()<=0){
            ids.add(System.currentTimeMillis());
        }
        VideoManagementQueryRequest queryReq = KsBeanUtil.convert(videoManagementPageReq, VideoManagementQueryRequest.class);
        queryReq.setStoreIdList(ids);
        queryReq.setSearchType(null);
        Page<VideoManagement> videoManagementPage = videoManagementService.page(queryReq);
        Page<VideoManagementVO> newPage = videoManagementPage.map(entity -> {
                    VideoManagementVO videoManagementVO = processVideoList(videoManagementPageReq, osdClientParam, entity, ids);
                    videoManagementVO.setIsFollow(1);
                    return videoManagementVO;
                }
        );
        MicroServicePage<VideoManagementVO> microPage = new MicroServicePage<>(newPage, videoManagementPageReq.getPageable());
        VideoManagementPageResponse finalRes = new VideoManagementPageResponse(microPage);
        return BaseResponse.success(finalRes);
    }


    /**
     * 关注
     * @param request
     * @return
     */
    @Override
    public BaseResponse follow(VideoFollowAddRequest request) {
        VideoFollow videoFollow = new VideoFollow();
        videoFollow.setCoverFollowCustomerId(request.getCoverFollowCustomerId());
        videoFollow.setFollowCustomerId(request.getCustomerId());
        videoFollow.setStoreId(request.getStoreId());
        Integer followCount = videoFollowService.getVideoFollowByFollowCustomerIdAndCoverFollowCustomerId(VideoFollowQueryRequest.builder()
                .followCustomerId(request.getCustomerId())
                .coverFollowCustomerId(request.getCoverFollowCustomerId()).build());

        if(followCount > 0){
            return BaseResponse.SUCCESSFUL();
        }
        videoFollowService.saveVideoFollow(videoFollow);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse cancelFollow(VideoFollowCancelRequest request) {
        VideoFollow videoFollow = new VideoFollow();
        videoFollow.setCoverFollowCustomerId(request.getCoverFollowCustomerId());
        videoFollow.setFollowCustomerId(request.getCustomerId());
        videoFollow.setStoreId(request.getStoreId());
        videoFollowService.deleteVideoFollow(videoFollow);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<VideoManagementListResponse> list(@RequestBody @Valid VideoManagementListRequest videoManagementListReq) {
        VideoManagementQueryRequest queryReq = KsBeanUtil.convert(videoManagementListReq, VideoManagementQueryRequest.class);
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        List<VideoManagement> videoManagementList = videoManagementService.list(queryReq);
        List<VideoManagementVO> newList = videoManagementList.stream().map(videoManagement -> {
            //获取url
            String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, videoManagement.getArtworkUrl());
            videoManagement.setArtworkUrl(resourceUrl);
            return videoManagementService.wrapperVo(videoManagement);
        }).collect(Collectors.toList());
        return BaseResponse.success(new VideoManagementListResponse(newList));
    }

    @Override
    public BaseResponse getByIdOrCustomerId(@RequestBody @Valid VideoByIdOrCustomerIdRequest quest) {
        VideoManagement videoManagement = videoManagementService.getOne(quest.getVideoId());
        if (Objects.nonNull(videoManagement)) {
            videoManagementService.playFewById(videoManagement);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<VideoManagementByIdResponse> getDetailsById(@Valid VideoManagementByIdRequest videoManagementByIdRequest) {
        VideoManagement videoManagement = videoManagementService.getOne(videoManagementByIdRequest.getVideoId());
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, videoManagement.getArtworkUrl());
        videoManagement.setArtworkUrl(resourceUrl);
        VideoManagementVO videoManagementVO = videoManagementService.wrapperVo(videoManagement);
        return BaseResponse.success(new VideoManagementByIdResponse(videoManagementVO));
    }
}

