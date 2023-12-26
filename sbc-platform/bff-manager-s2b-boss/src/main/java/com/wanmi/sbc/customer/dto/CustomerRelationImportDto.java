package com.wanmi.sbc.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/4/20 10:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRelationImportDto {

    private String customerAccount;

    private String employee;

    private String managerName;

    private String customerDetailId;
    
    private String employeeId;
    
    private String managerId;

}
