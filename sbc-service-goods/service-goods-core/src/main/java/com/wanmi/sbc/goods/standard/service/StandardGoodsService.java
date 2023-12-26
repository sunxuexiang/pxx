package com.wanmi.sbc.goods.standard.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.constant.StandardGoodsErrorCode;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.service.GoodsCateService;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.standard.model.root.StandardGoods;
import com.wanmi.sbc.goods.standard.model.root.StandardGoodsRel;
import com.wanmi.sbc.goods.standard.model.root.StandardPropDetailRel;
import com.wanmi.sbc.goods.standard.model.root.StandardSku;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardGoodsRepository;
import com.wanmi.sbc.goods.standard.repository.StandardPropDetailRelRepository;
import com.wanmi.sbc.goods.standard.repository.StandardSkuRepository;
import com.wanmi.sbc.goods.standard.request.StandardQueryRequest;
import com.wanmi.sbc.goods.standard.request.StandardSaveRequest;
import com.wanmi.sbc.goods.standard.request.StandardSkuQueryRequest;
import com.wanmi.sbc.goods.standard.response.StandardEditResponse;
import com.wanmi.sbc.goods.standard.response.StandardQueryResponse;
import com.wanmi.sbc.goods.standardimages.model.root.StandardImage;
import com.wanmi.sbc.goods.standardimages.repository.StandardImageRepository;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSkuSpecDetailRel;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSpec;
import com.wanmi.sbc.goods.standardspec.model.root.StandardSpecDetail;
import com.wanmi.sbc.goods.standardspec.repository.StandardSkuSpecDetailRelRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecDetailRepository;
import com.wanmi.sbc.goods.standardspec.repository.StandardSpecRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@Service
@Transactional(readOnly = true)
@Slf4j
public class StandardGoodsService {

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
    private GoodsBrandRepository goodsBrandRepository;

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private StandardPropDetailRelRepository standardPropDetailRelRepository;

    @Autowired
    private StandardGoodsRelRepository standardGoodsRelRepository;

    @Autowired
    private GoodsCateService goodsCateService;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    @Autowired
    private GoodsRepository goodsRepository;


    /**
     * 分页查询商品库
     *
     * @param request 参数
     * @return list
     */
    public StandardQueryResponse page(StandardQueryRequest request) {
        StandardQueryResponse response = new StandardQueryResponse();

        List<StandardSku> standardSkus = new ArrayList<>();
        List<StandardSkuSpecDetailRel> standardSkuSpecDetails = new ArrayList<>();
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        List<GoodsCate> goodsCateList = new ArrayList<>();

        //获取该分类的所有子分类
        if (request.getCateId() != null) {
            request.setCateIds(goodsCateService.getChlidCateId(request.getCateId()));
            if(CollectionUtils.isNotEmpty(request.getCateIds())) {
                request.getCateIds().add(request.getCateId());
                request.setCateId(null);
            }
        }
        //默认仅仅查询 1 长沙仓
        request.setWareId(Long.valueOf(Constants.yes));

        if (CollectionUtils.isNotEmpty(request.getErpNos())){
            List<String> stringList = request.getErpNos().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            request.setErpNos(stringList);
        }
        if (CollectionUtils.isNotEmpty(request.getFfskus())){
            List<String> stringList = request.getFfskus().stream().filter(string -> !string.isEmpty()).collect(Collectors.toList());
            request.setFfskus(stringList);
        }
        Page<StandardGoods> goodsPage = standardGoodsRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
        if (CollectionUtils.isEmpty(goodsPage.getContent()) &&  CollectionUtils.isNotEmpty(request.getFfskus())) {
            goodsPage= standardGoodsRepository.findAll(request.getStoreWhereCriteria(), request.getPageRequest());

        }

        if (CollectionUtils.isNotEmpty(goodsPage.getContent())) {
            List<String> goodsIds = goodsPage.getContent().stream().map(StandardGoods::getGoodsId).collect(Collectors.toList());
            //查询所有SKU
            StandardSkuQueryRequest skuQueryRequest = new StandardSkuQueryRequest();
            skuQueryRequest.setGoodsIds(goodsIds);
            skuQueryRequest.setErpNos(request.getErpNos());
            skuQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            standardSkus.addAll(standardSkuRepository.findAll(skuQueryRequest.getWhereCriteria()));
            //查询所有SKU规格值关联
            standardSkuSpecDetails.addAll(standardSkuSpecDetailRelRepository.findByGoodsIds(goodsIds));

            //填充每个SKU的规格关系
            Page<StandardGoods> finalGoodsPage = goodsPage;
            standardSkus.forEach(standardSku -> {
                //为空，则以商品库主图
                if (StringUtils.isBlank(standardSku.getGoodsInfoImg())) {
                    standardSku.setGoodsInfoImg(finalGoodsPage.getContent().stream().filter(goods -> goods.getGoodsId().equals(standardSku.getGoodsId())).findFirst().orElse(new StandardGoods()).getGoodsImg());
                }
                standardSku.setSpecDetailRelIds(standardSkuSpecDetails.stream().filter(specDetailRel -> specDetailRel.getGoodsInfoId().equals(standardSku.getGoodsInfoId())).map(StandardSkuSpecDetailRel::getSpecDetailRelId).collect(Collectors.toList()));
            });

            //填充每个SKU的SKU关系
            goodsPage.getContent().forEach(goods -> {
                goods.setGoodsInfoIds(standardSkus.stream().filter(standardSku -> standardSku.getGoodsId().equals(goods.getGoodsId())).map(StandardSku::getGoodsInfoId).collect(Collectors.toList()));
                //取SKU最小市场价
                goods.setMarketPrice(standardSkus.stream().filter(goodsInfo -> goods.getGoodsId().equals(goodsInfo.getGoodsId())).filter(goodsInfo -> Objects.nonNull(goodsInfo.getMarketPrice())).map(StandardSku::getMarketPrice).min(BigDecimal::compareTo).orElse(goods.getMarketPrice()));
            });

            //获取所有品牌
            GoodsBrandQueryRequest brandRequest = new GoodsBrandQueryRequest();
            brandRequest.setDelFlag(DeleteFlag.NO.toValue());
            brandRequest.setBrandIds(goodsPage.getContent().stream().filter(goods -> goods.getBrandId() != null).map(StandardGoods::getBrandId).distinct().collect(Collectors.toList()));
            goodsBrandList.addAll(goodsBrandRepository.findAll(brandRequest.getWhereCriteria()));

            //获取所有分类
            GoodsCateQueryRequest cateRequest = new GoodsCateQueryRequest();
            cateRequest.setCateIds(goodsPage.getContent().stream().filter(goods -> goods.getCateId() != null).map(StandardGoods::getCateId).distinct().collect(Collectors.toList()));
            goodsCateList.addAll(goodsCateRepository.findAll(cateRequest.getWhereCriteria()));
        }
        response.setStandardGoodsPage(goodsPage);
        response.setStandardSkuList(standardSkus);
        response.setStandardSkuSpecDetails(standardSkuSpecDetails);
        response.setGoodsBrandList(goodsBrandList);
        response.setGoodsCateList(goodsCateList);
        return response;
    }

    /**
     * 根据ID查询商品库
     *
     * @param goodsId 商品库ID
     * @return list
     */
    public StandardEditResponse findInfoById(String goodsId) throws SbcRuntimeException {
        StandardEditResponse response = new StandardEditResponse();
        StandardGoods goods = standardGoodsRepository.findById(goodsId).orElseThrow(() -> new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST));

        response.setGoods(goods);

        //查询商品库图片
        response.setImages(standardImageRepository.findByGoodsId(goods.getGoodsId()));

        //查询SKU列表
        StandardSkuQueryRequest infoQueryRequest = new StandardSkuQueryRequest();
        infoQueryRequest.setGoodsId(goods.getGoodsId());
        infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        List<StandardSku> standardSkus = standardSkuRepository.findAll(infoQueryRequest.getWhereCriteria());

        //商品库属性
        response.setGoodsPropDetailRels(standardPropDetailRelRepository.queryByGoodsId(goods.getGoodsId()));

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            response.setGoodsSpecs(standardSpecRepository.findByGoodsId(goods.getGoodsId()));
            response.setGoodsSpecDetails(standardSpecDetailRepository.findByGoodsId(goods.getGoodsId()));

            //对每个规格填充规格值关系
            response.getGoodsSpecs().forEach(standardSpec -> {
                standardSpec.setSpecDetailIds(response.getGoodsSpecDetails().stream().filter(specDetail -> specDetail.getSpecId().equals(standardSpec.getSpecId())).map(StandardSpecDetail::getSpecDetailId).collect(Collectors.toList()));
            });

            //对每个SKU填充规格和规格值关系
            Map<String, List<StandardSkuSpecDetailRel>> standardSkuSpecDetailRels = standardSkuSpecDetailRelRepository.findByGoodsId(goods.getGoodsId()).stream().collect(Collectors.groupingBy(StandardSkuSpecDetailRel::getGoodsInfoId));
            standardSkus.forEach(standardSku -> {
                standardSku.setMockSpecIds(standardSkuSpecDetailRels.getOrDefault(standardSku.getGoodsInfoId(), new ArrayList<>()).stream().map(StandardSkuSpecDetailRel::getSpecId).collect(Collectors.toList()));
                standardSku.setMockSpecDetailIds(standardSkuSpecDetailRels.getOrDefault(standardSku.getGoodsInfoId(), new ArrayList<>()).stream().map(StandardSkuSpecDetailRel::getSpecDetailId).collect(Collectors.toList()));
            });
        }
        response.setGoodsInfos(standardSkus);

        return response;
    }

    /**
     * 商品库新增
     *
     * @param saveRequest
     * @return SPU编号
     * @throws SbcRuntimeException
     */
    @Transactional
    public String add(StandardSaveRequest saveRequest) throws SbcRuntimeException {
        List<StandardImage> standardImages = saveRequest.getImages();
        List<StandardSku> standardSkus = saveRequest.getGoodsInfos();
        StandardGoods goods = saveRequest.getGoods();

        //验证商品库相关基础数据
        this.checkBasic(goods);
        goods.setDelFlag(DeleteFlag.NO);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(goods.getCreateTime());
        if (CollectionUtils.isNotEmpty(standardImages)) {
            goods.setGoodsImg(standardImages.get(0).getArtworkUrl());
        }
        if (goods.getMoreSpecFlag() == null) {
            goods.setMoreSpecFlag(Constants.no);
        }

        final String goodsId = standardGoodsRepository.save(goods).getGoodsId();

        //新增图片
        if (CollectionUtils.isNotEmpty(standardImages)) {
            standardImages.forEach(standardImage -> {
                standardImage.setCreateTime(goods.getCreateTime());
                standardImage.setUpdateTime(goods.getUpdateTime());
                standardImage.setGoodsId(goodsId);
                standardImage.setDelFlag(DeleteFlag.NO);
                standardImage.setImageId(standardImageRepository.save(standardImage).getImageId());
            });
        }

        //保存商品库属性
        List<StandardPropDetailRel> standardPropDetailRels = saveRequest.getGoodsPropDetailRels();
        //如果是修改则设置修改时间，如果是新增则设置创建时间，
        if(CollectionUtils.isNotEmpty(standardPropDetailRels)) {
            standardPropDetailRels.forEach(standardPropDetailRel -> {
                standardPropDetailRel.setDelFlag(DeleteFlag.NO);
                standardPropDetailRel.setCreateTime(LocalDateTime.now());
                standardPropDetailRel.setGoodsId(goodsId);
            });
            standardPropDetailRelRepository.saveAll(standardPropDetailRels);
        }

        List<StandardSpec> specs = saveRequest.getGoodsSpecs();
        List<StandardSpecDetail> specDetails = saveRequest.getGoodsSpecDetails();

        List<StandardSkuSpecDetailRel> specDetailRels = new ArrayList<>();

        //如果是多规格
        if (Constants.yes.equals(goods.getMoreSpecFlag())) {
            /**
             * 填放可用SKU相应的规格\规格值
             * （主要解决SKU可以前端删除，但相应规格值或规格仍然出现的情况）
             */
            Map<Long, Integer> isSpecEnable = new HashMap<>();
            Map<Long, Integer> isSpecDetailEnable = new HashMap<>();
            standardSkus.forEach(standardSku -> {
                standardSku.getMockSpecIds().forEach(specId -> {
                    isSpecEnable.put(specId, Constants.yes);
                });
                standardSku.getMockSpecDetailIds().forEach(detailId -> {
                    isSpecDetailEnable.put(detailId, Constants.yes);
                });
            });

            //新增规格
            specs.stream()
                    .filter(standardSpec -> Constants.yes.equals(isSpecEnable.get(standardSpec.getMockSpecId()))) //如果SKU有这个规格
                    .forEach(standardSpec -> {
                        standardSpec.setCreateTime(goods.getCreateTime());
                        standardSpec.setUpdateTime(goods.getUpdateTime());
                        standardSpec.setGoodsId(goodsId);
                        standardSpec.setDelFlag(DeleteFlag.NO);
                        standardSpec.setSpecId(standardSpecRepository.save(standardSpec).getSpecId());
                    });
            //新增规格值
            specDetails.stream()
                    .filter(standardSpecDetail -> Constants.yes.equals(isSpecDetailEnable.get(standardSpecDetail.getMockSpecDetailId()))) //如果SKU有这个规格值
                    .forEach(standardSpecDetail -> {
                        Optional<StandardSpec> specOpt = specs.stream().filter(standardSpec -> standardSpec.getMockSpecId().equals(standardSpecDetail.getMockSpecId())).findFirst();
                        if (specOpt.isPresent()) {
                            standardSpecDetail.setCreateTime(goods.getCreateTime());
                            standardSpecDetail.setUpdateTime(goods.getUpdateTime());
                            standardSpecDetail.setGoodsId(goodsId);
                            standardSpecDetail.setDelFlag(DeleteFlag.NO);
                            standardSpecDetail.setSpecId(specOpt.get().getSpecId());
                            standardSpecDetail.setSpecDetailId(standardSpecDetailRepository.save(standardSpecDetail).getSpecDetailId());
                        }
                    });
        }

        for (StandardSku sku : standardSkus) {
            sku.setGoodsId(goodsId);
            sku.setGoodsInfoName(goods.getGoodsName());
            sku.setCostPrice(goods.getCostPrice());
            sku.setCreateTime(goods.getCreateTime());
            sku.setUpdateTime(goods.getUpdateTime());
            sku.setDelFlag(goods.getDelFlag());
            String goodsInfoId = standardSkuRepository.save(sku).getGoodsInfoId();
            sku.setGoodsInfoId(goodsInfoId);

            //如果是多规格,新增SKU与规格明细值的关联表
            if (Constants.yes.equals(goods.getMoreSpecFlag())) {
                if (CollectionUtils.isNotEmpty(specs)) {
                    for (StandardSpec spec : specs) {
                        if (sku.getMockSpecIds().contains(spec.getMockSpecId())) {
                            for (StandardSpecDetail detail : specDetails) {
                                if (spec.getMockSpecId().equals(detail.getMockSpecId()) && sku.getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
                                    StandardSkuSpecDetailRel detailRel = new StandardSkuSpecDetailRel();
                                    detailRel.setGoodsId(goodsId);
                                    detailRel.setGoodsInfoId(goodsInfoId);
                                    detailRel.setSpecId(spec.getSpecId());
                                    detailRel.setSpecDetailId(detail.getSpecDetailId());
                                    detailRel.setDetailName(detail.getDetailName());
                                    detailRel.setCreateTime(detail.getCreateTime());
                                    detailRel.setUpdateTime(detail.getUpdateTime());
                                    detailRel.setDelFlag(detail.getDelFlag());
                                    specDetailRels.add(standardSkuSpecDetailRelRepository.save(detailRel));
                                }
                            }
                        }
                    }
                }
            }
        }
        return goodsId;
    }

    /**
     * 商品库更新
     *
     * @param saveRequest
     * @throws SbcRuntimeException
     */
    @Transactional
    public Map<String, Object> edit(StandardSaveRequest saveRequest) throws SbcRuntimeException {
        StandardGoods newGoods = saveRequest.getGoods();
        StandardGoods oldGoods = standardGoodsRepository.findById(newGoods.getGoodsId()).orElse(null);
        if (oldGoods == null || oldGoods.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(StandardGoodsErrorCode.NOT_EXIST);
        }
        //验证商品库相关基础数据
        this.checkBasic(newGoods);

        List<StandardImage> standardImages = saveRequest.getImages();
        if (CollectionUtils.isNotEmpty(standardImages)) {
            newGoods.setGoodsImg(standardImages.get(0).getArtworkUrl());
        } else {
            newGoods.setGoodsImg(null);
        }
        if (newGoods.getMoreSpecFlag() == null) {
            newGoods.setMoreSpecFlag(Constants.no);
        }

        LocalDateTime currDate = LocalDateTime.now();
        //更新商品库
        newGoods.setUpdateTime(currDate);
        KsBeanUtil.copyProperties(newGoods, oldGoods);
        standardGoodsRepository.save(oldGoods);

        //更新图片
        List<StandardImage> oldImages = standardImageRepository.findByGoodsId(newGoods.getGoodsId());
        if (CollectionUtils.isNotEmpty(oldImages)) {
            for (StandardImage oldImage : oldImages) {
                if (CollectionUtils.isNotEmpty(standardImages)) {
                    Optional<StandardImage> imageOpt = standardImages.stream()
                            .filter(standardImage -> oldImage.getImageId().equals(standardImage.getImageId()))
                            .findFirst();
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
                standardImageRepository.saveAll(oldImages);
            }
        }

        //新增图片
        if (CollectionUtils.isNotEmpty(standardImages)) {
            standardImages.stream().filter(standardImage -> standardImage.getImageId() == null).forEach(standardImage -> {
                standardImage.setCreateTime(currDate);
                standardImage.setUpdateTime(currDate);
                standardImage.setGoodsId(newGoods.getGoodsId());
                standardImage.setDelFlag(DeleteFlag.NO);
                standardImageRepository.save(standardImage);
            });
        }

        //保存商品库属性
        List<StandardPropDetailRel> goodsPropDetailRels = saveRequest.getGoodsPropDetailRels();
        //修改设置修改时间
        if (CollectionUtils.isNotEmpty(goodsPropDetailRels)) {
            //修改设置修改时间
            goodsPropDetailRels.forEach(goodsPropDetailRel -> {
                goodsPropDetailRel.setDelFlag(DeleteFlag.NO);
                if (goodsPropDetailRel.getRelId() != null) {
                    goodsPropDetailRel.setUpdateTime(LocalDateTime.now());
                }
            });
            //  先获取商品下所有的属性id，与前端传来的对比，id存在的做更新操作反之做保存操作
            List<StandardPropDetailRel> oldPropList = standardPropDetailRelRepository.queryByGoodsId(newGoods.getGoodsId());
            List<StandardPropDetailRel> insertList = new ArrayList<>();
            if (oldPropList.isEmpty()) {
                standardPropDetailRelRepository.saveAll(goodsPropDetailRels);
            } else {
                oldPropList.forEach(value -> {
                    goodsPropDetailRels.forEach(goodsProp -> {
                        if (value.getPropId().equals(goodsProp.getPropId())) {
                            standardPropDetailRelRepository.updateByGoodsIdAndPropId(goodsProp.getDetailId(), goodsProp.getGoodsId(), goodsProp.getPropId());
                        } else {
                            goodsProp.setCreateTime(LocalDateTime.now());
                            insertList.add(goodsProp);
                        }
                    });
                });
                standardPropDetailRelRepository.saveAll(insertList);
            }
        }
        List<StandardSpec> specs = saveRequest.getGoodsSpecs();
        List<StandardSpecDetail> specDetails = saveRequest.getGoodsSpecDetails();

        //如果是多规格
        if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {

            /**
             * 填放可用SKU相应的规格\规格值
             * （主要解决SKU可以前端删除，但相应规格值或规格仍然出现的情况）
             */
            Map<Long, Integer> isSpecEnable = new HashMap<>();
            Map<Long, Integer> isSpecDetailEnable = new HashMap<>();
            saveRequest.getGoodsInfos().forEach(standardSku -> {
                standardSku.getMockSpecIds().forEach(specId -> {
                    isSpecEnable.put(specId, Constants.yes);
                });
                standardSku.getMockSpecDetailIds().forEach(detailId -> {
                    isSpecDetailEnable.put(detailId, Constants.yes);
                });
            });

            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //更新规格
                List<StandardSpec> standardSpecs = standardSpecRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(standardSpecs)) {
                    for (StandardSpec oldSpec : standardSpecs) {
                        if (CollectionUtils.isNotEmpty(specs)) {
                            Optional<StandardSpec> specOpt = specs.stream().filter(spec -> oldSpec.getSpecId().equals(spec.getSpecId())).findFirst();
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
                        standardSpecRepository.save(oldSpec);
                    }
                }

                //更新规格值
                List<StandardSpecDetail> standardSpecDetails = standardSpecDetailRepository.findByGoodsId(oldGoods.getGoodsId());
                if (CollectionUtils.isNotEmpty(standardSpecDetails)) {

                    for (StandardSpecDetail oldSpecDetail : standardSpecDetails) {
                        if (CollectionUtils.isNotEmpty(specDetails)) {
                            Optional<StandardSpecDetail> specDetailOpt = specDetails.stream().filter(specDetail -> oldSpecDetail.getSpecDetailId().equals(specDetail.getSpecDetailId())).findFirst();
                            //如果规格值存在且SKU有这个规格值，更新
                            if (specDetailOpt.isPresent() && Constants.yes.equals(isSpecDetailEnable.get(specDetailOpt.get().getMockSpecDetailId()))) {
                                KsBeanUtil.copyProperties(specDetailOpt.get(), oldSpecDetail);

                                //更新SKU规格值表的名称备注
                                standardSkuSpecDetailRelRepository.updateNameBySpecDetail(specDetailOpt.get().getDetailName(), oldSpecDetail.getSpecDetailId(), oldGoods.getGoodsId());
                            } else {
                                oldSpecDetail.setDelFlag(DeleteFlag.YES);
                            }
                        } else {
                            oldSpecDetail.setDelFlag(DeleteFlag.YES);
                        }
                        oldSpecDetail.setUpdateTime(currDate);
                        standardSpecDetailRepository.save(oldSpecDetail);
                    }
                }
            }

            //新增规格
            if (CollectionUtils.isNotEmpty(specs)) {
                specs.stream().filter(standardSpec -> standardSpec.getSpecId() == null && Constants.yes.equals(isSpecEnable.get(standardSpec.getMockSpecId()))).forEach(standardSpec -> {
                    standardSpec.setCreateTime(currDate);
                    standardSpec.setUpdateTime(currDate);
                    standardSpec.setGoodsId(newGoods.getGoodsId());
                    standardSpec.setDelFlag(DeleteFlag.NO);
                    standardSpec.setSpecId(standardSpecRepository.save(standardSpec).getSpecId());
                });
            }
            //新增规格值
            if (CollectionUtils.isNotEmpty(specDetails)) {
                specDetails.stream().filter(standardSpecDetail -> standardSpecDetail.getSpecDetailId() == null && Constants.yes.equals(isSpecDetailEnable.get(standardSpecDetail.getMockSpecDetailId()))).forEach(standardSpecDetail -> {
                    Optional<StandardSpec> specOpt = specs.stream().filter(standardSpec -> standardSpec.getMockSpecId().equals(standardSpecDetail.getMockSpecId())).findFirst();
                    if (specOpt.isPresent()) {
                        standardSpecDetail.setCreateTime(currDate);
                        standardSpecDetail.setUpdateTime(currDate);
                        standardSpecDetail.setGoodsId(newGoods.getGoodsId());
                        standardSpecDetail.setDelFlag(DeleteFlag.NO);
                        standardSpecDetail.setSpecId(specOpt.get().getSpecId());
                        standardSpecDetail.setSpecDetailId(standardSpecDetailRepository.save(standardSpecDetail).getSpecDetailId());
                    }
                });
            }
        } else {//修改为单规格
            //如果老数据为多规格
            if (Constants.yes.equals(oldGoods.getMoreSpecFlag())) {
                //删除规格
                standardSpecRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除规格值
                standardSpecDetailRepository.deleteByGoodsId(newGoods.getGoodsId());

                //删除商品库规格值
                standardSkuSpecDetailRelRepository.deleteByGoodsId(newGoods.getGoodsId());
            }
        }

        //只存储新增的SKU数据，用于当修改价格及订货量设置为否时，只为新SKU增加相关的价格数据
        List<StandardSku> newStandardSku = new ArrayList<>();//需要被添加的sku信息

        //更新原有的SKU列表
        List<StandardSku> standardSkus = saveRequest.getGoodsInfos();
        List<StandardSku> oldStandardSkus = new ArrayList<>();//需要被更新的sku信息
        List<String> delInfoIds = new ArrayList<>();//需要被删除的sku信息
        if (CollectionUtils.isNotEmpty(standardSkus)) {
            StandardSkuQueryRequest infoQueryRequest = new StandardSkuQueryRequest();
            infoQueryRequest.setGoodsId(newGoods.getGoodsId());
            infoQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
            List<StandardSku> oldInfos = standardSkuRepository.findAll(infoQueryRequest.getWhereCriteria());

            if (CollectionUtils.isNotEmpty(oldInfos)) {
                for (StandardSku oldInfo : oldInfos) {
                    Optional<StandardSku> infoOpt = standardSkus.stream().filter(standardSku -> oldInfo.getGoodsInfoId().equals(standardSku.getGoodsInfoId())).findFirst();
                    //如果SKU存在，更新
                    if (infoOpt.isPresent()) {
                        infoOpt.get().setCostPrice(newGoods.getCostPrice());
                        KsBeanUtil.copyProperties(infoOpt.get(), oldInfo);
                        oldStandardSkus.add(oldInfo);//修改前后都存在的数据--加入需要被更新的sku中
                    } else {
                        oldInfo.setDelFlag(DeleteFlag.YES);
                        delInfoIds.add(oldInfo.getGoodsInfoId());//修改后不存在的数据--加入需要被删除的sku中
                    }
                    oldInfo.setGoodsInfoName(newGoods.getGoodsName());
                    oldInfo.setUpdateTime(currDate);
                    standardSkuRepository.save(oldInfo);
                }

                //删除SKU相关的规格关联表
                if (!delInfoIds.isEmpty()) {
                    standardSkuSpecDetailRelRepository.deleteByGoodsInfoIds(delInfoIds, newGoods.getGoodsId());
                }
            }

            //只保存新SKU
            for (StandardSku sku : standardSkus) {
                sku.setGoodsId(newGoods.getGoodsId());
                sku.setGoodsInfoName(newGoods.getGoodsName());
                sku.setCreateTime(currDate);
                sku.setUpdateTime(currDate);
                sku.setDelFlag(DeleteFlag.NO);
                //只处理新增的SKU
                if (sku.getGoodsInfoId() != null) {
                    continue;
                }
                sku.setCostPrice(oldGoods.getCostPrice());
                String standardSkuId = standardSkuRepository.save(sku).getGoodsInfoId();
                sku.setGoodsInfoId(standardSkuId);

                //如果是多规格,新增SKU与规格明细值的关联表
                if (Constants.yes.equals(newGoods.getMoreSpecFlag())) {
                    if (CollectionUtils.isNotEmpty(specs)) {
                        for (StandardSpec spec : specs) {
                            if (sku.getMockSpecIds().contains(spec.getMockSpecId())) {
                                for (StandardSpecDetail detail : specDetails) {
                                    if (spec.getMockSpecId().equals(detail.getMockSpecId()) && sku.getMockSpecDetailIds().contains(detail.getMockSpecDetailId())) {
                                        StandardSkuSpecDetailRel detailRel = new StandardSkuSpecDetailRel();
                                        detailRel.setGoodsId(newGoods.getGoodsId());
                                        detailRel.setGoodsInfoId(standardSkuId);
                                        detailRel.setSpecId(spec.getSpecId());
                                        detailRel.setSpecDetailId(detail.getSpecDetailId());
                                        detailRel.setDetailName(detail.getDetailName());
                                        detailRel.setCreateTime(currDate);
                                        detailRel.setUpdateTime(currDate);
                                        detailRel.setDelFlag(DeleteFlag.NO);
                                        standardSkuSpecDetailRelRepository.save(detailRel);
                                    }
                                }
                            }
                        }
                    }
                }
                newStandardSku.add(sku);//修改后才存在(新出现)的数据--加入需要被添加的sku中
            }
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("newStandardSku", newStandardSku);
        returnMap.put("delInfoIds", delInfoIds);
        returnMap.put("oldStandardSkus", oldStandardSkus);
        return returnMap;
    }

    /**
     * 商品库删除
     *
     * @param goodsIds 多个商品库
     * @throws SbcRuntimeException
     */
    @Transactional
    public void delete(List<String> goodsIds) throws SbcRuntimeException {

        if(standardGoodsRelRepository.countByStandardIds(goodsIds)>0){
            throw new SbcRuntimeException(StandardGoodsErrorCode.COMPANY_USED);
        }
        standardGoodsRelRepository.deleteByStandardIds(goodsIds);
        standardGoodsRepository.deleteByGoodsIds(goodsIds);
        standardSkuRepository.deleteByGoodsIds(goodsIds);
        standardPropDetailRelRepository.deleteByGoodsIds(goodsIds);
        standardSpecRepository.deleteByGoodsIds(goodsIds);
        standardSpecDetailRepository.deleteByGoodsIds(goodsIds);
        standardSkuSpecDetailRelRepository.deleteByGoodsIds(goodsIds);
    }


        /**
         * 供货商品库删除
         *
         * @param goodsIds 多个商品库
         * @throws SbcRuntimeException
         */
        @Transactional
        public void deleteProvider(List<String> goodsIds)  throws SbcRuntimeException {

            // 相关商品下架
            List<StandardGoodsRel> standardGoodsRels = standardGoodsRelRepository.findByStandardIds(goodsIds);
            List<String> standardGoodsIds = standardGoodsRels.stream().map(StandardGoodsRel::getGoodsId).collect(Collectors.toList());

            //删除供应商商品库商品

            goodsRepository.updateAddedFlagByGoodsIds(AddedFlag.NO.toValue(),standardGoodsIds);
            goodsInfoRepository.updateAddedFlagByGoodsIds(AddedFlag.NO.toValue(),standardGoodsIds);

            standardGoodsRelRepository.deleteByStandardIds(goodsIds);
            standardGoodsRepository.deleteByGoodsIds(goodsIds);
            standardSkuRepository.deleteByGoodsIds(goodsIds);
            standardPropDetailRelRepository.deleteByGoodsIds(goodsIds);
            standardSpecRepository.deleteByGoodsIds(goodsIds);
            standardSpecDetailRepository.deleteByGoodsIds(goodsIds);
            standardSkuSpecDetailRelRepository.deleteByGoodsIds(goodsIds);


        }

    /**
     * 列出已被导入的商品库ID
     * @param standardIds 商品库Id
     */
    public List<String> getUsedStandard(List<String> standardIds, List<Long> storeIds){
        return  standardGoodsRelRepository.findByStandardIds(standardIds, storeIds).stream().map(StandardGoodsRel::getStandardId).distinct().collect(Collectors.toList());
    }

    /**
     * 列出已被导入的商品ID
     * @param goodsIds 商品Id（非商品库）
     */
    public List<String> getUsedGoods(List<String> goodsIds){
//        return standardGoodsRepository.findByGoodsIds(goodsIds).stream().map(StandardGoods::getGoodsId).distinct().collect(Collectors.toList());
        return  standardGoodsRelRepository.findByGoodsIds(goodsIds).stream().map(StandardGoodsRel::getGoodsId).distinct().collect(Collectors.toList());
    }

    /**
     * 检测商品库公共基础类
     * 如分类、品牌、店铺分类
     *
     * @param goods 商品库信息
     */
    private void checkBasic(StandardGoods goods) {
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
    }


    /**
     * 列出已被导入的SKUID
     * @param standardIds 商品库Id
     */
    public List<String> getUsedGoodsId(List<String> standardIds, List<Long> storeIds){
        return  standardGoodsRelRepository.findByStandardIds(
                standardIds, storeIds).stream().map(StandardGoodsRel::getGoodsId).distinct().collect(Collectors.toList());
    }


    /**
     * 分页查询商品库
     *
     * @param request 参数
     * @return list
     */
    public List<String> queryGoodsIds(StandardQueryRequest request) {
        return standardGoodsRepository.findIdByList(request.getCateIds(), request.getPageable());
    }

    public StandardSku findByErpNo(String erpNo){
        return standardSkuRepository.findFirstByErpGoodsInfoNo(erpNo);

    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCateIdByGoodsId(Long cateId, String goodsId){
        standardGoodsRepository.updateCateIdByGoodsId(cateId,goodsId);
    }

    /**
     *
     * @param goodsIds 商品Id（非商品库）
     */
    public Map<String,String> getStandardGoodsIds(List<String> goodsIds){
        List<StandardGoodsRel> byGoodsIds = standardGoodsRelRepository.findByGoodsIds(goodsIds);
        Map<String,String> goodsIdsMap=new HashMap<>(500);
        byGoodsIds.forEach(param->{
            goodsIdsMap.put(param.getGoodsId(), param.getStandardId());
        });
        return  goodsIdsMap;
    }

}
