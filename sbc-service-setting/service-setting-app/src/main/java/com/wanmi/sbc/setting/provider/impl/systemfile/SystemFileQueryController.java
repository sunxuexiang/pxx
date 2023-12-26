package com.wanmi.sbc.setting.provider.impl.systemfile;

import com.wanmi.osd.OsdClient;
import com.wanmi.osd.bean.OsdClientParam;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.systemfile.SystemFileQueryProvider;
import com.wanmi.sbc.setting.api.request.systemfile.*;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileByIdResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFileListResponse;
import com.wanmi.sbc.setting.api.response.systemfile.SystemFilePageResponse;
import com.wanmi.sbc.setting.bean.vo.SystemFileVO;
import com.wanmi.sbc.setting.systemconfig.model.root.SystemConfig;
import com.wanmi.sbc.setting.systemconfig.service.SystemConfigService;
import com.wanmi.sbc.setting.systemfile.model.root.SystemFile;
import com.wanmi.sbc.setting.systemfile.service.SystemFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 平台文件查询服务接口实现
 * @author hudong
 * @date 2023-09-08 09:17
 */
@RestController
@Validated
public class SystemFileQueryController implements SystemFileQueryProvider {
    @Autowired
    private SystemFileService systemFileService;

    @Autowired
    private SystemConfigService systemConfigService;


    @Override
    public BaseResponse<SystemFilePageResponse> page(SystemFilePageRequest systemFilePageRequest) {
        SystemFileQueryRequest queryReq = new SystemFileQueryRequest();
        KsBeanUtil.copyPropertiesThird(systemFilePageRequest, queryReq);
        queryReq.setDelFlag(DeleteFlag.NO);
        queryReq.putSort("createTime", SortType.DESC.toValue());
        queryReq.putSort("id", SortType.ASC.toValue());
        // 查询可用云服务
        SystemConfig availableYun = systemConfigService.getAvailableYun();
        OsdClientParam osdClientParam = OsdClientParam.builder()
                .configType(availableYun.getConfigType())
                .context(availableYun.getContext())
                .build();
        Page<SystemFile> systemFilePage = systemFileService.page(queryReq);
        Page<SystemFileVO> newPage = systemFilePage.map(entity -> {
                    //获取url
                    String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, entity.getFileKey());
                    entity.setPath(resourceUrl);
                    return systemFileService.wrapperVo(entity);
                }
        );
        MicroServicePage<SystemFileVO> microPage = new MicroServicePage<>(newPage, systemFilePageRequest.getPageable());
        SystemFilePageResponse finalRes = new SystemFilePageResponse(microPage);
        return BaseResponse.success(finalRes);
    }

    @Override
    public BaseResponse<SystemFileListResponse> list(SystemFileListRequest systemFileListRequest) {
        SystemFileQueryRequest queryReq = new SystemFileQueryRequest();
        KsBeanUtil.copyPropertiesThird(systemFileListRequest, queryReq);
        List<SystemFile> systemFileList = systemFileService.list(queryReq);
        List<SystemFileVO> newList = systemFileList.stream().map(entity -> systemFileService.wrapperVo(entity)).collect(Collectors.toList());
        return BaseResponse.success(new SystemFileListResponse(newList));
    }

    @Override
    public BaseResponse<SystemFileByIdResponse> getById(SystemFileByIdRequest systemFileByIdRequest) {
        SystemFile systemFile = systemFileService.getById(systemFileByIdRequest.getId());
        return BaseResponse.success(new SystemFileByIdResponse(systemFileService.wrapperVo(systemFile)));
    }

    @Override
    public BaseResponse<SystemFileByIdResponse> getByFileKey(SystemFileByFileKeyRequest systemFileByFileKeyRequest) {
        SystemFile systemFile = systemFileService.getByFileKey(systemFileByFileKeyRequest.getFileKey());
        if (Objects.nonNull(systemFile)) {
            // 查询可用云服务
            SystemConfig availableYun = systemConfigService.getAvailableYun();
            OsdClientParam osdClientParam = OsdClientParam.builder()
                    .configType(availableYun.getConfigType())
                    .context(availableYun.getContext())
                    .build();
            //获取url
            String resourceUrl = OsdClient.instance().getResourceUrl(osdClientParam, systemFile.getFileKey());
            systemFile.setPath(resourceUrl);
            return BaseResponse.success(new SystemFileByIdResponse(systemFileService.wrapperVo(systemFile)));
        }
        return null;
    }
}

