package com.wanmi.sbc.configure;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfo;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.vo.GiftGoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftDetailVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingFullGiftLevelVO;
import com.wanmi.sbc.order.api.request.purchase.PurchaseMarketingRequest;
import com.wanmi.sbc.order.bean.vo.PurchaseMarketingCalcVO;
import com.wanmi.sbc.order.response.ImportGoodsInfo;
import com.wanmi.sbc.order.response.ImportGoodsInfosExcel;
import com.wanmi.sbc.shopcart.api.provider.purchase.PurchaseProvider;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseBatchSaveRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代客下单导入商品监听
 */

@Slf4j
@WebListener
public class GoodsInfoImportListener extends AnalysisEventListener<ImportGoodsInfo> implements ServletContextListener {

    private static GoodsInfoQueryProvider goodsInfoQueryProvider = null;

    private static EsGoodsInfoElasticService esGoodsInfoElasticService = null;

    private static CustomerQueryProvider customerQueryProvider = null;

    private static PurchaseProvider purchaseProvider = null;

//    private static CouponCodeQueryProvider couponCodeQueryProvider = null;
//
//    private static CommonUtil commonUtil = null;

    //数据校验
    private List<ImportGoodsInfo> excelList = Lists.newArrayList();

    //返回前端数据格式
    private ImportGoodsInfosExcel importGoodsInfosExcelResponse;

    //出现导入错误
    private Boolean excelFlag = true;

    //仓库id
    private String wareId;

    //用户id
    private String customerId;

    public GoodsInfoImportListener() {
        super();
        this.excelList.clear();
        this.excelFlag = true;
        this.wareId = null;
        this.customerId = null;
        this.importGoodsInfosExcelResponse = null;
    }

    @Override
    public void invoke(ImportGoodsInfo importGoodsInfo, AnalysisContext analysisContext) {
        excelList.add(importGoodsInfo);
    }

    /**
     * 校验excel
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (CollectionUtils.isEmpty(excelList)) {
            return;
        }

        importGoodsInfosExcelResponse = new ImportGoodsInfosExcel();

        //一次性查询所有导入商品信息，避免循环查找
        List<String> erpGoodsInfoNos = excelList.stream().map(e -> e.getErpGoodsInfoNo()).collect(Collectors.toList());
        List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listValidGoodsInfoAndStcokByErpGoodsInfoNos(
                GoodsInfoByErpNosRequest.builder()
                        .erpGoodsInfoNos(erpGoodsInfoNos)
                        .matchWareHouseFlag(true)
                        .wareId(Long.parseLong(wareId))
                        .build()).getContext().getGoodsInfos();

        if (CollectionUtils.isEmpty(goodsInfos)) {
            //所有商品没有库存
            excelFlag = false;
            excelList.forEach(e -> {
                e.setExcelError("商品库存不足！");
            });
            return;
        }

        //查询esgoods
        EsGoodsInfoQueryRequest esGoodsInfoQueryRequest = new EsGoodsInfoQueryRequest();
        List<String> goodsInfoIds = goodsInfos.stream().map(g -> g.getGoodsInfoId()).collect(Collectors.toList());
        esGoodsInfoQueryRequest.setGoodsInfoIds(goodsInfoIds);
        esGoodsInfoQueryRequest.setPageNum(0);
        //获取默认最大长度
        esGoodsInfoQueryRequest.setPageSize(10000);
        List<EsGoodsInfo> esGoodsInfoList = esGoodsInfoElasticService.getEsBaseInfoByParams(esGoodsInfoQueryRequest).getData();

        //查询下单用户
        CustomerGetByIdResponse customer =
                customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(customerId)).getContext();

        if (DeleteFlag.YES.equals(customer.getDelFlag()) || !CheckState.CHECKED.equals(customer.getCheckState())) {
            //所有商品没有库存
            excelFlag = false;
            excelList.forEach(e -> {
                e.setExcelError("用户不存在，或审核不通过！");
            });
            return;
        }

        //大批量提交数据使用普通循环效率更高
        List<ImportGoodsInfo> resultList = Lists.newArrayList();
        for (int i = 0; i < excelList.size(); i++) {
            //导入数据
            ImportGoodsInfo importGoodsInfo = excelList.get(i);
            //返回数据
            ImportGoodsInfo result = importGoodsInfo;

            if (Objects.isNull(importGoodsInfo.getErpGoodsInfoNo())
                    || Objects.isNull(importGoodsInfo.getGoodsInfoName())
                    || Objects.isNull(importGoodsInfo.getGoodsSpecs())
                    || 0 > importGoodsInfo.getNum()) {
                result.setExcelError("存在空字段");
                excelFlag = false;
            }
            //判断商品库存是否存在
            GoodsInfoVO goodsInfoVO = goodsInfos.stream().filter(g -> importGoodsInfo.getErpGoodsInfoNo().equals(g.getErpGoodsInfoNo())).findFirst().orElse(null);
            if (goodsInfoVO != null) {
                if (importGoodsInfo.getNum() > 0) {
                    //校验下单数
                    if (BigDecimal.valueOf(importGoodsInfo.getNum()).compareTo(goodsInfoVO.getStock()) < 0 ) {
                        //补充商品信息
                        ImportGoodsInfo goodsInfo = KsBeanUtil.convert(goodsInfoVO, ImportGoodsInfo.class);
                        if (CollectionUtils.isNotEmpty(esGoodsInfoList)) {
                            EsGoodsInfo esGoodsInfo = esGoodsInfoList.stream().filter(esg -> goodsInfoVO.getGoodsInfoId().equals(esg.getGoodsInfo().getGoodsInfoId())).findFirst().orElse(null);
                            if (esGoodsInfo != null) {
                                goodsInfo = KsBeanUtil.convert(esGoodsInfo.getGoodsInfo(), ImportGoodsInfo.class);
                                if (Objects.isNull(esGoodsInfo.getGoodsInfo().getSalePrice())) {
                                    goodsInfo.setSalePrice(esGoodsInfo.getGoodsInfo().getVipPrice());
                                }
                            }
                        }
                        result = KsBeanUtil.convert(goodsInfo, ImportGoodsInfo.class);
                        result.setNum(importGoodsInfo.getNum());
                        //设置购买数量
                        result.setBuyCount(importGoodsInfo.getNum().longValue());
                        result.setGoodsInfoId(goodsInfoVO.getGoodsInfoId());
                        result.setPrice(goodsInfo.getSalePrice());
                        result.setStock(goodsInfoVO.getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());

                        resultList.add(result);
                    } else {
                        //下单数超过库存数
                        result.setExcelError("商品库存不足！");
                        excelFlag = false;
                        resultList.add(result);
                    }
                } else {
                    //下单数不合法
                    result.setExcelError("下单数填写不合法！");
                    excelFlag = false;
                    resultList.add(result);
                }
            } else {
                result.setExcelError("商品未上架或者库存不足！");
                excelFlag = false;
                resultList.add(result);
            }
        }

        importGoodsInfosExcelResponse.setImportGoodsInfoList(resultList);

        //商品可以下单
        if (excelFlag) {
            // 获取采购营销信息及同步商品营销
            List<ImportGoodsInfo> importGoodsInfoList = importGoodsInfosExcelResponse.getImportGoodsInfoList();
            List<GoodsInfoVO> goodsInfoVOS = KsBeanUtil.convertList(importGoodsInfoList, GoodsInfoVO.class);
            List<String> goodsInfoIdList = importGoodsInfoList.stream().map(r -> r.getGoodsInfoId()).collect(Collectors.toList());

            PurchaseMarketingRequest purchaseMarketingRequest = new PurchaseMarketingRequest();
            purchaseMarketingRequest.setGoodsInfoIds(goodsInfoIdList);
            purchaseMarketingRequest.setGoodsInfos(goodsInfoVOS);
            purchaseMarketingRequest.setCustomer(KsBeanUtil.convert(customer, CustomerVO.class));
            purchaseMarketingRequest.setWareId(Long.parseLong(wareId));

            log.info("purchaseMarketingRequest {}", purchaseMarketingRequest);

            //加入购物车

            PurchaseBatchSaveRequest purchaseBatchSaveRequest = new PurchaseBatchSaveRequest();
            purchaseBatchSaveRequest.setGoodsInfos(KsBeanUtil.convertList(goodsInfoVOS, GoodsInfoDTO.class));
            purchaseBatchSaveRequest.setWareId(Long.parseLong(wareId));
            purchaseBatchSaveRequest.setCustomerId(customerId);
            //分享人默认为0；
            purchaseBatchSaveRequest.setInviteeId("0");
            purchaseProvider.batchSave(purchaseBatchSaveRequest);

            //查询营销
//            PurchaseMarketingResponse purchaseMarketingResponse = purchaseProvider.getPurchasesMarketing(purchaseMarketingRequest).getContext();
//
//            log.info("purchaseMarketingResponse {}", purchaseMarketingResponse);
//
//            //设置商品营销信息
//            importGoodsInfosExcelResponse.setGoodsMarketingMap(purchaseMarketingResponse.getMap());
//            //设置店铺营销信息
//            importGoodsInfosExcelResponse.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
//            //设置默认的选择营销
//            importGoodsInfosExcelResponse.setGoodsMarketings(purchaseMarketingResponse.getGoodsMarketings());
//            // 获取店铺对应的营销信息
//            List<PurchaseMarketingCalcVO> giftMarketing = new ArrayList<>();
//            //商品选择的营销(这里需要调整)
//            if (CollectionUtils.isNotEmpty(importGoodsInfosExcelResponse.getGoodsMarketings())) {
//                importGoodsInfosExcelResponse.setStoreMarketingMap(purchaseMarketingResponse.getStoreMarketingMap());
//                //过滤已选营销
//                HashMap<Long, List<PurchaseMarketingCalcVO>> map = new HashMap<>();
//                purchaseMarketingResponse.getStoreMarketingMap().forEach((k, v) -> {
//                    List<PurchaseMarketingCalcVO> calcResponses = v.stream().filter(p -> CollectionUtils.isNotEmpty(p.getGoodsInfoList())).collect(Collectors.toList());
//                    map.put(k, KsBeanUtil.convertList(calcResponses, PurchaseMarketingCalcVO.class));
//                    calcResponses.forEach(purchaseMarketingCalcVO -> {
//                        if (Objects.nonNull(purchaseMarketingCalcVO.getFullGiftLevelList())) {
//                            giftMarketing.add(purchaseMarketingCalcVO);
//                        }
//                    });
//                });
//                importGoodsInfosExcelResponse.setStoreMarketingMap(map);
//            } else {
//                importGoodsInfosExcelResponse.setStoreMarketingMap(new HashMap<>());
//            }
//            //组装赠品信息
//            if (CollectionUtils.isNotEmpty(giftMarketing)) {
//                //TODO:这里暂时写死，能匹配到仓
//                setGiftMarketingShop(giftMarketing, true);
//                importGoodsInfosExcelResponse.setGiftList(giftMarketing);
//            }
//
//            //计算金额
//            PurchaseCalcAmountRequest purchaseCalcAmountRequest = new PurchaseCalcAmountRequest();
//            //设置查询参数
//            PurchaseCalcAmountDTO purchaseCalcAmountDTO = new PurchaseCalcAmountDTO();
//            purchaseCalcAmountDTO = KsBeanUtil.convert(importGoodsInfosExcelResponse, PurchaseCalcAmountDTO.class);
//            purchaseCalcAmountDTO.setGoodsInfos(KsBeanUtil.convertList(importGoodsInfosExcelResponse.getImportGoodsInfoList(), GoodsInfoVO.class));
//            purchaseCalcAmountRequest.setPurchaseCalcAmount(purchaseCalcAmountDTO);
//            purchaseCalcAmountRequest.setGoodsInfoIds(goodsInfoIdList);
//            purchaseCalcAmountRequest.setCustomerVO(customer);
//
//            PurchaseListResponse purchaseListResponse = purchaseProvider.calcAmount(purchaseCalcAmountRequest).getContext();
//            importGoodsInfosExcelResponse.setTotalPrice(purchaseListResponse.getTotalPrice());
//            importGoodsInfosExcelResponse.setTradePrice(purchaseListResponse.getTradePrice());
//            importGoodsInfosExcelResponse.setDiscountPrice(purchaseListResponse.getDiscountPrice());
//
//            //计算优惠券
//
//            List<TradeItemInfoDTO> tradeDtos = Lists.newArrayList();
//            importGoodsInfoList.stream().forEach(importGoods -> {
//                TradeItemInfoDTO itemInfoDTO = new TradeItemInfoDTO();
//                itemInfoDTO.setGoodsInfoType(importGoods.getGoodsInfoType());
//                itemInfoDTO.setBrandId(importGoods.getBrandId());
//                itemInfoDTO.setNum(importGoods.getNum().longValue());
//                itemInfoDTO.setPrice(importGoods.getPrice().multiply(BigDecimal.valueOf(importGoods.getNum())));
//                itemInfoDTO.setCateId(importGoods.getCateId());
//                itemInfoDTO.setSkuId(importGoods.getGoodsInfoId());
//                itemInfoDTO.setSpuId(importGoods.getGoodsId());
//                itemInfoDTO.setStoreId(importGoods.getStoreId());
//                itemInfoDTO.setStoreCateIds(importGoods.getStoreCateIds());
//                itemInfoDTO.setDistributionCommission(importGoods.getDistributionCommission());
//                itemInfoDTO.setDistributionGoodsAudit(importGoods.getDistributionGoodsAudit());
//                itemInfoDTO.setGoodsBatchNo(importGoods.getGoodsInfoBatchNo());
//
//                tradeDtos.add(itemInfoDTO);
//            });
//
//            log.info("tradeDtos ============{}", tradeDtos);
//
//            CouponCodeListForUseByCustomerIdRequest requ = CouponCodeListForUseByCustomerIdRequest.builder()
//                    .customerId(Objects.nonNull(customer) && StringUtils.isNotBlank(customer.getParentCustomerId())
//                            ? customer.getParentCustomerId() : customerId)
//                    .tradeItems(tradeDtos).build();
//            requ.setStoreId(commonUtil.getStoreId());
//
//            log.info("requ==============={}", requ);
//            //设置优惠券
//            List<CouponCodeVO> couponCodeList = couponCodeQueryProvider.listForUseByCustomerId(requ).getContext().getCouponCodeList();
//            //所有可用优惠券
//            List<CouponCodeVO> availableCouponList = couponCodeList.stream().filter(cc -> CouponCodeStatus.AVAILABLE.equals(cc.getStatus())).collect(Collectors.toList());
//            //获取面值最大
//            if (CollectionUtils.isNotEmpty(availableCouponList)) {
//                CouponCodeVO couponCodeVO = availableCouponList.stream().max(Comparator.comparing(c -> c.getDenomination())).get();
//                //有优惠券增加优惠券减免金额
//                importGoodsInfosExcelResponse.setCouponCode(couponCodeVO);
//                importGoodsInfosExcelResponse.setTradePrice(importGoodsInfosExcelResponse.getTradePrice().subtract(couponCodeVO.getDenomination()));
//            }
        }

    }

    public List<ImportGoodsInfo> getListExcels() {
        return excelList;
    }

    public ImportGoodsInfosExcel getResult() {
        return importGoodsInfosExcelResponse;
    }

    public Boolean getExcelFlag() {
        return this.excelFlag;
    }

    //传仓库id
    public void setWareId(String wareId) {
        this.wareId = wareId;
    }

    //传用户id
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {

        goodsInfoQueryProvider = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext())
                .getBean(GoodsInfoQueryProvider.class);

        esGoodsInfoElasticService = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext())
                .getBean(EsGoodsInfoElasticService.class);

        customerQueryProvider = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext())
                .getBean(CustomerQueryProvider.class);

        purchaseProvider = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext())
                .getBean(PurchaseProvider.class);

//        couponCodeQueryProvider = WebApplicationContextUtils
//                .getWebApplicationContext(event.getServletContext())
//                .getBean(CouponCodeQueryProvider.class);
//
//        commonUtil = WebApplicationContextUtils
//                .getWebApplicationContext(event.getServletContext())
//                .getBean(CommonUtil.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        esGoodsInfoElasticService = null;
        goodsInfoQueryProvider = null;
        customerQueryProvider = null;
        purchaseProvider = null;
        importGoodsInfosExcelResponse = null;
//        couponCodeQueryProvider = null;
//        commonUtil = null;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     *
     * @Param: 查询补充赠品信息
     * @Return: void
     * @Author: yxb
     * @Date: 2021/2/2 19:50
     */
    private void setGiftMarketingShop(List<PurchaseMarketingCalcVO> giftMarketing, Boolean matchWareHouseFlag) {
        if (CollectionUtils.isNotEmpty(giftMarketing)) {
            Set<String> giftSkus = new HashSet<>();
            for (PurchaseMarketingCalcVO inner : giftMarketing) {
                List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                    List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                    for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                        giftSkus.add(gift.getProductId());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(giftSkus)) {
                List<GiftGoodsInfoVO> goodsInfos = goodsInfoQueryProvider.findGoodsInfoByIds(GoodsInfoListByIdsRequest.builder()
                        .goodsInfoIds(new ArrayList<>(giftSkus)).matchWareHouseFlag(matchWareHouseFlag).build()).getContext().getGoodsInfos();
                if (CollectionUtils.isNotEmpty(goodsInfos)) {
                    Map<String, GiftGoodsInfoVO> goodsInfoVOMap = goodsInfos.stream().collect(Collectors.toMap(GiftGoodsInfoVO::getGoodsInfoId, goods -> goods));
                    for (PurchaseMarketingCalcVO inner : giftMarketing) {
                        List<MarketingFullGiftLevelVO> fullGiftLevelList = inner.getFullGiftLevelList();
                        for (MarketingFullGiftLevelVO giftLevelVO : fullGiftLevelList) {
                            List<MarketingFullGiftDetailVO> fullGiftDetailList = giftLevelVO.getFullGiftDetailList();
                            for (MarketingFullGiftDetailVO gift : fullGiftDetailList) {
                                if (Objects.nonNull(goodsInfoVOMap.get(gift.getProductId()))) {
                                    gift.setGiftGoodsInfoVO(goodsInfoVOMap.get(gift.getProductId()));
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
