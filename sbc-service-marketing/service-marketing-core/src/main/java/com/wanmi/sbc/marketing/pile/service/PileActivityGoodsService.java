package com.wanmi.sbc.marketing.pile.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.ListByCondition4PileActivityRequest;
import com.wanmi.sbc.goods.api.request.info.ListGoodsInfoIdsRequest;
import com.wanmi.sbc.goods.bean.vo.ListGoodsInfoByGoodsInfoIdsVO;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsAddRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsDeleteRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsModifyRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityGoodsPageRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsPageVO;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import com.wanmi.sbc.marketing.pile.model.root.PileActivityGoods;
import com.wanmi.sbc.marketing.pile.repository.PileActivityGoodsRepository;
import com.wanmi.sbc.marketing.pile.repository.PileActivityRepository;
import com.wanmi.sbc.marketing.util.DbUtils;
import com.wanmi.sbc.marketing.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: chenchang
 * @Date: 2022/09/19
 * @Description: 囤货活动商品Service
 */
@Service
@Slf4j
public class PileActivityGoodsService {

    @Autowired
    private PileActivityService pileActivityService;

    @Autowired
    private PileActivityRepository pileActivityRepository;

    @Autowired
    private PileActivityGoodsRepository pileActivityGoodsRepository;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private EntityManager entityManager;

    @Transactional(rollbackFor = Exception.class)
    public void add(PileActivityGoodsAddRequest request) {
        PileActivity pileActivity = pileActivityService.checkExists(request.getActivityId());
        request.setPublicVirtualStock(pileActivity.getPublicVirtualStock());

        //todo enum
        if (Objects.equals(0, request.getSelectFlag())) {
            //选中商品sku
            saveActivityGoods(request, request.getSelectedGoods());
        } else if (Objects.equals(1, request.getSelectFlag())) {
            //分仓全选
            ListByCondition4PileActivityRequest req = new ListByCondition4PileActivityRequest();
            req.setStoreId(request.getStoreId());
            req.setWareId(request.getWareId());

            List<String> goodsInfoIds = goodsInfoQueryProvider.listByCondition4PileActivity(req).getContext();
            log.info("查找到可参与活动的商品数：{}", goodsInfoIds.size());
            if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
                saveActivityGoods(request, goodsInfoIds);
            }

        } else if (Objects.equals(2, request.getSelectFlag())) {
            Assert.notNull(request.getFilterCondition(),"filterCondition must not be null");
            //分仓条件筛选
            ListByCondition4PileActivityRequest req = JSONObject.parseObject(request.getFilterCondition(), ListByCondition4PileActivityRequest.class);
            req.setStoreId(request.getStoreId());
            req.setWareId(request.getWareId());

            List<String> goodsInfoIds = goodsInfoQueryProvider.listByCondition4PileActivity(req).getContext();
            log.info("查找到可参与活动的商品数：{}", goodsInfoIds.size());
            if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
                saveActivityGoods(request, goodsInfoIds);
            }
        }
    }

    private void saveActivityGoods(PileActivityGoodsAddRequest request, List<String> goodsInfoIds) {
        Set<String> oldGoodsSet = pileActivityGoodsRepository
                .findByActivityIdAndDelFlag(request.getActivityId(), DeleteFlag.NO)
                .stream().map(PileActivityGoods::getGoodsInfoId)
                .collect(Collectors.toSet());

        //重复添加，已有的不再添加
        List<String> goodsListAdded = goodsInfoIds;
        if (CollectionUtils.isNotEmpty(oldGoodsSet)) {
            goodsListAdded = goodsInfoIds.stream().distinct()
                    .filter(item -> !oldGoodsSet.contains(item))
                    .collect(Collectors.toList());
        }

        if (CollectionUtils.isNotEmpty(goodsListAdded)) {
            List<PileActivityGoods> goodsList = buildActivityGoods(request, goodsListAdded);
            DbUtils.batchInsert(entityManager, goodsList);
        }
    }

    private List<PileActivityGoods> buildActivityGoods(PileActivityGoodsAddRequest request, List<String> goodsInfoIds) {
        Assert.notNull(request, "request must not be null");
        Assert.notEmpty(goodsInfoIds, "goodsInfoIds must not be empty");

        return goodsInfoIds.stream().distinct().map(goodsInfoId -> {
            PileActivityGoods entity = new PileActivityGoods();
            entity.setActivityId(request.getActivityId());
            entity.setGoodsInfoId(goodsInfoId);
            entity.setWareId(request.getWareId());
            entity.setVirtualStock(request.getPublicVirtualStock());
            entity.setDelFlag(DeleteFlag.NO);
            entity.setCreateTime(LocalDateTime.now());
            entity.setCreatePerson(request.getCreatePerson());
            return entity;
        }).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void modify(PileActivityGoodsModifyRequest request) {
        PileActivityGoods oldGoods = assertExists(request.getActivityId(), request.getId());
        //TODO LOG

        PileActivityGoods upd = new PileActivityGoods();
        BeanUtils.copyProperties(oldGoods,upd);
        upd.setId(request.getId());
        upd.setVirtualStock(request.getVirtualStock());
        upd.setUpdatePerson(request.getUpdatePerson());
        upd.setUpdateTime(LocalDateTime.now());
        pileActivityGoodsRepository.save(upd);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(PileActivityGoodsDeleteRequest request) {
        // TODO: ENUM
        if(Objects.equals(0,request.getDelType())){
            PileActivityGoods oldGoods = getPileActivityGoods(request.getActivityId(), request.getId());
            if (Objects.isNull(oldGoods)) {
                return;
            }

            //TODO LOG
            PileActivityGoods upd = new PileActivityGoods();
            BeanUtils.copyProperties(oldGoods, upd);
            upd.setId(request.getId());
            upd.setDelPerson(request.getDeletePerson());
            upd.setDelTime(LocalDateTime.now());
            upd.setDelFlag(DeleteFlag.YES);
            pileActivityGoodsRepository.save(upd);
            return;
        }

        if (Objects.equals(1, request.getDelType())) {
            Assert.notNull(request.getActivityId(), "activityId must not be null");
            Assert.notNull(request.getWareId(), "wareId must not be null");
            pileActivityGoodsRepository.updateByActivityIdAndWareId(request.getActivityId(), request.getWareId(), request.getDeletePerson(), LocalDateTime.now());
        }

    }

    public PileActivityGoods assertExists(String activityId, Long id) {
        PileActivityGoods pileActivityGoods = getPileActivityGoods(activityId, id);
        if (Objects.isNull(pileActivityGoods)) {
            log.warn("参与囤货活动的商品不存在，活动id={},囤货商品表id={}", activityId, id);
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "参与囤货活动的商品不存在！");
        }
        return pileActivityGoods;
    }

    private PileActivityGoods getPileActivityGoods(String activityId, Long id) {
        return pileActivityGoodsRepository.findByIdAndActivityIdAndDelFlag(id, activityId,DeleteFlag.NO);
    }

    public MicroServicePage<PileActivityGoodsPageVO> page(PileActivityGoodsPageRequest request) {
        //获取活动商品id集合
        List<String> activityGoodsIds = pileActivityGoodsRepository.findGoodsInfoIdByActivityIdAndWareId(request.getActivityId(), request.getWareId());
        if (CollectionUtils.isEmpty(activityGoodsIds)) {
            return new MicroServicePage<>(new ArrayList<>(), request.getPageable(), 0);
        }

        //从商品服务获取满足商品查询条件的商品id
        List<String> matchGoodsIds = activityGoodsIds;
        if (!StringUtils.isAllBlank(request.getLikeErpNo(), request.getLikeGoodsName())
                || ObjectUtils.anyNotNull(request.getBrandId(), request.getCateId())
        ) {
            ListGoodsInfoIdsRequest listGoodsInfoIdsReq = new ListGoodsInfoIdsRequest();
            BeanUtils.copyProperties(request, listGoodsInfoIdsReq);
            listGoodsInfoIdsReq.setGoodsInfoIds(activityGoodsIds);
            matchGoodsIds = goodsInfoQueryProvider.listGoodsInfoIds(listGoodsInfoIdsReq).getContext();
            if (CollectionUtils.isEmpty(matchGoodsIds)) {
                return new MicroServicePage<>(new ArrayList<>(), request.getPageable(), 0);
            }
        }

        List<String> currentPageGoodsIds = PageUtils.getCurrentPage(matchGoodsIds,request.getPageNum(),request.getPageSize());

        //填充当前分页的商品信息
        Map<String, PileActivityGoods> activityGoodsMap = pileActivityGoodsRepository
                .findByActivityIdAndWareIdAndGoodsInfoIdInAndDelFlag(request.getActivityId(), request.getWareId(), currentPageGoodsIds, DeleteFlag.NO)
                .stream().collect(Collectors.toMap(PileActivityGoods::getGoodsInfoId, v -> v, (k1, k2) -> k1));

        Map<String, ListGoodsInfoByGoodsInfoIdsVO> goodsInfoMap = goodsInfoQueryProvider
                .listGoodsInfoByGoodsInfoIds(currentPageGoodsIds).getContext();

        List<PileActivityGoodsPageVO> responsesList = currentPageGoodsIds.stream().map(goodsId->{
            PileActivityGoods paGoods = activityGoodsMap.getOrDefault(goodsId, new PileActivityGoods());
            ListGoodsInfoByGoodsInfoIdsVO goodsInfo = goodsInfoMap.getOrDefault(goodsId, new ListGoodsInfoByGoodsInfoIdsVO());

            PileActivityGoodsPageVO vo = new PileActivityGoodsPageVO();
            vo.setId(paGoods.getId());
            vo.setWareId(paGoods.getWareId());
            vo.setVirtualStock(paGoods.getVirtualStock());
            vo.setGoodsInfoNo(goodsInfo.getGoodsInfoNo());
            vo.setErpNo(goodsInfo.getErpNo());
            vo.setGoodsInfoName(goodsInfo.getGoodsInfoName());
            vo.setCateName(goodsInfo.getCateName());
            vo.setCateId(goodsInfo.getCateId());
            vo.setBrandName(goodsInfo.getBrandName());
            vo.setBrandId(goodsInfo.getBrandId());
            vo.setGoodsInfoPrice(goodsInfo.getGoodsInfoPrice());
            return vo;
        }).collect(Collectors.toList());

        MicroServicePage<PileActivityGoodsPageVO> pageResp = new MicroServicePage<>(responsesList, request.getPageable(), matchGoodsIds.size());
        //有查询条件时返回总数不正确，再设置一次
        pageResp.setTotal(matchGoodsIds.size());
        return pageResp;
    }
}
