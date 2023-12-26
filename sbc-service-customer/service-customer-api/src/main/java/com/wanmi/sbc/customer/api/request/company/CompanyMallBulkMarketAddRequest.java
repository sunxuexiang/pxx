package com.wanmi.sbc.customer.api.request.company;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.customer.bean.vo.CompanyMallBulkMarketVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: aa
 * @author: gdq
 * @create: 2023-06-13 15:24
 **/
@ApiModel
@Data
public class CompanyMallBulkMarketAddRequest extends CompanyMallBulkMarketVO {
    private static final long serialVersionUID = 7552570854904824427L;
}

