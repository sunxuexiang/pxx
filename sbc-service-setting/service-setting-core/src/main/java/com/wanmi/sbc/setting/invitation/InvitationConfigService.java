package com.wanmi.sbc.setting.invitation;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigRequest;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigResponse;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.ConfigUpdateRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.LogisticsSaveRopRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.LogisticsRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ConfigVO;
import com.wanmi.sbc.setting.config.Config;
import com.wanmi.sbc.setting.config.ConfigRepository;
import com.wanmi.sbc.setting.util.SpecificationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by feitingting on 2019/11/6.
 */

@Service
/**
 * 邀新配置类
 */
public class InvitationConfigService {

    @Autowired
    private InvitationConfigRepository invitationConfigRepository;


    public InvitationConfigResponse getDetail(){
        List<InvitationConfig> list =invitationConfigRepository.findAll();
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        InvitationConfigResponse response = new InvitationConfigResponse();
        KsBeanUtil.copyPropertiesThird(list.get(0), response);
        return response;
    }
    @Transactional(rollbackFor = Exception.class)
    public void save(InvitationConfigRequest request){
        InvitationConfig config;
        List<InvitationConfig> list =invitationConfigRepository.findAll();
        if(CollectionUtils.isEmpty(list)){
            config = new InvitationConfig();
        }
        config =list.get(0);

        if(Objects.nonNull(request.getNewCustomersBuyLimit())){
            config.setNewCustomersBuyLimit(request.getNewCustomersBuyLimit());
        }

        if(Objects.nonNull(request.getOldCustomersBuyLimit())){
            config.setOldCustomersBuyLimit(request.getOldCustomersBuyLimit());
        }
        if(Objects.nonNull(request.getInvitationRules())){
            config.setInvitationRules(request.getInvitationRules());
        }
        invitationConfigRepository.saveAndFlush(config);
    }


}
