package com.wanmi.sbc.goods.goodsdevanning.repository;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoOnlyShelflifeDTO;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.goodsdevanning.model.root.GoodsDevanning;
import com.wanmi.sbc.goods.info.model.entity.GoodsInfoParams;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
public interface GoodsDevanningRepository extends JpaRepository<GoodsDevanning, String>, JpaSpecificationExecutor<GoodsDevanning>{


    @Query(value = "SELECT t1.goods_id,max(t1.step) as step,max(t1.goods_unit) as goods_unit\n" +
            "from  goods_devanning t1 \n" +
            "WHERE t1.devanning_goods_id in (?1)\n" +
            "GROUP BY t1.goods_id\n",nativeQuery = true)
    List<Object>  getGoodsDevanningMaxstep(List<String> SpuId);
}
