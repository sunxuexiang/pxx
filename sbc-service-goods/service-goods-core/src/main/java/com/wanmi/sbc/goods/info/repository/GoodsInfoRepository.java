package com.wanmi.sbc.goods.info.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoOnlyShelflifeDTO;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;

import com.wanmi.sbc.goods.bean.vo.GoodsInfoSimpleVo;
import com.wanmi.sbc.goods.info.model.entity.GoodsInfoParams;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * SKU数据源
 * Created by daiyitian on 2017/04/11.
 */
@Repository
public interface GoodsInfoRepository extends JpaRepository<GoodsInfo, String>, JpaSpecificationExecutor<GoodsInfo>{

    /**
     * 根据spuIdList查询sku
     * @param goodsIdList
     */
    List<GoodsInfo> findByGoodsIdIn(List<String> goodsIdList);

    Optional<GoodsInfo> findByGoodsInfoIdAndStoreIdAndDelFlag(String goodsInfoId, Long storeId, DeleteFlag deleteFlag);
    Optional<GoodsInfo> findByGoodsInfoIdAndParentGoodsInfoIdAndWareId(String goodsInfoId,  String parentGoodsInfoId, Long wareId);
    Optional<GoodsInfo> findByParentGoodsInfoIdAndWareId(String parentGoodsInfoId, Long wareId);

    List<GoodsInfo> findByParentGoodsInfoIdInAndWareId(List<String> parentGoodsInfoId, Long wareId);

    /**
     * 根据spuIdList查询sku(不包含已删除的)
     * @param goodsIdList
     */
    @Query("from GoodsInfo w where w.delFlag = '0' and w.goodsId in ?1")
    List<GoodsInfo> findByGoodsIds(List<String> goodsIdList);
 /**
  * 根据spuIdList查询sku(不包含已删除的)且上线的
  * @param goodsIdList
  */
 @Query("from GoodsInfo w where w.delFlag = '0' and w.addedFlag = 1  and w.goodsId in ?1")
    List<GoodsInfo> getByGoodsIdAndAdded(List<String> goodsIdList);
    /**
     * 根据skuIdList查询sku(包含已删除的)
     * @param goodsInfoIdList
     */
    @Query("from GoodsInfo w where w.goodsInfoId in ?1")
    List<GoodsInfo> findByGoodsInfoIds(List<String> goodsInfoIdList);


    /**
     * 根据skuIdList查询sku(只有保质期)
     * @param goodsInfoIdList
     */
    @Query("from GoodsInfoOnlyShelflifeDTO w where w.goodsInfoId in ?1")
    List<GoodsInfoOnlyShelflifeDTO> findByGoodsInfoIdsShelflife(List<String> goodsInfoIdList);


    /**
     * 根据skuIdList查询sku(正常且上架的商品)
     * @param goodsInfoIdList
     */
    @Query("from GoodsInfo w where w.delFlag = '0' and w.addedFlag = 1 and w.goodsInfoId in ?1")
    List<List<GoodsInfoOnlyShelflifeDTO>> findValidGoodsInfoByInfoIds(List<String> goodsInfoIdList);

    /**
     * 根据erpGoodsInfoNo来查找特价商品
     * @param goodsInfoNos
     */
    @Query("from GoodsInfo w where w.delFlag = '0' and w.goodsInfoType = 1 and w.erpGoodsInfoNo in ?1")
    List<GoodsInfo> findSpecialGoodsByErpNos(List<String>  goodsInfoNos);

    /**
     * 根据erpGoodsInfoNo来查找正常商品
     * @param goodsInfoNos
     */
    @Query("from GoodsInfo w where (w.goodsInfoType is null or w.goodsInfoType = 0) and w.erpGoodsInfoNo = ?1")
    GoodsInfo findByGoodsInfoNo(String  goodsInfoNos);

    /**
     * 根据erpGoodsInfoNo来查找特价商品
     * @param erpNo
     */
    @Query("from GoodsInfo w where w.goodsInfoType = 1 and w.erpGoodsInfoNo = ?1")
    List<GoodsInfo> findSpecialGoodsByErpNo(String  erpNo);

    /**
     * 根据erpGoodsInfoNo来查所有商品
     * @param goodsInfoNos
     */
    @Query("from GoodsInfo w where w.delFlag = 0 and w.erpGoodsInfoNo in ?1")
    List<GoodsInfo> findAllGoodsByErpNos(List<String>  goodsInfoNos);

   /**
    * @description  根据erpno,仓库Id 查goodsInfo
    * @author  shiy
    * @date    2023/4/4 12:30
    * @params  [java.lang.String, java.lang.Long]
    * @return  java.util.List<com.wanmi.sbc.goods.info.model.root.GoodsInfo>
   */
    @Query("from GoodsInfo w where w.delFlag = 0 and w.erpGoodsInfoNo = ?1 and w.wareId=?2 ")
    List<GoodsInfo> findGoodsByErpNosAndWareId(String goodsInfoNos,Long wareId);


    /**
     * 根据erpNo查询sku(正常且上架的商品)
     * @param erpGoodsInfoNoList
     */
    @Query("from GoodsInfo w where w.delFlag = 0 and w.addedFlag = 1 and w.erpGoodsInfoNo in ?1")
    List<GoodsInfo> findValidGoodsInfoByErpGoodsInfoNos(List<String> erpGoodsInfoNoList);

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.goodsId in ?1")
    void deleteByGoodsIds(List<String> goodsIds);

    /**
     * 根据多个商品ID编号进行删除
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.providerGoodsInfoId in ?1")
    void deleteByProviderGoodsInfoId(List<String> goodsIds);

    /**
     * 根据多个商品skuId进行删除
     * @param goodsInfoIds 商品skuId列表
     */
    @Modifying
    @Query("update GoodsInfo w set w.delFlag = '1' ,w.updateTime = now() where w.goodsInfoId in ?1")
    void deleteByGoodsInfoIds(List<String> goodsInfoIds);

    /**
     * 根据多个商品ID编号更新上下架状态
     * @param goodsIds 商品ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsId in ?2")
    void updateAddedFlagByGoodsIds(Integer addedFlag, List<String> goodsIds);

    /**
     * 根据多个商品skuId更新上下架状态
     * @param addedFlag 上下架状态
     * @param goodsInfoIds 商品skuId列表
     */
    @Modifying
    @Query("update GoodsInfo w set w.addedFlag = ?1, w.updateTime = now(), w.addedTime = now() where w.goodsInfoId in ?2")
    void updateAddedFlagByGoodsInfoIds(Integer addedFlag, List<String> goodsInfoIds);

    /**
     * 根据商品SKU编号加库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.stock = w.stock + ?1, w.updateTime = now() where w.goodsInfoId = ?2")
    int addStockById(BigDecimal stock, String goodsInfoId);

    /**
     * 根据商品SKU编号减库存
     * @param stock 库存数
     * @param goodsInfoId 商品ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.stock = w.stock - ?1, w.updateTime = now() where w.goodsInfoId = ?2 and w.stock  >= ?1")
    int subStockById(BigDecimal stock, String goodsInfoId);

    /**
     * 根据多个Sku编号更新审核状态
     * @param auditStatus 审核状态
     * @param goodsIds 多个商品
     */
    @Modifying
    @Query("update GoodsInfo w set w.auditStatus = ?1  where w.goodsId in ?2")
    void updateAuditDetail(CheckStatus auditStatus, List<String> goodsIds);


    @Modifying
    @Query("update GoodsInfo g set g.smallProgramCode = ?2 where g.goodsInfoId = ?1 ")
    void updateSkuSmallProgram(String goodsInfoId,String codeUrl);

    @Modifying
    @Query("update GoodsInfo g set g.smallProgramCode = null ")
    void clearSkuSmallProgramCode();

    /**
     * 根据品牌id 批量把sku品牌置为null
     * @param brandId
     */
    @Modifying
    @Query("update GoodsInfo g set g.brandId = null where g.brandId = :brandId")
    void updateSKUBrandByBrandId(@Param("brandId") Long brandId);

    /**
     * 根据店铺id及品牌id列表 批量把sku品牌置为null
     * @param storeId
     * @param brandIds
     */
    @Modifying
    @Query("update GoodsInfo g set g.brandId = null where g.storeId = :storeId and g.brandId in (:brandIds)")
    void updateBrandByStoreIdAndBrandIds(@Param("storeId") Long storeId, @Param("brandIds") List<Long> brandIds);

    /**
     * 根据店铺id及商品id列表
     * @param storeId
     * @param erpGoodsInfoNos
     */
    @Query("from GoodsInfo g where g.storeId = :storeId and g.erpGoodsInfoNo in (:erpGoodsInfoNos)")
    List<GoodsInfo> selectGoodsInfoByStoreIdAndErpGoodsInfoIds(@Param("storeId") Long storeId, @Param("erpGoodsInfoNos") List<String> erpGoodsInfoNos);

    /**
     * 根据多个分类ID编号更新sku关联分类
     * @param newCateId 分类ID
     * @param cateIds 多个分类ID
     */
    @Modifying
    @Query("update GoodsInfo w set w.cateId = ?1, w.updateTime = now() where w.cateId in ?2")
    void updateSKUCateByCateIds(Long newCateId, List<Long> cateIds);

    /**
     * 分销商品审核通过(单个)
     * @param goodsInfoId
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set w.distributionGoodsAudit = 2, w.updateTime = now() where w.goodsInfoId = ?1")
    int checkDistributionGoods(String goodsInfoId);

    /**
     * 批量审核分销商品
     * @param goodsInfoIds
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set w.distributionGoodsAudit = 2, w.updateTime = now() where w.goodsInfoId in ?1")
    int batchCheckDistributionGoods(List<String> goodsInfoIds);

    /**
     * 驳回或禁止分销商品
     * @param goodsInfoId
     * @param distributionGoodsAudit
     * @param distributionGoodsAuditReason
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set w.distributionGoodsAudit = ?2, w.distributionGoodsAuditReason = ?3, w.updateTime =" +
            " now() where w.goodsInfoId = ?1")
    int refuseCheckDistributionGoods(String goodsInfoId, DistributionGoodsAudit distributionGoodsAudit,
                                    String distributionGoodsAuditReason);

    /**
     * 删除分销商品
     * @param goodsInfoId
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set w.distributionGoodsAudit = 0, w.updateTime = now() where w.goodsInfoId = ?1")
    int delDistributionGoods(String goodsInfoId);

    /**
     * 编辑分销商品，修改佣金和状态
     * @param goodsInfoId
     * @param distributionCommission
     * @param distributionGoodsAudit
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set distributionCommission = ?2, w.distributionGoodsAudit = ?3, w.updateTime = now() " +
            "where w.goodsInfoId = ?1")
    int modifyDistributionGoods(String goodsInfoId, BigDecimal distributionCommission, DistributionGoodsAudit distributionGoodsAudit);

    /**
     * 编辑分销商品，修改佣金比例和状态
     * @param goodsInfoId
     * @param
     * @return
     */
    @Modifying
    @Query("update GoodsInfo w set commissionRate = ?2,distributionCommission = ?3, w.distributionGoodsAudit = ?4, w" +
            ".updateTime = now() where w.goodsInfoId = ?1")
    int modifyCommissionDistributionGoods(String goodsInfoId, BigDecimal commissionRate,BigDecimal
            distributionCommission,  DistributionGoodsAudit distributionGoodsAudit);
    /*
     * @Description: 商品ID<spu> 修改商品审核状态
     * @Author: Bob
     * @Date: 2019-03-11 16:28
    */
    @Modifying
    @Query("update GoodsInfo w set w.distributionGoodsAudit = :distributionGoodsAudit, w.updateTime = now() where w.goodsId = :goodsId")
    int modifyDistributeState(@Param("goodsId") String goodsId, @Param("distributionGoodsAudit") DistributionGoodsAudit distributionGoodsAudit);


    /**
     * 添加分销商品前，验证所添加的sku是否符合条件
     * 条件：商品是否有效状态（商品已审核通过且未删除和上架状态）以及是否是零售商品
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "select info.goods_info_id " +
            "from goods_info info " +
            "left join goods g on info.goods_id = g.goods_id " +
            "where info.goods_info_id in (:goodsInfoIds) " +
            "and (info.distribution_goods_audit != 0 " +
            "or info.added_flag = 0 " +
            "or info.del_flag = 1 " +
            "or g.sale_type = 0 " +
            "or info.audit_status != 1)", nativeQuery = true)
    List<Object> getInvalidGoodsInfoByGoodsInfoIds(@Param("goodsInfoIds") List<String> goodsInfoIds);


    /**
     * 添加企业购商品前，验证所添加的sku是否符合条件
     * 条件：商品是否有效状态（商品已审核通过且未删除和上架状态) 以及是否是零售商品
     * @param goodsInfoIds
     * @return
     */
    @Query(value = "select info.goods_info_id " +
            "from goods_info info " +
            "left join goods g on info.goods_id = g.goods_id " +
            "where info.goods_info_id in (:goodsInfoIds) " +
            "and (info.enterprise_goods_audit > 0 " +
            "or info.added_flag = 0 " +
            "or info.del_flag = 1 " +
            "or g.sale_type = 0 " +
            "or info.audit_status != 1)", nativeQuery = true)
    List<Object> getInvalidEnterpriseByGoodsInfoIds(@Param("goodsInfoIds") List<String> goodsInfoIds);

    @Query(value = "SELECT b.goods_id,a.goods_info_id,a.goods_info_img,b.goods_img,a.market_price,b.goods_unit,a.goods_info_name,a.del_flag,a.added_flag,a.audit_status,a.checked_added_flag,1 " +
            "FROM goods_info a LEFT JOIN goods b ON a.goods_id=b.goods_id  " +
            "WHERE a.goods_info_id in (:goodsInfoIds)",nativeQuery = true)
    List<Object> findGoodsInfoByIds(@Param("goodsInfoIds") List<String> goodsInfoIds);


    /**
     * 根据单品ids，查询商品名称、市场价
     *
     * @param goodsInfoIds 单品ids
     * @return
     */
    @Query(value = "select new com.wanmi.sbc.goods.info.model.entity.GoodsInfoParams(g.goodsInfoId, g.goodsInfoNo, g.goodsInfoName,g.marketPrice) " +
            " from GoodsInfo g where g.goodsInfoId in (?1)")
    List<GoodsInfoParams> findGoodsInfoParamsByIds(List<String> goodsInfoIds);

    /**
     * 修改商品的企业价格,并更新企业商品审核的状态
     * @param goodsInfoId
     * @param enterPrisePrice
     * @param enterPriseAuditState
     * @return
     */
    @Modifying
    @Query(value = "update GoodsInfo gi set gi.enterPrisePrice = :enterPrisePrice ,gi.enterPriseAuditState = :enterPriseAuditState, " +
            "gi.updateTime = now() where gi.goodsInfoId = :goodsInfoId and gi.delFlag = 0")
    int updateGoodsInfoEnterPrisePrice(@Param("goodsInfoId") String goodsInfoId,
                                       @Param("enterPrisePrice") BigDecimal enterPrisePrice,
                                       @Param("enterPriseAuditState") EnterpriseAuditState enterPriseAuditState);

    /**
     * 批量审核企业购商品 - 审核通过
     * @param goodsInfoIds
     * @param enterPriseAuditState
     * @return
     */
    @Modifying
    @Query(value = "update GoodsInfo gi set gi.enterPriseAuditState = :enterPriseAuditState,gi.updateTime = now() where gi.goodsInfoId in :goodsInfoIds " +
            "and gi.delFlag = 0")
    int batchAuditEnterprise(@Param("goodsInfoIds") List<String> goodsInfoIds,
                                    @Param("enterPriseAuditState") EnterpriseAuditState enterPriseAuditState);

    /**
     * 批量审核企业购商品 - 被驳回
     * @param goodsInfoIds
     * @param enterPriseAuditState
     * @return
     */
    @Modifying
    @Query(value = "update GoodsInfo gi set gi.enterPriseAuditState = :enterPriseAuditState,gi.enterPriseGoodsAuditReason = :enterPriseGoodsAuditReason, " +
            "gi.updateTime = now() where gi.goodsInfoId in :goodsInfoIds and gi.delFlag = 0")
    int batchRejectAuditEnterprise(@Param("goodsInfoIds") List<String> goodsInfoIds,
                              @Param("enterPriseAuditState") EnterpriseAuditState enterPriseAuditState,
                              @Param("enterPriseGoodsAuditReason") String enterPriseGoodsAuditReason);

    /**
     * 根据供应商商品详情找到商品详情
     * @param goodsInfoId
     */
    @Query
    GoodsInfo findByProviderGoodsInfoId(String goodsInfoId);

    /**
     * 根据供应商商品infoId找到
     * @param delInfoIds
     * @return
     */
    List<GoodsInfo> findByProviderGoodsInfoIdIn(List<String> delInfoIds);

    /**
     * 根据erpGoodsInfoNo来查找特价商品
     */
    @Query("from GoodsInfo w where w.goodsInfoType = 1 and w.delFlag = 0")
    List<GoodsInfo> findAllSpecialGoods();



    @Query(value = "select g.goods_info_no from goods_info as g where g.del_flag = 0 and g.erp_goods_info_no in ?1", nativeQuery = true)
    List<Object> findGoodsInfoNosByeErpNos(List<String> erpGoodsInfoNo);

    /**
     * 更新商品的批次号
     * @param goodsInfoId
     * @param goodsInfoBatchNo
     * @return
     */
    @Modifying
    @Query(value = "update goods_info as g set g.goods_info_batch_no = ?1 where g.del_flag = 0 and g.goods_info_id = ?2", nativeQuery = true)
    int updateGoodsInfoBatchNo(String goodsInfoBatchNo, String goodsInfoId);

    /**
     * 更新商品的关键字和排名
     * @param keywords
     * @param sortNum
     * @param sortNum
     * @return
     */
    @Modifying
    @Query(value = "update goods_info as g set g.key_words = ?1 , g.sort_num_key = ?2 where g.del_flag = 0 and g.goods_info_id = ?3", nativeQuery = true)
    int updateGoodsInfoKeywords(String keywords, Integer sortNum, String goodsInfoId);


    /**
     * 更新商品的分类排序的排名
     * @param sortNum
     * @param sortNum
     * @return
     */
    @Modifying
    @Query(value = "update goods_info as g set g.sort_num_cate = ?1 where g.del_flag = 0 and g.goods_info_id = ?2", nativeQuery = true)
    int updateGoodsInfoCateSortNum(Integer sortNum, String goodsInfoId);

    /**
     * 充值商品的竞价排名信息 sortNumKey, sortNumCate, keywords
     * @param goodsInfoId
     * @return
     */
    @Modifying
    @Query(value = "update goods_info as g set g.key_words = '', g.sort_num_key = 0, g.sort_num_cate = 0 where g.del_flag = 0 and g.goods_info_id = ?1", nativeQuery = true)
    int resetSkuSortNumAndKeywords( String goodsInfoId);

    /**
     * @discription 查询storeId
     * @author yangzhen
     * @date 2020/9/2 20:35
     * @param
     * @return
     */
    @Query(value = "select storeId from GoodsInfo where goodsInfoId=?1 and delFlag=0")
    Long queryStoreId(String skuId);

    /**
     * 查询erp无为空的商品
     */
    @Query("from GoodsInfo w where w.erpGoodsInfoNo is not null and w.delFlag = 0 group by w.erpGoodsInfoNo,w.goodsId")
    List<GoodsInfo> findAllByErp();

    @Query(value = "SELECT DISTINCT(o.goods_id ) FROM goods o LEFT JOIN goods_info w ON o.goods_id = w.goods_id LEFT " +
            "JOIN goods_ware_stock g ON g.goods_info_id = w.goods_info_id WHERE (o.goods_name LIKE '%T' or o.goods_name LIKE '%t' ) and w" +
            ".added_flag = 1 AND w.del_flag = 0 AND g.del_flag = 0 and o.del_flag = 0 and o.added_flag = 1 GROUP BY o.goods_id HAVING sum(g.stock) <=0",
            nativeQuery = true)
    List<String> listGoodsInfoByStock();

    /**
     * 获取带T且未下架、未删除的商品信息
     * @return
     */
    @Query("from GoodsInfo g where (g.goodsInfoName like '%T' or g.goodsInfoName like '%t') and g.addedFlag = 1 and g.delFlag = 0")
    List<GoodsInfo> findAllByTAndAddedFlag();

    /**
     * 根据序列号和商品ID查询商品序号是否重复
     * @param recommendSort
     * @param goodsInfoId
     * @return
     */
    @Query("from GoodsInfo where recommendSort=?1 and goodsInfoId<>?2 and delFlag=0")
    List<GoodsInfo> getExistByRecommendSort(Integer recommendSort, String goodsInfoId);

    /**
     * 编辑商品排序序号
     * @param recommendSort
     * @param goodsInfoId
     */
    @Modifying
    @Query("update GoodsInfo set recommendSort = ?1 where goodsInfoId = ?2")
    void modifyRecommendSort(Integer recommendSort, String goodsInfoId);

    /**
     * 移除推荐商品，并清除排序序号
     * @param goodsInfoId
     */
    @Modifying
    @Query("update GoodsInfo set recommendSort = 0 where goodsInfoId in ?1")
    void clearRecommendSort(List<String> goodsInfoId);

    /**
     * 清除所有推荐商品排序序号
     */
    @Modifying
    @Query("update GoodsInfo  set recommendSort = 0 where recommendSort != 0 and wareId = ?1")
    void clearAllRecommendSort(Long wareId);

    /**
     * 编辑商品父项id
     * @param parentGoodsInfoId
     * @param goodsInfoId
     */
    @Modifying
    @Query("update GoodsInfo set parentGoodsInfoId = ?1 where goodsInfoId = ?2")
    void modifyParentGoodsInfoId(String parentGoodsInfoId, String goodsInfoId);

    /**
     * 查询所有的库存小于0的商品数据
     * @return
     */
    @Query(value = "SELECT DISTINCT(o.goods_id ) FROM goods o LEFT JOIN goods_info w ON o.goods_id = w.goods_id LEFT " +
            "JOIN goods_ware_stock g ON g.goods_info_id = w.goods_info_id WHERE 1=1 and w" +
            ".added_flag = 1 AND w.del_flag = 0 AND g.del_flag = 0 and o.del_flag = 0 and o.added_flag = 1 GROUP BY o.goods_id HAVING sum(g.stock) <=0",
            nativeQuery = true)
    List<String> listStockoutGoods();

    @Query(value = "select goods_info_id from goods_info " +
            "where del_flag = 0\n" +
            "and added_flag = 1 \n" +
            "and audit_status =1\n" +
            "and if(IFNULL(:storeId,'') !='',store_id = :storeId,1=1) \n" +
            "and if(IFNULL(:wareId,'') !='',ware_id = :wareId,1=1) \n" +
            "and if(IFNULL(:goodsInfoId,'') !='',goods_info_id = :goodsInfoId,1=1) \n" +
            "and if(IFNULL(:goodsId,'') !='',goods_id = :goodsId,1=1) \n" +
            "and if(IFNULL(:likeErpNo,'') !='',erp_goods_info_no like concat('%', :likeErpNo,'%'),1=1) \n" +
            "and if(IFNULL(:likeGoodsName,'') !='',goods_info_name like concat('%', :likeGoodsName,'%'),1=1) \n" +
            "and if(IFNULL(:goodsInfoType,'') !='',goods_info_type = :goodsInfoType,1=1) \n" +
            "and if(IFNULL((COALESCE(:cateIds, NULL)),'') != '', cate_id IN (:cateIds),1=1) \n" +
            "and if(IFNULL(:brandId,'') !='',brand_id = :brandId,1=1) \n" +
            "and if(IFNULL((COALESCE(:goodsInfoIds, NULL)),'') != '', goods_info_id IN (:goodsInfoIds),1=1) \n" +
            "and 1=1 "
            , nativeQuery = true)
    List<String> listByCondition4PileActivity(@Param("storeId") Long storeId,
                                              @Param("wareId") Long wareId,
                                              @Param("goodsId") String goodsId,
                                              @Param("goodsInfoId") String goodsInfoId,
                                              @Param("likeErpNo") String likeErpNo,
                                              @Param("likeGoodsName") String likeGoodsName,
                                              @Param("goodsInfoType") Integer goodsInfoType,
                                              @Param("cateIds") List<Long> cateIds,
                                              @Param("brandId") Long brandId,
                                              @Param("goodsInfoIds") List<String> goodsInfoIds);

    @Query(value = "SELECT distinct brand_id FROM `goods_info` w where w.added_flag = 1 AND w.del_flag = 0 and brand_id in :brandIds and ware_id = :wareId", nativeQuery = true)
    List<Long> findBrandsHasAddedSku(@Param("brandIds") List<Long> brandIds, @Param("wareId") Long wareId);

    /**
     * 批量修改sku类目
     * @param cateId
     * @param goodsIds
     */
    @Modifying
    @Query("update GoodsInfo g set g.cateId = ?1, g.updateTime = now() where g.goodsId in ?2")
    void batchModifyCate(Long cateId, List<String> goodsIds);

    @EntityGraph(value="GoodsInfo.Graph", type = EntityGraph.EntityGraphType.FETCH)
    List<GoodsInfo> findAllByWareIdAndDelFlag(Long wareId, DeleteFlag deleteFlag);;


    @Query(value = "select new com.wanmi.sbc.goods.bean.vo.GoodsInfoSimpleVo(gi.goodsInfoId,gi.goodsInfoName,g.goodsId,g.goodsImg,g.goodsSubtitle) " +
            "from GoodsInfo gi left join Goods g on gi.goodsId = g.goodsId where g.delFlag = 0 and gi.goodsInfoId in ?1")
    List<GoodsInfoSimpleVo> findGoodsInfoSimpleVoBySkuIds(List<String> skuIds);

   @Query(value = "SELECT " +
           "DISTINCT c.brand_id " +
           "FROM goods_info c " +
           "WHERE c.is_scattered_quantitative =?1 AND c.del_flag =0 AND c.added_flag = 1",nativeQuery = true)
   List<Long> listByClassifyType(Integer type);

 /**
  * 更新商品的预售库存
  * @param num
  * @param goodsInfoId
  * @return
  */
 @Modifying
 @Query(value = "update goods_info as g set g.presell_stock = g.presell_stock + ?1 where g.del_flag = 0 and g.goods_info_id = ?2", nativeQuery = true)
 int updateGoodsInfoPresellNum(Long num, String goodsInfoId);
}
