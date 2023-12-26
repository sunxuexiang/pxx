package com.wanmi.sbc.common.base;

import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.CustomerRegisterType;
import com.wanmi.sbc.common.enums.Platform;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 * Created by jinwei on 27/3/2017.
 */
@ApiModel
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Operator implements Serializable {

    private static final long serialVersionUID = 8889187242758862859L;

    /**
     * 操作方
     */
    @ApiModelProperty(value = "操作方")
    private Platform platform;

    @ApiModelProperty(value = "操作人")
    private String name;

    @ApiModelProperty(value = "管理员Id")
    private String adminId;

    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "操作所在的Ip地址")
    private String ip;

    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private String storeId;

    @ApiModelProperty(value = "供应商类型")
    private Integer companyType;

    /**
     * 操作人账号
     */
    @ApiModelProperty(value = "操作人账号")
    private String account;

    /**
     * 会员的类型
     */
    @ApiModelProperty(value = "会员的类型")
    private CustomerRegisterType customerRegisterType;

    /**
     * 公司Id
     */
    @ApiModelProperty("公司Id")
    private Long companyInfoId;

    @ApiModelProperty("增值服务")
    private List<VASEntity> services = new ArrayList<>();

    /**
     * 父账户的Id
     */
    @ApiModelProperty("父Id")
    private String parentId;
}
