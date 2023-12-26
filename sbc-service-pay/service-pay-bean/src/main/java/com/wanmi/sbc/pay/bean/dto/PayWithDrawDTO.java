package com.wanmi.sbc.pay.bean.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.EnableStatus;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lm
 * @date 2022/10/21 9:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayWithDrawDTO implements Serializable {

    private static final long serialVersionUID = -6618059067856724471L;

    @ApiModelProperty("鲸币提现ID")
    private Integer withdrawId;

    /**
     * 账号名称
     */
    @ApiModelProperty("账号名称")
    private String accountName;

    /**
     * 账号
     */
    @ApiModelProperty("账号")
    private String bankAccount;

    /**
     * 开户行名称
     */
    @ApiModelProperty("开户行名称")
    private String bankName;

    /**
     * 开户行联行号
     */
    @ApiModelProperty("开户行联行号")
    private String bankNo;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用,1:启动，0：禁用")
    private EnableStatus enableStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
