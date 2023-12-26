package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.ReplenishmentFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageProvider;
import com.wanmi.sbc.goods.api.provider.stockoutmanage.StockoutManageQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockListRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageListRequest;
import com.wanmi.sbc.goods.api.request.stockoutmanage.StockoutManageModifyRequest;
import com.wanmi.sbc.goods.api.response.goodswarestock.GoodsWareStockListResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.stockoutmanage.StockoutManageListResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.StockoutManageVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 缺货管理统计任务
 *
 * @author liujing 2022-07-23
 */
@JobHandler(value = "storeOutManageJobHandler")
@Component
@Slf4j
public class StoreOutManageJobHandler extends IJobHandler {

    private Logger logger = LoggerFactory.getLogger(StoreOutManageJobHandler.class);

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private StockoutManageQueryProvider stockoutManageQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private StockoutManageProvider stockoutManageProvider;

    @Autowired
    private AuditQueryProvider auditQueryProvider;


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();
        logger.info("商品缺货管理统计定时任务开始时间: {}", localDateTime);

        //查询表数据
        StockoutManageListRequest request = new StockoutManageListRequest();
        request.setReplenishmentFlagList(Lists.newArrayList(ReplenishmentFlag.NO, ReplenishmentFlag.NOT_ALERT));
        request.setDelFlag(DeleteFlag.NO);
        request.setSource(2);

        BaseResponse<StockoutManageListResponse> stockOutResponse = stockoutManageQueryProvider.list(request);

        if (CollectionUtils.isEmpty(stockOutResponse.getContext().getStockoutManageVOList())) {
            logger.info("商品缺货管理统计定时任务查询数据为空结束处理，结束时间:{}", LocalDateTime.now());
            return SUCCESS;
        }
        logger.info("商品缺货管理统计定时任务查询到数据:{} 条数据", stockOutResponse.getContext().getStockoutManageVOList().size());
        List<StockoutManageVO> stockOutList = stockOutResponse.getContext().getStockoutManageVOList();

        //获取缺货管理所有的商品id
        List<String> goodInfoIds = stockOutList.stream().map(x -> x.getGoodsInfoId()).collect(Collectors.toList());

        GoodsWareStockByGoodsForIdsRequest goodsWareStockByGoodsForIdsRequest = new GoodsWareStockByGoodsForIdsRequest();
        goodsWareStockByGoodsForIdsRequest.setGoodsForIdList(goodInfoIds);
        //通过缺货管理表的商品id查询商品表信息
        BaseResponse<GoodsWareStockListResponse> goodsWareStockResponse = goodsWareStockQueryProvider.findByGoodsInfoIdIn(goodsWareStockByGoodsForIdsRequest);

        if (CollectionUtils.isEmpty(goodsWareStockResponse.getContext().getGoodsWareStockVOList())) {
            logger.info("商品缺货管理通过商品goodInfoIds 查询GoodsWareStockListResponse数据为空，结束时间:{}", LocalDateTime.now());
            return SUCCESS;
        }

        List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockResponse.getContext().getGoodsWareStockVOList();
        List<String> goodsWareStockIds = goodsWareStockVOList.stream().map(x -> x.getGoodsInfoId()).collect(Collectors.toList());

        long orderTimeout = this.getOrderTimeout();

        //对比当前记录是否已经补货完全
        for (StockoutManageVO stockoutManageVO : stockOutList) {
            LocalDateTime localDate = LocalDateTime.now();
            GoodsInfoVO goodsInfoVO = this.getAddedFlag(stockoutManageVO.getGoodsInfoId());
            if (goodsInfoVO == null) {
                log.info("商品缺货管理通过GoodsInfoId查询为空，处理StockoutID:{} GoodsInfoNo:{} 数据耗时:{}", stockoutManageVO.getStockoutId(), stockoutManageVO.getGoodsInfoNo(), (LocalDateTime.now().getNano() - localDate.getNano()) / (1000 * 1000));
                continue;
            }
            //最新的上下架状态为下架，并且 缺货记录也是下架则不需要做任何计算
            if (goodsInfoVO.getAddedFlag() == AddedFlag.NO.toValue() &&
                    stockoutManageVO.getAddedFlag() == AddedFlag.NO) {
                log.info("已下架不做处理，处理StockoutID:{} GoodsInfoNo:{} 数据耗时:{}", stockoutManageVO.getStockoutId(), stockoutManageVO.getGoodsInfoNo(), (LocalDateTime.now().getNano() - localDate.getNano()) / (1000 * 1000));
                continue;
            }
            StockoutManageModifyRequest modifyRequest = KsBeanUtil.convert(stockoutManageVO, StockoutManageModifyRequest.class);

            if (goodsWareStockIds.contains(stockoutManageVO.getGoodsInfoId())) {
                this.completeProcess(stockoutManageVO, modifyRequest, orderTimeout, goodsInfoVO);
            } else {
                //不包含时计算缺货天数
                this.dayProcess(stockoutManageVO, modifyRequest, goodsInfoVO);
            }
            log.info("处理StockoutID:{} GoodsInfoNo:{} 数据耗时:{}", stockoutManageVO.getStockoutId(), stockoutManageVO.getGoodsInfoNo(), (LocalDateTime.now().getNano() - localDate.getNano()) / (1000 * 1000));
        }

        logger.info("商品缺货管理统计定时任务结束,共耗时: {} 毫秒", (LocalDateTime.now().getNano() - localDateTime.getNano()) / (1000 * 1000));
        return SUCCESS;
    }

    /**
     * 获取订单自动超时时间
     *
     * @return
     */
    private long getOrderTimeout() {
        //查询订单自动超时时间
        TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_TIMEOUT_CANCEL);
        BaseResponse<TradeConfigGetByTypeResponse> baseResponse = auditQueryProvider.getTradeConfigByType(request);
        if (baseResponse == null ||
                baseResponse.getContext() == null) {
            return 15;
        }

        //已补货时间在订单超时时间范围之内，数据记录设置为delete
        //{"hour":15}
        JSONObject jsonObject = JSON.parseObject(baseResponse.getContext().getContext());
        log.info("订单超时json：{}", baseResponse.getContext().getContext());
        Long orderTimeout = jsonObject.getLong("hour");
        return orderTimeout;
    }

    private void orderTimeoutCheck(long orderTimeout, StockoutManageVO stockoutManageVO, StockoutManageModifyRequest modifyRequest) {


        LocalDateTime localDateTime = LocalDateTime.now();
        long dataMinutes = stockoutManageVO.getCreateTime().until(localDateTime, ChronoUnit.MINUTES);

        //X分钟之内已补齐的订单检查订单超时时间，满足条件缺货记录数据设置为del
        if (orderTimeout > dataMinutes) {
            modifyRequest.setDelFlag(DeleteFlag.YES);
            log.info("商品缺货管理在订单自动超时{}分钟内补货完成，该数据设置删除标识,stockoutId:{}", dataMinutes, stockoutManageVO.getStockoutId());
        }
    }

    /**
     * 能匹配上库存数据场景
     *
     * @param vo
     * @param modifyRequest
     */
    private void completeProcess(StockoutManageVO vo, StockoutManageModifyRequest modifyRequest, long orderTimeout, GoodsInfoVO goodsInfoVO) {
        log.info("商品缺货管理进入completeProcess方法，stockoutId：{}", vo.getStockoutId());
        //根据商品id与仓库id查询
        BaseResponse<GoodsWareStockListResponse> list = goodsWareStockQueryProvider.list(GoodsWareStockListRequest.builder().goodsInfoId(vo.getGoodsInfoId()).wareId(vo.getWareId()).build());
        if (list == null ||
                list.getContext() == null ||
                CollectionUtils.isEmpty(list.getContext().getGoodsWareStockVOList())) {
            log.warn("商品缺货管理通过商品id和仓库id查询数据为空:GoodsInfoId:{},wareId{}", vo.getGoodsInfoId(), vo.getWareId());
        }
        List<GoodsWareStockVO> goodsWareStockVOS = list.getContext().getGoodsWareStockVOList();
        Optional<GoodsWareStockVO> first = goodsWareStockVOS.stream().findFirst();

        //库存大于0时设置已补齐与补齐时间
        if (first.get().getStock().compareTo(new BigDecimal(0)) > 0) {
            log.info("商品缺货管理进入completeProcess方法商品库存大于0，stockoutId：{}", vo.getStockoutId());
            modifyRequest.setReplenishmentFlag(ReplenishmentFlag.YES);
            modifyRequest.setReplenishmentTime(LocalDateTime.now());
            this.check72Hours(vo.getStockoutTime(), modifyRequest);
        } else {
            log.info("商品缺货管理进入completeProcess方法商品库存小于0，stockoutId：{}", vo.getStockoutId());
            //重新计算缺货时间
            Long calculateDay = this.calculateDay(vo.getStockoutTime());
            modifyRequest.setStockoutDay(calculateDay);
            ReplenishmentFlag flag = this.getReplenishmentStatus(calculateDay);
            modifyRequest.setReplenishmentFlag(flag);
        }

        this.checkAndSetAddedFlag(goodsInfoVO, modifyRequest);
        stockoutManageProvider.modify(modifyRequest);
    }

    /**
     * 缺货提醒72小时以内补货完成的做del处理
     *
     * @param stockoutTime  缺货记录创建时间
     * @param modifyRequest 待修改的对象
     */
    private void check72Hours(LocalDateTime stockoutTime, StockoutManageModifyRequest modifyRequest) {
        Long day = this.calculateDay(stockoutTime);
        if (day <= 3) {
            modifyRequest.setDelFlag(DeleteFlag.YES);
            log.info("商品缺货管理在{}天内补货完成，该数据设置删除标识,stockoutId:{}", day, modifyRequest.getStockoutId());
        }
    }

    private GoodsInfoVO getAddedFlag(String goodsInfoId) {
        BaseResponse<GoodsInfoListByConditionResponse> baseResponse = goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder().goodsInfoId(goodsInfoId).build());
        if (baseResponse == null ||
                baseResponse.getContext() == null ||
                CollectionUtils.isEmpty(baseResponse.getContext().getGoodsInfos())) {
            return null;
        }
        GoodsInfoVO goodsInfoVO = baseResponse.getContext().getGoodsInfos().stream().findFirst().get();
        return goodsInfoVO;
    }

    /**
     * 检查上下架状态是否有变更
     *
     * @param goodsInfoVO
     * @param modifyRequest
     */
    private void checkAndSetAddedFlag(GoodsInfoVO goodsInfoVO, StockoutManageModifyRequest modifyRequest) {
        if (goodsInfoVO.getAddedFlag() == modifyRequest.getAddedFlag().toValue()) {
            return;
        }

        modifyRequest.setAddedFlag(AddedFlag.get(goodsInfoVO.getAddedFlag()));
        //下架时重新使用下架时间来计算缺货天数
        if (goodsInfoVO.getAddedFlag() == AddedFlag.NO.toValue()) {
            modifyRequest.setStockoutDay(this.calculateDay(modifyRequest.getStockoutTime(), goodsInfoVO.getAddedTime()));
        }
    }


    /**
     * 未补货情况
     *
     * @param vo
     * @param modifyRequest
     */
    private void dayProcess(StockoutManageVO vo, StockoutManageModifyRequest modifyRequest, GoodsInfoVO goodsInfoVO) {
        log.info("商品缺货管理进入dayProcess方法stockoutId：{}", vo.getStockoutId());
        Long day = this.calculateDay(vo.getStockoutTime());
        log.info("商品缺货管理进入dayProcess方法计算的day{},stockoutId：{}", day, vo.getStockoutId());
        ReplenishmentFlag flag = this.getReplenishmentStatus(day);
        log.info("商品缺货管理进入dayProcess方法计算的flag{}，stockoutId：{}", flag, vo.getStockoutId());
        //缺货管理表中 缺货状态与缺货天数相等时不做处理
        if (vo.getReplenishmentFlag() == flag &&
                vo.getStockoutDay() == day) {
            return;
        }

        modifyRequest.setReplenishmentFlag(flag);
        modifyRequest.setStockoutDay(day);
        this.checkAndSetAddedFlag(goodsInfoVO, modifyRequest);
        stockoutManageProvider.modify(modifyRequest);
    }

    /**
     * 缺货提醒状态（该状态从开始缺货则进行记录）只记录72小时以内商品
     * 缺货0-24小时显示缺货1天，缺货24-48小时显示缺货2天，48-72小时显示缺货3天
     * 暂未补齐状态-缺货72-96小时显示缺货4天，按此规则每增加24小时递增1天
     *
     * @param dataTime
     * @return 天数
     */
    private Long calculateDay(LocalDateTime dataTime) {
        LocalDateTime localDateTime = LocalDateTime.now();
        long minutes = dataTime.until(localDateTime, ChronoUnit.MINUTES);

        if (minutes <= 24 * 60) {
            return 1L;
        } else if (minutes > 24 * 60 && minutes <= 48 * 60) {
            return 2L;
        } else if (minutes > 48 * 60 && minutes <= 72 * 60) {
            return 3L;
        } else if (minutes > 72 * 60 && minutes <= 96 * 60) {
            return 4L;
        } else if (minutes > 96 * 60) {
            return new BigDecimal(minutes).divide(new BigDecimal(24 * 60), RoundingMode.CEILING).longValue();
        }
        return 1L;
    }

    private Long calculateDay(LocalDateTime stockTime, LocalDateTime addflagTime) {
        long minutes = stockTime.until(addflagTime, ChronoUnit.MINUTES);

        if (minutes <= 24 * 60) {
            return 1L;
        } else if (minutes > 24 * 60 && minutes <= 48 * 60) {
            return 2L;
        } else if (minutes > 48 * 60 && minutes <= 72 * 60) {
            return 3L;
        } else if (minutes > 72 * 60 && minutes <= 96 * 60) {
            return 4L;
        } else if (minutes > 96 * 60) {
            return new BigDecimal(minutes).divide(new BigDecimal(24 * 60), RoundingMode.CEILING).longValue();
        }
        return 1L;
    }

    /**
     * 缺货提醒状态（该状态从开始缺货则进行记录）只记录72小时以内商品
     * 缺货0-24小时显示缺货1天，缺货24-48小时显示缺货2天，48-72小时显示缺货3天
     * 暂未补齐状态-缺货72-96小时显示缺货4天，按此规则每增加24小时递增1天
     *
     * @param day
     * @return
     */
    private ReplenishmentFlag getReplenishmentStatus(Long day) {
        if (day * 24 <= 24) {
            return ReplenishmentFlag.NOT_ALERT;
        } else if (day * 24 > 72) {
            return ReplenishmentFlag.NO;
        }
        return ReplenishmentFlag.NOT_ALERT;
    }

    private GoodsWareStockVO getGoodsWareStock(String goodsInfoId, long wareId, List<GoodsWareStockVO> goodsWareStockList) {
        for (GoodsWareStockVO goodsWareStockVO : goodsWareStockList) {
            if (goodsInfoId.equals(goodsWareStockVO.getGoodsInfoId())
                    && (null != goodsWareStockVO.getWareId() && goodsWareStockVO.getWareId().longValue() == wareId)
                    && goodsWareStockVO.getDelFlag().name().equals(DeleteFlag.NO.name())
            ) {
                return goodsWareStockVO;
            }
        }
        return null;
    }

}
