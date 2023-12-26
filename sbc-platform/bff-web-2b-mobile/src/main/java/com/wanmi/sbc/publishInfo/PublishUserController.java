package com.wanmi.sbc.publishInfo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.response.LoginResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishUserProvider;
import com.wanmi.sbc.setting.api.provider.publishInfo.PublishUserQueryProvider;
import com.wanmi.sbc.setting.api.request.publishInfo.PublishUserRequest;
import com.wanmi.sbc.setting.api.response.publishInfo.PublishUserResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.setting.bean.vo.PublishUserVO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;


/**
 * Created by lwp on 2323/10/18.
 */
@Api(tags = "PublishUserController", description = "信息发布用户API")
@RestController
@RequestMapping("/publishUser")
public class PublishUserController {

    private static final Logger logger = LoggerFactory.getLogger(PublishUserController.class);

    @Autowired
    private PublishUserProvider publishUserProvider;

    @Autowired
    private PublishUserQueryProvider publishUserQueryProvider;

    @Autowired
    private RedisService redisService;

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${cookie.name}")
    private String name;

    @Value("${cookie.secure}")
    private boolean secure;

    @Value("${cookie.maxAge}")
    private Integer maxAge;

    @Value("${cookie.path}")
    private String path;

    @Value("${cookie.domain}")
    private String domain;

    private static final String JSON_WEB_TOKEN = "JSON_WEB_TOKEN:";

    /**
     * 注册
     *
     * @return BaseResponse
     */
    @ApiOperation(value = "注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public BaseResponse register(@RequestBody PublishUserRequest request) {
        String account = new String(Base64.getUrlDecoder().decode(request.getUserName().getBytes()));
        String password = new String(Base64.getUrlDecoder().decode(request.getUserPass().getBytes()));
        request.setUserName(account);
        request.setUserPass(password);
        publishUserProvider.addPublishUser(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 登录
     *
     * @return
     */
    @ApiOperation(value = "登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public BaseResponse login(@RequestBody PublishUserRequest request, HttpServletResponse httpServletResponse){
        logger.info("请求内容：{}",request);
        String account = new String(Base64.getUrlDecoder().decode(request.getUserName().getBytes()));
        String password = new String(Base64.getUrlDecoder().decode(request.getUserPass().getBytes()));
        request.setUserName(account);
        request.setUserPass(password);
        logger.info("转换后请求内容：{}",request);
        BaseResponse<PublishUserResponse> response = publishUserQueryProvider.getPublishUser(request);
        if(response != null){
            PublishUserVO publishUserVO = response.getContext().getPublishUserVO();
            Map<String, Object> claims = Maps.newHashMap();
            claims.put("userId", publishUserVO.getId());
            claims.put("userName", publishUserVO.getUserName());
            claims.put("ip", HttpUtil.getIpAddr());
            claims.put("platform", Platform.THIRD.toValue());
            Map<String, String> vasList = redisService.hgetall(ConfigKey.VALUE_ADDED_SERVICES.toString());
            claims.put(ConfigKey.VALUE_ADDED_SERVICES.toString(), JSONObject.toJSONString(vasList));
            Date now = new Date();
            String token = Jwts.builder().setSubject(publishUserVO.getUserName())
                    .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                    .setIssuedAt(now)
                    .setClaims(claims)
                    .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS))) // 有效期一个星期
                    .compact();
            Cookie cookie = new Cookie(name, token);
            cookie.setSecure(secure);
            cookie.setMaxAge(maxAge);
            cookie.setPath(path);
            if(StringUtils.isNotEmpty(domain)){
                cookie.setDomain(domain);
            }
            httpServletResponse.addCookie(cookie);
            //token存入redis(有效期一周)
            if(!redisService.hasKey(JSON_WEB_TOKEN.concat(token))){
                // 当前时间
                OffsetDateTime startTime = OffsetDateTime.now().with(LocalTime.MAX);
                // 当前时间加七天
                OffsetDateTime endTime = OffsetDateTime.now().with(LocalTime.MIN).plusDays(7);
                redisService.setString(JSON_WEB_TOKEN.concat(token),token, ChronoUnit.SECONDS.between(startTime, endTime));
            }
            return BaseResponse.success(LoginResponse.builder()
                    .accountName(publishUserVO.getUserName())
                    .token(token)
                    .build());
        }
        return BaseResponse.error("用户名或密码不正确");
    }
}
