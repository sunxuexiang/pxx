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
public class CompanyMallSupplierRecommendVO implements Serializable {
    private static final long serialVersionUID = 4860094582115362527L;

    @ApiModelProperty(value = "商家推荐id")
    private Long id;

    @ApiModelProperty(value = "商家Id")
    private Long companyInfoId;

    @ApiModelProperty(value = "商家名称")
    private String companyInfoName;

    private Long storeId;

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
     * 1: 打开，0；关闭
     */
    @ApiModelProperty(value = "开启状态：1: 打开，0；关闭")
    private Integer openStatus;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private BigDecimal sort;

    /**
    * 指定排序
    */
    private Integer assignSort;

    public static void main(String[] args) {
//        CompanyMallSupplierTabVO companyMallBulkMarketVO = new CompanyMallSupplierTabVO();
//        companyMallBulkMarketVO.setMarketName("哈哈测试你好呀");
//        companyMallBulkMarketVO.setProvinceId(330000L);
//        companyMallBulkMarketVO.setCityId(330100L);
//        companyMallBulkMarketVO.setDetailAddress("哈哈测试你好呀详细地址");
//        companyMallBulkMarketVO.setSort(new BigDecimal("1.2"));
//        System.out.println(JSON.toJSONString(companyMallBulkMarketVO));
    }
}
