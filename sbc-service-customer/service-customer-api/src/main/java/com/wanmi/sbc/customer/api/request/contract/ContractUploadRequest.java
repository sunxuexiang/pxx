package com.wanmi.sbc.customer.api.request.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractUploadRequest implements Serializable {

    private static final long serialVersionUID = 4202920113830049792L;

    //上传合同名称
    private String contractName;
    //上传合同URL
    private String contractUrl;
    //上传合同的ID
    private String fadadaId;
    //上传合同是否禁用标识
    private int contractFlag;
    private String createPerson;
    @ApiModelProperty(value = "企业类型，1.个人，2.企业 这是保存于xyy自己数据库")
    private Integer isPerson;


}
