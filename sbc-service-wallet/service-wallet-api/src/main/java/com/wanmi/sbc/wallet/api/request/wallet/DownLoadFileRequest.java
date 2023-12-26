package com.wanmi.sbc.wallet.api.request.wallet;

import com.wanmi.sbc.wallet.api.request.BalanceBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownLoadFileRequest {
    private static final long serialVersionUID = 2321329611892565308L;

    @ApiModelProperty(value = "下载文件名称")
    private String fileName;



}
