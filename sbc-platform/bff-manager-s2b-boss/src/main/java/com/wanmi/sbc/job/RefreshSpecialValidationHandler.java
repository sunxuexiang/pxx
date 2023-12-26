package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StorePageRequest;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
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
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
 * 散批库存同步
 */
@JobHandler(value = "refreshSpecialValidation")
@Component
@Slf4j
public class RefreshSpecialValidationHandler extends IJobHandler {
    @Autowired
    private RedisService redisService;


    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("刷新特殊验证码........:::: {}", LocalDateTime.now());
        String verifyCode = RandomStringUtils.randomNumeric(6);
        redisService.setString(CacheKeyConstant.TSFXYY_YZM_ALL_LOGIN, verifyCode);

        return SUCCESS;
    }


}
