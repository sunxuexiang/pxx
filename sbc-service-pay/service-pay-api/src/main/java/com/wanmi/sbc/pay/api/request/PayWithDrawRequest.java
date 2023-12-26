package com.wanmi.sbc.pay.api.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/10/21 9:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayWithDrawRequest implements Serializable {

    private static final long serialVersionUID = -6618059067856724471L;

    @ApiModelProperty("鲸币提现ID")
    private Integer withdrawId;

    /**
     * 账号名称
     */
    @ApiModelProperty("账号名称")
    @NotBlank(message = "账号名称不能为空")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5]{1,30}$", message = "账号名称只允许输入中文,且本长度不超过30")
    private String accountName;

    /**
     * 账号，账号限制30字符（只能输入数字）
     */
    @ApiModelProperty("账号")
    @Pattern(regexp = "^[0-9]{1,30}$",message = "账号只允许输入数字,且长度为30")
    private String bankAccount;

    /**
     * 开户行名称
     */
    @ApiModelProperty("开户行名称")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5]{1,30}$", message = "开户行名称只允许输入中文,且本长度不超过30")
    private String bankName;

    /**
     * 开户行联行号
     */
    @ApiModelProperty("开户行联行号")
    @Pattern(regexp = "^[0-9]+$",message = "开户行联行号只允许输入数字,且不能为空")
    private String bankNo;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用,1:启动，0：禁用")
    private EnableStatus enableStatus;

}
