package com.wanmi.sbc.marketing.pile.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.marketing.pile.model.root.PileActivity;
import com.wanmi.sbc.marketing.pile.model.root.PileActivityGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: chenchang
 * @Date: 2022/09/08
 * @Description: 囤货活动商品配置Repository
 */
@Repository
public interface PileActivityGoodsRepository extends JpaRepository<PileActivityGoods, Long>,
        JpaSpecificationExecutor<PileActivityGoods> {

    /**
     * 根据囤货活动id获取商品作用范围
     *
     * @param activityId 囤货活动id
     * @return
     */
    List<PileActivityGoods> findByActivityId(String activityId);


    /**
     *
     * @param activityId 囤货活动id
     * @return
     */
    List<PileActivityGoods> findByActivityIdAndGoodsInfoIdInAndDelFlag(String activityId,List<String> goodsInfoIds, DeleteFlag no);

    /**
     * 根据囤货活动id删除商品作用范围
     *
     * @param activityId 囤货活动id
     * @return
     */
    void deleteByActivityId(String activityId);

    /**
     * 扣减库存
     * @param num
     * @param goodsInfoId
     * @param activityId
     */
    @Modifying
    @Query("update PileActivityGoods set virtualStock = virtualStock- ?1 where   goodsInfoId = ?2 and activityId =?3")
    void subVirtualStock(Long num,String goodsInfoId,String activityId);


    /**
     * 增加库存
     * @param num
     * @param goodsInfoId
     * @param activityId
     */
    @Modifying
    @Query("update PileActivityGoods set virtualStock = virtualStock+ ?1 where   goodsInfoId = ?2 and activityId =?3")
    void addVirtualStock(Long num,String goodsInfoId,String activityId);

    PileActivityGoods findByIdAndActivityIdAndDelFlag(Long id, String activityId, DeleteFlag no);

    List<PileActivityGoods> findByActivityIdAndDelFlag(String activityId, DeleteFlag no);

    @Modifying
    @Query(value = "update pile_activity_goods " +
            " set del_flag=1,del_person=?3,del_time=?4 " +
            " where activity_id = ?1 and ware_id =?2 and del_flag=0 "
            , nativeQuery = true)
    void updateByActivityIdAndWareId(String activityId, Long wareId, String deletePerson, LocalDateTime now);

    @Query(value = "select goods_info_id from pile_activity_goods where del_flag =0 and activity_id = ?1 and ware_id =?2", nativeQuery = true)
    List<String> findGoodsInfoIdByActivityIdAndWareId(String activityId, Long wareId);

    List<PileActivityGoods> findByActivityIdAndWareIdAndGoodsInfoIdInAndDelFlag(String activityId, Long wareId, List<String> pageGoodsIds, DeleteFlag no);

    /**
     * 获取多商家下单囤货商品虚拟库存
     * @param goodsInfoIds 囤货商品id
     * @return
     */
    @Query(value = "select a.* from pile_activity_goods a, pile_activity b " +
            "where a.activity_id  = b.activity_id and activity_type = 0  and termination_flag = 0  and b.start_time < now() and b.end_time > now()  and b.del_flag = 0 and a.goods_info_id in ?1 and a.del_flag =0"
            , nativeQuery = true)
    List<PileActivityGoods> findPideGoodsListByGoodsInfoIds(List<String> goodsInfoIds);


    /**
     * 根据囤货活动id获取商品作用范围
     *
     * @param activityIds 囤货活动id
     * @return
     */
    List<PileActivityGoods> findByActivityIdIn(List<String> activityIds);
}
