package com.wanmi.sbc.customer.api.request.contract;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractUpdateRequest extends ContractUploadRequest {

    private static final long serialVersionUID = 4202920113830049792L;

    private Long contractId;

}
