package com.wanmi.sbc.store;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.setting.api.provider.storeresource.StoreResourceQueryProvider;
import com.wanmi.sbc.setting.api.provider.storeresource.StoreResourceSaveProvider;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.storeresource.*;
import com.wanmi.sbc.setting.api.request.videoresource.VideoResourceAddRequest;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.api.response.storeresource.StoreResourcePageResponse;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 店铺素材服务
 * Created by yinxianzhi on 18/10/18.
 * 完全参考平台素材管理
 */
@Api(tags = "StoreResourceController", description = "店铺素材服务 API")
@RestController
@RequestMapping("/store")
public class StoreResourceController {

    private static final Logger logger = LoggerFactory.getLogger(StoreResourceController.class);

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private StoreResourceSaveProvider storeResourceSaveProvider;

    @Autowired
    private StoreResourceQueryProvider storeResourceQueryProvider;

    @Autowired
    private YunServiceProvider yunServiceProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;



    /**
     * 分页店铺素材
     *
     * @param pageReq 店铺素材参数
     * @return
     */
    @ApiOperation(value = "分页店铺素材")
    @RequestMapping(value = "/resources", method = RequestMethod.POST)
    public BaseResponse page(@RequestBody @Valid StoreResourcePageRequest pageReq) {
        pageReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        pageReq.setStoreId(commonUtil.getStoreId());
        BaseResponse<StoreResourcePageResponse> response = storeResourceQueryProvider.page(pageReq);
        return BaseResponse.success(response.getContext().getStoreResourceVOPage());
    }

    /**
     * 上传店铺素材
     *
     * @param multipartFiles
     * @param cateId         分类id
     * @return
     */
    @ApiOperation(value = "上传店铺素材", notes = "resourceType-->IMAGE: 图片, VIDEO: 视频, LOGO: logo")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "List",
                    name = "uploadFile", value = "上传素材", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long",
                    name = "cateId", value = "素材分类Id", required = true),
            @ApiImplicitParam(paramType = "query",
                    name = "resourceType", value = "素材类型", required = true)
    })
    @RequestMapping(value = "/uploadStoreResource", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam("uploadFile") List<MultipartFile> multipartFiles, Long
            cateId, ResourceType resourceType) {

        //验证上传参数
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> resourceUrls = new ArrayList<>();
        long maxSize = 2048*1000L;

        if (resourceType.equals(ResourceType.IMAGE)){
            for (MultipartFile file : multipartFiles) {
                if (file == null || file.getSize() == 0) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                try {
                    byte[] bytes;
                    if(file.getSize() > maxSize){

                        String classPath = HttpUtil.getProjectRealPath();
                        String originalFilename = file.getOriginalFilename();
                        assert originalFilename != null;
                        String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                        File tempFile = new File(classPath + UUID.randomUUID() + fileSuffix);
                        Thumbnails.of(file.getInputStream()).scale(0.25f).toFile(tempFile);
                        bytes = Files.readAllBytes(tempFile.toPath());
                        tempFile.delete();
                    }else{
                        bytes=file.getBytes();
                    }
                    // 上传
                    String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                            .cateId(cateId)
                            .storeId(commonUtil.getStoreId())
                            .companyInfoId(commonUtil.getCompanyInfoId())
                            .resourceType(resourceType)
                            .resourceName(file.getOriginalFilename())
                            .content(bytes)
                            .build()).getContext();
                    resourceUrls.add(resourceUrl);
                } catch (Exception e) {
                    logger.error("uploadStoreResource error: {}", e.getMessage());
                    return ResponseEntity.ok(BaseResponse.FAILED());
                }
            }
        }else{
            for (MultipartFile file : multipartFiles) {
                if (file == null || file.getSize() == 0) {
                    throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
                }
                try {
                    // 上传
                    String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                            .cateId(cateId)
                            .storeId(commonUtil.getStoreId())
                            .companyInfoId(commonUtil.getCompanyInfoId())
                            .resourceType(resourceType)
                            .resourceName(file.getOriginalFilename())
                            .content(file.getBytes())
                            .build()).getContext();
                    resourceUrls.add(resourceUrl);
                } catch (Exception e) {
                    logger.error("uploadStoreResource error: {}", e.getMessage());
                    return ResponseEntity.ok(BaseResponse.FAILED());
                }
            }
        }

        return ResponseEntity.ok(resourceUrls);
    }

    /**
     * 编辑店铺素材
     */
    @ApiOperation(value = "编辑店铺素材")
    @RequestMapping(value = "/resource", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody @Valid StoreResourceModifyRequest
                                     modifyReq) {
        operateLogMQUtil.convertAndSend("店铺素材服务", "编辑店铺素材", "编辑店铺素材：素材ID" + (Objects.nonNull(modifyReq) ? modifyReq.getResourceId() : ""));
        modifyReq.setStoreId(commonUtil.getStoreId());
        modifyReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        modifyReq.setUpdateTime(LocalDateTime.now());
        return storeResourceSaveProvider.modify(modifyReq);
    }

    /**
     * 删除店铺素材
     */
    @ApiOperation(value = "删除店铺素材")
    @RequestMapping(value = "/resource", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody @Valid StoreResourceDelByIdListRequest delByIdListReq) {
        operateLogMQUtil.convertAndSend("店铺素材服务", "删除店铺素材", "删除店铺素材" );
        delByIdListReq.setStoreId(commonUtil.getStoreId());

        return storeResourceSaveProvider.deleteByIdList(delByIdListReq);
    }


    /**
     * 批量修改店铺素材的分类
     */
    @ApiOperation(value = "批量修改店铺素材的分类")
    @RequestMapping(value = "/resource/resourceCate", method = RequestMethod.PUT)
    public BaseResponse updateCate(@RequestBody @Valid StoreResourceMoveRequest
                                           moveRequest) {
        moveRequest.setStoreId(commonUtil.getStoreId());
        operateLogMQUtil.convertAndSend("店铺素材服务", "批量修改店铺素材的分类", "批量修改店铺素材的分类" );
        return storeResourceSaveProvider.move(moveRequest);
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
    public BaseResponse saveResources(@RequestBody @Valid StoreResourceAddRequest addRequest) {
        return storeResourceSaveProvider.add(addRequest);
    }
}
