package com.wanmi.sbc.goods.info.repository;


import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsExportByTimeAndStausVO;
import com.wanmi.sbc.goods.info.model.root.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods, String>, JpaSpecificationExecutor<Goods> {
    @Query("select g from Goods g where g.goodsId in ?1")
    List<Goods> findByGoodsIdIn(Collection<String> goodsIds);

    /**
     * 根据多个商品ID编号进行删除
     *
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update Goods w set w.delFlag = '1', w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品ID编号更新上下架状态
     *
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update Goods w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsId in ?2")
    void updateAddedFlagByGoodsIds(Integer addedFlag, List<String> goodsIds);


    /**
     * 根据多个商品ID编号更新上下架状态
     *
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update Goods w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.providerGoodsId in ?2")
    void updateAddedFlagByPrividerGoodsIds(Integer addedFlag, List<String> goodsIds);


    /**
     * 根据多个分类ID编号更新分类
     *
     * @param newCateId 分类ID
     * @param cateIds   多个分类ID
     */
    @Modifying
    @Query("update Goods w set w.cateId = ?1, w.updateTime = now() where w.cateId in ?2")
    void updateCateByCateIds(Long newCateId, List<Long> cateIds);

    /**
     * 根据多个商品ID编号更新审核状态
     *
     * @param auditStatus 审核状态
     * @param auditReason 审核原因
     * @param goodsIds    多个商品
     */
    @Modifying
    @Query("update Goods w set w.auditStatus = ?1, w.auditReason = ?2, w.goodsSeqNum = null , w.submitTime = now()   where w.goodsId in ?3")
    void updateAuditDetail(CheckStatus auditStatus, String auditReason, List<String> goodsIds);

    /**
     * 根据商家id 批量更新商家名称
     *
     * @param supplierName
     * @param companyInfoId
     */
    @Modifying
    @Query("update Goods g set g.supplierName = :supplierName where g.companyInfoId = :companyInfoId")
    void updateSupplierName(@Param("supplierName") String supplierName, @Param("companyInfoId") Long companyInfoId);

    /**
     * 根据品牌id 批量把spu品牌置为null
     *
     * @param brandId
     */
    @Modifying
    @Query("update Goods g set g.brandId = null where g.brandId = :brandId")
    void updateBrandByBrandId(@Param("brandId") Long brandId);


    /**
     * 根据店铺id及品牌id列表 批量把spu品牌置为null
     *
     * @param storeId
     * @param brandIds
     */
    @Modifying
    @Query("update Goods g set g.brandId = null where g.storeId = :storeId and g.brandId in (:brandIds)")
    void updateBrandByStoreIdAndBrandIds(@Param("storeId") Long storeId, @Param("brandIds") List<Long> brandIds);

    /**
     * 根据类别id查询SPU
     *
     * @param cateId
     * @return
     */
    @Query
    List<Goods> findAllByCateId(Long cateId);

    /**
     * 根据多个商品ID编号编辑运费模板
     *
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update Goods w set w.freightTempId = ?1, w.updateTime = now() where w.goodsId in ?2")
    void updateFreightTempIdByGoodsIds(Long freightTempId, List<String> goodsIds);

    /**
     * 修改商品运费模板为默认运费模板
     *
     * @param oldFreightTempId
     * @param freightTempId
     */
    @Modifying
    @Query("update Goods g set g.freightTempId = :freightTempId where g.freightTempId = :oldFreightTempId")
    void updateFreightTempId(@Param("oldFreightTempId") Long oldFreightTempId, @Param("freightTempId") Long freightTempId);

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 更新商品评论数
     * @Date 14:31 2019/4/11
     * @Param [goodsId]
     **/
    @Modifying
    @Query("update Goods g set g.goodsEvaluateNum = g.goodsEvaluateNum+1, g.updateTime = now() where g.goodsId = ?1")
    void updateGoodsEvaluateNum(@Param("goodsId") String goodsId);

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 更新商品收藏量
     * @Date 14:43 2019/4/11
     * @Param [goodsCollectNum, GoodsId]
     **/
    @Modifying
    @Query("update Goods g set g.goodsCollectNum = g.goodsCollectNum + ?1, g.updateTime = now()  where g.goodsId = ?2 and g.goodsCollectNum > 0")
    void updateGoodsCollectNum(@Param("goodsCollectNum") Long goodsCollectNum, @Param("goodsId") String GoodsId);

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 更新商品销量
     * @Date 14:43 2019/4/11
     * @Param [goodsSalesNum, goodsId]
     **/
    @Modifying
    @Query("update Goods g set g.goodsSalesNum = g.goodsSalesNum + ?1, g.updateTime = now()  where g.goodsId = ?2")
    void updateGoodsSalesNum(@Param("goodsSalesNum") Long goodsSalesNum, @Param("goodsId") String goodsId);

    /**
     * @return void
     * @Author lvzhenwei
     * @Description 更新商品好评数量
     * @Date 14:50 2019/4/11
     * @Param [goodsPositiveFeedback, goodsId]
     **/
    @Modifying
    @Query("update Goods g set g.goodsFavorableCommentNum = g.goodsFavorableCommentNum + ?1, g.updateTime = now()  where g.goodsId = ?2")
    void updateGoodsFavorableCommentNum(@Param("goodsPositiveFeedback") Long goodsFavorableCommentNum, @Param("goodsId") String goodsId);

    /**
     * 根据供应商商品id查询关联商品
     *
     * @param goodsIds
     * @return
     */
    @Query
    List<Goods> findAllByProviderGoodsIdIn(List<String> goodsIds);

    /**
     * 根据供应商商品id查询关联商品
     *
     * @param providerGoodsId
     * @return
     */
    @Query
    List<Goods> findAllByProviderGoodsId(String providerGoodsId);

    /**
     * 根据商品id查询商品信息
     *
     * @param goodsIds
     * @return
     */
    @Query
    List<Goods> findAllByGoodsIdIn(List<String> goodsIds);

    /**
     * @param goodsId
     * @return
     * @discription 根据goodsid 查询图文信息
     * @author yangzhen
     * @date 2020/9/3 11:21
     */
    @Query("select goodsDetail  from Goods  where goodsId = ?1")
    String getGoodsDetail(String goodsId);

    /**
     * 根据序列号和商品ID查询商品序号是否重复
     *
     * @param goodsSeqNum
     * @param goodsId
     * @return
     */
    @Query("from Goods where goodsSeqNum=?1 and goodsId<>?2 and delFlag=0")
    List<Goods> getExistByGoodsSeqNum(Integer goodsSeqNum, String goodsId);

    /**
     * 查询传入的店铺内商品排序序号是否重复
     * 返回1为重复
     *
     * @param goodsSeqNum
     * @param goodsId
     * @param storeId
     * @return
     */
    @Query(value = "SELECT 1 FROM goods WHERE store_goods_seq_num = ?1 AND goods_id != ?2 AND store_id = ?3 AND del_flag =0", nativeQuery = true)
    String checkStoreGoodsSeqNumExist(Integer goodsSeqNum, String goodsId, Long storeId);

    /**
     * 编辑商品排序序号
     *
     * @param goodsSeqNum
     * @param goodsId
     */
    @Modifying
    @Query("update Goods set goodsSeqNum=?1 where goodsId=?2")
    void modifyGoodsSeqNum(Integer goodsSeqNum, String goodsId);

    /**
     * 编辑店铺内商品排序序号
     *
     * @param goodsSeqNum
     * @param goodsId
     */
    @Modifying
    @Query("update Goods set storeGoodsSeqNum=?1 where goodsId=?2")
    void modifyStoreGoodsSeqNum(Integer goodsSeqNum, String goodsId);


    /**
     * 获取商品通过创建时间和上架转态且不为删除转态
     */
    @Query(value = " SELECT  t2.cate_name,t1.erp_goods_info_no,t1.goods_info_name,t1.shelflife,\n" +
            "\t\t\t\tt3.goods_subtitle,t1.market_price,t4.brand_name,\n" +
            "\t\t\t\tcase when t3.added_flag=0 then '下架' \n" +
            "\t\t\t\t\t\t when t3.added_flag=1 then '上架'\n" +
            "\t\t\t\t\t\t when t3.added_flag=2 then '部分上架'\n" +
            "\t\t\t\t\t\t else '-' end as del_flag\n" +
            "\t\t\t\t\n" +
            "\t\t\t\t,t5.warestock\n" +
            "from goods_info  t1  \n" +
            "inner join goods_cate t2 on t1.cate_id = t2.cate_id \n" +
            "left join goods t3 on t3.goods_id=t1.goods_id and t3.del_flag=0  \n" +
            "left join goods_brand t4 on t4.brand_id = t1.brand_id \n" +
            "inner join ( SELECT  goods_id,sum(stock) as warestock  from goods_ware_stock WHERE del_flag = 0  \n" +
            " group by goods_id)t5 on t5.goods_id = t1.goods_id\n" +
            "\n" +
            "WHERE t3.del_flag=0 and IF(?1 IS NOT NULL ,t3.added_flag = ?1,1=1) and IF(?2 IS NOT NULL OR ?2 !='',t3.create_time > ?2,1=1)" +
            " and IF(?3 IS NOT NULL OR ?3 !='',t3.create_time < ?3,1=1)", nativeQuery = true)
    List<Object> findgoodsByCreatTimeAndStaus(Integer added_flag, String create_time, String create_timeEnd);

    /**
     * 批量修改商品类目
     *
     * @param cateId
     * @param goodsIds
     */
    @Modifying
    @Query("update Goods w set w.cateId = ?1, w.updateTime = now() where w.goodsId in ?2")
    void batchModifyCate(Long cateId, List<String> goodsIds);


    @Query(value = "select store_id,count(*) as num  from goods where del_flag =0 and added_flag =1 and store_id in ?1 group by store_id", nativeQuery = true)
    List<Map<String, Object>> listStoreOnSaleGoodsNum(List<Long> storeIds);


    @Query(value = "select goods_id from `sbc-goods`.goods where  del_flag =0 and sale_type != 1 and added_flag =1  and ware_id =1 and audit_status =1 and create_time > :addTimeStart  order by  create_time desc limit :limitNum", nativeQuery = true)
    List<Map<String, Object>> listRecentAddedNewGoods(@Param("addTimeStart") LocalDateTime addTimeStart, @Param("limitNum") Integer limitNum);
}
