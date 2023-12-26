package com.wanmi.sbc.common.util.tencent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencentyun.TLSSigAPIv2;
import com.wanmi.sbc.common.base.*;
import com.wanmi.sbc.common.constant.TencentImApiConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.ObjectUtils;
import com.wanmi.sbc.common.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

@Slf4j
@Component
public class TencentImCustomerUtil {
    private static final String HTTPS_URL_PREFIX = "https://console.tim.qq.com/";
    /**
     * // 请求腾讯im的地址中usersig签名生成必须是管理员userId
     * */
    public static final String APP_MANAGER = "administrator";
    public static final Integer SIGN_EXPIRED = 86400*2;

    //@Value("${im.appid}")
//    private static long appid=1400717902;
//   // @Value("${im.secret}")
//    private static String key="029aaafc8ea1b828a84dc20f342eaef72554ab49b998fb17320db93bd787f9ea";

    /**
     * 前端获取签名（接口返回签名）
     * @param username
     * @return
     */
    public static String getTxCloudUserSig(String username,long appid,String key) {
        TLSSigAPIv2 tlsSigApi = new TLSSigAPIv2(appid, key);
        String userSig = tlsSigApi.genUserSig(username, SIGN_EXPIRED);
        return userSig;
    }

    /**
     * 请求腾讯云地址usersig签名生成
     */
    public static String createUsersig(long appid,String key) {
        TLSSigAPIv2 tlsSigApi = new TLSSigAPIv2(appid, key);
        String  userSig = tlsSigApi.genUserSig(APP_MANAGER, SIGN_EXPIRED);
        return userSig;
    }

    /**
     * 获取腾讯im请求路径
     */
    private static String getHttpsUrl(String imServiceApi,String  userSig, Integer random,long appid) {
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
    public static void accountImport(String userId, String userName, String faceUrl,String userSig,long appid,String appKey) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.ACCOUNT_IMPORT,txCloudUserSig, random,appid);
        log.info("腾讯云im导入单个账号，请求地址：{}", httpsUrl);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UserID", userId);
        if (StringUtils.isNotEmpty(userName)) {
            jsonObject.put("Nick", userName);
        }
        if (StringUtils.isNotEmpty(faceUrl)) {
            jsonObject.put("FaceUrl", faceUrl);
        }
        boolean b = accountCheck(userId, userSig, appid);
        if (b){
            log.info("腾讯云im导入单个账号，请求参数：{}", jsonObject);
            String result = HttpUtil.doPost2(httpsUrl, jsonObject);
            log.info("腾讯云im导入单个账号，返回结果：{}", result);
        }else{
            //如果设置了更新用户资料
            portraitSet(userName,faceUrl,userId,userSig,appid);
        }
        stopWatch.stop();
        log.info("腾讯云im导入单个账号耗时 {}", stopWatch.getTotalTimeSeconds());

    }

    public static String createGroup(String groupName, String groupImg, String customerImAccount, String groupType, String appKey,long appid){
        return createGroup(null, groupName, groupImg, customerImAccount, groupType, appKey, appid);
    }

    /**
     * 创建群组
     * @param groupName
     * @return
     */
    public static String createGroup(String groupId, String groupName, String groupImg, String customerImAccount, String groupType, String appKey,long appid){
        long startTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.CREATE_GROUP,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        if (!StringUtils.isEmpty(groupId)) {
            if (groupId.startsWith("@TGS#")) {
                groupId = groupId.substring(5);
            }
            jsonObject.put("GroupId",groupId);
        }
        if (StringUtils.isEmpty(groupName)) {
            groupName = "群聊";
        }
        jsonObject.put("Name",groupName);
        jsonObject.put("Type",groupType);
        jsonObject.put("FaceUrl",groupImg);
        if (!org.springframework.util.StringUtils.isEmpty(customerImAccount)) {
            jsonObject.put("Owner_Account", customerImAccount);
        }
        JSONArray customArray = new JSONArray();
        JSONObject customerField = new JSONObject();
        customerField.put("Key", "account");
        customerField.put("Value", customerImAccount);
        customArray.add(customerField);
        jsonObject.put("AppDefinedData", customArray);

        log.info("腾讯云创建群组，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云创建群组，返回结果：{}", result, (System.currentTimeMillis() - startTime) / 1000d);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if (resultJson.containsKey("GroupId") && !org.springframework.util.StringUtils.isEmpty(resultJson.getString("GroupId"))) {
                groupId= resultJson.getString("GroupId");
            }
            else {
                try {
                    Integer errorCode = resultJson.getInteger("ErrorCode");
                    if (errorCode != null && errorCode.equals(10004)) {
                        TencentImCustomerUtil.accountImport(customerImAccount, customerImAccount,
                                null, TencentImCustomerUtil.getTxCloudUserSig(customerImAccount, appid, appKey), appid, appKey);
                        return createGroup2(groupName, groupImg, customerImAccount, groupType, appKey, appid);
                    }
                }
                catch (Exception e) {
                    log.error("创建腾讯IM账号异常", e);
                }
            }
        }
        return groupId;
    }

    public static String createGroup2(String groupName, String groupImg, String customerImAccount, String groupType, String appKey,long appid){
        long startTime = System.currentTimeMillis();
        String groupId="";
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.CREATE_GROUP,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isEmpty(groupName)) {
            groupName = "群聊";
        }
        jsonObject.put("Name",groupName);
        jsonObject.put("Type",groupType);
        jsonObject.put("FaceUrl",groupImg);
        jsonObject.put("Owner_Account",customerImAccount);
        JSONArray customArray = new JSONArray();
        JSONObject customerField = new JSONObject();
        customerField.put("Key", "account");
        customerField.put("Value", customerImAccount);
        customArray.add(customerField);
        jsonObject.put("AppDefinedData", customArray);

        log.info("腾讯云创建群组，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云创建群组，返回结果：{}", result, (System.currentTimeMillis() - startTime) / 1000d);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if (resultJson.containsKey("GroupId")) {
                groupId= resultJson.getString("GroupId");
            }
        }
        return groupId;
    }

    /**
     * 创建群组
     * @param groupName
     * @return
     */
    public static String updateGroupInfo(String groupId, String groupName, String groupImg, String customerImAccount, String groupType, String appKey,long appid){
        boolean hasHead = false;
        try {
            JSONObject getJson = getGroupInfo(groupId, appKey, appid);
            if (getJson.containsKey("GroupInfo")) {
                JSONArray jsonArray = getJson.getJSONArray("GroupInfo");
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if (jsonObject.containsKey("FaceUrl") && !StringUtils.isEmpty(jsonObject.getString("FaceUrl"))) {
                    hasHead = true;
                    String faceUrl = jsonObject.getString("FaceUrl");
                    String name = jsonObject.getString("Name");
                    log.info("群头像 {}", faceUrl);
                    if (faceUrl.equals(groupImg) && name.equals(groupName)) {
                        return groupId;
                    }
                }
            }
        }
        catch (Exception e) {
            log.error("获取群信息异常", e);
        }

//        if (hasHead) {
//            changeGroupOwner(groupId, customerImAccount, appKey, appid);
//            return groupId;
//        }

        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.MODIFY_GROUP_BASE_INFO,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        jsonObject.put("Name",groupName);
        jsonObject.put("Type",groupType);
        jsonObject.put("FaceUrl", groupImg);
        if (!StringUtils.isEmpty(customerImAccount)) {
//            jsonObject.put("Owner_Account", customerImAccount);

            JSONArray customArray = new JSONArray();
            JSONObject customerField = new JSONObject();
            customerField.put("Key", "account");
            customerField.put("Value", customerImAccount);
            customArray.add(customerField);
            jsonObject.put("AppDefinedData", customArray);
        }
        long startTime = System.currentTimeMillis();
        log.info("腾讯云修改群组信息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云修改群组信息，返回结果：{} - 耗时 {}", result, (System.currentTimeMillis() - startTime) / 1000d);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if(Objects.nonNull(resultJson.get("GroupId"))){
                groupId= resultJson.get("GroupId").toString();
            }
        }

//        changeGroupOwner(groupId, customerImAccount, appKey, appid);
        return groupId;
    }

    public static void changeGroupOwner (String groupId, String customerImAccount, String appKey,long appid) {
        try {
            Integer random = RandomUtils.nextInt(0, 999999999);
            String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
            String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.MODIFY_GROUP_BASE_INFO, txCloudUserSig, random, appid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("GroupId", groupId);
            jsonObject.put("NewOwner_Account", customerImAccount);

            log.info("设置群主，请求参数：{}", jsonObject.toString());
            String result = HttpUtil.doPost2(httpsUrl, jsonObject);
            log.info("设置群主，返回结果：{}", result);
        }
        catch (Exception e) {
            log.error("设置群主异常", e);
        }
    }

    public static List<String> getGroupMemberList (String groupId, String appKey,long appid) {
        long startTime = System.currentTimeMillis();
        List<String> resultList = new ArrayList<>();
        try {
            Integer random = RandomUtils.nextInt(0, 999999999);
            String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
            String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.GET_GROUP_INFO, txCloudUserSig, random, appid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("GroupIdList", Arrays.asList(groupId));

            log.info("获取群详细信息，请求参数：{}", jsonObject.toString());
            String result = HttpUtil.doPost2(httpsUrl, jsonObject);
            log.info("获取群详细信息，返回结果：{} 耗时 {}", result, (System.currentTimeMillis() - startTime) / 1000d);

            JSONObject infoJson = JSON.parseObject(result);
            JSONArray groups = infoJson.getJSONArray("GroupInfo");
            JSONObject group = groups.getJSONObject(0);
            JSONArray memberArray = group.getJSONArray("MemberList");
            for (int i = 0; i < memberArray.size(); i++) {
                JSONObject member = memberArray.getJSONObject(i);
                String account = member.getString("Member_Account");
                if (!StringUtils.isEmpty(account)) {
                    resultList.add(account);
                }
            }
            log.info("群成员 {} - {}", groupId, resultList);
        }
        catch (Exception e) {
            log.info("获取群成员异常", e);
        }
        return resultList;
    }

    public static JSONObject getGroupInfo (String groupId, String appKey,long appid) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.GET_GROUP_INFO,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupIdList", Arrays.asList(groupId));

        log.info("获取群详细信息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("获取群详细信息，返回结果：{}", result);

        return JSONObject.parseObject(result);
    }

    /**
     * 添加群组成员
     * @param groupId
     * @return
     */
    public static String addGroupMember(String groupId, List<String> accountIds, String appKey,long appid){
        long startTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.ADD_GROUP_MEMBER,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        jsonObject.put("Silence",1); // 是否静默加人
        JSONArray memberListArray = new JSONArray();
        accountIds.forEach(userId -> {
            JSONObject itemJson = new JSONObject();
            itemJson.put("Member_Account", userId);
            memberListArray.add(itemJson);
        });
        jsonObject.put("MemberList",memberListArray);
        log.info("腾讯云添加群组成员，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云添加群组成员，返回结果：{} - 耗时 {}", result, (System.currentTimeMillis() - startTime) / 1000d);
        return groupId;
    }

    /**
     * 删除群组成员
     * @param groupId
     * @return
     */
    public static String deleteGroupMember(String groupId, List<String> accountIds, String appKey,long appid){
        long startTime = System.currentTimeMillis();
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.DELETE_GROUP_MEMBER,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        jsonObject.put("Silence",1); // 是否静默加人
        jsonObject.put("MemberToDel_Account",accountIds);
        log.info("腾讯云删除群组成员，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云删除群组成员，返回结果：{} - 耗时 {}", result, (System.currentTimeMillis() - startTime)/1000d);
        return groupId;
    }

    /**
     * 解散群组
     * @param groupId
     * @param userSig
     * @return
     */
    public static void destroyGroup(String groupId,String userSig,long appid){
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.DESTROY_GROUP,userSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        log.info("腾讯云解散群组，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云解散群组，返回结果：{}", result);
    }

    /**
     * 解散群组
     * @param groupId
     * @return
     */
    public static void sendGroupMsg(String groupId, String fromAccount, Long companyInfoId, Long storeId, String storeName, String message, String appKey,long appid,
                                    Integer settingType, String customerIMAccount){
        sendGroupMsg(groupId, fromAccount, companyInfoId, storeId, storeName, message, appKey, appid, settingType, null, customerIMAccount);
    }
    public static void sendGroupMsg(String groupId, String fromAccount, Long companyInfoId, Long storeId, String storeName, String message, String appKey,long appid,
                                    Integer settingType, String customerNick, String customerIMAccount){
        sendGroupMsg(groupId, fromAccount, companyInfoId, storeId, storeName, message, appKey, appid, settingType, customerNick, customerIMAccount, "system");
    }

    public static void sendGroupMsg(String groupId, String fromAccount, Long companyInfoId, Long storeId, String storeName, String message, String appKey,long appid,
                                    Integer settingType, String customerNick, String customerIMAccount, String msgType){
        sendGroupMsg(groupId, fromAccount, companyInfoId, storeId, storeName, message, appKey, appid, settingType, customerNick, customerIMAccount, msgType, null, null, null);
    }

    public static void sendGroupMsg(String groupId, String fromAccount, Long companyInfoId, Long storeId, String storeName, String message, String appKey,long appid,
                                    Integer settingType, String customerNick, String customerIMAccount, String msgType, String messageType){
        sendGroupMsg(groupId, fromAccount, companyInfoId, storeId, storeName, message, appKey, appid, settingType, customerNick, customerIMAccount, msgType, null, null, messageType);
    }

    public static void sendGroupMsg(String groupId, String fromAccount, Long companyInfoId, Long storeId, String storeName, String message, String appKey,long appid,
                                    Integer settingType, String customerNick, String customerIMAccount, String msgType,
                                    String switchSourceAccount, String switchSourceAccountNick, String messageType){
        long startTime = System.currentTimeMillis();
        if (storeId == null) {
            log.info("发送群聊消息店铺ID为空 {}", customerIMAccount);
        }
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.SEND_GROUP_MSG,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId",groupId);
        jsonObject.put("Random", RandomUtils.nextInt(0, 999999999));


        JSONObject customJson = new JSONObject();
        customJson.put("sendUser", TencentImCustomerUtil.APP_MANAGER);
        customJson.put("msgType", msgType);
        customJson.put("settingType", settingType);
        customJson.put("companyInoId", companyInfoId);
        customJson.put("storeId", storeId);
        customJson.put("storeName", storeName);
//        if (!StringUtils.isEmpty(customerIMAccount) && customerIMAccount.length() > 4) {
//            customJson.put("customerImAccount", customerIMAccount.substring(0, 3)+"****"+customerIMAccount.substring(customerIMAccount.length()-4));
//        }
        customJson.put("customerImAccount", customerIMAccount);
        if (!org.springframework.util.StringUtils.isEmpty(customerNick)) {
            int index = customerNick.indexOf("客服");
            if (index < 1) {
                customJson.put("customerNick", customerNick);
            }
            else {
                customJson.put("customerNick", storeName+customerNick.substring(index));
            }
        }
        if (!StringUtils.isEmpty(switchSourceAccount)) {
            customJson.put("switchSourceAccount", switchSourceAccount);
            customJson.put("switchSourceAccountNick", switchSourceAccountNick);
        }
        jsonObject.put("CloudCustomData", customJson.toString());


        if (!StringUtils.isEmpty(fromAccount)) {
            jsonObject.put("From_Account", fromAccount);
        }
        JSONArray msgBodyJson = new JSONArray();
        JSONObject msgItemJson = new JSONObject();
        if (StringUtils.isEmpty(messageType) || "TIMTextElem".equals(messageType)) {

            msgItemJson.put("MsgType", "TIMTextElem");
            JSONObject msgContentJson = new JSONObject();
            msgContentJson.put("Text", message);
            msgItemJson.put("MsgContent", msgContentJson);
        }
        // 发送图片
        else if ("TIMImageElem".equals(messageType)) {
            msgItemJson.put("MsgType", messageType);
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "");
            JSONObject msgContentJson = new JSONObject();
            msgContentJson.put("UUID", uuid);
            msgContentJson.put("ImageFormat", 1);
            JSONArray imageInfoArray = new JSONArray();
            for (int i=1; i<4; i++) {
                JSONObject imageJson = new JSONObject();
                imageJson.put("URL", message);
                imageJson.put("Type", i);
                imageInfoArray.add(imageJson);
            }
            msgContentJson.put("ImageInfoArray", imageInfoArray);
            msgItemJson.put("MsgContent", msgContentJson);
        }
        // 文件
        else if ("TIMFileElem".equals(messageType)) {
            msgItemJson.put("MsgType", messageType);
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "");
            JSONObject msgContentJson = new JSONObject();
            msgContentJson.put("UUID", uuid);
            msgContentJson.put("Url", message);
            msgContentJson.put("FileName", message);
            msgItemJson.put("MsgContent", msgContentJson);
        }
        // 视频
        else if ("TIMVideoFileElem".equals(messageType)) {
            msgItemJson.put("MsgType", messageType);
            String uuid = UUID.randomUUID().toString();
            uuid = uuid.replaceAll("-", "");
            JSONObject msgContentJson = new JSONObject();
            msgContentJson.put("VideoUUID", uuid);
            msgContentJson.put("ThumbUUID", uuid);
            msgContentJson.put("VideoUrl", message);
            msgContentJson.put("ThumbUrl", message);
            msgItemJson.put("MsgContent", msgContentJson);
        }

        msgBodyJson.add(msgItemJson);
        jsonObject.put("MsgBody", msgBodyJson);
        log.info("腾讯云发送群组消息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云发送群组消息，返回结果：{} 耗时 {}", result, (System.currentTimeMillis() - startTime) / 1000d);
        try {
            JSONObject responseJson = JSON.parseObject(result);
            String errorCode = responseJson.getString("ErrorCode");
            if ("10004".equals(errorCode)) {
                TencentImCustomerUtil.accountImport(fromAccount,customerNick,
                        null, TencentImCustomerUtil.getTxCloudUserSig(fromAccount,appid,appKey),appid,appKey);
            }
//            else if ("10010".equals(errorCode)) {
//                createGroup(groupId, storeName, null, customerIMAccount,"Private", appKey, appid);
//            }
        }
        catch (Exception e) {
            log.error("解析发送群聊消息异常", e);
        }
    }

    /**
     * 向群组发送系统消息
     * @param GroupId
     * @param content
     */
    public static void sendGroupSystemNotification(String GroupId,String content,String userSig,long appid){
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.SEND_GROUP_SYSTEM_NOTIFICATION,userSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId", GroupId);
        jsonObject.put("Content", content);
        log.info("腾讯云im群组发送系统消息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im群组发送系统消息，返回结果：{}", result);
    }

    public static int getOnlineMemberNum(String GroupId,String userSig,long appid){
        int onlineMemberNum=0;
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.GET_ONLINE_MEMBER_NUM,userSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GroupId", GroupId);
        log.info("腾讯云im群组获取在线人数，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im群组获取在线人数，返回结果：{}", result);
        if(Objects.nonNull(result)){
            JSONObject resultJson= JSON.parseObject(result);
            if(Objects.nonNull(resultJson.get("OnlineMemberNum"))){
                onlineMemberNum= Integer.parseInt(resultJson.get("OnlineMemberNum").toString());
            }
        }
        return onlineMemberNum;
    }



    /**
     * 获取会话信息
     * @param userId
     */
    public static String tencentImGetList(String userId,String userSig,long appid) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.RECENTCONTACT_GET_LIST,userSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("From_Account",userId);
        jsonObject.put("TimeStamp",0);
        jsonObject.put("StartIndex",0);
        jsonObject.put("TopTimeStamp",0);
        jsonObject.put("TopStartIndex",0);
        jsonObject.put("AssistFlags",0);
        log.info("腾讯云im获取会话信息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im获取会话信息，返回结果：{}", result);
        return result;
    }
    /**
     * 获取会话信息
     * @param userId
     */
    public static String tencentImGetUserInfo(String userId,String userSig,long appid) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.PROFILE_PORTRAIT_GE,userSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        List<String> list=new ArrayList<>();
        list.add("Tag_Profile_IM_Nick");
        list.add("Tag_Profile_IM_Image");
        list.add("Tag_Profile_IM_SelfSignature");
        jsonObject.put("From_Account",userId);
        jsonObject.put("TagList",list);
        log.info("腾讯云im获取会话信息，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im获取会话信息，返回结果：{}", result);
        return result;
    }
    /**
     * 删除会话
     * @param userId
     */
    public static String tencentImDelMsg(String userId,String type,String userSig,long appid,String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.PROFILE_PORTRAIT_GE,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("From_Account",userId);
        jsonObject.put("type",type);
        log.info("腾讯云im删除会话，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im删除会话，返回结果：{}", result);
        return result;
    }
    /**
     * 查询未读
     * @param userId
     */
    public static String unReadMsgNum(String userId, List<String> account, String userSig,long appid,String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.GET_C2C_UNREAD_MSG_NUM,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("To_Account",userId);
        jsonObject.put("Peer_Account",account);
        log.info("腾讯云im查询未读，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im查询未读，返回结果：{}", result);
        return result;
    }
    /**
     * 查询未读
     * @param userId
     */
    public static String merchantUnReadMsgNum(String userId,String userSig,long appid,String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.GET_C2C_UNREAD_MSG_NUM,txCloudUserSig, random,appid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("To_Account",userId);
        log.info("腾讯云im查询未读，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云im查询未读，返回结果：{}", result);
        return result;
    }


    /**
     * 拉取历史信息
     * @param time
     */
    public static String getHistory(String time,long appid,String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.OPEN_MSG_SVC_GET_HISTORY,txCloudUserSig, random,appid);
        log.info("腾讯云im拉取历史信息，请求地址：{}", httpsUrl);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ChatType", "C2C");
        jsonObject.put("MsgTime", time);
        log.info("拉取历史信息，请求参数：{}", jsonObject);
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("拉取历史信息，返回结果：{}", result);
        return result;
    }

    /**
     * 管理员给用户推送单聊消息并且开启离线推送
     * @return
     */
    public static String sendSingleChat (String toAccount, String msgContent, long appid, String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.OPEN_IM_SEND_MSG,txCloudUserSig, random,appid);
        log.info("腾讯管理员给用户发送单聊消息：{}", httpsUrl);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("messageType", TencentImMessageType.JingBiRecharge.getMsgType());
        bodyJson.put("SyncOtherMachine", 2);
        bodyJson.put("From_Account", APP_MANAGER);
        bodyJson.put("To_Account", toAccount);
        bodyJson.put("MsgRandom", random);

//        JSONArray msgBodyArray = new JSONArray();
//        JSONObject msgBody = new JSONObject();
//        msgBody.put("MsgType", "TIMTextElem");
//        JSONObject msgContextJson = new JSONObject();
//        msgContextJson.put("Text", "鲸币充值成功");
//        msgContextJson.put("messageType", TencentImMessageType.JingBiRecharge.getMsgType());
//        msgBody.put("MsgContent", msgContextJson);
//        msgBody.put("messageType", TencentImMessageType.JingBiRecharge.getMsgType());
//        msgBodyArray.add(msgBody);
//        bodyJson.put("MsgBody", msgBodyArray);

        JSONArray msgBodyArray = new JSONArray();
        JSONObject msgBody = new JSONObject();
        JSONObject msgContextJson = new JSONObject();
        JSONObject customMsgJson = new JSONObject();
        customMsgJson.put("text", "鲸币充值成功");
        customMsgJson.put("messageType", TencentImMessageType.JingBiRecharge.getMsgType());
        msgContextJson.put("Data", customMsgJson.toString());
        msgContextJson.put("Desc", "recharge");
        msgContextJson.put("Ext", "recharge");
        msgBody.put("MsgContent", msgContextJson);
        msgBodyArray.add(msgBody);

        msgBody.put("MsgType", "TIMCustomElem");
        bodyJson.put("MsgBody", msgBodyArray);

        JSONObject offlinePushJson = new JSONObject();
        offlinePushJson.put("PushFlag", 0);
        offlinePushJson.put("Desc", "鲸币充值成功");
        offlinePushJson.put("Ext", "鲸币充值成功");
        bodyJson.put("OfflinePushInfo", offlinePushJson);

        JSONArray forbidArray = new JSONArray();
        forbidArray.add("ForbidBeforeSendMsgCallback");
        forbidArray.add("ForbidAfterSendMsgCallback");
        bodyJson.put("ForbidCallbackControl", forbidArray);

        log.info("腾讯管理员给用户发送单聊消息，请求参数：{}", bodyJson);
        String result = HttpUtil.doPost2(httpsUrl, bodyJson);
        log.info("腾讯管理员给用户发送单聊消息，返回结果：{}", result);
        return result;
    }


    /**
     * 查询账户状态，判断用户是否需要更新
     * @param userId
     */
    public static boolean accountCheck(String userId,String userSig,long appid) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.ACCOUNT_CHECK,userSig, random,appid);
        List<ImAccountCheckVO> list=new ArrayList<>();
        ImAccountCheckVO bean=new ImAccountCheckVO();
        bean.setUserID(userId);
        list.add(bean);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("CheckItem",list);
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        AccountResultVO accountResultVO = JSONObject.parseObject(result, AccountResultVO.class);
        if (accountResultVO.getErrorCode()==0){
          if (accountResultVO.getResultItem().get(Constants.no).getResultCode()==0
                  &&accountResultVO.getResultItem().get(Constants.no).getAccountStatus().equals("Imported") ){
              //更新用户信息
              return false;
          }
        }
        return true;
    }

    /**
     * 更新用户资料
     * @param name
     */
    public static String portraitSet(String name,String logo,String userId,String userSig,long appid) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.PORTRAIT_SET,userSig, random,appid);

        ImPortraitSetVO imPortraitSetVO=new ImPortraitSetVO();
        List<ProfileItemDO> doList=new ArrayList<>();
        ProfileItemDO profileItemDO=new ProfileItemDO();
        //设置名称
        if(StringUtils.isNotEmpty(name)){
            profileItemDO.setTag("Tag_Profile_IM_Nick");
            profileItemDO.setValue(name);
            doList.add(profileItemDO);
        }
        //设置图像
        if(StringUtils.isNotEmpty(logo)){
            profileItemDO.setTag("Tag_Profile_IM_Image");
            profileItemDO.setValue(logo);
            doList.add(profileItemDO);
        }

        imPortraitSetVO.setFrom_Account(userId);
        imPortraitSetVO.setProfileItem(doList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("From_Account",userId);
        jsonObject.put("ProfileItem",doList);
        log.info("腾讯云更新用户资料，请求参数：{}", jsonObject.toString());
        String result = HttpUtil.doPost2(httpsUrl, jsonObject);
        log.info("腾讯云更新用户资料，返回结果：{}", result);
        return result;
    }

    /**
     * 查询在线IM账号列表
     * @param accountList
     * @param appId
     * @param appKey
     * @return
     */
    public static List<String> getOnlineAccount (List<String> accountList, Long appId, String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appId, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.ACCOUNT_QUERY_STATE,txCloudUserSig, random, appId);
        JSONObject bodyJson = new JSONObject();
        JSONArray accountArray = new JSONArray();
        accountList.stream().forEach(account -> {
            accountArray.add(account);
        });
        bodyJson.put("To_Account", accountArray);
        log.info("腾讯IM查询客户在线账号，请求参数：{}", bodyJson);
        String result = HttpUtil.doPost2(httpsUrl, bodyJson);
        log.info("腾讯IM查询客户在线账号，返回结果：{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONArray resultJsonArray = jsonObject.getJSONArray("QueryResult");
        List<String> resultList = new ArrayList<>();
        if (resultJsonArray == null || resultJsonArray.size() < 0) {
            return resultList;
        }
        for (int i=0; i<resultJsonArray.size(); i++) {
            JSONObject item = resultJsonArray.getJSONObject(i);
            String status = item.getString("Status");
            if (!"Online".equals(status)) {
                continue;
            }
            String account = item.getString("To_Account");
            resultList.add(account);
        }
        return resultList;
    }

    /**
     * 管理员给用户推送单聊消息并且开启离线推送
     * @return
     */
    public static String sendCustomMsg (String toAccount, String msgContent, TencentImMessageType tencentImMessageType, long appid, String appKey) {
        Integer random = RandomUtils.nextInt(0, 999999999);
        String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
        String httpsUrl = getHttpsUrl(TencentImApiConstant.AccountManage.OPEN_IM_SEND_MSG,txCloudUserSig, random,appid);
        log.info("腾讯管理员给用户发送自定义推送消息：{} - {} - {}", toAccount, tencentImMessageType.getMsgDesc(), msgContent);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("messageType", tencentImMessageType.getMsgType());
        bodyJson.put("SyncOtherMachine", 2);
        bodyJson.put("From_Account", APP_MANAGER);
        bodyJson.put("To_Account", toAccount);
        bodyJson.put("MsgRandom", random);

        JSONArray msgBodyArray = new JSONArray();
        JSONObject msgBody = new JSONObject();
        JSONObject msgContextJson = new JSONObject();
        JSONObject customMsgJson = new JSONObject();
        customMsgJson.put("text", msgContent);
        customMsgJson.put("messageType", tencentImMessageType.getMsgType());
        msgContextJson.put("Data", customMsgJson.toString());
        msgContextJson.put("Desc", "recharge");
        msgContextJson.put("Ext", "recharge");
        msgBody.put("MsgContent", msgContextJson);
        msgBodyArray.add(msgBody);

        msgBody.put("MsgType", "TIMCustomElem");
        bodyJson.put("MsgBody", msgBodyArray);

        JSONObject offlinePushJson = new JSONObject();
        offlinePushJson.put("PushFlag", 0);
        offlinePushJson.put("Desc", msgContent);
        offlinePushJson.put("Ext", msgContent);
        bodyJson.put("OfflinePushInfo", offlinePushJson);

        JSONArray forbidArray = new JSONArray();
        forbidArray.add("ForbidBeforeSendMsgCallback");
        forbidArray.add("ForbidAfterSendMsgCallback");
        bodyJson.put("ForbidCallbackControl", forbidArray);

        log.info("腾讯管理员给用户发送自定义推送消息，请求参数：{}", bodyJson);
        String result = HttpUtil.doPost2(httpsUrl, bodyJson);
        log.info("腾讯管理员给用户发送自定义推送消息，返回结果：{}", result);
        return result;
    }


    public static void markContact (String groupId, String fromAccount, List<Integer> setMarkList, String appKey, long appid) {
        try {
            Integer random = RandomUtils.nextInt(0, 999999999);
            String txCloudUserSig = getTxCloudUserSig(APP_MANAGER, appid, appKey);
            String httpsUrl = getHttpsUrl(TencentImApiConstant.GroupManage.MARK_CONTACT, txCloudUserSig, random, appid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("From_Account", fromAccount);
            JSONArray markArray = new JSONArray();
            JSONObject markJson = new JSONObject();
            markJson.put("OptType", 3);
            JSONObject contactItemJson = new JSONObject();
            contactItemJson.put("Type", 2);
            contactItemJson.put("ToGroupId", groupId);
            markJson.put("ContactItem", contactItemJson);
            markJson.put("SetMark", setMarkList);
            markArray.add(markJson);
            jsonObject.put("MarkItem", markArray);

            log.info("会话标记数据，请求参数：{}", jsonObject.toString());
            String result = HttpUtil.doPost2(httpsUrl, jsonObject);
            log.info("会话标记数据，返回结果：{}", result);
        }
        catch (Exception e) {
            log.error("设置群主异常", e);
        }
    }
}
