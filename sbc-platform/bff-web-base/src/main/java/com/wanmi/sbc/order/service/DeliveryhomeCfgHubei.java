package com.wanmi.sbc.order.service;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.dotdistance.DotDistanceProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressByIdResponse;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import com.wanmi.sbc.order.response.DeliveryHomeFlagResponse;
import com.wanmi.sbc.redis.RedisCache;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * @ClassName DeliveryhomeCfgHubei
 * @Description TODO
 * @Author shiy
 * @Date 2023/7/6 19:06
 * @Version 1.0
 */
@Service
public class DeliveryhomeCfgHubei {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private NetWorkProvider netWorkProvider;

    @Autowired
    private DotDistanceProvider dotDistanceProvider;

    public DeliveryHomeFlagResponse getHubeiDeliveryHomeFlagResponse(String customerDeleiverAddressId, DeliveryHomeFlagResponse resultResponse, CustomerDeliveryAddressByIdResponse response) {
        //后续可改成配置化这里先写死
        if(response.getProvinceId()==420000 && Objects.isNull(response.getNetworkId())){
            resultResponse.setFlag(DefaultFlag.ZD);
            //获取去redis获取最近地址
            Set<Object> zrange = redisCache.zrange(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE + customerDeleiverAddressId, 0, -1);
            if (CollectionUtils.isEmpty(zrange)){

                dotDistanceProvider.executOne(KsBeanUtil.convert(response, CustomerDeliveryAddressVO.class));
                //再次获取
                zrange = redisCache.zrange(CacheKeyConstant.H_NETWORK_ADRESS_DISTANCE + customerDeleiverAddressId, 0, -1);
                if (CollectionUtils.isEmpty(zrange)){
                    resultResponse.setAdress("ERROR:暂时还未给您匹配最近站点请5分钟之后重试");
                    // resultResponse.setAdress("暂未匹配到合适的站点，请修改收货地址或选择‘快递到家’结算");
                }else {
                    Object o = zrange.stream().findFirst().get();
                    NetWorkResponse context = netWorkProvider.findById(Long.valueOf(o.toString())).getContext();
                    NetWorkVO netWorkVO = context.getNetWorkVOS().stream().findFirst().get();
                    resultResponse.setAdress(netWorkVO.getNetworkAddress());
                    resultResponse.setPhone(netWorkVO.getPhone());
                    resultResponse.setContacts(netWorkVO.getContacts());
                    resultResponse.setNetworkId(netWorkVO.getNetworkId());
                    resultResponse.setNetworkName(netWorkVO.getNetworkName());

                }

            }else {
                Object o = zrange.stream().findFirst().get();
                NetWorkResponse context = netWorkProvider.findById(Long.valueOf(o.toString())).getContext();
                NetWorkVO netWorkVO = context.getNetWorkVOS().stream().findFirst().get();
                resultResponse.setAdress(netWorkVO.getNetworkAddress());
                resultResponse.setPhone(netWorkVO.getPhone());
                resultResponse.setContacts(netWorkVO.getContacts());
                resultResponse.setNetworkId(netWorkVO.getNetworkId());
                resultResponse.setNetworkName(netWorkVO.getNetworkName());
            }
            return resultResponse;
        }
        else if (response.getProvinceId()==420000 && Objects.nonNull(response.getNetworkId())){
            resultResponse.setFlag(DefaultFlag.YES);
            return resultResponse;
        }
        return null;
    }
}
