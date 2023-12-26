package com.wanmi.sbc.customer.api.request.fadada;


import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.stereotype.Component;

@ApiModel
@Data
@Component
public class FadadaNotifyRequest {
    private String transaction_id;
    private String contract_id;
    private String result_code;
    private String result_desc;
    private String download_url;
    private String viewpdf_url;
    private String timestamp;
    private String msg_digest;

}
