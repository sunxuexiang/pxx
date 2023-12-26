package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.common.util.tencent.TencentImCustomerUtil;
import com.wanmi.sbc.es.elastic.EsImMessage;
import com.wanmi.sbc.es.elastic.EsImMessageElasticService;
import com.wanmi.sbc.job.model.entity.ImFileVO;
import com.wanmi.sbc.job.model.entity.ImHistoryVO;
import com.wanmi.sbc.message.bean.vo.ImHistoryMsgVO;
import com.wanmi.sbc.message.bean.vo.MsgBody;
import com.wanmi.sbc.message.bean.vo.MsgList;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>腾讯IM历史聊天消息下载任务，每天下载前一天的聊天消息</p>
 * @Author zhouzhenguo
 * @createDate 2023-07-29 15:54
 * @Description: TODO
 * @Version 1.0
 */
@Component
@Slf4j
@JobHandler(value="imHistoryMessageDownloadJobHandler")
public class ImHistoryMessageDownloadJobHandler extends IJobHandler {

    @Autowired
    private SystemConfigQueryProvider systemConfigQueryProvider;

    @Resource
    private EsImMessageElasticService esImMessageElasticService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        log.info("imHistoryMessageDownloadJobHandler 开始下载腾讯IM昨天聊天消息.");
        SystemConfigResponse response =
                systemConfigQueryProvider.list(SystemConfigQueryRequest.builder().configKey(ConfigKey.ONLINESERVICE.toValue())
                        .configType(ConfigType.TX_IM_ONLIEN_SERVICE.toValue()).delFlag(DeleteFlag.NO).build()).getContext();
        if (response == null || ObjectUtils.isEmpty(response.getSystemConfigVOList())) {
            log.info("系统没有配置腾讯IM appId，退出历史消息下载任务");
            return ReturnT.SUCCESS;
        }
        JSONObject jsonObject = JSONObject.parseObject( response.getSystemConfigVOList().get(0).getContext());
        long appId= Long.valueOf(jsonObject.getString("appId"));
        String appKey= jsonObject.getString("appKey");
        if (StringUtils.isEmpty(appKey)) {
            log.info("系统没有配置腾讯IM appId，退出历史消息下载任务");
            return ReturnT.SUCCESS;
        }
        int hour = 0;
        int tryCount = 0;
        while (hour < Constants.IM_MAX_DAY_HOUR) {
            // 睡眠0.1秒，限制请求腾讯IM接口频率
            try {
                Thread.sleep(100);
            }
            catch (Exception e){}
            
            log.info("imHistoryMessageDownloadJobHandler 拉去 {} 小时历史消息", hour);
            String history = TencentImCustomerUtil.getHistory(DateUtil.nowTimeString()+String.format("%02d", hour), appId, appKey);
            ImHistoryVO imHistoryVO = JSONObject.parseObject(history, ImHistoryVO.class);
            // 拉去历史消息调用腾讯接口返回错误，进行重试5次
            if (imHistoryVO.getErrorCode() != Constants.no){
                if (tryCount < 5) {
                    tryCount++;
                    continue;
                }
                // 如果重试5次还报错，就拉取下一个小时的聊天消息
                else {
                    tryCount = 0;
                    hour ++;
                    continue;
                }
            }
            List<ImFileVO> imFileList = imHistoryVO.getFile();
            if (CollectionUtils.isNotEmpty(imFileList)){
                imFileList.forEach(imFile->{
                    List<EsImMessage> messageList = parseMessageFile(imHistoryVO, imFile);
                    esImMessageElasticService.writeMessageList(messageList);
                });
            }
            tryCount = 0;
            hour ++;
        }
        return SUCCESS;
    }

    /**
     * 下载腾讯IM聊天文件，以及解析文件中聊天内容，写入到ElasticSearch
     * @param imHistoryVO
     * @param imFile
     */
    private List<EsImMessage> parseMessageFile(ImHistoryVO imHistoryVO, ImFileVO imFile) {
        List<EsImMessage> resultList = new ArrayList<>();
        try {
            List<String> stringList = TarUtil.sendGetToGzip(imFile.getURL());
            StringBuffer builder =new StringBuffer();
            stringList.forEach(l->{
                builder.append(l);
            });
            ImHistoryMsgVO imHistoryMsgVO = JSONObject.parseObject(builder.toString(), ImHistoryMsgVO.class);
            List<MsgList> msgList = imHistoryMsgVO.getMsgList();
            msgList.forEach(msg->{
                List<MsgBody> msgBody = msg.getMsgBody();
                msgBody.forEach(ms->{
                    EsImMessage message = new EsImMessage();
                    message.setMsgType(ms.getMsgType());
                    message.setToAccount(msg.getTo_Account());
                    message.setFromAccount(msg.getFrom_Account());
                    message.setMsgTimestamp(Long.parseLong(msg.getMsgTimestamp()));
                    message.setMsgContent(ms.getMsgContent().getText());
                    resultList.add(message);
                });
            });
        } catch (MalformedURLException e) {
            log.error("imHistoryMessageDownloadJobHandler请求异常：" + JSON.toJSONString(imHistoryVO), e);
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            log.error("imHistoryMessageDownloadJobHandler转换异常：" + JSON.toJSONString(imHistoryVO), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("imHistoryMessageDownloadJobHandler读取异常：" + JSON.toJSONString(imHistoryVO), e);
            throw new RuntimeException(e);
        }
        return resultList;
    }
}
