package com.wanmi.sbc.setting.systemstoreconfig.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.api.request.systemstoreconfig.SystemStoreConfigQueryRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.systemstoreconfig.model.root.SystemStoreConfig;
import com.wanmi.sbc.setting.systemstoreconfig.repository.SystemStoreConfigRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.wanmi.sbc.setting.bean.vo.SystemStoreConfigVO;
import com.wanmi.sbc.common.util.KsBeanUtil;

import java.util.List;
import java.util.Objects;

/**
 * @desc  系统配置表业务逻辑
 * @author shiy  2023/7/3 16:45
*/
@Service("SystemStoreConfigService")
public class SystemStoreConfigService {
    @Autowired
    private SystemStoreConfigRepository systemStoreConfigRepository;

    /**
     * 新增系统配置表
     *
     * @author yang
     */
    @Transactional
    public SystemStoreConfig add(SystemStoreConfig entity) {
        systemStoreConfigRepository.save(entity);
        return entity;
    }

    /**
     * 修改系统配置表
     *
     * @author yang
     */
    @Transactional
    public SystemStoreConfig modify(SystemStoreConfig entity) {
        systemStoreConfigRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除系统配置表
     *
     * @author yang
     */
    @Transactional
    public void deleteById(Long id) {
        systemStoreConfigRepository.deleteById(id);
    }

    /**
     * 批量删除系统配置表
     *
     * @author yang
     */
    @Transactional
    public void deleteByIdList(List<Long> ids) {
        systemStoreConfigRepository.deleteByIdList(ids);
    }

    /**
     * 单个查询系统配置表
     *
     * @author yang
     */
    public SystemStoreConfig getById(Long id) {
        return systemStoreConfigRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询系统配置表
     *
     * @author yang
     */
    public Page<SystemStoreConfig> page(SystemStoreConfigQueryRequest queryReq) {
        return systemStoreConfigRepository.findAll(
                SystemStoreConfigWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询系统配置表
     *
     * @author yang
     */
    public List<SystemStoreConfig> list(SystemStoreConfigQueryRequest queryReq) {
        return systemStoreConfigRepository.findAll(
                SystemStoreConfigWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * @desc  转换VO集合
     * @author shiy  2023/7/4 8:56
    */
    public List<SystemStoreConfigVO> getVoList(SystemStoreConfigQueryRequest queryReq) {
        List<SystemStoreConfig> configList = list(queryReq);
        List<SystemStoreConfigVO> configVOList = KsBeanUtil.copyListProperties(configList,SystemStoreConfigVO.class );
        return configVOList;
    }

    /**
     * 查询可用的云配置
     * @return
     */
    public SystemStoreConfig getAvailableYun() {
        SystemStoreConfigQueryRequest queryRequest = SystemStoreConfigQueryRequest.builder()
                .configKey(ConfigKey.RESOURCESERVER.toString())
                .status(EnableStatus.ENABLE.toValue())
                .delFlag(DeleteFlag.NO)
                .build();
        List<SystemStoreConfig> SystemStoreConfigList = list(queryRequest);
        if (Objects.nonNull(SystemStoreConfigList) && SystemStoreConfigList.size() > 0) {
            return SystemStoreConfigList.get(0);
        } else {
            throw new SbcRuntimeException("K-061001");
        }
    }

    /**
     * 将实体包装成VO
     *
     * @author yang
     */
    public SystemStoreConfigVO wrapperVo(SystemStoreConfig SystemStoreConfig) {
        if (SystemStoreConfig != null) {
            SystemStoreConfigVO SystemStoreConfigVO = new SystemStoreConfigVO();
            KsBeanUtil.copyPropertiesThird(SystemStoreConfig, SystemStoreConfigVO);
            return SystemStoreConfigVO;
        }
        return null;
    }

}
