package com.wanmi.sbc.invitation;

import com.alibaba.fastjson.JSONObject;
import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.detail.CustomerDetailGetCustomerIdResponse;
import com.wanmi.sbc.setting.api.provider.baseconfig.BaseConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.businessconfig.BusinessConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigProvider;
import com.wanmi.sbc.setting.api.provider.invitation.InvitationConfigResponse;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.ConfigQueryRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.baseconfig.BaseConfigRopResponse;
import com.wanmi.sbc.setting.api.response.businessconfig.BusinessConfigRopResponse;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.system.request.OnlineServiceUrlRequest;
import com.wanmi.sbc.system.response.SobotResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Objects;

/**
 * 邀新设置服务
 * Created by CHENLI on 2017/5/12.
 */
@Api(tags = "InvitationController", description = "邀新设置 API")
@RestController
@RequestMapping("/invitation")
public class InvitationController {

    @Autowired
    private InvitationConfigProvider invitationConfigProvider;

    /**
     * 查询邀新设置
     */
    @ApiOperation(value = "查询邀新设置")
    @RequestMapping(value = "/getConfig", method = RequestMethod.GET)
    public BaseResponse<InvitationConfigResponse> getConfig() {
        return invitationConfigProvider.detail();
    }

}