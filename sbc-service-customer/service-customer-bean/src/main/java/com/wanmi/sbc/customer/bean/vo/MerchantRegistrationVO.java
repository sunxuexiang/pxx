package com.wanmi.sbc.customer.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商家入驻申请信息
 * @author  hudong
 * @date 2023/6/17 08:52
 */
@ApiModel
@Data
public class MerchantRegistrationVO implements Serializable {

    private static final long serialVersionUID = -3430186713937000934L;

    /**
     * 申请ID
     */
    @ApiModelProperty(value = "申请ID")
    private Long applicationId;

    /**
     * 商家名称
     */
    @ApiModelProperty(value = "商家名称")
    private String merchantName;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

    /**
     * 商家联系方式
     */
    @ApiModelProperty(value = "商家联系方式")
    private String merchantPhone;

    /**
     * 对接人
     */
    @ApiModelProperty(value = "对接人")
    private String contactPerson;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    /**
     * 对接人
     */
    @ApiModelProperty(value = "是否处理(0:否,1:是)")
    private Integer handleFlag;
    /**
     * 删除标志
     */
    @ApiModelProperty(value = "删除标志 0未删除 1已删除")
    private DeleteFlag delFlag;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 商家地址
     */
    @ApiModelProperty(value = "商家地址")
    private String merchantAddress;


}
