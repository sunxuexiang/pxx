package com.wanmi.sbc.goods.customerarealimitdetail.Service;

import com.wanmi.sbc.goods.customerarealimitdetail.model.root.CustomerAreaLimitDetail;
import com.wanmi.sbc.goods.customerarealimitdetail.repository.CustomerAreaLimitDetailRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class CustomerAreLimitDetailService {
    @Autowired
    private CustomerAreaLimitDetailRepository customerAreaLimitDetailRepository;


    /**
     * 查询某用户某商品某地区当日购买数量
     *
     * @param customerId
     * @param goodsInfoId
     * @param regionId
     * @param tid
     */
    public List<CustomerAreaLimitDetail> getbycustomerIdAndGoodsInfoidAndRegionId(String customerId, String goodsInfoId, List<Long> regionId, String tid) {
        if (StringUtils.isBlank(tid)) {
            return customerAreaLimitDetailRepository.getbycustomerIdAndGoodsInfoidAndRegionId(customerId, goodsInfoId, regionId);
        }
        return customerAreaLimitDetailRepository.getbycustomerIdAndGoodsInfoidAndRegionIdAAndTradeIdNot(customerId, goodsInfoId, regionId, tid);
    }


}
