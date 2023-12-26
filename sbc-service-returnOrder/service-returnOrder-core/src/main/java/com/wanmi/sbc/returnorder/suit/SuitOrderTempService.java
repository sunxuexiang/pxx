package com.wanmi.sbc.returnorder.suit;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.suit.request.SuitOrderTempAddRequest;
import com.wanmi.sbc.returnorder.suit.request.SuitOrderTempEditRequest;
import com.wanmi.sbc.returnorder.suit.request.SuitOrderTempQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class SuitOrderTempService {

    @Autowired
    private SuitOrderTempRepository suitOrderTempRepository;

    /**
     * 新增套装订单数据
     */
    public void addSuitOrderTemp(SuitOrderTempAddRequest request){

        if(Objects.isNull(request)){
            return;
        }

        SuitOrderTemp suitOrderTemp = new SuitOrderTemp();

        suitOrderTemp.setSuitBuyNum(request.getSuitBuyNum());
        suitOrderTemp.setOrderCode(request.getOrderCode());
        suitOrderTemp.setCustomerId(request.getCustomerId());
        suitOrderTemp.setMarketingId(request.getMarketingId());
        suitOrderTemp.setCreateTime(LocalDateTime.now());

        suitOrderTempRepository.save(suitOrderTemp);
    }

    /**
     * 修改套装订单数据
     */
    public void updateSuitOrderTemp(SuitOrderTempEditRequest request){

        if(Objects.isNull(request)){
            return;
        }
        SuitOrderTemp suitOrderTemp = KsBeanUtil.convert(request, SuitOrderTemp.class);
        suitOrderTempRepository.saveAndFlush(suitOrderTemp);
    }

    /**
     * 根据订单号查询套装订单数据
     */
    public SuitOrderTemp getSuitOrderTempByOrdercode(SuitOrderTempQueryRequest request){

        if(Objects.isNull(request.getOrderCode())){
            return null;
        }

        return suitOrderTempRepository.queryFirstByOrderCode(request.getOrderCode());
    }

    /**
     * 查询用户当前套装已购买数
     */
    public Integer getSuitBuyCountByCustomerAndMarketingId(SuitOrderTempQueryRequest request){
        return suitOrderTempRepository.getSuitOrderTempsByCustomerIdAndMarketingId(request.getCustomerId(), request.getMarketingId());
    }
}
