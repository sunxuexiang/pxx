package com.wanmi.sbc.customer.detail.repository;


import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.bean.enums.CustomerStatus;
import com.wanmi.sbc.customer.bean.vo.CustomerDetailVO;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetail;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailBase;
import com.wanmi.sbc.customer.detail.model.root.CustomerDetailCopy;
import com.wanmi.sbc.customer.employee.model.root.EmployeeCopy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员详情数据源
 * Created by CHENLI on 2017/4/18.
 */
@Repository
public interface CustomerDetailCopyRepository extends JpaRepository<CustomerDetailCopy, String>,
        JpaSpecificationExecutor<CustomerDetailCopy> {


    @Query("select new CustomerDetailCopy(c.customerId,c.customerName, c.provinceId, c.cityId, c.areaId, c.contactPhone)  from CustomerDetailCopy c where c.delFlag = 0 and c.customerStatus = 0")
    List<CustomerDetailCopy> findAll();

    /**
     * 根据城市列表查询客户
     * @param cities
     * @return
     */
    @Query("select new CustomerDetailCopy(c.customerId,c.customerName, c.provinceId, c.cityId, c.areaId, c.contactPhone)  from CustomerDetailCopy c where c.delFlag = 0 and c.customerStatus = 0 and c.cityId in :cities")
    List<CustomerDetailCopy> findCustomerByCities(@Param("cities") List<String> cities);

    /**
     * 根据ID列表查询客户信息
     */
    @Query("select new CustomerDetailCopy(c.customerId,c.customerName, c.provinceId, c.cityId, c.areaId, c.contactPhone)  from CustomerDetailCopy c where c.delFlag = 0 and c.customerStatus = 0 and c.customerId in :customerIds")
    List<CustomerDetailCopy> findCustomerDetailByIds(@Param("customerIds") List<String> customerIds);
}
