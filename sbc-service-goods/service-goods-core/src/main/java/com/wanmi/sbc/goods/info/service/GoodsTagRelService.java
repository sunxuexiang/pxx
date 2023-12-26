package com.wanmi.sbc.goods.info.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goods.GoodsTagRelReOperateRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsTagRelReRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsTagRelResponse;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsTag;
import com.wanmi.sbc.goods.info.model.root.GoodsTagRel;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.info.repository.GoodsTagRelRepository;
import com.wanmi.sbc.goods.info.repository.GoodsTagRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class GoodsTagRelService {

    @Autowired
    private GoodsTagRelRepository goodsTagRelRepository;

    @Autowired
    private GoodsTagRepository goodsTagRepository;

    @Autowired
    private GoodsRepository goodsRepository;


    public List<GoodsTagRelResponse> listGoodsTagRel(GoodsTagRelReRequest request) {
        return KsBeanUtil.convertList(goodsTagRelRepository.findAll(getWhereCriteria(request)), GoodsTagRelResponse.class);
    }

    public Specification<GoodsTagRel> getWhereCriteria(GoodsTagRelReRequest request) {
        return (root, query, build) -> {
            List<Predicate> predicates = new ArrayList<>();
            final Long tagId = request.getTagId();
            if (Objects.nonNull(tagId)) {
                predicates.add(build.equal(root.get("tagId"), tagId));
            }
            final List<Long> tagIds = request.getTagIds();
            if (CollectionUtils.isNotEmpty(tagIds)) {
                CriteriaBuilder.In in = build.in(root.get("tagId"));
                tagIds.forEach(id -> in.value(id));
                predicates.add(in);
            }
            final String goodsId = request.getGoodsId();
            if (StringUtils.isNotBlank(goodsId)) {
                predicates.add(build.equal(root.get("goodsId"), goodsId));
            }
            final List<String> goodsIds = request.getGoodsIds();
            if (CollectionUtils.isNotEmpty(goodsIds)) {
                CriteriaBuilder.In in = build.in(root.get("goodsId"));
                goodsIds.forEach(id -> in.value(id));
                predicates.add(in);
            }
            predicates.add(build.equal(root.get("delFlag"), DeleteFlag.NO.toValue()));
            javax.persistence.criteria.Predicate[] p = predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]);
            return p.length == 0 ? null : p.length == 1 ? p[0] : build.and(p);
        };
    }

    public boolean batchGoodsTagRel(GoodsTagRelReOperateRequest request) {
        final Integer type = request.getType();
        if (CollectionUtils.isEmpty(request.getGoodsIds()) || null == request.getTagId() || null == type) {
            log.error("batchGoodsTagRel 参数异常，{}", JSON.toJSONString(request));
            return false;
        }
        final GoodsTag tag = goodsTagRepository.findById(request.getTagId()).orElse(null);
        if (tag == null) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "当前标签不存在");
        }
        if (type != 1 && type != 2) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "操作异常");
        }
        final List<Goods> goods = goodsRepository.findByGoodsIdIn(request.getGoodsIds());
        if (!Objects.equals(request.getGoodsIds().size(), goods.size())) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品数据异常");
        }
        // 防止重复插入先删
        goodsTagRelRepository.updateDelFlagByTagIdAndGoodsIdIn(DeleteFlag.YES, request.getTagId(), request.getGoodsIds());
        if (request.getType() == 1) {
            List<GoodsTagRel> goodsTagRelLIst = new ArrayList<>();
            goods.forEach(g -> {
                final GoodsTagRel goodsTagRel = new GoodsTagRel();
                goodsTagRelLIst.add(goodsTagRel);
                goodsTagRel.setTagId(tag.getTagId());
                goodsTagRel.setTagName(tag.getTagName());
                goodsTagRel.setGoodsId(g.getGoodsId());
                goodsTagRel.setGoodsName(g.getGoodsName());
                goodsTagRel.setCreateTime(LocalDateTime.now());
                goodsTagRel.setUpdateTime(LocalDateTime.now());
                goodsTagRel.setDelFlag(DeleteFlag.NO);
            });
            goodsTagRelRepository.saveAll(goodsTagRelLIst);
        }
        return true;
    }
}
