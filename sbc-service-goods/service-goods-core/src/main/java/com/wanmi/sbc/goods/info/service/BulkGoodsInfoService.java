package com.wanmi.sbc.goods.info.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.StringUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.ListNoDeleteStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.store.ListNoDeleteStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.SpecialGoodsModifyRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsBatchNoDTO;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.GoodsImageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.request.DevanningGoodsInfoQueryRequest;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.repository.GoodsWareStockRepository;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.reponse.*;
import com.wanmi.sbc.goods.info.repository.BulkGoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.BulkGoodsRepository;
import com.wanmi.sbc.goods.info.request.*;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.repository.GoodsCustomerPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsIntervalPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsLevelPriceRepository;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.service.StoreCateService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 零售sku服务
 * @author: XinJiang
 * @time: 2022/3/8 15:56
 */
@Slf4j
@Service
public class BulkGoodsInfoService {

    @Autowired
    private BulkGoodsInfoRepository bulkGoodsInfoRepository;

    @Autowired
    private BulkGoodsRepository bulkGoodsRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsWareStockRepository goodsWareStockRepository;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private GoodsIntervalPriceRepository goodsIntervalPriceRepository;

    @Autowired
    private GoodsLevelPriceRepository goodsLevelPriceRepository;

    @Autowired
    private GoodsCustomerPriceRepository goodsCustomerPriceRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private GoodsLabelService goodsLabelService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private StoreCateService storeCateService;

    @Autowired
    private GoodsBrandRepository goodsBrandRepository;

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;


    /**
     * SKU分页
     *
     * @param queryRequest 查询请求
     * @return 商品分页响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public BulkGoodsInfoResponse pageView(GoodsInfoQueryRequest queryRequest) {
        // TODO log
        log.info("RetailGoodsInfoService queryRequest:{}",JSON.toJSONString(queryRequest));
        if (StringUtils.isNotBlank(queryRequest.getLikeGoodsNo())
                || queryRequest.getStoreCateId() != null
                || CollectionUtils.isNotEmpty(queryRequest.getBrandIds())
                || CollectionUtils.isNotEmpty(queryRequest.getCateIds())
                || (queryRequest.getCateId() != null && queryRequest.getCateId() > 0)
                || queryRequest.getBrandId() != null && queryRequest.getBrandId() > 0) {
            GoodsQueryRequest goodsQueryRequest = GoodsQueryRequest.builder()
                    .likeGoodsNo(queryRequest.getLikeGoodsNo())
                    .brandId(queryRequest.getBrandId())
                    .brandIds(queryRequest.getBrandIds())
                    .cateId(queryRequest.getCateId())
                    .cateIds(queryRequest.getCateIds()).build();
            if (Objects.nonNull(queryRequest.getWareId())){
                goodsQueryRequest.setWareId(queryRequest.getWareId());
            }
            //获取该分类的所有子分类
            if (queryRequest.getCateId() != null && queryRequest.getCateId() > 0) {
                goodsQueryRequest.setCateIds(goodsCateService.getChlidCateId(queryRequest.getCateId()));
                goodsQueryRequest.getCateIds().add(queryRequest.getCateId());
            }
            //批量分类
            if (CollectionUtils.isNotEmpty(queryRequest.getCateIds())) {
                List<Long> cateIds = new ArrayList<>();
                for (Long cateId : queryRequest.getCateIds()) {
                    List<Long> childCateIds = goodsCateService.getChlidCateId(cateId);
                    if (CollectionUtils.isNotEmpty(childCateIds)) {
                        cateIds.addAll(childCateIds);
                    }
                    cateIds.add(cateId);
                }
                goodsQueryRequest.setCateIds(cateIds.stream().distinct().collect(Collectors.toList()));
            }

            //获取该店铺分类下的所有spuIds
            if (queryRequest.getStoreCateId() != null && queryRequest.getStoreCateId() > 0) {
                List<StoreCateGoodsRela> relas = storeCateService.findAllChildRela(queryRequest.getStoreCateId(), true);
                if (CollectionUtils.isNotEmpty(relas)) {
                    goodsQueryRequest.setStoreCateGoodsIds(relas.stream().map(StoreCateGoodsRela::getGoodsId).collect(Collectors.toList()));
                } else {
                    return BulkGoodsInfoResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageRequest(), 0)).build();
                }
            }

            List<BulkGoods> retailGoodses = bulkGoodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
            if (CollectionUtils.isEmpty(retailGoodses)) {
                return BulkGoodsInfoResponse.builder().goodsInfoPage(new MicroServicePage<>(Collections.emptyList(), queryRequest.getPageRequest(), 0)).build();
            }
            queryRequest.setGoodsIds(retailGoodses.stream().map(BulkGoods::getGoodsId).collect(Collectors.toList()));
        }

        //分页查询SKU信息列表
        Page<BulkGoodsInfo> goodsInfoPage = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria(), queryRequest.getPageRequest());
        //TODO log
        // log.info("RetailGoodsInfoService goodsInfoPage:{}",JSON.toJSONString(goodsInfoPage));

        MicroServicePage<BulkGoodsInfo> microPage = KsBeanUtil.convertPage(goodsInfoPage, BulkGoodsInfo.class);
        if (Objects.isNull(microPage) || microPage.getTotalElements() < 1 || CollectionUtils.isEmpty(microPage.getContent())) {

            return BulkGoodsInfoResponse.builder().goodsInfoPage(microPage).build();
        }

        // 商户库存转供应商库存
        //this.turnProviderStock(microPage.getContent());

        //查询SPU
        List<String> goodsIds = microPage.getContent().stream().map(BulkGoodsInfo::getGoodsId).distinct().collect(Collectors.toList());
        List<BulkGoods> retailGoodsList = bulkGoodsRepository.findAll(GoodsQueryRequest.builder().goodsIds(goodsIds).build().getBulkWhereCriteria());
        Map<String, BulkGoods> retailGoodsMap = retailGoodsList.stream().collect(Collectors.toMap(BulkGoods::getGoodsId, goods -> goods));
        //查询规格明细关联表
        List<String> skuIds = microPage.getContent().stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
        //分仓信息重新查询库存
        Long wareId = wareHouseQueryProvider.list(WareHouseListRequest.builder().storeId(microPage.getContent().get(0).getStoreId()).build())
                .getContext().getWareHouseVOList().get(0).getWareId();
        if (Objects.nonNull(wareId) && CollectionUtils.isNotEmpty(skuIds)) {
            List<GoodsWareStock> goodsWareStocks = goodsWareStockRepository.findByGoodsInfoIdIn(skuIds);
            microPage.getContent().forEach(inner -> {
                Optional<GoodsWareStock> first = goodsWareStocks.stream().filter(param -> param.getWareId().equals(wareId)
                        && param.getGoodsInfoId().equals(inner.getGoodsInfoId())).findFirst();
                if (first.isPresent()) {
                    inner.setStock(first.get().getStock().setScale(0,BigDecimal.ROUND_DOWN).longValue());
                    if(Objects.nonNull(inner.getMarketingId()) && inner.getPurchaseNum() > 0){
                        inner.setStock(inner.getPurchaseNum());
                    }
                } else {
                    inner.setStock(0L);
//                    if(Objects.nonNull(inner.getMarketingId()) && inner.getPurchaseNum() > 0){
//                        inner.setStock(inner.getPurchaseNum());
//                    }
                }
            });
        }
        List<GoodsBrand> brands = goodsBrandRepository.findAll(GoodsBrandQueryRequest.builder().delFlag(DeleteFlag.NO.toValue()).brandIds(retailGoodsList.stream().map(BulkGoods::getBrandId).collect(Collectors.toList())).build().getWhereCriteria());

        List<GoodsCate> cates = goodsCateRepository.findAll(GoodsCateQueryRequest.builder().cateIds(retailGoodsList.stream().map(BulkGoods::getCateId).collect(Collectors.toList())).build().getWhereCriteria());
        //商品对应图片
        List<GoodsImage> byGoodsIds = goodsImageRepository.findByGoodsIds(goodsIds);
        // TODO
        // log.info("RetailGoodsInfoService brands:{}",JSON.toJSONString(brands));
        // TODO
        // log.info("RetailGoodsInfoService cates:{}",JSON.toJSONString(cates));
        // TODO
        // log.info("RetailGoodsInfoService byGoodsIds:{}",JSON.toJSONString(byGoodsIds));
        // TODO
        // log.info("RetailGoodsInfoService microPage:{}",JSON.toJSONString(microPage));

        microPage.getContent().forEach(goodsInfo -> {
            //明细组合->，规格值1 规格值2
            if (MapUtils.isNotEmpty(specDetailMap)) {
                goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                        .map(GoodsInfoSpecDetailRel::getDetailName)
                        .collect(Collectors.toList()), " "));
            }

            //原价
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());


            BulkGoods retailGoods = retailGoodsMap.get(goodsInfo.getGoodsId());
            if (retailGoods != null) {
                goodsInfo.setGoodsSubtitle(retailGoods.getGoodsSubtitle());
                goodsInfo.setSaleType(retailGoods.getSaleType());
                goodsInfo.setAllowPriceSet(retailGoods.getAllowPriceSet());
                goodsInfo.setPriceType(retailGoods.getPriceType());
                goodsInfo.setGoodsUnit(retailGoods.getGoodsUnit());
                goodsInfo.setBrandId(retailGoods.getBrandId());
                goodsInfo.setCateId(retailGoods.getCateId());
                goodsInfo.setGoodsCubage(retailGoods.getGoodsCubage());
                goodsInfo.setGoodsWeight(retailGoods.getGoodsWeight());
                goodsInfo.setFreightTempId(retailGoods.getFreightTempId());

                //如果是特殊请求则使用第二张
                if (queryRequest.getImageFlag() && CollectionUtils.isNotEmpty(byGoodsIds)) {
                    Map<String, List<GoodsImage>> goodsImagesMap = byGoodsIds.stream().collect(Collectors.groupingBy(GoodsImage::getGoodsId));

                    List<GoodsImage> goodsImages = goodsImagesMap.get(goodsInfo.getGoodsId());
                    if(CollectionUtils.isNotEmpty(goodsImages) && goodsImages.size() > 1){
                        goodsInfo.setGoodsInfoImg(goodsImages.get(1).getArtworkUrl());
                    }
                }
                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(retailGoods.getGoodsImg());
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock() < 1) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });
        return BulkGoodsInfoResponse.builder().goodsInfoPage(microPage)
                .goodses(retailGoodsList)
                .cates(cates)
                .brands(brands).build();
    }

    /**
     * 根据ID查询商品SKU
     *
     * @param goodsInfoId 商品SKU编号
     * @return 商品SKU详情
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public BulkGoodsInfoEditResponse findById(String goodsInfoId) {
        BulkGoodsInfoEditResponse response = new BulkGoodsInfoEditResponse();
        BulkGoodsInfo goodsInfo = bulkGoodsInfoRepository.findById(goodsInfoId).orElse(null);
        if (goodsInfo == null || DeleteFlag.YES.toValue() == goodsInfo.getDelFlag().toValue()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        //填充本品信息
        if (Objects.nonNull(goodsInfo) && DefaultFlag.YES.equals(goodsInfo.getIsSuitGoods())
                && Objects.nonNull(goodsInfo.getChoseProductSkuId())) {
            response.setChoseProductGoodsInfo(bulkGoodsInfoRepository.findById(goodsInfo.getChoseProductSkuId()).orElse(null));
        }

        // 经营商户库存转供应商库存
        this.turnSingleProviderStock(goodsInfo);

        BulkGoods goods = bulkGoodsRepository.findById(goodsInfo.getGoodsId()).orElseThrow(() -> new SbcRuntimeException(GoodsErrorCode.NOT_EXIST));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            List<GoodsInfoSpecDetailRel> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsId(goods.getGoodsId());
            goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
            goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.stream().filter(detailRel -> detailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            goodsInfo.setSpecText(StringUtils.join(goodsInfoSpecDetailRels.stream().filter(specDetailRel -> goodsInfo.getGoodsInfoId().equals(specDetailRel.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getDetailName).collect(Collectors.toList()), " "));
        }

        //查询sku库存信息
        List<GoodsWareStock> goodsWareStocks = goodsWareStockRepository.findByGoodsInfoId(goodsInfo.getGoodsInfoId());
        List<Long> wareIds = goodsWareStocks.stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
        List<WareHouse> wareHouses = wareHouseRepository.findByWareIdIn(wareIds);
        goodsWareStocks.forEach(goodsWareStock -> {
            wareHouses.forEach(wareHouse -> {
                if (goodsWareStock.getWareId().equals(wareHouse.getWareId())) {
                    goodsWareStock.setWareName(wareHouse.getWareName());
                }
            });
        });

        List<GoodsWareStock> stockList = goodsWareStocks.stream().
                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId()) &&
                        goodsWareStock.getWareId() == 49l).
                collect(Collectors.toList());
        goodsInfo.setGoodsWareStocks(stockList);

        response.setGoodsInfo(goodsInfo);
        response.setGoods(goods);

        //商品按订货区间，查询订货区间
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            response.setGoodsIntervalPrices(goodsIntervalPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
        } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
            response.setGoodsLevelPrices(goodsLevelPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
            //如果是按单独客户定价
            if (Constants.yes.equals(goodsInfo.getCustomFlag())) {
                response.setGoodsCustomerPrices(goodsCustomerPriceRepository.findSkuByGoodsInfoId(goodsInfo.getGoodsInfoId()));
            }
        }
        response.setImages(goodsImageRepository.findByGoodsId(goods.getGoodsId()));

        //获取散批全部仓位
        List<WareHouse> bulkWareHouse = wareHouseService.getBulkWareHouse(2);
        response.setWareHousesList(bulkWareHouse);
        return response;
    }



    /**
     * 商品如果来自供应商，将商户库存转供应商库存
     *
     * @param goodsInfo
     * @return
     */
    public BulkGoodsInfo turnSingleProviderStock(BulkGoodsInfo goodsInfo) {
        if (Objects.nonNull(goodsInfo)) {
            // 商户商品skuid对应的供应商商品skuid
            String providerGoodsInfoId = goodsInfo.getProviderGoodsInfoId();
            if (StringUtils.isNotEmpty(providerGoodsInfoId)) {
                BulkGoodsInfo providerGoodsInfo = bulkGoodsInfoRepository.findById(providerGoodsInfoId).orElse(new BulkGoodsInfo());
                goodsInfo.setStock(providerGoodsInfo.getStock());
            }
        }

        return goodsInfo;
    }

    public BulkGoodsInfo findByGoodsInfoIdAndStoreIdAndDelFlag(String skuId, Long storeId) {
        BulkGoodsInfo goodsInfo = this.bulkGoodsInfoRepository.findByGoodsInfoIdAndStoreIdAndDelFlag(skuId, storeId, DeleteFlag.NO).orElse(null);
        return goodsInfo;
    }


    /**
     * 获取SKU详情
     *
     * @param skuId 商品skuId
     * @return 商品sku详情
     */
    public BulkGoodsInfo findOne(String skuId) {
        BulkGoodsInfo goodsInfo = this.bulkGoodsInfoRepository.findById(skuId).orElse(null);

        return goodsInfo;
    }


    /**
     * 商品SKU更新
     *
     * @param saveRequest sku信息
     * @return 商品信息
     */
    @Transactional
    public BulkGoodsInfo edit(BulkGoodsInfoSaveRequest saveRequest) {
        BulkGoodsInfo newGoodsInfo = saveRequest.getGoodsInfo();
        BulkGoodsInfo oldGoodsInfo = bulkGoodsInfoRepository.findById(newGoodsInfo.getGoodsInfoId()).orElse(null);
        if (oldGoodsInfo == null || oldGoodsInfo.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        BulkGoods goods = bulkGoodsRepository.findById(oldGoodsInfo.getGoodsId()).orElse(null);
        if (goods == null) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(Collections.singletonList(newGoodsInfo.getGoodsInfoNo()));
        infoQueryRequest.setNotGoodsInfoId(oldGoodsInfo.getGoodsInfoId());
        //验证SKU编码重复
        if (bulkGoodsInfoRepository.count(infoQueryRequest.getBulkWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }

        LocalDateTime currDate = LocalDateTime.now();

        //分析同一SPU的SKU上下架状态，去更新SPU上下架状态
        //累计状态值
        Integer addedCount = 0;
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsId(oldGoodsInfo.getGoodsId());
        List<BulkGoodsInfo> goodsInfos = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria());
        for (BulkGoodsInfo goodsInfo : goodsInfos) {
            if (goodsInfo.getGoodsInfoId().equals(newGoodsInfo.getGoodsInfoId())) {
                addedCount += newGoodsInfo.getAddedFlag() == null ? AddedFlag.NO.toValue() : newGoodsInfo.getAddedFlag();
            } else {
                addedCount += goodsInfo.getAddedFlag() == null ? AddedFlag.NO.toValue() : goodsInfo.getAddedFlag();
            }
        }

        Integer oldGoodsAddedFalg = goods.getAddedFlag();
        //如果是0，则所有SKU的上下架状态都是下架
        if (addedCount == 0) {
            goods.setAddedFlag(AddedFlag.NO.toValue());
        } else if (addedCount == goodsInfos.size()) {//累计等于同一SPU的SKU个数，上架
            goods.setAddedFlag(AddedFlag.YES.toValue());
        } else {//部分上架
            goods.setAddedFlag(AddedFlag.PART.toValue());
        }
        //修改商品副标题
        if(Objects.nonNull(newGoodsInfo.getAddStep()) && Objects.nonNull(newGoodsInfo.getMarketPrice())){
            //String goodsSubtitle=goods.getGoodsSubtitle().substring(2,goods.getGoodsSubtitle().length());
            // goods.setGoodsSubtitle(newGoodsInfo.getAddStep().intValue()+goods.getGoodsUnit()+goodsSubtitle);
            BigDecimal onePrice = newGoodsInfo.getMarketPrice()
                    .divide(newGoodsInfo.getAddStep(), 2, BigDecimal.ROUND_CEILING);
            goods.setGoodsSubtitle(newGoodsInfo.getAddStep().intValue()+goods.getGoodsUnit()+"(1.00"+goods.getGoodsUnit()+"x"+onePrice+"元/"+goods.getGoodsUnit()+")");
            goods.setGoodsSubtitleNew(onePrice+"元/"+goods.getGoodsUnit());
        }
        //更新商品
        goods.setUpdateTime(currDate);
        //当上下架状态不一致，更新上下架时间
        if (!oldGoodsAddedFalg.equals(goods.getAddedFlag())) {
            goods.setAddedTime(currDate);
        }
        bulkGoodsRepository.save(goods);

        //更新商品SKU
        if (newGoodsInfo.getStock() == null) {
            newGoodsInfo.setStock(0L);
        }
        //当上下架状态不一致，更新上下架时间
        if (!oldGoodsInfo.getAddedFlag().equals(newGoodsInfo.getAddedFlag())) {
            newGoodsInfo.setAddedTime(currDate);
        }

        newGoodsInfo.setUpdateTime(currDate);
        if (Objects.nonNull(oldGoodsInfo.getCommissionRate())) {
            newGoodsInfo.setDistributionCommission(newGoodsInfo.getMarketPrice().multiply(oldGoodsInfo.getCommissionRate()));
        }
        if (Objects.nonNull(oldGoodsInfo.getAddStep()) && oldGoodsInfo.getAddStep().compareTo(newGoodsInfo.getAddStep())!=0){
            //业务逻辑修改商品需要先下架才能修改歩长
            if (newGoodsInfo.getAddedFlag().compareTo(1)!=0){
                if (newGoodsInfo.getAddStep().compareTo(BigDecimal.ZERO)==0){
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"修改步长不能为0");
                }
                //修改库存 同步修改
                List<GoodsWareStock> collect = goodsWareStockService.findByGoodsInfoIdIn(Arrays.asList(newGoodsInfo.getGoodsInfoId())).stream().filter(
                        v -> {return v.getWareId().equals(oldGoodsInfo.getWareId());}
                ).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)){
                    GoodsWareStock goodsWareStock = collect.stream().findFirst().get();
                    BigDecimal newStock = goodsWareStock.getStock()
                             .multiply(oldGoodsInfo.getAddStep())
                            .divide(newGoodsInfo.getAddStep(), 0, BigDecimal.ROUND_DOWN);
                    goodsWareStockService.updateByGoodsInfoId(newGoodsInfo.getGoodsInfoId(),newStock,oldGoodsInfo.getWareId());
                }


                //需要自动修改spu的重量
                goods.setGoodsWeight(goods.getGoodsWeight().divide(oldGoodsInfo.getAddStep(),2,BigDecimal.ROUND_UP)
                        .multiply(newGoodsInfo.getAddStep()));
                bulkGoodsRepository.save(goods);
                oldGoodsInfo.setAddStep(newGoodsInfo.getAddStep());


            }else {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"上架状态不能修改步长");
            }
        }

        if (newGoodsInfo.getGoodsInfoQrcode() == null) {
            oldGoodsInfo.setGoodsInfoQrcode(null);
        }
        if (Objects.isNull(newGoodsInfo.getInquiryFlag())){
            newGoodsInfo.setInquiryFlag(Constants.no);
        }
        if (newGoodsInfo.getAllowedPurchaseArea() == null){
            oldGoodsInfo.setAllowedPurchaseArea(null);
        }
        if (newGoodsInfo.getAllowedPurchaseAreaName() == null){
            oldGoodsInfo.setAllowedPurchaseAreaName(null);
        }
        if (newGoodsInfo.getSingleOrderAssignArea() == null){
            oldGoodsInfo.setSingleOrderAssignArea(null);
        }
        if (newGoodsInfo.getSingleOrderAssignAreaName() == null){
            oldGoodsInfo.setSingleOrderAssignAreaName(null);
        }
        if (newGoodsInfo.getSingleOrderPurchaseNum() == null){
            oldGoodsInfo.setSingleOrderPurchaseNum(null);
        }
        if (newGoodsInfo.getVirtualStock() == null){
            oldGoodsInfo.setVirtualStock(0L);
        }
        if (Objects.isNull(newGoodsInfo.getIsSuitGoods())) {
            oldGoodsInfo.setIsSuitGoods(DefaultFlag.NO);
        } else {
            if (Objects.isNull(newGoodsInfo.getChoseProductSkuId()) && newGoodsInfo.getIsSuitGoods().equals(DefaultFlag.YES)) {
                throw new SbcRuntimeException(GoodsErrorCode.GOODS_PRODUCT_NOT_EXISTS);
            }
        }

        if(newGoodsInfo.getRetailPrice() == null){
            oldGoodsInfo.setRetailPrice(null);
        }
        //散称或定量字段为空，默认给-1
        if (Objects.isNull(newGoodsInfo.getIsScatteredQuantitative())){
            oldGoodsInfo.setIsScatteredQuantitative(-1);
        }
        if (Objects.isNull(newGoodsInfo.getErpPrice())){
            newGoodsInfo.setErpPrice(oldGoodsInfo.getErpPrice());
        }
        KsBeanUtil.copyProperties(newGoodsInfo, oldGoodsInfo);
        //如果修改歩长对应的商品也需要同步库存
        bulkGoodsInfoRepository.save(oldGoodsInfo);
        //更新最新库存信息
        List<GoodsWareStock> goodsWareStocks = newGoodsInfo.getGoodsWareStocks();
        if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
            goodsWareStocks.forEach(stock -> {
                goodsWareStockRepository.updateByGoodsInfoId(newGoodsInfo.getGoodsInfoId(), stock.getStock(), stock.getWareId());
            });
        }

        //更新标准库商品库供货价
        String goodsId = oldGoodsInfo.getGoodsId();
        StandardGoodsRel standardGoodsRel = standardGoodsRelRepository.findByGoodsId(goodsId);
        if (standardGoodsRel != null) {
            String standardId = standardGoodsRel.getStandardId();
            List<StandardGoods> standardGoodsList = standardGoodsRepository.findByGoodsIdIn(Arrays.asList(standardId));
            if (CollectionUtils.isNotEmpty(standardGoodsList)) {
                for (StandardGoods standardGoods : standardGoodsList) {
                    standardGoods.setSupplyPrice(oldGoodsInfo.getSupplyPrice());
                    standardGoodsRepository.save(standardGoods);
                }
            }
        }
        //更新商家商品库供货价等
        BulkGoodsInfo supplierGoodsInfo = bulkGoodsInfoRepository.findByProviderGoodsInfoId(oldGoodsInfo.getGoodsInfoId());
        if (supplierGoodsInfo != null) {
            supplierGoodsInfo.setSupplyPrice(oldGoodsInfo.getSupplyPrice());
            supplierGoodsInfo.setStock(oldGoodsInfo.getStock());
            supplierGoodsInfo.setAddedFlag(oldGoodsInfo.getAddedFlag());
            supplierGoodsInfo.setGoodsInfoBarcode(oldGoodsInfo.getGoodsInfoBarcode());
            supplierGoodsInfo.setGoodsInfoNo(oldGoodsInfo.getGoodsInfoNo());
            bulkGoodsInfoRepository.save(supplierGoodsInfo);
        }

        return oldGoodsInfo;
    }
    @Transactional
    public void edit(BulkGoodsInfo bulkGoodsInfo,List<Long> wareIds){
        List<BulkGoodsInfo> bulkGoodsInfos = bulkGoodsInfoRepository.queryGoodsInfoByParentId(bulkGoodsInfo.getParentGoodsInfoId());
        // log.info("数据"+bulkGoodsInfos);
        BulkGoods goods = bulkGoodsRepository.findById(bulkGoodsInfo.getGoodsId()).orElse(null);
        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        List<BulkGoodsInfo> collect = bulkGoodsInfos.stream().filter(v -> {
            return wareIds.contains(v.getWareId());
        }).filter(v -> {
            return v.getWareId()!=bulkGoodsInfo.getWareId();
        }).collect(Collectors.toList());
        //修改sku 和spu
        // log.info("最后数据"+collect);
        collect.stream().forEach(v->{

            BulkGoods goods1 = bulkGoodsRepository.findById(v.getGoodsId()).orElse(null);
            if (Objects.isNull(goods1)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
            }
            goods1.setGoodsWeight(goods.getGoodsWeight());
            goods1.setGoodsSubtitle(goods.getGoodsSubtitle());
            bulkGoodsRepository.save(goods1);
            //v.setFullStep(bulkGoodsInfo.getFullStep());
            v.setMarketPrice(bulkGoodsInfo.getMarketPrice());
            v.setRetailPrice(bulkGoodsInfo.getRetailPrice());
            v.setVipPrice(bulkGoodsInfo.getVipPrice());
            if (Objects.nonNull(v.getAddStep()) && v.getAddStep().compareTo(bulkGoodsInfo.getAddStep())!=0){
                    if (bulkGoodsInfo.getAddStep().compareTo(BigDecimal.ZERO)==0){
                        throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"修改步长不能为0");
                    }
                    //修改库存 同步修改
                    List<GoodsWareStock> collect1 = goodsWareStockService.findByGoodsInfoIdIn(Arrays.asList(bulkGoodsInfo.getGoodsInfoId())).stream().filter(
                            q -> {return q.getWareId().equals(v.getWareId());}
                    ).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(collect1)){
                        GoodsWareStock goodsWareStock = collect1.stream().findFirst().get();
                        BigDecimal newStock = goodsWareStock.getStock()
                                .multiply(v.getAddStep())
                                .divide(bulkGoodsInfo.getAddStep(), 0, BigDecimal.ROUND_DOWN);
                        goodsWareStockService.updateByGoodsInfoId(bulkGoodsInfo.getGoodsInfoId(),newStock,v.getWareId());
                    }
                    v.setAddStep(bulkGoodsInfo.getAddStep());
            }
            bulkGoodsInfoRepository.save(bulkGoodsInfo);
        });




    }


    public void checkAddFlag(BulkGoodsInfoSaveRequest saveRequest,List<Long> wareIds){
        BulkGoodsInfo newGoodsInfo = saveRequest.getGoodsInfo();
        if (newGoodsInfo.getAddedFlag().compareTo(1)==0){
            throw new SbcRuntimeException(GoodsErrorCode.GOODS_NOT_UPDATE_ADDSTEPE);
        }
        BulkGoodsInfo bulkGoodsInfo = bulkGoodsInfoRepository.findById(newGoodsInfo.getGoodsInfoId()).orElse(null);
        if (bulkGoodsInfo == null || bulkGoodsInfo.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        List<BulkGoodsInfo> bulkGoodsInfos = bulkGoodsInfoRepository.queryGoodsInfoByParentId(bulkGoodsInfo.getParentGoodsInfoId());
        List<BulkGoodsInfo> newbulkGoodsInfos = bulkGoodsInfos.stream().filter(v -> {
            return !v.getGoodsInfoId().equalsIgnoreCase(newGoodsInfo.getGoodsInfoId());
        }).filter(v->{
            return wareIds.contains(v.getWareId());
        }).collect(Collectors.toList());
        newbulkGoodsInfos.forEach(v->{
            if (v.getAddedFlag().compareTo(1)==0){
                throw new SbcRuntimeException(GoodsErrorCode.GOODS_NOT_UPDATE_ADDSTEPE);
            }
        });
    }


    /**
     * SKU分页
     *
     * @param queryRequest 查询请求
     * @return 商品分页列表
     */
    @Transactional(readOnly = true, timeout = 10)
    public Page<BulkGoodsInfo> page(GoodsInfoQueryRequest queryRequest) {
        Page<BulkGoodsInfo> goodsInfoPage = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria(),
                queryRequest.getPageRequest());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoPage.getContent());
        return goodsInfoPage;
    }

    /**
     * 更新SKU设价
     *
     * @param saveRequest sku设价信息
     */
    @Transactional
    public BulkGoodsInfo editPrice(BulkGoodsInfoSaveRequest saveRequest) {
        BulkGoodsInfo newGoodsInfo = saveRequest.getGoodsInfo();
        BulkGoodsInfo oldGoodsInfo = bulkGoodsInfoRepository.findById(newGoodsInfo.getGoodsInfoId()).orElse(null);
        if (oldGoodsInfo == null || oldGoodsInfo.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        BulkGoods goods = bulkGoodsRepository.findById(oldGoodsInfo.getGoodsId()).orElseThrow(() -> new SbcRuntimeException(GoodsErrorCode.NOT_EXIST));

        //传递设价内部值
        oldGoodsInfo.setCustomFlag(newGoodsInfo.getCustomFlag());
        oldGoodsInfo.setLevelDiscountFlag(newGoodsInfo.getLevelDiscountFlag());
        bulkGoodsInfoRepository.save(oldGoodsInfo);

        //先删除设价数据
        goodsIntervalPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());
        goodsLevelPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());
        goodsCustomerPriceRepository.deleteByGoodsInfoId(newGoodsInfo.getGoodsInfoId());

        //按订货量设价，保存订货区间
        List<GoodsIntervalPrice> goodsIntervalPrices = null;
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            goodsIntervalPrices = saveRequest.getGoodsIntervalPrices();
            if (CollectionUtils.isEmpty(saveRequest.getGoodsIntervalPrices()) || saveRequest.getGoodsIntervalPrices().stream().filter(intervalPrice -> intervalPrice.getCount() == 1).count() == 0) {
                GoodsIntervalPrice intervalPrice = new GoodsIntervalPrice();
                intervalPrice.setCount(1L);
                intervalPrice.setPrice(newGoodsInfo.getMarketPrice());
                if (saveRequest.getGoodsIntervalPrices() == null) {
                    saveRequest.setGoodsLevelPrices(new ArrayList<>());
                }
                saveRequest.getGoodsIntervalPrices().add(intervalPrice);
            }

            if (CollectionUtils.isNotEmpty(goodsIntervalPrices)) {
                goodsIntervalPrices.forEach(intervalPrice -> {
                    intervalPrice.setGoodsId(goods.getGoodsId());
                    intervalPrice.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    intervalPrice.setType(PriceType.SKU);
                });
                goodsIntervalPriceRepository.saveAll(goodsIntervalPrices);
            }
        } else {
            //否则按客户
            if (CollectionUtils.isNotEmpty(saveRequest.getGoodsLevelPrices())) {
                saveRequest.getGoodsLevelPrices().forEach(goodsLevelPrice -> {
                    goodsLevelPrice.setGoodsId(goods.getGoodsId());
                    goodsLevelPrice.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    goodsLevelPrice.setType(PriceType.SKU);
                });
                goodsLevelPriceRepository.saveAll(saveRequest.getGoodsLevelPrices());
            }

            //按客户单独定价
            if (Constants.yes.equals(newGoodsInfo.getCustomFlag()) && CollectionUtils.isNotEmpty(saveRequest.getGoodsCustomerPrices())) {
                saveRequest.getGoodsCustomerPrices().forEach(price -> {
                    price.setGoodsId(goods.getGoodsId());
                    price.setGoodsInfoId(newGoodsInfo.getGoodsInfoId());
                    price.setType(PriceType.SKU);
                });
                goodsCustomerPriceRepository.saveAll(saveRequest.getGoodsCustomerPrices());
            }
        }

        return this.edit(saveRequest);
    }

    /**
     * 条件查询SKU数据
     *
     * @param request 查询条件
     * @return 商品sku列表
     */
    public List<BulkGoodsInfo> findByParams(BulkGoodsInfoQueryRequest request) {
        List<BulkGoodsInfo> goodsInfoList = this.bulkGoodsInfoRepository.findAll(request.getWhereCriteria());
        // 商户库存转供应商库存
        //this.turnProviderStock(goodsInfoList); //2020 5/15  喜丫丫 库存由WMS第三方提供无需转供应库存
        return goodsInfoList;
    }

    /**
     * 根据ID批量查询商品SKU
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public BulkGoodsInfoResponse findSkuByIds(GoodsInfoRequest infoRequest) {

        StringBuffer sb = new StringBuffer();
        Long sTm = System.currentTimeMillis();
        sb.append("purchase findSkuByIds start");

        try {
            if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
                return BulkGoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
            }
            //批量查询SKU信息列表
            GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
            queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
            queryRequest.setStoreId(infoRequest.getStoreId());
            //queryRequest.setAddedFlag(1);
            if (infoRequest.getDeleteFlag() != null) {
                queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
            }
            //关联goodsLabels
            List<GoodsLabel> goodsLabelList = goodsLabelService.findTopByDelFlag();
            List<BulkGoodsInfo> retailGoodsInfos = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria());

            sb.append(",goodsInfoRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(retailGoodsInfos)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{retailGoodsInfos});
            }

            //设置仓库的库存
        /*List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(
                goodsInfos.stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()),
                infoRequest.getWareId());*/
            List<Long> unOnline = new ArrayList<>(10);
            List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(retailGoodsInfos.stream()
                    .map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList()));

            sb.append(",goodsWareStockService.findByGoodsInfoIdIn end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

//            Map<String, Long> goodsNumsMap = pileTradeQueryProvider.getGoodsPileNumBySkuIds(PilePurchaseRequest.builder().goodsInfoIds(goodsInfos
//                    .stream().map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList())).build()).getContext().getGoodsNumsMap();

            if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
                if (Objects.nonNull(infoRequest.getMatchWareHouseFlag()) && !infoRequest.getMatchWareHouseFlag()) {
                    List<Long> storeList = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
                    unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE).stream()
                            .map(WareHouseVO::getWareId).collect(Collectors.toList());

                    sb.append(",queryWareHouses end time=");
                    sb.append(System.currentTimeMillis() - sTm);
                    sTm = System.currentTimeMillis();

                }
                for (BulkGoodsInfo retailGoodsInfo : retailGoodsInfos) {
                    List<GoodsWareStock> stockList;
                    if (CollectionUtils.isNotEmpty(unOnline)) {
                        List<Long> finalUnOnline = unOnline;
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> retailGoodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                    } else {
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> retailGoodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        retailGoodsInfo.setStock(sumStock.setScale(0,BigDecimal.ROUND_DOWN).longValue());
//                        if(Objects.isNull(goodsInfo.getMarketingId()) || Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() == -1){
//                            goodsInfo.setStock(sumStock);
//                        }
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }else{
//                            //计算库存 加上虚拟库存 减去囤货数量
//                            calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                        }
                    } else {
                        retailGoodsInfo.setStock(0L);
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                        }else{
//                            //计算库存 加上虚拟库存 减去囤货数量
//                            calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                        }
                    }
                }

            /*Map<String,GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream().collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId,g->g));
            goodsInfos.stream().forEach(g->{
                if(Objects.nonNull(goodsWareStockMap.get(g.getGoodsInfoId()))){
                    g.setStock(goodsWareStockMap.get(g.getGoodsInfoId()).getStock());
                }
            });*/
            }


            //2020-05-15  商品库存 通过WMS第三方查询
      /*  goodsInfos.forEach((goodsInfo)->{
            BaseResponse<InventoryQueryResponse> response = queryStorkByWMS(InventoryQueryRequest.builder()
                    .customerID(AbstarctWMSConstant.CUSTOMER_ID)
                    .sku(goodsInfo.getGoodsInfoNo()).build());
            goodsInfo.setStock(response.getContext().getInventoryQueryReturnVO().getQty());
        });*/


            // 商户库存转供应商库存
            // this.turnProviderStock(goodsInfos);

            //批量查询SPU信息列表
            List<String> goodsIds = retailGoodsInfos.stream().map(BulkGoodsInfo::getGoodsId).collect(Collectors.toList());
            GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
            goodsQueryRequest.setGoodsIds(goodsIds);
            List<BulkGoods> retailGoodses = bulkGoodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());


            //标签组装进goods
            retailGoodses.stream().forEach(item -> {
                if (StringUtils.isNotBlank(item.getLabelIdStr())) {
                    List<Long> goodsLabelIds = Arrays.stream(item.getLabelIdStr().split(",")).map(Long::parseLong)
                            .collect(Collectors.toList());
                    //过滤除符合条件的标签对象
                    item.setGoodsLabels(goodsLabelList.stream()
                            .filter(label -> goodsLabelIds.contains(label.getId()))
                            .collect(Collectors.toList()));

                }
            });

            HashMap<String, List<GoodsLabel>> goodsLabelMap = new HashMap<>();
            retailGoodses.forEach(item -> {
                goodsLabelMap.put(item.getGoodsId(), item.getGoodsLabels());
            });

            sb.append(",goodsRepository.findAll end time=");
            sb.append(System.currentTimeMillis() - sTm);
            sTm = System.currentTimeMillis();

            if (CollectionUtils.isEmpty(retailGoodses)) {
                throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
            }
            Map<String, BulkGoods> goodsMap = retailGoodses.stream().collect(Collectors.toMap(BulkGoods::getGoodsId, g -> g));


            List<String> skuIds = retailGoodsInfos.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
            //如果需要规格值，则查询
            if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
                specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));

                sb.append(",goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds end time=");
                sb.append(System.currentTimeMillis() - sTm);
                sTm = System.currentTimeMillis();
            }

            //遍历SKU，填充销量价、商品状态
            retailGoodsInfos.forEach(goodsInfo -> {
                goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
                BulkGoods retailGoods = goodsMap.get(goodsInfo.getGoodsId());
                if (retailGoods != null) {
                    //建立扁平化数据
                    if (retailGoods.getGoodsInfoIds() == null) {
                        retailGoods.setGoodsInfoIds(new ArrayList<>());
                    }
                    retailGoods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                    goodsInfo.setPriceType(retailGoods.getPriceType());
                    goodsInfo.setGoodsUnit(retailGoods.getGoodsUnit());
                    goodsInfo.setCateId(retailGoods.getCateId());
                    goodsInfo.setBrandId(retailGoods.getBrandId());
                    goodsInfo.setGoodsCubage(retailGoods.getGoodsCubage());
                    goodsInfo.setGoodsWeight(retailGoods.getGoodsWeight());
                    goodsInfo.setFreightTempId(retailGoods.getFreightTempId());
                    goodsInfo.setGoodsEvaluateNum(retailGoods.getGoodsEvaluateNum());
                    goodsInfo.setGoodsSalesNum(retailGoods.getGoodsSalesNum());
                    goodsInfo.setGoodsCollectNum(retailGoods.getGoodsCollectNum());
                    goodsInfo.setGoodsFavorableCommentNum(retailGoods.getGoodsFavorableCommentNum());

                    //sku标签
                    goodsInfo.setGoodsLabels(goodsLabelMap.get(goodsInfo.getGoodsId()));

                    //为空，则以商品主图
                    if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                        GoodsImage goodsImage = goodsImageRepository.findByGoodsId(retailGoods.getGoodsId()).stream().findFirst().orElse(null);
                        if (Objects.nonNull(goodsImage)) {
                            goodsInfo.setGoodsInfoImg(goodsImage.getArtworkUrl());
                        } else {
                            goodsInfo.setGoodsInfoImg(retailGoods.getGoodsImg());
                        }
                    }

                    //填充规格值
                    if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(retailGoods.getMoreSpecFlag())) {
                        goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                                .map(GoodsInfoSpecDetailRel::getDetailName)
                                .collect(Collectors.toList()), " "));
                    }

                    if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);

                        if ((Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock() < 1) && (Objects.isNull(goodsInfo.getMarketingId()) || Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() == -1)) {
                            goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                        //限购商品
//                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() != -1){
//                            if(goodsInfo.getPurchaseNum() < 1){
//                                goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
//                            }
//                        }

                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                } else {//不存在，则做为删除标记
                    goodsInfo.setDelFlag(DeleteFlag.YES);
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            });

            sb.append(",goodsInfos.forEach end time=");
            sb.append(System.currentTimeMillis() - sTm);

            //定义响应结果
            BulkGoodsInfoResponse responses = new BulkGoodsInfoResponse();
            responses.setGoodsInfos(retailGoodsInfos);
            responses.setGoodses(retailGoodses);
            return responses;
        } finally {
            log.info(sb.toString());
        }

    }

    /**
     * 根据id批量查询SKU数据
     *
     * @param skuIds 商品skuId
     * @return 商品sku列表
     */
    public List<BulkGoodsInfo> findByIds(List<String> skuIds, Long wareId) {
        List<BulkGoodsInfo> goodsInfoList = this.bulkGoodsInfoRepository.findAllById(skuIds);
        //设置库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.getGoodsStockByAreaIdAndGoodsInfoIds(skuIds, wareId);
        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(skuIds);

        if (Objects.nonNull(wareId) && CollectionUtils.isNotEmpty(goodsWareStocks)) {
            Map<String, GoodsWareStock> goodsWareStockMap = goodsWareStocks.stream()
                    .collect(Collectors.toMap(GoodsWareStock::getGoodsInfoId, g -> g));
            goodsInfoList.forEach(g -> g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO).setScale(0,BigDecimal.ROUND_DOWN).longValue()));
        }
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
//        goodsInfoList.forEach(g -> {
//            if (Objects.isNull(g.getStock())) {
//                g.setStock(0L);
//                if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                    g.setStock(g.getPurchaseNum());
//                }
//            }

//            if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                g.setStock(g.getPurchaseNum());
//            }else{
        //计算库存 加上虚拟库存 减去囤货数量
//                calGoodsInfoStock(g,goodsNumsMap);
//            }
//        });
        return goodsInfoList;
    }

    /**
     * 填充商品的有效性
     *
     * @param goodsInfoList 填充后有效状态的商品列表数据
     */
    public List<BulkGoodsInfo> fillGoodsStatus(List<BulkGoodsInfo> goodsInfoList) {
        //验证商品，并设为无效
        goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).forEach(goodsInfo -> {
            if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                    && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                goodsInfo.setGoodsStatus(GoodsStatus.OK);
                //是否是限购商品
                boolean purchaseFlag = Objects.nonNull(goodsInfo.getPurchaseNum()) && goodsInfo.getPurchaseNum() != -1 ;
                //非限购商品
                if (goodsInfo.getStock() < 1 && (Objects.isNull(goodsInfo.getPurchaseNum()) || goodsInfo.getPurchaseNum() != -1)) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
                //限购商品
                if(purchaseFlag && goodsInfo.getPurchaseNum() < 1){
                    goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            } else {
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        if (goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).count() < 1) {
            return goodsInfoList;
        }

        List<Long> storeIds = goodsInfoList.stream().map(BulkGoodsInfo::getStoreId).distinct().collect(Collectors.toList());
        ListNoDeleteStoreByIdsRequest listNoDeleteStoreByIdsRequest = new ListNoDeleteStoreByIdsRequest();
        listNoDeleteStoreByIdsRequest.setStoreIds(storeIds);
        BaseResponse<ListNoDeleteStoreByIdsResponse> listNoDeleteStoreByIdsResponseBaseResponse = storeQueryProvider.listNoDeleteStoreByIds(listNoDeleteStoreByIdsRequest);
        Map<Long, StoreVO> storeMap = listNoDeleteStoreByIdsResponseBaseResponse.getContext().getStoreVOList().stream().collect(Collectors.toMap(StoreVO::getStoreId, s -> s));
        LocalDateTime now = LocalDateTime.now();
        goodsInfoList.stream().filter(goodsInfo -> !Objects.equals(GoodsStatus.INVALID, goodsInfo.getGoodsStatus())).forEach(goodsInfo -> {
            StoreVO store = storeMap.get(goodsInfo.getStoreId());
            if (!(store != null
                    && Objects.equals(DeleteFlag.NO, store.getDelFlag())
                    && Objects.equals(StoreState.OPENING, store.getStoreState())
                    && (now.isBefore(store.getContractEndDate()) || now.isEqual(store.getContractEndDate()))
                    && (now.isAfter(store.getContractStartDate()) || now.isEqual(store.getContractStartDate()))
            )) {
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });
        return goodsInfoList;
    }



    public List<BulkGoodsInfo> queryBygoodsId(String goodsId) {
        List<String> goodsIds = new ArrayList<>();
        goodsIds.add(goodsId);
        List<BulkGoodsInfo> goodsInfoList = bulkGoodsInfoRepository.findByGoodsIds(goodsIds);
        if (CollectionUtils.isEmpty(goodsInfoList)) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
      /* if (wmsAPIFlag){
           goodsInfoList.get(0).setStock(queryStorkByWMS(InventoryQueryRequest.builder()
                   .customerID(AbstractXYYConstant.CUSTOMER_ID)
                   .sku(goodsInfoList.get(0).getGoodsInfoNo()).build()).getContext().getInventoryQueryReturnVO().getQty());
       }*/
        // 商户库存转供应商库存
        // this.turnProviderStock(goodsInfoList);
        return goodsInfoList;
    }

    /**
     * 根据id批量查询SKU数据
     *
     * @param skuIds 商品skuId
     * @return 商品sku列表
     */
    public List<BulkGoodsInfo> findGoodsInfoAndStockByIds(List<String> skuIds, Boolean matchWareHouseFlag) {
        List<BulkGoodsInfo> goodsInfoList = this.bulkGoodsInfoRepository.findAllById(skuIds);
//        Map<String, Long> goodsNumsMap = getGoodsPileNumBySkuIds(skuIds);
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList = goodsWareStockService.findByGoodsInfoIdIn(skuIds);
        Map<String, BigDecimal> stockMap = new HashMap<>();
        Map<String, List<GoodsWareStock>> goodsWareStockMap = new HashMap<>();
        List<Long> unOnline = new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            for (String goodsInfoId : skuIds) {
                List<GoodsWareStock> stockList;
                if (CollectionUtils.isNotEmpty(unOnline)) {

                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> goodsInfoId.equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    stockMap.put(goodsInfoId, sumStock);
                    goodsWareStockMap.put(goodsInfoId,stockList);
                } else {
                    stockMap.put(goodsInfoId, BigDecimal.ZERO);
                }
            }
        }
        goodsInfoList.forEach(goodsInfo -> {
            if (Objects.nonNull(stockMap.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setStock(stockMap.get(goodsInfo.getGoodsInfoId()).setScale(0,BigDecimal.ROUND_DOWN).longValue());
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }else{
//                    calGoodsInfoStock(goodsInfo,goodsNumsMap);
//                }
            } else {
                goodsInfo.setStock(0L);
//                if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
//                    goodsInfo.setStock(goodsInfo.getPurchaseNum());
//                }
            }
            if (CollectionUtils.isNotEmpty(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()))) {
                goodsInfo.setGoodsWareStocks(goodsWareStockMap.get(goodsInfo.getGoodsInfoId()));
            }
        });
        return goodsInfoList;
    }

    /**
     * 查询商品信息
     *
     * @param goodsInfoIds
     * @return
     */
    public List<RetailGoodsInfo> findGoodsInfoByIds(List<String> goodsInfoIds, Boolean matchWareHouseFlag) {
        List<Object> goodsInfoIdObj = bulkGoodsInfoRepository.findGoodsInfoByIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIdObj)) {
            List<RetailGoodsInfo> goodsInfoList = resultToGoodsInfoList(goodsInfoIdObj);
            List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
            if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
                List<Long> unOnline = null;
                if (Objects.nonNull(matchWareHouseFlag) && !matchWareHouseFlag) {
                    List<Long> storeList = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
                    unOnline = wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE).stream()
                            .map(WareHouseVO::getWareId).collect(Collectors.toList());
                }
                for (RetailGoodsInfo goodsInfo : goodsInfoList) {
                    List<GoodsWareStock> stockList;
                    if (CollectionUtils.isNotEmpty(unOnline)) {
                        List<Long> finalUnOnline = unOnline;
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                        && finalUnOnline.contains(goodsWareStock.getWareId())).
                                collect(Collectors.toList());
                    } else {
                        stockList = goodsWareStocks.stream().
                                filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                                collect(Collectors.toList());
                    }
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                        goodsInfo.setStock(sumStock.longValue());
                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
                        }
                    } else {
                        goodsInfo.setStock(0L);
                        if(Objects.nonNull(goodsInfo.getMarketingId()) && goodsInfo.getPurchaseNum() > 0){
                            goodsInfo.setStock(goodsInfo.getPurchaseNum());
                        }
                    }
                    if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                            && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())
                            && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OK);
                        if (goodsInfo.getStock() < 1) {
                            goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                        }
                    } else {
                        goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                    }
                }

                return goodsInfoList;
            }
        } else {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    /**
     * 功能描述: <br>
     * 〈〉组装商品信息
     *
     * @Param: [resultsObj]
     * @Return: java.util.List<com.wanmi.sbc.goods.info.model.root.GoodsInfo>
     * @Author: yxb
     * @Date: 2021/2/2 14:26
     */
    private List<RetailGoodsInfo> resultToGoodsInfoList(List<Object> resultsObj) {
        return resultsObj.stream().map(item -> {
            RetailGoodsInfo goodsInfo = new RetailGoodsInfo();
            Object[] results = StringUtil.cast(item, Object[].class);
            goodsInfo.setGoodsId(StringUtil.cast(results, 0, String.class));
            goodsInfo.setGoodsInfoId(StringUtil.cast(results, 1, String.class));
            String goodsInfoImg = StringUtil.cast(results, 2, String.class);
            if (StringUtils.isNotBlank(goodsInfoImg)) {
                goodsInfo.setGoodsInfoImg(goodsInfoImg);
            } else {
                String goodsImg = StringUtil.cast(results, 3, String.class);
                if (StringUtils.isNotBlank(goodsImg)) {
                    goodsInfo.setGoodsInfoImg(goodsImg);
                } else {
                    goodsInfo.setGoodsInfoImg("");
                }
            }
            goodsInfo.setMarketPrice(StringUtil.cast(results, 4, BigDecimal.class));
            goodsInfo.setGoodsUnit(StringUtil.cast(results, 5, String.class));
            goodsInfo.setGoodsInfoName(StringUtil.cast(results, 6, String.class));
            int delFlag = StringUtil.cast(results, 7, Byte.class).intValue();
            goodsInfo.setDelFlag(DeleteFlag.fromValue(delFlag));
            int addFlag = StringUtil.cast(results, 8, Byte.class).intValue();
            int auditFlag = StringUtil.cast(results, 9, Byte.class).intValue();
            int checkedAddFlag = StringUtil.cast(results,10,Byte.class).intValue();
            goodsInfo.setCheckedAddedFlag(checkedAddFlag);
            goodsInfo.setAddedFlag(addFlag);
            goodsInfo.setAuditStatus(CheckStatus.fromValue(auditFlag));

            return goodsInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据ID批量查询商品SKU(过滤线上仓)
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public BulkGoodsInfoResponse findSkuByIdsAndMatchFlag(GoodsInfoRequest infoRequest) {
        if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
            return BulkGoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询SKU信息列表
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
        queryRequest.setStoreId(infoRequest.getStoreId());
        if (infoRequest.getDeleteFlag() != null) {
            queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
        }
        List<BulkGoodsInfo> goodsInfos = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isEmpty(goodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{goodsInfos});
        }

        //设置仓库的库存
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfos.stream()
                .map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        List<Long> unOnline = new ArrayList<>(10);
        if (Objects.nonNull(infoRequest.getMatchWareHouseFlag()) && !infoRequest.getMatchWareHouseFlag()) {
            List<Long> collect = goodsWareStocks.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline = wareHouseService.queryWareHouses(collect, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());

        }
        if (CollectionUtils.isNotEmpty(goodsWareStocks)) {
            List<GoodsWareStock> stockList;
            for (BulkGoodsInfo g : goodsInfos) {
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStocks.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStocks.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }
                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    g.setStock(sumStock.longValue());
//                    if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                        g.setStock(g.getPurchaseNum());
//                    }
                } else {
                    g.setStock(0L);
//                    if(Objects.nonNull(g.getMarketingId()) && g.getPurchaseNum() > 0){
//                        g.setStock(g.getPurchaseNum());
//                    }
                }
            }
        }
        //批量查询SPU信息列表
        List<String> goodsIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<BulkGoods> goodses = bulkGoodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, BulkGoods> goodsMap = goodses.stream().collect(Collectors.toMap(BulkGoods::getGoodsId, g -> g));


        List<String> skuIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
        }

        //遍历SKU，填充销量价、商品状态
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            BulkGoods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }

                if (Objects.equals(DeleteFlag.NO, goodsInfo.getDelFlag())
                        && Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus()) && Objects.equals(AddedFlag.YES.toValue(), goodsInfo.getAddedFlag())) {
                    goodsInfo.setGoodsStatus(GoodsStatus.OK);
                    if (Objects.isNull(goodsInfo.getStock()) || goodsInfo.getStock() < 1) {
                        goodsInfo.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                } else {
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        BulkGoodsInfoResponse responses = new BulkGoodsInfoResponse();
        responses.setGoodsInfos(goodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }


    /**
     * 根据ID批量查询商品SKU(过滤线上仓)
     *
     * @param infoRequest 查询参数
     * @return 商品列表响应
     */
    @Transactional(readOnly = true, timeout = 10, propagation = Propagation.REQUIRES_NEW)
    public BulkGoodsInfoResponse findSkuByIdsAndMatchFlagNoStock(GoodsInfoRequest infoRequest) {
        if (CollectionUtils.isEmpty(infoRequest.getGoodsInfoIds())) {
            return BulkGoodsInfoResponse.builder().goodsInfos(new ArrayList<>()).goodses(new ArrayList<>()).build();
        }
        //批量查询SKU信息列表
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(infoRequest.getGoodsInfoIds());
        queryRequest.setStoreId(infoRequest.getStoreId());
        if (infoRequest.getDeleteFlag() != null) {
            queryRequest.setDelFlag(infoRequest.getDeleteFlag().toValue());
        }
        List<BulkGoodsInfo> goodsInfos = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isEmpty(goodsInfos)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST, new Object[]{goodsInfos});
        }

        //批量查询SPU信息列表
        List<String> goodsIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsId).collect(Collectors.toList());
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);
        List<BulkGoods> goodses = bulkGoodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isEmpty(goodses)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        Map<String, BulkGoods> goodsMap = goodses.stream().collect(Collectors.toMap(BulkGoods::getGoodsId, g -> g));

        List<String> skuIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        //对每个SKU填充规格和规格值关系
        Map<String, List<GoodsInfoSpecDetailRel>> specDetailMap = new HashMap<>();
        //如果需要规格值，则查询
//        if (Constants.yes.equals(infoRequest.getIsHavSpecText())) {
//            specDetailMap.putAll(goodsInfoSpecDetailRelRepository.findByAllGoodsInfoIds(skuIds).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId)));
//        }

        //遍历SKU，填充销量价、商品状态
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(goodsInfo.getMarketPrice() == null ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            BulkGoods goods = goodsMap.get(goodsInfo.getGoodsId());
            if (goods != null) {
                //建立扁平化数据
                if (goods.getGoodsInfoIds() == null) {
                    goods.setGoodsInfoIds(new ArrayList<>());
                }
                goods.getGoodsInfoIds().add(goodsInfo.getGoodsInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setGoodsUnit(goods.getGoodsUnit());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setGoodsCubage(goods.getGoodsCubage());
                goodsInfo.setGoodsWeight(goods.getGoodsWeight());
                goodsInfo.setFreightTempId(goods.getFreightTempId());
                goodsInfo.setGoodsEvaluateNum(goods.getGoodsEvaluateNum());
                goodsInfo.setGoodsSalesNum(goods.getGoodsSalesNum());
                goodsInfo.setGoodsCollectNum(goods.getGoodsCollectNum());
                goodsInfo.setGoodsFavorableCommentNum(goods.getGoodsFavorableCommentNum());

                //为空，则以商品主图
                if (StringUtils.isEmpty(goodsInfo.getGoodsInfoImg())) {
                    goodsInfo.setGoodsInfoImg(goods.getGoodsImg());
                }

                //填充规格值
                if (MapUtils.isNotEmpty(specDetailMap) && Constants.yes.equals(goods.getMoreSpecFlag())) {
                    goodsInfo.setSpecText(StringUtils.join(specDetailMap.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream()
                            .map(GoodsInfoSpecDetailRel::getDetailName)
                            .collect(Collectors.toList()), " "));
                }
            } else {//不存在，则做为删除标记
                goodsInfo.setDelFlag(DeleteFlag.YES);
                goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
            }
        });

        //定义响应结果
        BulkGoodsInfoResponse responses = new BulkGoodsInfoResponse();
        responses.setGoodsInfos(goodsInfos);
        responses.setGoodses(goodses);
        return responses;
    }

    /**
     * 单表ID查询goodsInfo
     */
    public GoodsInfoResponse findGoodInfoByIds(SpecialGoodsModifyRequest request) {
        GoodsInfoResponse response = new GoodsInfoResponse();
        List<BulkGoodsInfo> goodsInfoList = bulkGoodsInfoRepository.findByGoodsInfoIds(request.getGoodsInfoIdList());
        response.setGoodsInfos(KsBeanUtil.convert(goodsInfoList,GoodsInfo.class));
        return response;

    }

    /**
     * SKU统计
     *
     * @param queryRequest 查询请求
     * @return 统计个数
     */
    @Transactional(readOnly = true, timeout = 10)
    public long count(GoodsInfoQueryRequest queryRequest) {
        return bulkGoodsInfoRepository.count(queryRequest.getBulkWhereCriteria());
    }

    /**
     * 批量更新商品的批次号
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse batchUpdateGoodsInfoBatchNo(List<GoodsBatchNoDTO> goodsBatchNoDTOS) {
        goodsBatchNoDTOS.stream().forEach(g ->
                this.updateGoodsInfoBatchNo(g.getGoodsInfoBatchNo(), g.getGoodsInfoId()));
        return BaseResponse.SUCCESSFUL();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId) {
        bulkGoodsInfoRepository.updateGoodsInfoBatchNo(goodsInfoBatchNo, goodsInfoId);
    }

    public List<GoodsInfoVO> findByRetailGoodsInfoIsNull(){
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setRetailImgFlag(Boolean.TRUE);
        List<BulkGoodsInfo> retailGoodsInfos = bulkGoodsInfoRepository.findAll(queryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isNotEmpty(retailGoodsInfos)) {
            return KsBeanUtil.convertList(retailGoodsInfos,GoodsInfoVO.class);
        } else {
            return Collections.emptyList();
        }
    }

    public Map<String,List<GoodsImageVO>> getGoodsImagesOfMap(List<String> spuIds) {
        Map<String,List<GoodsImageVO>> goodsImageMap = new HashMap();
        spuIds.forEach(spuId -> {
            List<GoodsImage> goodsImages = goodsImageRepository.findByGoodsId(spuId);
            if (CollectionUtils.isNotEmpty(goodsImages)) {
                goodsImageMap.put(spuId,KsBeanUtil.convertList(goodsImages,GoodsImageVO.class));
            }
        });
        return goodsImageMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateGoodsInfoImg(List<GoodsInfoVO> goodsInfoVOS) {
        goodsInfoVOS.forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsInfoVO.getGoodsInfoImg())) {
                bulkGoodsInfoRepository.updateGoodsInfoImg(goodsInfoVO.getGoodsInfoId(),goodsInfoVO.getGoodsInfoImg());
            }
        });
    }

    /**
     * 查询erp不为空的商品
     *
     * @return
     */
    public List<BulkGoodsInfo> findAllByErp() {
        return bulkGoodsInfoRepository.findAllByErp();
    }

    /**
     * 通过erp获取上架未删除商品
     * @param erpNo
     * @return
     */
    public BulkGoodsInfo findRetailGoodsInfoByErpNo(String erpNo) {
        BulkGoodsInfo retailGoodsInfo = bulkGoodsInfoRepository.findBulkGoodsInfoByErpGoodsInfoNoAndAddedFlagAndDelFlag(erpNo, DefaultFlag.YES.toValue(), DeleteFlag.NO);
        return retailGoodsInfo;
    }

    /**
     * 条件查询SKU数据
     *
     * @param request 查询条件
     * @return 商品sku列表
     */
    public List<BulkGoodsInfo> listByParentId(DevanningGoodsInfoQueryRequest request) {
        return bulkGoodsInfoRepository.queryGoodsInfoByParentId(request.getParentGoodsInfoId());
    }
}
