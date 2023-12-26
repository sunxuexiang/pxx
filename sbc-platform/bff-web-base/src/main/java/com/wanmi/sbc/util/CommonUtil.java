package com.wanmi.sbc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanmi.ms.autoconfigure.JwtProperties;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.DistributeChannel;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.base.VASEntity;
import com.wanmi.sbc.common.constant.VASStatus;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerProvider;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.detail.CustomerDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.enterpriseinfo.EnterpriseInfoQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.customer.CustomerModifyRequest;
import com.wanmi.sbc.customer.api.request.detail.CustomerDetailByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.enterpriseinfo.EnterpriseInfoByCustomerIdRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.response.EmpLoginResponse;
import com.wanmi.sbc.customer.response.LoginResponse;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.provider.bidding.BiddingQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.bidding.BiddingListRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.bidding.BiddingListResponse;
import com.wanmi.sbc.goods.bean.enums.ActivityStatus;
import com.wanmi.sbc.goods.bean.enums.BiddingType;
import com.wanmi.sbc.goods.bean.vo.BiddingVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.saas.api.provider.domainstorerela.DomainStoreRelaQueryProvider;
import com.wanmi.sbc.saas.api.request.domainstorerela.DomainStoreRelaByDomainRequest;
import com.wanmi.sbc.saas.api.response.domainstorerela.DomainStoreRelaByDomainResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.setting.bean.enums.ConfigKey;
import com.wanmi.sbc.vas.api.constants.iep.IepServiceErrorCode;
import com.wanmi.sbc.vas.api.provider.iepsetting.IepSettingQueryProvider;
import com.wanmi.sbc.vas.bean.vo.IepSettingVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.CompressionCodecs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BFF公共工具类
 * Created by daiyitian on 15/12/29.
 */
@Component
@Slf4j
public final class CommonUtil {

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private CustomerDetailQueryProvider customerDetailQueryProvider;

    @Autowired
    private DomainStoreRelaQueryProvider domainStoreRelaQueryProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IepSettingQueryProvider iepSettingQueryProvider;

    @Autowired
    private EnterpriseInfoQueryProvider enterpriseInfoQueryProvider;

    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private BiddingQueryProvider biddingQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private CustomerProvider customerProvider;

    @Value("${jwt.secret-key}")
    private String key;

    @Value("${channel}")
    private String CHANNEL;

    @Value("${wareId}")
    private String WAREID;


    private static final String JSON_WEB_TOKEN = "JSON_WEB_TOKEN:";

    /**
     * 获取当前登录编号
     *
     * @return
     */
    public String getOperatorId() {
        return getOperator().getUserId();
    }


    /**
     * 正则表达式：验证手机号 匹配最新的正则表达式
     */
    public String REGEX_MOBILE = "^1[0-9]\\d{9}$";

    /**
     * 正则表达式：验证邮箱
     */
    public String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：银行卡
     */
    public String REGEX_BANK_CARD = "^([0-9]{1})(\\d{14}|\\d{15}|\\d{16}|\\d{18})$";

    /**
     * 图片格式后缀大全
     */
    public static String[] IMAGE_SUFFIX = new String[]{"bmp", "jpg", "jpeg", "heif", "png", "tif", "gif", "pcx", "tga",
            "exif", "fpx", "svg", "psd", "cdr", "pcd", "dxf", "ufo", "eps", "ai", "raw", "WMF", "webp"};


    /**
     * 常见视频格式后缀大全
     */
    public static String[] VIDEO_SUFFIX = new String[]{"avi", "wmv", "rm", "rmvb", "mpeg1", "mpeg2", "mpeg4(mp4)",
            "3gp", "asf", "swf"
            , "vob", "dat", "mov", "m4v", "flv", "f4v", "mkv", "mts", "ts", "imv", "amv", "xv", "qsv"};


    /**
     * 获取登录客户信息
     *
     * @return
     */
    public CustomerVO getCustomer() {
        if(Objects.isNull(getOperatorId())){
            return null;
        }
        //获取会员和等级
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (getOperatorId())).getContext();
        if (customer == null) {
            throw new SbcRuntimeException(CommonErrorCode.PERMISSION_DENIED);
        }
        return customer;
    }

    /**
     * 获取用户的收货地址等信息
     */


    /**
     * 获取分销渠道对象
     *
     * @return
     */
    public DistributeChannel getDistributeChannel() {
        DistributeChannel distributeChannel = JSONObject.parseObject(
                HttpUtil.getRequest().getHeader("distribute-channel"), DistributeChannel.class);
        if (Objects.isNull(distributeChannel)) {
            distributeChannel = new DistributeChannel();
        }
        return distributeChannel;
    }


    /**
     * 获取购物车归属
     * 当且仅当为店铺精选时，需要根据InviteeId区分购物车
     */
    public String getPurchaseInviteeId() {

        if (null != this.getDistributeChannel() && Objects.equals(this.getDistributeChannel().getChannelType(),
                ChannelType.SHOP)) {
            return this.getDistributeChannel().getInviteeId();
        }
        return Constants.PURCHASE_DEFAULT;
    }


    /**
     * 获取当前登录对象
     *
     * @return
     */
    public Operator getOperator() {
        Claims claims = (Claims) (HttpUtil.getRequest().getAttribute("claims"));
        if (claims == null) {
            return this.getUserInfo();
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

        /**保存用户来源渠道*/
        CustomerModifyRequest customerModifyRequest = new CustomerModifyRequest();
        customerModifyRequest.setCustomerId(String.valueOf(claims.get("customerId")));
        customerModifyRequest.setSourceChannel(this.getChannel(HttpUtil.getRequest()));
        customerProvider.modifyCustomerChannel(customerModifyRequest);

        return Operator.builder()
                .account(ObjectUtils.toString(claims.get("customerAccount")))
                .platform(Platform.CUSTOMER)
                .adminId(ObjectUtils.toString(claims.get("adminId")))
                .ip(String.valueOf(claims.get("ip")))
                .name(String.valueOf(claims.get("customerName")))
                .userId(String.valueOf(claims.get("customerId")))
                .customerRegisterType(CustomerRegisterType.fromValue(Integer.parseInt(String.valueOf(claims.get("customerRegisterType")))))
                .services(services)
                .parentId(String.valueOf(claims.get("parentId")))
                .build();
    }

    public LoginResponse loginByCustomerId(String customerId, String jwtSecretKey) {
//        Customer customer = customerService.findInfoById(customerId);
        CustomerGetByIdResponse customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest
                (customerId)).getContext();
        if (customer == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_010005);
        }

        CustomerDetailVO customerDetail = customerDetailQueryProvider.getCustomerDetailByCustomerId(
                CustomerDetailByCustomerIdRequest.builder().customerId(customer.getCustomerId()).build()).getContext();

        if (customerDetail == null) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_010005);
        }
        //是否禁用
        if (CustomerStatus.DISABLE.toValue() == customerDetail.getCustomerStatus().toValue()) {
            throw new SbcRuntimeException(SiteResultCode.ERROR_010002, new Object[]{"，原因为：" + customerDetail
                    .getForbidReason()});
        }
        //组装登录信息
        return this.getLoginResponse(customer, jwtSecretKey);
    }

    /**
     * 拼接登录后返回值
     *
     * @param customer
     * @return
     */
    public LoginResponse getLoginResponse(CustomerVO customer, String jwtSecretKey) {
        Date date = new Date();
        Map<String, String> vasList = redisService.hgetall(ConfigKey.VALUE_ADDED_SERVICES.toString());
        String token = Jwts.builder().setSubject(customer.getCustomerAccount())
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(date)
                .claim("customerId", customer.getCustomerId())
                .claim("customerAccount", customer.getCustomerAccount())
                .claim("customerName", customer.getCustomerDetail().getCustomerName())
                .claim("customerType",customer.getCustomerType())
                .claim("customerRegisterType",customer.getCustomerRegisterType())
                .claim("ip", customer.getLoginIp())
                .claim("parentId",customer.getParentCustomerId())
                .claim(ConfigKey.VALUE_ADDED_SERVICES.toString(), JSONObject.toJSONString(vasList))
                .setExpiration(DateUtils.addMonths(date, 1))
                .compact();

        // 如果是企业会员，则增加公司信息与审核信息/不通过原因
        EnterpriseInfoVO enterpriseInfoVO =
                enterpriseInfoQueryProvider.getByCustomerId(EnterpriseInfoByCustomerIdRequest.builder()
                        .customerId(customer.getCustomerId()).build()).getContext().getEnterpriseInfoVO();

        // 登陆时查询是否是分销员邀请注册，若是则增加邀请码返回前台展示使用
        DistributionCustomerVO distributionCustomerVO =
                distributionCustomerQueryProvider.getByCustomerId(DistributionCustomerByCustomerIdRequest.builder()
                .customerId(customer.getCustomerId()).build()).getContext().getDistributionCustomerVO();
        String inviteCode = StringUtils.EMPTY;
        if(Objects.nonNull(distributionCustomerVO)){
            String inviteId = StringUtils.isEmpty(distributionCustomerVO.getInviteCustomerIds()) ? StringUtils.EMPTY :
                            distributionCustomerVO.getInviteCustomerIds().split(",")[0];
            DistributionCustomerVO inviteCustomer =
                    distributionCustomerQueryProvider.getByCustomerId(DistributionCustomerByCustomerIdRequest.builder()
                            .customerId(inviteId).build()).getContext().getDistributionCustomerVO();
            if(Objects.nonNull(inviteCustomer)){
                inviteCode = inviteCustomer.getInviteCode();
            }
        }

        //token存入redis(有效期一周)
        if(!redisService.hasKey(JSON_WEB_TOKEN.concat(token))){
            // 当前时间
            OffsetDateTime startTime = OffsetDateTime.now().with(LocalTime.MAX);
            // 当前时间加300天
            OffsetDateTime endTime = OffsetDateTime.now().with(LocalTime.MIN).plusDays(3000);
            redisService.setString(JSON_WEB_TOKEN.concat(token),token, ChronoUnit.SECONDS.between(startTime, endTime));
        }

        /**保存用户来源渠道*/
//        CustomerModifyRequest customerModifyRequest = new CustomerModifyRequest();
//        customerModifyRequest.setCustomerId(customer.getCustomerId());
//        customerModifyRequest.setSourceChannel(this.getChannel(HttpUtil.getRequest()));
//        customerProvider.modifyCustomerChannel(customerModifyRequest);

        return LoginResponse.builder()
                .accountName(customer.getCustomerAccount())
                .customerId(customer.getCustomerId())
                .token(token)
                .checkState(customer.getCheckState().toValue())
                .enterpriseCheckState(customer.getEnterpriseCheckState())
                .enterpriseCheckReason(customer.getEnterpriseCheckReason())
                .enterpriseStatusXyy(customer.getEnterpriseStatusXyy())
                .customerDetail(customer.getCustomerDetail())
                .enterpriseInfoVO(enterpriseInfoVO)
                .inviteCode(inviteCode)
                .build();
    }


    public EmpLoginResponse getLoginResponseForEmployeeCopy(EmployeeCopyVo employeeCopyVo, String jwtSecretKey) {
        Date date = new Date();
        Map<String, String> vasList = redisService.hgetall(ConfigKey.VALUE_ADDED_SERVICES.toString());
        String token = Jwts.builder().setSubject(employeeCopyVo.getAccountName())
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
                .setIssuedAt(date)
                .claim("employeeId", employeeCopyVo.getEmployeeId())
                .claim("employAccount", employeeCopyVo.getAccountName())
                .claim("employName", employeeCopyVo.getEmployeeName())
                .claim("employPermType",employeeCopyVo.getPermType())
                .claim("provinceCode",employeeCopyVo.getProvince())
                .claim("cityCode",employeeCopyVo.getCity())
                .claim("areaCode",employeeCopyVo.getArea())
                .claim(ConfigKey.VALUE_ADDED_SERVICES.toString(), JSONObject.toJSONString(vasList))
                .setExpiration(DateUtils.addMonths(date, 1))
                .compact();

        //token存入redis(有效期一周)
        if(!redisService.hasKey(JSON_WEB_TOKEN.concat(token))){
            // 当前时间
            OffsetDateTime startTime = OffsetDateTime.now().with(LocalTime.MAX);
            // 当前时间加300天
            OffsetDateTime endTime = OffsetDateTime.now().with(LocalTime.MIN).plusDays(3000);
            redisService.setString(JSON_WEB_TOKEN.concat(token),token, ChronoUnit.SECONDS.between(startTime, endTime));
        }

        return EmpLoginResponse.builder()
                .accountName(employeeCopyVo.getAccountName())
                .employeeId(employeeCopyVo.getEmployeeId())
                .employeeMobile(employeeCopyVo.getEmployeeMobile())
                .jobNo(employeeCopyVo.getJobNo())
                .permType(employeeCopyVo.getPermType())
                .manageArea(employeeCopyVo.getManageArea())
                .token(token)
                .build();
    }

    /**
     * 获取当前品牌商城Id
     * 该方法非特殊情况下, 不要调用
     * <p>
     * 支付的时候会跳转页面打开第三方支付的页面, 此时无法获取到origin,
     * 因此我们手动给他添加一个url.
     * 参考示例: h5端支付宝支付
     * @param url
     * @return
     */
    public DomainStoreRelaVO getDomainInfo(String url) {
        if (this.getSaasStatus()) {
            String referer = url;
            if (StringUtils.isBlank(referer)) {
                // 获取小程序的请求地址
                referer = HttpUtil.getRequest().getHeader("Origin");
            }

            if (StringUtils.isBlank(referer)) {
                // 获取小程序的请求地址
                referer = HttpUtil.getRequest().getHeader("ctxPath");
            }
            if (StringUtils.isBlank(referer)) {
                return null;
            }
            referer = referer.substring(referer.indexOf("://") + 3);
            log.info("request origin is: " + referer);
            DomainStoreRelaVO domainStore = this.findDomainStore(referer);
            //获取域名对应品牌商城信息
            if (Objects.isNull(domainStore)) {
                String domain = this.getSaasDomain();
                int index = referer.indexOf(domain);
                referer = referer.substring(0, index);
                DomainStoreRelaByDomainResponse domainStoreRelaByDomainResponse =
                        domainStoreRelaQueryProvider.findByDomain(DomainStoreRelaByDomainRequest.builder()
                                .domain(referer).build()).getContext();
                //获取域名对应品牌商城信息
                if (Objects.nonNull(domainStoreRelaByDomainResponse.getDomainStoreRelaVO())) {
                    return domainStoreRelaByDomainResponse.getDomainStoreRelaVO();
                } else {

                    throw new SbcRuntimeException(CommonErrorCode.METHOD_NOT_ALLOWED);
                }
            }

            return domainStore;
        } else {
            return null;
        }
    }

    /**
     * 获取当前品牌商城Id
     *
     * @return
     */
    public DomainStoreRelaVO getDomainInfo() {
        return getDomainInfo("");
    }

    /**
     * 判断商城是否启用Saas化配置
     *
     * @return
     */
    private boolean getSaasStatus() {
        return StringUtils.equals(VASStatus.ENABLE.toValue(), redisService.getString(CacheKeyConstant.SAAS_SETTING));
    }

    private String getSaasDomain() {
        return redisService.getString(CacheKeyConstant.SAAS_DOMAIN);
    }

    /**
     * saas鉴权
     *
     * @param storeId
     */
    public void checkIfStore(Long storeId) {
        DomainStoreRelaVO domainStoreRelaVO = this.getDomainInfo();
        if (Objects.nonNull(domainStoreRelaVO)) {
            if (!Objects.equals(storeId, domainStoreRelaVO.getStoreId())) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
        }
    }


    public DomainStoreRelaVO findDomainStore(String domain) {
        Map<String, String> domains = redisService.hgetall(CacheKeyConstant.CACHE_KEY);

        Optional<String> optional = domains.keySet().stream().filter(domain::contains).findFirst();

        if (optional.isPresent()) {
            String domainInfo = domains.get(optional.get());

            return JSONObject.parseObject(domainInfo, DomainStoreRelaVO.class);
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
        return getStoreIdWithDefault("");
    }

    /**
     * 获取当前登录的店铺信息ID
     * 若没有设置默认值为boss对应的店铺ID
     *
     * @return
     */
    public Long getStoreIdWithDefault(String url) {
        DomainStoreRelaVO domainStoreRelaVO = getDomainInfo(url);
        return getStoreIdWithDefault(domainStoreRelaVO);
    }

    /**
     * 获取当前登录的店铺信息ID
     * 若没有设置默认值为boss对应的店铺ID
     *
     * @return
     */
    public Long getStoreIdWithDefault(DomainStoreRelaVO domainStoreRelaVO) {
        if (Objects.nonNull(domainStoreRelaVO)) {
            return Long.valueOf(domainStoreRelaVO.getStoreId());
        }
        return Constants.BOSS_DEFAULT_STORE_ID;
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
        Claims claims = (Claims) HttpUtil.getRequest().getAttribute("claims");
        if (claims == null) {
            Map<String, String> vasList = redisService.hgetall(ConfigKey.VALUE_ADDED_SERVICES.toString());
            return vasList.entrySet().stream().map(m -> {
                VASEntity vasEntity = new VASEntity();
                vasEntity.setServiceName(VASConstants.fromValue(m.getKey()));
                vasEntity.setServiceStatus(StringUtils.equals(VASStatus.ENABLE.toValue(), m.getValue()));
                return vasEntity;
            }).collect(Collectors.toList());
        } else {
            return this.getOperator().getServices();
        }
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
     * 获取仓库信息
     * @param wareId
     * @return
     */
    public WareHouseVO getWareHouseByWareId(Long wareId){
//        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
//        log.info("=========wareHousesStr:{}",wareHousesStr);
//        if(StringUtils.isNotEmpty(wareHousesStr)){
//            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
//            Optional<WareHouseVO> wareHouseVO = wareHouseVOS.stream().filter(w -> w.getWareId().equals(wareId)).findFirst();
//            if(wareHouseVO.isPresent()){
//                return wareHouseVO.get();
//            }
//        }else {
//            WareHouseVO wareHouseVO = wareHouseQueryProvider.getByWareId(WareHouseByIdRequest.builder()
//                    .wareId(wareId).storeId(getStoreIdWithDefault()).build()).getContext().getWareHouseVO();
//            if (Objects.nonNull(wareHouseVO)){
//                return wareHouseVO;
//            }
//        }
//        return new WareHouseVO();

        WareHouseVO wareHouseVO = wareHouseQueryProvider.getByWareId(WareHouseByIdRequest.builder()
                .wareId(wareId).storeId(getStoreIdWithDefault()).build()).getContext().getWareHouseVO();
        if (Objects.nonNull(wareHouseVO)){
            return wareHouseVO;
        }

        return new WareHouseVO();
    }

    /**
     * 获取仓库信息
     * @param storeIdList
     * @return
     */
    public List<WareHouseVO> getWareHouseByStoreId(List<Long> storeIdList,WareHouseType wareHouseType){
        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr, WareHouseVO.class);
            return wareHouseVOS.stream().filter(param -> storeIdList.contains(param.getStoreId())
                    &&param.getDelFlag().equals(DeleteFlag.NO)&&wareHouseType.equals(param.getWareHouseType())).collect(Collectors.toList());
        }
        return wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .wareHouseType(wareHouseType)
                .storeIdList(storeIdList)
                .build()).getContext().getWareHouseVOList();

    }

    /**
     * 获取所有的关键词/获取竞价的分类
     */
    public List<String> getKeywordsFromCache(BiddingType biddingType){
//        String key = BiddingType.KEY_WORDS_TYPE.equals(biddingType)
//                ? BiddingCacheConstants.GOODS_KEY_WORDS : BiddingCacheConstants.GOODS_CATE_KEYS;
//        List<String> keywords = redisService.gSets(key);
//        if(CollectionUtils.isNotEmpty(keywords)){
//            return keywords;
//        }else{
//            BiddingListResponse response = biddingQueryProvider.list(BiddingListRequest.builder()
//                    .biddingStatus(ActivityStatus.SALE)
//                    .biddingType(biddingType)
//                    .delFlag(DeleteFlag.NO)
//                    .build()).getContext();
//            if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getBiddingVOList())){
//                List<String> keywordList = response.getBiddingVOList().stream().map(BiddingVO::getKeywords).collect(Collectors.toList());
//                List<String> allKeywords = new ArrayList<>();
//                keywordList.stream().forEach(k->allKeywords.addAll(Arrays.asList(k.split(","))));
//                redisService.iSet(key,allKeywords);
//                return allKeywords;
//            }
//        }
        BiddingListResponse response = biddingQueryProvider.list(BiddingListRequest.builder()
                .biddingStatus(ActivityStatus.SALE)
                .biddingType(biddingType)
                .delFlag(DeleteFlag.NO)
                .build()).getContext();
        if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getBiddingVOList())){
            List<String> keywordList = response.getBiddingVOList().stream().map(BiddingVO::getKeywords).collect(Collectors.toList());
            List<String> allKeywords = new ArrayList<>();
            keywordList.stream().forEach(k->allKeywords.addAll(Arrays.asList(k.split(","))));
            return allKeywords;
        }
        return new ArrayList<>();
    }

    /**
     * 判断是否是大客户—— 已审核，有企业证书，和社会统一信用代码
     * @return
     */
    public boolean isVipCustomer(){
        CustomerVO customerVO = this.getCustomer();
        if(Objects.nonNull(customerVO) && EnterpriseCheckState.CHECKED.equals(customerVO.getEnterpriseStatusXyy())){
            if(Objects.nonNull(customerVO.getBusinessLicenseUrl()) && Objects.nonNull(customerVO.getSocialCreditCode())){
                return true;
            }
        }
        return false;
    }

    /**
     * 通过vip标识判断是否大客户 0-否 1-是
     * @return
     */
    public boolean isVipCustomerByVipFlag() {
        CustomerVO customerVO = this.getCustomer();
        if (Objects.nonNull(customerVO) && DefaultFlag.YES.equals(customerVO.getVipFlag())){
            return true;
        }
        return false;
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
            if(StringUtils.isNotBlank(token)){
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
     * 获取用户来源
     */
    public String getChannel(HttpServletRequest request) {

        if(StringUtils.isBlank(CHANNEL)){
            CHANNEL = "channel";
        }

        String channelData = request.getHeader(CHANNEL);

        //默认为大白鲸h5
        if(StringUtils.isBlank(channelData)){
            return "cjdbj-h5";
        }

        return channelData;
    }

    /**
     * 验证转换为Claims
     * @param token
     * @return
     */
    private Claims validate(String token) {
        try {
            final Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
            log.debug("JwtFilter out ['Authorization success']");
            return claims;
        } catch (final SignatureException | MalformedJwtException | ExpiredJwtException e) {
            log.info("JwtFilter exception, exMsg:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取客户地址
     * 1.获取客户上次选择地址
     * 2.若无上次选择地址获取客户默认地址
     * 3.若无默认地址获取地址中的第一条
     * @return
     */
    public CustomerDeliveryAddressResponse getDeliveryAddress() {
        CustomerDeliveryAddressRequest queryRequest = new CustomerDeliveryAddressRequest();
        String customerId = this.getOperatorId();
        queryRequest.setCustomerId(customerId);
        BaseResponse<CustomerDeliveryAddressResponse> customerDeliveryAddressResponseBaseResponse = customerDeliveryAddressQueryProvider.getChoosedOrAnyOneByCustomerId(queryRequest);
        CustomerDeliveryAddressResponse customerDeliveryAddressResponse = customerDeliveryAddressResponseBaseResponse.getContext();
        return customerDeliveryAddressResponse;
    }

    /**
     *
     * 请求头获取仓库信息
     */
    public WareHouseVO getWareHouse(HttpServletRequest request){

        String wareIdStr = request.getHeader(WAREID);
        Long wareId = NumberUtils.LONG_ONE;
        //默认为大白鲸h5
        if(StringUtils.isNotBlank(wareIdStr)){
            wareId = Long.valueOf(wareIdStr);
        }

        String wareHousesStr = redisService.hget(WareHouseConstants.WARE_HOUSES,WareHouseConstants.WARE_HOUSE_MAIN_FILED);
        if(StringUtils.isNotEmpty(wareHousesStr)){
            List<WareHouseVO> wareHouseVOS = JSON.parseArray(wareHousesStr,WareHouseVO.class);
            Long finalWareId = wareId;
            Optional<WareHouseVO> wareHouseVO = wareHouseVOS.stream().filter(w -> w.getWareId().equals(finalWareId)).findFirst();
            if(wareHouseVO.isPresent()){
                return wareHouseVO.get();
            }
        }
        return wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                .wareId(wareId).storeId(getStoreIdWithDefault()).build()).getContext().getWareHouseVO();
    }

    /**
     *
     * 请求头获取仓库信息
     */
    public Long getWareId(HttpServletRequest request){

        String wareIdStr = request.getHeader(WAREID);
        Long wareId = NumberUtils.LONG_ONE;
        //默认为大白鲸h5
        if(StringUtils.isNotBlank(wareIdStr)){
            wareId = Long.valueOf(wareIdStr);
        }

        return wareId;
    }

    /**
     *
     * 请求头获取散批仓库信息
     */
    public Long getBulkWareId(HttpServletRequest request){

        String wareIdStr = request.getHeader("bulkWareId");
        //默认为大白鲸h5
        if(StringUtils.isNotBlank(wareIdStr)){
            return Long.valueOf(wareIdStr);
        }
        return null;
    }

    /**
     *
     * 请求头获取省id
     */
    public Long getProvinceId(HttpServletRequest request){

        String provinceIdStr = request.getHeader("provinceId");
        Long provinceId = null;
        //默认为大白鲸h5
        if(StringUtils.isNotBlank(provinceIdStr)){
            provinceId = Long.valueOf(provinceIdStr);
        }

        return provinceId;
    }

    /**
     *
     * 请求头获取市id
     */
    public Long getCityId(HttpServletRequest request){

        String cityIdStr = request.getHeader("cityId");
        Long cityId = null;
        //默认为大白鲸h5
        if(StringUtils.isNotBlank(cityIdStr)){
            cityId = Long.valueOf(cityIdStr);
        }

        return cityId;
    }

    public CustomerDeliveryAddressResponse getProvinceCity(HttpServletRequest httpRequest){
        CustomerDeliveryAddressResponse deliveryAddress =null;
        Long provinceId = this.getProvinceId(httpRequest);
        Long cityId = this.getCityId(httpRequest);
        if (Objects.nonNull(provinceId)&&Objects.nonNull(cityId)) {
            deliveryAddress=CustomerDeliveryAddressResponse.builder().build();
            deliveryAddress.setCityId(cityId);
            deliveryAddress.setProvinceId(provinceId);
        }
        return deliveryAddress;
    }
}
