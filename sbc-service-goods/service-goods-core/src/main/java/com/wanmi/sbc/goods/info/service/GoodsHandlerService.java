package com.wanmi.sbc.goods.info.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.response.store.StoreSimpleResponse;
import com.wanmi.sbc.goods.api.request.goods.GoodsCopyByStoreRequest;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.enums.GoodsWareStockImportType;
import com.wanmi.sbc.goods.bean.enums.SaleType;
import com.wanmi.sbc.goods.brand.model.root.ContractBrand;
import com.wanmi.sbc.goods.brand.repository.ContractBrandRepository;
import com.wanmi.sbc.goods.brand.request.ContractBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.ContractBrandService;
import com.wanmi.sbc.goods.cate.model.root.ContractCate;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.ContractCateRepository;
import com.wanmi.sbc.goods.cate.request.ContractCateQueryRequest;
import com.wanmi.sbc.goods.cate.request.ContractCateSaveRequest;
import com.wanmi.sbc.goods.cate.service.ContractCateService;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.config.GoodsNacosConfig;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.repository.DevanningGoodsInfoRepository;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateGoods;
import com.wanmi.sbc.goods.freight.service.FreightTemplateGoodsService;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodsattributekey.service.GoodsAttributeKeyService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import com.wanmi.sbc.goods.mapping.service.GoodsInfoMappingService;
import com.wanmi.sbc.goods.storecate.model.root.StoreCate;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.goods.storecate.request.StoreCateQueryRequest;
import com.wanmi.sbc.goods.storecate.service.StoreCateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
public class GoodsHandlerService {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private DevanningGoodsInfoRepository devanningGoodsInfoRepository;

    @Autowired
    private GoodsAttributeKeyService goodsAttributeKeyService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockDetailService goodsWareStockDetailService;

    @Autowired
    private GoodsInfoMappingService goodsInfoMappingService;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;

    @Autowired
    private FreightTemplateGoodsService freightTemplateGoodsService;

    @Autowired
    private ContractBrandService contractBrandService;

    @Autowired
    private ContractBrandRepository contractBrandRepository;

    @Autowired
    private StoreCateService storeCateService;

    @Autowired
    private ContractCateService contractCateService;

    @Autowired
    private ContractCateRepository contractCateRepository;

    @Autowired
    private GoodsNacosConfig goodsNacosConfig;

    private static String generateGoodsNo() {
        // 生产orderNo和前端的逻辑保持一致，之前前端传的
        return "P" +
                String.valueOf(new Date().getTime())
                        .substring(4, 10) +
                String.valueOf(Math.random())
                        .substring(2, 5);
    }

    private static String generateGoodsInfoNo() {
        // 生产orderNo和前端的逻辑保持一致，之前前端传的
        return '8' + String.valueOf(new Date().getTime()).substring(4, 10) + String.valueOf(Math.random()).substring(2, 5);
    }

    private Long getFreightTemplate(Long storeId) {
        List<FreightTemplateGoods> templateGoods = freightTemplateGoodsService.queryAll(storeId, DeliverWay.EXPRESS.toValue());
        return templateGoods.get(0).getFreightTempId();
    }

    private String getErpNo(long cateId, long storeId, int count) {

        StringBuffer buffer = new StringBuffer();

        GoodsCate info = goodsCateService.findById(cateId);

        String[] catePath = info.getCatePath().split(Constants.CATE_PATH_SPLITTER);

        List<GoodsInfo> allList = goodsInfoRepository.findAll((GoodsInfoQueryRequest.builder().storeId(storeId).build().getWhereCriteria()));

        //添加两级分类
        buffer.append(catePath[Constants.yes]);
        buffer.append(Constants.STRING_SLASH_HENG);
        buffer.append(catePath[Constants.IMPORT_GOODS_MAX_SIZE]);
        buffer.append(Constants.STRING_SLASH_HENG);
        String newADD = String.valueOf(String.format("%0" + 3 + "d", allList.size() + count));
        buffer.append(newADD);
        buffer.append(Constants.STRING_SLASH_HENG);
        buffer.append(storeId);
        String erpNo = buffer.toString();
        final GoodsInfoMapping byErpGoodsInfoNo = goodsInfoMappingService.findByErpGoodsInfoNo(erpNo);
        // 如何重复直接随机类目 这个方法有bug，直接把最多只能999个商品
        if (null != byErpGoodsInfoNo){
            Random random = new Random();
            int random_number = random.nextInt(10000); // 生成 0 到 9999 之间的随机数
            String formatted_number = String.format("%04d", random_number);
            buffer = new StringBuffer();
            buffer.append(formatted_number);
            buffer.append(Constants.STRING_SLASH_HENG);
            buffer.append(catePath[Constants.IMPORT_GOODS_MAX_SIZE]);
            buffer.append(Constants.STRING_SLASH_HENG);
            buffer.append(String.format("%0" + 3 + "d", allList.size() + count));
            buffer.append(Constants.STRING_SLASH_HENG);
            buffer.append(storeId);
            erpNo = buffer.toString();
        }
        return erpNo;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<String> copyGoodsByStore(GoodsCopyByStoreRequest request, StoreSimpleResponse storeSimpleResponse) {
        final LocalDateTime now = LocalDateTime.now();
        final Long storeId = request.getStoreId();
        final String supplierName = storeSimpleResponse.getSupplierName();
        final Long companyInfoId = storeSimpleResponse.getCompanyInfoId();
        final Long freightTemplateId = getFreightTemplate(storeId);
        List<String> goodsIds = new ArrayList<>();
        final List<String> goodsIdsOld = request.getGoodsIds();
        final List<Goods> goodsList = goodsRepository.findByGoodsIdIn(goodsIdsOld);
        if (!storeSimpleResponse.getCompanyType().equals(CompanyType.SUPPLIER)) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "复制目标商家必须是第三方商家");
        }
        if (goodsList.stream().anyMatch(f -> !Objects.equals(f.getCompanyType(), CompanyType.SUPPLIER))) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "复制的商品必须是第三方商家");
        }
        if (goodsList.stream().map(Goods::getStoreId).distinct().count() > 1) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "复制的商品必须是来自一个商家");
        }
        if (null == freightTemplateId) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商家没有默认运费模板");
        }
        if (goodsIdsOld.size() > goodsNacosConfig.getCopyGoodsSpuLimit()) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "复制商品数量超过最大限制，限制数量为：" + goodsNacosConfig.getCopyGoodsSpuLimit());
        }
        boolean sameStore = Objects.equals(storeId, goodsList.get(0).getStoreId());
        final List<StoreCateGoodsRela> storeCateGoodsRelas = storeCateGoodsRelaRepository.selectByGoodsId(goodsIdsOld);
        final Map<String, Long> goodsStoreCatMap;
        if (!sameStore) {
            wrapBrand(storeId, goodsList);
            wrapCat(storeId, goodsList);
            goodsStoreCatMap = wrapStoreCat(storeId, storeCateGoodsRelas);
        } else {
            goodsStoreCatMap = new HashMap<>();
        }
        goodsList.forEach(goodsOld -> {
            final String goodsIdOld = goodsOld.getGoodsId();

            final List<GoodsInfo> goodsInfosOld = goodsInfoRepository.findByGoodsIdIn(Lists.newArrayList(goodsIdOld));
            final GoodsInfo goodsInfoOld = goodsInfosOld.get(0);
            if (goodsInfosOld.size() != 1) {
                log.warn("复制sku商品存在多个规格,goodsId:{}", goodsIdOld);
                return;
            }
            // 拆箱表
            final String goodsInfoIdOld = goodsInfoOld.getGoodsInfoId();
            List<DevanningGoodsInfo> devanningGoodsInfosOld = devanningGoodsInfoRepository.getByInfoId(goodsInfoIdOld);
            if (devanningGoodsInfosOld.size() != 1) {
                log.warn("复制拆箱商品存在多个规格,goodsId:{}", goodsIdOld);
                return;
            }

            final Goods convertGoods = KsBeanUtil.convert(goodsOld, Goods.class);
            convertGoods.setGoodsId(null);
            if (!sameStore) {
                convertGoods.setFreightTempId(freightTemplateId);
            }
            convertGoods.setStoreId(storeId);
            convertGoods.setSupplierName(supplierName);
            convertGoods.setCompanyInfoId(companyInfoId);
            convertGoods.setGoodsNo(generateGoodsNo());
            convertGoods.setCreateTime(now);
            convertGoods.setUpdateTime(now);
            convertGoods.setAddedTime(now);
            convertGoods.setSubmitTime(now);
            final Goods goodsSave = goodsRepository.save(convertGoods);
            final String goodsId = goodsSave.getGoodsId();
            goodsIds.add(goodsId);

            // goodsInfo
            final GoodsInfo convertGoodsInfo = KsBeanUtil.convert(goodsInfoOld, GoodsInfo.class);
            convertGoodsInfo.setCreateTime(now);
            convertGoodsInfo.setGoodsInfoId(null);
            convertGoodsInfo.setUpdateTime(now);
            convertGoodsInfo.setAddedTime(now);
            convertGoodsInfo.setGoodsId(goodsId);
            if (!sameStore) {
                convertGoodsInfo.setFreightTempId(freightTemplateId);
            }
            convertGoodsInfo.setStoreId(storeId);
            convertGoodsInfo.setCompanyInfoId(companyInfoId);
            convertGoodsInfo.setHostSku(1);
            convertGoodsInfo.setGoodsInfoNo(generateGoodsInfoNo());
            convertGoodsInfo.setErpGoodsInfoNo(getErpNo(goodsOld.getCateId(), storeId, 1));
            final GoodsInfo goodsInfoSave = goodsInfoRepository.save(convertGoodsInfo);
            final String goodsInfoId = goodsInfoSave.getGoodsInfoId();


            final DevanningGoodsInfo devanningGoodsInfoOld = devanningGoodsInfosOld.get(0);
            final DevanningGoodsInfo devanningGoodsInfo = KsBeanUtil.convert(devanningGoodsInfoOld, DevanningGoodsInfo.class);
            devanningGoodsInfo.setDevanningId(null);
            devanningGoodsInfo.setCreateTime(now);
            devanningGoodsInfo.setUpdateTime(now);
            devanningGoodsInfo.setGoodsId(goodsId);
            devanningGoodsInfo.setGoodsInfoId(goodsInfoId);
            devanningGoodsInfo.setGoodsInfoBatchNo(goodsInfoSave.getGoodsInfoBatchNo());
            devanningGoodsInfo.setGoodsInfoNo(goodsInfoSave.getGoodsInfoNo());
            devanningGoodsInfo.setHostSku(1);
            devanningGoodsInfo.setStoreId(storeId);
            devanningGoodsInfo.setCompanyInfoId(companyInfoId);
            final DevanningGoodsInfo devanningGoodsInfoSave = devanningGoodsInfoRepository.save(devanningGoodsInfo);

            if (CollectionUtils.isNotEmpty(storeCateGoodsRelas)) {
                final List<StoreCateGoodsRela> storeCateGoodsRelasNew = KsBeanUtil.convertList(storeCateGoodsRelas, StoreCateGoodsRela.class);
                storeCateGoodsRelasNew.forEach(rela -> {
                    rela.setGoodsId(goodsId);
                    Long storeCatId = goodsStoreCatMap.get(goodsIdOld);
                    if (null != storeCatId) {
                        rela.setStoreCateId(storeCatId);
                    }
                    storeCateGoodsRelaRepository.save(rela);
                });
            }

            // 图片
            final List<GoodsImage> goodsImagesOld = goodsImageRepository.findByGoodsId(goodsIdOld);
            if (CollectionUtils.isNotEmpty(goodsImagesOld)) {
                goodsImagesOld.forEach(goodsImageOld -> {
                    final GoodsImage convertImage = KsBeanUtil.convert(goodsImageOld, GoodsImage.class);
                    convertImage.setGoodsId(goodsId);
                    convertImage.setCreateTime(now);
                    convertImage.setUpdateTime(now);
                    convertImage.setGoodsInfoId(null);
                    convertImage.setImageId(null);
                    goodsImageRepository.save(convertImage);
                });
            }

            // 属性
            final List<GoodsAttributeKey> attributeKeysOld = goodsAttributeKeyService.queryGoodsInfoList(Lists.newArrayList(goodsInfoIdOld));
            attributeKeysOld.forEach(goodsAttributeOld -> {
                final GoodsAttributeKey goodsAttributeKeyInsert = KsBeanUtil.convert(goodsAttributeOld, GoodsAttributeKey.class);
                goodsAttributeKeyInsert.setGoodsId(goodsId);
                goodsAttributeKeyInsert.setGoodsInfoId(goodsInfoId);
                goodsAttributeKeyInsert.setAttributeId(null);
                goodsAttributeKeyInsert.setAttribute(null);
                goodsAttributeKeyService.add(goodsAttributeKeyInsert);
            });

//            final List<GoodsWareStock> goodsWareStockList = goodsWareStockService.findByGoodsInfoIdIn(Lists.newArrayList(goodsInfoIdOld));
//            final BigDecimal stock = goodsWareStockList.get(0).getStock();
            final BigDecimal stock = goodsInfoSave.getStock();

            // 新增商品库存信息
            GoodsWareStock goodsWareStock = new GoodsWareStock();
            goodsWareStock.setGoodsInfoId(goodsInfoId);
            goodsWareStock.setGoodsInfoNo(goodsInfoSave.getGoodsInfoNo());
            goodsWareStock.setGoodsId(goodsId);
            goodsWareStock.setStoreId(storeId);
            goodsWareStock.setStock(stock);
            goodsWareStock.setCreateTime(now);
            goodsWareStock.setUpdateTime(now);
            goodsWareStock.setWareId(goodsInfoSave.getWareId());
            goodsWareStock.setDelFlag(DeleteFlag.NO);
            goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId() + "_" + goodsWareStock.getWareId());
            goodsWareStock.setAddStep(goodsInfoSave.getAddStep());//相对最小单位的换算率
            goodsWareStock.setSaleType(goodsInfoSave.getSaleType());//销售类别(0:批发,1:零售,2散批)
            goodsWareStock.setAddStep(goodsInfoSave.getAddStep());
            if (SaleType.WHOLESALE.toValue() == goodsWareStock.getSaleType()) {
                goodsWareStock.setMainAddStep(BigDecimal.ONE);
            }
            GoodsWareStock addStock = goodsWareStockService.add(goodsWareStock);
            //
            if (Objects.nonNull(addStock)) {
                GoodsWareStockDetail goodsWareStockDetail = new GoodsWareStockDetail();
                goodsWareStockDetail.setGoodsWareStockId(addStock.getId());
                goodsWareStockDetail.setStockImportNo(new StringBuilder("IM").append(RandomStringUtils.randomNumeric(6)).toString());
                goodsWareStockDetail.setGoodsInfoId(goodsInfoSave.getGoodsInfoId().toString());
                goodsWareStockDetail.setGoodsInfoNo(goodsInfoSave.getGoodsInfoNo());
                goodsWareStockDetail.setStock(stock.longValue());
                goodsWareStockDetail.setWareId(goodsInfoSave.getWareId());
                goodsWareStockDetail.setImportType(GoodsWareStockImportType.EDIT);
                goodsWareStockDetail.setOperateStock(stock.longValue());
                goodsWareStockDetail.setDelFlag(DeleteFlag.NO);
                //  goodsWareStockDetail.setCreatePerson();
                goodsWareStockDetail.setCreateTime(now);
                goodsWareStockDetailService.add(goodsWareStockDetail);
            }

            // 更新关联表
            GoodsInfoMapping mapping = new GoodsInfoMapping();
            mapping.setGoodsInfoId(goodsInfoId);
            mapping.setErpGoodsInfoNo(goodsInfoSave.getErpGoodsInfoNo());
            mapping.setWareId(goodsInfoSave.getWareId());
            GoodsInfoMapping goodsInfoMappingSave = goodsInfoMappingService.saveGoodsInfoMapping(mapping);
            if (null != goodsInfoMappingSave) {
                goodsInfoSave.setParentGoodsInfoId(goodsInfoMappingSave.getParentGoodsInfoId());
                goodsInfoRepository.save(goodsInfoSave);
                devanningGoodsInfoSave.setParentGoodsInfoId(goodsInfoMappingSave.getParentGoodsInfoId());
                devanningGoodsInfoRepository.save(devanningGoodsInfoSave);
            }
        });
        return goodsIds;
    }

    private void wrapCat(Long storeId, List<Goods> goodsList) {
        final ContractCateQueryRequest contractCateQueryRequest = new ContractCateQueryRequest();
        contractCateQueryRequest.setStoreId(storeId);
        List<ContractCate> catesStore = contractCateRepository.findAll(contractCateQueryRequest.getWhereCriteria());
        final List<Long> ownerStoreIds = catesStore.stream().map(f -> f.getGoodsCate().getCateId()).distinct().collect(Collectors.toList());

        final List<Long> contrCatIds = goodsList.stream().map(Goods::getCateId).collect(Collectors.toList());
        final List<GoodsCate> cates = goodsCateService.findByIds(contrCatIds);
        cates.forEach(f -> {
            if (!ownerStoreIds.contains(f.getCateId())){
                final ContractCateSaveRequest contractCateSaveRequest = new ContractCateSaveRequest();
                contractCateSaveRequest.setContractCateId(null);
                contractCateSaveRequest.setStoreId(storeId);
                contractCateSaveRequest.setCateId(f.getCateId());
                contractCateSaveRequest.setCateRate(f.getCateRate());
                contractCateService.add(contractCateSaveRequest);
            }
        });
    }

    private Map<String, Long> wrapStoreCat(Long storeId, List<StoreCateGoodsRela> storeCateGoodsRelas) {
        Map<String, Long> map = new HashMap<>();
        Map<Long, Long> catMap = new HashMap<>();
        final List<Long> storeCatIds = storeCateGoodsRelas.stream().map(StoreCateGoodsRela::getStoreCateId).distinct().collect(Collectors.toList());
        final List<StoreCate> storeCates = storeCateService.findByIds(storeCatIds);

        final List<StoreCate> storeCatList = storeCateService.list(StoreCateQueryRequest.builder().storeId(storeId).delFlag(DeleteFlag.NO).build());
        storeCates.forEach(o -> {
            if (Objects.equals(0L, o.getCateParentId())) {
                return;
            }
            final Long storeCatId = storeCateService.createStoreCatName(storeId, o.getCateName(), storeCatList);
            catMap.put(o.getStoreCateId(), storeCatId);
        });
        storeCateGoodsRelas.forEach(o -> map.put(o.getGoodsId(), catMap.get(o.getStoreCateId())));
        return map;
    }

    private void wrapBrand(Long storeId, List<Goods> goodsList) {
        final List<Long> brandIds = goodsList.stream().map(Goods::getBrandId).distinct().collect(Collectors.toList());
        final ContractBrandQueryRequest brandQuery = new ContractBrandQueryRequest();
        brandQuery.setGoodsBrandIds(brandIds);
        final List<ContractBrand> contractBrandsOld = contractBrandService.queryList(brandQuery);

        final ContractBrandQueryRequest contractBrandQueryRequest = new ContractBrandQueryRequest();
        contractBrandQueryRequest.setStoreId(storeId);
        final List<ContractBrand> contractBrands = contractBrandService.queryList(contractBrandQueryRequest);
        final Map<Long, ContractBrand> brandIdMap = contractBrands.stream().collect(Collectors.toMap(f -> f.getGoodsBrand().getBrandId(), Function.identity(),(o,n) -> o));

        contractBrandsOld.forEach(brand -> {
            final ContractBrand convertBrand = KsBeanUtil.convert(brand, ContractBrand.class);
            final ContractBrand contractBrand = brandIdMap.get(convertBrand.getGoodsBrand().getBrandId());
            if (null == contractBrand) {
                convertBrand.setContractBrandId(null);
                convertBrand.setCheckBrand(null);
                convertBrand.setStoreId(storeId);
                contractBrandRepository.save(convertBrand);
            }
        });
    }
}
