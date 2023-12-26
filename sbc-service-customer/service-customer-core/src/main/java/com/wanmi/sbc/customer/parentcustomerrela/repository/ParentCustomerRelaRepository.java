package com.wanmi.sbc.customer.parentcustomerrela.repository;

import com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>子主账号关联关系DAO</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@Repository
public interface ParentCustomerRelaRepository extends JpaRepository<ParentCustomerRela, String>,
        JpaSpecificationExecutor<ParentCustomerRela> {

    /**
     * 根据子账号Id查询是否已被绑定
     * @param customerIds
     * @return
     */
   // int countByCustomerIdExists(String customerId);
    int countByCustomerIdContains(List<String> customerIds);

    /**
     * 根据主账号查询绑定关系
     * @param parentId
     * @return
     */
    List<ParentCustomerRela> findAllByParentId(String parentId);

    ParentCustomerRela findByCustomerId(String id);
    List<ParentCustomerRela> findByCustomerIdOrParentId(String customerId,String parentId );
    /**
     * 根据主账号删除关联关系
     * @param parentId
     * @return
     */
    int deleteAllByParentId(String parentId);

    /**
     * 功能描述: 批量更新parentId
     * 〈〉
     * @Param: [customerList, parentId]
     * @Return: void
     * @Author: yxb
     * @Date: 2020/5/27 14:26
     */
    @Modifying
    @Query("update ParentCustomerRela set  parentId=?2 where id in ?1")
    void updateParentId(List<String> customerList,String parentId);


    /**
     * 功能描述: 验证是否存在关联的主账号
     * 〈〉
     * @Param: [id]
     * @Return: com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela
     * @Author: yxb
     * @Date: 2020/5/27 9:41
     */
    @Query(value = "select   w.parent_id from `parent_customer_rela` w where  w.customer_id = ?1 limit 1 ",nativeQuery = true)
    Object findByCustomerIdStatus(String CustomerId);

    /**
     * 功能描述:  验证是否存在关联的子账号
     * 〈〉
     * @Param: [id]
     * @Return: com.wanmi.sbc.customer.parentcustomerrela.model.root.ParentCustomerRela
     * @Author: yxb
     * @Date: 2020/5/27 9:42
     */
    @Query(value = " select w.parent_id, w.customer_id from `parent_customer_rela` w where  w.parent_id = ?1 limit 1",nativeQuery = true)
    Object findByParentIdStatus(String parentId);

}
