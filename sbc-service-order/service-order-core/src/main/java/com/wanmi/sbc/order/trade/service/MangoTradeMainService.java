package com.wanmi.sbc.order.trade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.order.trade.model.root.*;
import com.wanmi.sbc.order.trade.request.TradeQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Transactional
public class MangoTradeMainService {

    @Autowired
    private TradeService tradeService;

    @PersistenceContext
    private EntityManager entityManager;


    public void saveTrade(String beginTime, String endTime) {

        String pageBeginTime = null;
        String pageEndTime = null;

        if (StringUtils.isNotEmpty(beginTime) && StringUtils.isNotEmpty(endTime)) {
            pageBeginTime = beginTime;
            pageEndTime = endTime;
        } else {
            //未传参则同步昨天数据
            LocalDateTime localDateTime = DateUtil.parseDay(DateUtil.yesterdayDate());
            pageBeginTime = DateUtil.getDateTime(localDateTime);
            pageEndTime = DateUtil.getDateTime(localDateTime);
        }

        TradeQueryRequest requestCount = new TradeQueryRequest();
        requestCount.setBeginTime(pageBeginTime);
        requestCount.setEndTime(pageEndTime);

        long count = tradeService.countNum(requestCount.getWhereCriteria(), requestCount);
        log.info("*************************************************************************(BENGIN)");

        log.info("筛选出的mango符合数据-------------------->" + count);
        log.info("筛选出的mango符合数据-------------------->requestCount" + JSON.toJSONString(requestCount));
        for (int i = 0; i <= (count / 1000) + 1; i++) {
            TradeQueryRequest request = new TradeQueryRequest();
            request.setBeginTime(pageBeginTime);
            request.setEndTime(pageEndTime);
            request.setPageNum(i);
            request.setPageSize(1000);
            log.info("筛选出的mango符合数据-------------------->request" + JSON.toJSONString(request));
            Page<Trade> page = tradeService.page(request.getWhereCriteria(), request);
            if (CollectionUtils.isNotEmpty(page.getContent())) {
                List<Trade> trades = page.getContent();
                log.info("筛选出的mango符合数据-------------------->trades" + trades.size());
                saveToMysql(trades);
            }
        }
        log.info("*************************************************************************(END)");
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveToMysql(List<Trade> trades) {
        //主订单集合
        List<MangoTradePlus> mangoTradePlusList = new ArrayList<>();
        //订单附表集合
        List<MangoTradeMain> mangoTradeMainList = new ArrayList<>();
        //该订单相关商品集合
        List<MangoTradeItem> mangoTradeItemsList = new ArrayList<>();
        //该订单相关营销信息集合
        List<MangoTradeMarketing> mangoTradeMarkeingList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trades)) {
            trades.forEach(var -> {
                mangoTradePlusList.add(copyToMangoTradePlus(var));
                mangoTradeMainList.add(copyToMangoTradeMain(var));
                mangoTradeItemsList.addAll(copyToMangoTradeItem(var));
                log.info("mangoTradeItemsList-------------->>>>mangoTradeItemsList" + mangoTradeItemsList.size());
                mangoTradeMarkeingList.addAll(copyToMangoTradeMarketing(var));
                log.info("mangoTradeMarkeingList-------------->>>>mangoTradeItemsList" + mangoTradeMarkeingList.size());

                //三个集合有一个达到500长度或主集合已便利结束则保存
                if (mangoTradePlusList.size() > 500 ||
                        mangoTradeMainList.size() > 500 ||
                        mangoTradeItemsList.size() > 500 ||
                        mangoTradeMarkeingList.size() > 500 ||
                        var.equals(trades.get(trades.size() - 1))) {

                    if (CollectionUtils.isNotEmpty(mangoTradePlusList)) {
                        addBatchMangoTradePlus(mangoTradePlusList);
                        log.info("mangoTradePlusList-------------->>>>mangoTradePlusList" + mangoTradePlusList.size());
                        mangoTradePlusList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoTradeMainList)) {
                        addBatchMangoTradeMain(mangoTradeMainList);
                        log.info("mangoTradeMainList-------------->>>>mangoTradeMainList" + mangoTradeMainList.size());
                        mangoTradeMainList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoTradeItemsList)) {
                        addBatchMangoTradeItem(mangoTradeItemsList);
                        log.info("mangoTradeItemsList-------------->>>>mangoTradeItemsList" + mangoTradeItemsList.size());
                        mangoTradeItemsList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoTradeMarkeingList)) {
                        addBatchMangoTradeMarketing(mangoTradeMarkeingList);
                        log.info("mangoTradeMarkeingList-------------->>>>mangoTradeMarkeingList" + mangoTradeMarkeingList.size());
                        mangoTradeMarkeingList.clear();
                    }
                }
            });
        }
    }

    private MangoTradeMain copyToMangoTradeMain(Trade trade) {
        MangoTradeMain mangoTradeMain = new MangoTradeMain();
        mangoTradeMain.setOrderNo(trade.getId());
        mangoTradeMain.setParentId(trade.getParentId());

        mangoTradeMain.setActivityType(Integer.parseInt(trade.getActivityType()));
        if (Objects.nonNull(trade.getTradeState())) {
            mangoTradeMain.setAuditState(trade.getTradeState().getAuditState().getStatusId());
            mangoTradeMain.setFlowState(trade.getTradeState().getFlowState().getStateId());
            mangoTradeMain.setPayState(trade.getTradeState().getPayState().getStateId());
            mangoTradeMain.setDeliverStatus(trade.getTradeState().getDeliverStatus().getStatusId());
        }
        if (Objects.nonNull(trade.getBuyer())) {
            mangoTradeMain.setBuyerId(trade.getBuyer().getId());
            mangoTradeMain.setBuyerErpId(trade.getBuyer().getCustomerErpId());
            mangoTradeMain.setBuyerName(trade.getBuyer().getName());
            mangoTradeMain.setBuyerAccount(trade.getBuyer().getAccount());
            mangoTradeMain.setBuyerPhone(trade.getBuyer().getPhone());
            mangoTradeMain.setBuyerEmployeeId(trade.getBuyer().getEmployeeId());
        }

        if (Objects.nonNull(trade.getConsignee())) {
            mangoTradeMain.setConsigneeName(trade.getConsignee().getName());
            mangoTradeMain.setConsigneePhone(trade.getConsignee().getPhone());
        }

        if (Objects.nonNull(trade.getDeliverWay())) {
            mangoTradeMain.setDeliverWay(trade.getDeliverWay().toString());
        }

        mangoTradeMain.setGroupId(trade.getGroupId());
        mangoTradeMain.setGoodsTotalNum(trade.getGoodsTotalNum());
        if (Objects.nonNull(trade.getOrderEvaluateStatus())) {
            mangoTradeMain.setOrderEvaluateStatus(trade.getOrderEvaluateStatus().toValue());
        }
        if (Objects.nonNull(trade.getOrderSource())) {
            mangoTradeMain.setOrderSource(trade.getOrderSource().toValue());
        }

        if (Objects.nonNull(trade.getOrderType())) {
            if (Objects.nonNull(trade.getOrderType().getOrderTypeId())) {
                mangoTradeMain.setOrderType(trade.getOrderType().getOrderTypeId());
            }
        }

        if (Objects.nonNull(trade.getPayWay())) {
            mangoTradeMain.setPayWay(trade.getPayWay().toValue());
        }

        if (Objects.nonNull(trade.getPlatform())) {
            mangoTradeMain.setPlatform(trade.getPlatform().toValue());
        }

        if (Objects.nonNull(trade.getRefundFlag())) {
            mangoTradeMain.setRefundFlag(trade.getRefundFlag().toString());
        }

        mangoTradeMain.setReturnOrderNum(trade.getReturnOrderNum());
        mangoTradeMain.setShopName(trade.getShopName());
        if (Objects.nonNull(trade.getSupplier())) {
            mangoTradeMain.setSupplierSupplierCode(trade.getSupplier().getSupplierCode());
            mangoTradeMain.setSupplierSupplierName(trade.getSupplier().getSupplierName());
            mangoTradeMain.setSupplierEmployeeId(trade.getSupplier().getEmployeeId());
            mangoTradeMain.setSupplierStoreId(trade.getSupplier().getStoreId().toString());
            mangoTradeMain.setSupplierFreightTemplateType(trade.getSupplier().getFreightTemplateType().toString());
            mangoTradeMain.setSupplierStoreName(trade.getSupplier().getStoreName());
            mangoTradeMain.setSupplierEmployeeId(trade.getSupplier().getEmployeeId());
            mangoTradeMain.setSupplierErpId(trade.getSupplier().getErpId());
            if (Objects.nonNull(trade.getSupplier().getCompanyType())) {
                mangoTradeMain.setSupplierCompanyType(trade.getSupplier().getCompanyType().toString());
            }
        }

        if (Objects.nonNull(trade.getTradePrice())) {
            if (Objects.nonNull(trade.getTradePrice().getGoodsPrice())) {
                mangoTradeMain.setPriceGoodsPrice(trade.getTradePrice().getGoodsPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getDeliveryPrice())) {
                mangoTradeMain.setPriceDeliveryPrice(trade.getTradePrice().getDeliveryPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getOriginPrice())) {
                mangoTradeMain.setPriceOriginPrice(trade.getTradePrice().getOriginPrice());
            }

            if (Objects.nonNull(trade.getTradePrice().getTotalPrice())) {
                mangoTradeMain.setPriceTotalPrice(trade.getTradePrice().getTotalPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getTotalPayCash())) {
                mangoTradeMain.setPriceTotalPayCash(trade.getTradePrice().getTotalPayCash());
            }
            if (Objects.nonNull(trade.getTradePrice().getCmCommission())) {
                mangoTradeMain.setPriceCmCommission(trade.getTradePrice().getCmCommission());
            }
            if (Objects.nonNull(trade.getTradePrice().getOrderSupplyPrice())) {
                mangoTradeMain.setPriceOrderSupplyPrice(trade.getTradePrice().getOrderSupplyPrice());
            }
        }
        if (Objects.nonNull(trade.getTradeCoupon())) {
            mangoTradeMain.setCouponCodeId(trade.getTradeCoupon().getCouponCodeId());
            mangoTradeMain.setCouponCode(trade.getTradeCoupon().getCouponCode());
            mangoTradeMain.setCouponName(trade.getTradeCoupon().getCouponName());
            mangoTradeMain.setCouponType(trade.getTradeCoupon().getCouponType().toString());
        }
        if (Objects.nonNull(trade.getTradeState())) {
            if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                mangoTradeMain.setCreateTime(trade.getTradeState().getCreateTime());
            }
        }
        return mangoTradeMain;
    }

    private MangoTradePlus copyToMangoTradePlus(Trade trade) {
        MangoTradePlus mangoPipleTradePlus = new MangoTradePlus();

        mangoPipleTradePlus.setOrderNo(trade.getId());
        mangoPipleTradePlus.setBuyerRemark(trade.getBuyerRemark());

        if (Objects.nonNull(trade.getBookingDate())) {
            mangoPipleTradePlus.setBookingDate(trade.getBookingDate().toString());
        }
        if (Objects.nonNull(trade.getConsignee())) {
            mangoPipleTradePlus.setConsigneeProvinceId(trade.getConsignee().getProvinceId().toString());
            mangoPipleTradePlus.setConsigneeCityId(trade.getConsignee().getCityId().toString());
            mangoPipleTradePlus.setConsigneeAreaId(trade.getConsignee().getAreaId().toString());
            mangoPipleTradePlus.setConsigneeAddress(trade.getConsignee().getAddress());
            mangoPipleTradePlus.setConsigneeExpectTime(trade.getConsignee().getExpectTime());
        }
        mangoPipleTradePlus.setRequestIp(trade.getRequestIp());
        mangoPipleTradePlus.setStoreBagsFlag(trade.getStoreBagsFlag().toString());
        mangoPipleTradePlus.setStoreEvaluate(trade.getStoreEvaluate().toString());
        if (Objects.nonNull(trade.getTradePrice())) {
            mangoPipleTradePlus.setPriceDeliveryPrice(trade.getTradePrice().getDeliveryPrice());
            mangoPipleTradePlus.setPricePrivilegePrice(trade.getTradePrice().getPrivilegePrice());
            if (Objects.nonNull(trade.getTradePrice().getDiscountsPrice())) {
                mangoPipleTradePlus.setPriceDiscountsPrice(trade.getTradePrice().getDiscountsPrice());
            }
            mangoPipleTradePlus.setPriceIntegral(trade.getTradePrice().getIntegral());
            mangoPipleTradePlus.setPriceIntegralPrice(trade.getTradePrice().getIntegralPrice());
            mangoPipleTradePlus.setPricePoints(trade.getTradePrice().getPoints());
            mangoPipleTradePlus.setPricePointsPrice(trade.getTradePrice().getPointsPrice());
            mangoPipleTradePlus.setPricePointWorth(trade.getTradePrice().getPointWorth());
            mangoPipleTradePlus.setPriceSpecial(trade.getTradePrice().isSpecial() + "");
            mangoPipleTradePlus.setPriceEnableDeliveryPrice(trade.getTradePrice().isEnableDeliveryPrice() + "");
            mangoPipleTradePlus.setPriceDeliveryCouponPrice(trade.getTradePrice().getDeliveryCouponPrice());
            mangoPipleTradePlus.setPriceBalancePrice(trade.getTradePrice().getBalancePrice());
            mangoPipleTradePlus.setPriceOnlinePrice(trade.getTradePrice().getOnlinePrice());
        }

        if (Objects.nonNull(trade.getTradeCoupon())) {
            if (Objects.nonNull(trade.getTradeCoupon().getGoodsInfoIds())) {
                mangoPipleTradePlus.setCouponGoodsIds(JSONArray.parseArray(JSON.toJSONString(trade.getTradeCoupon().getGoodsInfoIds())).toJSONString());
            }

            mangoPipleTradePlus.setCouponDiscountsAmount(trade.getTradeCoupon().getDiscountsAmount());
            mangoPipleTradePlus.setCouponFullBuyPrice(trade.getTradeCoupon().getFullBuyPrice());
        }
        if (Objects.nonNull(trade.getTradeState())) {
            if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                mangoPipleTradePlus.setCreateTime(trade.getTradeState().getCreateTime());
            }
        }
        return mangoPipleTradePlus;
    }

    private List<MangoTradeMarketing> copyToMangoTradeMarketing(Trade trade) {
        List<MangoTradeMarketing> marketing = new ArrayList<>();


        if (CollectionUtils.isNotEmpty(trade.getTradeMarketings())) {
            trade.getTradeMarketings().forEach(var -> {
                MangoTradeMarketing mangoTradeMarketing = new MangoTradeMarketing();
                mangoTradeMarketing.setOrderNo(trade.getId());
                if (Objects.nonNull(var.getMarketingId())) {
                    mangoTradeMarketing.setMarketingId(var.getMarketingId().toString());
                }

                mangoTradeMarketing.setMarketingName(var.getMarketingName());
                if (Objects.nonNull(var.getMarketingType())) {
                    mangoTradeMarketing.setMarketingType(var.getMarketingType().toString());
                }
                if (Objects.nonNull(var.getSkuIds())) {
                    mangoTradeMarketing.setMarketingSkuIds(JSONArray.parseArray(JSON.toJSONString(var.getSkuIds())).toJSONString());
                }
                if (Objects.nonNull(var.getSubType())) {
                    mangoTradeMarketing.setMarketingSubType(var.getSubType().toString());
                }
                if (Objects.nonNull(var.getFullDiscountLevel())) {
                    if (Objects.nonNull(var.getFullDiscountLevel().getDiscountLevelId())) {
                        mangoTradeMarketing.setFullDiscountLevelId(var.getFullDiscountLevel().getDiscountLevelId().toString());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getFullAmount())) {
                        mangoTradeMarketing.setFullFullAmount(var.getFullDiscountLevel().getFullAmount());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getFullCount())) {
                        mangoTradeMarketing.setFullFullCount(var.getFullDiscountLevel().getFullCount().toString());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getDiscount())) {
                        mangoTradeMarketing.setFullDiscount(var.getFullDiscountLevel().getDiscount().toString());
                    }
                }
                if (Objects.nonNull(var.getGiftLevel())) {
                    mangoTradeMarketing.setGiftLevel(JSON.toJSONString(var.getGiftLevel()));
                }
                if (Objects.nonNull(var.getGiftIds())) {
                    mangoTradeMarketing.setGiftIds(JSONArray.parseArray(JSON.toJSONString(var.getGiftIds())).toJSONString());
                }

                if (Objects.nonNull(var.getReductionLevel())) {
                    if (Objects.nonNull(var.getReductionLevel().getReductionLevelId())) {
                        mangoTradeMarketing.setReductionReductionLevelId(var.getReductionLevel().getReductionLevelId().toString());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getFullAmount())) {
                        mangoTradeMarketing.setReductionFullAmount(var.getReductionLevel().getFullAmount());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getFullCount())) {
                        mangoTradeMarketing.setReductionFullCount(var.getReductionLevel().getFullCount().toString());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getReduction())) {
                        mangoTradeMarketing.setReductionReduction(var.getReductionLevel().getReduction().toString());
                    }
                }

                if (Objects.nonNull(var.getDiscountsAmount())) {
                    mangoTradeMarketing.setMarketingDiscountsAmount(var.getDiscountsAmount());
                }
                if (Objects.nonNull(var.getRealPayAmount())) {
                    mangoTradeMarketing.setMarketingRealPayaAmount(var.getRealPayAmount());
                }
                if (Objects.nonNull(var.getMultiple())) {
                    mangoTradeMarketing.setMultiple(var.getMultiple().toString());
                }

                if (Objects.nonNull(trade.getTradeState())) {
                    if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                        mangoTradeMarketing.setCreateTime(trade.getTradeState().getCreateTime());
                    }
                }
                marketing.add(mangoTradeMarketing);
            });
        }
        return marketing;
    }

    private List<MangoTradeItem> copyToMangoTradeItem(Trade trade) {
        List<MangoTradeItem> item = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trade.getTradeItems())) {
            trade.getTradeItems().forEach(var -> {
                MangoTradeItem mangoTradeItem = new MangoTradeItem();

                mangoTradeItem.setOrderNo(trade.getId());
                mangoTradeItem.setOid(var.getOid());
                mangoTradeItem.setAdminId(var.getAdminId());
                if (Objects.nonNull(var.getStoreId())) {
                    mangoTradeItem.setStoreId(var.getStoreId().toString());
                }

                mangoTradeItem.setSupplierCode(var.getSupplierCode());
                mangoTradeItem.setSpuId(var.getSpuId());
                mangoTradeItem.setSpuName(var.getSpuName());
                mangoTradeItem.setSkuId(var.getSkuId());
                mangoTradeItem.setSkuName(var.getSkuName());
                mangoTradeItem.setSkuNo(var.getSkuNo());

                if (Objects.nonNull(var.getGoodsWeight())) {
                    mangoTradeItem.setGoodsWeight(var.getGoodsWeight());
                }
                if (Objects.nonNull(var.getGoodsCubage())) {
                    mangoTradeItem.setGoodsCubage(var.getGoodsCubage().toString());
                }
                if (Objects.nonNull(var.getFreightTempId())) {
                    mangoTradeItem.setFreightTempId(var.getFreightTempId().toString());
                }
                if (Objects.nonNull(var.getCateId())) {
                    mangoTradeItem.setCateId(var.getCateId().toString());
                }
                mangoTradeItem.setCateName(var.getCateName());
                mangoTradeItem.setPic(var.getPic());
                if (Objects.nonNull(var.getBrand())) {
                    mangoTradeItem.setBrand(var.getBrand().toString());
                }

                mangoTradeItem.setNum(var.getNum().intValue());
                mangoTradeItem.setDeliveredNum(var.getDeliveredNum().intValue());
                mangoTradeItem.setDeliverStatus(var.getDeliverStatus().toString());
                mangoTradeItem.setUnit(var.getUnit());
                mangoTradeItem.setPrice(var.getPrice());
                mangoTradeItem.setOriginalPrice(var.getOriginalPrice());
                mangoTradeItem.setLevelPrice(var.getLevelPrice());
                mangoTradeItem.setVipPrice(var.getVipPrice());
                mangoTradeItem.setCost(var.getCost());
                if (Objects.nonNull(var.getSplitPrice())) {
                    mangoTradeItem.setSplitPrice(var.getSplitPrice());
                }
                mangoTradeItem.setBn(var.getBn());
                if (Objects.nonNull(var.getCanReturnNum())) {
                    mangoTradeItem.setCanReturnNum(var.getCanReturnNum().intValue());
                }

                mangoTradeItem.setSpecdDetails(var.getSpecDetails());
                if (Objects.nonNull(var.getCateRate())) {
                    mangoTradeItem.setCateRate(var.getCateRate().toString());
                }

                if (Objects.nonNull(var.getMarketingIds())) {
                    mangoTradeItem.setMarketingIds(JSONArray.parseArray(JSON.toJSONString(var.getMarketingIds())).toJSONString());
                }
                if (Objects.nonNull(var.getMarketingSettlements())) {
                    mangoTradeItem.setMarketingSettlements(JSON.toJSONString(var.getMarketingSettlements()));
                }
                if (Objects.nonNull(var.getSpecialPrice())) {
                    mangoTradeItem.setSpecialPrice(var.getSpecialPrice());
                }
                mangoTradeItem.setGoodsEvaluateStatus(var.getGoodsEvaluateStatus().toString());
                if (Objects.nonNull(var.getPoints())) {
                    mangoTradeItem.setPoints(var.getPoints().toString());
                }

                mangoTradeItem.setPointsPrice(var.getPointsPrice());
                mangoTradeItem.setPointsGoodsId(var.getPointsGoodsId());
                mangoTradeItem.setSettlementPrice(var.getSettlementPrice());
                if (Objects.nonNull(var.getEnterPriseAuditState())) {
                    mangoTradeItem.setEnterPriseAuditState(var.getEnterPriseAuditState().toString());
                }
                if (Objects.nonNull(var.getGoodsInfoType())) {
                    mangoTradeItem.setGoodsInfoType(var.getGoodsInfoType().toString());
                }
                if (Objects.nonNull(var.getCouponSettlements())) {
                    mangoTradeItem.setCouponSettlements(JSON.toJSONString(var.getCouponSettlements()));
                }
                if (Objects.nonNull(var.getIsFlashSaleGoods())) {
                    mangoTradeItem.setIsFlashSaleGoods(var.getIsFlashSaleGoods().toString());
                }

                if (Objects.nonNull(var.getFlashSaleGoodsId())) {
                    mangoTradeItem.setIsFlashSaleGoods(var.getFlashSaleGoodsId().toString());
                }

                if (Objects.nonNull(var.getProviderId())) {
                    mangoTradeItem.setIsFlashSaleGoods(var.getProviderId().toString());
                }

                if (Objects.nonNull(var.getSupplyPrice())) {
                    mangoTradeItem.setIsFlashSaleGoods(var.getSupplyPrice().toString());
                }

                if (Objects.nonNull(var.getTotalSupplyPrice())) {
                    mangoTradeItem.setIsFlashSaleGoods(var.getTotalSupplyPrice().toString());
                }

                mangoTradeItem.setProviderName(var.getProviderName());
                mangoTradeItem.setProviderCode(var.getProviderCode());
                mangoTradeItem.setGoodsBatchNo(var.getGoodsBatchNo());
                mangoTradeItem.setSaleUnit(var.getSaleUnit());
                if (Objects.nonNull(var.getSpecialPrice())) {
                    mangoTradeItem.setSpecialPrice(var.getSpecialPrice());
                }
                mangoTradeItem.setErpSkuNo(var.getErpSkuNo());
                if (Objects.nonNull(var.getAddStep())) {
                    mangoTradeItem.setAddsStep(var.getAddStep().toString());
                }
                mangoTradeItem.setGoodsSubtitle(var.getGoodsSubtitle());
                if (Objects.nonNull(var.getUseNum())) {
                    mangoTradeItem.setUseNum(var.getUseNum().intValue());
                }

                mangoTradeItem.setPileOrderCode(var.getPileOrderCode());
                if (Objects.nonNull(trade.getTradeState())) {
                    if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                        mangoTradeItem.setCreateTime(trade.getTradeState().getCreateTime());
                    }
                }
                item.add(mangoTradeItem);
            });
        }
        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradePlus(List<MangoTradePlus> list) {
        for (MangoTradePlus projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeMain(List<MangoTradeMain> list) {
        for (MangoTradeMain projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeMarketing(List<MangoTradeMarketing> list) {
        for (MangoTradeMarketing projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeItem(List<MangoTradeItem> list) {
        for (MangoTradeItem projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }
}
