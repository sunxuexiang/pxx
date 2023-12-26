package com.wanmi.sbc.goods.marketing.service;


import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.goods.bean.vo.GoodsMarketingVO;
import com.wanmi.sbc.goods.marketing.model.data.GoodsMarketing;
import com.wanmi.sbc.goods.marketing.repository.GoodsMarketingRepository;
import com.wanmi.sbc.goods.redis.RedisCache;
import com.wanmi.sbc.goods.redis.RedisKeyConstants;
import com.wanmi.sbc.mongo.MongoTccHelper;
import com.wanmi.sbc.mongo.annotation.MongoRollback;
import com.wanmi.sbc.mongo.core.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品营销
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsMarketingService {
    @Autowired
    private GoodsMarketingRepository marketingRepository;

    @Autowired
    private GoodsMarketingService goodsMarketingService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 新增文档
     * 专门用于数据新增服务,不允许数据修改的时候调用
     *
     * @param goodsMarketing
     */
    @MongoRollback(persistence = GoodsMarketing.class, operation = Operation.ADD)
    public GoodsMarketing addGoodsMarketing(GoodsMarketing goodsMarketing) {
        return marketingRepository.save(goodsMarketing);
    }

    /**
     * 修改文档
     * 专门用于数据修改服务,不允许数据新增的时候调用
     *
     * @param goodsMarketing
     */
    @MongoRollback(persistence = GoodsMarketing.class, operation = Operation.UPDATE)
    public GoodsMarketing updateGoodsMarketing(GoodsMarketing goodsMarketing) {
        return marketingRepository.save(goodsMarketing);
    }

    /**
     * 获取采购单中, 各商品用户选择/默认选择参加的营销活动(满减/满折/满赠)
     *
     * @param customerId
     * @return
     */
    public List<GoodsMarketing> queryGoodsMarketingList(String customerId) {
        List<GoodsMarketing> goodsMarketings = marketingRepository.queryGoodsMarketingListByCustomerId(customerId);
        // 过滤掉脏数据
        return  goodsMarketings.stream().filter(goodsMarketing -> {
            return Objects.nonNull(goodsMarketing.getMarketingId())
                    && Objects.nonNull(goodsMarketing.getGoodsInfoId())
                    && Objects.nonNull(goodsMarketing.getCustomerId());
        }).collect(Collectors.toList());
    }

    /**
     * 根据用户编号删除商品使用的营销
     *
     * @param customerId
     * @return
     */
    @MongoRollback(persistence = GoodsMarketing.class, operation = Operation.UPDATE)
    public int delByCustomerId(String customerId) {
        return marketingRepository.deleteAllByCustomerId(customerId);
    }

    /**
     * 根据用户编号和商品编号列表删除商品使用的营销
     *
     * @param customerId
     * @param goodsInfoIds
     * @return
     */
    @MongoRollback(persistence = GoodsMarketing.class, operation = Operation.UPDATE)
    public int delByCustomerIdAndGoodsInfoIds(String customerId, List<String> goodsInfoIds) {
        return marketingRepository.deleteByCustomerIdAndGoodsInfoIdIn(customerId, goodsInfoIds);
    }

    /**
     * 批量添加商品使用的营销
     *
     * @param goodsMarketings
     * @return
     */
    @MongoRollback(persistence = GoodsMarketing.class, operation = Operation.UPDATE)
    public List<GoodsMarketing> batchAdd(List<GoodsMarketing> goodsMarketings) {
        return marketingRepository.insert(goodsMarketings);
    }

    @Resource
    private MongoTccHelper mongoTccHelper;

    @SuppressWarnings("unused")
    public void confirmModify(GoodsMarketing goodsMarketing) {
        mongoTccHelper.confirm();
    }

    @SuppressWarnings("unused")
    public void cancelModify(GoodsMarketing goodsMarketing) {
        mongoTccHelper.cancel();
    }

    /**
     * 修改商品使用的营销
     *
     * @param goodsMarketing
     * @return
     */
    @TccTransaction
    public GoodsMarketing modify(GoodsMarketing goodsMarketing) {
        if (StringUtils.isEmpty(goodsMarketing.getId())) {
            goodsMarketing.setId(UUIDUtil.getUUID());
        }
        List<GoodsMarketing> list= marketingRepository.queryByCustomerIdAndGoodsInfoId(goodsMarketing.getCustomerId(), goodsMarketing.getGoodsInfoId());
//        GoodsMarketing oldGoodsMarketing = marketingRepository.queryByCustomerIdAndGoodsInfoId(goodsMarketing.getCustomerId(), goodsMarketing.getGoodsInfoId());
        GoodsMarketing oldGoodsMarketing =new GoodsMarketing();
        if (CollectionUtils.isNotEmpty(list) && list.size()>1){
            oldGoodsMarketing = null;
            list.forEach(v->{
                marketingRepository.deleteById(v.getId());
            });
        }else if (CollectionUtils.isNotEmpty(list) && list.size()==1){
            oldGoodsMarketing = list.get(0);
        }else {
            oldGoodsMarketing = null;
        }
        if (oldGoodsMarketing == null) {
            return goodsMarketingService.addGoodsMarketing(goodsMarketing);
        }

        oldGoodsMarketing.setMarketingId(goodsMarketing.getMarketingId());
        return goodsMarketingService.updateGoodsMarketing(oldGoodsMarketing);
    }




    public List<GoodsMarketing> getMarketingByCustomerAndGoodsInfoId(String customerId,String goodsInfoIds){
        List<GoodsMarketing> list= marketingRepository.queryByCustomerIdAndGoodsInfoId(customerId,goodsInfoIds);
        if (CollectionUtils.isNotEmpty(list) && list.size()>1){
                for (int i=0;i<list.size()-1;i++){
                    marketingRepository.deleteById(list.get(i).getId());
                }
                this.getMarketingByCustomerAndGoodsInfoId(customerId,goodsInfoIds);
        }
        return list;
    }

    public int deleteByMarketingIdAndGoodsInfoId(Long marketingId, String goodsInfoId){
        return marketingRepository.deleteByMarketingIdAndGoodsInfoId(marketingId, goodsInfoId);
    }

}
