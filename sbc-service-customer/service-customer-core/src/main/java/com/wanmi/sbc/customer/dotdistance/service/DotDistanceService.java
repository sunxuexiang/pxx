package com.wanmi.sbc.customer.dotdistance.service;

import com.mongodb.DuplicateKeyException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.request.address.UpdateCustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.api.response.baiduBean.GetDistance;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.customer.dotdistance.model.root.DotDistance;
import com.wanmi.sbc.customer.dotdistance.repository.DotDistanceRepository;
import com.wanmi.sbc.customer.redis.RedisCache;
import com.wanmi.sbc.customer.redis.RedisService;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class DotDistanceService {

    @Autowired
    private DotDistanceRepository dotDistanceRepository;
    @Autowired
    private NetWorkProvider netWorkProvider;
    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;
    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisService redisService;
    @Autowired
    private RedisCache redisCache;



    /**
     * 批量
     */
    public void executAll(){
        //获取湖北所有网点的经纬度地址
        List<NetWorkVO> netWorkVOS = netWorkProvider.getNetWorkAllData("420000").getContext().getNetWorkVOS();
        if (Collections.isEmpty(netWorkVOS)){
            return ;
        }
        //过滤以及停用的
        netWorkVOS=netWorkVOS.stream().filter(v->{
            if (v.getDelFlag()==0){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        Map<Long, NetWorkVO> collect1 = netWorkVOS.stream().collect(Collectors.toMap(NetWorkVO::getNetworkId, Function.identity(), (a, b) -> a));
        //所有网点id集合
        List<Long> collectold =  netWorkVOS.stream().map(NetWorkVO::getNetworkId).collect(Collectors.toList()) ;
        List<String> collect = collectold.stream().map(v -> {
            return v.toString();
        }).collect(Collectors.toList());
        //获取所有湖北地址并且没有达到符合标准的网点 数量
        Integer context = customerDeliveryAddressQueryProvider.getDataByProviceNum("420000").getContext();
        Integer num = 300;//每次分页查询300条
        Integer cishu =  context/num;
        if (context%num>0){
            cishu++;
        }
        for (int i = 0 ; i<cishu ;i++){
            //获取所有湖北地址并且没有达到符合标准的网点 数据
            CustomerDeliveryAddressListResponse context1 = customerDeliveryAddressQueryProvider.getDataByProviceAndNetWorkId(CustomerDeliveryAddressByProAndNetWorkRequest
                    .builder()
                    .provinceId("420000")
                    .pagenum(num)
                    .page(i + 1).build()).getContext();
            if (!Collections.isEmpty(context1.getCustomerDeliveryAddressVOList())){
                context1.getCustomerDeliveryAddressVOList().forEach(v->{
                    this.execut(v,collect,collect1);
                });
            }
        }
    }

    /**
     * 单条
     * @param customerDeliveryAddressVO 用户地址表实体vo
     */
    public void executOneAdress(CustomerDeliveryAddressVO customerDeliveryAddressVO){
        //获取湖北所有网点的经纬度地址
        List<NetWorkVO> netWorkVOS = netWorkProvider.getNetWorkAllData("420000").getContext().getNetWorkVOS();
        if (Collections.isEmpty(netWorkVOS)){
            return ;
        }
        //过滤以及停用的
        netWorkVOS=netWorkVOS.stream().filter(v->{
            if (v.getDelFlag()==0){
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        Map<Long, NetWorkVO> collect1 = netWorkVOS.stream().collect(Collectors.toMap(NetWorkVO::getNetworkId, Function.identity(), (a, b) -> a));
        //所有网点id集合
        List<Long> collectold =  netWorkVOS.stream().map(NetWorkVO::getNetworkId).collect(Collectors.toList()) ;
        List<String> collect = collectold.stream().map(v -> {
            return v.toString();
        }).collect(Collectors.toList());
        this.execut(customerDeliveryAddressVO,collect,collect1);
    }


    /**
     * 执行方法
     * @param customerDeliveryAddressVO 用户地址表实体vo
     * @param collect 网点地址id集合
     * @param collect1 网点地址 hash key:id value:实体
     */
    public void execut(CustomerDeliveryAddressVO customerDeliveryAddressVO,List<String> collect, Map<Long, NetWorkVO> collect1){
        AtomicBoolean addflag = new AtomicBoolean(false);
        AtomicReference<BigDecimal> bdistnace = new AtomicReference<>(new BigDecimal(0.00));
        for (int w = 0; w<collect.size();w++){
            addflag.set(false);
            bdistnace.set(new BigDecimal(0.00));
            String q = collect.get(w);
            NetWorkVO netWorkVO = collect1.get(Long.valueOf(q));
            //判断该地址和网点是否扫描过
            String key = CacheKeyConstant.NETWORK_ADRESS_DISTANCE + customerDeliveryAddressVO.getDeliveryAddressId();
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(key);
            RLock rLock = readWriteLock.readLock();
            try {
                rLock.lock();
                Long zrank = redisCache.zrank(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE + customerDeliveryAddressVO.getDeliveryAddressId(), q);
                if (Objects.isNull(zrank)) {
                    //获取距离
                    //获取2个经纬度的距离
                    Map<String, Object> parammap = new HashMap<>();
                    parammap.put("longitudeFrom", netWorkVO.getLng());//第一个点的经度
                    parammap.put("latitudeFrom", netWorkVO.getLat());//第一个点的纬度
                    parammap.put("longitudeTo", customerDeliveryAddressVO.getNLng());//第二个点的经度
                    parammap.put("latitudeTo", customerDeliveryAddressVO.getNLat());//第二个点的纬度
                    GetDistance context2 = netWorkProvider.getJWDistance(parammap).getContext();
                    double distanceResult = context2.getDistanceResult();
                    addflag.set(true);
                    bdistnace.set(new BigDecimal(distanceResult).setScale(2, RoundingMode.HALF_UP));
                }
            } finally {
                rLock.unlock();
            }
            if (addflag.get()) {
                RLock rLock1 = readWriteLock.writeLock();
                try {
                    rLock1.lock();
                    //插入redis
                    redisCache.zadd(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE + customerDeliveryAddressVO.getDeliveryAddressId(), q, bdistnace.get().doubleValue());

                    //插入mysql
                    DotDistance build = DotDistance.builder().Distance(bdistnace.get())
                            .delFlag(0).deliveryAddressId(customerDeliveryAddressVO.getDeliveryAddressId())
                            .createTime(LocalDateTime.now())
                            .networkAddress(customerDeliveryAddressVO.getDeliveryAddress())
                            .networkId(Long.valueOf(q)).build();
                    try {
                        dotDistanceRepository.save(build);
                    } catch (Exception e) {
                        //如果有重复唯一索引异常不用管他继续
                        continue;
                    }

                } finally {
                    rLock1.unlock();
                }
            }

        }
    }

}
