package com.wanmi.sbc.order.bean.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: LogisticsInfoVO
 * @Description: TODO
 * @Date: 2020/11/6 14:49
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class LogisticsInfoDTO implements Serializable {
    private static final long serialVersionUID = 4042597524929953471L;
    //公司id
    @ApiModelProperty(value = "公司id")
    private String id;
    /**
     * 公司编号
     */
    @ApiModelProperty(value = "公司编号")
    private String companyNumber;
    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String logisticsCompanyName;
    /**
     * 公司电话
     */
    @ApiModelProperty(value = "公司电话")
    private String logisticsCompanyPhone;
    /**
     * 物流公司地址
     */
    @ApiModelProperty(value = "物流公司地址")
    private String logisticsAddress;
    /**
     * 收货点
     */
    @ApiModelProperty(value = "收货点")
    private String receivingPoint;

    /**
     * 是否是客户自建标志位，0:否，1：是
     */
    @ApiModelProperty(value = "是否是客户自建标志位，0:否，1：是")
    private Integer insertFlag;

    private String deliverLogistics;
}
