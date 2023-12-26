package com.wanmi.sbc.init.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.redis.RedisHsetBean;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaQueryProvider;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaListRequest;
import com.wanmi.sbc.saas.api.response.domainstorerela.DomainStoreRelaListResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author: songhanlin
 * @Date: Created In 14:02 2020/3/2
 * @Description: SaaS Service
 */
@Slf4j
@Service
public class SaasService {

    @Autowired
    private RedisService redisService;

    @Resource
    private AuditQueryProvider auditQueryProvider;

    @Resource
    private DomainStoreRelaQueryProvider domainStoreRelaQueryProvider;

    /**
     * saas服务初始化
     */
    public void saasInit() {
        this.deleteSaasRedis();
        ConfigQueryRequest request = new ConfigQueryRequest();
        request.setConfigKey(ConfigKey.S2BAUDIT.toString());

        List<ConfigVO> list = auditQueryProvider.getByConfigKey(request).getContext().getConfigVOList();
        String status = VASStatus.DISABLE.toString();
        if (CollectionUtils.isNotEmpty(list)) {
            Optional<ConfigVO> configOptional = list.stream().filter(s -> StringUtils.equals(s.getConfigType(),
                    ConfigType.SAASSETTING.toValue())).findFirst();

            Optional<ConfigVO> domainOptional = list.stream().filter(s -> StringUtils.equals(s.getConfigType(),
                    ConfigType.SAAS_DOMAIN.toValue())).findFirst();

            if (configOptional.isPresent() && domainOptional.isPresent()
                    && StringUtils.isNotBlank(domainOptional.get().getContext())) {
                ConfigVO configVO = configOptional.get();

                if (configVO.getStatus() == 0) {
                    log.info("Saas化系统关闭");
                } else {
                    status = VASStatus.ENABLE.toValue();
                    ConfigVO domain = domainOptional.get();
                    redisService.setString(CacheKeyConstant.SAAS_DOMAIN, domain.getContext());
                    cacheForRedis(domain.getContext());
                    log.info("Saas化系统启动");
                }
            } else {
                log.info("非Saas化系统");
            }
        } else {
            log.info("非Saas化系统");
        }
        redisService.setString(CacheKeyConstant.SAAS_SETTING, status);
    }

    /**
     * 缓存域名映射
     */
    private void cacheDomain(DomainStoreRelaListResponse domainStoreRelaListResponse, String mainHost) {
        if (Objects.nonNull(domainStoreRelaListResponse)) {
            List<DomainStoreRelaVO> list = domainStoreRelaListResponse.getDomainStoreRelaVOList();
            if (CollectionUtils.isNotEmpty(list)) {
                List<RedisHsetBean> redisHsetBeans = new ArrayList<>();
                list.forEach(domain -> {
                    if (StringUtils.isNotBlank(domain.getH5Domain())) {
                        RedisHsetBean h5Domain = new RedisHsetBean();
                        h5Domain.setField((domain.getH5Domain() + mainHost).toLowerCase());
                        h5Domain.setValue(JSON.toJSONString(domain));
                        redisHsetBeans.add(h5Domain);
                    }
                    if (StringUtils.isNotBlank(domain.getPcDomain())) {
                        RedisHsetBean pcDomain = new RedisHsetBean();
                        pcDomain.setValue(JSON.toJSONString(domain));
                        pcDomain.setField((domain.getPcDomain() + mainHost).toLowerCase());
                        redisHsetBeans.add(pcDomain);
                    }
                });
                if (CollectionUtils.isNotEmpty(redisHsetBeans)) {
                    redisService.hsetPipeline(CacheKeyConstant.CACHE_KEY, redisHsetBeans);
                }
            }
        }
    }

    /**
     * 缓存域名映射提供给nginx
     */
    private void cacheDomainForNginx(DomainStoreRelaListResponse domainStoreRelaListResponse, String mainHost) {
        if (Objects.nonNull(domainStoreRelaListResponse)) {
            List<DomainStoreRelaVO> list = domainStoreRelaListResponse.getDomainStoreRelaVOList();
            if (CollectionUtils.isNotEmpty(list)) {
                redisService.setString(CacheKeyConstant.CACHE_PC_KEY_FOR_NGINX,
                        list.stream().filter(d -> StringUtils.isNotBlank(d.getPcDomain()))
                                .map(d -> (d.getPcDomain() + mainHost).toLowerCase()).distinct().collect(Collectors.joining(" ")));
                redisService.setString(CacheKeyConstant.CACHE_MOBILE_KEY_FOR_NGINX,
                        list.stream().filter(d -> StringUtils.isNotBlank(d.getH5Domain()))
                                .map(d -> (d.getH5Domain() + mainHost).toLowerCase()).distinct().collect(Collectors.joining(" ")));
            }
        }
    }

    /**
     * 缓存信息到redis
     */
    private void cacheForRedis(String mainHost) {
        DomainStoreRelaListResponse domainStoreRelaListResponse =
                domainStoreRelaQueryProvider.list(DomainStoreRelaListRequest.builder().delFlag(DeleteFlag.NO).build()).getContext();
        cacheDomain(domainStoreRelaListResponse, mainHost);
        cacheDomainForNginx(domainStoreRelaListResponse, mainHost);
    }

    /**
     * 删除saas缓存在redis中的信息
     */
    private void deleteSaasRedis() {
        redisService.delete(CacheKeyConstant.SAAS_DOMAIN);
        redisService.delete(CacheKeyConstant.SAAS_SETTING);
        redisService.delete(CacheKeyConstant.CACHE_KEY);
        redisService.delete(CacheKeyConstant.CACHE_PC_KEY_FOR_NGINX);
        redisService.delete(CacheKeyConstant.CACHE_MOBILE_KEY_FOR_NGINX);
    }
}
