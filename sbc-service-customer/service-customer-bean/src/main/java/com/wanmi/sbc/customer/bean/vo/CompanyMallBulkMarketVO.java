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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: sbc-backgroud
 * @description: 商家入驻，批发市场VO
 * @author: gdq
 * @create: 2023-06-13 14:51
 **/
@Data
@ApiModel
public class CompanyMallBulkMarketVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;
    /**
     * 市场Id
     */
    @ApiModelProperty(value = "市场Id")
    private Long marketId;

    /**
     * 市场编号
     */
    @ApiModelProperty(value = "市场编号")
    private String marketCode;

    /**
     * 市场名称
     */
    @ApiModelProperty(value = "市场名称")
    private String marketName;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private Long provinceId;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private Long cityId;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @ApiModelProperty(value = "省名称")
    private String provinceName;

    @ApiModelProperty(value = "市名称")
    private String cityName;

    @ApiModelProperty(value = "连接信息")
    private String concatInfo;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "操作人")
    private String operator;

    /**
     * 修改时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 删除标志 0未删除 1已删除
     */
    @ApiModelProperty(value = "删除标志 0未删除 1已删除")
    private DeleteFlag delFlag;

    /**
     * 开启状态，1: 打开，0；关闭
     */
    @ApiModelProperty(value = "开启状态，1: 打开，0；关闭")
    private Integer openStatus;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private BigDecimal sort;
    @ApiModelProperty(value = "是否支持商家凑件")
    private Integer patchFlag;
    public Integer getPatchFlag() {
        return patchFlag==null?0:patchFlag;
    }
}
