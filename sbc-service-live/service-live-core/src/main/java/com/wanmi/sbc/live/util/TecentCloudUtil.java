package com.wanmi.sbc.live.util;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.wanmi.sbc.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.live.v20180801.LiveClient;
import com.tencentcloudapi.live.v20180801.models.DayStreamPlayInfo;
import com.tencentcloudapi.live.v20180801.models.DescribeStreamPlayInfoListRequest;
import com.tencentcloudapi.live.v20180801.models.DescribeStreamPlayInfoListResponse;
import com.tencentcloudapi.live.v20180801.models.DropLiveStreamRequest;
import com.tencentcloudapi.live.v20180801.models.DropLiveStreamResponse;
import com.tencentcloudapi.live.v20180801.models.ForbidLiveStreamRequest;
import com.tencentcloudapi.live.v20180801.models.ForbidLiveStreamResponse;
import com.tencentcloudapi.live.v20180801.models.ResumeLiveStreamRequest;
import com.tencentcloudapi.live.v20180801.models.ResumeLiveStreamResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * 直播使用的腾讯云操作工具
 *
 * @author hl128k
 *
 */
@Slf4j
@Component
public class TecentCloudUtil {
    private static Logger logger = LoggerFactory.getLogger(TecentCloudUtil.class);

    // 用于 生成推流防盗链的key
    private static String PUSH_KEY = "7da4061c0744c9e5804494f187bd41ff";
    // API密钥id  7da4061c0744c9e5804494f187bd41ff
    //@Value("${im.appid}")
    private static String SECRET_ID = "1400787555";
    // API密钥key
    //@Value("${im.secret}")
    private static String SECRET_KEY = "030a04a76482db2eabe26f4b9722501e3a069fcc7b2029ad90ab4a6059fc2d15";

    // 用于 生成拉流防盗链的key 可无
    private static final String LIVE_KEY = "";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-mm-dd'T'hh:mm:ss'Z'");

    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat("yyyy-MM-dd");

    private static Credential credential = null;

    private static LiveClient liveClient = null;

    // 推流域名
    @Value("${live.push.domain}")
    public String PUSH_DOMAIN_INSTANCE;
    // 拉流域名
    @Value("${live.live.domain}")
    public String LIVE_DOMAIN_INSTANCE;

    @Value("${im.appid}")
    private String appIdConfig;

    @Value("${im.secret}")
    private String appSecret;

    @Value("${live.push.key}")
    private String pushKey;

    // 推流域名
    public static String PUSH_DOMAIN;
    // 拉流域名
    public static String LIVE_DOMAIN;
    // API回调地址
    public static final String API_ADDRESS = "live.tencentcloudapi.com";

    public static String PUSH_DOMIAN_NAME;

    public static String LIVE_DOMIAN_NAME;

    public static final String APP_NAME = "live";

    /**
     * 推流地址
     */
    public static String PUSH_URL;

    @PostConstruct
    public void init() {
        Assert.notNull(PUSH_DOMAIN_INSTANCE, "PUSH_DOMAIN 未配置");
        Assert.notNull(LIVE_DOMAIN_INSTANCE, "LIVE_DOMAIN 未配置");

        log.info("腾讯IM初始化，appId {} appSecret {} pushKey {}", appIdConfig, appSecret, pushKey);
        if (StringUtils.isNotEmpty(appIdConfig)) {
            SECRET_ID = appIdConfig;
        }
        if (StringUtils.isNotEmpty(appSecret)) {
            SECRET_KEY = appSecret;
        }
        if (StringUtils.isNotEmpty(pushKey)) {
            PUSH_KEY = pushKey;
        }

        PUSH_DOMAIN = PUSH_DOMAIN_INSTANCE;
        LIVE_DOMAIN = LIVE_DOMAIN_INSTANCE;
        PUSH_DOMIAN_NAME = PUSH_DOMAIN + ".livepush.myqcloud.com";
        LIVE_DOMIAN_NAME = LIVE_DOMAIN + ".livecdn.liveplay.myqcloud.com";
        PUSH_URL = "webrtc://" + PUSH_DOMAIN + "/";
        log.info("域名配置信息为:PUSH_DOMAIN={},LIVE_DOMAIN={}", PUSH_DOMAIN, LIVE_DOMAIN);
    }

    public static void main(String[] args) throws Exception {

        DayStreamPlayInfo[] describeStreamPlayInfoList = describeStreamPlayInfoList("5CD1485C",
                FORMAT2.format(new Date()) + "00:00:00", FORMAT1.format(new Date()));
        for (DayStreamPlayInfo dayStreamPlayInfo : describeStreamPlayInfoList) {
            HashMap<String, String> map = new HashMap<String, String>();
            dayStreamPlayInfo.toMap(map, APP_NAME);
            System.out.println(map.toString());
        }

        System.out.println(getPushUrlByMinute("888881", 30));
        System.out.println(getPullFlvUrl("88888", null));

    }

    /**
     * 实例化认证对象
     *
     * @return
     */
    private static synchronized Credential getCredential() {
        if (credential == null) {
            credential = new Credential(SECRET_ID, SECRET_KEY);
        }
        return credential;
    }

    /**
     * 实例化认证对象
     *
     * @return
     */
    private static LiveClient getLiveClient() {
        if (liveClient == null) {
            liveClient = new LiveClient(getCredential(), "");
        }
        return liveClient;
    }

    /**
     * 禁推 一个直播流 全参数
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @param appName
     *            应用名称
     * @param resumeTime
     *            禁推时长（单位分钟）
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String forbidLiveStream(String streamName, String appName, Long resumeTime)
            throws TencentCloudSDKException {
        Long currentTime = System.currentTimeMillis() + resumeTime * 60L * 1000L;
        LiveClient client = getLiveClient();
        ForbidLiveStreamRequest req = new ForbidLiveStreamRequest();
        req.setAppName(appName);
        req.setStreamName(streamName);
        req.setDomainName(PUSH_DOMIAN_NAME);
        req.setResumeTime(FORMAT.format(new Date(currentTime)));
        ;
        ForbidLiveStreamResponse res = client.ForbidLiveStream(req);
        return res.getRequestId();
    }

    /**
     * 禁推 一个直播流
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @param resumeTime
     *            禁推时长（单位分钟）
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String forbidLiveStream(String streamName, Long resumeTime) throws TencentCloudSDKException {
        return forbidLiveStream(streamName, APP_NAME, resumeTime);
    }

    /**
     * 禁推 一个直播流 禁推30分钟自动生成
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String forbidLiveStream(String streamName) throws TencentCloudSDKException {
        return forbidLiveStream(streamName, APP_NAME, 30L);
    }


    /**
     * 恢复一个被禁止的直播流 全参数
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @param appName
     *            应用名称
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String resumeLiveStream(String streamName, String appName) throws TencentCloudSDKException {
        LiveClient client = getLiveClient();
        ResumeLiveStreamRequest req = new ResumeLiveStreamRequest();
        req.setAppName(appName);
        req.setDomainName(PUSH_DOMIAN_NAME);
        req.setStreamName(streamName);
        ResumeLiveStreamResponse res = client.ResumeLiveStream(req);
        return res.getRequestId();
    }

    /**
     * 恢复一个被禁止的直播流
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String resumeLiveStream(String streamName) throws TencentCloudSDKException {
        return resumeLiveStream(streamName, APP_NAME);
    }

    /**
     * 暂停一个直播流 全参数
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @param appName
     *            应用名称
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String dropLiveStream(String streamName, String appName) throws TencentCloudSDKException {
        LiveClient client = getLiveClient();
        DropLiveStreamRequest req = new DropLiveStreamRequest();
        req.setAppName(appName);
        req.setDomainName(PUSH_DOMIAN_NAME);
        req.setStreamName(streamName);
        DropLiveStreamResponse res = client.DropLiveStream(req);
        return res.getRequestId();
    }

    /**
     * 暂停一个直播流 全参数
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @return 请求 ID
     * @throws TencentCloudSDKException
     */
    public static String dropLiveStream(String streamName) throws TencentCloudSDKException {
        return dropLiveStream(streamName, APP_NAME);
    }





    /**
     * 查询播放数据，支持按流名称查询详细播放数据，也可按播放域名查询详细总数据
     *
     * @author hl128k
     * @param streamName
     *            直播流名字
     * @throws TencentCloudSDKException
     */
    public static DayStreamPlayInfo[] describeStreamPlayInfoList(String streamName, String StartTime, String EndTime)
            throws TencentCloudSDKException {
        LiveClient client = getLiveClient();
        DescribeStreamPlayInfoListRequest req = new DescribeStreamPlayInfoListRequest();
        req.setStreamName(streamName);
        req.setStartTime(StartTime);
        req.setEndTime(EndTime);
        DescribeStreamPlayInfoListResponse res = client.DescribeStreamPlayInfoList(req);
        return res.getDataInfoList();
    }



    /**
     * 这是推流防盗链的生成 KEY+ streamName + txTime
     *
     * @author hl128k
     * @param key
     *            防盗链使用的key
     * @param streamName
     *            通常为直播码
     * @param txTime
     *            到期时间
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getSafeUrl(String key, String streamName, long txTime) throws UnsupportedEncodingException {
        String input = new StringBuilder().append(key).append(streamName).append(Long.toHexString(txTime).toUpperCase())
                .toString();

        String txSecret = null;

        txSecret = MD5Util.md5Hex(input);

        return txSecret == null ? ""
                : new StringBuilder().append("txSecret=").append(txSecret).append("&").append("txTime=")
                .append(Long.toHexString(txTime).toUpperCase()).toString();
    }

    /**
     * 推流地址生成 全参数设置
     *
     * @author hl128k
     * @param streamName
     *            流名称/房间号
     * @param appName
     *            应用名称
     * @param minute
     *            过期时间
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getPushUrl(String streamName, String appName, long minute)
            throws UnsupportedEncodingException {
        Long now = System.currentTimeMillis() + 60L * minute * 1000L;// 要转成long类型
        // 分*秒*时*毫秒
        // 当前毫秒数+需要加上的时间毫秒数 = 过期时间毫秒数
        Long txTime = now / 1000;// 推流码过期时间秒数

        String safeUrl = getSafeUrl(PUSH_KEY, streamName, txTime);

        String realPushUrl = PUSH_URL + appName + "/" + streamName + "?" + safeUrl;

        return realPushUrl;
    }

    /**
     * 推流地址生成 minute分钟
     *
     * @author hl128k
     * @throws UnsupportedEncodingException
     */
    public static String getPushUrlByMinute(String streamName, long minute) throws UnsupportedEncodingException {
        return getPushUrl(streamName, APP_NAME, minute);
    }

    /**
     * 推流地址生成 Hour小时
     *
     * @author hl128k
     * @throws UnsupportedEncodingException
     */
    public static String getPushUrlByHour(String streamName, long Hour) throws UnsupportedEncodingException {
        return getPushUrlByMinute(streamName, Hour * 60L);
    }

    /**
     * 推流地址生成 day天
     *
     * @author hl128k
     * @throws UnsupportedEncodingException
     */
    public static String getPushUrlByDay(String streamName, long day) throws UnsupportedEncodingException {
        return getPushUrlByHour(streamName, day * 24L);
    }

    /**
     * 推流地址生成 30天
     *
     * @author hl128k
     * @throws UnsupportedEncodingException
     */
    public static String getPushUrl(String streamName) throws UnsupportedEncodingException {
        return getPushUrlByDay(streamName, 30);
    }

    /**
     * 获取拉流地址 全属性
     *
     * @author hl128k
     * @param protocol
     *            流类型
     * @param streamName
     *            直播流名字
     * @param appName
     *            应用名称
     * @param resolution
     *            清晰度（540，720，1080）
     * @param suffix
     *            流地址的后缀
     * @return
     */
    public static String getPullUrl(String protocol, String streamName, String appName, String resolution,
                                    String suffix) {
        String PullUrl = "";
        PullUrl += ((protocol == null || protocol.isEmpty()) ? "rtmp" : protocol) + "://" + LIVE_DOMAIN + "/";
        PullUrl += ((appName == null || appName.isEmpty()) ? APP_NAME : appName) + "/";
        PullUrl += streamName;
        PullUrl += (resolution == null || resolution.isEmpty()) ? "" : ("_" + resolution + "p");
        PullUrl += (suffix == null || suffix.isEmpty()) ? "" : ("." + suffix);
        return PullUrl;
    }

    public static String getPullRtmpUrl(String streamName, String resolution) {
        return getPullUrl("rtmp", streamName, APP_NAME, resolution, null);
    }

    public static String getPullFlvUrl(String streamName, String resolution) {
        return getPullUrl("http", streamName, APP_NAME, resolution, "flv");
    }

    public static String getPullM3u8Url(String streamName, String resolution) {
        return getPullUrl("http", streamName, APP_NAME, resolution, "m3u8");
    }

    public static String getPullWbrtcUrl(String streamName, String resolution) {
            return getPullWbrtcUrl("webrtc",streamName, APP_NAME, resolution,null);
    }


    public static String getPullWbrtcUrl(String protocol, String streamName, String appName, String resolution,
                                    String suffix) {
        String PullUrl = "";
        PullUrl += ((protocol == null || protocol.isEmpty()) ? "webrtc" : protocol) + "://" + LIVE_DOMAIN + "/";
        PullUrl += ((appName == null || appName.isEmpty()) ? APP_NAME : appName) + "/";
        PullUrl += streamName;
        PullUrl += (resolution == null || resolution.isEmpty()) ? "" : ("_" + resolution + "p");
        PullUrl += (suffix == null || suffix.isEmpty()) ? "" : ("." + suffix);
        return PullUrl;
    }
}
