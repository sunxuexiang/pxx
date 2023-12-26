package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.RoleMenuProvider;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.videoresource.VideoResourceSaveProvider;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.videoresource.*;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadVideoResourceRequest;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourceAddResponse;
import com.wanmi.sbc.setting.api.response.videoresource.VideoResourcePageResponse;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 视频教程资源管理API
 * @author hudong
 * @date 2023-06-26 14:04:22
 */
@Api(description = "视频资源管理API", tags = "VideoResourceManagerController")
@RestController
@RequestMapping(value = "/videoResourceManager")
public class VideoResourceManagerController {


    private static final Logger logger = LoggerFactory.getLogger(VideoResourceManagerController.class);

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private VideoResourceSaveProvider videoResourceSaveProvider;

    @Autowired
    private VideoResourceQueryProvider videoResourceQueryProvider;

    @Autowired
    private YunServiceProvider yunServiceProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 分页视频教程素材
     *
     * @param pageReq 视频教程素材参数
     * @return
     */
    @ApiOperation(value = "分页视频教程素材")
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public BaseResponse page(@RequestBody @Valid VideoResourcePageRequest pageReq) {

        pageReq.setStoreId(0L);
        BaseResponse<VideoResourcePageResponse> response = videoResourceQueryProvider.page(pageReq);
        return BaseResponse.success(response.getContext().getVideoResourceVOPage());
    }

    /**
     * 上传店铺素材
     *
     * @param multipartFiles
     * @param cateId         分类id
     * @return
     */
    @ApiOperation(value = "上传视频教程素材", notes = "resourceType-->0: 图片, 1: 视频, 2: logo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "List",
                    name = "uploadFile", value = "上传素材", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String",
                    name = "cateId", value = "素材分类Id", required = true),
            @ApiImplicitParam(paramType = "query",
                    name = "resourceType", value = "素材类型", required = true)
    })
    @RequestMapping(value = "/uploadVideoResource", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam("uploadFile") List<MultipartFile> multipartFiles, String
            cateId, ResourceType resourceType,Integer hvType) {

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
                String resourceUrl = yunServiceProvider.uploadVideoFile(YunUploadVideoResourceRequest.builder()
                        .cateId(cateId)
                        .storeId(0L)
                        .companyInfoId(0L)
                        .resourceType(resourceType)
                        .hvType(hvType)
                        .resourceName(file.getOriginalFilename())
                        .content(file.getBytes())
                        .build()).getContext();
                resourceUrls.add(resourceUrl);
            } catch (Exception e) {
                logger.error("uploadVideoResource error: {}", e.getMessage());
                return ResponseEntity.ok(BaseResponse.FAILED());
            }
        }
        operateLogMQUtil.convertAndSend("设置", "上传视频教程素材", "操作成功");
        return ResponseEntity.ok(resourceUrls);
    }

    /**
     * 编辑视频教程素材
     */
    @ApiOperation(value = "编辑视频教程素材")
    @RequestMapping(value = "/resource", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody @Valid VideoResourceModifyRequest
                                     modifyReq) {
        operateLogMQUtil.convertAndSend("设置", "编辑视频教程素材", "编辑视频教程素材：素材ID" + (Objects.nonNull(modifyReq) ? modifyReq.getCateId() : ""));
        modifyReq.setStoreId(Objects.isNull(commonUtil.getStoreId()) ? 0L : commonUtil.getStoreId());
        modifyReq.setUpdateTime(LocalDateTime.now());
        return videoResourceSaveProvider.modify(modifyReq);
    }

    /**
     * 删除视频教程素材
     */
    @ApiOperation(value = "删除视频教程素材")
    @RequestMapping(value = "/resource", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody @Valid VideoResourceDelByIdListRequest delByIdListReq) {

        delByIdListReq.setStoreId(Objects.isNull(commonUtil.getStoreId()) ? 0L : commonUtil.getStoreId());
        operateLogMQUtil.convertAndSend("设置", "删除视频教程素材", "删除视频教程素材");
        return videoResourceSaveProvider.deleteByIdList(delByIdListReq);
    }


    /**
     * 批量修改视频教程素材的分类
     */
    @ApiOperation(value = "批量修改视频教程素材的分类")
    @RequestMapping(value = "/resource/resourceCate", method = RequestMethod.PUT)
    public BaseResponse updateCate(@RequestBody @Valid VideoResourceMoveRequest
                                           moveRequest) {
        moveRequest.setStoreId(Objects.isNull(commonUtil.getStoreId()) ? 0L : commonUtil.getStoreId());
        operateLogMQUtil.convertAndSend("设置", "批量修改视频教程素材的分类", "批量修改视频教程素材的分类");
        return videoResourceSaveProvider.move(moveRequest);
    }

    /**
     * 获取OSS上传资源
     */
    @ApiOperation(value = "获取OSS上传资源")
    @RequestMapping(value = "/resource/getOssToken", method = RequestMethod.GET)
    public BaseResponse getOssToken(){
         return yunServiceProvider.getOssToken();
    }

    /**
     * 保存地址
     * @return
     */
    @ApiOperation(value = "保存地址")
    @RequestMapping(value = "/saveResources", method = RequestMethod.POST)
    public BaseResponse saveResources(@RequestBody @Valid VideoResourceAddRequest addRequest) {
        return videoResourceSaveProvider.saveResources(addRequest);
    }

}
