package com.wanmi.sbc.customer.api.request.fadada;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@ApiModel
@Data
@Component
public class FadadaRegisterRequest {
    private String companyName;
    private String transactionNo;
    private String authenticationType;
    private String status;
    private String sign;

}
