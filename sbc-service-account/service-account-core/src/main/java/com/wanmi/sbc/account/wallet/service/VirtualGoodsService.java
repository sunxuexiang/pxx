package com.wanmi.sbc.account.wallet.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.google.common.collect.Lists;
import com.wanmi.sbc.account.api.request.wallet.VirtualGoodsRequest;
import com.wanmi.sbc.account.bean.vo.VirtualGoodsVO;
import com.wanmi.sbc.account.wallet.model.root.VirtualGoods;
import com.wanmi.sbc.account.wallet.repository.VirtualGoodsRepository;
import com.wanmi.sbc.account.wallet.request.VirtualGoodsQueryRequest;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponActivityProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponActivityConfigSaveRequest;
import com.wanmi.sbc.marketing.api.response.coupon.CouponActivityDetailResponse;
import com.wanmi.sbc.marketing.bean.constant.Constant;
import com.wanmi.sbc.marketing.bean.enums.CouponActivityType;
import com.wanmi.sbc.marketing.bean.vo.CouponActivityVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jeffrey
 * @create 2021-08-21 17:13
 */

@Service
@Transactional(readOnly = true, timeout = 10)
@Slf4j
public class VirtualGoodsService {
    @Autowired
    private VirtualGoodsRepository virtualGoodsRepository;

    @Autowired
    private CouponActivityProvider couponActivityProvider;


    /**
     * 根据goodsic查询虚拟商品列表
     *
     * @return
     */
    public VirtualGoods getVirtualGoods(Long goodsId) {
        Integer delFlag = 0;
        return virtualGoodsRepository.findByGoodsIdAndDelFlag(goodsId, delFlag);
    }

    /**
     * 查询多个商品(根据id)
     *
     * @param goodsIdList
     * @return
     */
    public List<VirtualGoods> getVirtualGoodsList(List<Long> goodsIdList) {
        Integer delFlag = 0;
        return virtualGoodsRepository.findByDelFlagAndGoodsIdIn(goodsIdList, delFlag);
    }

    /**
     * 分页查询所有虚拟商品
     *
     * @return
     */

    public Page<VirtualGoods> getPageVirtualGoodsList(VirtualGoodsRequest request) {
        request.setDelFlag(0);
        VirtualGoodsQueryRequest newRequest = KsBeanUtil.convert(request, VirtualGoodsQueryRequest.class);
        Pageable pageable = PageRequest.of(request.getPageNum(), request.getPageSize(), Sort.Direction.DESC, "createTime");
        return virtualGoodsRepository.findAll(VirtualGoodsQueryRequest.build(newRequest), pageable);
    }

    /**
     * 增加一个虚拟商品
     * 如果有优惠券就添加一个活动
     */
    @Transactional
    @LcnTransaction
    public Boolean saveVirturalGoods(VirtualGoodsRequest request) {
        VirtualGoodsVO virtualGoodsVO = request.getVirtualGoodsVO();
        virtualGoodsVO.setDelFlag(0);
        //判断是否有优惠券信息
        List<CouponActivityConfigSaveRequest> couponActivityConfigs = request.getCouponActivityConfigs();
        if(CollectionUtils.isNotEmpty(couponActivityConfigs)){
            CouponActivityVO activity = this.createActivity(request);
            virtualGoodsVO.setActivityId(activity.getActivityId());
        }
        VirtualGoods virtualGoods = virtualGoodsRepository.save(KsBeanUtil.convert(virtualGoodsVO, VirtualGoods.class));
        if (Objects.nonNull(virtualGoods)) {
            return true;
        }
        return false;
    }

    private CouponActivityVO createActivity(VirtualGoodsRequest request){
        List<CouponActivityConfigSaveRequest> couponActivityConfigs = request.getCouponActivityConfigs();
        CouponActivityAddRequest couponActivityAddRequest = new CouponActivityAddRequest();
        couponActivityAddRequest.setCouponActivityConfigs(couponActivityConfigs);
        couponActivityAddRequest.setPlatformFlag(DefaultFlag.YES);
        couponActivityAddRequest.setCreatePerson(request.getCreatePerson());
        couponActivityAddRequest.setStoreId(Constant.BOSS_DEFAULT_STORE_ID);
        //设置是否平台等级
        couponActivityAddRequest.setJoinLevelType(DefaultFlag.YES);
        couponActivityAddRequest.setActivityName("签到赠送优惠券");
        couponActivityAddRequest.setActivityTitle("充值成功");
        couponActivityAddRequest.setActivityDesc("充值优惠券已发放至您的账户");
        //用户充值赠券
        couponActivityAddRequest.setCouponActivityType(CouponActivityType.RECHARGE_COUPON);
        couponActivityAddRequest.setReceiveType(DefaultFlag.NO);
        couponActivityAddRequest.setPlatformFlag(DefaultFlag.YES);
        couponActivityAddRequest.setJoinLevel("-1");
        BaseResponse<CouponActivityDetailResponse> response = couponActivityProvider.add(couponActivityAddRequest);
        return response.getContext().getCouponActivity();
    }

    /**
     * 根据ID删除一个商品(逻辑删除)
     */
    @Transactional
    public void deleteVirtualGoodsByGoodsId(Long goodsId) {
        VirtualGoods virtualGoods = getVirtualGoods(goodsId);
        if (virtualGoods == null) {
            return;
        }
        virtualGoods.setDelFlag(1);
        virtualGoodsRepository.save(virtualGoods);
    }

    /**
     * 批量删除虚拟商品
     *
     * @param goodsIdList
     */
    @Transactional
    public void deleteVirtualGoodsByGoodsIds(List<Long> goodsIdList) {
        List<VirtualGoods> virtualGoodsList = virtualGoodsRepository.findByGoodsIdIn(goodsIdList);
        for (VirtualGoods virtualGoods : virtualGoodsList) {
            virtualGoods.setDelFlag(1);
        }
        virtualGoodsRepository.saveAll(virtualGoodsList);
    }
}
