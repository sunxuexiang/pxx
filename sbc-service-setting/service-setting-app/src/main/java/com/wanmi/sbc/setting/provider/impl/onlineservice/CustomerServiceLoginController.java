package com.wanmi.sbc.setting.provider.impl.onlineservice;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceLoginProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.ImOnlineServiceSignRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.bean.vo.ImOnlineServiceItemVO;
import com.wanmi.sbc.setting.bean.vo.SystemConfigVO;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceLoginRecord;
import com.wanmi.sbc.setting.imonlineservice.service.CustomerServiceLoginRecordService;
import com.wanmi.sbc.setting.imonlineserviceitem.root.ImOnlineServiceItem;
import com.wanmi.sbc.setting.imonlineserviceitem.service.ImOnlineServiceItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>客服登录信息OpenFeign</p>
 * @author zhouzhenguo
 * @date 2023-09-7 16:10:28
 */
@RestController
@Validated
@Slf4j
public class CustomerServiceLoginController implements CustomerServiceLoginProvider {

    @Autowired
    private CustomerServiceLoginRecordService customerServiceLoginRecordService;

    @Autowired
    private ImOnlineServiceItemService imOnlineServiceItemService;

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Override
    public BaseResponse loginSuccess(ImOnlineServiceSignRequest request) {
        CustomerServiceLoginRecord loginRecord = customerServiceLoginRecordService.loginSuccess(request);
        if (loginRecord == null) {
            return BaseResponse.FAILED();
        }
        return BaseResponse.success(loginRecord);
    }

    @Override
    public BaseResponse getStatus(ImOnlineServiceSignRequest request) {
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCustomerServiceAccount(request.getCustomerServiceAccount());
        if (imOnlineServiceItem == null) {
            return BaseResponse.success(new ImOnlineServiceItemVO());
        }
        return BaseResponse.success(KsBeanUtil.convert(imOnlineServiceItem, ImOnlineServiceItemVO.class));
    }

    @Override
    public BaseResponse getImAccountLoginState(ImOnlineServiceSignRequest request) {
        ImOnlineServiceItem imOnlineServiceItem = imOnlineServiceItemService.getByCompanyInfoIdAndPhoneNo(request.getCompanyId(), request.getPhoneNo());
        if (imOnlineServiceItem == null) {
            return BaseResponse.success(2);
        }
        SystemConfigResponse systemConfigResponse =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        SystemConfigVO systemConfigVO= systemConfigResponse.getSystemConfigVOList().get(Constants.no);
        JSONObject jsonObject = JSONObject.parseObject(systemConfigVO.getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        List<String> accountList = Arrays.asList(imOnlineServiceItem.getCustomerServiceAccount());
        List<String> onlineList = TencentImCustomerUtil.getOnlineAccount(accountList, appId, appKey);
        if (ObjectUtils.isEmpty(onlineList)) {
            // 腾讯IM没有登录，将客服状态设置为离线
            imOnlineServiceItem.setServiceStatus(1);
            imOnlineServiceItemService.save(Arrays.asList(imOnlineServiceItem));
            return BaseResponse.success(0);
        }
        return BaseResponse.success(1);
    }

}
