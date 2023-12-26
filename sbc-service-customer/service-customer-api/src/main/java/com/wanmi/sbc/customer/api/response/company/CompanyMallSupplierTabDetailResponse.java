package com.wanmi.sbc.customer.api.response.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallSupplierTabVO;
import lombok.Data;

import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 16:08
 **/
@Data
public class CompanyMallSupplierTabDetailResponse extends CompanyMallSupplierTabVO {
    private static final long serialVersionUID = -4095657679144553907L;

    private List<Store> stores;

    @Data
    public static class Store{
        private Long storeId;
        private String storeName;
        private String supplierName;

        private Long companyInfoId;
    }
}
