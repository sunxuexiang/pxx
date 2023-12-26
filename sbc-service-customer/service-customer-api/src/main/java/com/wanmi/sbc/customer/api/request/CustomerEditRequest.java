package com.wanmi.sbc.customer.api.request;

import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.customer.bean.enums.CheckState;
import com.wanmi.sbc.customer.bean.enums.CustomerType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * boss端保存客户
 * Created by CHENLI on 2017/4/19.
 */
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerEditRequest extends BaseRequest {

    /**
     * 会员详细信息标识UUID
     */
    @ApiModelProperty(value = "会员详细信息标识UUID")
    private String customerDetailId;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID")
    private String customerId;

    /**
     * 客户等级ID
     */
    @ApiModelProperty(value = "客户等级ID")
    private Long customerLevelId;

    /**
     * 账户
     */
    @ApiModelProperty(value = "账户")
    private String customerAccount;

    /**
     * 会员名称
     */
    @ApiModelProperty(value = "会员名称")
    private String customerName;

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
     * 区
     */
    @ApiModelProperty(value = "区")
    private Long areaId;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String customerAddress;

    /**
     * 联系人名字
     */
    @ApiModelProperty(value = "联系人名字")
    private String contactName;

    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String contactPhone;

    /**
     * 负责业务员
     */
    @ApiModelProperty(value = "负责业务员")
    private String employeeId;

    /**
     * 审核状态 0：待审核 1：已审核 2：审核未通过
     */
    @ApiModelProperty(value = "审核状态")
    private CheckState checkState;

    /**
     * 客户类型 0:平台客户,1:商家客户
     */
    @ApiModelProperty(value = "客户类型")
    private CustomerType customerType;

    /**
     * 所属商家Id
     */
    @ApiModelProperty(value = "所属商家Id")
    private Long companyInfoId;

    /**
     * 所属店铺Id
     */
    @ApiModelProperty(value = "所属店铺Id")
    private Long storeId;

    /**
     * 是否重置密码
     */
    @ApiModelProperty(value = "是否重置密码")
    private boolean passReset;

    /**
     * 重置密码对应的新账号
     */
    @ApiModelProperty(value = "重置密码对应的新账号")
    private String customerAccountForReset;

    /**
     * 商家和客户关联关系id，用于修改等级
     */
    @ApiModelProperty(value = "商家和客户关联关系id")
    private String storeCustomerRelaId;

}
