package com.wanmi.sbc.account.wallet.repository;

/**
 * @author jeffrey
 * @create 2021-08-21 10:02
 */

import com.wanmi.sbc.account.wallet.model.root.VirtualGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VirtualGoodsRepository extends JpaRepository<VirtualGoods,Long>, JpaSpecificationExecutor<VirtualGoods> {

    void deleteByGoodsIdIn(List<Long> goodsIdList);

    List<VirtualGoods> findByDelFlagAndGoodsIdIn(List<Long> goodsIdList,Integer delFlag);

    VirtualGoods findByGoodsIdAndDelFlag(Long goodsId, Integer delFlag);

    Page<VirtualGoods> findAllByDelFlag(Integer delFlag, PageRequest pageRequest);

    VirtualGoods findByGoodsId(Long goodsId);

    List<VirtualGoods> findByGoodsIdIn(List<Long> goodsIdList);
}
