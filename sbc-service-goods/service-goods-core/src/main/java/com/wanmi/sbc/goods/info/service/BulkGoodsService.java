package com.wanmi.sbc.goods.info.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.customer.api.constant.SigningClassErrorCode;
import com.wanmi.sbc.customer.api.constant.StoreCateErrorCode;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.request.goods.GoodsBatchModifyCateRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsModifyCollectNumRequest;
import com.wanmi.sbc.goods.api.request.goodslabel.GoodsLabelQueryRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.ContractBrandRepository;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.brand.request.ContractBrandQueryRequest;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.ContractCateRepository;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.ContractCateQueryRequest;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.common.GoodsCommonService;
import com.wanmi.sbc.goods.distributor.goods.repository.DistributiorGoodsInfoRepository;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsRepository;
import com.wanmi.sbc.goods.goodslabel.model.root.GoodsLabel;
import com.wanmi.sbc.goods.goodslabel.service.GoodsLabelService;
import com.wanmi.sbc.goods.goodslabelrela.model.root.GoodsLabelRela;
import com.wanmi.sbc.goods.goodslabelrela.repository.GoodsLabelRelaRepository;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.BulkGoodsQueryResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.RetailGoodsQueryResponse;
import com.wanmi.sbc.goods.info.repository.*;
import com.wanmi.sbc.goods.info.request.BulkGoodsSaveRequest;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.RetailGoodsSaveRequest;
import com.wanmi.sbc.goods.pointsgoods.repository.PointsGoodsRepository;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
import com.wanmi.sbc.goods.price.repository.GoodsCustomerPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsIntervalPriceRepository;
import com.wanmi.sbc.goods.price.repository.GoodsLevelPriceRepository;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.goods.storecate.repository.StoreCateRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import com.wanmi.sbc.goods.storegoodstab.model.root.GoodsTabRela;
import com.wanmi.sbc.goods.storegoodstab.model.root.StoreGoodsTab;
import com.wanmi.sbc.goods.storegoodstab.repository.GoodsTabRelaRepository;
import com.wanmi.sbc.goods.storegoodstab.repository.StoreGoodsTabRepository;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 零售spu服务
 * @author: XinJiang
 * @time: 2022/3/8 15:56
 */
@Slf4j
@Service
public class BulkGoodsService {


    @Autowired
    BulkGoodsInfoRepository bulkGoodsInfoRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private BulkGoodsRepository goodsRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsBrandRepository goodsBrandRepository;

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private GoodsLabelRelaRepository goodsLabelRelaRepository;

    @Autowired
    private GoodsLabelService goodsLabelService;

    @Autowired
    private FreightTemplateGoodsRepository freightTemplateGoodsRepository;

    @Autowired
    private StoreGoodsTabRepository storeGoodsTabRepository;

    @Autowired
    private GoodsTabRelaRepository goodsTabRelaRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private BulkGoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsPropDetailRelRepository goodsPropDetailRelRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsIntervalPriceRepository goodsIntervalPriceRepository;

    @Autowired
    private GoodsLevelPriceRepository goodsLevelPriceRepository;

    @Autowired
    private GoodsCustomerPriceRepository goodsCustomerPriceRepository;

    @Autowired
    private DistributiorGoodsInfoRepository distributiorGoodsInfoRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private PointsGoodsRepository pointsGoodsRepository;

    @Autowired
    private GoodsAresService goodsAresService;

    @Autowired
    private OsUtil osUtil;

    @Autowired
    private ContractCateRepository contractCateRepository;

    @Autowired
    private ContractBrandRepository contractBrandRepository;

    @Autowired
    private StoreCateRepository storeCateRepository;

    @Autowired
    private GoodsCommonService goodsCommonService;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;

    @Autowired
    private StandardSkuRepository standardSkuRepository;
    @Autowired
    private StoreQueryProvider storeQueryProvider;
    @Value("${retail.retailWareId}")
    private Long retailWareId;

    /**
     * 分页查询商品
     *
     * @param request 参数
     * @return list
     */
    public BulkGoodsQueryResponse page(GoodsQueryRequest request) {
        BulkGoodsQueryResponse response = new BulkGoodsQueryResponse();

        List<BulkGoodsInfo> goodsInfos = new ArrayList<>();
        List<GoodsInfoSpecDetailRel> goodsInfoSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();

        //根据SKU模糊查询SKU，获取SKU编号
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setStoreIds(request.getStoreIds());
        infoQueryRequest.setStockUp(request.getStockUp());
        //是否先查询SKU信息，获取SKU编号(默认不查询)
        Boolean checkSKU = false;
        if (StringUtils.isNotBlank(request.getLikeGoodsInfoNo()) || StringUtils.isNotBlank(request.getLikeErpNo())) {
            infoQueryRequest.setLikeGoodsInfoNo(request.getLikeGoodsInfoNo());
            infoQueryRequest.setLikeErpNo(request.getLikeErpNo());
            checkSKU = true;
        }
        if (Objects.nonNull(request.getSpecialPriceFirst()) || Objects.nonNull(request.getSpecialPriceLast())
                || StringUtils.isNotEmpty(request.getGoodsInfoBatchNo())) {
            infoQueryRequest.setSpecialPriceFirst(request.getSpecialPriceFirst());
            infoQueryRequest.setSpecialPriceLast(request.getSpecialPriceLast());
            infoQueryRequest.setGoodsInfoBatchNo(request.getGoodsInfoBatchNo());
            checkSKU = true;
        }
        if (checkSKU) {
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            infoQueryRequest.setGoodsInfoType(request.getGoodsInfoType());
            List<BulkGoodsInfo> infos = bulkGoodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria());
            if (CollectionUtils.isNotEmpty(infos)) {
                request.setGoodsIds(infos.stream().map(BulkGoodsInfo::getGoodsId).collect(Collectors.toList()));
            } else {
                return response;
            }
        }

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if (CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }

        if (Objects.isNull(request.getStockUp())) {
            Page<BulkGoods> goodsPage = goodsRepository.findAll(request.getBulkWhereCriteria(), request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
                List<String> goodsIds = goodsPage.getContent().stream().map(BulkGoods::getGoodsId).collect(Collectors.toList());
                //查询所有SKU
                infoQueryRequest.setLikeGoodsInfoNo(null);
                infoQueryRequest.setGoodsIds(goodsIds);
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
                goodsInfos.addAll(bulkGoodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria()));

                // 商户库存转供应商库存
                // goodsInfoService.turnProviderStock(goodsInfos);

                //查询所有SKU规格值关联
                goodsInfoSpecDetails.addAll(goodsInfoSpecDetailRelRepository.findByGoodsIds(goodsIds));

                //填充每个SKU的规格关系
                goodsInfos.forEach(goodsInfo -> {
                    //为空，则以商品主图
                    if (StringUtils.isBlank(goodsInfo.getGoodsInfoImg())) {
                        goodsInfo.setGoodsInfoImg(goodsPage.getContent().stream().filter(goods -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).findFirst().orElse(new BulkGoods()).getGoodsImg());
                    }

                    goodsInfo.setSpecDetailRelIds(goodsInfoSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).map(GoodsInfoSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));


                });

                //填充每个SKU的SKU关系
                goodsPage.getContent().forEach(goods -> {
                    goods.setGoodsInfoIds(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId())).map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
                    //合计库存
                    goods.setStock(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getStock())).mapToLong(BulkGoodsInfo::getStock).sum());
                    //取SKU最小市场价
                    goods.setMarketPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getMarketPrice())).map(BulkGoodsInfo::getMarketPrice).min(BigDecimal::compareTo).orElse(goods.getMarketPrice()));
                    //取SKU最小供货价
                    goods.setSupplyPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSupplyPrice())).map(BulkGoodsInfo::getSupplyPrice).min(BigDecimal::compareTo).orElse(goods.getSupplyPrice()));
                    //取商品类型是否特价
                    List<BulkGoodsInfo> goodsInfoList =
                            goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && goodsInfo.getGoodsInfoType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(goodsInfoList)) {
                        goods.setGoodsType(2);//特价
                    }
                    //取商品特价价格
                    goods.setSpecialPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getSpecialPrice())).map(BulkGoodsInfo::getSpecialPrice).min(BigDecimal::compareTo).orElse(goods.getSpecialPrice()));

                    goods.setGoodsInfoBatchNo(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId()) && !Objects.equals(goodsInfo.getGoodsInfoType(), 0)).filter(goodsInfo -> Objects.nonNull(goodsInfo.getGoodsInfoBatchNo())).map(BulkGoodsInfo::getGoodsInfoBatchNo).findFirst().orElse(""));

                    goods.setVipPrice(goodsInfos.stream().filter(goodsInfo -> goods.getGoodsId().equals(
                                    goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getVipPrice()) && goodsInfo.getVipPrice().compareTo(BigDecimal.ZERO) > 0)
                            .map(BulkGoodsInfo::getVipPrice).min(BigDecimal::compareTo).orElse(goods.getVipPrice()));
                    goods.setMarketingId(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(BulkGoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfos.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(BulkGoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });

                //获取所有品牌
                GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
                brandRequest.setDelFlag(DeleteFlag.NO.toValue());
                brandRequest.setBrandIds(goodsPage.getContent().stream().filter
                        (goods -> Objects.nonNull(goods.getBrandId())).map(BulkGoods::getBrandId).distinct().collect(Collectors.toList()));
                goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

                //获取所有分类
                GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
                cateRequest.setCateIds(goodsPage.getContent().stream().filter(goods -> Objects.nonNull(goods.getCateId())).map(BulkGoods::getCateId).distinct().collect(Collectors.toList()));
                goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
            }
            response.setGoodsPage(goodsPage);
            response.setGoodsInfoList(goodsInfos);
            response.setGoodsInfoSpecDetails(goodsInfoSpecDetails);
            response.setGoodsBrandList(goodsBrandList);
            response.setGoodsCateList(goodsCateList);
        }else {
            List<BulkGoods> goodsList = new ArrayList<>();
            Page<BulkGoodsInfo> goodsInfoPage = bulkGoodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria(),request.getPageRequest());
            if (CollectionUtils.isNotEmpty(goodsInfoPage.getContent())){
                List<BulkGoodsInfo> goodsInfoList = goodsInfoPage.getContent();
                List<String> goodsIds = goodsInfoPage.stream().map(BulkGoodsInfo::getGoodsId).collect(Collectors.toList());
                GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
                goodsQueryRequest.setGoodsIds(goodsIds);
                goodsQueryRequest.setAuditStatus(CheckStatus.CHECKED);
                goodsList.addAll(goodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria()));
                goodsList.stream().forEach(goods -> {
                    goods.setMarketingId(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getMarketingId())).map(BulkGoodsInfo::getMarketingId).findFirst().orElse(0L));
                    goods.setPurchaseNum(goodsInfoList.stream().filter(goodsInfo -> goodsInfo.getGoodsId().equals(goods.getGoodsId()) && Objects.nonNull(goodsInfo.getPurchaseNum())).map(BulkGoodsInfo::getPurchaseNum).findFirst().orElse(0L));
                });
            }
            response.setGoodsPage(new PageImpl<>(goodsList,goodsInfoPage.getPageable(),goodsInfoPage.getTotalElements()));
        }

        return response;
    }


    /**
     * 根据ID查询商品
     *
     * @param goodsId 商品ID
     * @return list
     */
    public BulkGoodsEditResponse findInfoById(String goodsId, Long wareId, Boolean matchWareHouseFlag) throws SbcRuntimeException {
        BulkGoodsEditResponse response = new BulkGoodsEditResponse();
        BulkGoods goods = goodsRepository.findById(goodsId).orElse(null);
        // log.info("商品信息================="+goods.toString());
        List<GoodsLabel> labels = new ArrayList<>();

        //从关联表获取标签数据
        Optional<List<GoodsLabelRela>> byGoodsId = goodsLabelRelaRepository.findByGoodsId(goods.getGoodsId());
        if (byGoodsId.isPresent()) {
            List<GoodsLabelRela> goodsLabelRelas = byGoodsId.get();
            List<Long> idList = goodsLabelRelas.stream().map(GoodsLabelRela::getLabelId).collect(Collectors.toList());
            labels = goodsLabelService.list(GoodsLabelQueryRequest.builder()
                    .idList(idList)
                    .visible(1)
                    .delFlag(DeleteFlag.NO).build());
            goods.setGoodsLabels(labels);
            if (CollectionUtils.isNotEmpty(labels)) {
                String join = StringUtils.join(idList, ",");
                goods.setLabelIdStr(join);
            }
        } else {
            goods.setLabelIdStr(null);
        }

        if (Objects.isNull(goods)) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        if (Objects.nonNull(goods.getFreightTempId())) {
            FreightTemplateGoods goodsTemplate = freightTemplateGoodsRepository.queryById(goods.getFreightTempId());
            if (Objects.nonNull(goodsTemplate)) {
                goods.setFreightTempName(goodsTemplate.getFreightTempName());
            }
        }
        response.setGoods(goods);

        // 获取商家所绑定的模板列表
        List<StoreGoodsTab> storeGoodsTabs = storeGoodsTabRepository.queryTabByStoreId(goods.getStoreId());
        response.setStoreGoodsTabs(storeGoodsTabs);
        if (CollectionUtils.isNotEmpty(storeGoodsTabs)) {
            storeGoodsTabs.sort(Comparator.comparingInt(StoreGoodsTab::getSort));
            List<GoodsTabRela> goodsTabRelas = goodsTabRelaRepository.queryListByTabIds(goodsId, storeGoodsTabs.stream().map(StoreGoodsTab::getTabId).collect(Collectors.toList()));
            response.setGoodsTabRelas(goodsTabRelas);
        }

        //查询商品图片
        response.setImages(goodsImageRepository.findByGoodsId(goods.getGoodsId()));

        //查询SKU列表
        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setGoodsId(goods.getGoodsId());
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        List<BulkGoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria());

        // 商户库存转供应商库存
        // goodsInfoService.turnProviderStock(goodsInfos);

        List<GoodsLabel> finalLabels = labels;
        goodsInfos.forEach(goodsInfo -> {
            goodsInfo.setSalePrice(Objects.isNull(goodsInfo.getMarketPrice()) ? BigDecimal.ZERO : goodsInfo.getMarketPrice());
            goodsInfo.setPriceType(goods.getPriceType());
            goodsInfo.setBrandId(goods.getBrandId());
            goodsInfo.setCateId(goods.getCateId());
            goodsInfo.setGoodsLabels(finalLabels);
        });

        //查询sku库存信息
        List<String> goodsInfoIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(goodsInfoIds);
        List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
        List<Long> wareIds = goodsWareStocks.stream().map(GoodsWareStock::getWareId).collect(Collectors.toList());
        List<WareHouse> wareHouses = wareHouseRepository.findByWareIdIn(wareIds);
        goodsWareStocks.forEach(goodsWareStock -> {
            wareHouses.forEach(wareHouse -> {
                if (goodsWareStock.getWareId().equals(wareHouse.getWareId())) {
                    goodsWareStock.setWareName(wareHouse.getWareName());
                }
            });
        });
        //如果不能匹配到仓，需要去除线上仓
        List<Long> unOnline = new ArrayList<>(10);
        if (Objects.nonNull(matchWareHouseFlag)&&!matchWareHouseFlag){
            List<WareHouse> onlineWareHouses = wareHouseService
                    .list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).wareHouseType(WareHouseType.STORRWAREHOUSE).storeId(goods.getStoreId()).build());
            List<Long> onlineWareIds= onlineWareHouses.stream().map(WareHouse::getWareId).collect(Collectors.toList());
            unOnline=onlineWareIds;
            goodsWareStocks = goodsWareStocks.stream().filter(param -> onlineWareIds.contains(param.getWareId())).collect(Collectors.toList());
        }
        List<GoodsWareStock> finalGoodsWareStocks = goodsWareStocks;
        List<Long> finalUnOnline1 = unOnline;
        goodsInfos.forEach(goodsInfo -> {
            List<GoodsWareStock> stockList;
            if (CollectionUtils.isNotEmpty(finalUnOnline1)) {
                List<Long> finalUnOnline = finalUnOnline1;
                stockList = finalGoodsWareStocks.stream().
                        filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                && finalUnOnline.contains(goodsWareStock.getWareId())).
                        collect(Collectors.toList());
            } else {
                stockList = finalGoodsWareStocks.stream().
                        filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                        collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(stockList)){
                BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                goodsInfo.setStock(sumStock.setScale(0,BigDecimal.ROUND_DOWN).longValue());
            }else {
                goodsInfo.setStock(0l);
            }
            //计算商品虚拟库存
//           goodsInfoService.calGoodsInfoStock(goodsInfo,goodsNumsMap);
            goodsInfo.setGoodsWareStocks(stockList);
        });

        //商品属性
        response.setGoodsPropDetailRels(goodsPropDetailRelRepository.queryByGoodsId(goods.getGoodsId()));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(goodsSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(goodsSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(goodsSpec -> {
                goodsSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(goodsSpec.getSpecId())).map(GoodsSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            Map<String, List<GoodsInfoSpecDetailRel>> goodsInfoSpecDetailRels = goodsInfoSpecDetailRelRepository.findByGoodsId(goods.getGoodsId()).stream().collect(Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId));
            goodsInfos.forEach(goodsInfo -> {
                goodsInfo.setMockSpecIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecId).collect(Collectors.toList()));
                goodsInfo.setMockSpecDetailIds(goodsInfoSpecDetailRels.getOrDefault(goodsInfo.getGoodsInfoId(), new ArrayList<>()).stream().map(GoodsInfoSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            });
        }
        response.setGoodsInfos(goodsInfos);

        //商品按订货区间，查询订货区间
        if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
            response.setGoodsIntervalPrices(goodsIntervalPriceRepository.findByGoodsId(goods.getGoodsId()));
        } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
            response.setGoodsLevelPrices(goodsLevelPriceRepository.findByGoodsId(goods.getGoodsId()));
            //如果是按单独客户定价
            if (Constants.yes.equals(goods.getCustomFlag())) {
                response.setGoodsCustomerPrices(goodsCustomerPriceRepository.findByGoodsId(goods.getGoodsId()));
            }
        }

        return response;
    }

    /**
     * 根据商品id查询零售Goods
     * @param goodsId
     * @return
     */
    public BulkGoods getRetailGoodsById(String goodsId) {
        return goodsRepository.findById(goodsId).orElse(null);
    }


    /**
     * 编辑零售商品排序
     * @param goodsId
     * @param goodsSeqNum
     */
    @Transactional
    public void modifyGoodsSeqNum(String goodsId, Integer goodsSeqNum) {
        //验证商品排序序号是否重复
        if(goodsSeqNum!=null){
            List<BulkGoods> _goodsList = goodsRepository.getExistByGoodsSeqNum(goodsSeqNum, goodsId);
            if (CollectionUtils.isNotEmpty(_goodsList)) {
                throw new SbcRuntimeException(GoodsErrorCode.SEQ_NUM_ALREADY_EXISTS);
            }
        }
        goodsRepository.modifyGoodsSeqNum(goodsSeqNum, goodsId);
    }
    /**
     * 根据商品id批量查询RetailGoods
     * @param goodsIds
     * @return
     */
    public List<BulkGoods> listRetailGoodsByGoodsIds(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);

        List<BulkGoods> goodsList = goodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());

        for (BulkGoods goods : goodsList) {
            // 商品上架，带T/t,校验是否有库存，没有库存给出提示
            if (isErpGoods(goods.getGoodsName())) {
                //查询SKU列表
                GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
                infoQueryRequest.setGoodsId(goods.getGoodsId());
                infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
                List<BulkGoodsInfo> goodsInfos = goodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria());


                //查询sku库存信息
                List<String> goodsInfoIds = goodsInfos.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
                List<GoodsWareStock> goodsWareStocks = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIds);
                //如果不能匹配到仓，需要去除线上仓
//                List<WareHouse> onlineWareHouses = wareHouseService
//                        .list(WareHouseQueryRequest.builder().delFlag(DeleteFlag.NO).wareHouseType(WareHouseType.STORRWAREHOUSE).storeId(goods.getStoreId()).build());
//                List<Long> onlineWareIds = onlineWareHouses.stream().map(WareHouse::getWareId).collect(Collectors.toList());
//                goodsWareStocks = goodsWareStocks.stream().filter(param -> onlineWareIds.contains(param.getWareId())).collect(Collectors.toList());
                List<GoodsWareStock> finalGoodsWareStocks = goodsWareStocks;

                long sumStock = goodsInfos.stream().mapToLong(goodsInfo -> {
                    List<GoodsWareStock> stockList = finalGoodsWareStocks.stream().
                            filter(goodsWareStock -> goodsInfo.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        return stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add).longValue();
                    }
                    return 0l;
                }).sum();
                if (sumStock == 0l) {
                    throw new SbcRuntimeException("K-180004");
                }
            }
        }
        return goodsList;
    }



    /**
     * 更新商品上下架状态
     *
     * @param addedFlag 状态
     * @param goodsIds  多个商品
     * @throws SbcRuntimeException
     */
    @Transactional
    public void updateAddedStatus(Integer addedFlag, List<String> goodsIds) throws SbcRuntimeException {
        goodsRepository.updateAddedFlagByGoodsIds(addedFlag, goodsIds);
        goodsInfoRepository.updateAddedFlagByGoodsIds(addedFlag, goodsIds);
        if (0 == addedFlag) {
            goodsIds.forEach(goodsID->{
                distributiorGoodsInfoRepository.deleteByGoodsId(goodsID);
            });
        }
    }



    public List<BulkGoods> findAll(GoodsQueryRequest goodsQueryRequest){
        return goodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
    }

    /**
     * 商品删除
     *
     * @param goodsIds 多个商品
     * @throws SbcRuntimeException
     */
    @Transactional
    public void delete(List<String> goodsIds) throws SbcRuntimeException {

//
        List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByGoodsIds(goodsIds);
        List<String> standardIds = standardGoodsRels.stream().map(StandardGoodsRel::getStandardId).collect(Collectors.toList());

        // 关联商品下架
        goodsRepository.updateAddedFlagByPrividerGoodsIds(AddedFlag.NO.toValue(),goodsIds);



        goodsRepository.deleteByGoodsIds(goodsIds);
        goodsInfoRepository.deleteByGoodsIds(goodsIds);
        goodsPropDetailRelRepository.deleteByGoodsIds(goodsIds);
        goodsSpecRepository.deleteByGoodsIds(goodsIds);
        goodsSpecDetailRepository.deleteByGoodsIds(goodsIds);
        goodsInfoSpecDetailRelRepository.deleteByGoodsIds(goodsIds);
        standardGoodsRelRepository.deleteByGoodsIds(goodsIds);
        pointsGoodsRepository.deleteByGoodsIdList(goodsIds);
        goodsIds.forEach(goodsID->{
            distributiorGoodsInfoRepository.deleteByGoodsId(goodsID);
        });


        //ares埋点-商品-后台批量删除商品spu
        goodsAresService.dispatchFunction("delGoodsSpu", goodsIds);
    }


    /**
     * 根据商品id查询Goods
     * @param goodsId
     * @return
     */
    public BulkGoods getGoodsById(String goodsId) {
        return goodsRepository.findById(goodsId).orElse(null);
    }

    /**
     *
     * @param goodsIds
     * @return
     */
    public List<BulkGoods> listByProviderGoodsIds(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setProviderGoodsIds(goodsIds);
        return goodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
    }


    /**
     * 零售商品更新
     *
     * @param saveRequest
     * @throws SbcRuntimeException
     */
    @Transactional
    public Map<String, Object> edit(BulkGoodsSaveRequest saveRequest) throws SbcRuntimeException {
        BulkGoods newGoods = saveRequest.getGoods();
        BulkGoods oldGoods = goodsRepository.findById(newGoods.getGoodsId()).orElse(null);
        if (oldGoods == null || oldGoods.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }

        //如果S2B模式下，商品已审核无法编辑分类
        /*if (osUtil.isS2b() && CheckStatus.CHECKED.toValue() == oldGoods.getAuditStatus().toValue() && (!oldGoods.getCateId().equals(newGoods.getCateId()))) {
            throw new SbcRuntimeException(GoodsErrorCode.EDIT_GOODS_CATE);
        }*/

        //验证SPU编码重复
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsNo(newGoods.getGoodsNo());
        queryRequest.setNotGoodsId(newGoods.getGoodsId());
        if (goodsRepository.count(queryRequest.getBulkWhereCriteria()) > 0) {
            throw new SbcRuntimeException(GoodsErrorCode.SPU_NO_EXIST);
        }

        //更新关联的商品标签数据
        goodsLabelRelaRepository.deleteByGoodsId(oldGoods.getGoodsId());
        if (StringUtils.isNotBlank(newGoods.getLabelIdStr())) {
            List<GoodsLabelRela> goodsLabelRelasList = Arrays.asList(newGoods.getLabelIdStr().split(",")).stream().map(labelId -> {
                GoodsLabelRela goodsLabelRela = new GoodsLabelRela();
                goodsLabelRela.setGoodsId(oldGoods.getGoodsId());
                goodsLabelRela.setLabelId(Long.parseLong(labelId));
                return goodsLabelRela;
            }).collect(Collectors.toList());
            goodsLabelRelaRepository.saveAll(goodsLabelRelasList);
        }

        GoodsInfoQueryRequest infoQueryRequest = new GoodsInfoQueryRequest();
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        infoQueryRequest.setGoodsInfoNos(saveRequest.getGoodsInfos().stream().map(BulkGoodsInfo::getGoodsInfoNo).distinct().collect(Collectors.toList()));
        infoQueryRequest.setNotGoodsId(newGoods.getGoodsId());
        //如果SKU数据有重复
        if (saveRequest.getGoodsInfos().size() != infoQueryRequest.getGoodsInfoNos().size()) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_EXIST);
        }

        //验证SKU编码重复
        List<BulkGoodsInfo> goodsInfosList = goodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria());
        if (CollectionUtils.isNotEmpty(goodsInfosList)) {
            throw new SbcRuntimeException(GoodsErrorCode.SKU_NO_H_EXIST, new Object[]{StringUtils.join(goodsInfosList.stream().map(BulkGoodsInfo::getGoodsInfoNo).collect(Collectors.toList()), ",")});
        }

        if (Objects.isNull(newGoods.getPriceType())) {
            newGoods.setPriceType(GoodsPriceType.MARKET.toValue());
        }

        //验证商品相关基础数据
        newGoods.setStoreId(oldGoods.getStoreId());
        this.checkBasic(newGoods);

        List<GoodsImage> goodsImages = saveRequest.getImages();
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            newGoods.setGoodsImg(goodsImages.get(0).getArtworkUrl());
        } else {
            newGoods.setGoodsImg(null);
        }
        if (newGoods.getMoreSpecFlag() == null) {
            newGoods.setMoreSpecFlag(Constants.no);
        }
        if (newGoods.getCustomFlag() == null) {
            newGoods.setCustomFlag(Constants.no);
        }
        if (newGoods.getLevelDiscountFlag() == null) {
            newGoods.setLevelDiscountFlag(Constants.no);
        }

        LocalDateTime currDate = LocalDateTime.now();
        //更新商品
        newGoods.setUpdateTime(currDate);

        //上下架状态是否发生变化
        boolean isChgAddedTime = false;
        if (!oldGoods.getAddedFlag().equals(newGoods.getAddedFlag())) {
            isChgAddedTime = true;
        }

        //更新上下架时间
        if (isChgAddedTime) {
            newGoods.setAddedTime(newGoods.getUpdateTime());
        } else {
            newGoods.setAddedTime(oldGoods.getAddedTime());
        }

        //设价类型是否发生变化 -> 影响sku的独立设价状态为false
        boolean isChgPriceType = false;
        if (!newGoods.getPriceType().equals(oldGoods.getPriceType())) {
            isChgPriceType = true;
        }

        //如果设价方式变化为非按客户设价，则将spu市场价清空
        if (isChgPriceType
                && !Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(newGoods.getPriceType())) {
            newGoods.setMarketPrice(null);
            newGoods.setCustomFlag(Constants.no);
        }
        if (SaleType.RETAIL.toValue() == newGoods.getSaleType() && GoodsPriceType.STOCK.toValue() == newGoods.getPriceType()) {
            newGoods.setPriceType(GoodsPriceType.MARKET.toValue());
        }

        KsBeanUtil.copyProperties(newGoods, oldGoods);
        oldGoods.setGoodsWeight(newGoods.getGoodsWeight());
        // log.info("存入数据"+ JSONObject.toJSONString(oldGoods));
        // log.info("传输数据"+JSONObject.toJSONString(newGoods));
        goodsCommonService.setCheckState(oldGoods);
        goodsRepository.save(oldGoods);

        //更新图片
        List<GoodsImage> oldImages = goodsImageRepository.findByGoodsId(newGoods.getGoodsId());
        if (CollectionUtils.isNotEmpty(oldImages)) {
            for (GoodsImage oldImage : oldImages) {
                if (CollectionUtils.isNotEmpty(goodsImages)) {
                    Optional<GoodsImage> imageOpt = goodsImages.stream().filter(goodsImage -> oldImage.getImageId().equals(goodsImage.getImageId())).findFirst();
                    //如果图片存在，更新
                    if (imageOpt.isPresent()) {
                        KsBeanUtil.copyProperties(imageOpt.get(), oldImage);
                    } else {
                        oldImage.setDelFlag(DeleteFlag.YES);
                    }
                } else {
                    oldImage.setDelFlag(DeleteFlag.YES);
                }
                oldImage.setUpdateTime(currDate);
                goodsImageRepository.saveAll(oldImages);
            }
        }

        //新增图片
        if (CollectionUtils.isNotEmpty(goodsImages)) {
            goodsImages.stream().filter(goodsImage -> goodsImage.getImageId() == null).forEach(goodsImage -> {
                goodsImage.setCreateTime(currDate);
                goodsImage.setUpdateTime(currDate);
                goodsImage.setGoodsId(newGoods.getGoodsId());
                goodsImage.setDelFlag(DeleteFlag.NO);
                goodsImageRepository.save(goodsImage);
            });
        }
        //保存商品属性
        List<GoodsPropDetailRel> goodsPropDetailRels = saveRequest.getGoodsPropDetailRels();
        if (CollectionUtils.isNotEmpty(goodsPropDetailRels)) {
            //修改设置修改时间
            goodsPropDetailRels.forEach(goodsPropDetailRel -> {
                goodsPropDetailRel.setDelFlag(DeleteFlag.NO);
                if (goodsPropDetailRel.getRelId() != null) {
                    goodsPropDetailRel.setUpdateTime(LocalDateTime.now());
                }
            });
            //  先获取商品下所有的属性id，与前端传来的对比，id存在的做更新操作反之做保存操作
            List<GoodsPropDetailRel> oldPropList = goodsPropDetailRelRepository.queryByGoodsId(newGoods.getGoodsId());
            List<GoodsPropDetailRel> insertList = new ArrayList<>();
            if (oldPropList.isEmpty()) {
                goodsPropDetailRelRepository.saveAll(goodsPropDetailRels);
            } else {
                oldPropList.forEach(value -> {
                    goodsPropDetailRels.forEach(goodsProp -> {
                        if (value.getPropId().equals(goodsProp.getPropId())) {
                            goodsPropDetailRelRepository.updateByGoodsIdAndPropId(goodsProp.getDetailId(), goodsProp.getGoodsId(), goodsProp.getPropId());
                        } else {
                            goodsProp.setCreateTime(LocalDateTime.now());
                            insertList.add(goodsProp);
                        }
                    });
                });
                goodsPropDetailRelRepository.saveAll(insertList);
            }
        }
        //店铺分类
        if (osUtil.isS2b() && CollectionUtils.isNotEmpty(newGoods.getStoreCateIds())) {
            storeCateGoodsRelaRepository.deleteByGoodsId(newGoods.getGoodsId());
            newGoods.getStoreCateIds().forEach(cateId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setGoodsId(newGoods.getGoodsId());
                rela.setStoreCateId(cateId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }

        List<GoodsSpec> specs = saveRequest.getGoodsSpecs();
        List<GoodsSpecDetail> specDetails = saveRequest.getGoodsSpecDetails();
        List<GoodsTabRela> goodsTabRelas = saveRequest.getGoodsTabRelas();
        if (CollectionUtils.isNotEmpty(goodsTabRelas)) {
            goodsTabRelas.forEach(info -> {
                goodsTabRelaRepository.save(info);
            });
        }

        //如果是多规格
        if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {

            /**
             * 填放可用SKU相应的规格\规格值
             * （主要解决SKU可以前端删除，但相应规格值或规格仍然出现的情况）
             */
            Map<Long, Integer> isSpecEnable = new HashMap<>();
            Map<Long, Integer> isSpecDetailEnable = new HashMap<>();
            saveRequest.getGoodsInfos().forEach(goodsInfo -> {
                goodsInfo.getMockSpecIds().forEach(specId -> {
                    isSpecEnable.put(specId, Constants.yes);
                });
                goodsInfo.getMockSpecDetailIds().forEach(detailId -> {
                    isSpecDetailEnable.put(detailId, Constants.yes);
                });
            });

            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //更新规格
                List<GoodsSpec> goodsSpecs = goodsSpecRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(goodsSpecs)) {
                    for (GoodsSpec oldSpec : goodsSpecs) {
                        if (CollectionUtils.isNotEmpty(specs)) {
                            Optional<GoodsSpec> specOpt = specs.stream().filter(spec -> oldSpec.getSpecId().equals(spec.getSpecId())).findFirst();
                            //如果规格存在且SKU有这个规格，更新
                            if (specOpt.isPresent() && Constants.yes.equals(isSpecEnable.get(specOpt.get().getMockSpecId()))) {
                                KsBeanUtil.copyProperties(specOpt.get(), oldSpec);
                            } else {
                                oldSpec.setDelFlag(DeleteFlag.YES);
                            }
                        } else {
                            oldSpec.setDelFlag(DeleteFlag.YES);
                        }
                        oldSpec.setUpdateTime(currDate);
                        goodsSpecRepository.save(oldSpec);
                    }
                }

                //更新规格值
                List<GoodsSpecDetail> goodsSpecDetails = goodsSpecDetailRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(goodsSpecDetails)) {

                    for (GoodsSpecDetail oldSpecDetail : goodsSpecDetails) {
                        if (CollectionUtils.isNotEmpty(specDetails)) {
                            Optional<GoodsSpecDetail> specDetailOpt = specDetails.stream().filter(specDetail -> oldSpecDetail.getSpecDetailId().equals(specDetail.getSpecDetailId())).findFirst();
                            //如果规格值存在且SKU有这个规格值，更新
                            if (specDetailOpt.isPresent() && Constants.yes.equals(isSpecDetailEnable.get(specDetailOpt.get().getMockSpecDetailId()))) {
                                KsBeanUtil.copyProperties(specDetailOpt.get(), oldSpecDetail);

                                //更新SKU规格值表的名称备注
                                goodsInfoSpecDetailRelRepository.updateNameBySpecDetail(specDetailOpt.get().getDetailName(), oldSpecDetail.getSpecDetailId(), oldGoods.getGoodsId());
                            } else {
                                oldSpecDetail.setDelFlag(DeleteFlag.YES);
                            }
                        } else {
                            oldSpecDetail.setDelFlag(DeleteFlag.YES);
                        }
                        oldSpecDetail.setUpdateTime(currDate);
                        goodsSpecDetailRepository.save(oldSpecDetail);
                    }
                }
            }

            //新增规格
            if (CollectionUtils.isNotEmpty(specs)) {
                specs.stream().filter(goodsSpec -> goodsSpec.getSpecId() == null && Constants.yes.equals(isSpecEnable.get(goodsSpec.getMockSpecId()))).forEach(goodsSpec -> {
                    goodsSpec.setCreateTime(currDate);
                    goodsSpec.setUpdateTime(currDate);
                    goodsSpec.setGoodsId(newGoods.getGoodsId());
                    goodsSpec.setDelFlag(DeleteFlag.NO);
                    goodsSpec.setSpecId(goodsSpecRepository.save(goodsSpec).getSpecId());
                });
            }
            //新增规格值
            if (CollectionUtils.isNotEmpty(specDetails)) {
                specDetails.stream().filter(goodsSpecDetail -> goodsSpecDetail.getSpecDetailId() == null && Constants.yes.equals(isSpecDetailEnable.get(goodsSpecDetail.getMockSpecDetailId()))).forEach(goodsSpecDetail -> {
                    Optional<GoodsSpec> specOpt = specs.stream().filter(goodsSpec -> goodsSpec.getMockSpecId().equals(goodsSpecDetail.getMockSpecId())).findFirst();
                    if (specOpt.isPresent()) {
                        goodsSpecDetail.setCreateTime(currDate);
                        goodsSpecDetail.setUpdateTime(currDate);
                        goodsSpecDetail.setGoodsId(newGoods.getGoodsId());
                        goodsSpecDetail.setDelFlag(DeleteFlag.NO);
                        goodsSpecDetail.setSpecId(specOpt.get().getSpecId());
                        goodsSpecDetail.setSpecDetailId(goodsSpecDetailRepository.save(goodsSpecDetail).getSpecDetailId());
                    }
                });
            }
        } else {//修改为单规格
            //如果老数据为多规格
            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //删除规格
                goodsSpecRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除规格值
                goodsSpecDetailRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除商品规格值
                goodsInfoSpecDetailRelRepository.deleteByGoodsId(newGoods.getGoodsId());
            }
        }

        //只存储新增的SKU数据，用于当修改价格及订货量设置为否时，只为新SKU增加相关的价格数据
        List<BulkGoodsInfo> newGoodsInfo = new ArrayList<>();//需要被添加的sku信息

        //更新原有的SKU列表
        List<BulkGoodsInfo> goodsInfos = saveRequest.getGoodsInfos();
        List<BulkGoodsInfo> oldGoodsInfos = new ArrayList<>();//需要被更新的sku信息
        List<String> delInfoIds = new ArrayList<>();//需要被删除的sku信息
        if (CollectionUtils.isNotEmpty(goodsInfos)) {
            infoQueryRequest = new GoodsInfoQueryRequest();
            infoQueryRequest.setGoodsId(newGoods.getGoodsId());
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            List<BulkGoodsInfo> oldInfos = goodsInfoRepository.findAll(infoQueryRequest.getBulkWhereCriteria());

            if (CollectionUtils.isNotEmpty(oldInfos)) {
                for (BulkGoodsInfo oldInfo : oldInfos) {
                    if (Objects.isNull(oldInfo.getStock())) {
                        oldInfo.setStock(0L);
                    }

                    Optional<BulkGoodsInfo> infoOpt = goodsInfos.stream().filter(goodsInfo -> oldInfo.getGoodsInfoId().equals(goodsInfo.getGoodsInfoId())).findFirst();
                    //如果SKU存在，更新
                    if (infoOpt.isPresent()) {
                        infoOpt.get().setAddedFlag(oldInfo.getAddedFlag());

                        //如果上下架不是部分上下架，以SPU为准
                        if (!newGoods.getAddedFlag().equals(AddedFlag.PART.toValue())) {
                            infoOpt.get().setAddedFlag(newGoods.getAddedFlag());
                        }

                        //更新上下架时间
                        if (isChgAddedTime) {
                            infoOpt.get().setAddedTime(newGoods.getAddedTime());
                        }

                        if (Objects.isNull(infoOpt.get().getStock())) {
                            infoOpt.get().setStock(0L);
                        }

                        //如果发生设价类型变化，原SKU的独立设价设为FALSE
                        if (isChgPriceType) {
                            oldInfo.setAloneFlag(Boolean.FALSE);
                        }

                        //非独立设价SKU的叠加等级价、自定义客户都要以SPU为准
                        if (Objects.isNull(oldInfo.getAloneFlag())
                                || Boolean.FALSE.equals(oldInfo.getAloneFlag())) {
                            infoOpt.get().setCustomFlag(newGoods.getCustomFlag());
                            infoOpt.get().setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
                        }
                        //市场价刷新非独立设价的SKU
                        if (Objects.nonNull(newGoods.getMarketPrice()) && Boolean.FALSE.equals(oldInfo
                                .getAloneFlag())) {
                            //infoOpt.get().setMarketPrice(newGoods.getMarketPrice());
                        }

                        //不允许独立设价 sku的叠加客户等级折扣  与spu同步
                        if (newGoods.getPriceType() == 1 && newGoods.getAllowPriceSet() == 0) {
                            infoOpt.get().setLevelDiscountFlag(newGoods.getLevelDiscountFlag());
                        }
                        infoOpt.get().setCostPrice(newGoods.getCostPrice());
                        infoOpt.get().setAuditStatus(oldGoods.getAuditStatus());
                        infoOpt.get().setBrandId(oldGoods.getBrandId());
                        infoOpt.get().setCateId(oldGoods.getCateId());
                        infoOpt.get().setSaleType(oldGoods.getSaleType());
                        if (infoOpt.get().getGoodsInfoQrcode() == null) {
                            oldInfo.setGoodsInfoQrcode(null);
                        }
                        if (Objects.isNull(infoOpt.get().getErpPrice())){
                            infoOpt.get().setErpPrice(oldInfo.getErpPrice());
                        }
                        KsBeanUtil.copyProperties(infoOpt.get(), oldInfo);
                        //修改预估佣金
                        if (Objects.nonNull(oldInfo.getMarketPrice()) && Objects.nonNull(oldInfo.getCommissionRate())) {
                            oldInfo.setDistributionCommission(oldInfo.getMarketPrice().multiply(oldInfo
                                    .getCommissionRate()));
                        }
                        oldGoodsInfos.add(oldInfo);//修改前后都存在的数据--加入需要被更新的sku中
                    } else {
                        oldInfo.setDelFlag(DeleteFlag.YES);
                        delInfoIds.add(oldInfo.getGoodsInfoId());//修改后不存在的数据--加入需要被删除的sku中
                    }
                    oldInfo.setGoodsInfoName(newGoods.getGoodsName());
                    oldInfo.setUpdateTime(currDate);
                    goodsInfoRepository.save(oldInfo);
                }
                //更新最新库存信息
                List<BulkGoodsInfo> goodsInfoList = goodsInfos.stream().
                        filter(goodsInfo -> StringUtils.isNotEmpty(goodsInfo.getGoodsInfoId())).collect(Collectors.toList());
                goodsInfoList.forEach(goodsInfo -> {
                    List<GoodsWareStock> stockList = goodsInfo.getGoodsWareStocks();
                    if (CollectionUtils.isNotEmpty(stockList)) {
                        stockList.forEach(stock -> {
                            goodsWareStockService.updateByGoodsInfoId(goodsInfo.getGoodsInfoId(), stock.getStock(), stock.getWareId());
                        });
                    }
                });

                //删除SKU相关的规格关联表
                if (!delInfoIds.isEmpty()) {
                    goodsInfoSpecDetailRelRepository.deleteByGoodsInfoIds(delInfoIds, newGoods.getGoodsId());
                    //批量删除积分商城中该sku，避免前台出现脏数据
                    pointsGoodsRepository.deleteByGoodInfoIdList(delInfoIds);
                }
            }

            //只保存新SKU
            for (BulkGoodsInfo sku : goodsInfos) {
                sku.setCateId(newGoods.getCateId());
                sku.setBrandId(newGoods.getBrandId());
                sku.setGoodsId(newGoods.getGoodsId());
                sku.setGoodsInfoName(newGoods.getGoodsName());
                sku.setCreateTime(currDate);
                sku.setUpdateTime(currDate);
                sku.setDelFlag(DeleteFlag.NO);
                sku.setCompanyInfoId(oldGoods.getCompanyInfoId());
                sku.setPriceType(oldGoods.getPriceType());
                sku.setStoreId(oldGoods.getStoreId());
                sku.setAuditStatus(oldGoods.getAuditStatus());
                sku.setCompanyType(oldGoods.getCompanyType());
                //只处理新增的SKU
                if (sku.getGoodsInfoId() != null) {
                    continue;
                }
                if (sku.getStock() == null) {
                    sku.setStock(0L);
                }
                sku.setCustomFlag(oldGoods.getCustomFlag());
                sku.setLevelDiscountFlag(oldGoods.getLevelDiscountFlag());

                //新商品会采用SPU市场价
                if ((2 != newGoods.getSaleType()) && newGoods.getMarketPrice() != null) { // 2代表新散批
                    sku.setMarketPrice(newGoods.getMarketPrice());
                }
                sku.setCostPrice(oldGoods.getCostPrice());

                //如果SPU选择部分上架，新增SKU的上下架状态为上架
                sku.setAddedFlag(oldGoods.getAddedFlag().equals(AddedFlag.PART.toValue()) ? AddedFlag.YES.toValue() : newGoods.getAddedFlag());
                sku.setAddedTime(oldGoods.getAddedTime());
                sku.setAloneFlag(Boolean.FALSE);
                sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                sku.setSaleType(newGoods.getSaleType());
                sku.setGoodsSource(oldGoods.getGoodsSource());
                String goodsInfoId = goodsInfoRepository.save(sku).getGoodsInfoId();
                sku.setGoodsInfoId(goodsInfoId);

                //如果是多规格,新增SKU与规格明细值的关联表
                if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {
                    if (CollectionUtils.isNotEmpty(specs)) {
                        for (GoodsSpec spec : specs) {
                            if (sku.getMockSpecIds().contains(spec.getMockSpecId())) {
                                for (GoodsSpecDetail detail : specDetails) {
                                    if (spec.getMockSpecId().equals(detail.getMockSpecId()) && sku.getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
                                        GoodsInfoSpecDetailRel detailRel = new GoodsInfoSpecDetailRel();
                                        detailRel.setGoodsId(newGoods.getGoodsId());
                                        detailRel.setGoodsInfoId(goodsInfoId);
                                        detailRel.setSpecId(spec.getSpecId());
                                        detailRel.setSpecDetailId(detail.getSpecDetailId());
                                        detailRel.setDetailName(detail.getDetailName());
                                        detailRel.setCreateTime(currDate);
                                        detailRel.setUpdateTime(currDate);
                                        detailRel.setDelFlag(DeleteFlag.NO);
                                        goodsInfoSpecDetailRelRepository.save(detailRel);
                                    }
                                }
                            }
                        }
                    }
                }
                newGoodsInfo.add(sku);//修改后才存在(新出现)的数据--加入需要被添加的sku中
            }
        }

        //为新增加的SKU补充设价数据
        if (CollectionUtils.isNotEmpty(newGoodsInfo)) {
            if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(oldGoods.getPriceType())) {
                saveRequest.setGoodsIntervalPrices(goodsIntervalPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
            } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(oldGoods.getPriceType())) {
                saveRequest.setGoodsLevelPrices(goodsLevelPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
                //按客户单独定价
                if (Constants.yes.equals(oldGoods.getCustomFlag())) {
                    saveRequest.setGoodsCustomerPrices(goodsCustomerPriceRepository.findByGoodsId(oldGoods.getGoodsId()));
                }
            }
            this.saveGoodsPrice(newGoodsInfo, oldGoods, saveRequest);
        }

        //如果spu是部分上架,当相关Sku都是上架时，自动设为上架
        if (AddedFlag.PART.toValue() == oldGoods.getAddedFlag()) {
            long addedCount = goodsInfos.stream().filter(g -> DeleteFlag.NO.equals(g.getDelFlag()) && AddedFlag.YES.toValue() == g.getAddedFlag()).count();
            long count = goodsInfos.stream().filter(g -> DeleteFlag.NO.equals(g.getDelFlag())).count();
            if (addedCount == count) {
                oldGoods.setAddedFlag(AddedFlag.YES.toValue());
                goodsRepository.save(oldGoods);
            }
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("newGoodsInfo", newGoodsInfo);
        returnMap.put("delInfoIds", delInfoIds);
        returnMap.put("oldGoodsInfos", oldGoodsInfos);
        return returnMap;
    }

    /**
     * 储存商品相关设价信息
     *
     * @param goodsInfos  sku集
     * @param goods       同一个spu信息
     * @param saveRequest 请求封装参数
     */
    private void saveGoodsPrice(List<BulkGoodsInfo> goodsInfos, BulkGoods goods, BulkGoodsSaveRequest saveRequest) {
        List<String> skuIds = new ArrayList<>();
        //提取非独立设价的Sku编号,进行清理设价数据
        if (goods.getPriceType() == 1 && goods.getAllowPriceSet() == 0) {
            skuIds = goodsInfos.stream()
                    .map(BulkGoodsInfo::getGoodsInfoId)
                    .collect(Collectors.toList());
        } else {
            skuIds = goodsInfos.stream()
                    .filter(sku -> Objects.isNull(sku.getAloneFlag()) || !sku.getAloneFlag())
                    .map(BulkGoodsInfo::getGoodsInfoId)
                    .collect(Collectors.toList());
        }

        if (skuIds.size() > 0) {
            goodsIntervalPriceRepository.deleteByGoodsInfoIds(skuIds);
            goodsLevelPriceRepository.deleteByGoodsInfoIds(skuIds);
            goodsCustomerPriceRepository.deleteByGoodsInfoIds(skuIds);
        }

        for (BulkGoodsInfo sku : goodsInfos) {
            //如果SKU是保持独立，则不更新
            if (!(goods.getPriceType() == 1 && goods.getAllowPriceSet() == 0) && Objects.nonNull(sku.getAloneFlag())
                    && sku.getAloneFlag()) {
                continue;
            }

            //按订货量设价，保存订货区间
            if (Integer.valueOf(GoodsPriceType.STOCK.toValue()).equals(goods.getPriceType())) {
                if (CollectionUtils.isNotEmpty(saveRequest.getGoodsIntervalPrices())) {
                    List<GoodsIntervalPrice> newGoodsInterValPrice = new ArrayList<>();
                    saveRequest.getGoodsIntervalPrices().forEach(intervalPrice -> {
                        GoodsIntervalPrice newIntervalPrice = new GoodsIntervalPrice();
                        newIntervalPrice.setGoodsId(sku.getGoodsId());
                        newIntervalPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newIntervalPrice.setType(PriceType.SKU);
                        newIntervalPrice.setCount(intervalPrice.getCount());
                        newIntervalPrice.setPrice(intervalPrice.getPrice());
                        newGoodsInterValPrice.add(newIntervalPrice);
                    });
                    goodsIntervalPriceRepository.saveAll(newGoodsInterValPrice);
                }
            } else if (Integer.valueOf(GoodsPriceType.CUSTOMER.toValue()).equals(goods.getPriceType())) {
                //按客户等级
                if (CollectionUtils.isNotEmpty(saveRequest.getGoodsLevelPrices())) {
                    List<GoodsLevelPrice> newLevelPrices = new ArrayList<>();
                    saveRequest.getGoodsLevelPrices().forEach(goodsLevelPrice -> {
                        GoodsLevelPrice newLevelPrice = new GoodsLevelPrice();
                        newLevelPrice.setLevelId(goodsLevelPrice.getLevelId());
                        newLevelPrice.setGoodsId(sku.getGoodsId());
                        newLevelPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newLevelPrice.setPrice(goodsLevelPrice.getPrice());
                        newLevelPrice.setCount(goodsLevelPrice.getCount());
                        newLevelPrice.setMaxCount(goodsLevelPrice.getMaxCount());
                        newLevelPrice.setType(PriceType.SKU);
                        newLevelPrices.add(newLevelPrice);
                    });
                    goodsLevelPriceRepository.saveAll(newLevelPrices);
                }

                //按客户单独定价
                if (Constants.yes.equals(goods.getCustomFlag()) && CollectionUtils.isNotEmpty(saveRequest.getGoodsCustomerPrices())) {
                    List<GoodsCustomerPrice> newCustomerPrices = new ArrayList<>();
                    saveRequest.getGoodsCustomerPrices().forEach(price -> {
                        GoodsCustomerPrice newCustomerPrice = new GoodsCustomerPrice();
                        newCustomerPrice.setGoodsInfoId(sku.getGoodsInfoId());
                        newCustomerPrice.setGoodsId(sku.getGoodsId());
                        newCustomerPrice.setCustomerId(price.getCustomerId());
                        newCustomerPrice.setMaxCount(price.getMaxCount());
                        newCustomerPrice.setCount(price.getCount());
                        newCustomerPrice.setType(PriceType.SKU);
                        newCustomerPrice.setPrice(price.getPrice());
                        newCustomerPrices.add(newCustomerPrice);
                    });
                    goodsCustomerPriceRepository.saveAll(newCustomerPrices);
                }
            }
        }
    }

    public List<BulkGoods> findByProviderGoodsId(String providerGoodsId) {
        return goodsRepository.findAllByProviderGoodsId(providerGoodsId);
    }


    /**
     * 同步商家商品和商品库sku 里的supplyPrice
     * @param goodsSaveRequest
     * @param providerGoods
     */
    @Transactional
    public void synStoreGoodsInfoAndStandardSkuForSupplyPrice(BulkGoodsSaveRequest goodsSaveRequest, List<BulkGoods> providerGoods) {
        //同步商家商品的供货价
        providerGoods.forEach(s->{s.setSupplyPrice(goodsSaveRequest.getGoods().getSupplyPrice());});
        goodsRepository.saveAll(providerGoods);

        List<String> goodIds = providerGoods.stream().map(BulkGoods::getGoodsId).collect(Collectors.toList());

        //供应商商品goodsInfoId->supplyPrice
        HashMap<String, BigDecimal> providerMapSupplyPrice = new HashMap<>();
        //供应商商品goodsInfoId->stock
        HashMap<String, Long> providerMapStock = new HashMap<>();
        List<BulkGoodsInfo> providerGoodsInfos = goodsSaveRequest.getGoodsInfos();
        providerGoodsInfos.forEach(goodsInfo->{
            providerMapSupplyPrice.put(goodsInfo.getGoodsInfoId(), goodsInfo.getSupplyPrice());
            providerMapStock.put(goodsInfo.getGoodsInfoId(),goodsInfo.getStock());
        });

        //商家商品goodsInfoId->供应商商品goodsInfoId
        HashMap<String, String> storeMapSupplyPrice = new HashMap<>();
        List<BulkGoodsInfo> storeGoodsInfos = goodsInfoRepository.findByGoodsIdIn(goodIds);
        storeGoodsInfos.forEach(goodsInfo->{storeMapSupplyPrice.put(goodsInfo.getGoodsInfoId(), goodsInfo.getProviderGoodsInfoId());});

        //商品库skuId->供应商goodsInfoId
        HashMap<String, String> standardMapSupplyPrice = new HashMap<>();
        List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByGoodsIds(goodIds);
        List<String> standardGoodsIds = standardGoodsRels.stream().map(StandardGoodsRel::getStandardId).collect(Collectors.toList());
        List<StandardSku> standardGoodsInfos = standardSkuRepository.findByGoodsIdIn(standardGoodsIds);
        standardGoodsInfos.forEach(standardSku->{standardMapSupplyPrice.put(standardSku.getGoodsId(), standardSku.getProviderGoodsInfoId());});

        //更新商家商品supplyPrice 和stock
        storeGoodsInfos.forEach(goodsInfo->{
            goodsInfo.setSupplyPrice(providerMapSupplyPrice.get(storeMapSupplyPrice.get(goodsInfo.getGoodsInfoId())));
            goodsInfo.setStock(providerMapStock.get(storeMapSupplyPrice.get(goodsInfo.getGoodsInfoId())));
        });
        goodsInfoRepository.saveAll(storeGoodsInfos);

        //更新商品库suppliPrice 和sotck
        standardGoodsInfos.forEach(standardSku->{
            standardSku.setSupplyPrice(providerMapSupplyPrice.get(standardMapSupplyPrice.get(standardSku.getGoodsInfoId())));
            standardSku.setStock(providerMapStock.get(standardMapSupplyPrice.get(standardSku.getGoodsInfoId())));
        });
        standardSkuRepository.saveAll(standardGoodsInfos);
    }



    @Transactional
    public List<String> synDeleteStoreGoodsInfoAndStandardSku(List<String> delInfoIds) {
        if(delInfoIds != null){
            List<BulkGoodsInfo> byProviderGoodsInfoIdIn = goodsInfoRepository.findByProviderGoodsInfoIdIn(delInfoIds);
            goodsInfoRepository.deleteByProviderGoodsInfoId(delInfoIds);
            standardSkuRepository.deleteByGoodsIds(delInfoIds);
            return byProviderGoodsInfoIdIn.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }



    /**
     * 检测商品公共基础类
     * 如分类、品牌、店铺分类
     *
     * @param goods 商品信息
     */
    public void checkBasic(BulkGoods goods) {
        GoodsCate cate = this.goodsCateRepository.findById(goods.getCateId()).orElse(null);
        if (Objects.isNull(cate) || Objects.equals(DeleteFlag.YES, cate.getDelFlag())) {
            throw new SbcRuntimeException(GoodsCateErrorCode.NOT_EXIST);
        }

        if (goods.getBrandId() != null) {
            GoodsBrand brand = this.goodsBrandRepository.findById(goods.getBrandId()).orElse(null);
            if (Objects.isNull(brand) || Objects.equals(DeleteFlag.YES, brand.getDelFlag())) {
                throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
            }
        }

        if (osUtil.isS2b()) {
            //验证是否签约分类
            ContractCateQueryRequest cateQueryRequest = new ContractCateQueryRequest();
            cateQueryRequest.setStoreId(goods.getStoreId());
            cateQueryRequest.setCateId(goods.getCateId());
            if (contractCateRepository.count(cateQueryRequest.getWhereCriteria()) < 1) {
                throw new SbcRuntimeException(SigningClassErrorCode.CONTRACT_CATE_NOT_EXIST);
            }

            //验证是否签约品牌
            if (goods.getBrandId() != null) {
                ContractBrandQueryRequest brandQueryRequest = new ContractBrandQueryRequest();
                brandQueryRequest.setStoreId(goods.getStoreId());
                brandQueryRequest.setGoodsBrandIds(Collections.singletonList(goods.getBrandId()));
                if (contractBrandRepository.count(brandQueryRequest.getWhereCriteria()) < 1) {
                    throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
                }
            }

            //验证店铺分类存在性
            StoreCateQueryRequest request = new StoreCateQueryRequest();
            request.setStoreId(goods.getStoreId());
            request.setStoreCateIds(goods.getStoreCateIds());
            request.setDelFlag(DeleteFlag.NO);
            if (goods.getStoreCateIds().size() != storeCateRepository.count(request.getWhereCriteria())) {
                throw new SbcRuntimeException(StoreCateErrorCode.NOT_EXIST);
            }
        }
    }


    private Boolean isErpGoods(String goodsName) {
        String lastChar = subStringLastChar(goodsName);
        if ("t".equals(lastChar) || "T".equals(lastChar)) {
            return true;
        }else {
            return false;
        }

    }
    private String subStringLastChar(String str) {
        return str.substring(str.length() - 1, str.length());
    }

    /**
     * 根据商品id批量查询Goods
     * @param goodsIds
     * @return
     */

    public List<BulkGoods> listByGoodsIdsNoValid(List<String> goodsIds) {
        GoodsQueryRequest goodsQueryRequest = new GoodsQueryRequest();
        goodsQueryRequest.setGoodsIds(goodsIds);

        return goodsRepository.findAll(goodsQueryRequest.getBulkWhereCriteria());
    }

    /**
     * 根据多个SpuID查询属性关联
     *
     * @param goodsIds
     * @return
     */
    public List<GoodsPropDetailRel> findRefByGoodIds(List<String> goodsIds) {
        List<Object> objectList = goodsPropDetailRelRepository.findRefByGoodIds(goodsIds);
        if (objectList != null && objectList.size() > 0) {
            List<GoodsPropDetailRel> rels = new ArrayList<>();
            objectList.stream().forEach(obj -> {
                GoodsPropDetailRel rel = new GoodsPropDetailRel();
                Object[] object = (Object[]) obj;
                Long propId = ((BigInteger) object[0]).longValue();
                Long detailId = ((BigInteger) object[1]).longValue();
                String goodsId = String.valueOf(object[2]);
                rel.setPropId(propId);
                rel.setDetailId(detailId);
                rel.setGoodsId(goodsId);
                rels.add(rel);
            });
            return rels;
        }
        return Collections.emptyList();
    }

    /**
     * 按条件查询数量
     * @return
     */
    public long countByCondition(GoodsQueryRequest request){
        return goodsRepository.count(request.getBulkWhereCriteria());
    }

    /**
     * @Author lvzhenwei
     * @Description 更新商品收藏量
     * @Date 15:54 2019/4/11
     * @Param [goodsModifyCollectNumRequest]
     * @return void
     **/
    @Transactional
    public void updateGoodsCollectNum(GoodsModifyCollectNumRequest goodsModifyCollectNumRequest){
        goodsRepository.updateGoodsCollectNum(goodsModifyCollectNumRequest.getGoodsCollectNum(),goodsModifyCollectNumRequest.getGoodsId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchModifyBulkCate(GoodsBatchModifyCateRequest request) {
        goodsRepository.batchModifyCate(request.getCateId(), request.getGoodsIds());
        goodsInfoRepository.batchModifyCate(request.getCateId(), request.getGoodsIds());
    }
}
