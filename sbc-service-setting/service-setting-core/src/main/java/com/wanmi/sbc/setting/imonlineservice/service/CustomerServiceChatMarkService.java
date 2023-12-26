package com.wanmi.sbc.setting.imonlineservice.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceChatMarkRequest;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatMarkRepository;
import com.wanmi.sbc.setting.imonlineservice.repository.CustomerServiceChatRepository;
import com.wanmi.sbc.setting.imonlineservice.root.CustomerServiceChatMark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceChatMarkService {

    @Autowired
    private CustomerServiceChatMarkRepository customerServiceChatMarkRepository;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;


    public void add(CustomerServiceChatMarkRequest request) {
        int today = getToday();

        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        List<Integer> setMarkList = new ArrayList<>();
        setMarkList.add(0);
        TencentImCustomerUtil.markContact(request.getImGroupId(), request.getServerAccount(), setMarkList, appKey, appId);

        List<CustomerServiceChatMark> customerServiceChatMarkList = customerServiceChatMarkRepository.findByMarkDateAndServerAccountAndImGroupId(today, request.getServerAccount(), request.getImGroupId());
        if (ObjectUtils.isEmpty(customerServiceChatMarkList)) {
            CustomerServiceChatMark customerServiceChatMark = KsBeanUtil.convert(request, CustomerServiceChatMark.class);
            customerServiceChatMark.setMarkDate(today);
            customerServiceChatMarkRepository.save(customerServiceChatMark);
        }
    }

    public void delete(CustomerServiceChatMarkRequest request) {
        int today = getToday();

        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        List<Integer> setMarkList = new ArrayList<>();
        TencentImCustomerUtil.markContact(request.getImGroupId(), request.getServerAccount(), setMarkList, appKey, appId);

        customerServiceChatMarkRepository.deleteByMarkDateAndServerAccountAndImGroupId(today, request.getServerAccount(), request.getImGroupId());
    }

    private int getToday () {
        String day = simpleDateFormat.format(new Date());
        return Integer.parseInt(day);
    }

    public List<CustomerServiceChatMark> getTodayListByServerAccount(String fromAccount) {
        if (StringUtils.isEmpty(fromAccount)) {
            return new ArrayList<>();
        }
        int today = getToday();
        return customerServiceChatMarkRepository.findByMarkDateAndServerAccount(today, fromAccount);
    }

    public CustomerServiceChatMark getTodayByServerAccountAndImGroupId(String serverAccount, String imGroupId) {
        int today = getToday();
        List<CustomerServiceChatMark> markList = customerServiceChatMarkRepository.findByMarkDateAndServerAccountAndImGroupId(today, serverAccount, imGroupId);
        if (ObjectUtils.isEmpty(markList)) {
            return null;
        }
        return markList.get(0);
    }
}
