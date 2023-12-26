package com.wanmi.sbc.system;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.*;
import com.wanmi.sbc.setting.api.response.yunservice.YunConfigListResponse;
import com.wanmi.sbc.setting.api.response.yunservice.YunConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 素材服务
 * Created by yinxianzhi on 18/10/15.
 */
@Api(tags = "ResourceServerController", description = "素材服务")
@Slf4j
@RestController
public class ResourceServerController {

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private YunServiceProvider yunServiceProvider;

    @ApiOperation(value = "上传素材")
    @RequestMapping(value = "/uploadResource", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam("uploadFile") List<MultipartFile> multipartFiles, Long
            cateId, ResourceType resourceType) {
        log.info("-----------------------上传素材接口：{}",resourceType);
        operateLogMQUtil.convertAndSend("设置", "上传素材", "上传素材：资源ID" + (Objects.nonNull(cateId) ? cateId : ""));
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
                log.info("-------------------------开始上传：{}",file.getOriginalFilename());
                String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                        .cateId(cateId)
                        .resourceType(resourceType)
                        .content(file.getBytes())
                        .resourceName(file.getOriginalFilename())
                        .build()).getContext();
                resourceUrls.add(resourceUrl);
                log.info("-------------------------上传结束：{}",file.getOriginalFilename());
            } catch (Exception e) {
                throw new SbcRuntimeException(e);
            }
        }
        return ResponseEntity.ok(resourceUrls);
    }

    /**
     * 商品详情富文本编辑器ueditor需要用到的文件上传方法，返回格式与普通的有区别
     *
     * @param uploadFile
     * @param cateId
     * @return
     */
    @RequestMapping(value = "/uploadImage4UEditor", method = RequestMethod.POST)
    public String uploadFile4UEditor(@RequestParam("uploadFile") MultipartFile uploadFile, Long cateId) {
        //验证上传参数
        if (null == uploadFile || uploadFile.getSize() == 0) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        String fileName = uploadFile.getOriginalFilename();
        operateLogMQUtil.convertAndSend("设置", "上传图片编辑", "上传图片编辑：资源ID" + (Objects.nonNull(cateId) ? cateId : ""));
        try {
            // 上传
            String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                    .cateId(cateId)
                    .resourceType(ResourceType.IMAGE)
                    .content(uploadFile.getBytes())
                    .resourceName(fileName)
                    .build()).getContext();
            return "{original:'" + fileName + "',name:'" + fileName + "',url:'"
                    + resourceUrl + "',size:" + uploadFile.getSize() + ",state:'SUCCESS'}";
        } catch (Exception e) {
            throw new SbcRuntimeException(e);
        }
    }

    /**
     * 查询素材服务器
     *
     * @return 素材服务器
     */
    @ApiOperation(value = "查询素材服务器")
    @RequestMapping(value = "/system/resourceServers", method = RequestMethod.GET)
    public BaseResponse<YunConfigListResponse> page() {
        YunConfigListResponse yunConfigListResponse = yunServiceProvider.list(YunConfigListRequest.builder()
                .configKey(ConfigKey.RESOURCESERVER.toString())
                .delFlag(DeleteFlag.NO)
                .build()).getContext();
        return BaseResponse.success(yunConfigListResponse);
    }

    /**
     * 获取素材服务器详情信息
     *
     * @param resourceServerId 编号
     * @return 配置详情
     */
    @ApiOperation(value = "获取素材服务器详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "resourceServerId", value = "素材编号",
            required = true)
    @RequestMapping(value = "/system/resourceServer/{resourceServerId}", method = RequestMethod.GET)
    public BaseResponse<YunConfigResponse> list(@PathVariable Long resourceServerId) {
        YunConfigResponse yunConfigResponse = yunServiceProvider.getById(YunConfigByIdRequest.builder()
                .id(resourceServerId)
                .build()).getContext();
        return BaseResponse.success(yunConfigResponse);
    }

    /**
     * 编辑素材服务器
     */
    @ApiOperation(value = "编辑素材服务器")
    @RequestMapping(value = "/system/resourceServer", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody YunConfigModifyRequest request) {
        if (request.getId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        yunServiceProvider.modify(request);
        operateLogMQUtil.convertAndSend("设置", "编辑素材服务器接口", "编辑素材服务器接口");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 前端上传Base64编码文件内容
     */
    @ApiOperation(value = "前端上传Base64编码文件内容")
    @RequestMapping(value = "/uploadBase64File", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadBase64File(@RequestBody YunBase64FileRequest request, Long
            cateId, ResourceType resourceType) {
        log.info("-----------------------上传Base64文件：{}",resourceType);
        operateLogMQUtil.convertAndSend("设置", "前端上传Base64编码文件内容", "前端上传Base64编码文件内容：资源ID" + (Objects.nonNull(cateId) ? cateId : ""));
        //验证上传参数
        if (StringUtils.isEmpty(request.getContent())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> resourceUrls = new ArrayList<>();
        try {
            StringBuffer fileNameAppend = new StringBuffer();
            fileNameAppend.append(cateId);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
            fileNameAppend.append(dateTimeFormatter.format(LocalDateTime.now()));
            fileNameAppend.append(new Random().nextInt(99999999));
            fileNameAppend.append(".jpg");
            String fileName = fileNameAppend.toString();

            String content = request.getContent();
            if (content.contains("data:")) {
                int start = content.indexOf(",");
                content = content.substring(start + 1);
            }
            // 上传
            log.info("-------------------------开始上传：{}",fileName);
            String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                    .cateId(cateId)
                    .resourceType(resourceType)
                    .content(Base64.getDecoder().decode(content))
                    .resourceName(fileName)
                    .build()).getContext();
            resourceUrls.add(resourceUrl);
            log.info("-------------------------上传结束：{}",fileName);
        } catch (Exception e) {
            throw new SbcRuntimeException(e);
        }
        return ResponseEntity.ok(resourceUrls);
    }
}
