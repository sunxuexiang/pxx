package com.wanmi.sbc.customer.api.request.customer;

import com.wanmi.sbc.customer.api.request.CustomerBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program:
 * @description: 关联子账户
 * @author: Mr.Tian
 * @create: 2020-06-03 14:23
 **/
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddRelaRequest extends CustomerBaseRequest {

    private static final long serialVersionUID = -1709264666378683734L;

    @ApiModelProperty(value = "操作人id")
    private String customerId;

    @ApiModelProperty(value = "被绑定的子账号号码")
    private List<String> customerAccountList;

}
