package com.wanmi.sbc.goods.activitygoodspicture.repository;

import com.wanmi.sbc.goods.activitygoodspicture.model.root.ActivityGoodsPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityGoodsPictureRepository  extends JpaRepository<ActivityGoodsPicture, Long>, JpaSpecificationExecutor<ActivityGoodsPicture> {


    List<ActivityGoodsPicture> findAllByGoodsInfoIdAndGoodsId(String goodsInfoId, String goodsId);

    List<ActivityGoodsPicture> findByGoodsInfoIdIn(List<String> goodsInfoId);

    List<ActivityGoodsPicture> findByGoodsIdIn(List<String> goodsId);
}
