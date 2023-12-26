package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.job.model.entity.ImFileVO;
import com.wanmi.sbc.job.model.entity.ImHistoryVO;
import com.wanmi.sbc.message.api.provider.imhistory.ImHistorySaveProvider;
import com.wanmi.sbc.setting.api.provider.systemconfig.SystemConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.systemconfig.SystemConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.systemconfig.SystemConfigResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.TarUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;

/**
 * <p>im历史数据</p>
 * @Author shiGuangYi
 * @createDate 2023-06-29 15:54
 * @Description: TODO
 * @Version 1.0
 */
    @Component
    @Slf4j
    @JobHandler(value="ImHisToryJobHandler")
    public class ImHisToryJobHandler extends IJobHandler {
    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;
    @Autowired
    private ImHistorySaveProvider imHistorySaveProvider;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        JSONObject jsonObject = JSONObject.parseObject(   response.getSystemConfigVOList().get(0).getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        int count= Constants.yes;
        while (count<=Constants.IM_MAX_DAY_HOUR){
            String history = TencentImCustomerUtil.getHistory(DateUtil.nowTimeString()+String.format("%02d", count), appId, appKey);
            ImHistoryVO javaObject = JSONObject.parseObject(history, ImHistoryVO.class);
            if (javaObject.getErrorCode()==Constants.no){
                List<ImFileVO> file = javaObject.getFile();
                if (CollectionUtils.isNotEmpty(file)){
                    file.forEach(l->{
                        try {
                            List<String> stringList = TarUtil.sendGetToGzip(l.getURL());
                          imHistorySaveProvider.add(stringList);
                        } catch (MalformedURLException e) {
                            log.error("请求异常："+javaObject);
                            throw new RuntimeException(e);
                        } catch (UnsupportedEncodingException e) {
                            log.error("转换异常："+javaObject);
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            log.error("读取异常："+javaObject);
                            throw new RuntimeException(e);
                        }

                    });
                }
            }
            count++;
        }
        return SUCCESS;
    }
}
