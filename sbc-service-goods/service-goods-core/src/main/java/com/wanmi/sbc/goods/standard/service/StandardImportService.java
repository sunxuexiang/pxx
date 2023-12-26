package com.wanmi.sbc.goods.standard.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StorePageRequest;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsImportErrorCode;
import com.wanmi.sbc.goods.api.constant.StandardGoodsErrorCode;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.api.response.standard.StandardImportGoodsResponse;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.common.GoodsCommonService;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.repository.FreightTemplateGoodsRepository;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.root.*;
import com.wanmi.sbc.goods.info.repository.*;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsRequest;
import com.wanmi.sbc.goods.info.service.GoodsService;
import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import com.wanmi.sbc.goods.mapping.repository.GoodsInfoMappingRepository;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.model.root.StandardPropDetailRel;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.standard.repository.StandardPropDetailRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import com.wanmi.sbc.goods.standard.request.StandardImportRequest;
import com.wanmi.sbc.goods.standard.request.StandardQueryRequest;
import com.wanmi.sbc.goods.standard.request.StandardSkuQueryRequest;
import com.wanmi.sbc.goods.standardimages.model.root.StandardImage;
import com.wanmi.sbc.goods.standardimages.repository.StandardImageRepository;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSkuSpecDetailRel;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSpec;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSpecDetail;
import com.wanmi.sbc.goods.standardspec.repository.StandardSkuSpecDetailRelRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecDetailRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecRepository;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.goods.storecate.repository.StoreCateRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品库服务
 * Created by daiyitian on 2017/4/11.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class StandardImportService {

    @Autowired
    private StandardGoodsRepository standardGoodsRepository;

    @Autowired
    private StandardSkuRepository standardSkuRepository;

    @Autowired
    private StandardSpecRepository standardSpecRepository;

    @Autowired
    private StandardSpecDetailRepository standardSpecDetailRepository;

    @Autowired
    private StandardSkuSpecDetailRelRepository standardSkuSpecDetailRelRepository;

    @Autowired
    private StandardImageRepository standardImageRepository;

    @Autowired
    private StandardPropDetailRelRepository standardPropDetailRelRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private RetailGoodsRepository retailGoodsRepository;

    @Autowired
    private BulkGoodsRepository bulkGoodsRepository;

    @Autowired
    private BulkGoodsInfoRepository bulkGoodsInfoRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Autowired
    private RetailGoodsInfoRepository retailGoodsInfoRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private GoodsPropDetailRelRepository goodsPropDetailRelRepository;

    @Autowired
    private StoreCateRepository storeCateRepository;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;

    @Autowired
    private FreightTemplateGoodsRepository freightTemplateGoodsRepository;

    @Autowired
    private GoodsCommonService goodsCommonService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsInfoMappingRepository goodsInfoMappingRepository;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    /**
     * 商品库批量导入商品
     * @param request 参数
     * @return
     */
    @Transactional
    public StandardImportGoodsResponse importGoods(StandardImportRequest request){
        StandardImportGoodsResponse response = new StandardImportGoodsResponse();
        Boolean delFlag = standardGoodsRepository.findAllById(request.getGoodsIds()).stream().anyMatch(s -> DeleteFlag.YES == s.getDelFlag());
        if (delFlag){
            throw new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST);
        }
        if(standardGoodsRelRepository.countByStandardAndStoreIds(request.getGoodsIds(), Collections.singletonList(request.getStoreId())) > 0){
            throw new SbcRuntimeException(StandardGoodsErrorCode.COMPANY_IMPORT);
        }

        //店铺分类未配置
        List<StoreCate> storeCateList = storeCateRepository.findAll(
                StoreCateQueryRequest.builder()
                        .storeId(request.getStoreId())
                        .delFlag(DeleteFlag.NO)
                        .isDefault(DefaultFlag.YES).build().getWhereCriteria());
        if(CollectionUtils.isEmpty(storeCateList)){
            throw new SbcRuntimeException(GoodsCateErrorCode.DEFAULT_CATE_NOT_EXIST);
        }

        Long defaultStoreCateId = storeCateList.get(0).getStoreCateId();
        List<FreightTemplateGoods> freightTemp =new ArrayList<>();
        if (request.getCompanyType().equals(CompanyType.BULK)){
            //运费模板未配置 散批运费模板需要批发的运费模板
            //获取批发店的店铺id
            Long storeId = storeQueryProvider.page(StorePageRequest.builder().companyType(CompanyType.PLATFORM)
                    .build()).getContext().getStoreVOPage().getContent().get(0).getStoreId();
            freightTemp = getFreightTemplateGoods(request);
        }else {
            WareHouseQueryRequest wareHouseQueryRequest = new WareHouseQueryRequest();
            wareHouseQueryRequest.setDelFlag(DeleteFlag.NO);
            wareHouseQueryRequest.setDefaultFlag(DefaultFlag.YES);
        //    List<WareHouse> wareHousList = wareHouseService.list(wareHouseQueryRequest);
            //运费模板未配置
            freightTemp = getFreightTemplateGoods(request);
        }
        List<StandardGoods> standardGoodses = standardGoodsRepository.findAll(StandardQueryRequest.builder().goodsIds(request.getGoodsIds()).build().getWhereCriteria());
        List<String> newSkuIds = new ArrayList<>();
        List<String> retailSkuIds = new ArrayList<>();
        List<String> bulkSkuIds = new ArrayList<>();
        List<StandardGoodsRel> goodsRels = standardGoodsRelRepository.findByStandardIds(request.getGoodsIds());
        Map<String, String> mappingOldSpu = new HashMap<>();
        Map<String, Long> mappingOldStoreId = new HashMap<>();
        if(CollectionUtils.isNotEmpty(goodsRels)){
            //规格映射map,商品库Id -> 老商品Id
            goodsRels.forEach(goodsRel->{
                mappingOldSpu.put(goodsRel.getStandardId(), goodsRel.getGoodsId());
                mappingOldStoreId.put(goodsRel.getStandardId(), goodsRel.getStoreId());
            });
        }

        if(CollectionUtils.isNotEmpty(standardGoodses)){
            //规格映射Map,商品库Id -> 新商品Id
            Map<String, String> mappingSpu = new HashMap<>();
            //规格映射Map,商品库规格Id -> 新商品规格Id
            Map<Long, Long> mappingSpec = new HashMap<>();
            //规格映射Map,商品库规格值Id -> 新商品规格值Id
            Map<Long, Long> mappingDetail = new HashMap<>();
            //规格映射Map,商品库SkuId -> 新商品SkuId
            Map<String, String> mappingSku = new HashMap<>();
            //规格映射Map,商品库Id -> 商品来源
            Map<String, Integer> mappingGoodsSourse = new HashMap<>();

            LocalDateTime now = LocalDateTime.now();
            Map<String, Goods> GoodsMap = new HashMap<>();
            //导入Spu
            List<FreightTemplateGoods> finalFreightTemp = freightTemp;
            standardGoodses.forEach(standardGoods -> {
                // log.info("打印数据==========="+JSONObject.toJSONString(standardGoods));
                Goods goods = new Goods();
                BeanUtils.copyProperties(standardGoods, goods);
                goods.setGoodsId(null);
                goods.setAddedFlag(AddedFlag.NO.toValue());
                goods.setDelFlag(DeleteFlag.NO);
                goods.setCreateTime(now);
                goods.setUpdateTime(now);
                goods.setAddedTime(now);
                goods.setSubmitTime(now);
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setCustomFlag(Constants.no);
                goods.setLevelDiscountFlag(Constants.no);
                goods.setCompanyInfoId(request.getCompanyInfoId());
                goods.setCompanyType(request.getCompanyType());
                goods.setStoreId(request.getStoreId());
                goods.setSupplierName(request.getSupplierName());
                goods.setGoodsNo(goodsCommonService.getSpuNoByUnique());
                goods.setStoreCateIds(Collections.singletonList(defaultStoreCateId));
                goods.setSaleType(SaleType.WHOLESALE.toValue());
                goods.setGoodsSubtitleNew(standardGoods.getGoodsSubtitleNew());

                if(1 == standardGoods.getGoodsSaleType()){
                    finalFreightTemp.forEach(var ->{
                        if(var.getWareId() == 1l){
                            goods.setFreightTempId(var.getFreightTempId());
                        }
                    });
                }else if (2 == standardGoods.getGoodsSaleType()){
                    finalFreightTemp.forEach(freightTempFalg -> {
                        if(Objects.nonNull(goods.getWareId()) && freightTempFalg.getWareId().equals(Constants.WHOLESALE_BULK.get(goods.getWareId()))){
                            goods.setFreightTempId(freightTempFalg.getFreightTempId());
                        }
                    });
                }

                else{
                    finalFreightTemp.forEach(freightTempFalg -> {
                        if(Objects.nonNull(goods.getWareId()) && freightTempFalg.getWareId().equals(goods.getWareId())){
                            goods.setFreightTempId(freightTempFalg.getFreightTempId());
                        }
                    });
                }

                goods.setPriceType(GoodsPriceType.MARKET.toValue());

                goods.setGoodsVideo(standardGoods.getGoodsVideo());
                //商品来源，0供应商，1商家 导入后就是商家商品 以此来区分列表展示
                goods.setGoodsSource(1);
                goods.setSupplyPrice(standardGoods.getSupplyPrice());
                goods.setRecommendedRetailPrice(standardGoods.getRecommendedRetailPrice());
                goods.setProviderGoodsId(mappingOldSpu.get(standardGoods.getGoodsId()));
                goods.setProviderId(mappingOldStoreId.get(standardGoods.getGoodsId()));
                goods.setProviderName(standardGoods.getProviderName());
                //允许独立设价
                goods.setAllowPriceSet(1);

                //初始化商品对应的数量（收藏、销量、评论数、好评数）
                if(goods.getGoodsCollectNum() == null){
                    goods.setGoodsCollectNum(0L);
                }
                if(goods.getGoodsSalesNum() == null){
                    goods.setGoodsSalesNum(0L);
                }
                if(goods.getGoodsEvaluateNum() == null){
                    goods.setGoodsEvaluateNum(0L);
                }
                if(goods.getGoodsFavorableCommentNum() == null){
                    goods.setGoodsFavorableCommentNum(0L);
                }
                String newGoodsId = null;
                if (Objects.nonNull(standardGoods.getGoodsSaleType()) && 1 == standardGoods.getGoodsSaleType()) {
                    goods.setSaleType(SaleType.RETAIL.toValue());
                    WareHouseVO wareHouse = wareHouseService.findBySelfErpIdAndDefaultFlagAndDelFlag();
                    //
                    goods.setWareId(1L);
                    newGoodsId = retailGoodsRepository.save(KsBeanUtil.convert(goods, RetailGoods.class)).getGoodsId();
                }
                else if (Objects.nonNull(standardGoods.getGoodsSaleType()) && 2 == standardGoods.getGoodsSaleType()){
                    goods.setSaleType(SaleType.BULK.toValue());
//                    WareHouseVO wareHouse = wareHouseService.findBulkBySelfErpIdAndDefaultFlagAndDelFlag();
                    // log.info("=========存入数据"+JSONObject.toJSONString(goods));
                    newGoodsId=bulkGoodsRepository.save(KsBeanUtil.convert(goods, BulkGoods.class)).getGoodsId();
                }
                else {
                    Goods save = goodsRepository.save(goods);
                    newGoodsId =save.getGoodsId();
                    GoodsMap.put(newGoodsId,save);
                }
                // 检查商品基本信息
                goodsService.checkBasic(goods);

                mappingSpu.put(standardGoods.getGoodsId(), newGoodsId);
                mappingGoodsSourse.put(standardGoods.getGoodsId(),standardGoods.getGoodsSource());

                //关联商品与商品库
                StandardGoodsRel rel = new StandardGoodsRel();
                rel.setGoodsId(newGoodsId);
                rel.setStandardId(standardGoods.getGoodsId());
                rel.setStoreId(request.getStoreId());
                standardGoodsRelRepository.save(rel);
            });
            // log.info("商品同步=============="+GoodsMap);
            List<StandardGoods> spuList = standardGoodses;
            List<GoodsWareStock> standardGoodsList = new ArrayList<>();
            //多个仓库的情况，仅仅需要同步仓为1的数据
            WareHouse  wareHouseVO = wareHouseService.list(WareHouseQueryRequest.builder().storeId(request.getStoreId()).build()).get(0);
            if(Objects.isNull(wareHouseVO)){
                throw new SbcRuntimeException(GoodsImportErrorCode.EMPTY_ERROR,"当前仓库默认仓库为空");
            }
            //导入Sku
            List<StandardSku> skus = standardSkuRepository.findAll(StandardSkuQueryRequest.builder().goodsIds(request.getGoodsIds()).delFlag(DeleteFlag.NO.toValue()).build().getWhereCriteria());
            if (CollectionUtils.isNotEmpty(skus)) {
                List<String> sku_0_id_list = new ArrayList<>(skus.size());
                List<String> sku_1_2_parent_id_list = new ArrayList<>(skus.size());
                for(StandardSku sku:skus){
                    if(SaleType.WHOLESALE.toValue()==sku.getGoodsSaleType()){
                        sku_0_id_list.add(sku.getGoodsInfoId());
                    }else{
                        if(StringUtils.isNotBlank(sku.getMainSkuGoodsInfoId())) {
                            sku_1_2_parent_id_list.add(sku.getMainSkuGoodsInfoId());
                        }
                    }
                }
                List<StandardSku> childSkuListByMainSku = new ArrayList<>(1);
                List<StandardSku> mainSkuListByChildSku = new ArrayList<>(1);
                if(sku_0_id_list.size()>0) {
                    childSkuListByMainSku = standardSkuRepository.findSkuListByMainSku(sku_0_id_list);
                }
                List<StandardSku> finalChildSkuListByMainSku = childSkuListByMainSku;
                if(sku_1_2_parent_id_list.size()>0) {
                    mainSkuListByChildSku = standardSkuRepository.findAllById(sku_1_2_parent_id_list);
                }
                List<StandardSku> finalMainSkuListByChildSku = mainSkuListByChildSku;
                skus.forEach(standardSku -> {
                    GoodsInfo sku = new GoodsInfo();
                    BeanUtils.copyProperties(standardSku, sku);
                    sku.setGoodsInfoId(null);
                    sku.setGoodsId(mappingSpu.get(standardSku.getGoodsId()));
                    sku.setProviderId(mappingOldStoreId.get(standardSku.getGoodsId()));
                    sku.setGoodsSource(1);
                    sku.setCreateTime(now);
                    sku.setUpdateTime(now);
                    sku.setAddedTime(now);
                    sku.setAddedFlag(AddedFlag.NO.toValue());
                    sku.setCompanyInfoId(request.getCompanyInfoId());
                    sku.setLevelDiscountFlag(Constants.no);
                    sku.setCustomFlag(Constants.no);
                    sku.setStoreId(request.getStoreId());
                    sku.setAuditStatus(CheckStatus.CHECKED);
                    sku.setCompanyType(request.getCompanyType());
                    sku.setGoodsInfoNo(goodsCommonService.getSkuNoByUnique());
                    sku.setStock(Objects.isNull(standardSku.getStock()) ? new BigDecimal(0) : BigDecimal.valueOf(standardSku.getStock()));
                    sku.setAloneFlag(Boolean.FALSE);
                    sku.setSupplyPrice(standardSku.getSupplyPrice());
                    sku.setGoodsInfoType(Integer.valueOf(0));
                    sku.setAddStep(standardSku.getAddStep());
                    sku.setSortNumKey(0);
                    sku.setSortNumCate(0);
                    sku.setShelflife(standardSku.getShelflife());
                    //修改成正的条形码
                    sku.setGoodsInfoBarcode(standardSku.getGoodsInfoBarcode());

                    Optional<StandardGoods> standardGoods = spuList.stream().filter(goodses -> goodses.getGoodsId().equals(standardSku.getGoodsId())).findFirst();
                    sku.setCateId(standardGoods.get().getCateId());
                    sku.setBrandId(standardGoods.get().getBrandId());
                    sku.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                    //默认销售类型 批发
                    sku.setSaleType(SaleType.WHOLESALE.toValue());
                    //设置默认(加入sku 初始值)
                    sku.setGoodsInfoCubage(standardGoods.get().getGoodsCubage());
                    sku.setGoodsInfoUnit(standardGoods.get().getGoodsUnit());
                    sku.setGoodsInfoWeight(standardGoods.get().getGoodsWeight());
                    String skuId = null;
                    //如果是大白鲸超市零售商品
                    if (Objects.nonNull(standardSku.getGoodsSaleType()) && 1 == standardSku.getGoodsSaleType()) {
                        sku.setSaleType(SaleType.RETAIL.toValue());
                        WareHouseVO wareHouse = wareHouseService.findBySelfErpIdAndDefaultFlagAndDelFlag();
                        sku.setWareId(wareHouse.getWareId());
                        skuId = retailGoodsInfoRepository.save(KsBeanUtil.convert(sku, RetailGoodsInfo.class)).getGoodsInfoId();
                        retailSkuIds.add(skuId);
                    }
                    else if (Objects.nonNull(standardSku.getGoodsSaleType()) && 2 == standardSku.getGoodsSaleType()){

                        sku.setSaleType(SaleType.BULK.toValue());
//                        WareHouseVO wareHouse = wareHouseService.findBulkBySelfErpIdAndDefaultFlagAndDelFlag();
//                        sku.setWareId(wareHouse.getWareId());
                        BulkGoodsInfo convert = KsBeanUtil.convert(sku, BulkGoodsInfo.class);
                        // log.info("=========存入数据"+JSONObject.toJSONString(convert));
                        convert.setErpPrice(convert.getMarketPrice());
                        skuId = bulkGoodsInfoRepository.save(convert).getGoodsInfoId();
                        bulkSkuIds.add(skuId);
                    }
                    else {
                        skuId = goodsInfoRepository.save(sku).getGoodsInfoId();
                        //拆箱商品更新
                        DevanningGoodsInfo convert = KsBeanUtil.convert(sku, DevanningGoodsInfo.class);
                        if (Objects.nonNull(GoodsMap.get(convert.getGoodsId()))){
                            convert.setDivisorFlag(BigDecimal.ONE);
                            convert.setGoodsInfoSubtitle(GoodsMap.get(convert.getGoodsId()).getGoodsSubtitle());
                            convert.setGoodsSubtitleNew(GoodsMap.get(convert.getGoodsId()).getGoodsSubtitleNew());
                            //特殊情况可能为空
                            if (null!=convert && StringUtils.isEmpty(convert.getGoodsInfoSubtitle())){
                                convert.setGoodsInfoSubtitle(standardGoods.get().getGoodsSubtitle());
                                convert.setGoodsSubtitleNew(standardGoods.get().getGoodsSubtitleNew());
                            }
                            convert.setGoodsUnit(GoodsMap.get(convert.getGoodsId()).getGoodsUnit());
                            convert.setDevanningUnit(GoodsMap.get(convert.getGoodsId()).getGoodsUnit());
                            devanningGoodsInfoRepository.save(convert);
                        }
                        //更新映射关系
                        GoodsInfoMapping byErpGoodsInfoNo = goodsInfoMappingRepository.findByErpGoodsInfoNo(sku.getErpGoodsInfoNo());
                        if(Objects.nonNull(byErpGoodsInfoNo) && !skuId.equals(byErpGoodsInfoNo.getGoodsInfoId())){
                            goodsInfoMappingRepository.updateGoodsInfoIdByErpNo(skuId,byErpGoodsInfoNo.getErpGoodsInfoNo());
                            GoodsInfo updateSku = new GoodsInfo();
                            updateSku.setGoodsInfoId(skuId);
                            updateSku.setParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId());
                            goodsInfoRepository.modifyParentGoodsInfoId(byErpGoodsInfoNo.getParentGoodsInfoId(), skuId);
                        }

                        newSkuIds.add(skuId);
                    }
                    String finalSkuId = skuId;
                    mappingSku.put(standardSku.getGoodsInfoId(), skuId);
                    //新增商品库存信息
                //    houseList.forEach(wareHouseVO -> {
                        GoodsWareStock goodsWareStock = new GoodsWareStock();
                        KsBeanUtil.copyPropertiesThird(wareHouseVO, goodsWareStock);
                        goodsWareStock.setGoodsInfoId(finalSkuId);
                        goodsWareStock.setGoodsInfoNo(sku.getGoodsInfoNo());
                        goodsWareStock.setGoodsId(sku.getGoodsId());
                        goodsWareStock.setStoreId(sku.getStoreId());
                        goodsWareStock.setStock(BigDecimal.ZERO);
                        goodsWareStock.setCreateTime(now);
                        goodsWareStock.setUpdateTime(now);
                        goodsWareStock.setDelFlag(DeleteFlag.NO);
                        //仓库id设置为1
                        goodsWareStock.setWareId(Long.valueOf(1));
                        goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId()+"_"+goodsWareStock.getWareId());
                        goodsWareStock.setAddStep(sku.getAddStep());//相对最小单位的换算率
                        goodsWareStock.setSaleType(sku.getSaleType());//销售类别(0:批发,1:零售,2散批)
                        if(SaleType.WHOLESALE.toValue()==goodsWareStock.getSaleType()){
                            goodsWareStock.setMainAddStep(BigDecimal.ONE);
                        }
                        standardGoodsList.add(goodsWareStock);
                        goodsWareStockService.add(goodsWareStock);
                        if(goodsWareStock.getWareId().equals(standardSku.getWareId())) {
                            updateGoodsWareStockByMutilSpeci(finalChildSkuListByMainSku, finalMainSkuListByChildSku, standardSku, goodsWareStock);
                        }
                        //新增乡镇件商品库存表信息[未考虑多仓信息，如以后开启多仓需要去掉此判断]
                        if ("WH01".equals(wareHouseVO.getWareCode())) {
                            GoodsWareStockVillages goodsWareStockVillages = KsBeanUtil.convert(goodsWareStock,GoodsWareStockVillages.class);
                            goodsWareStockVillagesService.addVillages(goodsWareStockVillages);
                        }

                  //  });
                });
            }

            //导入规格
            List<StandardSpec> specs = standardSpecRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(specs)) {
                specs.forEach(standardSpec -> {
                    GoodsSpec spec = new GoodsSpec();
                    BeanUtils.copyProperties(standardSpec, spec);
                    spec.setSpecId(null);
                    spec.setGoodsId(mappingSpu.get(standardSpec.getGoodsId()));
                    mappingSpec.put(standardSpec.getSpecId(), goodsSpecRepository.save(spec).getSpecId());
                });
            }

            //导入规格值
            List<StandardSpecDetail> details = standardSpecDetailRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(details)) {
                details.forEach(specDetail -> {
                    GoodsSpecDetail detail = new GoodsSpecDetail();
                    BeanUtils.copyProperties(specDetail, detail);
                    detail.setSpecDetailId(null);
                    detail.setSpecId(mappingSpec.get(specDetail.getSpecId()));
                    detail.setGoodsId(mappingSpu.get(specDetail.getGoodsId()));
                    mappingDetail.put(specDetail.getSpecDetailId(), goodsSpecDetailRepository.save(detail).getSpecDetailId());
                });
            }

            //导入规格值与Sku的关系
            List<StandardSkuSpecDetailRel> rels = standardSkuSpecDetailRelRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(rels)) {
                rels.forEach(standardRel -> {
                    GoodsInfoSpecDetailRel rel = new GoodsInfoSpecDetailRel();
                    BeanUtils.copyProperties(standardRel, rel);
                    rel.setSpecDetailRelId(null);
                    rel.setSpecId(mappingSpec.get(standardRel.getSpecId()));
                    rel.setSpecDetailId(mappingDetail.get(standardRel.getSpecDetailId()));
                    rel.setGoodsInfoId(mappingSku.get(standardRel.getGoodsInfoId()));
                    rel.setGoodsId(mappingSpu.get(standardRel.getGoodsId()));
                    goodsInfoSpecDetailRelRepository.save(rel);
                });
            }

            //导入图片
            List<StandardImage> imageList = standardImageRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(imageList)) {
                imageList.forEach(standardImage -> {
                    GoodsImage image = new GoodsImage();
                    BeanUtils.copyProperties(standardImage, image);
                    image.setImageId(null);
                    image.setGoodsId(mappingSpu.get(standardImage.getGoodsId()));
                    goodsImageRepository.save(image);
                });
            }

            //导入商品属性
            List<StandardPropDetailRel> propDetailRelList = standardPropDetailRelRepository.findByGoodsIds(request.getGoodsIds());
            if(CollectionUtils.isNotEmpty(propDetailRelList)) {
                propDetailRelList.forEach(goodsPropDetailRel -> {
                    GoodsPropDetailRel rel = new GoodsPropDetailRel();
                    BeanUtils.copyProperties(goodsPropDetailRel, rel);
                    rel.setRelId(null);
                    rel.setGoodsId(mappingSpu.get(goodsPropDetailRel.getGoodsId()));
                    goodsPropDetailRelRepository.save(rel);
                });
            }

            mappingSpu.values().forEach(spuId -> {
                StoreCateGoodsRela rela = new StoreCateGoodsRela();
                rela.setGoodsId(spuId);
                rela.setStoreCateId(defaultStoreCateId);
                storeCateGoodsRelaRepository.save(rela);
            });
        }
        if (CollectionUtils.isNotEmpty(newSkuIds)) {
            response.setSkuIdList(newSkuIds);
        }
        if (CollectionUtils.isNotEmpty(retailSkuIds)) {
            response.setRetailSkuIds(retailSkuIds);
        }
        if (CollectionUtils.isNotEmpty(bulkSkuIds)) {
            response.setBulkSkuIds(bulkSkuIds);
        }

        return response;
    }

    private List<FreightTemplateGoods> getFreightTemplateGoods(StandardImportRequest request) {
        List<FreightTemplateGoods> freightTemp;
        freightTemp = freightTemplateGoodsRepository.queryByDefault(request.getStoreId(),DeliverWay.EXPRESS);
        if(CollectionUtils.isEmpty(freightTemp)){
            freightTemp = freightTemplateGoodsRepository.queryByDefault(request.getStoreId(),DeliverWay.INTRA_CITY_LOGISTICS);
            if(CollectionUtils.isEmpty(freightTemp)) {
                throw new SbcRuntimeException(GoodsImportErrorCode.NOT_SETTING);
            }
        }
        return freightTemp;
    }

    private void updateGoodsWareStockByMutilSpeci(List<StandardSku> finalChildSkuListByMainSku, List<StandardSku> finalMainSkuListByChildSku, StandardSku standardSku, GoodsWareStock addingGoodsWareStock) {
        if(SaleType.WHOLESALE.toValue()== addingGoodsWareStock.getSaleType()){
            //修改子物料
            if(finalChildSkuListByMainSku.size()>0) {
                //根据主物料找到主物料下所有的子物料
                List<StandardSku> childStandardSkuList = finalChildSkuListByMainSku.stream().filter(s -> s.getMainSkuGoodsInfoId().equals(standardSku.getGoodsInfoId())).collect(Collectors.toList());
                List<String> erpGoodsInfoNo_1_list = new ArrayList<>(childStandardSkuList.size());
                List<String> erpGoodsInfoNo_2_list = new ArrayList<>(childStandardSkuList.size());
                for(StandardSku childSku : childStandardSkuList){
                    //子物料分类处理
                    if (SaleType.RETAIL.toValue() == childSku.getGoodsSaleType()) {
                        erpGoodsInfoNo_1_list.add(childSku.getErpGoodsInfoNo());
                    }else if (SaleType.BULK.toValue() == childSku.getGoodsSaleType()) {
                        erpGoodsInfoNo_2_list.add(childSku.getErpGoodsInfoNo());
                    }
                }
                List<String> goodsInfoIdList = null;
                if(CollectionUtils.isNotEmpty(erpGoodsInfoNo_1_list)){
                    //零售子物料
                    List<RetailGoodsInfo> goodsInfoList =  retailGoodsInfoRepository.findAllGoodsByErpNos(erpGoodsInfoNo_1_list);
                    goodsInfoIdList = goodsInfoList.stream().map(RetailGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
                    //零售子物料的库存
                    List<GoodsWareStock> retailChildWareStockList = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIdList);
                    if(CollectionUtils.isNotEmpty(retailChildWareStockList)){
                        retailChildWareStockList.forEach(retailChild->{
                            RetailGoodsInfo retailGoodsInfo = goodsInfoList.stream().filter(g->g.getGoodsInfoId().equals(retailChild.getGoodsInfoId()) && g.getWareId().equals(retailChild.getWareId())).findFirst().orElse(null);
                            if(retailGoodsInfo!=null){
                                retailChild.setSaleType(SaleType.RETAIL.toValue());
                                retailChild.setAddStep(retailGoodsInfo.getAddStep());
                                //子物料相对主物料换算关系 = 主物料步长/子物料步长
                                retailChild.setMainAddStep(addingGoodsWareStock.getAddStep().divide(retailChild.getAddStep(),BigDecimal.ROUND_HALF_UP));
                                retailChild.setMainSkuId(addingGoodsWareStock.getGoodsInfoId());
                                retailChild.setMainSkuWareId(addingGoodsWareStock.getWareId());
                                retailChild.setParentGoodsWareStockId(addingGoodsWareStock.getId());
                                goodsWareStockService.updateStockMutilSpeci(retailChild);
                            }
                        });
                    }
                }
                if(CollectionUtils.isNotEmpty(erpGoodsInfoNo_2_list)){
                    //散批子物料
                    List<BulkGoodsInfo> goodsInfoList =  bulkGoodsInfoRepository.findAllGoodsByErpNos(erpGoodsInfoNo_2_list);
                    goodsInfoIdList  = goodsInfoList.stream().map(BulkGoodsInfo::getGoodsInfoId).collect(Collectors.toList());
                    //散批子物料的库存
                    List<GoodsWareStock> bulkChildWareStockList = goodsWareStockService.findByGoodsInfoIdIn(goodsInfoIdList);
                    if(CollectionUtils.isNotEmpty(bulkChildWareStockList)){
                        bulkChildWareStockList.forEach(bulkChild->{
                            BulkGoodsInfo bulkGoodsInfo = goodsInfoList.stream().filter(g->g.getGoodsInfoId().equals(bulkChild.getGoodsInfoId())&& g.getWareId().equals(bulkChild.getWareId())).findFirst().orElse(null);
                            if(bulkGoodsInfo!=null){
                                bulkChild.setSaleType(SaleType.BULK.toValue());
                                bulkChild.setAddStep(bulkGoodsInfo.getAddStep());
                                //子物料相对主物料换算关系 = 主物料步长/子物料步长
                                bulkChild.setMainAddStep(addingGoodsWareStock.getAddStep().divide(bulkChild.getAddStep(),BigDecimal.ROUND_HALF_UP));
                                bulkChild.setMainSkuId(addingGoodsWareStock.getGoodsInfoId());
                                bulkChild.setMainSkuWareId(addingGoodsWareStock.getWareId());
                                bulkChild.setParentGoodsWareStockId(addingGoodsWareStock.getId());
                                goodsWareStockService.updateStockMutilSpeci(bulkChild);
                                bulkGoodsInfoRepository.updateFullStepById(bulkChild.getGoodsInfoId(),addingGoodsWareStock.getAddStep().intValue());
                            }
                        });
                    }
                }
            }
        }else{
            //找主物料
            if(finalMainSkuListByChildSku.size()>0 && StringUtils.isNotBlank(standardSku.getMainSkuGoodsInfoId())) {
                //子物料对应的主物料
                StandardSku mainStandSku = finalMainSkuListByChildSku.stream().filter(s -> s.getGoodsInfoId().equals(standardSku.getMainSkuGoodsInfoId())).findFirst().orElse(null);
                if(mainStandSku!=null){
                    //主物料
                    List<GoodsInfo> goodsInfoList =  goodsInfoRepository.findGoodsByErpNosAndWareId(mainStandSku.getErpGoodsInfoNo(),mainStandSku.getWareId());
                    if(CollectionUtils.isEmpty(goodsInfoList)){
                        return;
                    }
                    GoodsInfo wholesaleGoodsInfo = goodsInfoList.get(0);
                    //主物料库存
                    GoodsWareStock wholesaleGoodsWareStock = goodsWareStockService.getWareStockByWareIdAndInfoId(wholesaleGoodsInfo.getGoodsInfoId(),wholesaleGoodsInfo.getWareId());
                    if(wholesaleGoodsWareStock!=null){
                        //子物料相对主物料换算关系 = 主物料步长/子物料步长
                        addingGoodsWareStock.setMainAddStep(wholesaleGoodsWareStock.getAddStep().divide(addingGoodsWareStock.getAddStep(),BigDecimal.ROUND_HALF_UP));
                        addingGoodsWareStock.setMainSkuId(wholesaleGoodsWareStock.getGoodsInfoId());
                        addingGoodsWareStock.setMainSkuWareId(wholesaleGoodsWareStock.getWareId());
                        addingGoodsWareStock.setParentGoodsWareStockId(wholesaleGoodsWareStock.getId());
                        goodsWareStockService.updateStockMutilSpeci(addingGoodsWareStock);
                        //如果是散批，修改散批信息中的主物料步长
                        if(SaleType.BULK.toValue()==addingGoodsWareStock.getSaleType()) {
                            bulkGoodsInfoRepository.updateFullStepById(addingGoodsWareStock.getGoodsInfoId(), wholesaleGoodsWareStock.getAddStep().intValue());
                        }
                    }
                }
            }
        }
    }


    /**
     * 商品批量导入商品库
     * @param request 参数
     * @return
     */
    @Transactional
    public void importStandard(GoodsRequest request){
        if(standardGoodsRelRepository.countByGoodsIds(request.getGoodsIds()) > 0){
            throw new SbcRuntimeException(StandardGoodsErrorCode.COMPANY_IMPORT);
        }

        List<Goods> goodses = goodsRepository.findAll(GoodsQueryRequest.builder().goodsIds(request.getGoodsIds())
                .build().getWhereCriteria());

        if(CollectionUtils.isNotEmpty(goodses)){
            //规格映射Map,商品Id -> 新商品库Id
            Map<String, String> mappingSpu = new HashMap<>();
            //规格映射Map,商品规格Id -> 新商品库规格Id
            Map<Long, Long> mappingSpec = new HashMap<>();
            //规格映射Map,商品规格值Id -> 新商品库规格值Id
            Map<Long, Long> mappingDetail = new HashMap<>();
            //规格映射Map,商品SkuId -> 新商品库SkuId
            Map<String, String> mappingSku = new HashMap<>();
            LocalDateTime now = LocalDateTime.now();

            //导入Spu
            goodses.forEach(goods -> {
                StandardGoods standardGoods = new StandardGoods();
                BeanUtils.copyProperties(goods, standardGoods);
                standardGoods.setProviderName(goods.getSupplierName());
                standardGoods.setGoodsId(null);
                standardGoods.setDelFlag(DeleteFlag.NO);
                standardGoods.setCreateTime(now);
                standardGoods.setUpdateTime(now);
                String newGoodsId = standardGoodsRepository.save(standardGoods).getGoodsId();
                mappingSpu.put(goods.getGoodsId(), newGoodsId);

                //关联商品与商品库
                StandardGoodsRel rel = new StandardGoodsRel();
                rel.setGoodsId(goods.getGoodsId());
                rel.setStandardId(newGoodsId);
                rel.setStoreId(goods.getStoreId());
                standardGoodsRelRepository.save(rel);
            });

            //导入Sku
            List<GoodsInfo> skus = goodsInfoRepository.findAll(GoodsInfoQueryRequest.builder().goodsIds(request.getGoodsIds()).delFlag(DeleteFlag.NO.toValue()).build().getWhereCriteria());
            if (CollectionUtils.isNotEmpty(skus)) {
                skus.forEach(sku -> {
                    StandardSku standardSku = new StandardSku();
                    BeanUtils.copyProperties(sku, standardSku);
                    standardSku.setProviderGoodsInfoId(sku.getGoodsInfoId());
                    standardSku.setSupplyPrice(sku.getSupplyPrice());
                    standardSku.setStock(sku.getStock().longValue());
                    standardSku.setGoodsInfoId(null);
                    standardSku.setGoodsId(mappingSpu.get(sku.getGoodsId()));
                    standardSku.setCreateTime(now);
                    standardSku.setUpdateTime(now);
                    mappingSku.put(sku.getGoodsInfoId(), standardSkuRepository.save(standardSku).getGoodsInfoId());
                });
            }

            //导入规格
            List<GoodsSpec> specs = goodsSpecRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(specs)) {
                specs.forEach(spec -> {
                    StandardSpec standardSpec = new StandardSpec();
                    BeanUtils.copyProperties(spec, standardSpec);
                    standardSpec.setSpecId(null);
                    standardSpec.setGoodsId(mappingSpu.get(spec.getGoodsId()));
                    mappingSpec.put(spec.getSpecId(), standardSpecRepository.save(standardSpec).getSpecId());
                });
            }

            //导入规格值
            List<GoodsSpecDetail> details = goodsSpecDetailRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(details)) {
                details.forEach(specDetail -> {
                    StandardSpecDetail detail = new StandardSpecDetail();
                    BeanUtils.copyProperties(specDetail, detail);
                    detail.setSpecDetailId(null);
                    detail.setSpecId(mappingSpec.get(specDetail.getSpecId()));
                    detail.setGoodsId(mappingSpu.get(specDetail.getGoodsId()));
                    mappingDetail.put(specDetail.getSpecDetailId(), standardSpecDetailRepository.save(detail).getSpecDetailId());
                });
            }

            //导入规格值与Sku的关系
            List<GoodsInfoSpecDetailRel> rels = goodsInfoSpecDetailRelRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(rels)) {
                rels.forEach(rel -> {
                    StandardSkuSpecDetailRel standardRel = new StandardSkuSpecDetailRel();
                    BeanUtils.copyProperties(rel, standardRel);
                    standardRel.setSpecDetailRelId(null);
                    standardRel.setSpecId(mappingSpec.get(rel.getSpecId()));
                    standardRel.setSpecDetailId(mappingDetail.get(rel.getSpecDetailId()));
                    standardRel.setGoodsInfoId(mappingSku.get(rel.getGoodsInfoId()));
                    standardRel.setGoodsId(mappingSpu.get(rel.getGoodsId()));
                    standardSkuSpecDetailRelRepository.save(standardRel);
                });
            }

            //导入图片
            List<GoodsImage> imageList = goodsImageRepository.findByGoodsIds(request.getGoodsIds());
            if (CollectionUtils.isNotEmpty(imageList)) {
                imageList.forEach(image -> {
                    StandardImage standardImage = new StandardImage();
                    BeanUtils.copyProperties(image, standardImage);
                    standardImage.setImageId(null);
                    standardImage.setGoodsId(mappingSpu.get(image.getGoodsId()));
                    standardImageRepository.save(standardImage);
                });
            }

            //导入商品属性
            List<GoodsPropDetailRel> propDetailRelList = goodsPropDetailRelRepository.findByGoodsIds(request.getGoodsIds());
            if(CollectionUtils.isNotEmpty(propDetailRelList)) {
                propDetailRelList.forEach(goodsPropDetailRel -> {
                    StandardPropDetailRel rel = new StandardPropDetailRel();
                    BeanUtils.copyProperties(goodsPropDetailRel, rel);
                    rel.setRelId(null);
                    rel.setGoodsId(mappingSpu.get(goodsPropDetailRel.getGoodsId()));
                    standardPropDetailRelRepository.save(rel);
                });
            }
        }
    }
}
