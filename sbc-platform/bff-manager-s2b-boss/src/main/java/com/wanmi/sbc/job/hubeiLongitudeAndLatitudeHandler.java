package com.wanmi.sbc.job;

import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.network.NetWorkProvider;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressListResponse;
import com.wanmi.sbc.customer.api.response.baiduBean.ReturnLocationBean;
import com.wanmi.sbc.customer.api.response.netWork.NetWorkResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerDeliveryAddressVO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 湖北地址赋值经纬度
 */
@JobHandler(value = "hubeiLongitudeAndLatitude")
@Component
@Slf4j
public class hubeiLongitudeAndLatitudeHandler extends IJobHandler {

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;
    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;

    @Autowired
    private NetWorkProvider netWorkProvider;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        //获取未定位且是湖北省的网点 这里后续可以做成配置化目前只有湖北有
        CustomerDeliveryAddressListResponse context = customerDeliveryAddressQueryProvider.getDataNojw("420000").getContext();
        List<CustomerDeliveryAddressVO> customerDeliveryAddressVOList = context.getCustomerDeliveryAddressVOList();
        // logger.info("查询结果："+customerDeliveryAddressVOList);
        customerDeliveryAddressVOList=customerDeliveryAddressVOList.stream().filter(v->{
            if (StringUtils.isNotEmpty(v.getDeliveryAddress())){
                return true;
            }
                return false;
        }).collect(Collectors.toList());
        if (Collections.isEmpty(customerDeliveryAddressVOList)){
            return SUCCESS;
        }
        customerDeliveryAddressVOList.forEach(v->{
            if (Objects.nonNull(v) && Objects.nonNull(v.getDeliveryAddress())){
                ReturnLocationBean context1 = netWorkProvider.getJW(v.getDeliveryAddress()).getContext();
                if (context1.getStatus()==0){
                    //修改地址经纬度
                    v.setNLat(context1.getResult().getLocation().getLat());
                    v.setNLng(context1.getResult().getLocation().getLng());
                    customerDeliveryAddressProvider.updateJW(v);
                }

            }
        });

        return SUCCESS;
    }


}
