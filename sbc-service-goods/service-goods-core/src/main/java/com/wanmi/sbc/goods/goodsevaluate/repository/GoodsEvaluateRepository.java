package com.wanmi.sbc.goods.goodsevaluate.repository;

import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluatePageRequest;
import com.wanmi.sbc.goods.goodsevaluate.model.root.GoodsEvaluate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * <p>商品评价DAO</p>
 *
 * @author liutao
 * @date 2019-02-25 15:14:16
 */
@Repository
public interface GoodsEvaluateRepository extends JpaRepository<GoodsEvaluate, String>,
        JpaSpecificationExecutor<GoodsEvaluate> {

    /**
     * @param goodsId spuID
     * @Description: 商品好评率（根据spu查询）
     * @Author: Bob
     * @Date: 2019-04-09 15:44
     */
    @Query(value = "SELECT convert(b.counts/a.counts*100,decimal(15,0)) as praise FROM" +
            " (SELECT count(*) as counts FROM goods_evaluate t WHERE t.goods_id = :goodsId  and t.del_flag = 0) as a," +
            "(SELECT count(*) as counts FROM goods_evaluate t WHERE t.evaluate_score >= 4 and t.goods_id = :goodsId  " +
            "and t.del_flag = 0) as b",
            nativeQuery = true)
    String queryPraise(@Param("goodsId") String goodsId);

    /**
     * @Author lvzhenwei
     * @Description 更新商品评价点赞数
     * @Date 17:10 2019/5/7
     * @Param [evaluateId]
     * @return void
     **/
    @Modifying
    @Query("update GoodsEvaluate set goodNum = goodNum + 1 where evaluateId = :evaluateId")
    void updateGoodsEvaluateGoodNum(@Param("evaluateId") String evaluateId);

    @Query("from GoodsEvaluate where goodsId = :#{#req.goodsId} and delFlag = :#{#req.delFlag} and isShow = " +
            ":#{#req.isShow} and evaluateContent <> '系统评价'")
    List<GoodsEvaluate> queryTopData(@Param("req")GoodsEvaluatePageRequest req, Pageable pageable);
}
