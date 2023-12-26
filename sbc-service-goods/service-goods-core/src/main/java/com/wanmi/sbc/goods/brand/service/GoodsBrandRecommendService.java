package com.wanmi.sbc.goods.brand.service;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandRecommendRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandRecommendResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandRecommendVO;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrandRecommend;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRecommendRepository;
import com.wanmi.sbc.goods.brand.repository.GoodsBrandRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GoodsBrandRecommendService {

    @Autowired
    private GoodsBrandRecommendRepository goodsBrandRecommendRepository;

    @Autowired
    private GoodsBrandRepository goodsBrandRepository;

    /**
     * 添加商品品牌推荐
     *
     * @param request
     * @return
     */
    public BaseResponse addGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        List<GoodsBrandRecommendVO> addGoodsBrandRecommendVOList = request.getAddGoodsBrandRecommendVOList();
        if (addGoodsBrandRecommendVOList.isEmpty()) {
            return BaseResponse.SUCCESSFUL();
        }

        //查看此商品品牌有没有入库
        List<Long> brandIdList = addGoodsBrandRecommendVOList.stream().map(GoodsBrandRecommendVO::getBrandId).collect(Collectors.toList());
        List<GoodsBrandRecommend> chainGoodsBrandRecommendsByBrandIdIn = goodsBrandRecommendRepository.findGoodsBrandRecommendsByBrandIdIn(brandIdList);
        if (!chainGoodsBrandRecommendsByBrandIdIn.isEmpty()) {
            //如有此前被删除的数据,更新标识为未删除
            chainGoodsBrandRecommendsByBrandIdIn.stream().forEach(chainGoodsBrandRecommend -> {
                chainGoodsBrandRecommend.setDelFlag(0);
                chainGoodsBrandRecommend.setAddedFlag(1);
            });
            goodsBrandRecommendRepository.saveAll(chainGoodsBrandRecommendsByBrandIdIn);

            //之前已经添加的品牌
            List<Long> collect = chainGoodsBrandRecommendsByBrandIdIn.stream().map(GoodsBrandRecommend::getBrandId).collect(Collectors.toList());
            //商品品牌推荐新增数据准备
            addGoodsBrandRecommendVOList = addGoodsBrandRecommendVOList.stream().filter(goodsBrandRecommendVO -> !collect.contains(goodsBrandRecommendVO.getBrandId())).collect(Collectors.toList());
        }
        if (addGoodsBrandRecommendVOList.isEmpty()) {
            return BaseResponse.SUCCESSFUL();
        }

        //商品品牌推荐入库
        List<GoodsBrandRecommend> addGoodsBrandRecommendList = KsBeanUtil.convertList(addGoodsBrandRecommendVOList, GoodsBrandRecommend.class);
        for (GoodsBrandRecommend chainGoodsBrandRecommend : addGoodsBrandRecommendList) {
            chainGoodsBrandRecommend.setDelFlag(0);
            chainGoodsBrandRecommend.setAddedFlag(1);
            chainGoodsBrandRecommend.setAddedTime(LocalDateTime.now());
            chainGoodsBrandRecommend.setUpdateTime(LocalDateTime.now());
            chainGoodsBrandRecommend.setCreateTime(LocalDateTime.now());
        }
        goodsBrandRecommendRepository.saveAll(addGoodsBrandRecommendList);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品品牌推荐上下架
     *
     * @param request
     * @return
     */
    public BaseResponse updateAddedGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        List<Long> updateAddedByGoodsBrandRecommendIdList = request.getUpdateAddedByGoodsBrandRecommendIdList();
        List<GoodsBrandRecommend> chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn = goodsBrandRecommendRepository.findGoodsBrandRecommendsByGoodsBrandRecommendIdIn(updateAddedByGoodsBrandRecommendIdList);
        if (chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn.isEmpty()) {
            return BaseResponse.SUCCESSFUL();
        }
        for (GoodsBrandRecommend chainGoodsBrandRecommend : chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn) {
            if (Objects.equals(chainGoodsBrandRecommend.getAddedFlag(), 1)) {
                chainGoodsBrandRecommend.setAddedFlag(0);
            } else if (Objects.equals(chainGoodsBrandRecommend.getAddedFlag(), 0)) {
                chainGoodsBrandRecommend.setAddedFlag(1);
            }
        }
        goodsBrandRecommendRepository.saveAll(chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn);

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除商品品牌推荐
     *
     * @param request
     * @return
     */
    public BaseResponse deleteGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        List<Long> deleteByGoodsBrandRecommendIdList = request.getDeleteByGoodsBrandRecommendIdList();
        List<GoodsBrandRecommend> chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn = goodsBrandRecommendRepository.findGoodsBrandRecommendsByGoodsBrandRecommendIdIn(deleteByGoodsBrandRecommendIdList);
        if (chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn.isEmpty()) {
            return BaseResponse.SUCCESSFUL();
        }
        for (GoodsBrandRecommend chainGoodsBrandRecommend : chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn) {
            chainGoodsBrandRecommend.setDelFlag(1);
        }

        goodsBrandRecommendRepository.saveAll(chainGoodsBrandRecommendsByGoodsBrandRecommendIdIn);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 查询商品品牌推荐列表
     *
     * @param request
     * @return
     */
    public GoodsBrandRecommendResponse listGoodsBrandRecommend(GoodsBrandRecommendRequest request) {
        //Page<GoodsBrandRecommend> all = goodsBrandRecommendRepository.findAll(request.getPageRequest());
        Page<GoodsBrandRecommend> chainGoodsBrandRecommendsByDelFlagAndAddedFlag = goodsBrandRecommendRepository.findGoodsBrandRecommendsByDelFlagAndAddedFlag(0, 1, request.getPageable());
        MicroServicePage<GoodsBrandRecommendVO> goodsBrandRecommendVOS = KsBeanUtil.convertPage(chainGoodsBrandRecommendsByDelFlagAndAddedFlag, GoodsBrandRecommendVO.class);

        return GoodsBrandRecommendResponse.builder().goodsBrandRecommendVOList(goodsBrandRecommendVOS).build();
    }

    /**
     * 查询商品品牌推荐列表
     * -- 数据进行了补充
     *
     * @param request
     * @return
     */
    public GoodsBrandRecommendResponse listGoodsBrandRecommendDataSupplement(GoodsBrandRecommendRequest request) {
        Page<GoodsBrandRecommend> chainGoodsBrandRecommends = goodsBrandRecommendRepository.findGoodsBrandRecommendsByDelFlag(0, request.getPageable());
        MicroServicePage<GoodsBrandRecommendVO> goodsBrandRecommendVOS = KsBeanUtil.convertPage(chainGoodsBrandRecommends, GoodsBrandRecommendVO.class);

        List<GoodsBrandRecommendVO> content = goodsBrandRecommendVOS.getContent();
        if (!CollectionUtils.isEmpty(content)) {
            List<GoodsBrand> byBrandIdIn = goodsBrandRepository.findByBrandIdIn(content.stream().map(GoodsBrandRecommendVO::getBrandId).collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(byBrandIdIn)) {
                return GoodsBrandRecommendResponse.builder().goodsBrandRecommendVOList(goodsBrandRecommendVOS).build();
            }
            for (GoodsBrandRecommendVO goodsBrandRecommendVO : content) {
                GoodsBrand chainGoodsBrand1 = byBrandIdIn.stream().filter(chainGoodsBrand -> Objects.equals(chainGoodsBrand.getBrandId(), goodsBrandRecommendVO.getBrandId())).findFirst().orElse(new GoodsBrand());
                goodsBrandRecommendVO.setBrandName(chainGoodsBrand1.getBrandName());
                goodsBrandRecommendVO.setLogo(chainGoodsBrand1.getLogo());
            }
        }

        return GoodsBrandRecommendResponse.builder().goodsBrandRecommendVOList(goodsBrandRecommendVOS).build();
    }

    /**
     * 修改商品品牌推荐名称是否显示
     *
     * @return
     */
    public BaseResponse updateGoodsBrandRecommendNameStatus(Integer nameStatus) {
        if (!nameStatus.equals(0) && !nameStatus.equals(1)) {
            return BaseResponse.error("参数值错误！");
        }
        goodsBrandRecommendRepository.updateGoodsBrandRecommendNameStatus(nameStatus);
        return BaseResponse.SUCCESSFUL();
    }
}
