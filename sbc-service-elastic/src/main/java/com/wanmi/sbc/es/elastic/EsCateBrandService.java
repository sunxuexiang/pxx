package com.wanmi.sbc.es.elastic;

import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.model.root.GoodsBrandNest;
import com.wanmi.sbc.es.elastic.model.root.GoodsCateNest;
import com.wanmi.sbc.es.elastic.request.EsCateBrandQueryRequest;
import com.wanmi.sbc.es.elastic.request.EsCateDeleteRequest;
import com.wanmi.sbc.goods.api.constant.GoodsCateErrorCode;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateListByConditionResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * ES商品信息数据源操作
 * Created by daiyitian on 2017/4/21.
 */
@Service
public class EsCateBrandService {

    @Autowired
    private EsCateBrandRepository esCateBrandRepository;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    /**
     * ES_cate_brand定义结构
     *
     * @param goodsCate  商品分类
     * @param goodsBrand 商品品牌
     * @return
     */
    public EsCateBrand putEsCateBrand(GoodsCateVO goodsCate, GoodsBrandVO goodsBrand) {
        GoodsCateNest cate = new GoodsCateNest();
        cate.setCateId(0L);
        if (goodsCate != null) {
            KsBeanUtil.copyPropertiesThird(goodsCate, cate);
        }

        GoodsBrandNest brand = new GoodsBrandNest();
        brand.setBrandId(0L);
        if (goodsBrand != null) {
            KsBeanUtil.copyPropertiesThird(goodsBrand, brand);
        }
        EsCateBrand esCateBrand = new EsCateBrand();
        esCateBrand.setId(String.valueOf(cate.getCateId()).concat("_").concat(String.valueOf(brand.getBrandId())));
//        if(brand.getDelFlag() == null || DeleteFlag.YES.toValue() == brand.getDelFlag().toValue()){
//            brand.setBrandName(StringUtils.EMPTY);
//        }
//        if(cate.getDelFlag() == null || DeleteFlag.YES.toValue() == cate.getDelFlag().toValue()){
//            cate.setCateName(StringUtils.EMPTY);
//        }
        esCateBrand.setGoodsCate(cate);
        esCateBrand.setGoodsBrand(brand);
        return esCateBrand;
    }

    /**
     * 保存
     *
     * @param goodsCate
     * @param goodsBrand
     */
    public void save(GoodsCateVO goodsCate, GoodsBrandVO goodsBrand) {
        esCateBrandRepository.save(this.putEsCateBrand(goodsCate, goodsBrand));
    }

    /**
     * 单批保存
     *
     * @param cateBrand
     */
    public void save(EsCateBrand cateBrand) {
        esCateBrandRepository.save(cateBrand);
    }

    /**
     * 批量保存
     *
     * @param cateBrands
     */
    public void save(List<EsCateBrand> cateBrands) {
        esCateBrandRepository.saveAll(cateBrands);
    }

    /**
     * 批量删除
     *
     * @param cateBrands
     */
    public void delete(List<EsCateBrand> cateBrands) {
        esCateBrandRepository.deleteAll(cateBrands);
    }

    /**
     * 根据分类ID获取
     *
     * @param cateIds 多个分类
     * @return
     */
    public Iterable<EsCateBrand> queryCateBrandByCateIds(List<Long> cateIds) {
        return esCateBrandRepository.search(EsCateBrandQueryRequest.builder().goodsCateIds(cateIds).build().getWhereCriteria());
    }

    /**
     * 根据品牌ID获取
     *
     * @param brandIds 多个品牌
     * @return
     */
    public Iterable<EsCateBrand> queryCateBrandByBrandIds(List<Long> brandIds) {
        return esCateBrandRepository.search(EsCateBrandQueryRequest.builder().goodsBrandIds(brandIds).build().getWhereCriteria());
    }

    /**
     * 商品分类同步至es
     *
     * @param updateToEs
     */
    public void updateToEs(List<GoodsCateVO> updateToEs) {
//        Map<Long, GoodsCateNest> goodsCateMap = updateToEs.stream()
//                .map(cate -> KsBeanUtil.convert(cate, GoodsCateNest.class))
//                .collect(Collectors.toMap(GoodsCateNest::getCateId, goodsCate -> goodsCate));
//        Iterable<EsCateBrand> esCateBrands = this.queryCateBrandByCateIds(updateToEs.stream().map
//        (GoodsCateVO::getCateId).collect(Collectors.toList()));
//        Iterable<EsCateBrand> esCateBrands = this.queryCateBrandByCateIds(updateToEs.stream().map
//        (GoodsCateVO::getCateId).collect(Collectors.toList()));

        if (CollectionUtils.isNotEmpty(updateToEs)) {
            esGoodsInfoElasticService.updateCateName(updateToEs.get(0));
            esRetailGoodsInfoElasticService.updateCateName(updateToEs.get(0));
        }
//        List<EsCateBrand> cateBrandList = new ArrayList<>();
//        if (nonNull(esCateBrands)) {
//            esCateBrands.forEach(cateBrand -> {
//                if (nonNull(cateBrand.getGoodsCate())) {
//                    cateBrand.setGoodsCate(goodsCateMap.get(cateBrand.getGoodsCate().getCateId()));
//                    cateBrandList.add(cateBrand);
//                }
//            });
//        }
//        if (CollectionUtils.isNotEmpty(cateBrandList)) {
//            this.save(cateBrandList);
//        }
    }

    /**
     * es模块-创建时迁移
     *
     * @param delRequest
     */
    public void deleteCateFromEs(EsCateDeleteRequest delRequest) {
        GoodsCateNest goodsCateNest;
        //查询默认分类
        if (delRequest.isDefault()) {
            //查询默认分类
            GoodsCateListByConditionRequest request = new GoodsCateListByConditionRequest();
            request.setIsDefault(DefaultFlag.YES.toValue());
            List<GoodsCateVO> goodsCateList = null;
            GoodsCateListByConditionResponse goodsCateListByConditionResponse =
                    goodsCateQueryProvider.listByCondition(request).getContext();
            if (Objects.nonNull(goodsCateListByConditionResponse)) {
                goodsCateList = goodsCateListByConditionResponse.getGoodsCateVOList();
            }
            //如果默认分类不存在，不允许删除
            if (CollectionUtils.isEmpty(goodsCateList)) {
                throw new SbcRuntimeException(GoodsCateErrorCode.DEFAULT_CATE_NOT_EXIST);
            } else {
                goodsCateNest = KsBeanUtil.convert(goodsCateList.get(0), GoodsCateNest.class);
            }
        } else {
            if (delRequest.getInsteadCate() != null) {//添加删除之后的替换分类
                goodsCateNest = KsBeanUtil.convert(delRequest.getInsteadCate(), GoodsCateNest.class);
            } else {
                goodsCateNest = new GoodsCateNest();
            }
        }

        Iterable<EsCateBrand> esCateBrands = this.queryCateBrandByCateIds(delRequest.getDeleteIds());
        List<EsCateBrand> cateBrandList = new ArrayList<>();
        esCateBrands.forEach(cateBrand -> {
            cateBrand.setGoodsCate(goodsCateNest);
            cateBrandList.add(cateBrand);
        });
        if (CollectionUtils.isNotEmpty(cateBrandList)) {
            this.save(cateBrandList);
        }

    }

    /**
     * es模块-创建时迁移
     *
     * @param isDelete   是否是删除品牌，false时表示更新品牌
     * @param goodsBrand 操作品牌实体
     */
    public void updateBrandFromEs(boolean isDelete, GoodsBrandVO goodsBrand) {
//        Iterable<EsCateBrand> esCateBrands =
//                this.queryCateBrandByBrandIds(Collections.singletonList(goodsBrand.getBrandId()));
//        List<EsCateBrand> cateBrandList = new ArrayList<>();
//
//        esCateBrands.forEach(cateBrand -> {
//            if (isDelete) {
//                cateBrand.getGoodsBrand().setBrandName(StringUtils.EMPTY);
//            } else {
//                cateBrand.setGoodsBrand(KsBeanUtil.convert(goodsBrand, GoodsBrandNest.class));
//            }
//            cateBrandList.add(cateBrand);
//        });
//        if (CollectionUtils.isNotEmpty(cateBrandList)) {
//            this.save(cateBrandList);
//        }
        if (Objects.nonNull(goodsBrand)) {
            if (isDelete) {
                goodsBrand = new GoodsBrandVO();
            }
            esGoodsInfoElasticService.updateBrandName(goodsBrand);
            esRetailGoodsInfoElasticService.updateBrandName(goodsBrand);
        }
    }

    /**
     * 批量更新es
     * @param goodsBrandVOS
     */
    public void updateBrandsFromEs(List<GoodsBrandVO> goodsBrandVOS) {
        goodsBrandVOS.forEach(goodsBrandVO -> this.updateBrandFromEs(false, goodsBrandVO));
    }
}
