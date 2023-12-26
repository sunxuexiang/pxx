package com.wanmi.sbc.customer.api.response.fadada;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@NoArgsConstructor
public class UploadContractResponese {

    //上传合同名称
    private String contractName;
    //上传合同URL
    private String contractUrl;
    //上传合同的ID
    private String fadadaId;
    //上传合同是否禁用标识
    private int contractFlag;
    private String createPerson;
    private Long contractId;
    private Integer isPerson;
}
