package com.wanmi.sbc.wms.api.response.wms;

import com.wanmi.sbc.wms.bean.vo.ResponseWMSReturnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ResponseWMSReturnResponse
 * @Description: TODO
 * @Author: yxb
 * @Date: 2020/5/15 20:01
 * @Version: 1.0
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWMSReturnResponse {

    @ApiModelProperty(value = "返回类型")
    private ResponseWMSReturnVO responseWMSReturnVO;
}
