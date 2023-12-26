package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateQueryProvider;
import com.wanmi.sbc.setting.api.provider.videoresourcecate.VideoResourceCateSaveProvider;
import com.wanmi.sbc.setting.api.request.AllRoleMenuInfoListRequest;
import com.wanmi.sbc.setting.api.request.videomanagement.VideoManagementPageRequest;
import com.wanmi.sbc.setting.api.request.videoresourcecate.*;
import com.wanmi.sbc.setting.api.response.videomanagement.VideoManagementPageResponse;
import com.wanmi.sbc.setting.api.response.videoresourcecate.*;
import com.wanmi.sbc.setting.bean.vo.VideoManagementVO;
import com.wanmi.sbc.setting.bean.vo.VideoResourceCateVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 视频教程资源分类管理API
 * @author hudong
 * @date 2023-06-26 14:04:22
 */
@Api(description = "视频教程资源分类管理API", tags = "VideoResourceCateManagerController")
@RestController
@RequestMapping(value = "/videoResourceCateManager")
public class VideoResourceCateManagerController {
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private VideoResourceCateSaveProvider videoResourceCateSaveProvider;

    @Autowired
    private VideoResourceCateQueryProvider videoResourceCateQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 查询视频教程素材分类
     */
    @ApiOperation(value = "查询视频教程素材分类")
    @RequestMapping(value = {"/video/resourceCates"}, method = RequestMethod.POST)
    public BaseResponse<VideoResourceCateListResponse> list(@RequestBody @Valid VideoResourceCateListRequest listReq) {
        VideoResourceCateListRequest queryRequest = VideoResourceCateListRequest.builder()
                .storeId(0L).cateType(listReq.getCateType()).build();
        BaseResponse<VideoResourceCateListResponse> response = videoResourceCateQueryProvider.list
                (queryRequest);
        return response;
    }

    @ApiOperation(value = "分页查询视频教程素材分类")
    @PostMapping("/page")
    public BaseResponse<VideoResourceCatePageResponse> getPage(@RequestBody @Valid VideoResourceCatePageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        if (pageReq.getStoreId() == null || pageReq.equals(0)) {
            pageReq.setStoreId(commonUtil.getStoreId());
        }
        BaseResponse<VideoResourceCatePageResponse> baseResponse = videoResourceCateQueryProvider.page(pageReq);
//        MicroServicePage<VideoResourceCateVO> microServicePage = baseResponse.getContext().getVideoResourceCateVOPage();
//
//        if (!ObjectUtils.isEmpty(microServicePage) && !ObjectUtils.isEmpty(microServicePage.getContent())) {
//            List<VideoResourceCateVO> videoList = microServicePage.getContent();
//        }
        return baseResponse;
    }


    /**
     * 新增视频教程素材分类
     */
    @ApiOperation(value = "新增视频教程素材分类")
    @RequestMapping(value = "/video/resourceCate", method = RequestMethod.POST)
    public BaseResponse add(@RequestBody VideoResourceCateAddRequest addReq) {
        if (addReq == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        addReq.setStoreId(commonUtil.getStoreId());
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());

        BaseResponse<VideoResourceCateAddResponse> response = videoResourceCateSaveProvider.add(addReq);
        //记录操作日志
        if (Objects.nonNull(addReq.getCateParentId())) {
            operateLogMQUtil.convertAndSend("设置", "新增子分类", "新增子分类：" + addReq.getCateName());
        } else {
            operateLogMQUtil.convertAndSend("设置", "新增一级分类", "新增一级分类：" + addReq.getCateName());
        }

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 编辑视频教程素材分类
     */
    @ApiOperation(value = "编辑视频教程素材分类")
    @RequestMapping(value = "/video/resourceCate", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody VideoResourceCateModifyRequest modifyReq) {
        if (modifyReq == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        //查询对应分类名称
        Optional<VideoResourceCateVO> cateInfoOptional = this.findCateInfoByCateId(modifyReq.getCateId());

        modifyReq.setStoreId(commonUtil.getStoreId());
        modifyReq.setCompanyInfoId(commonUtil.getCompanyInfoId());

        BaseResponse<VideoResourceCateModifyResponse> response = videoResourceCateSaveProvider.modify(modifyReq);

        //记录操作日志
        cateInfoOptional.ifPresent(map -> operateLogMQUtil.convertAndSend("设置", "编辑分类",
                "分类名称：" + map.getCateName() + " 改成 " + modifyReq.getCateName()));
        return response;
    }



    /**
     * 检测视频教程素材分类是否有子类
     */
    @ApiOperation(value = "检测视频教程素材分类是否有子类")
    @RequestMapping(value = "/video/resourceCate/child", method = RequestMethod.POST)
    public BaseResponse checkChild(@RequestBody VideoResourceCateCheckChildRequest request) {
        if (request == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setStoreId(commonUtil.getStoreId());
        BaseResponse<Integer> response =videoResourceCateQueryProvider.checkChild(request);
        return response;
    }




    /**
     * 检测店铺素材分类是否已关联素材
     */
    @ApiOperation(value = "检测店铺素材分类是否已关联素材")
    @RequestMapping(value = "/video/resourceCate/resource", method = RequestMethod.POST)
    public BaseResponse checkResource(@RequestBody VideoResourceCateCheckResourceRequest request) {
        if (request == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        request.setStoreId(commonUtil.getStoreId());
        BaseResponse<Integer> response =videoResourceCateQueryProvider.checkResource(request);
        return BaseResponse.success(response);
    }


    /**
     * 删除店铺素材分类
     */
    @ApiOperation(value = "删除店铺素材分类")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "cateId", value = "分类Id", required = true)
    @RequestMapping(value = "/video/resourceCate/{cateId}", method = RequestMethod.DELETE)
    public BaseResponse delete(@PathVariable String cateId) {
        if (cateId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        //查询对应分类名称
        Optional<VideoResourceCateVO> cateInfoOptional = this.findCateInfoByCateId(cateId);

        VideoResourceCateDelByIdRequest delByIdReq = new VideoResourceCateDelByIdRequest();
        delByIdReq.setCateId(cateId);
        delByIdReq.setStoreId(commonUtil.getStoreId());
        BaseResponse response = videoResourceCateSaveProvider.delete(delByIdReq);

        //记录操作人日志
        cateInfoOptional.ifPresent(map -> operateLogMQUtil.convertAndSend("设置", "删除分类",
                "删除分类：" + map.getCateName()));
        return response;
    }


    /**
     * 初始化二三级目录视频教程素材分类
     */
    @ApiOperation(value = "初始化二三级目录视频教程素材分类")
    @RequestMapping(value = "/video/resourceCate/initMenuInfoListToCate", method = RequestMethod.POST)
    public BaseResponse initMenuInfoListToCate(@RequestBody AllRoleMenuInfoListRequest infoListRequest) {
        if (infoListRequest == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        videoResourceCateSaveProvider.initMenuInfoListToCate(infoListRequest);
        operateLogMQUtil.convertAndSend("设置", "视频教程管理", "初始化视频教程素材资源：" + infoListRequest.getSystemTypeCd().toValue());
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 根据cateId查询CateInfo
     *
     * @param cateId
     * @return
     */
    private Optional<VideoResourceCateVO> findCateInfoByCateId(String cateId) {
        if (cateId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        VideoResourceCateByIdRequest queryRequest = new VideoResourceCateByIdRequest();
        queryRequest.setCateId(cateId);
        BaseResponse<VideoResourceCateByIdResponse> response = videoResourceCateQueryProvider.getById(queryRequest);
        VideoResourceCateVO videoResourceCateVO = response.getContext().getVideoResourceCateVO();
        if (Objects.isNull(videoResourceCateVO)) {
            return Optional.empty();
        }
        return Optional.of(videoResourceCateVO);
    }

}
