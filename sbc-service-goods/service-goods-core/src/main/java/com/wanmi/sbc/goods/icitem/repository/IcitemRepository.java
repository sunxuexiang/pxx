package com.wanmi.sbc.goods.icitem.repository;

import com.wanmi.sbc.goods.icitem.model.root.Icitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * <p>配送到家DAO</p>
 * @author lh
 * @date 2020-12-05 18:16:34
 */
@Repository
public interface IcitemRepository extends JpaRepository<Icitem, String>,
        JpaSpecificationExecutor<Icitem> {

}
