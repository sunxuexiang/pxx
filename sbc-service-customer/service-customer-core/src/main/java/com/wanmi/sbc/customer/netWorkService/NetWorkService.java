package com.wanmi.sbc.customer.netWorkService;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.*;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.*;
import com.wanmi.sbc.customer.CustomerDetailQueryRequest;
import com.wanmi.sbc.customer.account.repository.CustomerAccountRepository;
import com.wanmi.sbc.customer.address.model.root.CustomerDeliveryAddress;
import com.wanmi.sbc.customer.address.repository.CustomerDeliveryAddressRepository;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.constant.EmployeeErrorCode;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.request.address.UpdateCustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.request.customer.*;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaQueryRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.WorkOrderQueryRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.api.response.baiduBean.GetDistance;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.api.response.customer.CustomerAddResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailPageForSupplierResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerDetailPageResponse;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetForSupplierResponse;
import com.wanmi.sbc.customer.api.response.employee.EmployeeAccountResponse;
import com.wanmi.sbc.customer.ares.CustomerAresService;
import com.wanmi.sbc.customer.bean.dto.CustomerVerifyRelaDTO;
import com.wanmi.sbc.customer.bean.enums.*;
import com.wanmi.sbc.customer.bean.vo.*;
import com.wanmi.sbc.customer.company.model.root.CompanyInfo;
import com.wanmi.sbc.customer.company.service.CompanyInfoService;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.repository.CustomerDetailRepository;
import com.wanmi.sbc.customer.distribution.service.DistributionCustomerService;
import com.wanmi.sbc.customer.dotdistance.repository.DotDistanceRepository;
import com.wanmi.sbc.customer.dotdistance.service.DotDistanceService;
import com.wanmi.sbc.customer.employee.model.root.Employee;
import com.wanmi.sbc.customer.employee.service.EmployeeService;
import com.wanmi.sbc.customer.enterpriseinfo.model.root.EnterpriseInfo;
import com.wanmi.sbc.customer.enterpriseinfo.service.EnterpriseInfoService;
import com.wanmi.sbc.customer.growthvalue.service.GrowthValueIncreaseService;
import com.wanmi.sbc.customer.invoice.model.entity.CustomerInvoiceQueryRequest;
import com.wanmi.sbc.customer.invoice.repository.CustomerInvoiceRepository;
import com.wanmi.sbc.customer.level.model.root.CustomerLevel;
import com.wanmi.sbc.customer.level.service.CustomerLevelService;
import com.wanmi.sbc.customer.model.root.Customer;
import com.wanmi.sbc.customer.model.root.CustomerBase;
import com.wanmi.sbc.customer.mq.ProducerService;
import com.wanmi.sbc.customer.netWorkService.model.ReturnLocationBeanVO;
import com.wanmi.sbc.customer.netWorkService.model.root.NetWork;
import com.wanmi.sbc.customer.netWorkService.repository.NetWorkRepository;
import com.wanmi.sbc.customer.netWorkService.request.NetWorkQueryRequest;
import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import com.wanmi.sbc.customer.parentcustomerrela.service.ParentCustomerRelaService;
import com.wanmi.sbc.customer.points.model.root.CustomerPointsDetail;
import com.wanmi.sbc.customer.points.service.CustomerPointsDetailService;
import com.wanmi.sbc.customer.redis.RedisCache;
import com.wanmi.sbc.customer.repository.CustomerRepository;
import com.wanmi.sbc.customer.service.CustomerSiteService;
import com.wanmi.sbc.customer.sms.SmsSendUtil;
import com.wanmi.sbc.customer.storecustomer.repository.StoreCustomerRepository;
import com.wanmi.sbc.customer.storecustomer.root.StoreCustomerRela;
import com.wanmi.sbc.customer.storelevel.model.entity.StoreLevelQueryRequest;
import com.wanmi.sbc.customer.storelevel.model.root.StoreLevel;
import com.wanmi.sbc.customer.storelevel.repository.StoreLevelRepository;
import com.wanmi.sbc.customer.storelevel.service.StoreLevelService;
import com.wanmi.sbc.customer.util.QueryConditionsUtil;
import com.wanmi.sbc.customer.util.SafeLevelUtil;
import com.wanmi.sbc.customer.workorder.model.root.WorkOrder;
import com.wanmi.sbc.customer.workorder.service.WorkOrderService;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 会员服务接口
 * Created by CHENLI on 2017/4/19.
 */
@Service
@Transactional
@Slf4j
public class NetWorkService {



    /**
     * 百度开放平台超级喜吖吖应用的AK KEY
     */
    final static String AK = "FqLyzbOkIFlRk0N4r8m9UCzxL8WM7KIL";


    /**
     * 地理编码 URL
     */
    final static String ADDRESS_TO_LONGITUDEA_URL = "http://api.map.baidu.com/geocoding/v3/?output=json&location=showLocation";


    @Autowired
    private NetWorkRepository netWorkRepository;

    @Autowired
    private CustomerDeliveryAddressRepository customerDeliveryAddressRepository;

    @Autowired
    private DotDistanceRepository dotDistanceRepository;

    @Autowired
    private DotDistanceService dotDistanceService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 地理编码
     * @param address  (广东省广州市黄埔区)
     *         详细的位置信息
     * @return
     */
    public ReturnLocationBean AddressTolongitudea(String address) {
        if (StringUtils.isBlank(address)) {
            return null;
        }
        address=address.replaceAll("\\s+", "");
        if (address.length()>68){
            address=address.substring(0,68);
        }
        address = URLEncoder.encode(address);


        String url = ADDRESS_TO_LONGITUDEA_URL + "&ak=" + AK + "&address=" + address;
        HttpClient client = HttpClients.createDefault(); // 创建默认http连接
        HttpPost post = new HttpPost(url);// 创建一个post请求
        try {
            HttpResponse response = client.execute(post);// 用http连接去执行get请求并且获得http响应
            HttpEntity entity = response.getEntity();// 从response中取到响实体
            String html = EntityUtils.toString(entity);// 把响应实体转成文本

            return JSON.parseObject(html, ReturnLocationBean.class);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 根据经纬度，计算两点间的距离
     * @param longitudeFrom  第一个点的经度
     * @param latitudeFrom  第一个点的纬度
     * @param longitudeTo 第二个点的经度
     * @param latitudeTo  第二个点的纬度
     * @return 返回距离 单位米
     */
    public   double getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
        log.info("查询经纬度地址入参"+longitudeFrom+","+latitudeFrom+","+longitudeTo+","+latitudeTo);
        GlobalCoordinates source = new GlobalCoordinates(latitudeFrom, longitudeFrom);
        GlobalCoordinates target = new GlobalCoordinates(latitudeTo, longitudeTo);
        return new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
    }

    /**
     * 根据经纬度，计算两点间的距离
     * @param longitudeFrom  第一个点的经度
     * @param latitudeFrom  第一个点的纬度
     * @param longitudeTo 第二个点的经度
     * @param latitudeTo  第二个点的纬度
     * @param accurate  保留小数点几位
     * @return 返回距离 单位千米
     */
    public   double getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo,int accurate) {
        double distance = getDistance(longitudeFrom, latitudeFrom, longitudeTo, latitudeTo);
        if (accurate < 0) {
            throw new RuntimeException("精确度必须是正整数或零");
        }
        return new BigDecimal(distance).divide(new BigDecimal(1000),accurate, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 通过动态条件查询网点信息（用于运营后台查询）
     * @param netWorkQueryRequest
     * @return
     */
    public List<NetWork> qureyNetWorkInfo(NetWorkQueryRequest netWorkQueryRequest){
        List<NetWork> all = netWorkRepository.findAll(netWorkQueryRequest.getWhereCriteria());
        return all;
    }

    /**
     * 查询Page
     * @param netWorkQueryRequest
     * @return
     */
    public Page<NetWork> findAll(NetWorkQueryRequest netWorkQueryRequest){
        log.info("查询条件"+JSON.toJSONString(netWorkQueryRequest.getWhereCriteria()));
        log.info("查询分页条件"+JSON.toJSONString(netWorkQueryRequest.getPageRequest()));
        Page<NetWork> all = netWorkRepository.findAll(netWorkQueryRequest.getWhereCriteria(), netWorkQueryRequest.getPageRequest());
        return all;
    }


    /**
     * 批量停用网点
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNetWorkByNetworkIds (List<Long> list){

        String networkStopF = CacheKeyConstant.NETWORK_STOP_F;
        RLock fairLock = redissonClient.getFairLock(networkStopF);
        fairLock.lock();
        try {

            //停用网点将network表的deletflag改1
            netWorkRepository.deleteNetWorkByNetworkIds(list);

            //将用户地址表 network_id 改成 null
            customerDeliveryAddressRepository.updateNetworkId(list);
            String province = "420000";
            for (Long id : list) {
                NetWork netWork = netWorkRepository.getOne(id);
                if (Objects.nonNull(netWork) && StringUtils.equals(province, netWork.getProvince())) {
                    extracted(KsBeanUtil.convert(netWork, NetWorkVO.class));
                }

            }

            //删除dot_distance表的networkid = list的
            dotDistanceRepository.deleteByNetWorkIds(list);

            //清空缓存
            Set<String> keys = redisCache.keys(null);
            redisCache.delete(keys);

        }finally {
            fairLock.unlock();
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNetWorkData(NetWorkVO netWorkVO) {
        String province = "420000";
        if (StringUtils.equals(province, netWorkVO.getProvince())) {
            customerDeliveryAddressRepository.updateNetworkIdByArea(Long.valueOf(netWorkVO.getArea()));

            extracted(netWorkVO);

            // 更新湖北不满足免配网点 自提站点 数据
            dotDistanceRepository.deleteAll();

            //清空缓存
            Set<String> keys = redisCache.keys(null);
            redisCache.delete(keys);
        }
    }

    public void extracted(NetWorkVO netWorkVO) {
        // 更新同区域免配网点
        String networkStopF = CacheKeyConstant.NETWORK_STOP_F;
        RLock fairLock = redissonClient.getFairLock(networkStopF);
        fairLock.lock();
        try {
            List<NetWork> netWork = netWorkRepository.getNeedQuryData("420000")
                    .stream().filter(o -> o.getDelFlag() == 0 && Objects.equals(netWorkVO.getArea(), o.getArea())).collect(toList());

            List<CustomerDeliveryAddress> addressList = customerDeliveryAddressRepository.getDataByAreaId(Long.valueOf(netWorkVO.getArea()));

            for (CustomerDeliveryAddress address : addressList) {

                List<String> excludeIds = new ArrayList<>();
                if (StringUtils.isNotEmpty(address.getNetworkIds())){
                    String[] split = address.getNetworkIds().split(",");
                    List<String> list = Arrays.asList(split);
                    excludeIds.addAll(list);
                }

                Double dis = null;
                for (NetWork work : netWork) {
                    if (!excludeIds.contains(work.getNetworkId().toString())) {
                        double distance = this.getDistance(work.getLng().doubleValue(), work.getLat().doubleValue(), address.getNLng().doubleValue(), address.getNLat().doubleValue());
                        if (distance < work.getDistance()) {
                            if (Objects.isNull(dis) || distance < dis) {
                                address.setNetworkId(work.getNetworkId());
                                dis = distance;
                            }
                        } else {
                            if (StringUtils.isNotEmpty(address.getNetworkIds())) {
                                address.setNetworkIds(address.getNetworkIds() + "," + work.getNetworkId());
                            } else {
                                address.setNetworkIds(String.valueOf(work.getNetworkId()));
                            }
                        }
                    }
                }
                customerDeliveryAddressRepository.saveAndFlush(address);
            }

        }finally {
            fairLock.unlock();
        }
    }
}
