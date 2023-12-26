package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.request.address.UpdateCustomerDeliveryAddressByProAndNetWorkRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.api.response.baiduBean.GetDistance;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 加定时任务 判断湖北地址是否满足免店配条件
 */
@JobHandler(value = "matchingNetwork")
@Component
@Slf4j
public class matchingNetworkHandler extends IJobHandler {

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;
    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;

    @Autowired
    private NetWorkProvider netWorkProvider;
    @Autowired
    private RedissonClient redissonClient;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        //获取湖北所有网点的经纬度地址
        String networkStopF = CacheKeyConstant.NETWORK_STOP_F;
        RLock fairLock = redissonClient.getFairLock(networkStopF);
        fairLock.lock();
        try {
            List<NetWorkVO> netWorkVOS = netWorkProvider.getNetWorkAllData("420000").getContext().getNetWorkVOS();
            if (Collections.isEmpty(netWorkVOS)){
                return SUCCESS;
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
                        List<String> copycollect = new ArrayList<>();
                        collect.forEach(pa->{
                            copycollect.add(pa);
                        });

                        if (StringUtils.isNotEmpty(v.getNetworkIds())){
                            //以及同步的网点id
                            String[] split = v.getNetworkIds().split(",");
                            List<String> list = Arrays.asList(split);
                            copycollect.removeAll(list);
                        }

                        Double dis  = null;
                        for (int w = 0; w<copycollect.size();w++){
                            String q = copycollect.get(w);
                            if (collect.contains(q)){
                                NetWorkVO netWorkVO = collect1.get(Long.valueOf(q));
                                if (StringUtils.isNotEmpty(netWorkVO.getArea()) && Objects.nonNull(v.getAreaId()) && netWorkVO.getArea().equals(v.getAreaId().toString())) {
                                    //获取2个经纬度的距离
                                    Map<String,Object> parammap = new HashMap<>();
                                    parammap.put("longitudeFrom",netWorkVO.getLng());//第一个点的经度
                                    parammap.put("latitudeFrom",netWorkVO.getLat());//第一个点的纬度
                                    parammap.put("longitudeTo",v.getNLng());//第二个点的经度
                                    parammap.put("latitudeTo",v.getNLat());//第二个点的纬度
                                    GetDistance context2 = netWorkProvider.getJWDistance(parammap).getContext();
                                    if (context2.getDistanceResult()<netWorkVO.getDistance()){
                                        //说明满足
                                        if (Objects.isNull(dis) || context2.getDistanceResult() < dis) {
                                            v.setNetworkId(Long.valueOf(q));
                                            dis = context2.getDistanceResult();
                                        }

                                    }else {
                                        //不满足加入networkids下次不查询
                                        if (StringUtils.isNotEmpty(v.getNetworkIds())){
                                            v.setNetworkIds(v.getNetworkIds()+","+q);
                                        }else {
                                            v.setNetworkIds(q);
                                        }
                                    }
                                }else {
                                    //不满足加入networkids下次不查询
                                    if (StringUtils.isNotEmpty(v.getNetworkIds())){
                                        v.setNetworkIds(v.getNetworkIds()+","+q);
                                    }else {
                                        v.setNetworkIds(q);
                                    }
                                }

                            }
                        }

                        //保存客户地址表
                        customerDeliveryAddressProvider.modifyNetWorkid(UpdateCustomerDeliveryAddressByProAndNetWorkRequest.builder()
                                .customerDeliveryAddressVO(v).build());
                    });
                }
            }
        }finally {
            fairLock.unlock();
        }

        return SUCCESS;
    }


}
