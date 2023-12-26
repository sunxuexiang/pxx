package com.wanmi.sbc.live.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencentyun.TLSSigAPIv2;
import com.wanmi.sbc.live.chat.constant.TencentImApiConstant;
import com.wanmi.sbc.live.chat.response.ChatReturnResponse;
import com.wanmi.sbc.live.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Component
public class TencentImUtil {
    private static final String HTTPS_URL_PREFIX = "https://console.tim.qq.com/";
    public static final String APP_MANAGER = "administrator";// 请求腾讯im的地址中usersig签名生成必须是管理员userId
    public static final Integer SIGN_EXPIRED = 86400;

    // 设置Redis缓存IM签名时间必须低于腾讯签名有效时间
    public static final Integer SIGN_CACHE_TIME = 76400;

    @Value("${im.appid}")
    private Long appIdConfig;

    @Value("${im.secret}")
    private String appSecret;

    @Value("${live.push.key}")
    private String pushKey;

    private static long appid=1400787555;
    private static String key="030a04a76482db2eabe26f4b9722501e3a069fcc7b2029ad90ab4a6059fc2d15";

    @PostConstruct
    private void initImConfig () {
        log.info("腾讯IM初始化，appId {} appSecret {} pushKey {}", appIdConfig, appSecret, pushKey);
        if (appIdConfig != null) {
            appid = appIdConfig;
        }
        if (StringUtils.isNotEmpty(appSecret)) {
            key = appSecret;
        }
    }

    /**
     * 前端获取签名（接口返回签名）
     * @param username
     * @return
     */
    public static String getTxCloudUserSig(String username) {
        long entryTime = System.currentTimeMillis();
        TLSSigAPIv2 tlsSigApi = new TLSSigAPIv2(appid, key);
        String userSig = tlsSigApi.genUserSig(username, SIGN_EXPIRED);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯IM直播签名耗时 {} - {}", username, (exitTime - entryTime) / 1000d);
        return userSig;
    }

    /**
     * 请求腾讯云地址usersig签名生成
     */
    public static String createUsersig() {
        long entryTime = System.currentTimeMillis();
        TLSSigAPIv2 tlsSigApi = new TLSSigAPIv2(appid, key);
        String  userSig = tlsSigApi.genUserSig(APP_MANAGER, SIGN_EXPIRED);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯IM直播签名耗时 {} - {}", APP_MANAGER, (exitTime - entryTime) / 1000d);
        return userSig;
    }

    /**
     * 获取腾讯im请求路径
     */
    private static String getHttpsUrl(String imServiceApi,String  userSig, Integer random) {
        return String.format("%s%s?sdkappid=%s&identifier=%s&usersig=%s&random=%s&contenttype=json",
                HTTPS_URL_PREFIX, imServiceApi, appid, APP_MANAGER, userSig, random);
    }

    /**
     * 导入单个账号
     * @param userId 用户id
     */
    public static void accountImport(String userId,String userSig) {
        accountImport(userId, null);
    }

    /**
     * 添加账号
     * @param userId
     * @param userName
     */
    public static void accountImport(String userId, String userName,String userSig) {
        accountImport(userId, userName, null);
    }

    /**
     * 添加账号
     * @param userId
     * @param userName
     * @param faceUrl
     */
    public static void accountImport(String userId, String userName, String faceUrl,String userSig) {
        long entryTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.ACCOUNT_IMPORT,userSig, random);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Identifier", userId);
        if (StringUtils.isNotEmpty(userName)) {
            jsonObject.put("Nick", userName);
        }
        if (StringUtils.isNotEmpty(faceUrl)) {
            jsonObject.put("FaceUrl", faceUrl);
        }
        log.info("腾讯云im导入单个账号，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯云im导入单个账号，返回结果：{}", result, (exitTime - entryTime) / 1000d);
    }


    /**
     * 创建群组
     * @param groupName
     * @return
     */
    public static String createGroup(String groupName,String userSig){
        long entryTime = System.currentTimeMillis();
        String groupId="";
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.CREATE_GROUP,userSig, random);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name",groupName);
        jsonObject.put("Type","AVChatRoom");
        log.info("腾讯云创建群组，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯云创建群组，返回结果：{}", result, (exitTime - entryTime) / 1000d);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if(Objects.nonNull(resultJson.get("GroupId"))){
                groupId= resultJson.get("GroupId").toString();
            }
        }
        return groupId;
    }

    /**
     * 解散群组
     * @param groupId
     * @param userSig
     * @return
     */
    public static void destroyGroup(String groupId,String userSig){
        long entryTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.DESTROY_GROUP,userSig, random);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        log.info("腾讯云解散群组，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯云解散群组，返回结果：{}", result, (exitTime - entryTime) / 1000d);
    }

    /**
     * 向群组发送系统消息
     * @param GroupId
     * @param content
     */
    public static void sendGroupSystemNotification(String GroupId,String content,String userSig){
        long entryTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.SEND_GROUP_SYSTEM_NOTIFICATION,userSig, random);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId", GroupId);
        jsonObject.put("Content", content);
        log.info("腾讯云im群组发送系统消息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯云im群组发送系统消息，返回结果：{}, 耗时 {}", result, (exitTime - entryTime) / 1000d);
    }

    public static int getOnlineMemberNum(String GroupId,String userSig){
        long entryTime = System.currentTimeMillis();
        int onlineMemberNum=0;
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.GET_ONLINE_MEMBER_NUM,userSig, random);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId", GroupId);
        log.info("腾讯云im群组获取在线人数，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        long exitTime = System.currentTimeMillis();
        log.info("腾讯云im群组获取在线人数，返回结果：{} 耗时 {}", result, (exitTime - entryTime) / 1000d);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if(Objects.nonNull(resultJson.get("OnlineMemberNum"))){
                onlineMemberNum= Integer.parseInt(resultJson.get("OnlineMemberNum").toString());
            }
        }
        return onlineMemberNum;
    }
}
