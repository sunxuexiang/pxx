package com.wanmi.sbc.account.wallet.repository;

import com.wanmi.sbc.account.wallet.model.root.CustomerBindBankCard;
import com.wanmi.sbc.common.enums.DefaultFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: TODO
 * @author: jiangxin
 * @create: 2021-08-20 16:01
 */
@Repository
public interface CustomerBindBankCardRepository extends JpaRepository<CustomerBindBankCard,Long>, JpaSpecificationExecutor<CustomerBindBankCard> {

    /**
     * 根据卡号查找绑定银行卡信息
     * @param bankCode
     * @param delFlag
     * @return
     */
    CustomerBindBankCard findOneByBankCodeAndDelFlag(String bankCode, DefaultFlag delFlag);

    /**
     * 根据卡号查找银行卡信息
     * @param bankCode
     * @return
     */
    CustomerBindBankCard findOneByBankCode(String bankCode);

    /**
     * 根据钱包id查询所有绑定银行卡信息
     * @param walletId
     * @param delFlag
     * @return
     */
    List<CustomerBindBankCard> findAllByWalletIdAndDelFlag(Long walletId,DefaultFlag delFlag);

    /**
     * 验证银行卡是否绑定
     * @param bankCode
     * @param delFlag
     * @return
     */
    Integer countByBankCodeAndDelFlag(String bankCode,DefaultFlag delFlag);

}
