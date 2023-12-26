package com.wanmi.sbc.goods.lastgoodswrite.repository;

import com.wanmi.sbc.goods.lastgoodswrite.model.root.LastGoodsWrite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * <p>用户最后一次商品记录DAO</p>
 * @author 费传奇
 * @date 2021-04-23 17:33:51
 */
@Repository
public interface LastGoodsWriteRepository extends JpaRepository<LastGoodsWrite, Long>,
        JpaSpecificationExecutor<LastGoodsWrite> {

}
