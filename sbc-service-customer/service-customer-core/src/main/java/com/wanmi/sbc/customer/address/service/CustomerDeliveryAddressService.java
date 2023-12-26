package com.wanmi.sbc.customer.address.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpCommonResult;
import com.wanmi.sbc.common.util.HttpCommonUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.address.model.root.CustomerDeliveryAddress;
import com.wanmi.sbc.customer.address.repository.CustomerDeliveryAddressExceRepository;
import com.wanmi.sbc.customer.address.repository.CustomerDeliveryAddressRepository;
import com.wanmi.sbc.customer.address.request.CustomerDeliveryAddressEditRequest;
import com.wanmi.sbc.customer.address.request.CustomerDeliveryAddressQueryRequest;
import com.wanmi.sbc.customer.address.response.DeliveryAddressResponse;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.response.baiduBean.GetDistance;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.customer.dotdistance.repository.DotDistanceRepository;
import com.wanmi.sbc.customer.dotdistance.service.DotDistanceService;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.netWorkService.NetWorkService;
import com.wanmi.sbc.customer.redis.RedisCache;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import com.wanmi.sbc.customer.service.model.CustomerKingdeeModel;
import com.wanmi.sbc.customer.util.CustomerKingdeeLoginUtils;
import com.wanmi.sbc.setting.api.provider.region.RegionCopyProvider;
import com.wanmi.sbc.setting.api.provider.region.RegionQueryProvider;
import com.wanmi.sbc.setting.api.request.yunservice.RegionCopyQueryCodeRequest;
import com.wanmi.sbc.setting.api.response.region.RegionQueryResponse;
import com.wanmi.sbc.setting.bean.vo.RegionCopyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 客户收货地址服务
 * Created by CHENLI on 2017/4/20.
 */
@Service
@Slf4j
public class CustomerDeliveryAddressService {

    @Autowired
    private CustomerDeliveryAddressRepository repository;


    @Autowired
    private DotDistanceRepository dotDistanceRepository;

    @Autowired
    private DotDistanceService dotDistanceService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerKingdeeLoginUtils loginUtils;

    @Autowired
    private CustomerDeliveryAddressExceRepository repositoryExce;

    @Autowired
    private RegionQueryProvider regionQueryProvider;

    @Autowired
    private NetWorkService netWorkService;

    @Autowired
    private RegionCopyProvider regionCopyProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisCache redisCache;

    @Value("${kingdee.login.url}")
    private String loginUrl;

    @Value("${kingdee.Customer.url}")
    private String customerUrl;

    @Value("${kingdee.user}")
    private String kingdeeUser;

    @Value("${kingdee.pwd}")
    private String kingdeePwd;

    @Value("${kingdee.organization}")
    private String kingdeeOrganization;

    /**
     * 是否开启新金蝶
     */
    @Value("${kingdee.open.state}")
    private Boolean kingdeeOpenState;

    @Value("${kingdee.fGroup}")
    private String kingdeeFGroup;

    @Autowired
    private NetWorkProvider netWorkProvider;





    /**
     * 根据ID查询
     *
     * @param deliveredId
     * @return
     */
    public CustomerDeliveryAddress findById(String deliveredId) {
        return repository.findById(deliveredId).orElse(null);
    }

    /**
     * 功能描述: 更新最后一次选择地址
     */
    public void update(String deliveredId,String customerId) {
        List<CustomerDeliveryAddress> addressesList = repository.findByCustomerIdAndDelFlagOrderByCreateTimeDesc(customerId, DeleteFlag.NO);
        if (CollectionUtils.isEmpty(addressesList)){
            throw new SbcRuntimeException(CommonErrorCode.FAILED,"地址不存在");
        }
        for (CustomerDeliveryAddress inner:addressesList){
            if (deliveredId.equals(inner.getDeliveryAddressId())){
                inner.setChooseFlag(DefaultFlag.YES);
                continue;
            }
            inner.setChooseFlag(DefaultFlag.NO);
        }
        repository.saveAll(addressesList);
    }


    /**
     * 查询客户默认的收货地址
     * 如果客户没有设置默认地址，则取该客户其中的一条收货地址
     *
     * @param queryRequest
     * @return
     */
    public CustomerDeliveryAddress findDefault(CustomerDeliveryAddressQueryRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setIsDefaltAddress(DefaultFlag.YES.toValue());
        CustomerDeliveryAddress deliveryAddress = repository.findOne(queryRequest.getWhereCriteria()).orElse(null);
        if (Objects.isNull(deliveryAddress)) {
            List<CustomerDeliveryAddress> addressList = this.findAddressList(queryRequest.getCustomerId());
            if (CollectionUtils.isNotEmpty(addressList)) {
                deliveryAddress = this.findAddressList(queryRequest.getCustomerId()).get(0);
            }
        }
        return deliveryAddress;
    }


    /**
     * 查询客户已选中的收货地址
     * 如果客户没有选过地址，则取该客户的默认收货地址
     * 如果客户没有默认的收货地址，则取该客户其中的一条收货地址
     *
     * @param queryRequest
     * @return
     */
    public CustomerDeliveryAddress findChoosed(CustomerDeliveryAddressQueryRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setChooseFlag(DefaultFlag.YES);
        CustomerDeliveryAddress deliveryAddress = repository.findOne(queryRequest.getWhereCriteria()).orElse(null);
        if (Objects.isNull(deliveryAddress)) {
            return findDefault(CustomerDeliveryAddressQueryRequest.builder()
                    .delFlag(DeleteFlag.NO.toValue())
                    .customerId(queryRequest.getCustomerId())
                    .isDefaltAddress(DefaultFlag.YES.toValue())
                    .build());
        }
        return deliveryAddress;
    }


    /**
     * 查询客户的收货地址
     *
     * @param customerId
     * @return
     */
    public List<CustomerDeliveryAddress> findAddressList(String customerId) {
        List<CustomerDeliveryAddress> addressList = repository.findByCustomerIdAndDelFlagOrderByCreateTimeDesc
                (customerId, DeleteFlag.NO);
        if (CollectionUtils.isNotEmpty(addressList)) {
            return addressList;
        }
        return Collections.emptyList();
    }

    /**
     * 保存客户的收货地址
     *
     * @param editRequest
     */
    @Transactional
    public CustomerDeliveryAddress saveAddress(CustomerDeliveryAddressEditRequest editRequest, String employeeId) {
        //如果设置为默认地址
        if (editRequest.getIsDefaltAddress().equals(DefaultFlag.YES)) {
            CustomerDeliveryAddress address = this.getDefault(editRequest.getCustomerId());
            if (Objects.nonNull(address)) {
                //把已设为默认的地址给取消默认
                address.setIsDefaltAddress(DefaultFlag.NO);
                repository.save(address);
            }
        }
        CustomerDeliveryAddress address = new CustomerDeliveryAddress();
        BeanUtils.copyProperties(editRequest, address);
        address.setDelFlag(DeleteFlag.NO);
//        address.setIsDefaltAddress(DefaultFlag.NO);
        address.setCreatePerson(employeeId);
        address.setCreateTime(LocalDateTime.now());
        //判断传入的数据有没有详细经纬度
        if (Objects.isNull(address.getNLat()) || Objects.isNull(address.getNLng())){
            //通过百度地图API获取经纬度
            //详细地址
            String deliveryAddress = address.getDeliveryAddress();
            //获取省名称
            RegionCopyQueryCodeRequest request = new RegionCopyQueryCodeRequest();
            request.setCode(address.getProvinceId());
            String provinceIdName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
            //获取市名称
            request.setCode(address.getCityId());
            String cityName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
            //获取区域名称
            request.setCode(address.getAreaId());
            String areaName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
            String twonName = address.getTwonName();
            //合并地址
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(provinceIdName).append(cityName).append(areaName).append(twonName).append(deliveryAddress);
            ReturnLocationBean returnLocationBean = netWorkService.AddressTolongitudea(stringBuffer.toString());
            if (returnLocationBean.getStatus()==0){
                BigDecimal lat = returnLocationBean.getResult().getLocation().getLat();
                BigDecimal lng = returnLocationBean.getResult().getLocation().getLng();
                address.setNLat(lat);
                address.setNLng(lng);
            }else {
                throw new SbcRuntimeException("K-060002","百度地图api未查询到经纬度请重新确认地址");
            }
        }
        if (kingdeeOpenState) {
            //查询用户是否存在地址
            int site = repository.selectCountCustomerAddress(editRequest.getCustomerId());
            if (site == 0) {
                //查询客户信息
                Customer customer = customerRepository.findByCustomerIdAndDelFlag(editRequest.getCustomerId(), DeleteFlag.NO);
                if (customer != null && editRequest != null) {
                    pushUserKingdee(customer, editRequest);
                } else {
                    log.info("CustomerDeliveryAddressService.saveAddress Free value");
                }
            }
        }
        Long provinceId = 420000L;
        if (Objects.equals(provinceId, address.getProvinceId()) && Objects.nonNull(address.getNLat())) {
            // 匹配 免配站点
            List<NetWorkVO> netWorkVOS = netWorkProvider.getNetWorkAllData("420000").getContext().getNetWorkVOS()
                    .stream().filter(v-> v.getDelFlag() == 0 && Objects.equals(String.valueOf(address.getAreaId()),v.getArea())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(netWorkVOS)) {
                Double distance = null;
                for (NetWorkVO vo : netWorkVOS) {
                    Map<String,Object> parammap = new HashMap<>();
                    parammap.put("longitudeFrom",vo.getLng());//第一个点的经度
                    parammap.put("latitudeFrom",vo.getLat());//第一个点的纬度
                    parammap.put("longitudeTo",address.getNLng());//第二个点的经度
                    parammap.put("latitudeTo",address.getNLat());//第二个点的纬度
                    GetDistance context2 = netWorkProvider.getJWDistance(parammap).getContext();
                    if (context2.getDistanceResult()<vo.getDistance()){
                        if (Objects.isNull(distance) || context2.getDistanceResult() < distance) {
                            address.setNetworkId(vo.getNetworkId());
                            distance = context2.getDistanceResult();
                        }
                        //break;
                    }else {
                        //不满足加入networkids下次不查询
                        if (StringUtils.isNotEmpty(address.getNetworkIds())){
                            address.setNetworkIds(address.getNetworkIds() + "," + vo.getNetworkId());
                        }else {
                            address.setNetworkIds(String.valueOf(vo.getNetworkId()));
                        }
                    }
                }
            }
        }

        CustomerDeliveryAddress customerDeliveryAddress = repository.saveAndFlush(address);

        if (Objects.equals(provinceId, customerDeliveryAddress.getProvinceId()) && Objects.isNull(customerDeliveryAddress.getNetworkId())) {
            //获取写锁
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(CacheKeyConstant.NETWORK_ADRESS_DISTANCE + customerDeliveryAddress.getDeliveryAddressId());
            RLock rLock = readWriteLock.writeLock();
            //是否要刷新地址距离表
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            try {
                rLock.lock();
                //删除网点地址距离表
                dotDistanceRepository.deleteByDeliveryAddressId(customerDeliveryAddress.getDeliveryAddressId());
                //删除redis缓存
                if (redisCache.hasKey(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE+customerDeliveryAddress.getDeliveryAddressId())){
                    redisCache.delete(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE+customerDeliveryAddress.getDeliveryAddressId());
                    atomicBoolean.set(true);
                }
            }finally {
                rLock.unlock();
            }
            if (atomicBoolean.get()){
                //刷新单个
                dotDistanceService.executOneAdress(KsBeanUtil.convert(customerDeliveryAddress, CustomerDeliveryAddressVO.class));
            }
        }



        return customerDeliveryAddress;
    }

    /**
     * push注册客户到金蝶
     * @param customer
     */
    private void pushUserKingdee(Customer customer,CustomerDeliveryAddressEditRequest editRequest){
        log.info("CustomerSiteService.pushUserKingdee req customerId:{}",customer.getCustomerId());
        try {
            if (StringUtils.isEmpty(customer.getCustomerAccount())){
                log.error("CustomerSiteService.pushUserKingdee CustomerAccount is null");
                return;
            }
            if (editRequest.getProvinceId() == null){
                log.error("CustomerSiteService.pushUserKingdee FProvince is null");
                return;
            }
            if (editRequest.getCityId() == null){
                log.error("CustomerSiteService.pushUserKingdee FCity is null");
                return;
            }
            if (editRequest.getAreaId() == null){
                log.error("CustomerSiteService.pushUserKingdee FArea is null");
                return;
            }
            CustomerKingdeeModel kingdeeModel = new CustomerKingdeeModel();
            kingdeeModel.setFNumber(customer.getCustomerAccount());
            kingdeeModel.setFName(customer.getCustomerAccount());
            Map fCreateOrgId = new HashMap();
            fCreateOrgId.put("FNumber",kingdeeOrganization);
            kingdeeModel.setFCreateOrgId(fCreateOrgId);//组织
            Map fProvince = new HashMap();
            fProvince.put("FNumber",editRequest.getProvinceId());
            kingdeeModel.setFProvince(fProvince);//省份
            Map fCity = new HashMap();
            fCity.put("FNumber",editRequest.getCityId());
            kingdeeModel.setFCity(fCity);//城市
            Map area = new HashMap();
            area.put("FNumber",editRequest.getAreaId());
            kingdeeModel.setFArea(area);//区
            kingdeeModel.setFContact(customer.getCustomerAccount());
            kingdeeModel.setFTel(customer.getCustomerAccount());
            kingdeeModel.setFAddress(editRequest.getDeliveryAddress());
            Map fGroup = new HashMap();
            fGroup.put("FNumber",kingdeeFGroup);
            kingdeeModel.setFGroup(fGroup);
            Map fPriceListId = new HashMap();
            fPriceListId.put("FNumber","XSJMB0007");
            kingdeeModel.setFPriceListId(fPriceListId);
            //登录财务系统
            Map<String,Object> requestLogMap = new HashMap<>();
            requestLogMap.put("user",kingdeeUser);
            requestLogMap.put("pwd",kingdeePwd);
            String loginToken = loginUtils.userLoginKingdee(requestLogMap, loginUrl);
            if (StringUtils.isNotEmpty(loginToken)){
                //提交财务单
                Map<String,Object> requestMap = new HashMap<>();
                requestMap.put("Model",kingdeeModel);
                HttpCommonResult result1 = HttpCommonUtil.postHeader(customerUrl, requestMap, loginToken);
                log.info("CustomerSiteService.pushUserKingdee result1:{}", result1.getResultData());
            }else {
                log.error("CustomerSiteService.pushUserKingdee push kingdee error");
            }
        }catch (Exception e){
            log.error("CustomerSiteService.pushUserKingdee customerId:{} error:{}",customer.getCustomerId(),e);
            return;
        }
    }


    /**
     * 修改客户的收货地址
     *
     * @param editRequest
     */
    @Transactional
    public CustomerDeliveryAddress update(CustomerDeliveryAddressEditRequest editRequest, String employeeId) {
        CustomerDeliveryAddress address = repository.findById(editRequest.getDeliveryAddressId()).orElse(null);
        if (Objects.isNull(address)){
            return null;
        }
        //如果设置为默认地址
        if (editRequest.getIsDefaltAddress().equals(DefaultFlag.YES) && address.getIsDefaltAddress().equals
                (DefaultFlag.NO)) {
            CustomerDeliveryAddress addressDefault = this.getDefault(address.getCustomerId());
            if (Objects.nonNull(addressDefault)) {
                //把已设为默认的地址给取消默认
                addressDefault.setIsDefaltAddress(DefaultFlag.NO);
                repository.save(addressDefault);
            }
        }
        /**
         * 重新获取百度地图经纬度
         */
        //通过百度地图API获取经纬度
        //详细地址
        String deliveryAddress = editRequest.getDeliveryAddress();
        //获取省名称
        RegionCopyQueryCodeRequest request = new RegionCopyQueryCodeRequest();
        request.setCode(editRequest.getProvinceId());
        String provinceIdName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
        //获取市名称
        request.setCode(editRequest.getCityId());
        String cityName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
        //获取区域名称
        request.setCode(editRequest.getAreaId());
        String areaName = regionCopyProvider.queryByCodeAndReturn(request).getContext();
        String twonName = editRequest.getTwonName();
        //合并地址
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(provinceIdName).append(cityName).append(areaName).append(twonName).append(deliveryAddress);
        ReturnLocationBean returnLocationBean = netWorkService.AddressTolongitudea(stringBuffer.toString());
        if (returnLocationBean.getStatus()==0){
            BigDecimal lat = returnLocationBean.getResult().getLocation().getLat();
            BigDecimal lng = returnLocationBean.getResult().getLocation().getLng();
            editRequest.setNLat(lat);
            editRequest.setNLng(lng);
        }else {
            throw new SbcRuntimeException("K-060002","百度地图api未查询到经纬度请重新确认地址");
        }

        KsBeanUtil.copyProperties(editRequest, address);
        if (Objects.isNull(editRequest.getCityId())) {
            address.setCityId(0L);
            address.setAreaId(0L);
        }
        address.setNetworkId(null);
        address.setNetworkIds(null);
        address.setUpdatePerson(employeeId);
        address.setUpdateTime(LocalDateTime.now());

        Long provinceId = 420000L;
        if (Objects.equals(provinceId, address.getProvinceId()) && Objects.nonNull(address.getNLat())) {
            // 匹配 免配站点
            List<NetWorkVO> netWorkVOS = netWorkProvider.getNetWorkAllData("420000").getContext().getNetWorkVOS()
                    .stream().filter(v-> v.getDelFlag() == 0 && Objects.equals(String.valueOf(address.getAreaId()),v.getArea())).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(netWorkVOS)) {
                Double distance = null;
                for (NetWorkVO vo : netWorkVOS) {
                    Map<String,Object> parammap = new HashMap<>();
                    parammap.put("longitudeFrom",vo.getLng());//第一个点的经度
                    parammap.put("latitudeFrom",vo.getLat());//第一个点的纬度
                    parammap.put("longitudeTo",address.getNLng());//第二个点的经度
                    parammap.put("latitudeTo",address.getNLat());//第二个点的纬度
                    GetDistance context2 = netWorkProvider.getJWDistance(parammap).getContext();
                    if (context2.getDistanceResult()<vo.getDistance()){
                        if (Objects.isNull(distance) || context2.getDistanceResult() < distance) {
                            address.setNetworkId(vo.getNetworkId());
                            distance = context2.getDistanceResult();
                        }
                    }else {
                        //不满足加入networkids下次不查询
                        if (StringUtils.isNotEmpty(address.getNetworkIds())){
                            address.setNetworkIds(address.getNetworkIds() + "," + vo.getNetworkId());
                        }else {
                            address.setNetworkIds(String.valueOf(vo.getNetworkId()));
                        }
                    }
                }
            }
        }

        //保存地址表
        CustomerDeliveryAddress customerDeliveryAddress = repository.saveAndFlush(address);
        //获取写锁
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(CacheKeyConstant.NETWORK_ADRESS_DISTANCE + editRequest.getDeliveryAddressId());
        RLock rLock = readWriteLock.writeLock();
        //是否要刷新地址距离表
        // AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        try {
            rLock.lock();
            //删除网点地址距离表
            dotDistanceRepository.deleteByDeliveryAddressId(editRequest.getDeliveryAddressId());
            //删除redis缓存
            if (redisCache.hasKey(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE+editRequest.getDeliveryAddressId())){
                redisCache.delete(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE+editRequest.getDeliveryAddressId());
                // atomicBoolean.set(true);
            }
        }finally {
            rLock.unlock();
        }
        /*if (atomicBoolean.get()){
            //刷新单个
            dotDistanceService.executOneAdress(KsBeanUtil.convert(customerDeliveryAddress, CustomerDeliveryAddressVO.class));
        }*/

        if (Objects.equals(provinceId, customerDeliveryAddress.getProvinceId()) && Objects.isNull(customerDeliveryAddress.getNetworkId())){
            //刷新单个
            dotDistanceService.executOneAdress(KsBeanUtil.convert(customerDeliveryAddress, CustomerDeliveryAddressVO.class));
        }
        return customerDeliveryAddress;
    }

    /**
     * 删除客户的收货地址
     *
     * @param addressId
     */
    @Transactional
    public void deleteAddress(String addressId) {
        repository.deleteAddress(addressId);
    }

    /**
     * 设置为默认客户地址
     *
     * @param addressId
     * @param customerId
     */
    @Transactional
    public void setDefaultAddress(String addressId, String customerId) {
        //查询该客户是否设置默认地址
        CustomerDeliveryAddress address = this.getDefault(customerId);
        if (Objects.nonNull(address)) {
            //把已设为默认的地址给取消默认
            address.setIsDefaltAddress(DefaultFlag.NO);
            repository.save(address);
        }
        repository.updateDefault(addressId, customerId);
    }

    /**
     * 只查询客户默认地址
     *
     * @param customerId
     * @return
     */
    public CustomerDeliveryAddress getDefault(String customerId) {
        CustomerDeliveryAddressQueryRequest queryRequest = new CustomerDeliveryAddressQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setIsDefaltAddress(DefaultFlag.YES.toValue());
        queryRequest.setCustomerId(customerId);
        CustomerDeliveryAddress deliveryAddress = repository.findOne(queryRequest.getWhereCriteria()).orElse(null);
        return deliveryAddress;
    }

    /**
     * 查询该客户有多少条收货地址
     *
     * @param customerId
     * @return
     */
    public int countCustomerAddress(String customerId) {
        return repository.countCustomerAddress(customerId);
    }


    public Map<String, Integer> synAdrress() {
        Map<String, Integer> resultMap = new HashMap<>();
        List<CustomerDeliveryAddress> addressList = repository.selectCountCustomerAddress();
        log.info("查出可修改记录数据repository：" + addressList.size());
        if (null != addressList && !addressList.isEmpty()) {
            //https://www.youbianku.cn/api/search_street_custom.php?address=%E7%9B%8A%E9%98%B3%E5%B8%82%E8%B5%84%E9%98%B3%E5%8C%BA%E4%BA%BA%E6%B0%91%E6%94%BF%E5%BA%9C-%E8%A5%BF%E5%8D%97%E9%97%A8&key=MjAyMi0zLTIxeW91Ymlhbmt1
            String url = "https://www.youbianku.cn/api/search_street_custom.php?";
            CloseableHttpClient httpclient = HttpClients.createDefault();
            resultMap.put("error",0);
            resultMap.put("success",0);
            for (CustomerDeliveryAddress address : addressList){
                try{
                    Thread.sleep(100);
                    //都为空的情况下
                    if (address.getTwonId() == null) {
                        List<Long> city = new ArrayList<>();
                        city.add(address.getProvinceId());
                        city.add(address.getCityId());
                        city.add(address.getAreaId());
                        List<RegionCopyVO> responseResults  = regionQueryProvider.queryRegionCopyNumber(RegionQueryResponse.builder().number(city).build()).getContext();
                        StringBuffer consigneeProvince = new StringBuffer();
                        for (RegionCopyVO regionVO :responseResults) {
                            consigneeProvince.append(regionVO.getName());
                        }
                        if(responseResults.toString() == null || responseResults.toString().equals("")){
                            repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                            continue;
                        }
                        String str = consigneeProvince.toString().replaceAll(" ", "");
                        StringBuffer sb = new StringBuffer();
                        sb.append("address=");
                        sb.append(str);
                        sb.append(address.getDeliveryAddress().replaceAll(" ", ""));
                        sb.append("&key=MjAyMi0zLTIxeW91Ymlhbmt1");
                        HttpGet httpget = new HttpGet(url + sb.toString());
                        try {
                            CloseableHttpResponse response = httpclient.execute(httpget);
                            HttpEntity entity = response.getEntity();
                            String result = EntityUtils.toString(entity);
                            log.info("请求区域同步返回数据：" + result);
                            if(!StringUtils.isBlank(result)){
                                DeliveryAddressResponse deliveryAddressResponse = JSONObject.parseObject(result, DeliveryAddressResponse.class);
                                if(null != deliveryAddressResponse.getResults() && !deliveryAddressResponse.getResults().isEmpty()){
                                    String townId = String.valueOf(deliveryAddressResponse.getResults().get(0).getStreet_code());
                                    String town= deliveryAddressResponse.getResults().get(0).getStreet();
                                    log.info("执行同步操作townId：" + townId + " town:" + town + " id:" + address.getDeliveryAddressId());
                                    if(!StringUtils.isBlank(townId) && !StringUtils.isBlank(town)){
                                        townId = townId.substring(0,townId.length() - 3);
                                        log.info("执行同步操作1111townId：" + townId + " town:" + town);
                                        repository.updateTwon(address.getDeliveryAddressId(), address.getCustomerId(), Long.valueOf(townId), town, 1);
                                    }else{
                                        repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                                    }
                                }else{
                                    repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                                }
                            }else{
                                repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                            }

                        } catch (Exception e) {
                            repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                            resultMap.put("error",resultMap.get("error") + 1);
                            e.printStackTrace();
                        }
                    }else{
                        repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                    }
                }catch (Exception e){
                    repository.updateTwonDelivery(address.getDeliveryAddressId(), address.getCustomerId(), 1 );
                    e.printStackTrace();
                }
            }
        }
        return resultMap;
    }

    public static void main(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("湖南省益阳市资阳区益阳市资阳区人 民政府-西南门&key=MjAyMi0zLTIxeW91Ymlhbmt1");
        String str = sb.toString().replaceAll(" ", "");
        System.out.println(str);
       /* String url = "https://www.youbianku.cn/api/search_street_custom.php?";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringBuffer sb = new StringBuffer();
        sb.append("address=");
        sb.append("益阳市资阳区人民政府-西南门");
        sb.append("&key=MjAyMi0zLTIxeW91Ymlhbmt1");
        HttpGet httpget = new HttpGet(url + sb.toString());
        try{
            CloseableHttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
//        }*/
//        String str = "430902401000";
//        System.out.println(str.substring(0, str.length() - 3));
    }


    public List<CustomerDeliveryAddressVO> getAdressDataByProvinceId(String provinceId){
        List<Object> adressDataByProvinceId = repository.getAdressDataByProvinceId(provinceId);
        List<CustomerDeliveryAddressVO> collect = adressDataByProvinceId.stream().map(v -> {
            return convertFromNativeSQLResult(v);
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 从数据库实体转换为返回前端的用户信息
     * (字段顺序不可变)
     */
    public static CustomerDeliveryAddressVO convertFromNativeSQLResult(Object result) {
        CustomerDeliveryAddressVO response = new CustomerDeliveryAddressVO();
        response.setDeliveryAddress((String) ((Object[]) result)[0]);
        response.setDeliveryAddressId((String) ((Object[]) result)[1]);

        return response;
    }


}
