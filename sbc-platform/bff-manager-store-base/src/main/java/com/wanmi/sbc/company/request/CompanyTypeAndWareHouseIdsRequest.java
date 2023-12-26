package com.wanmi.sbc.company.request;

import com.wanmi.sbc.common.enums.CompanyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author baijianzhong
 * @ClassName CompanyTypeAndWareHouseIdsRequest
 * @Date 2020-09-08 14:42
 * @Description TODO
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompanyTypeAndWareHouseIdsRequest {

    private static final long serialVersionUID = 4279319434969072120L;


    /**
     * 商家的Id
     */
    private Long companyInfoId;

    /**
     * erpId
     */
    @NotNull
    private String erpId;

    /**
     * 商家的类型
     */
    @NotNull
    private CompanyType companyType;

    /**
     * 选择仓库的信息
     */
    private List<Long> wareIds;

}
