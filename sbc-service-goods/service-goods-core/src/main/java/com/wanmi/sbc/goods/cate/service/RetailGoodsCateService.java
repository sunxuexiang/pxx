package com.wanmi.sbc.goods.cate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.cate.model.root.GoodsCate;
import com.wanmi.sbc.goods.cate.model.root.RetailGoodsCate;
import com.wanmi.sbc.goods.cate.repository.GoodsCateRepository;
import com.wanmi.sbc.goods.cate.repository.RetailGoodsCateRepository;
import com.wanmi.sbc.goods.cate.request.GoodsCateQueryRequest;
import com.wanmi.sbc.goods.cate.request.GoodsCateSortRequest;
import com.wanmi.sbc.goods.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @description: 散批分类service
 * @author: XinJiang
 * @time: 2022/5/5 15:18
 */
@Service
public class RetailGoodsCateService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsCateRepository goodsCateRepository;

    @Autowired
    private RetailGoodsCateRepository retailGoodsCateRepository;

    /**
     * 根据ID批量查询商品分类
     *
     * @param cateIds 多个分类ID
     * @return list
     */
    public List<GoodsCate> findByIds(List<Long> cateIds) {
        return goodsCateRepository.findAll(GoodsCateQueryRequest.builder().cateIds(cateIds).build().getWhereCriteria());
    }

    /**
     * 条件查询散批商品分类
     *
     * @param request 参数
     * @return list
     */
    public List<RetailGoodsCate> queryRetailGoodsCate(GoodsCateQueryRequest request) {
        Sort sort = request.getSort();
        List<RetailGoodsCate> list;
        if (Objects.nonNull(sort)) {
            list = retailGoodsCateRepository.findAll(request.getWhereCriteriaForRetailGoodsCate(), sort);
        } else {
            list = retailGoodsCateRepository.findAll(request.getWhereCriteriaForRetailGoodsCate());
        }
        return ListUtils.emptyIfNull(list);
    }

    /**
     * 新增散批商品分类
     * @param cateIds
     * @param cateGrade
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRetailGoodsCate(List<Long> cateIds,Integer cateGrade) {
        List<GoodsCate> goodsCates = findByIds(cateIds);
        List<RetailGoodsCate> getRetailGoodsCates = this.checkRetailRecommendCate(goodsCates,cateGrade);
        List<RetailGoodsCate> retailGoodsCates = KsBeanUtil.convert(goodsCates,RetailGoodsCate.class);
        Integer maxSort = 0;
        if (CollectionUtils.isNotEmpty(getRetailGoodsCates)) {
            maxSort = getRetailGoodsCates.stream().mapToInt(r -> r.getSort()).max().getAsInt();
        }
        for(RetailGoodsCate retailGoodsCate : retailGoodsCates) {
            retailGoodsCate.setSort(maxSort++);
        }

        retailGoodsCateRepository.saveAll(retailGoodsCates);
        //生成缓存
        this.fillRetailGoodsCateRedis(cateGrade);
    }

    /**
     * 校验散批推荐分类
     * @param goodsCates
     */
    private List<RetailGoodsCate> checkRetailRecommendCate(List<GoodsCate> goodsCates,Integer cateGrade) {
        List<RetailGoodsCate> retailGoodsCates = this.queryRetailGoodsCate(GoodsCateQueryRequest
                .builder().delFlag(DeleteFlag.NO.toValue()).cateGrade(cateGrade).build());
        if (CollectionUtils.isNotEmpty(retailGoodsCates)) {
            int size = goodsCates.size();
            //推荐分类设置不能超过10个校验
            if (CollectionUtils.isNotEmpty(retailGoodsCates)) {
                size = size+retailGoodsCates.size();
                if (size > 10) {
                    throw new SbcRuntimeException(GoodsErrorCode.GOODS_CATE_GRADE_RECOMMEND);
                }
            }

            //推荐分类选中是否三级商品分类校验
            if (cateGrade == 3) {
                goodsCates.forEach(goodsCate -> {
                    if (3 != goodsCate.getCateGrade()) {
                        throw new SbcRuntimeException(GoodsErrorCode.GOODS_CATE_GRADE_ERROR);
                    }
                });
            }
            if (cateGrade == 2) {
                goodsCates.forEach(goodsCate -> {
                    if (2 != goodsCate.getCateGrade()) {
                        throw new SbcRuntimeException(GoodsErrorCode.GOODS_CATE_GRADE_ERROR,"所选分类包含不属于二级分类");
                    }
                });
            }

            //推荐分类重复添加校验
            List<Long> cateIds = goodsCates.stream().map(GoodsCate::getCateId).collect(Collectors.toList())
                    .stream().filter(item -> retailGoodsCates.stream().map(RetailGoodsCate::getCateId)
                            .collect(Collectors.toList()).contains(item))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(cateIds)) {
                throw new SbcRuntimeException(cateIds,GoodsErrorCode.GOODS_CATE_RECOMMEND_REPEAT);
            }
        }
        return retailGoodsCates;
    }

    /**
     * 删除散批商品分类
     * @param cateIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void delRetailGoodsCateRecommend(List<Long> cateIds,Integer cateGrade) {
        cateIds.forEach(cateId -> retailGoodsCateRepository.deleteById(cateId));
        //生成缓存
        this.fillRetailGoodsCateRedis(cateGrade);
    }

    /**
     * 散批推荐分类拖拽排序
     * @param goodsCateList
     */
    @Transactional(rollbackFor = Exception.class)
    public void retailGoodsCateDragSort(List<GoodsCateSortRequest> goodsCateList,Integer cateGrade) {
        if (CollectionUtils.isEmpty(goodsCateList)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (CollectionUtils.isNotEmpty(goodsCateList) && goodsCateList.size() > Constants.GOODSCATE_MAX_SIZE) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsCateList.forEach(cate ->
                retailGoodsCateRepository.updateCateSort(cate.getCateId(),
                        cate.getCateSort()));
        //生成缓存
        this.fillRetailGoodsCateRedis(cateGrade);
    }

    /**
     * 生成redis缓存
     * @param cateGrade
     */
    public void fillRetailGoodsCateRedis(Integer cateGrade) {
        GoodsCateQueryRequest request = new GoodsCateQueryRequest();
        request.setCateGrade(cateGrade);
        request.setDelFlag(DeleteFlag.NO.toValue());
        request.putSort("sort", SortType.ASC.toValue());
        List<RetailGoodsCate> retailGoodsCates = this.queryRetailGoodsCate(request);
        redisService.setString(CacheKeyConstant.RETAIL_GOODS_CATE_RECOMMEND.concat(String.valueOf(cateGrade)), JSON.toJSONString(retailGoodsCates,
                SerializerFeature.DisableCircularReferenceDetect));
    }

    /**
     * 获取推荐商品分类列表（缓存级）
     * @return
     */
    public List<GoodsCateVO> getRetailGoodsCateByCache(Integer cateGrade){
        if (redisService.hasKey(CacheKeyConstant.RETAIL_GOODS_CATE.concat(String.valueOf(cateGrade)))) {
            return JSONArray.parseArray(redisService.getString(CacheKeyConstant.RETAIL_GOODS_CATE.concat(String.valueOf(cateGrade))), GoodsCateVO.class);
        }

        //生成缓存
        this.fillRetailGoodsCateRedis(cateGrade);
        return JSONArray.parseArray(redisService.getString(CacheKeyConstant.RETAIL_GOODS_CATE.concat(String.valueOf(cateGrade))), GoodsCateVO.class);
    }
}
