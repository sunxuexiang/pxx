package com.wanmi.sbc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.ms.autoconfigure.JwtProperties;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.base.VASEntity;
import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.vas.api.constants.iep.IepServiceErrorCode;
import com.wanmi.sbc.vas.api.provider.iepsetting.IepSettingQueryProvider;
import com.wanmi.sbc.vas.bean.vo.IepSettingVO;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BFF公共工具类
 * Created by daiyitian on 15/12/29.
 */
@Component
public final class CommonUtil {

    @Value("${cookie.name}")
    private String cookieName;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IepSettingQueryProvider iepSettingQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;
    @Autowired
    private JwtProperties jwtProperties;
    @Value("${jwt.secret-key}")
    private String key;

    /**
     * 获取当前登录编号
     *
     * @return
     */
    public String getOperatorId() {
        return getOperator().getUserId();
    }

    /**
     * 获取当前登录的公司信息ID
     *
     * @return
     */
    public Long getCompanyInfoId() {
        String companyInfoId = this.getOperator().getAdminId().toString();
        if (StringUtils.isNotBlank(companyInfoId)) {
            return Long.valueOf(companyInfoId);
        }
        return null;
    }


    /**
     * 获取当前登录的公司信息ID
     *
     * @return
     */
    public Long getCompanyInfoIdWithDefault() {
        String companyInfoId = this.getOperator().getAdminId().toString();
        if (StringUtils.isNotBlank(companyInfoId)) {
            return Long.valueOf(companyInfoId);
        }
        return Constants.BOSS_DEFAULT_COMPANY_INFO_ID;
    }
    /**
     * 获取当前登录的店铺信息ID
     *
     * @return
     */
    public Long getStoreId() {
        String storeId = this.getOperator().getStoreId();
        if (StringUtils.isNotBlank(storeId)) {
            return Long.valueOf(storeId);
        }
        return null;
    }

    /**
     * 获取当前登录的店铺信息ID
     * 若没有设置默认值为boss对应的店铺ID
     *
     * @return
     */
    public Long getStoreIdWithDefault() {
        String storeId = this.getOperator().getStoreId();
        if (StringUtils.isNotBlank(storeId)) {
            return Long.valueOf(storeId);
        }
        return Constants.BOSS_DEFAULT_STORE_ID;
    }


    /**
     * 获取当前登录的商家类型
     *
     * @return
     */
    public int getCompanyType() {
        return this.getOperator().getCompanyType();
    }


    /**
     * 获取当前登录的商家客户等级类别
     * 自营 平台等级
     * 店铺 店铺等级
     *
     * @return
     */
    public DefaultFlag getCustomerLevelType() {
        return this.getOperator().getCompanyType().equals(BoolFlag.YES) ? DefaultFlag.NO : DefaultFlag.YES;
    }

    /**
     * 获取当前登录账号(手机号)
     *
     * @return
     */
    public String getAccountName() {
        return this.getOperator().getName();
    }

    /**
     * 获取当前登录对象
     *
     * @return
     */
    public Operator getOperator() {
        Claims claims = (Claims) HttpUtil.getRequest().getAttribute("claims");
        if (claims == null) {
            throw new SbcRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }

        Object vasObject = claims.get(ConfigKey.VALUE_ADDED_SERVICES.toString());
        List<VASEntity> services = new ArrayList<>();
        if (Objects.nonNull(vasObject)) {
            String vasJson = vasObject.toString();
            Map<String, String> map = (Map<String, String>) JSONObject.parse(vasJson);
            services = map.entrySet().stream().map(m -> {
                VASEntity vasEntity = new VASEntity();
                vasEntity.setServiceName(VASConstants.fromValue(m.getKey()));
                vasEntity.setServiceStatus(StringUtils.equals(VASStatus.ENABLE.toValue(), m.getValue()));
                return vasEntity;
            }).collect(Collectors.toList());
        }

        return Operator.builder()
                .platform(Platform.forValue(ObjectUtils.toString(claims.get("platform"))))
                .adminId(ObjectUtils.toString(claims.get("adminId")))
                .ip(ObjectUtils.toString(claims.get("ip")))
                .name(ObjectUtils.toString(claims.get("EmployeeName")))
                .userId(ObjectUtils.toString(claims.get("employeeId")))
                .storeId(ObjectUtils.toString(claims.get("storeId")))
                .companyType(claims.get("companyType") != null ? Integer.valueOf(ObjectUtils.toString(claims.get("companyType"))) : 1)
                .account(ObjectUtils.toString(claims.get("EmployeeName")))
                .companyInfoId(Long.valueOf(Objects.toString(claims.get("companyInfoId"), "0")))
                .services(services)
                .build();
    }

    /**
     * 获取当前登录对象(JWT忽略的时候使用)
     *
     * @return
     */
    public Operator getUserInfo() {
        Claims claims = (Claims) (HttpUtil.getRequest().getAttribute("claims"));
        if (claims == null) {
            //从header中直接获取token解析 —— 解决需要在被过滤的请求中获取当前登录人信息
            String token = this.getToken(HttpUtil.getRequest());
            if(org.apache.commons.lang3.StringUtils.isNotBlank(token)){
                claims =  this.validate(token);
            }else{
                return new Operator();
            }
        }
        //已登录会员，需要再次比对storeid 加强校验，防止携带其他店铺登录的token，越权查询操作数据
        return Operator.builder()
                .account(ObjectUtils.toString(claims.get("customerAccount")))
                .platform(Platform.CUSTOMER)
                .adminId(ObjectUtils.toString(claims.get("adminId")))
                .name(String.valueOf(claims.get("customerName")))
                .ip(String.valueOf(claims.get("ip")))
                .userId(String.valueOf(claims.get("customerId")))
                .customerRegisterType(CustomerRegisterType.fromValue(Integer.parseInt(String.valueOf(claims.get("customerRegisterType")))))
                .parentId(String.valueOf(claims.get("parentId")))
                .build();
    }

    /**
     * 获取jwtToken
     * @return
     */
    private String getToken(HttpServletRequest request) {

        String jwtHeaderKey = org.apache.commons.lang3.StringUtils.isNotBlank(jwtProperties.getJwtHeaderKey()) ? jwtProperties.getJwtHeaderKey
                () : "Authorization";
        String jwtHeaderPrefix = org.apache.commons.lang3.StringUtils.isNotBlank(jwtProperties.getJwtHeaderPrefix()) ? jwtProperties
                .getJwtHeaderPrefix() : "Bearer ";

        String authHeader = request.getHeader(jwtHeaderKey);

        //当token失效,直接返回失败
        if(authHeader != null && authHeader.length() > 16){
            return authHeader.substring(jwtHeaderPrefix.length());
        }
        return null;
    }

    /**
     * 验证转换为Claims
     * @param token
     * @return
     */
    private Claims validate(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            return claims;
        } catch (final SignatureException | MalformedJwtException | ExpiredJwtException e) {
            return null;
        }
    }

    /**
     * 查询指定增值服务是否购买
     *
     * @param constants
     * @return
     */
    public boolean findVASBuyOrNot(VASConstants constants) {
        boolean flag = false;
        List<VASEntity> list = this.getAllServices();
        VASEntity vasEntity =
                list.stream().filter(f -> StringUtils.equals(f.getServiceName().toValue(), constants.toValue()) && f.isServiceStatus()).findFirst().orElse(null);
        if (Objects.nonNull(vasEntity)) {
            flag = vasEntity.isServiceStatus();
        }
        return flag;
    }

    /**
     * 获取所有增值服务
     *
     * @return
     */
    public List<VASEntity> getAllServices() {
        return this.getOperator().getServices();
    }

    /**
     * 获取当前登录对象,可为空
     *
     * @return
     */
    public Operator getOperatorWithNull() {
        Claims claims = (Claims) HttpUtil.getRequest().getAttribute("claims");
        if (claims == null) {
            return null;
        }

        return Operator.builder()
                .platform(Platform.forValue(ObjectUtils.toString(claims.get("platform"))))
                .adminId(ObjectUtils.toString(claims.get("adminId")))
                .ip(ObjectUtils.toString(claims.get("ip")))
                .name(ObjectUtils.toString(claims.get("EmployeeName")))
                .account(ObjectUtils.toString(claims.get("EmployeeName")))
                .userId(ObjectUtils.toString(claims.get("employeeId")))
                .storeId(ObjectUtils.toString(claims.get("storeId")))
                .build();
    }

    /**
     * cookie中获取token，以获得用户信息
     *
     * @return
     */
    public Operator getOperatorFromCookie() {
        String token = CookieUtil.getValue(HttpUtil.getRequest(), cookieName);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        Claims claims = jwtUtil.validate(token);
        if (claims == null) {
            return null;
        }
        return Operator.builder()
                .platform(Platform.forValue(ObjectUtils.toString(claims.get("platform"))))
                .adminId(ObjectUtils.toString(claims.get("adminId")))
                .ip(ObjectUtils.toString(claims.get("ip")))
                .name(ObjectUtils.toString(claims.get("EmployeeName")))
                .account(ObjectUtils.toString(claims.get("EmployeeName")))
                .userId(ObjectUtils.toString(claims.get("employeeId")))
                .storeId(ObjectUtils.toString(claims.get("storeId")))
                .build();
    }

    /**
     * 获取企业购配置信息
     *
     * @return
     */
    public IepSettingVO getIepSettingInfo() {
        if (!this.findVASBuyOrNot(VASConstants.VAS_IEP_SETTING)) {
            throw new SbcRuntimeException(IepServiceErrorCode.DID_NOT_BUY_IEP_SERVICE);
        }
        if (redisService.hasKey(CacheKeyConstant.IEP_SETTING)) {
            IepSettingVO iepSettingVO = JSONObject.parseObject(redisService.getString(CacheKeyConstant.IEP_SETTING),
                    IepSettingVO.class);
            return iepSettingVO;
        } else {
            return iepSettingQueryProvider.cacheIepSetting().getContext().getIepSettingVO();
        }
    }

    /**
     * 判断商城是否启用Saas化配置
     *
     * @return
     */
    public boolean getSaasStatus() {
        return StringUtils.equals(VASStatus.ENABLE.toValue(), redisService.getString(CacheKeyConstant.SAAS_SETTING));
    }

    public String getSaasDomain() {
        return redisService.getString(CacheKeyConstant.SAAS_DOMAIN);
    }


    /**
     * 获取所有可用的分仓信息
     * @return
     */
    public List<WareHouseVO> queryAllWareHouses(){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            return JSON.parseArray(wareHousesStr,WareHouseVO.class);

        }
        return wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .build()).getContext().getWareHouseVOList();
    }

    /**
     * 获取仓库信息
     * @param wareId
     * @return
     */
    public WareHouseVO getWareHouseByWareId(Long wareId){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            Optional<WareHouseVO> wareHouseVO = wareHouseVOS.stream().filter(w -> w.getWareId().equals(wareId)).findFirst();
            if(wareHouseVO.isPresent()){
                return wareHouseVO.get();
            }
        }else {
            WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                    .wareId(wareId).build()).getContext().getWareHouseVO();
            if (Objects.nonNull(wareHouseVO)){
                return wareHouseVO;
            }
        }
        return new WareHouseVO();
    }
}
