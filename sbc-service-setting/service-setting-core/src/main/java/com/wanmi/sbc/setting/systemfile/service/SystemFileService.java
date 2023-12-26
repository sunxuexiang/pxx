package com.wanmi.sbc.setting.systemfile.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.request.systemfile.SystemFileQueryRequest;
import com.wanmi.sbc.setting.bean.vo.SystemFileVO;
import com.wanmi.sbc.setting.systemfile.model.root.SystemFile;
import com.wanmi.sbc.setting.systemfile.repository.SystemFileRepository;
import com.wanmi.sbc.setting.yunservice.YunService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 平台文件业务逻辑
 *
 * @author hudong
 * @date 2023-09-08 16:12:49
 */
@Service("SystemFileService")
public class SystemFileService {
    @Autowired
    private SystemFileRepository systemFileRepository;


    /**
     * 新增平台文件
     *
     * @author hd
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemFile add(SystemFile entity) {
        entity.setDelFlag(DeleteFlag.NO);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        systemFileRepository.save(entity);
        return entity;
    }

    /**
     * 修改平台文件
     *
     * @author hd
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemFile modify(SystemFile newVideoResource) {
        SystemFile oldVideoResource = systemFileRepository.findById(newVideoResource.getId()).orElse(null);
        if (oldVideoResource == null || oldVideoResource.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(SettingErrorCode.RESOURCE_NOT_EXIST_ERROR);
        }
        //更新素材
        newVideoResource.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(newVideoResource, oldVideoResource);
        systemFileRepository.save(oldVideoResource);
        return oldVideoResource;
    }

    /**
     * 单个删除平台文件
     *
     * @author hd
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        systemFileRepository.deleteById(id);
    }

    /**
     * 批量更新平台文件
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePathByIds(String path, List<Long> ids) {
        systemFileRepository.updatePathByIds(path, ids);
    }

    /**
     * 批量删除平台文件
     *
     * @author hd
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        SystemFileQueryRequest queryRequest = SystemFileQueryRequest.builder()
                .idList(ids)
                .build();
        List<SystemFile> systemFiles = systemFileRepository.findAll(SystemFileWhereCriteriaBuilder.build(queryRequest));
        if (CollectionUtils.isNotEmpty(systemFiles)) {
            systemFiles.forEach( systemFile -> {
                deleteById(systemFile.getId());
            });
        }
    }

    /**
     * 批量删除店铺资源库
     *
     * @author hd
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdList(List<Long> ids) {
        systemFileRepository.deleteByIdList(ids);
    }


    /**
     * 单个查询文件
     *
     * @author hd
     */
    public SystemFile getById(Long id) {
        return systemFileRepository.findById(id).orElse(null);
    }

    /**
     * 单个查询文件
     *
     * @author hd
     */
    public SystemFile getByFileKey(String fileKey) {
        return systemFileRepository.findByFileKey(fileKey);
    }

    /**
     * 分页查询文件
     *
     * @author hd
     */
    public Page<SystemFile> page(SystemFileQueryRequest queryReq) {
        return systemFileRepository.findAll(
                SystemFileWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询文件
     *
     * @author hd
     */
    public List<SystemFile> list(SystemFileQueryRequest queryReq) {
        return systemFileRepository.findAll(
                SystemFileWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }



    /**
     * 将实体包装成VO
     *
     * @author hd
     */
    public SystemFileVO wrapperVo(SystemFile systemFile) {
        if (systemFile != null) {
            SystemFileVO systemFileVO = new SystemFileVO();
            KsBeanUtil.copyPropertiesThird(systemFile, systemFileVO);
            return systemFileVO;
        }
        return null;
    }
}
