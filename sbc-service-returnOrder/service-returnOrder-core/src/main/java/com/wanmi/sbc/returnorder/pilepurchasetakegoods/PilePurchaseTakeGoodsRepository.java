package com.wanmi.sbc.returnorder.pilepurchasetakegoods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PilePurchaseTakeGoodsRepository  extends JpaRepository<PilePurchaseTakeGoods,Long>, JpaSpecificationExecutor<PilePurchaseTakeGoods> {
}
