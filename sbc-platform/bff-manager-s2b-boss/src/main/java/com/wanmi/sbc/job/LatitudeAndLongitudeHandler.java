package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkResponse;
import com.wanmi.sbc.redis.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 网点赋值金纬度
 */
@JobHandler(value = "latitudeAndLongitude")
@Component
@Slf4j
public class LatitudeAndLongitudeHandler extends IJobHandler {

    @Autowired
    private NetWorkProvider netWorkProvider;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        //获取未定位且是湖北省的网点 这里后续可以做成配置化目前只有湖北有
        NetWorkResponse context = netWorkProvider.getNetWorkData("420000").getContext();
        if (Collections.isEmpty(context.getNetWorkVOS())){
            return SUCCESS;
        }
        context.getNetWorkVOS().forEach(v->{
            if (Objects.nonNull(v) && Objects.nonNull(v.getNetworkAddress())){
                ReturnLocationBean context1 = netWorkProvider.getJW(v.getNetworkAddress()).getContext();
                if (context1.getStatus()==0){
                    v.setLat(context1.getResult().getLocation().getLat());
                    v.setLng(context1.getResult().getLocation().getLng());
                    netWorkProvider.updateJIngWei(v);
                }
            }
        });
        return SUCCESS;
    }


}
