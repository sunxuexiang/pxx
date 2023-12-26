package com.wanmi.sbc.customer.api.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.customer.api.utils.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Convert;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@ApiModel
public class CompanyMallSupplierTabBaseVO implements Serializable {
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

    //商城类型
    private String mallTypes;

    // 配送方式列表
    private List<Integer> mallTypeList;

    public String getMallTypes() {
        if(CollectionUtils.isNotEmpty(this.mallTypeList) && StringUtils.isBlank(this.mallTypes)){
            this.mallTypes = JSON.toJSONString(this.mallTypeList);
        }
        return this.mallTypes;
    }
    public List<Integer> getMallTypeList() {
        if (CollectionUtils.isEmpty(this.mallTypeList) && StringUtils.isNotBlank(this.mallTypes)) {
            this.mallTypeList = JSON.parseArray(this.mallTypes, Integer.class);
        }
        if(CollectionUtils.isEmpty(this.mallTypeList)){
            this.mallTypeList = Arrays.asList(Integer.valueOf(0));
        }
        return this.mallTypeList;
    }

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
