package com.wanmi.sbc.customer.api.response.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierRecommendVO;
import lombok.Data;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 16:08
 **/
@Data
public class CompanyMallSupplierRecommendResponse extends CompanyMallSupplierRecommendVO {
    private static final long serialVersionUID = -4095657679144553907L;
    private String storeName;
}
