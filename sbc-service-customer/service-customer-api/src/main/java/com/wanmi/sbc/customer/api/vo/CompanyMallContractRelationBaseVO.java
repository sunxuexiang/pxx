package com.wanmi.sbc.customer.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Convert;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel
public class CompanyMallContractRelationBaseVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 商家Id
     */
    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;

    @ApiModelProperty(value = "店铺Id")
    private Long storeId;

    /**
     * 市场名称
     */
    @ApiModelProperty(value = "关联类型，1：tab,2:商城分类")
    private Integer relationType;

    /**
     * 关联的value
     */
    @ApiModelProperty(value = "关联的value，tabId,商城分类Id")
    private String relationValue;

    /**
     * 关联的name
     */
    @ApiModelProperty(value = "关联的name")
    private String relationName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String operator;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;


    private Integer sort;

}