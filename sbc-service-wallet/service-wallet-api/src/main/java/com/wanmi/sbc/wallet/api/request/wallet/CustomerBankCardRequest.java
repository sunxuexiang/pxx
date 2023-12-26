package com.wanmi.sbc.wallet.api.request.wallet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseQueryRequest;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @Description: 绑定银行卡请求
 * @author: jiangxin
 * @create: 2021-08-20 11:55
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBankCardRequest extends BaseQueryRequest {

    /**
     * 银行卡id
     */
    @ApiModelProperty(value = "银行卡id，从1开始自增")
    private Integer bankCardId;

    /**
     *账户id
     */
    @ApiModelProperty(value = "钱包账户id，关联钱包账户表")
    private Long walletId;

    /**
     * 开户行
     */
    @ApiModelProperty(value = "开户行")
    private String bankName;

    /**
     * 开户支行
     */
    @ApiModelProperty(value = "开户支行")
    private String bankBranch;

    /**
     * 持卡人
     */
    @ApiModelProperty(value = "持卡人姓名")
    private String cardHolder;

    /**
     * 银行卡类型
     */
    @ApiModelProperty(value = "银行卡类型")
    private String cardType;

    /**
     * 银行卡号
     */
    @ApiModelProperty(value = "银行卡号")
    private String bankCode;

    /**
     * 绑定手机号
     */
    @ApiModelProperty(value = "绑定手机号")
    private String bindPhone;

    /**
     * 开户地区【存省市区id，以,分割】
     */
    @ApiModelProperty(value = "开户地区【存省市区id，以,分割】")
    private String bankArea;

    /**
     * 绑定时间
     */
    @ApiModelProperty(value = "绑定时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime bindTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 删除标志
     */
    @ApiModelProperty(value = "是否删除")
    private DefaultFlag delFlag;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime delTime;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码")
    private String verifyCode;

}
