package com.wanmi.sbc.job;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressProvider;
import com.wanmi.sbc.goods.GoodsController;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoCountByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.job.service.CheckInventoryHandlerService;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckInventory
 * @Description: TODO
 * @Date: 2020/5/30 10:25
 * @Version: 1.0
 */
@JobHandler(value = "customerDeliveryAddressHandler")
@Component
@Slf4j
public class CustomerDeliveryAddressHandler extends IJobHandler {


    @Autowired
    private CustomerDeliveryAddressProvider customerDeliveryAddressProvider;

    private Logger logger = LoggerFactory.getLogger(CustomerDeliveryAddressHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        customerDeliveryAddressProvider.synAdrress();
        return SUCCESS;
    }
}
