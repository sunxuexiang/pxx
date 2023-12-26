package com.wanmi.sbc.setting.systemcancellationpolicy.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.request.systemcancellationpolicy.SystemCancellationPolicyQueryRequest;
import com.wanmi.sbc.setting.api.request.systemprivacypolicy.SystemPrivacyPolicyQueryRequest;
import com.wanmi.sbc.setting.bean.vo.SystemCancellationPolicyVO;
import com.wanmi.sbc.setting.systemcancellationpolicy.model.root.SystemCancellationPolicy;
import com.wanmi.sbc.setting.systemcancellationpolicy.repository.SystemCancellationPolicyRepository;
import com.wanmi.sbc.setting.systemprivacypolicy.model.root.SystemPrivacyPolicy;
import com.wanmi.sbc.setting.systemprivacypolicy.service.SystemPrivacyPolicyWhereCriteriaBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>隐私政策业务逻辑</p>
 *
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@Service("SystemCancellationPolicyService")
public class SystemCancellationPolicyService {
    @Autowired
    private SystemCancellationPolicyRepository systemCancellationPolicyRepository;


    /**
     * 查询隐私政策设置
     *
     * @return
     */
    public SystemCancellationPolicy querySystemCancellationPolicy() {
        List<SystemCancellationPolicy> configs = systemCancellationPolicyRepository.findByDelFlag(DeleteFlag.NO);
        SystemCancellationPolicy systemCancellationPolicy = new SystemCancellationPolicy();
        if (CollectionUtils.isNotEmpty(configs)) {
            systemCancellationPolicy = configs.get(0);
        }
        return systemCancellationPolicy;
    }


    /**
     * 新增隐私政策
     *
     * @author yangzhen
     */
    @Transactional
    public SystemCancellationPolicy add(SystemCancellationPolicy entity) {
        systemCancellationPolicyRepository.save(entity);
        return entity;
    }

    /**
     * 修改隐私政策
     *
     * @author yangzhen
     */
    @Transactional
    public SystemCancellationPolicy modify(SystemCancellationPolicy entity) {
        systemCancellationPolicyRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除隐私政策
     *
     * @author yangzhen
     */
    @Transactional
    public void deleteById(String id) {
        systemCancellationPolicyRepository.deleteById(id);
    }

    /**
     * 批量删除隐私政策
     *
     * @author yangzhen
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        systemCancellationPolicyRepository.deleteByIdList(ids);
    }

    /**
     * 单个查询隐私政策
     *
     * @author yangzhen
     */
    public SystemCancellationPolicy getById(String id) {
        return systemCancellationPolicyRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询隐私政策
     *
     * @author yangzhen
     */
    public Page<SystemCancellationPolicy> page(SystemCancellationPolicyQueryRequest queryReq) {
        return systemCancellationPolicyRepository.findAll(
                SystemCancellationPolicyWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询隐私政策
     *
     * @author yangzhen
     */
    public List<SystemCancellationPolicy> list(SystemCancellationPolicyQueryRequest queryReq) {
        return systemCancellationPolicyRepository.findAll(
                SystemCancellationPolicyWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 将实体包装成VO
     *
     * @author yangzhen
     */
    public SystemCancellationPolicyVO wrapperVo(SystemCancellationPolicy systemCancellationPolicy) {
        if (systemCancellationPolicy != null) {
            SystemCancellationPolicyVO systemCancellationPolicyVO = new SystemCancellationPolicyVO();
            KsBeanUtil.copyPropertiesThird(systemCancellationPolicy, systemCancellationPolicyVO);
            return systemCancellationPolicyVO;
        }
        return null;
    }
}
