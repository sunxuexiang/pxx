package com.wanmi.sbc.customer.bean.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: sbc-backgroud
 * @description: 商家入驻，批发市场VO
 * @author: gdq
 * @create: 2023-06-13 14:51
 **/
@Data
@ApiModel
public class CompanyMallSupplierTabVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "tabName")
    private String tabName;

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

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志 0未删除 1已删除")
    private DeleteFlag delFlag;

    /**
     * 开启状态：1: 打开，0；关闭
     */
    @ApiModelProperty(value = "开启状态：1: 打开，0；关闭")
    private Integer openStatus;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private BigDecimal sort;

    private String storeIds;

    // 配送方式
    private String deliveryTypes;

    // 配送方式列表
    private List<Integer> deliveryTypeList;

    private Long relCatId;

    // 包装配送方式和配送方式列表
    public void wrap() {
        if (CollectionUtils.isNotEmpty(this.deliveryTypeList) && StringUtils.isBlank(this.deliveryTypes)) {
            this.deliveryTypes = JSON.toJSONString(this.deliveryTypeList);
        }
        if (CollectionUtils.isEmpty(this.deliveryTypeList) && StringUtils.isNotBlank(this.deliveryTypes)) {
            this.deliveryTypeList = JSON.parseArray(this.deliveryTypes, Integer.class);
        }
    }
}
