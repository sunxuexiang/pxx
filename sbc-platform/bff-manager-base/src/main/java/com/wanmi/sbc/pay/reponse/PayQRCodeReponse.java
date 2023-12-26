package com.wanmi.sbc.pay.reponse;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayQRCodeReponse {

    private String aiPayQRCode;
    private String WxPayQRCode;
    private String Cshdk_Url;
}
