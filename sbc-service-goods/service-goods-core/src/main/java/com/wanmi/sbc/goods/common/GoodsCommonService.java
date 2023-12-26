package com.wanmi.sbc.goods.common;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattributekey.GoodsAttributeKeyQueryProvider;
import com.wanmi.sbc.goods.api.request.common.GoodsCommonBatchAddRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoRequest;
import com.wanmi.sbc.goods.bean.dto.*;
import com.wanmi.sbc.goods.bean.enums.*;
import com.wanmi.sbc.goods.bean.vo.DevanningGoodsInfoVO;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.devanninggoodsinfo.model.root.DevanningGoodsInfo;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.goodsattribute.service.GoodsAttributeService;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import com.wanmi.sbc.goods.goodsattributekey.service.GoodsAttributeKeyService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.goodswarestockdetail.model.root.GoodsWareStockDetail;
import com.wanmi.sbc.goods.goodswarestockdetail.service.GoodsWareStockDetailService;
import com.wanmi.sbc.goods.images.GoodsImage;
import com.wanmi.sbc.goods.images.GoodsImageRepository;
import com.wanmi.sbc.goods.info.model.root.BulkGoods;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.RetailGoods;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoQueryRequest;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import com.wanmi.sbc.goods.storecate.model.root.StoreCateGoodsRela;
import com.wanmi.sbc.goods.storecate.repository.StoreCateGoodsRelaRepository;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品公共服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class GoodsCommonService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsImageRepository goodsImageRepository;

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;

    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;

    @Autowired
    private StoreCateGoodsRelaRepository storeCateGoodsRelaRepository;
    @Autowired
    private GoodsWareStockDetailService goodsWareStockDetailService;
    @Autowired
    private OsUtil osUtil;

    @Autowired
    private AuditQueryProvider auditQueryProvider;

    @Autowired
    private GoodsAttributeKeyService goodsAttributeKeyService;
    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;
    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsCateService goodsCateService;
    @Autowired
    private GoodsCommonService goodsCommonService;
    /**
     * 批量导入商品数据
     * @param request 商品批量信息
     * @return 批量新增的skuId
     */
    @Transactional
    public List<String> batchAdd(GoodsCommonBatchAddRequest request){
        Map<String, List<BatchGoodsInfoDTO>> skus = request.getGoodsInfoList().stream()
                .collect(Collectors.groupingBy(BatchGoodsInfoDTO::getMockGoodsId));

        Map<String, List<BatchGoodsSpecDTO>> allSpecs = new HashMap<>();
        if (CollectionUtils.isNotEmpty(request.getSpecList())) {
            allSpecs.putAll(request.getSpecList().stream()
                    .collect(Collectors.groupingBy(BatchGoodsSpecDTO::getMockGoodsId)));
        }

        Map<String, List<BatchGoodsSpecDetailDTO>> allSpecDetails = new HashMap<>();
        if (CollectionUtils.isNotEmpty(request.getSpecList())) {
            allSpecDetails.putAll(request.getSpecDetailList().stream()
                    .collect(Collectors.groupingBy(BatchGoodsSpecDetailDTO::getMockGoodsId)));
        }

        Map<String, List<BatchGoodsImageDTO>> images = new HashMap<>();
        if (CollectionUtils.isNotEmpty(request.getImageList())) {
            images.putAll(request.getImageList().stream()
                    .collect(Collectors.groupingBy(BatchGoodsImageDTO::getMockGoodsId)));
        }

        List<String> newSkuIds = new ArrayList<>();
        request.getGoodsList().forEach(goods -> {
            goods.setCreateTime(LocalDateTime.now());
            goods.setAddedTime(goods.getCreateTime());
            goods.setUpdateTime(goods.getCreateTime());
            goods.setCustomFlag(Constants.no);
            goods.setLevelDiscountFlag(Constants.no);
            goods.setDelFlag(DeleteFlag.NO);
            goods.setPriceType(GoodsPriceType.MARKET.toValue());

            List<BatchGoodsInfoDTO> goodsInfoList = skus.get(goods.getGoodsNo());
            goods.setMoreSpecFlag(Constants.no);
            if (goodsInfoList.stream().anyMatch(goodsInfo -> CollectionUtils.isNotEmpty(goodsInfo.getMockSpecDetailIds()))) {
                goods.setMoreSpecFlag(Constants.yes);
            }

            //判定上下架
            long yes_addedFlag = goodsInfoList.stream().filter(goodsInfo -> AddedFlag.YES.toValue() == goodsInfo.getAddedFlag()).count();
            goods.setAddedFlag(AddedFlag.PART.toValue());
            if (goodsInfoList.size() == yes_addedFlag) {
                goods.setAddedFlag(AddedFlag.YES.toValue());
            } else if (yes_addedFlag == 0) {
                goods.setAddedFlag(AddedFlag.NO.toValue());
            }

            Goods tempGoods = KsBeanUtil.convert(goods, Goods.class);
            this.setCheckState(tempGoods);
            goods.setAuditStatus(tempGoods.getAuditStatus());
            tempGoods.setGoodsSalesNum(0L);
            tempGoods.setGoodsCollectNum(0L);
            tempGoods.setGoodsEvaluateNum(0L);
            tempGoods.setGoodsFavorableCommentNum(0L);
            String goodsId = goodsRepository.save(tempGoods).getGoodsId();

            List<BatchGoodsSpecDTO> specs = allSpecs.getOrDefault(goods.getMockGoodsId(), Collections.emptyList());
            List<BatchGoodsSpecDetailDTO> specDetails = allSpecDetails.getOrDefault(goods.getMockGoodsId(), Collections.emptyList());

            //如果是多规格
            if (Constants.yes.equals(goods.getMoreSpecFlag())) {
                //新增含有规格值的规格
                specs.stream()
                        .filter(goodsSpec -> specDetails.stream().anyMatch(goodsSpecDetail ->
                                goodsSpecDetail.getMockSpecId().equals(goodsSpec.getMockSpecId())))
                        .forEach(goodsSpec -> {
                            goodsSpec.setCreateTime(goods.getCreateTime());
                            goodsSpec.setUpdateTime(goods.getCreateTime());
                            goodsSpec.setGoodsId(goodsId);
                            goodsSpec.setDelFlag(DeleteFlag.NO);
                            goodsSpec.setSpecId(goodsSpecRepository.save(
                                    KsBeanUtil.convert(goodsSpec, GoodsSpec.class)).getSpecId());
                        });
                //新增规格值
                specDetails.forEach(goodsSpecDetail -> {
                    Optional<BatchGoodsSpecDTO> specOpt = specs.stream().filter(goodsSpec -> goodsSpec.getMockSpecId
                            ().equals(goodsSpecDetail.getMockSpecId())).findFirst();
                    if (specOpt.isPresent()) {
                        goodsSpecDetail.setCreateTime(goods.getCreateTime());
                        goodsSpecDetail.setUpdateTime(goods.getCreateTime());
                        goodsSpecDetail.setGoodsId(goodsId);
                        goodsSpecDetail.setDelFlag(DeleteFlag.NO);
                        goodsSpecDetail.setSpecId(specOpt.get().getSpecId());
                        goodsSpecDetail.setSpecDetailId(goodsSpecDetailRepository.save(
                                KsBeanUtil.convert(goodsSpecDetail, GoodsSpecDetail.class)).getSpecDetailId());
                    }
                });
            }

            goodsInfoList.forEach(goodsInfo -> {
                goodsInfo.setGoodsId(goodsId);
                goodsInfo.setGoodsInfoName(goods.getGoodsName());
                goodsInfo.setCreateTime(goods.getCreateTime());
                goodsInfo.setUpdateTime(goods.getCreateTime());
                goodsInfo.setAddedTime(goods.getCreateTime());
                goodsInfo.setDelFlag(goods.getDelFlag());
                goodsInfo.setCompanyInfoId(goods.getCompanyInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setCustomFlag(goods.getCustomFlag());
                goodsInfo.setLevelDiscountFlag(goods.getLevelDiscountFlag());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setStoreId(goods.getStoreId());
                goodsInfo.setAuditStatus(goods.getAuditStatus());
                goodsInfo.setCompanyType(goods.getCompanyType());
                goodsInfo.setAloneFlag(Boolean.FALSE);
                goodsInfo.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                goodsInfo.setSaleType(goods.getSaleType());
                String skuId = goodsInfoRepository.save(KsBeanUtil.convert(goodsInfo, GoodsInfo.class))
                        .getGoodsInfoId();
                newSkuIds.add(skuId);
                //存储规格
                //如果是多规格,新增SKU与规格明细值的关联表
                if (Constants.yes.equals(goods.getMoreSpecFlag())) {
                    for (BatchGoodsSpecDTO spec : specs) {
                        if (goodsInfo.getMockSpecIds().contains(spec.getMockSpecId())) {
                            for (BatchGoodsSpecDetailDTO detail : specDetails) {
                                if (spec.getMockSpecId().equals(detail.getMockSpecId()) && goodsInfo
                                        .getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
                                    GoodsInfoSpecDetailRel detailRel = new GoodsInfoSpecDetailRel();
                                    detailRel.setGoodsId(goodsId);
                                    detailRel.setGoodsInfoId(skuId);
                                    detailRel.setSpecId(spec.getSpecId());
                                    detailRel.setSpecDetailId(detail.getSpecDetailId());
                                    detailRel.setDetailName(detail.getDetailName());
                                    detailRel.setCreateTime(detail.getCreateTime());
                                    detailRel.setUpdateTime(detail.getUpdateTime());
                                    detailRel.setDelFlag(detail.getDelFlag());
                                    detailRel.setSpecName(spec.getSpecName());
                                    goodsInfoSpecDetailRelRepository.save(detailRel);
                                }
                            }
                        }
                    }
                }
            });

            if (CollectionUtils.isNotEmpty(goods.getStoreCateIds())) {
                goods.getStoreCateIds().forEach(cateId -> {
                    StoreCateGoodsRela rela = new StoreCateGoodsRela();
                    rela.setGoodsId(goodsId);
                    rela.setStoreCateId(cateId);
                    storeCateGoodsRelaRepository.save(rela);
                });
            }

            //批量保存
            List<BatchGoodsImageDTO> imageUrls = images.get(goods.getGoodsNo());
            if (CollectionUtils.isNotEmpty(imageUrls)) {
                imageUrls.forEach(img -> {
                    img.setGoodsId(goodsId);
                    img.setCreateTime(goods.getCreateTime());
                    img.setUpdateTime(goods.getCreateTime());
                    img.setDelFlag(goods.getDelFlag());
                    goodsImageRepository.save(KsBeanUtil.convert(img, GoodsImage.class));
                });
            }
        });
        return newSkuIds;
    }
    /**
     * 批量导入商品数据
     * @param request 商品批量信息
     * @return 批量新增的skuId
     */
    @Transactional
    public List<String> storeBatchAdd(GoodsCommonBatchAddRequest request){
        Map<String, List<BatchGoodsInfoDTO>> skus = request.getGoodsInfoList().stream()
                .collect(Collectors.groupingBy(BatchGoodsInfoDTO::getMockGoodsId));

        Map<String, List<BatchGoodsImageDTO>> images = new HashMap<>();
        if (CollectionUtils.isNotEmpty(request.getImageList())) {
            images.putAll(request.getImageList().stream()
                    .collect(Collectors.groupingBy(BatchGoodsImageDTO::getMockGoodsId)));
        }

        List<String> newSkuIds = new ArrayList<>();
        request.getGoodsList().forEach(goods -> {
            goods.setCreateTime(LocalDateTime.now());
            goods.setAddedTime(goods.getCreateTime());
            goods.setUpdateTime(goods.getCreateTime());
            goods.setCustomFlag(Constants.no);
            goods.setLevelDiscountFlag(Constants.no);
            goods.setDelFlag(DeleteFlag.NO);
            goods.setPriceType(GoodsPriceType.MARKET.toValue());

            List<BatchGoodsInfoDTO> goodsInfoList = skus.get(goods.getGoodsNo());
            goods.setMoreSpecFlag(Constants.no);
            Goods tempGoods = KsBeanUtil.convert(goods, Goods.class);
            this.setCheckState(tempGoods);
            goods.setAuditStatus(tempGoods.getAuditStatus());
            tempGoods.setGoodsSalesNum(0L);
            tempGoods.setGoodsCollectNum(0L);
            tempGoods.setGoodsEvaluateNum(0L);
            tempGoods.setGoodsFavorableCommentNum(0L);
            tempGoods.setProviderId(0L);
            //spu 显示 主spu 的价格
            goodsInfoList.forEach(goodsInfo -> {
                if (String.valueOf(goodsInfo.getHostSku()).equals(Constants.yes.toString())||goodsInfoList.size()==Constants.yes ){
                    BigDecimal addStep = goodsInfo.getAddStep();//步长
                    if (Objects.isNull(addStep)){
                        addStep=BigDecimal.ONE;
                    }
                    BigDecimal subtitlePrice = goodsInfo.getMarketPrice().divide(addStep, 2, BigDecimal.ROUND_UP);
                    //1箱(30.00杯x0.00元/杯)
                    String goodsSubtitle = "1"+goodsInfo.getGoodsInfoUnit()+"=" + addStep + goodsInfo.getDevanningUnit() + "x" + subtitlePrice + "元/" +  goodsInfo.getDevanningUnit();
                    String goodsSubtitleNew = subtitlePrice + "/" + goodsInfo.getDevanningUnit();
                    tempGoods.setGoodsSubtitle(goodsSubtitle);
                    tempGoods.setGoodsSubtitleNew(goodsSubtitleNew);
                    if (Objects.nonNull(goodsInfo) && Objects.nonNull(goodsInfo.getCostPrice())){
                        tempGoods.setCostPrice(goodsInfo.getCostPrice());
                    }else {
                        tempGoods.setCostPrice(goodsInfo.getMarketPrice());
                    }
                    tempGoods.setGoodsUnit(goodsInfo.getGoodsInfoUnit());
                    tempGoods.setGoodsWeight(goodsInfo.getGoodsInfoWeight());
                    tempGoods.setGoodsCubage(goodsInfo.getGoodsInfoCubage());
                    tempGoods.setMarketPrice(goodsInfo.getMarketPrice());
                    tempGoods.setVipPrice(goodsInfo.getVipPrice());
                    if (tempGoods.getGoodsViewNum() == null) {
                        tempGoods.setGoodsViewNum(Long.valueOf(Constants.no));
                    }
                    tempGoods.setProviderId(Long.valueOf(Constants.no));
                    tempGoods.setProviderGoodsId(String.valueOf(Constants.no));
                    tempGoods.setLockStock(Integer.valueOf(0));
                    tempGoods.setGoodsImg(goodsInfo.getGoodsInfoImg());
                }
            });
            tempGoods.setWareId(Long.valueOf(Constants.yes));
            //重新生成
            tempGoods.setGoodsNo(goodsCommonService.getSpuNoByUnique());
            String goodsId = goodsRepository.save(tempGoods).getGoodsId();


            final int[] count = {1};
            goodsInfoList.forEach(goodsInfo -> {
                String erpNo = getErpNo(goods.getCateId(), goods.getStoreId(),count[0]);
                goodsInfo.setErpGoodsInfoNo(erpNo);
                goodsInfo.setGoodsId(goodsId);
                goodsInfo.setGoodsInfoName(goods.getGoodsName());
                goodsInfo.setCreateTime(goods.getCreateTime());
                goodsInfo.setUpdateTime(goods.getCreateTime());
                goodsInfo.setAddedTime(goods.getCreateTime());
                goodsInfo.setDelFlag(goods.getDelFlag());
                goodsInfo.setCompanyInfoId(goods.getCompanyInfoId());
                goodsInfo.setPriceType(goods.getPriceType());
                goodsInfo.setCustomFlag(goods.getCustomFlag());
                goodsInfo.setLevelDiscountFlag(goods.getLevelDiscountFlag());
                goodsInfo.setCateId(goods.getCateId());
                goodsInfo.setBrandId(goods.getBrandId());
                goodsInfo.setStoreId(goods.getStoreId());
                goodsInfo.setAuditStatus(goods.getAuditStatus());
                goodsInfo.setCompanyType(goods.getCompanyType());
                goodsInfo.setAloneFlag(Boolean.FALSE);
                goodsInfo.setDistributionGoodsAudit(DistributionGoodsAudit.COMMON_GOODS);
                goodsInfo.setSaleType(goods.getSaleType());
                goodsInfo.setWareId(Long.valueOf(Constants.yes));
                goodsInfo.setLockStock(BigDecimal.valueOf(0));
                if (goodsInfo.getSupplyPrice() == null) {
                    goodsInfo.setGoodsSubtitle(String.valueOf(Constants.no));
                }
                goodsInfo.setInquiryFlag(Integer.valueOf(Constants.no));
                if (null!=goodsInfo &&Objects.isNull(goodsInfo.getGoodsInfoUnit())){
                    //如果不设置sku 的商品单位
                    goodsInfo.setGoodsInfoUnit(goods.getGoodsUnit());
                }
                if (null!=goodsInfo &&Objects.isNull(goodsInfo.getGoodsInfoWeight())){
                    //如果不设置sku 的商品重量
                    goodsInfo.setGoodsInfoWeight(goods.getGoodsWeight());
                }
                if (null!=goodsInfo &&Objects.isNull(goodsInfo.getGoodsInfoCubage())){
                    //如果不设置sku 的商品体积
                    goodsInfo.setGoodsInfoCubage(goods.getGoodsCubage());
                }
                BigDecimal addStep = goodsInfo.getAddStep();//步长
                if (Objects.isNull(addStep)){
                    addStep=BigDecimal.ONE;
                }

                goodsInfo.setGoodsInfoType(0);
                goodsInfo.setSortNumKey(0);
                goodsInfo.setSortNumCate(0);
                goodsInfo.setSupplyPrice(BigDecimal.ZERO);
                goodsInfo.setSingleOrderPurchaseNum(Long.valueOf(0));
                goodsInfo.setMarketing(Long.valueOf(-1));
                goodsInfo.setPurchaseNum(Long.valueOf(-1));
                goodsInfo.setAddedFlag(goods.getAddedFlag());
                String skuId = goodsInfoRepository.save(KsBeanUtil.convert(goodsInfo, GoodsInfo.class))
                        .getGoodsInfoId();
                goodsInfo.setGoodsInfoId(skuId);
                newSkuIds.add(skuId);
                //如果是多规格,新增SKU与规格明细值的关联表
                List<GoodsAttributeKeyDTO> goodsAttributeKeys = goodsInfo.getGoodsAttributeKeys();
                if (CollectionUtils.isNotEmpty(goodsAttributeKeys)){
                    for (GoodsAttributeKeyDTO g:goodsAttributeKeys) {
                        GoodsAttributeKey goodsAttributeKey =new GoodsAttributeKey();
                        goodsAttributeKey.setAttributeId(g.getAttributeId());
                        goodsAttributeKey.setGoodsInfoId(skuId);
                        goodsAttributeKey.setGoodsId(goodsId);
                        goodsAttributeKey.setGoodsAttributeValue(g.getGoodsAttributeValue());
                        goodsAttributeKey.setGoodsAttributeId(g.getAttributeId());
                        goodsAttributeKeyService.add(goodsAttributeKey);
                    }
                }


                //开始更新库存
                GoodsWareStock goodsWareStock = new GoodsWareStock();
                goodsWareStock.setGoodsInfoId(skuId);
                goodsWareStock.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                goodsWareStock.setGoodsId(goodsInfo.getGoodsId());
                goodsWareStock.setStoreId(goodsInfo.getStoreId());
                goodsWareStock.setStock(goodsInfo.getStock());
                goodsWareStock.setCreateTime(LocalDateTime.now());
                goodsWareStock.setUpdateTime(LocalDateTime.now());
                goodsWareStock.setWareId(goodsInfo.getWareId());
                goodsWareStock.setDelFlag(DeleteFlag.NO);
                goodsWareStock.setGoodsInfoWareId(goodsWareStock.getGoodsInfoId()+"_"+goodsWareStock.getWareId());
                goodsWareStock.setAddStep(goodsInfo.getAddStep());//相对最小单位的换算率
                goodsWareStock.setSaleType(goodsInfo.getSaleType());//销售类别(0:批发,1:零售,2散批)
                goodsWareStock.setAddStep(goodsInfo.getAddStep());
                if(SaleType.WHOLESALE.toValue()==goodsWareStock.getSaleType()){
                    goodsWareStock.setMainAddStep(BigDecimal.ONE);
                }
                GoodsWareStock addStock = goodsWareStockService.add(goodsWareStock);
                if (Objects.nonNull(addStock)){
                    GoodsWareStockDetail goodsWareStockDetail=new GoodsWareStockDetail();
                    goodsWareStockDetail.setGoodsWareStockId(addStock.getId());
                    goodsWareStockDetail.setStockImportNo(new StringBuilder("IM").append(RandomStringUtils.randomNumeric(6)).toString());
                    goodsWareStockDetail.setGoodsInfoId(skuId);
                    goodsWareStockDetail.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
                    goodsWareStockDetail.setStock(goodsInfo.getStock().longValue());
                    goodsWareStockDetail.setWareId(Long.valueOf(Constants.yes));
                    goodsWareStockDetail.setImportType(GoodsWareStockImportType.EDIT);
                    goodsWareStockDetail.setOperateStock(goodsInfo.getStock().longValue());
                    goodsWareStockDetail.setDelFlag(DeleteFlag.NO);
                    //  goodsWareStockDetail.setCreatePerson();
                    goodsWareStockDetail.setCreateTime(LocalDateTime.now());
                    goodsWareStockDetailService.add(goodsWareStockDetail);
                }


                count[0]++;

            });

            goodsInfoList.forEach(goodsInfo -> {

                DevanningGoodsInfo convert = KsBeanUtil.convert(goodsInfo, DevanningGoodsInfo.class);
                convert.setDivisorFlag(BigDecimal.ONE);
                //1箱(30.00杯x0.00元/杯)
                BigDecimal subtitlePrice = goodsInfo.getMarketPrice().divide(goodsInfo.getAddStep(), 2, BigDecimal.ROUND_UP);
                String goodsSubtitle = "1"+goodsInfo.getGoodsInfoUnit()+"=" + goodsInfo.getAddStep() + goodsInfo.getDevanningUnit() + "x" + subtitlePrice + "元/" +  goodsInfo.getDevanningUnit();
                String goodsSubtitleNew = subtitlePrice + "/" + goodsInfo.getDevanningUnit();
                convert.setDevanningUnit(goodsInfo.getDevanningUnit());
                convert.setGoodsWeight(goodsInfo.getGoodsInfoWeight());
                convert.setGoodsCubage(goodsInfo.getGoodsInfoCubage());
                convert.setIsScatteredQuantitative(goodsInfo.getIsScatteredQuantitative()==null?0:goodsInfo.getIsScatteredQuantitative());
                convert.setSupplyPrice(BigDecimal.ZERO);
                convert.setVipPrice(goodsInfo.getVipPrice());
                convert.setProviderId(goods.getProviderId());
                convert.setGoodsInfoBatchNo(goodsInfo.getGoodsInfoBatchNo());
                convert.setSingleOrderPurchaseNum(goodsInfo.getSingleOrderPurchaseNum());
                convert.setPurchaseNum(goodsInfo.getPurchaseNum());
                convert.setWareId(goodsInfo.getWareId());
                convert.setHostSku(goodsInfo.getHostSku());
                convert.setAddStep(goodsInfo.getAddStep());
                convert.setGoodsInfoSubtitle(goodsSubtitle);
                convert.setGoodsSubtitleNew(goodsSubtitleNew);
                devanningGoodsInfoService.addImport(convert);
            });
            if (CollectionUtils.isNotEmpty(goods.getStoreCateIds())) {
                goods.getStoreCateIds().forEach(cateId -> {
                    StoreCateGoodsRela rela = new StoreCateGoodsRela();
                    rela.setGoodsId(goodsId);
                    rela.setStoreCateId(cateId);
                    storeCateGoodsRelaRepository.save(rela);
                });
            }

            //批量保存
            List<BatchGoodsImageDTO> imageUrls = images.get(goods.getGoodsNo());
            if (CollectionUtils.isNotEmpty(imageUrls)) {
                imageUrls.forEach(img -> {
                    img.setGoodsId(goodsId);
                    img.setCreateTime(goods.getCreateTime());
                    img.setUpdateTime(goods.getCreateTime());
                    img.setDelFlag(goods.getDelFlag());
                    goodsImageRepository.save(KsBeanUtil.convert(img, GoodsImage.class));
                });
            }
        });
        return newSkuIds;
    }
    /**
     *
     * 获取副标题信息
     * */
    private String getGoodsSubtitle(List<GoodsAttributeKeyDTO> goodsAttributeKeys) {

        StringBuilder builder=new StringBuilder();
        if (CollectionUtils.isNotEmpty(goodsAttributeKeys)){
            goodsAttributeKeys.forEach(goodsAttributeKey -> {
                builder.append(goodsAttributeKey.getGoodsAttributeValue()+" ");
            });
            return builder.toString().trim();
        }
        return null;
    }
    /**
     * 新增/编辑操作中，商品审核状态
     *
     * @param goods 商品
     */
    public void setCheckState(Goods goods) {
        //B2B模式直接审核通过
        if (osUtil.isB2b()) {
            goods.setAuditStatus(CheckStatus.CHECKED);
            goods.setSubmitTime(LocalDateTime.now());
            return;
        }
        //新增商品
        if (Objects.isNull(goods.getAuditStatus())) {
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            } else {
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            }
        } else if (Objects.equals(CheckStatus.NOT_PASS, goods.getAuditStatus())
                || Objects.equals(CheckStatus.FORBADE, goods.getAuditStatus())) {//审核未通过/禁售中商品
            //自营
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            } else {//第三方
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            }

        }
    }
    /**
     * 新增/编辑操作中，商品审核状态
     *
     * @param goods 商品
     */
    public void setStoreCheckState(Goods goods) {
        //B2B模式直接审核通过
        if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
            goods.setAuditStatus(CheckStatus.WAIT_CHECK);
        } else {
            goods.setAuditStatus(CheckStatus.CHECKED);
            goods.setSubmitTime(LocalDateTime.now());
        }
    }
    /**
     * 新增/编辑操作中，商品审核状态
     *
     * @param goods 商品
     */
    public void setCheckState(RetailGoods goods) {
        //B2B模式直接审核通过
        if (osUtil.isB2b()) {
            goods.setAuditStatus(CheckStatus.CHECKED);
            goods.setSubmitTime(LocalDateTime.now());
            return;
        }
        //新增商品
        if (Objects.isNull(goods.getAuditStatus())) {
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            } else {
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            }
        } else if (Objects.equals(CheckStatus.NOT_PASS, goods.getAuditStatus()) || Objects.equals(CheckStatus.FORBADE, goods.getAuditStatus())) {//审核未通过/禁售中商品
            //自营
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            } else {//第三方
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            }

        }
    }

    /**
     * 新增/编辑操作中，商品审核状态
     *
     * @param goods 商品
     */
    public void setCheckState(BulkGoods goods) {
        //B2B模式直接审核通过
        if (osUtil.isB2b()) {
            goods.setAuditStatus(CheckStatus.CHECKED);
            goods.setSubmitTime(LocalDateTime.now());
            return;
        }
        //新增商品
        if (Objects.isNull(goods.getAuditStatus())) {
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            } else {
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                    return;
                }
                goods.setAuditStatus(CheckStatus.CHECKED);
                goods.setSubmitTime(LocalDateTime.now());
            }
        } else if (Objects.equals(CheckStatus.NOT_PASS, goods.getAuditStatus()) || Objects.equals(CheckStatus.FORBADE, goods.getAuditStatus())) {//审核未通过/禁售中商品
            //自营
            if (Objects.equals(BoolFlag.NO, goods.getCompanyType())) {
                if (auditQueryProvider.isBossGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            } else {//第三方
                if (auditQueryProvider.isSupplierGoodsAudit().getContext().isAudit()) {
                    goods.setAuditStatus(CheckStatus.WAIT_CHECK);
                } else {
                    goods.setAuditStatus(CheckStatus.CHECKED);
                    goods.setSubmitTime(LocalDateTime.now());
                }
            }

        }
    }
    /**
     * 递归方式，获取全局唯一SPU编码
     * @return Spu编码
     */
    public String getSpuNoByUnique(){
        String spuNo = getSpuNo();
        GoodsQueryRequest queryRequest = new GoodsQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsNo(spuNo);
        if (goodsRepository.count(queryRequest.getWhereCriteria()) > 0) {
            return getSpuNoByUnique();
        }
        return spuNo;
    }

    /**
     * 获取Spu编码
     * @return Spu编码
     */
    public String getSpuNo() {
        return "P".concat(String.valueOf(System.currentTimeMillis()).substring(4, 10)).concat(RandomStringUtils.randomNumeric(3));
    }

    /**
     * 递归方式，获取全局唯一SPU编码
     * @return Sku编码
     */
    public String getSkuNoByUnique(){
        String skuNo = getSkuNo();
        GoodsInfoQueryRequest queryRequest = new GoodsInfoQueryRequest();
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setGoodsInfoNos(Collections.singletonList(skuNo));
        if (goodsInfoRepository.count(queryRequest.getWhereCriteria()) > 0) {
            return getSkuNoByUnique();
        }
        return skuNo;
    }
    /**
     *
     * 本地生产erp编码
     * 一级分类-二级分类-商品数量-店铺id
     * */

    private String getErpNo(long cateId,long storeId,int count){

        StringBuffer buffer=new StringBuffer();

        GoodsCate info = goodsCateService.findById(cateId);

        String[] catePath = info.getCatePath().split(Constants.CATE_PATH_SPLITTER);

        //添加两级分类
        buffer.append(catePath[Constants.yes]);
        buffer.append(Constants.STRING_SLASH_HENG);
        buffer.append(catePath[Constants.IMPORT_GOODS_MAX_SIZE]);
        buffer.append(Constants.STRING_SLASH_HENG);
        List<GoodsInfo> allList = goodsInfoRepository.findAll((GoodsInfoQueryRequest.builder().storeId(storeId).build().getWhereCriteria()));
        String newADD = String.valueOf(String.format("%0"+3+"d",allList.size()+count));
        buffer.append(newADD);
        buffer.append(Constants.STRING_SLASH_HENG);
        buffer.append(storeId);

        return buffer.toString();
    }
    /**
     * 获取Sku编码
     * @return Sku编码
     */
    public String getSkuNo() {
        return "8".concat(String.valueOf(System.currentTimeMillis()).substring(4, 10)).concat(RandomStringUtils.randomNumeric(3));
    }
}
