package com.wanmi.sbc.customer.api.response.company;

import com.wanmi.sbc.customer.bean.vo.CompanyMallContractRelationVO;
import com.wanmi.sbc.customer.bean.vo.CompanyMallReturnGoodsAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description:
 * @author: gdq
 * @create: 2023-06-13 16:08
 **/
@Data
@ApiModel(value = "签约属性列表")
public class CompanyMallContactRelationBatchSaveResponse implements Serializable {
    private static final long serialVersionUID = -4095657679144553907L;

    @ApiModelProperty(value = "签约属性列表")
    private List<CompanyMallContractRelationVO> contactRelationList;
}
