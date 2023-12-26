package com.wanmi.sbc.returnorder.trade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.wanmi.sbc.common.util.DateUtil;
import com.wanmi.sbc.returnorder.trade.request.TradeQueryRequest;
import com.wanmi.sbc.returnorder.trade.model.root.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class MangoPileTradeMainService {

    @Autowired
    private PileTradeService tradeService;

    @PersistenceContext
    private EntityManager entityManager;


    public void savePileTrade(String beginTime, String endTime) {

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
        log.info("*************************************************************************(PileBENGIN)");

        log.info("筛选出的mango符合数据-------------------->Pile" + count);
        log.info("筛选出的mango符合数据-------------------->requestCountPile" + JSON.toJSONString(requestCount));
        for (int i = 0; i <= (count / 1000) + 1; i++) {
            TradeQueryRequest request = new TradeQueryRequest();
            request.setBeginTime(pageBeginTime);
            request.setEndTime(pageEndTime);
            request.setPageNum(i);
            request.setPageSize(1000);
            log.info("筛选出的mango符合数据-------------------->requestPile" + JSON.toJSONString(request));
            Page<PileTrade> page = tradeService.page(request.getWhereCriteria(), request);
            if (CollectionUtils.isNotEmpty(page.getContent())) {
                List<PileTrade> trades = page.getContent();
                log.info("筛选出的mango符合数据-------------------->tradesPile" + trades.size());
                saveToMysql(trades);
            }
        }
        log.info("*************************************************************************(PileEND)");
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveToMysql(List<PileTrade> trades) {
        //主订单集合
        List<MangoPileTradePlus> mangoPipleTradePlusList = new ArrayList<>();
        //订单附表集合
        List<MangoPileTradeMain> mangoPipleTradeMainList = new ArrayList<>();
        //该订单相关商品集合
        List<MangoPileTradeItem> mangoPipleTradeItemsList = new ArrayList<>();
        //该订单相关营销信息集合
        List<MangoPileTradeMarketing> mangoPipleTradeMarkeingList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trades)) {
            trades.forEach(var -> {
                mangoPipleTradePlusList.add(copyToMangoPipleTradePlus(var));
                mangoPipleTradeMainList.add(copyToMangoPipleTradeMain(var));
                mangoPipleTradeItemsList.addAll(copyToMangoPipleTradeItem(var));
                log.info("mangoTradeItemsList-------------->>>>mangoTradeItemsList" + mangoPipleTradeItemsList.size());
                mangoPipleTradeMarkeingList.addAll(copyToMangoPipleTradeMarketing(var));
                log.info("mangoTradeMarkeingList-------------->>>>mangoTradeItemsList" + mangoPipleTradeMarkeingList.size());

                //三个集合有一个达到500长度或主集合已便利结束则保存

                if (mangoPipleTradePlusList.size() > 500 ||
                        mangoPipleTradeMainList.size() > 500 ||
                        mangoPipleTradeItemsList.size() > 500 ||
                        mangoPipleTradeMarkeingList.size() > 500 ||
                        var.equals(trades.get(trades.size() - 1))) {

                    if (CollectionUtils.isNotEmpty(mangoPipleTradePlusList)) {
                        addBatchMangoTradePlus(mangoPipleTradePlusList);
                        mangoPipleTradePlusList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoPipleTradeMainList)) {
                        addBatchMangoTradeMain(mangoPipleTradeMainList);
                        mangoPipleTradeMainList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoPipleTradeItemsList)) {
                        addBatchMangoTradeItem(mangoPipleTradeItemsList);
                        mangoPipleTradeItemsList.clear();
                    }
                    if (CollectionUtils.isNotEmpty(mangoPipleTradeMarkeingList)) {
                        addBatchMangoTradeMarketing(mangoPipleTradeMarkeingList);
                        mangoPipleTradeMarkeingList.clear();
                    }
                }
            });
        }
    }

    private MangoPileTradeMain copyToMangoPipleTradeMain(PileTrade trade) {
        MangoPileTradeMain mangoPipleTradeMain = new MangoPileTradeMain();
        mangoPipleTradeMain.setOrderNo(trade.getId());
        mangoPipleTradeMain.setParentId(trade.getParentId());

        if (Objects.nonNull(trade.getTradeState())) {
            mangoPipleTradeMain.setAuditState(trade.getTradeState().getAuditState().getStatusId());
            mangoPipleTradeMain.setFlowState(trade.getTradeState().getFlowState().getStateId());
            mangoPipleTradeMain.setPayState(trade.getTradeState().getPayState().getStateId());
            mangoPipleTradeMain.setDeliverStatus(trade.getTradeState().getDeliverStatus().getStatusId());
        }
        if (Objects.nonNull(trade.getBuyer())) {
            mangoPipleTradeMain.setBuyerId(trade.getBuyer().getId());
            mangoPipleTradeMain.setBuyerErpId(trade.getBuyer().getCustomerErpId());
            mangoPipleTradeMain.setBuyerName(trade.getBuyer().getName());
            mangoPipleTradeMain.setBuyerAccount(trade.getBuyer().getAccount());
            mangoPipleTradeMain.setBuyerPhone(trade.getBuyer().getPhone());
            mangoPipleTradeMain.setBuyerEmployeeId(trade.getBuyer().getEmployeeId());
        }

        if (Objects.nonNull(trade.getConsignee())) {
            mangoPipleTradeMain.setConsigneeName(trade.getConsignee().getName());
            mangoPipleTradeMain.setConsigneePhone(trade.getConsignee().getPhone());
        }

        if (Objects.nonNull(trade.getDeliverWay())) {
            mangoPipleTradeMain.setDeliverWay(trade.getDeliverWay().toString());
        }

        mangoPipleTradeMain.setGroupId(trade.getGroupId());
        mangoPipleTradeMain.setGoodsTotalNum(trade.getGoodsTotalNum());
        if (Objects.nonNull(trade.getOrderEvaluateStatus())) {
            mangoPipleTradeMain.setOrderEvaluateStatus(trade.getOrderEvaluateStatus().toValue());
        }
        if (Objects.nonNull(trade.getOrderSource())) {
            mangoPipleTradeMain.setOrderSource(trade.getOrderSource().toValue());
        }

        if (Objects.nonNull(trade.getOrderType())) {
            if (Objects.nonNull(trade.getOrderType().getOrderTypeId())) {
                mangoPipleTradeMain.setOrderType(trade.getOrderType().getOrderTypeId());
            }
        }
        if (Objects.nonNull(trade.getPayWay())) {
            mangoPipleTradeMain.setPayWay(trade.getPayWay().toValue());
        }
        if (Objects.nonNull(trade.getPlatform())) {
            mangoPipleTradeMain.setPlatform(trade.getPlatform().toValue());
        }
        if (Objects.nonNull(trade.getRefundFlag())) {
            mangoPipleTradeMain.setRefundFlag(trade.getRefundFlag().toString());
        }

        mangoPipleTradeMain.setReturnOrderNum(trade.getReturnOrderNum());
        mangoPipleTradeMain.setShopName(trade.getShopName());
        if (Objects.nonNull(trade.getSupplier())) {
            mangoPipleTradeMain.setSupplierSupplierCode(trade.getSupplier().getSupplierCode());
            mangoPipleTradeMain.setSupplierSupplierName(trade.getSupplier().getSupplierName());
            mangoPipleTradeMain.setSupplierEmployeeId(trade.getSupplier().getEmployeeId());
            mangoPipleTradeMain.setSupplierStoreId(trade.getSupplier().getStoreId().toString());
            mangoPipleTradeMain.setSupplierFreightTemplateType(trade.getSupplier().getFreightTemplateType().toString());
            mangoPipleTradeMain.setSupplierStoreName(trade.getSupplier().getStoreName());
            mangoPipleTradeMain.setSupplierEmployeeId(trade.getSupplier().getEmployeeId());
            mangoPipleTradeMain.setSupplierErpId(trade.getSupplier().getErpId());
            if (Objects.nonNull(trade.getSupplier().getCompanyType())) {
                mangoPipleTradeMain.setSupplierCompanyType(trade.getSupplier().getCompanyType().toString());
            }
        }

        if (Objects.nonNull(trade.getTradePrice())) {
            if (Objects.nonNull(trade.getTradePrice().getGoodsPrice())) {
                mangoPipleTradeMain.setPriceGoodsPrice(trade.getTradePrice().getGoodsPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getDeliveryPrice())) {
                mangoPipleTradeMain.setPriceDeliveryPrice(trade.getTradePrice().getDeliveryPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getOriginPrice())) {
                mangoPipleTradeMain.setPriceOriginPrice(trade.getTradePrice().getOriginPrice());
            }

            if (Objects.nonNull(trade.getTradePrice().getTotalPrice())) {
                mangoPipleTradeMain.setPriceTotalPrice(trade.getTradePrice().getTotalPrice());
            }
            if (Objects.nonNull(trade.getTradePrice().getTotalPayCash())) {
                mangoPipleTradeMain.setPriceTotalPayCash(trade.getTradePrice().getTotalPayCash());
            }
            if (Objects.nonNull(trade.getTradePrice().getCmCommission())) {
                mangoPipleTradeMain.setPriceCmCommission(trade.getTradePrice().getCmCommission());
            }
            if (Objects.nonNull(trade.getTradePrice().getOrderSupplyPrice())) {
                mangoPipleTradeMain.setPriceOrderSupplyPrice(trade.getTradePrice().getOrderSupplyPrice());
            }
        }
        if (Objects.nonNull(trade.getTradeCoupon())) {
            mangoPipleTradeMain.setCouponCodeId(trade.getTradeCoupon().getCouponCodeId());
            mangoPipleTradeMain.setCouponCode(trade.getTradeCoupon().getCouponCode());
            mangoPipleTradeMain.setCouponName(trade.getTradeCoupon().getCouponName());
            mangoPipleTradeMain.setCouponType(trade.getTradeCoupon().getCouponType().toString());
        }
        if (Objects.nonNull(trade.getTradeState())) {
            if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                mangoPipleTradeMain.setCreateTime(trade.getTradeState().getCreateTime());
            }
        }
        return mangoPipleTradeMain;
    }

    private MangoPileTradePlus copyToMangoPipleTradePlus(PileTrade trade) {
        MangoPileTradePlus mangoPipleTradePlus = new MangoPileTradePlus();

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

    private List<MangoPileTradeMarketing> copyToMangoPipleTradeMarketing(PileTrade trade) {
        List<MangoPileTradeMarketing> marketing = new ArrayList<>();


        if (CollectionUtils.isNotEmpty(trade.getTradeMarketings())) {
            trade.getTradeMarketings().forEach(var -> {
                MangoPileTradeMarketing mangoPipleTradeMarketing = new MangoPileTradeMarketing();
                mangoPipleTradeMarketing.setOrderNo(trade.getId());
                if (Objects.nonNull(var.getMarketingId())) {
                    mangoPipleTradeMarketing.setMarketingId(var.getMarketingId().toString());
                }

                mangoPipleTradeMarketing.setMarketingName(var.getMarketingName());
                if (Objects.nonNull(var.getMarketingType())) {
                    mangoPipleTradeMarketing.setMarketingType(var.getMarketingType().toString());
                }
                if (Objects.nonNull(var.getSkuIds())) {
                    mangoPipleTradeMarketing.setMarketingSkuIds(JSONArray.parseArray(JSON.toJSONString(var.getSkuIds())).toJSONString());
                }
                if (Objects.nonNull(var.getSubType())) {
                    mangoPipleTradeMarketing.setMarketingSubType(var.getSubType().toString());
                }
                if (Objects.nonNull(var.getFullDiscountLevel())) {
                    if (Objects.nonNull(var.getFullDiscountLevel().getDiscountLevelId())) {
                        mangoPipleTradeMarketing.setFullDiscountLevelId(var.getFullDiscountLevel().getDiscountLevelId().toString());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getFullAmount())) {
                        mangoPipleTradeMarketing.setFullFullAmount(var.getFullDiscountLevel().getFullAmount());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getFullCount())) {
                        mangoPipleTradeMarketing.setFullFullCount(var.getFullDiscountLevel().getFullCount().toString());
                    }
                    if (Objects.nonNull(var.getFullDiscountLevel().getDiscount())) {
                        mangoPipleTradeMarketing.setFullDiscount(var.getFullDiscountLevel().getDiscount().toString());
                    }
                }
                if (Objects.nonNull(var.getGiftLevel())) {
                    mangoPipleTradeMarketing.setGiftLevel(JSON.toJSONString(var.getGiftLevel()));
                }
                if (Objects.nonNull(var.getGiftIds())) {
                    mangoPipleTradeMarketing.setGiftIds(JSONArray.parseArray(JSON.toJSONString(var.getGiftIds())).toJSONString());
                }

                if (Objects.nonNull(var.getReductionLevel())) {
                    if (Objects.nonNull(var.getReductionLevel().getReductionLevelId())) {
                        mangoPipleTradeMarketing.setReductionReductionLevelId(var.getReductionLevel().getReductionLevelId().toString());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getFullAmount())) {
                        mangoPipleTradeMarketing.setReductionFullAmount(var.getReductionLevel().getFullAmount());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getFullCount())) {
                        mangoPipleTradeMarketing.setReductionFullCount(var.getReductionLevel().getFullCount().toString());
                    }
                    if (Objects.nonNull(var.getReductionLevel().getReduction())) {
                        mangoPipleTradeMarketing.setReductionReduction(var.getReductionLevel().getReduction().toString());
                    }
                }

                if (Objects.nonNull(var.getDiscountsAmount())) {
                    mangoPipleTradeMarketing.setMarketingDiscountsAmount(var.getDiscountsAmount());
                }
                if (Objects.nonNull(var.getRealPayAmount())) {
                    mangoPipleTradeMarketing.setMarketingRealPayaAmount(var.getRealPayAmount());
                }
                if (Objects.nonNull(var.getMultiple())) {
                    mangoPipleTradeMarketing.setMultiple(var.getMultiple().toString());
                }

                if (Objects.nonNull(trade.getTradeState())) {
                    if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                        mangoPipleTradeMarketing.setCreateTime(trade.getTradeState().getCreateTime());
                    }
                }
                marketing.add(mangoPipleTradeMarketing);
            });
        }
        return marketing;
    }

    private List<MangoPileTradeItem> copyToMangoPipleTradeItem(PileTrade trade) {
        List<MangoPileTradeItem> item = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(trade.getTradeItems())) {
            trade.getTradeItems().forEach(var -> {
                MangoPileTradeItem mangoPipleTradeItem = new MangoPileTradeItem();

                mangoPipleTradeItem.setOrderNo(trade.getId());
                mangoPipleTradeItem.setOid(var.getOid());
                mangoPipleTradeItem.setAdminId(var.getAdminId());
                if (Objects.nonNull(var.getStoreId())) {
                    mangoPipleTradeItem.setStoreId(var.getStoreId().toString());
                }

                mangoPipleTradeItem.setSupplierCode(var.getSupplierCode());
                mangoPipleTradeItem.setSpuId(var.getSpuId());
                mangoPipleTradeItem.setSpuName(var.getSpuName());
                mangoPipleTradeItem.setSkuId(var.getSkuId());
                mangoPipleTradeItem.setSkuName(var.getSkuName());
                mangoPipleTradeItem.setSkuNo(var.getSkuNo());

                if (Objects.nonNull(var.getGoodsWeight())) {
                    mangoPipleTradeItem.setGoodsWeight(var.getGoodsWeight());
                }
                if (Objects.nonNull(var.getGoodsCubage())) {
                    mangoPipleTradeItem.setGoodsCubage(var.getGoodsCubage().toString());
                }
                if (Objects.nonNull(var.getFreightTempId())) {
                    mangoPipleTradeItem.setFreightTempId(var.getFreightTempId().toString());
                }
                if (Objects.nonNull(var.getCateId())) {
                    mangoPipleTradeItem.setCateId(var.getCateId().toString());
                }
                mangoPipleTradeItem.setCateName(var.getCateName());
                mangoPipleTradeItem.setPic(var.getPic());
                if (Objects.nonNull(var.getBrand())) {
                    mangoPipleTradeItem.setBrand(var.getBrand().toString());
                }

                mangoPipleTradeItem.setNum(var.getNum().intValue());
                mangoPipleTradeItem.setDeliveredNum(var.getDeliveredNum().intValue());
                mangoPipleTradeItem.setDeliverStatus(var.getDeliverStatus().toString());
                mangoPipleTradeItem.setUnit(var.getUnit());
                mangoPipleTradeItem.setPrice(var.getPrice());
                mangoPipleTradeItem.setOriginalPrice(var.getOriginalPrice());
                mangoPipleTradeItem.setLevelPrice(var.getLevelPrice());
                mangoPipleTradeItem.setVipPrice(var.getVipPrice());
                mangoPipleTradeItem.setCost(var.getCost());
                if (Objects.nonNull(var.getSplitPrice())) {
                    mangoPipleTradeItem.setSplitPrice(var.getSplitPrice());
                }
                mangoPipleTradeItem.setBn(var.getBn());
                if (Objects.nonNull(var.getCanReturnNum())) {
                    mangoPipleTradeItem.setCanReturnNum(var.getCanReturnNum().intValue());
                }

                mangoPipleTradeItem.setSpecdDetails(var.getSpecDetails());
                if (Objects.nonNull(var.getCateRate())) {
                    mangoPipleTradeItem.setCateRate(var.getCateRate().toString());
                }

                if (Objects.nonNull(var.getMarketingIds())) {
                    mangoPipleTradeItem.setMarketingIds(JSONArray.parseArray(JSON.toJSONString(var.getMarketingIds())).toJSONString());
                }
                if (Objects.nonNull(var.getMarketingSettlements())) {
                    mangoPipleTradeItem.setMarketingSettlements(JSON.toJSONString(var.getMarketingSettlements()));
                }
                if (Objects.nonNull(var.getSpecialPrice())) {
                    mangoPipleTradeItem.setSpecialPrice(var.getSpecialPrice());
                }
                mangoPipleTradeItem.setGoodsEvaluateStatus(var.getGoodsEvaluateStatus().toString());
                if (Objects.nonNull(var.getPoints())) {
                    mangoPipleTradeItem.setPoints(var.getPoints().toString());
                }

                mangoPipleTradeItem.setPointsPrice(var.getPointsPrice());
                mangoPipleTradeItem.setPointsGoodsId(var.getPointsGoodsId());
                mangoPipleTradeItem.setSettlementPrice(var.getSettlementPrice());
                if (Objects.nonNull(var.getEnterPriseAuditState())) {
                    mangoPipleTradeItem.setEnterPriseAuditState(var.getEnterPriseAuditState().toString());
                }
                if (Objects.nonNull(var.getGoodsInfoType())) {
                    mangoPipleTradeItem.setGoodsInfoType(var.getGoodsInfoType().toString());
                }
                if (Objects.nonNull(var.getCouponSettlements())) {
                    mangoPipleTradeItem.setCouponSettlements(JSON.toJSONString(var.getCouponSettlements()));
                }
                if (Objects.nonNull(var.getIsFlashSaleGoods())) {
                    mangoPipleTradeItem.setIsFlashSaleGoods(var.getIsFlashSaleGoods().toString());
                }

                if (Objects.nonNull(var.getFlashSaleGoodsId())) {
                    mangoPipleTradeItem.setIsFlashSaleGoods(var.getFlashSaleGoodsId().toString());
                }

                if (Objects.nonNull(var.getProviderId())) {
                    mangoPipleTradeItem.setIsFlashSaleGoods(var.getProviderId().toString());
                }

                if (Objects.nonNull(var.getSupplyPrice())) {
                    mangoPipleTradeItem.setIsFlashSaleGoods(var.getSupplyPrice().toString());
                }

                if (Objects.nonNull(var.getTotalSupplyPrice())) {
                    mangoPipleTradeItem.setIsFlashSaleGoods(var.getTotalSupplyPrice().toString());
                }

                mangoPipleTradeItem.setProviderName(var.getProviderName());
                mangoPipleTradeItem.setProviderCode(var.getProviderCode());
                mangoPipleTradeItem.setGoodsBatchNo(var.getGoodsBatchNo());
                mangoPipleTradeItem.setSaleUnit(var.getSaleUnit());
                if (Objects.nonNull(var.getSpecialPrice())) {
                    mangoPipleTradeItem.setSpecialPrice(var.getSpecialPrice());
                }
                mangoPipleTradeItem.setErpSkuNo(var.getErpSkuNo());
                if (Objects.nonNull(var.getAddStep())) {
                    mangoPipleTradeItem.setAddsStep(var.getAddStep().toString());
                }
                mangoPipleTradeItem.setGoodsSubtitle(var.getGoodsSubtitle());
                if (Objects.nonNull(var.getUseNum())) {
                    mangoPipleTradeItem.setUseNum(var.getUseNum().intValue());
                }

                mangoPipleTradeItem.setPileOrderCode(var.getPileOrderCode());
                if (Objects.nonNull(trade.getTradeState())) {
                    if (Objects.nonNull(trade.getTradeState().getCreateTime())) {
                        mangoPipleTradeItem.setCreateTime(trade.getTradeState().getCreateTime());
                    }
                }
                item.add(mangoPipleTradeItem);
            });
        }
        return item;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradePlus(List<MangoPileTradePlus> list) {
        for (MangoPileTradePlus projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeMain(List<MangoPileTradeMain> list) {
        for (MangoPileTradeMain projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeMarketing(List<MangoPileTradeMarketing> list) {
        for (MangoPileTradeMarketing projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBatchMangoTradeItem(List<MangoPileTradeItem> list) {
        for (MangoPileTradeItem projectApplyDO : list) {
            entityManager.merge(projectApplyDO);//insert插入操作
        }
        entityManager.flush();
        entityManager.clear();
    }
}
