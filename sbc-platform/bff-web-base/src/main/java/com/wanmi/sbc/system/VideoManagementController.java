package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByNameRequest;
import com.wanmi.sbc.customer.api.response.employee.EmployeeListByAccountTypeResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByNameResponse;
import com.wanmi.sbc.customer.bean.vo.EmployeeListByAccountTypeVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementProvider;
import com.wanmi.sbc.setting.api.provider.videomanagement.VideoManagementQueryProvider;
import com.wanmi.sbc.setting.api.request.videomanagement.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementByIdResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementListResponse;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.bean.enums.StateType;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>描述<p>
 * 视频管理~
 * @author zhaowei
 * @date 2021/4/19
 */
@Api(description = "视频管理管理API", tags = "VideoManagementController")
@RestController
@RequestMapping(value = "/videomanagement")
public class VideoManagementController {

    @Autowired
    private VideoManagementQueryProvider videoManagementQueryProvider;

    @Autowired
    private VideoManagementProvider videoManagementProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;


    @ApiOperation(value = "分页查询视频管理")
    @PostMapping("/page")
    public BaseResponse<VideoManagementPageResponse> getPage(@RequestBody @Valid VideoManagementPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.setState(StateType.PUT_SHELF);
        BaseResponse<VideoManagementPageResponse> appletsPage = videoManagementQueryProvider.getAppletsPage(pageReq);
        processUserInfo(appletsPage);
        return appletsPage;
    }

    @ApiOperation(value = "安卓分页查询视频管理")
    @PostMapping(value = "/android")
    public BaseResponse<VideoManagementPageResponse> android(@RequestBody VideoManagementPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.setState(StateType.PUT_SHELF);
        if (!org.springframework.util.StringUtils.isEmpty(pageReq.getVideoName())) {
            BaseResponse<ListStoreByNameResponse> baseResponse = storeQueryProvider.listByName(ListStoreByNameRequest.builder().storeName(pageReq.getVideoName()).build());
            if (!ObjectUtils.isEmpty(baseResponse.getContext()) && !ObjectUtils.isEmpty(baseResponse.getContext().getStoreVOList())) {
                pageReq.setStoreIdList(baseResponse.getContext().getStoreVOList().stream().map(StoreVO::getStoreId).collect(Collectors.toList()));
            }
            else {
                if (((Integer)1).equals(pageReq.getSearchType())) {
                    VideoManagementPageResponse videoManagementPageResponse = new VideoManagementPageResponse();
                    MicroServicePage<VideoManagementVO> videoManagementVOPage = new MicroServicePage<>();
                    videoManagementVOPage.setTotal(0);
                    videoManagementPageResponse.setVideoManagementVOPage(videoManagementVOPage);
                    return BaseResponse.success(videoManagementPageResponse);
                }
            }
        }
        else {
            pageReq.setSearchType(null);
        }
        BaseResponse<VideoManagementPageResponse> appletsListAndroid = videoManagementQueryProvider.getAppletsListAndroid(pageReq);
        processUserInfo(appletsListAndroid);
        return appletsListAndroid;
    }

    @ApiOperation(value = "c端点击视频播放,增加播放数")
    @RequestMapping(value = "/getVideoById", method = RequestMethod.POST)
    public BaseResponse getByIdOrCustomerId(@RequestBody @Valid VideoByIdOrCustomerIdRequest quest) {
        return videoManagementQueryProvider.getByIdOrCustomerId(quest);
    }

    @ApiOperation(value = "点赞列表")
    @RequestMapping(value = "/getLikeVideo", method = RequestMethod.POST)
    public BaseResponse<VideoManagementPageResponse> getLikeVideo(@RequestBody @Valid VideoManagementPageRequest request) {
        if (StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setDelFlag(DeleteFlag.NO);
        request.setState(StateType.PUT_SHELF);
        BaseResponse<VideoManagementPageResponse> response = videoManagementQueryProvider.likePage(request);
        processUserInfo(response);
        return response;
    }


    @ApiOperation(value = "关注列表")
    @RequestMapping(value = "/getVideoFollow",method = RequestMethod.POST)
    public BaseResponse<VideoManagementPageResponse> getVideoFollow(@RequestBody @Valid VideoManagementPageRequest request){
        if (StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-000009");
        }
        request.setDelFlag(DeleteFlag.NO);
        request.setState(StateType.PUT_SHELF);
        BaseResponse<VideoManagementPageResponse> page = videoManagementQueryProvider.followPage(request);
        processUserInfo(page);
        return page;
    }

    @ApiOperation(value = "关注")
    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    public BaseResponse follow(@RequestBody @Valid VideoFollowAddRequest request){
        if (StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-000009");
        }
        return videoManagementQueryProvider.follow(request);
    }

    @ApiOperation(value = "取消关注")
    @RequestMapping(value = "/cancelFollow",method = RequestMethod.POST)
    public BaseResponse cancelFollow(@RequestBody @Valid VideoFollowCancelRequest request){
        if (StringUtils.isEmpty(request.getCustomerId())) {
            throw new SbcRuntimeException("K-000009");
        }
        return videoManagementQueryProvider.cancelFollow(request);
    }

    @ApiOperation(value = "点赞")
    @RequestMapping(value = "/addVideoLike", method = RequestMethod.POST)
    public BaseResponse addVideoLike(@RequestBody @Valid VideoLikeAddRequest addReq) {
        return videoManagementProvider.addVideoLike(addReq);
    }

    @ApiOperation(value = "取消点赞")
    @RequestMapping(value = "/cancelVideoLike", method = RequestMethod.POST)
    public BaseResponse cancelVideoLike(@RequestBody @Valid VideoLikeCancelRequest canceReq) {
        return videoManagementProvider.cancelVideoLike(canceReq);
    }



    public void processUserInfoList(BaseResponse<VideoManagementListResponse> list){
        if(CollectionUtils.isEmpty(list.getContext().getVideoManagementVOList())){
            return;
        }
        List<String> ids = new ArrayList<>();
        List<VideoManagementVO> videoManagementVOList = list.getContext().getVideoManagementVOList();
        videoManagementVOList.stream().forEach(videoManagementVO -> {
            String coverFollowCustomerId = videoManagementVO.getCoverFollowCustomerId();
            if(!StringUtils.isEmpty(coverFollowCustomerId)){
                ids.add(coverFollowCustomerId);
            }
        });
        //有用户,查询用户信息,给用户设置信息
        if(ids.size()>0){
            EmployeeByIdsRequest employeeByIdsRequest = new EmployeeByIdsRequest();
            employeeByIdsRequest.setEmployeeIds(ids);
            BaseResponse<EmployeeListByAccountTypeResponse> byIds = employeeQueryProvider.getByIds(employeeByIdsRequest);
            List<EmployeeListByAccountTypeVO> employeeList = byIds.getContext().getEmployeeList();
            //进行数据比对
            videoManagementVOList.stream().forEach(videoManagementVO -> {
                for (EmployeeListByAccountTypeVO employeeListByAccountTypeVO: employeeList) {
                    if(videoManagementVO.getCoverFollowCustomerId().equals(employeeListByAccountTypeVO.getEmployeeId())){
                        videoManagementVO.setEmployeeName(employeeListByAccountTypeVO.getEmployeeName());
                        //退出循环
                        break;
                    }
                }
            });
        }
    }

    public void processUserInfo(BaseResponse<VideoManagementPageResponse> page){
        if(page.getContext().getVideoManagementVOPage() == null
                || ObjectUtils.isEmpty(page.getContext().getVideoManagementVOPage().getContent())){
            return;
        }
        List<String> ids = new ArrayList<>();
        MicroServicePage<VideoManagementVO> videoManagementVOPage = page.getContext().getVideoManagementVOPage();
        videoManagementVOPage.stream().forEach(videoManagementVO -> {
            String coverFollowCustomerId = videoManagementVO.getCoverFollowCustomerId();
            if(!StringUtils.isEmpty(coverFollowCustomerId)){
                ids.add(coverFollowCustomerId);
            }
        });
        //有用户,查询用户信息,给用户设置信息
        if(ids.size()>0){
            EmployeeByIdsRequest employeeByIdsRequest = new EmployeeByIdsRequest();
            employeeByIdsRequest.setEmployeeIds(ids);
            BaseResponse<EmployeeListByAccountTypeResponse> byIds = employeeQueryProvider.getByIds(employeeByIdsRequest);
            List<EmployeeListByAccountTypeVO> employeeList = byIds.getContext().getEmployeeList();
            //进行数据比对
            videoManagementVOPage.stream().forEach(videoManagementVO -> {
                for (EmployeeListByAccountTypeVO employeeListByAccountTypeVO: employeeList) {
                    if(videoManagementVO.getCoverFollowCustomerId().equals(employeeListByAccountTypeVO.getEmployeeId())){
                        videoManagementVO.setEmployeeName(employeeListByAccountTypeVO.getEmployeeName());
                        //退出循环
                        break;
                    }
                }
            });
        }

        List<Long> storeIds = page.getContext().getVideoManagementVOPage().getContent().stream().map(VideoManagementVO::getStoreId).distinct().collect(Collectors.toList());
        ListStoreByIdsRequest listStoreByIdsRequest = ListStoreByIdsRequest.builder().storeIds(storeIds).build();
        BaseResponse<ListStoreByIdsResponse> storeResponse = storeQueryProvider.listByIds(listStoreByIdsRequest);
        for (VideoManagementVO videoManagementVO : page.getContext().getVideoManagementVOPage().getContent()) {
            for (StoreVO storeVO : storeResponse.getContext().getStoreVOList()) {
                if (storeVO.getStoreId().equals(videoManagementVO.getStoreId())) {
                    videoManagementVO.setStoreName(storeVO.getStoreName());
                    videoManagementVO.setStoreLogo(storeVO.getStoreLogo());
                    break;
                }
            }
        }
    }

}
